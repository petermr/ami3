package org.contentmine.ami.tools.dictionary;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
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

	public static final Logger LOG = LogManager.getLogger(DictionaryDisplayTool.class);

	private static final String AUTHORS = "authors";
	private static final String DATE = "date";
	private static final String DESCRIPTION = "description";
	private static final String ID = "id";
	private static final String FULL = "FULL";
	private static final String LIST = "LIST";
	private static final String NAME = "name";
	private static final String TERM = "term";
	private static final String TITLE = "title";
	private static final String URL = "url";
	private static final String WIKIDATA = "wikidata";
	private static final String WIKIPEDIA = "wikipedia";
	private static final int    DEFAULT_MAX_ENTRIES = 3;
	
	private static List<String> ALLOWED_DICTIONARY_ATTRIBUTES = new ArrayList<>(Arrays.asList(
			new String[]{}));
	private static List<String> MANDATORY_DICTIONARY_ATTRIBUTES = new ArrayList<>(Arrays.asList(
			new String[]{TITLE}));
	private static List<String> ALLOWED_DESC_ATTRIBUTES = new ArrayList<>(Arrays.asList(
			new String[]{AUTHORS, DATE}));
	private static List<String> MANDATORY_DESC_ATTRIBUTES = new ArrayList<>(Arrays.asList(
			new String[]{}));
	private static List<String> ALLOWED_ENTRY_ATTRIBUTES = new ArrayList<>(Arrays.asList(
			new String[]{ID, DESCRIPTION, NAME, TERM, URL, WIKIPEDIA, WIKIDATA }));
	private static List<String> MANDATORY_ENTRY_ATTRIBUTES = new ArrayList<>(Arrays.asList(
			new String[]{TERM}));


	public enum DescAttributeName {
		author("name, free form"),
		date("as ISO 8601"),
		type("free form"),
		;
		private String description;

		private DescAttributeName(String description) {
			this.description = description; 
		}

		/** gets DescAttributeName by value
		 * @param val
		 * @return
		 */
		public static List<String> getDescAttributeNames(Element desc) {
			List<String> attNames = new ArrayList<>();
			for (DescAttributeName value : values()) {
				String attName = value.toString();
				if (desc.getAttributeValue(attName) != null) {
					attNames.add(attName);
				}
			}
			return attNames;
		}
		
	}
	
	private List<Path> paths;
	
    @Parameters(index = "0",
    		arity="0..*",
//    		split=",",
    		description = "primary operation: (${COMPLETION-CANDIDATES}); if no operation, runs help"
    		)
    private Operation operation = Operation.help;

    @Option(names = {"--fields"}, 
    		arity="0..*",
    	    split=",",
   		    description = "list of fields to report 0 = (${COMPLETION-CANDIDATES}); 0 parameters lists all"
    		)
    private List<DictionaryField> fields = new ArrayList<>();

    @Option(names = {"--files"}, 
    		arity="1..*",
   		    description = "files to list (paths may be added later)"
    		)
	protected List<File> files = new ArrayList<>();
    
    @Option(names = {"--maxEntries"}, 
    		arity="1",
   		    description = "max entries to list (${DEFAULT-VALUE}) in dictionary"
    		)
	protected int maxEntries = DEFAULT_MAX_ENTRIES;

    @Option(names = {"--remote"}, 
    		arity="1..*",
   		    description = "list of remote dictionaries (not sure of format yet!)"
    		)
    private List<String> remoteUrls = new ArrayList<>(Arrays.asList(
    		new String[] {"https://github.com/petermr/dictionary"}));

    @Option(names = {"--suffix"}, 
   		    description = "suffix for dictionary, default XML)"
    		)
    private String suffix = CTree.XML;
    
    @Option(names = {"--validate"}, 
   		    description = "add validation annotation)"
    		)
    private boolean validate = false;
    

//    private String dictionary;

	public DictionaryDisplayTool() {
		super();
	}

	@Override
	protected void parseSpecifics() {
		super.parseSpecifics();
		if (fields.size() == 0) {
			fields = new ArrayList<>(Arrays.asList(DictionaryField.values()));
			LOG.info("list all fields ");
		}
	}

	@Override
	protected void runSpecifics() {
        runSub();
	}

	public void runSub() {
		getOrCreateExistingDictionaryTop();
		getOrCreateSingleDictionaryName();
		LOG.info(">"+dictionaryTop.getAbsolutePath());
		if (dictionaryName != null) {
			listDictionaryInfo(new File(dictionaryTop, dictionaryName + "." + suffix));
		} else {
			List<File> fileList = (files.size() > 0) ? files : collectDictionaryFiles(dictionaryTop);
			listFiles(fileList);
		}
	}
	
	private String getOrCreateSingleDictionaryName() {
		if (dictionaryName == null) {
			if (parent.getDictionaryNameList().size() == 1) {
				dictionaryName = parent.getDictionaryNameList().get(0);
			}
		}
		return dictionaryName;
	}

	private void listFiles(List<File> files) {
		if (files.size() == 0) {
			LOG.warn("no files");
		} else {
			Collections.sort(files);
			LOG.debug(DebugPrint.MARKER, "list all FILE dictionaries "+files.size());
			for (File file : files) {
				listDictionaryInfo(file);
			}
		}
	}

	public int getMaxEntries() {
		return maxEntries;
	}
	
	private void listHardcoded() {
//		LOG.warn("\n\nalso hardcoded functions (which resolve abbreviations):\n");
//		LOG.warn("    gene    (relies on font/style) ");
//		LOG.warn("    species (resolves abbreviations) ");
	}

	public void listDictionaryInfo() {
		
		LOG.warn("\nDictionary: " + simpleDictionary.getDictionaryName()+"\n");
		List<Element> entries = XMLUtil.getQueryElements(simpleDictionary.getDictionaryElement(), "./*[local-name()='entry']");
		LOG.warn("entries: "+entries.size());
		LOG.trace("validate "+validate);
		validateWithXPath();
		printFieldSummary();
		printDictionary();
		printDescs();
		printEntries();
	}

	private void validateWithXPath() {
		validateDictionaryAttributes();
		validateDictionaryChildren();
		validateDescAttributes();
		validateDescChildren();
		validateEntryAttributes();
		validateEntryChildren();
	}

	private void validateDictionaryAttributes() {
		String dictionaryName = simpleDictionary.getDictionaryName();
		validate("./*[@title and not(@title='"+dictionaryName+"')]", 
				"******Bad dictionary title (" + dictionaryName + ") ****** ");
	}

	private void validateDictionaryChildren() {
		validate("./*[local-name()!='desc' and local-name()!='entry']", 
				"******Bad dictionary children****** ");
	}

	private void validateDescAttributes() {
		validate("./*[local-name()='desc' and @* and not(@date) and not(@author)]", 
				"******Bad desc attributes****** ");
	}

	private void validateDescChildren() {
		validate("./*[local-name()='desc' and count(*) > 0]",
				"******Bad desc children ****** ");
	}

	private void validateEntryAttributes() {
		// unusual attributes?
		validate("./*[local-name()='entry' and @*["
				+ " not("
				+ "    name()='term'"
				+ " or name()='name'"
				+ " or name()='description'"
				+ " or name()='id'"
				+ " or name()='wikipediaPage'"
				+ " or name()='wikipediaURL'"
				+ " or name()='wikidataID'"
				+ " or name()='wikidataURL'"
				+ " or name()='wikidataAltLabel'"

				+ " or starts-with(name(),'_p')"
				+ " or starts-with(name(),'_q')"
				+ " or starts-with(name(),'_')"
				+ ")"
				+ "]"
				
				+ "]",
				"******Unrecognised attribute(s) on entry****** \n"
				+ "[term/name/description/id/wikipediaPage/wikipediaURL/wikidataAltLabel]\n"
				+ "or starts-with _p<number> or _q<number> or _\n");
		
		
	}
	private void validateEntryChildren() {
		validate("./*[local-name()='entry']/*[not(local-name()='synonym')]",
				"******Unrecognised child on entry****** ");
	}

	private void validate(String xpath, String message, Element element) {
		List<Node> nodes = XMLUtil.getQueryNodes(element, xpath);
		if (nodes.size() != 0) {
			for (int i = 0; i < Math.min(nodes.size(), maxEntries); i++) {
				LOG.error(message + nodes.get(i).toXML());
			}
		}
	}

	private void validate(String xpath, String message) {
		validate(xpath, message, simpleDictionary.getDictionaryElement());
	}

	/** counts the predefined attributes
	 * 
	 */
	private void printFieldSummary() {
		LOG.info("> fieldSummary: ");
		StringBuilder sb = new StringBuilder();
		for (DictionaryField field : fields) {
			Multiset<String> fieldSet = HashMultiset.create();
			if (field.getType().equals(FieldType.ATTRIBUTE)) {
				String xpath = ".//*[@*[name()='"+field.toString()+"' and not(.='')]]";
				List<Element> elements = XMLUtil.getQueryElements(simpleDictionary.getDictionaryElement(), xpath);
				String fieldS = field.toString();
				sb.append("@" + fieldS + ": " + elements.size() + " ");
				for (Element element : elements) {
					String fieldValue = element.getAttributeValue(fieldS);
					LOG.info(fieldS + "> "+fieldValue);
					fieldSet.add(fieldValue);
				}
			}
			List<Entry<String>> fieldList = MultisetUtil.createListSortedByCount(fieldSet);
			LOG.info(field+": "+fieldList);
		}
		LOG.info("Attributes: "+sb);
	}

	private void printDictionary() {
		LOG.trace("dictionary: " + simpleDictionary.getDictionaryName());
		for (int i = 0; i < simpleDictionary.getDictionaryElement().getAttributeCount(); i++) {
			Attribute attribute = simpleDictionary.getDictionaryElement().getAttribute(i);
			String attName = attribute.getLocalName();
			LOG.info("dictionary@" + attName + ": " + simpleDictionary.getDictionaryElement().getAttributeValue(attName));
		}
		if (validate) {
			LOG.info("======= start validation =======");
			validateDictionary();
			LOG.info("======= end validation =======");
		}
	}

	private String validateDictionary() {
		for (int i = 0; i < simpleDictionary.getDictionaryElement().getAttributeCount(); i++) {
			Attribute attribute = simpleDictionary.getDictionaryElement().getAttribute(i);
			String attName = attribute.getLocalName();
			LOG.info("dictionary@" + attName + ": " + simpleDictionary.getDictionaryElement().getAttributeValue(attName));
		}
		return null;
	}

	private void printDescs() {
		List<Element> descList = XMLUtil.getQueryElements(simpleDictionary.getDictionaryElement(), "./*[local-name()='desc']");
		for (Element desc : descList) {
			LOG.info("Desc: "+desc.getValue());
			if (validate) {
				validateDesc(desc);
			}
		}
	}

	private String validateDesc(Element desc) {
		List<String> attNames = DescAttributeName.getDescAttributeNames(desc);
		for (String attName : attNames) {
			LOG.info(attName + ": " + desc.getAttributeValue(attName));
		}
		return null;
	}

	private void printEntries() {
		List<Element> entryList = XMLUtil.getQueryElements(simpleDictionary.getDictionaryElement(), "./*[local-name()='entry']");
		for (int i = 0; i < entryList.size(); i++) {
			Element entry =  entryList.get(i);
			if (i < maxEntries) {
				String term = entry.getAttributeValue(TERM);
				LOG.warn("    "+term);
				if (false && validate) { // validation elsewhere
					String message = validateAttributes(entry, MANDATORY_ENTRY_ATTRIBUTES, ALLOWED_ENTRY_ATTRIBUTES);
//					message += validateChildren(entry, MANDATORY_ENTRY_CHILDREN, ALLOWED_ENTRY_CHILDREN);
					if (message.trim().length() != 0) {
						LOG.warn("**** " + term + ": " + message);
					}
				}
			}
		}
		if (maxEntries < entryList.size()) {
			LOG.warn("    " + "....");
		}
	}

	private String validateChildren(Element entry, List<String> mandatoryChildren, List<String> allowedChildren) {
		StringBuilder sb = new StringBuilder();
		sb.append(checkMandatoryChildren(entry, mandatoryChildren));
		sb.append(checkAllowedChildren(entry, allowedChildren));
		return sb.toString();
	}

	private String checkMandatoryChildren(Element entry, List<String> mandatoryList) {
		StringBuilder sb = new StringBuilder("missing children");
		boolean ok = true;
		for (String mandatory : mandatoryList) {
			String term = entry.getAttributeValue(mandatory);
			if (term == null) {
				LOG.error("No "  + mandatory + " for: " + entry.toXML());
				sb.append(" " + mandatory);
				ok = false;
			}
		}
		return ok ? "" : sb.toString();
	}
	
	private String checkAllowedChildren(Element entry, List<String> allowedList) {
		StringBuilder sb = new StringBuilder("unknown children");
		boolean ok = true;
		for (int i = 0; i < entry.getAttributeCount(); i++) {
			String attName = entry.getAttribute(i).getLocalName();
			if (!allowedList.contains(attName)) {
				ok = false;
				sb .append(" " + attName);
			}
		}
		return ok ? "" : sb.toString();
	}
	private String validateAttributes(Element entry, List<String> mandatoryAttributes, List<String> allowedAttributes) {
		StringBuilder sb = new StringBuilder();
		sb.append(checkMandatoryAttributes(entry, mandatoryAttributes));
		sb.append(checkAllowedAttributes(entry, allowedAttributes));
		return sb.toString();
	}

	private String checkMandatoryAttributes(Element entry, List<String> mandatoryList) {
		StringBuilder sb = new StringBuilder("missing attributes");
		boolean ok = true;
		for (String mandatory : mandatoryList) {
			String term = entry.getAttributeValue(mandatory);
			if (term == null) {
				LOG.error("No "  + mandatory + " for: " + entry.toXML());
				sb.append(" " + mandatory);
				ok = false;
			}
		}
		return ok ? "" : sb.toString();
	}
	
	private String checkAllowedAttributes(Element entry, List<String> allowedList) {
		StringBuilder sb = new StringBuilder("unknown attributes");
		boolean ok = true;
		for (int i = 0; i < entry.getAttributeCount(); i++) {
			String attName = entry.getAttribute(i).getLocalName();
			if (!allowedList.contains(attName)) {
				ok = false;
				sb .append(" " + attName);
			}
		}
		return ok ? "" : sb.toString();
	}

	// ================== LIST ===================
	
		
		

	public void help(List<String> argList) {
		LOG.error("shouldn't use this help?");
		System.err.println("Dictionary processor");
		System.err.println("    dictionaries are normally added as arguments to search "
				+ "(e.g. ami-search-cooccur [dictionary [dictionary ...]]");
		if (argList.size() == 0) {
			File parentFile = files == null || files.size() == 0 ? null : files.get(0).getParentFile();
			LOG.debug(DebugPrint.MARKER,"\nlist of dictionaries taken from AMI dictionary list (" + parentFile + "):\n");
		} else {
			LOG.debug(DebugPrint.MARKER, "\nlist of dictionaries taken from : "+argList+"\n");
		}
		List<File> files = this/*dictionaries*/.getDictionaries();
		listAllDictionariesBriefly(files);
	}

//// PATH
//	private void listDictionaryPaths(List<String> argList) {
////		File dictionaryHead = new File(NAConstants.MAIN_AMI_DIR, "plugins/dictionary");
//		try {
//			String pathname = NAConstants.DICTIONARY_RESOURCE;
//			LOG.trace("PATHNAME "+pathname);
//			pathname = "/"+"org/contentmine/ami/plugins/dictionary";
//			final Path path = Paths.get(String.class.getResource(pathname).toURI());
//			LOG.trace("PATH "+path);
//			FileSystem fileSystem = path.getFileSystem();
//			List<FileStore> fileStores = Lists.newArrayList(fileSystem.getFileStores());
//			LOG.trace(fileStores.size());
//			for (FileStore fileStore : fileStores) {
//				LOG.trace("F"+fileStore);
//			}
//			final byte[] bytes = Files.readAllBytes(path);
//			String fileContent = new String(bytes/*, CHARSET_ASCII*/);
//		} catch (Exception e) {
//			LOG.error(e);
//		}
//	}

	/** used in Help */
	private void listAllDictionariesBriefly(List<File> files) {
		System.out.println("LIST FILES BRIEFLY");
		int count = 0;
		int perLine = 5;
		System.out.print("\n    ");
		for (File file : files) {
			String name = FilenameUtils.getBaseName(file.toString());
			System.out.print((name + "                     ").substring(0, 20));
			if (count++ %perLine == perLine - 1) System.err.print("\n    ");
		}
		listHardcoded();
	}

//	private void listAllDictionariesBrieflyPaths() {
//			int count = 0;
//			int perLine = 5;
//			System.err.print("\n    ");
//			for (Path path : paths) {
//	//			LOG.debug(path);
//				String name = FilenameUtils.getBaseName(path.toString());
//				System.err.print((name + "                     ").substring(0, 20));
//				if (count++ %perLine == perLine - 1) System.err.print("\n    ");
//			}
//			listHardcoded();
//		}

//	/** not yet used */
//	// PATH VERSION
//	private void listDictionaryInfoPath(File file, String dictionary) {
//		Element dictionaryElement = null;
//		try {
//			dictionaryElement = XMLUtil.parseQuietlyToRootElement(new FileInputStream(file));
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException("Cannot find "+file);
//		}
//		listDictionaryInfo(dictionary, dictionaryElement);
//		
//	}

//	// PATH VERSION
//	private void listDictionaryInfoPath(String dictionaryName) {
//		File dictionaryFile = null;
//		for (File file : files) {
//			String baseName = FilenameUtils.getBaseName(file.getName());
//			if (dictionaryName.equals(baseName)) {
//				listDictionaryInfo(baseName, file);
//				dictionaryFile = file;
//				break;
//			} else {
//			}
//		}
//		if (dictionaryFile == null) {
//			System.err.println("\nUnknown dictionary: "+dictionaryName);
//		}
//	}

	// ================== LIST ===================
	
		
		
	//	private File getDictionaryDir() {
	//		return dictionaryDir;
	//	}
		
		private List<File> getDictionaries() {
			LOG.debug(DebugPrint.MARKER, " * dictionaries from: "+getOrCreateExistingDictionaryTop());
			// not sure we use this
			File xmlDictionaryDir = dictionaryTop;
			files = new CMineGlobber().setRegex(".*\\.xml").setLocation(xmlDictionaryDir).setRecurse(true).listFiles();
			Collections.sort(files);
			return files;
		}
		

		/** TEST */
		  private static File[] getResourceFolderFiles (String folder) {
			  System.err.println("RESOURCE FOLDER "+folder);
		    ClassLoader loader = Thread.currentThread().getContextClassLoader();
		    URL url = loader.getResource(folder);
		    if (url == null) {
		    	return new File[0];
		    } else {
			    String path = url.getPath();
			    System.err.println(">> "+path);
			    File[] fff = new File(path).listFiles();
	        	for (File ffff : fff) {
	        		getResourceFolderFiles(ffff.toString());
	        	}
			    return fff;
		    }
		  }

		  /** not sure this is useful */
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
