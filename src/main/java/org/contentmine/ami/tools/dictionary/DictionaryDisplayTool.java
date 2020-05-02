package org.contentmine.ami.tools.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.contentmine.ami.tools.AMIDictionaryToolOLD.Operation;
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
				+ "   ${COMMAND-FULL-NAME} create --informat wikipage%n"
				+ "    --input https://en.wikipedia.org/wiki/List_of_fish_common_names%n"
				+ "    --dictionary commonfish --directory mydictionary --outformats xml,html%n"
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

    @Option(names = {"--remote"}, 
    		arity="1..*",
   		    description = "list of remote dictionaries "
    		)
    private List<String> remoteUrls = new ArrayList<>(Arrays.asList(
    		new String[] {"https://github.com/petermr/dictionary"}));


    protected List<File> files;
	protected int maxEntries = 0;

	public DictionaryDisplayTool() {
		super();
	}

	@Override
	protected void parseSpecifics() {
		System.err.println("parseSpecifics NYI "+this.getClass());
//		printDebug();
	}

	@Override
	protected void runSpecifics() {
		System.err.println("runSpecifics NYI "+this.getClass());
        runSub();
	}

	public void runSub() {
		List<String> argList = Arrays.asList(LIST);
		files = listDictionaryFiles(dictionaryTop);
		Collections.sort(files);
		
		if (argList.size() == 1 && argList.get(0).toUpperCase().equals(LIST)) {
			DebugPrint.debugPrint("list all FILE dictionaries "+files.size());
			for (File file : files) {
				listDictionaryInfo(FilenameUtils.getBaseName(file.getName()));
			}
		} else if (argList.size() >= 1 && argList.get(0).toUpperCase().equals(FULL)) {
			argList.remove(0);
			maxEntries = DEFAULT_MAX_ENTRIES;
			if (argList.size() >= 1) {
				String arg = argList.get(0);
				try {
					maxEntries = Integer.parseInt(arg);
					argList.remove(0);
				} catch (NumberFormatException nfe) {
//					DebugPrint.debugPrintln(Level.ERROR, "Requires maxEntries, found: "+arg);
				}
			}
			for (String arg : argList) {
				listDictionaryInfo(arg);
			}
		} else {
			listAllDictionariesBriefly();
			for (String arg : argList) {
				listDictionaryInfo(arg);
			}
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

}
