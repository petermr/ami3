package org.contentmine.ami.tools;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.CMFileUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** cleans some of all of the project.
 * 
 * @author pm286
 *
 */
@Command(
name = "clean",
description = {
		"Cleans specific files or directories in project.",
		"Accepts explicit paths or regular expressions.",
		"${COMMAND-FULL-NAME} -p /Users/pm286/workspace/tigr2ess --dir results cooccurrence%n"
		+ "    deletes subdirectories results/ and cooccurrence/ in projcts tigr2ess",
		"${COMMAND-FULL-NAME} -p /Users/pm286/workspace/tigr2ess --file commonest.dataTables.html\\%n"
		+ "           count.dataTables.html entries.dataTables.html full.dataTables.html%n"
		+ "    deletes 4 files by name%n"
})
public class AMICleanTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMICleanTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum Cleaner {
		FULLTEXT_HTML("fulltext.html", "f", "remove fulltext.html (probably not recoverable without redownload)"),
		FULLTEXT_PDF("fulltext.pdf", "f", "remove fulltext.pdf (probably not recoverable without redownload)"),
		FULLTEXT_XML("fulltext.xml", "f", "remove fulltext.xml (probably not recoverable without redownload)"),
		PDFIMAGES("pdfimages", "d", "remove pdfimages/ directory and contents (created by parsing fulltext.pdf)"),
		RAWIMAGES("rawimages", "d", "remove rawimages/ directory and contents (probably directly downloaded)"),
		SCHOLARLY_HTML("scholarly.html", "f", "remove scholarly.html (created by parsing)"),
		SVGDIR("svg", "d", "remove svg/ directory and contents (created by parsing fulltext.pdf)"),
		;
		public String file;
		public String type;
		public String message;
		
		private Cleaner(String file, String type, String message) {
			this.file = file;
			this.type = type;
			this.message = message;
		}
		
		public void help() {
			DebugPrint.debugPrint(file + ": " + message);
		}

		public void clean(CTree cTree, String arg) {
		}

		public boolean matches(String arg) {
			return this.file.equals(arg);
		}
	}

    @Option(names = {"--file"},
		arity = "0..*",
        description = "files to delete by name; e.g. --file scholarly.html deletes child files <ctree>/scholarly.html")
    private String[] files;

    @Option(names = {"--fileglob"},
		arity = "0..*",
        description = "files to delete by glob; use with care (I am still working this out")
    private String[] fileGlobs;

    @Option(names = {"--dir"},
		arity = "0..*",
        description = "directories to delete by name, e.g. --dir svg deletes child directories <ctree>/svg"
        )
    private String[] dirs;

    @Option(names = {"--dirglob"},
		arity = "0..*",
        description = "directories to delete by glob; use with care (I am still working this out)")
    private String[] dirGlobs;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMICleanTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMICleanTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	AMICleanTool amiCleaner = new AMICleanTool();
    	amiCleaner.runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
    	System.out.println("files         "+Util.toStringList(files));
    	System.out.println("fileGlobs     "+Util.toStringList(fileGlobs));
    	System.out.println("dirs          "+Util.toStringList(dirs));
    	System.out.println("dirGlobs      "+Util.toStringList(dirGlobs));
	}

    @Override
    protected void runSpecifics() {
        runClean();
    }

    private void runClean() {
 //   	if (files != null) cleanFiles(Arrays.asList(files));
    	if (dirs != null) cleanFileOrDirs(Arrays.asList(dirs));
    	if (files!= null) cleanFileOrDirs(Arrays.asList(files));
    	if (fileGlobs != null && getCProjectDirectory() != null) {
    		for (String fileGlob : fileGlobs) {
	    		List<File> globList = CMineGlobber.listGlobbedFilesQuietly(cProject.getDirectory(), fileGlob);
	    		LOG.debug("GLOB: " + fileGlob + "(" + globList.size() + ") ==> " + globList);
    			CMFileUtil.forceDeleteQuietly(globList);
	    		globList = CMineGlobber.listSortedChildFiles(cProject.getDirectory(), fileGlob);
	    		LOG.debug("CHILD GLOB: " + fileGlob+" ==> "+globList);
    		}
    	}
    }

	public void cleanFiles(List<String> filenames) {
		for (String file : filenames) {
			cleanReserved(file);
		}
	}

	public void cleanFileOrDirs(List<String> filenameList) {
		if (filenameList == null) {
			addLoggingLevel(Level.WARN,"No filenameList given");
			return;
		}
		if (cProject != null) {
			for (String filename : filenameList) {
				cProject.cleanTrees(filename);
			}
		} else if (cTree != null) {
			if (dirs != null) {
				for (String dir : dirs) {
					cTree.cleanFileOrDirs(dir);
				}
			}
			if (files != null) {
				for (String file : files) {
					cTree.cleanFileOrDirs(file);
				}
			}
		} else {
			addLoggingLevel(Level.ERROR, "must give cProject or cTree");
		}
	}

	public boolean cleanReserved(String arg) {
		for (Cleaner cleaner : Cleaner.values()) {
			if (cleaner.matches(arg)) {
				cProject.cleanTrees(arg);
				return true;
			}
		}
		return false;
	}

	public void cleanTrees(String filename) {
		cProject.cleanTrees(filename);
	}

}
