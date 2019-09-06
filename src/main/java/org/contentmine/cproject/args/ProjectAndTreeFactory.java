package org.contentmine.cproject.args;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.files.Unzipper;

/** creates CTRress and CProjects.
 * 
 * @author pm286
 *
 */
public class ProjectAndTreeFactory {
	
	private static final Logger LOG = Logger.getLogger(ProjectAndTreeFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private DefaultArgProcessor argProcessor;
	private File projectFile;
	private boolean createdProjectDir;
	private String zipRootName;
	public ProjectAndTreeFactory(DefaultArgProcessor argProcessor) {
		this.argProcessor = argProcessor;
	}
	
	void createCTreeListFrom(List<String> cTreeNames) {
		LOG.trace("createCTreeListFrom");
		if (cTreeNames.size() == 0) {
			if (argProcessor.getInputList() == null || argProcessor.getInputList().size() == 0) {
				LOG.error("Must give inputList before --CTree or --ctree");
			} else if (argProcessor.getOutput() == null) {
				LOG.error("Must give output before --CTree or --ctree");
			} else {
				argProcessor.finalizeInputList();
				createCTreeFromOutput();
			}
		} else {
			createCTreeList(cTreeNames);
		}
	}

	private void createCTreeFromOutput() {
		String output = argProcessor.getOutput();
		File newCTree = output == null ? null : new File(output);
		if (newCTree != null) {
			createCTreeFromFile(newCTree);
		}
	}

	/** used when there is a list of files (--input) and maybe also 
	 * needs to create a --project)
	 * 
	 * @param qDirectoryNames
	 */
	private void createCTreeList(List<String> qDirectoryNames) {
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
	
		argProcessor.cTreeList = new CTreeList();
		LOG.trace("creating CTreeList from: "+qDirectoryNames);
		for (String qDirectoryName : qDirectoryNames) {
			File qDirectory = new File(qDirectoryName);
			if (!qDirectory.exists()) {
				LOG.error("File does not exist: "+qDirectory.getAbsolutePath());
				continue;
			}
			if (!qDirectory.isDirectory()) {
				LOG.error("Not a directory: "+qDirectory.getAbsolutePath());
				continue;
			}
			CTree cTree = new CTree(qDirectoryName);
			LOG.trace("...creating CTree from: "+qDirectoryName);
			if (cTree.containsNoReservedFilenames() && cTree.containsNoReservedDirectories()) {
				LOG.trace("... No reserved files or directories: "+cTree);
				List<File> childFiles = new ArrayList<File>(Arrays.asList(qDirectory.listFiles(directoryFilter)));
				List<String> childFilenames = new ArrayList<String>();
				for (File childFile : childFiles) {
					if (childFile.isDirectory()) {
						childFilenames.add(childFile.toString());
					}
				}
				LOG.trace(childFilenames);
				// recurse (no mixed directory structures)
				// FIXME 
				LOG.trace("Recursing CTrees is probably  a BUG");
				createCTreeList(childFilenames);
			} else {
				argProcessor.cTreeList.add(cTree);
			}
		}
		LOG.trace("CTreeList: "+argProcessor.cTreeList.size());
		for (CTree CTree : argProcessor.cTreeList) {
			LOG.trace("CTree: "+CTree);
			
		}
	}

	private void createCTreeFromFile(File cTree) {
		argProcessor.cTreeList = new CTreeList();
		for (String filename : argProcessor.getInputList()) {
			createCTreeFromFilenameAndWriteReservedFile(cTree, new File(filename));
		}
	}

	private void createCTreeFromFilenameAndWriteReservedFile(File cTreeDir, File infile) {
			String filename = infile.getName();
			if (!infile.isDirectory()) {
				argProcessor.ensureCTreeList();
	//			File CTreeParent = output == null ? infile.getParentFile() : cTreeDir;
				File CTreeParent = cTreeDir == null ? infile.getParentFile() : cTreeDir;
				String cmName = createUnderscoredFilename(filename);
				File directory = new File(CTreeParent, cmName);
				CTree cTree = new CTree(directory, true);
				String reservedFilename = CTree.getCTreeReservedFilenameForExtension(filename);
				try {
					cTree.writeReservedFile(infile, reservedFilename, true);
					argProcessor.cTreeList.add(cTree);
				} catch (Exception e) {
					throw new RuntimeException("Cannot create/write: "+filename, e);
				}
			}
		}

	private String createUnderscoredFilename(String filename) {
		String cmName = filename.replaceAll("\\p{Punct}", "_")+"/";
		return cmName;
	}

	private void createProjectAndCTreesFromInputFiles() {
		LOG.trace("createProjectAndCTreesFromInputFiles");
		List<String> inputList = argProcessor.getInputList();
		if (inputList == null || inputList.size() == 0 ) {
			LOG.error("no input files to create under project");
		} else {
			for (String filename : inputList) {
				File file = new File(filename);
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					if (files != null) {
						for (File file0 : files) {
							createCTreeFromFilenameAndWriteReservedFile(projectFile, file0);
						}
					}
				} else if (isZipFile(file)){
					createTreeFromZipFile(projectFile, file);
				} else {
					createCTreeFromFilenameAndWriteReservedFile(projectFile, file);
				}
			}
		}
	}

	/** create CTrees EITHER from *.PDF/HTML/XML etc  
	 * OR from subdirectories which will be CTrees
	 * 
	 * LOGIC:
	 *  (a) if "project" exists and is a directory then assume directory children are 
	 *    already valid CTrees
	 *  (b) is "project" does NOT exist but "input" does and is directory:
	 *    list all files (files) of given extension(s) (-e foo bar) under "input" and create
	 *    project as directory, then create NEW directories under "project"
	 *    using names of "files" and creating "fulltext.foo" "fulltext.bar"
	 *  
	 *    
	 */
	void createCTreeListFromProject() {
		LOG.trace("createCTreeListFromProject");
		if (false) {
		} else if (!projectFile.exists() || createdProjectDir) {
			createProjectAndCTreesFromInputFiles();
		} else if (argProcessor.cProject != null) {
			createCTreesFromDirectories();
		} else if (projectFile.isFile()) {
			LOG.error("project file must be a directory: "+projectFile);
		} else {
			LOG.error("Unacceptable project option, probable BUG");
		}
	}

	void createProject() {
		createdProjectDir = false;
		String projectDirString = argProcessor.getProjectDirString();
		if (projectDirString != null) {
			projectFile = new File(projectDirString);
			if (projectFile.exists()) {
				if (projectFile.isDirectory()) {
					argProcessor.cProject = new CProject(projectFile);
				} else {
					throw new RuntimeException("project file must be dicrectory: "+projectFile);
				}
			} else {
				// not sure this will work as CTree's should be children
				String extension = FilenameUtils.getExtension(projectFile.toString());
				if (extension != null && !extension.trim().equals("")) {
					throw new RuntimeException("Project file cannot have extensions");
				}
				argProcessor.cProject = new CProject(projectFile);
				argProcessor.PROJECT_LOG().info(projectFile+" does not exist, creating project and populating it");
  				projectFile.mkdirs();
				createdProjectDir = true;
			}
		}
	}

	private void extractDirectoriesToCTrees() {
		List<File> subdirectories = Arrays.asList(projectFile.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file != null && file.isDirectory();
			}}));
		Collections.sort(subdirectories);
		for (File subDirectory : subdirectories) {
			CTree cTree = new CTree(subDirectory);
			argProcessor.cTreeList.add(cTree);
		}
		return;
	}

	private void extractZipFilesToCTrees() {
		List<File> zipFiles = Arrays.asList(projectFile.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file != null && isZipFile(file);
			}}));
		Collections.sort(zipFiles);
		for (File zipFile : zipFiles) {
			createTreeFromZipFile(projectFile, zipFile);
			CTree cTree = new CTree(zipFile);
			argProcessor.cTreeList.add(cTree);
		}
	}

	private void createCTreesFromDirectories() {
		LOG.trace("createCTreesFromDirectories");
		argProcessor.cTreeList = new CTreeList();
		extractDirectoriesToCTrees();
		extractZipFilesToCTrees();
	}

	private void createTreeFromZipFile(File projectFile, File file) {
		Unzipper unZipper = new Unzipper();
		unZipper.setZipFile(file);
		unZipper.setOutDir(projectFile);
		unZipper.setIncludePatternString(argProcessor.getIncludePatternString());
		try {
			unZipper.extractZip();
		} catch (IOException e) {
			throw new RuntimeException("Cannot unzip file: "+e);
		}
		zipRootName = unZipper.getZipRootName();
		if (zipRootName == null) {
			LOG.debug("No zipRoot "+file);
		} else {
			this.renameFiles(new File(projectFile, zipRootName));
		}
	}

	private boolean isZipFile(File file) {
		boolean isZip = false;
		if (FilenameUtils.getExtension(file.toString()).toLowerCase().equals("zip")) {
			ZipFile zf;
			try {
				zf = new ZipFile(file);
				Enumeration<? extends ZipEntry> zipEntries = zf.entries();
				isZip = zipEntries.hasMoreElements();
			} catch (ZipException ze) {
				isZip = false;
			} catch (IOException e) {
				isZip = false;
			}
		}
		return isZip;
		
	}

	void renameFiles(File rootFile) {
		if (argProcessor.renamePairs != null && rootFile != null) {
			if (!rootFile.isDirectory()) {
				throw new RuntimeException("rootFile is not a directory: "+rootFile);
			}
			List<File> files = new ArrayList<File>(FileUtils.listFiles(rootFile, null, true));
			for (List<String> renamePair : argProcessor.renamePairs) {
				for (File file : files) {
					if (file.getName().matches(renamePair.get(0))) {
					    File newNameFile = new File(rootFile, renamePair.get(1));
					    boolean isMoved = file.renameTo(newNameFile);
					    if (!isMoved) {
					        throw new RuntimeException("cannot rename: "+file.getName());
					    }					
					}
				}
			}
		}
	}
	

}
