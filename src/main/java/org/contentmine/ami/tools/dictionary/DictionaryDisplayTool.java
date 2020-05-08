package org.contentmine.ami.tools.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.NAConstants;

import com.google.common.collect.Lists;

import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
		name = "display",
		description = {
				"Displays AMI dictionaries. (Under Development)",
				"  Example (NYI):%n"
				+ "   ${COMMAND-FULL-NAME} display --informat wikipage%n"
		})
public class DictionaryDisplayTool extends AbstractAMIDictTool {

	private static final Logger LOG = Logger.getLogger(DictionaryDisplayTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final int DEFAULT_MAX_ENTRIES = 20;
	private static final String XML = "xml";
	private static final String FULL = "FULL";
	private static final String LIST = "LIST";


	private List<Path> paths;
	
    @Parameters(index = "0",
    		arity="0..*",
//    		split=",",
    		description = "primary operation: (${COMPLETION-CANDIDATES}); if no operation, runs help"
    		)
    private Operation operation = Operation.help;

    @Option(names = {"--full"}, 
    		arity="1..*",
   		    description = "list of remote dictionaries "
    		)
    private List<String> full = new ArrayList<>();


    @Option(names = {"--remote"}, 
    		arity="1..*",
   		    description = "list of remote dictionaries "
    		)
    private List<String> remoteUrls = new ArrayList<>(Arrays.asList(
    		new String[] {"https://github.com/petermr/dictionary"}));

    @Option(names = {"--maxEntries"}, 
    		arity="1",
   		    description = "max entries "
    		)
	protected int maxEntries = DEFAULT_MAX_ENTRIES;

    @Option(names = {"--files"}, 
    		arity="1..*",
   		    description = "files"
    		)
	protected List<File> files = new ArrayList<>();

	public DictionaryDisplayTool() {
		super();
	}

	@Override
	protected void parseSpecifics() {
		System.out.println("full                " + full);
		System.out.println("files               " + files);
		System.out.println("maxEntries          " + maxEntries);
		System.out.println("remote              " + remoteUrls);
	}

	@Override
	protected void runSpecifics() {
        runSub();
	}

	public void runSub() {
		getOrCreateExistingDictionaryTop();
//		List<String> argList = Arrays.asList(LIST);
		System.err.println(">"+dictionaryTop.getAbsolutePath());
		files = listDictionaryFiles(dictionaryTop);
		if (files == null) {
			System.err.println("no files");
			return;
		} 
		Collections.sort(files);
		
		if (files.size() > 0) {
			listFiles();
		} else if (full.size() > 0) {
			listFullDictionaryInfo();
		} else {
			listDictionariesBriefly(files);
		}
	}

	private void listDictionariesBriefly(List<File> files) {
		listAllDictionariesBriefly();
		for (File file : files) {
			listDictionaryInfo(file.toString());
		}
	}

	private void listFullDictionaryInfo() {
		for (String fullx : full) {
			listDictionaryInfo(full.toString());
		}
	}

	private void listFiles() {
		DebugPrint.debugPrint("list all FILE dictionaries "+files.size());
		for (File file : files) {
			listDictionaryInfo(FilenameUtils.getBaseName(file.getName()));
		}
	}

	public int getMaxEntries() {
		return maxEntries;
	}

	/** uses directories */
	private void listDictionaryInfo(String dictionaryName) {
		File dictionaryFile = null;
		for (File file : files) {
			String baseName = FilenameUtils.getBaseName(file.getName());
			if (dictionaryName.equals(baseName)) {
				listDictionaryInfo(file, baseName);
				dictionaryFile = file;
				break;
			} else {
			}
		}
		if (dictionaryFile == null) {
			System.err.println("\nUnknown dictionary: "+dictionaryName);
		}
	}

//	private void setMaxEntries(int maxEntries) {
//		this.maxEntries = maxEntries;
//	}

	private List<File> listDictionaryFiles(File dictionaryHead) {
		if (dictionaryHead == null) {
			System.err.println("null dictionaryhead");
			return null;
		}
		
		DebugPrint.debugPrint("dictionaries from "+dictionaryHead);
		
		List<File> newFiles = new ArrayList<File>();
		File[] listFiles = dictionaryHead.listFiles();
		if (listFiles == null) {
			LOG.error("cannot list dictionary files; terminated");
		} else {
			List<File> files = Arrays.asList(listFiles);
			for (File file : files) {
				String filename = file.toString();
				if (XML.equals(FilenameUtils.getExtension(filename))) {
					newFiles.add(file);
				}
			}
			Collections.sort(newFiles);
		}
		return newFiles;
	}

	private void listDictionaryInfo(File file, String dictionaryName) {
		Element dictionaryElement = null;
		try {
			dictionaryElement = XMLUtil.parseQuietlyToRootElement(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find "+file);
		}
		listDictionaryInfo(dictionaryName, dictionaryElement);
		
	}

	private void listHardcoded() {
		System.err.println("\n\nalso hardcoded functions (which resolve abbreviations):\n");
		System.err.println("    gene    (relies on font/style) ");
		System.err.println("    species (resolves abbreviations) ");
	}

	private void listDictionaryInfo(String dictionary, Element dictionaryElement) {
		System.err.println("\nDictionary: "+dictionary);
		List<Element> entries = XMLUtil.getQueryElements(dictionaryElement, "./*[local-name()='entry']");
		System.err.println("entries: "+entries.size());
		printDescs(dictionaryElement);
		printEntries(dictionaryElement);
	}

	private void printDescs(Element dictionaryElement) {
		List<Element> descList = XMLUtil.getQueryElements(dictionaryElement, "./*[local-name()='desc']");
		for (Element desc : descList) {
			System.err.println(desc.getValue());
		}
	}

	private void printEntries(Element dictionaryElement) {
		List<Element> entryList = XMLUtil.getQueryElements(dictionaryElement, "./*[local-name()='entry']");
		for (int i = 0; i < Math.min(entryList.size(), maxEntries); i++) {
			Element entry =  entryList.get(i);
			System.err.println(entry.getAttributeValue("term"));
		}
	}

	// ================== LIST ===================
	
		
		

	public void help(List<String> argList) {
		LOG.error("shouldn't use this help?");
		System.err.println("Dictionary processor");
		System.err.println("    dictionaries are normally added as arguments to search "
				+ "(e.g. ami-search-cooccur [dictionary [dictionary ...]]");
		if (argList.size() == 0) {
			File parentFile = files == null || files.size() == 0 ? null : files.get(0).getParentFile();
			DebugPrint.debugPrint("\nlist of dictionaries taken from AMI dictionary list (" + parentFile + "):\n");
		} else {
			DebugPrint.debugPrint("\nlist of dictionaries taken from : "+argList+"\n");
		}
//		DictionaryDisplayTool dictionaries = new DictionaryTool();
		files = this/*dictionaries*/.getDictionaries();
	//		paths = dictionaries.getDictionaryPaths();
		listAllDictionariesBriefly();
	//		listAllDictionariesBrieflyPaths();
	}

// PATH
	private void listDictionaryPaths(List<String> argList) {
//		File dictionaryHead = new File(NAConstants.MAIN_AMI_DIR, "plugins/dictionary");
		try {
			String pathname = NAConstants.DICTIONARY_RESOURCE;
			LOG.trace("PATHNAME "+pathname);
			pathname = "/"+"org/contentmine/ami/plugins/dictionary";
			final Path path = Paths.get(String.class.getResource(pathname).toURI());
			LOG.trace("PATH "+path);
			FileSystem fileSystem = path.getFileSystem();
			List<FileStore> fileStores = Lists.newArrayList(fileSystem.getFileStores());
			LOG.trace(fileStores.size());
			for (FileStore fileStore : fileStores) {
				LOG.trace("F"+fileStore);
			}
			final byte[] bytes = Files.readAllBytes(path);
			String fileContent = new String(bytes/*, CHARSET_ASCII*/);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	protected void listAllDictionariesBriefly() {
		int count = 0;
		int perLine = 5;
		System.err.print("\n    ");
		for (File file : files) {
			String name = FilenameUtils.getBaseName(file.toString());
			System.err.print((name + "                     ").substring(0, 20));
			if (count++ %perLine == perLine - 1) System.err.print("\n    ");
		}
		listHardcoded();
	}

	private void listAllDictionariesBrieflyPaths() {
			int count = 0;
			int perLine = 5;
			System.err.print("\n    ");
			for (Path path : paths) {
	//			LOG.debug(path);
				String name = FilenameUtils.getBaseName(path.toString());
				System.err.print((name + "                     ").substring(0, 20));
				if (count++ %perLine == perLine - 1) System.err.print("\n    ");
			}
			listHardcoded();
		}

	/** not yet used */
	// PATH VERSION
	private void listDictionaryInfoPath(File file, String dictionary) {
		Element dictionaryElement = null;
		try {
			dictionaryElement = XMLUtil.parseQuietlyToRootElement(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find "+file);
		}
		listDictionaryInfo(dictionary, dictionaryElement);
		
	}

	// PATH VERSION
	private void listDictionaryInfoPath(String dictionaryName) {
		File dictionaryFile = null;
		for (File file : files) {
			String baseName = FilenameUtils.getBaseName(file.getName());
			if (dictionaryName.equals(baseName)) {
				listDictionaryInfo(file, baseName);
				dictionaryFile = file;
				break;
			} else {
			}
		}
		if (dictionaryFile == null) {
			System.err.println("\nUnknown dictionary: "+dictionaryName);
		}
	}

	// ================== LIST ===================
	
		
		
	//	private File getDictionaryDir() {
	//		return dictionaryDir;
	//	}
		
		private List<File> getDictionaries() {
			DebugPrint.debugPrint(" * dictionaries from: "+getOrCreateExistingDictionaryTop());
			// not sure we use this
	//		File xmlDictionaryDir = getXMLDictionaryDir(dictionaryDir);
			File xmlDictionaryDir = dictionaryTop;
			files = new CMineGlobber().setRegex(".*\\.xml").setLocation(xmlDictionaryDir).setRecurse(true).listFiles();
	//		File[] fileArray = xmlDictionaryDir.listFiles(new FilenameFilter() {
	//			public boolean accept(File dir, String name) {
	////				LOG.debug("d"+dir+"/"+name);
	//				return name != null && name.endsWith(".xml");
	//			}
	//		});
	//		files = fileArray == null ? new ArrayList<File>() : Arrays.asList(fileArray);
			Collections.sort(files);
			return files;
		}
		

		/** TEST */
		  private static File[] getResourceFolderFiles (String folder) {
			  System.err.println("RESOURCE FOLDER "+folder);
		    ClassLoader loader = Thread.currentThread().getContextClassLoader();
		    URL url = loader.getResource(folder);
		    if (url == null) {
//		    	System.err.println("Null URL "+folder);
		    	return new File[0];
		    } else {
			    String path = url.getPath();
			    System.err.println(">> "+path);
			    File[] fff = new File(path).listFiles();
//			    System.err.println(fff.length);
	        	for (File ffff : fff) {
	        		getResourceFolderFiles(ffff.toString());
	        	}
			    return fff;
		    }
		  }

		  public static void main (String[] args) {
		    for (File f : getResourceFolderFiles("org/contentmine/ami/plugins/dictionary")) {
  		        System.out.println(f);
  		        if (f.isDirectory()) {
  		        	String ff = f.getAbsolutePath();
  		        	int idx = ff.indexOf("org/contentmine/ami");
  		        	File[] fff = getResourceFolderFiles(ff.substring(idx));
  		        }
		    }
		  }

}
