package org.contentmine.cproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.cproject.args.AbstractTool;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.AbstractMDAnalyzer;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.metadata.crossref.CrossrefAnalyzer;
import org.contentmine.cproject.metadata.crossref.CrossrefMD;
import org.contentmine.cproject.metadata.epmc.EpmcMD;
import org.contentmine.cproject.metadata.quickscrape.QSRecord;
import org.contentmine.cproject.metadata.quickscrape.QuickscrapeLog;
import org.contentmine.cproject.metadata.quickscrape.QuickscrapeMD;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.Multimap;

import nu.xom.Attribute;
import nu.xom.Element;

/** runs CMine commands especially crossref, etc.
 * 
 * Might get split out from as its own class later.
 * 
 * @author pm286
 *
 */
public class CProjectArgProcessor extends DefaultArgProcessor {

	public static final Logger LOG = Logger.getLogger(CProjectArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String RESOURCE_NAME_TOP = "/" + CHESConstants.ORG_CM + "/cproject";
	private static String ARGS_RESOURCE = RESOURCE_NAME_TOP+"/"+"args.xml";
	private static final String SHUFFLE = "shuffle";
	private static final String URLS_TXT = "urls.txt";
	private static final String NO_HTTP = "noHttp";
	private static final String MARK_EMPTY = "markEmpty";

	private String inUrlFilename;
	private AbstractMetadata.Type metadataType;
	private Boolean shuffle;
	private String csvFilename;
	private List<String> csvHeadings;
	private String cProject2Name;
	private List<String> renameTreeOptions;
	private String outUrlFilename;
//	private CTreeList cTreeList;
	private boolean markEmpty;
	private List<String> inUrls;
	private boolean renamePDF;
	private List<String> renameFileOptions;
	private List<String> deleteFiles;
	private String duplicatesDir;
	private File rawDirectory;
	private String prefixOutFilename;
	private List<String> prefixList;
	private String quickscrapeLogName;
	public String quickscrapeNo200Filename;
	public String quickscrapeCaptureFilename;
	private int quickscrapeLowCapture;
	private int quickscrapeHighCapture;
	private String removeUrlsWithPrefixesFilename;

	public CProjectArgProcessor() {
		super(CProjectArgProcessor.class);
		this.readArgumentOptions(this.getArgsResource());
	}
	
	public CProjectArgProcessor(String args) {
		this(args == null ? null : args.replaceAll("\\s+", " ").split(" "));
	}

	public CProjectArgProcessor(String[] args) {
		this();
		setDefaults();
		parseArgs(args);
	}

	public CProjectArgProcessor(AbstractTool abstractTool) {
		super(abstractTool);
	}

	private void setDefaults() {
		shuffle = false;
		metadataType = Type.CROSSREF;
	}

	private String getArgsResource() {
		return ARGS_RESOURCE;
	}
	
	/** create cTrees from raw files
	 */
	public void parseCreateCTreesFrom(ArgumentOption option, ArgIterator argIterator) {
		String rawDirectoryString = argIterator.getString(option);
		rawDirectory = new File(rawDirectoryString);
	}

	/** create filename to extract CSV
	 */
	public void parseCSV(ArgumentOption option, ArgIterator argIterator) {
		List<String> csvArgs = argIterator.getStrings(option);
		csvFilename = null;
		if (csvArgs.size() < 1) {
			csvHelp();
//			throw new RuntimeException("CSV requires filename");
		} else {
			csvFilename = csvArgs.get(0);
			if (csvArgs.size() > 1) {
				csvHeadings = new ArrayList<String>(csvArgs.subList(1, csvArgs.size()));
			} else {
				csvHeadings = new ArrayList<String>(AbstractMetadata.getDefaultHeaders());
			}
		}
	}

	/** deleteFile/s in Ctree
	 */
	public void parseDeleteFile(ArgumentOption option, ArgIterator argIterator) {
		deleteFiles = argIterator.getStrings(option);
	}

	/** directory to output duplicates on merge
	 */
	public void parseDuplicates(ArgumentOption option, ArgIterator argIterator) {
		duplicatesDir = argIterator.getString(option);
	}

	/** file to extract prefixes to
	 */
	public void parseExtractPrefixes(ArgumentOption option, ArgIterator argIterator) {
		prefixOutFilename = argIterator.getString(option);
	}

	/** extract cTrees by prefix
	 */
	public void parseExtractByPrefix(ArgumentOption option, ArgIterator argIterator) {
		prefixList = argIterator.getStrings(option);
	}

	/** create input filename with URLs
	 */
	public void parseInUrls(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		inUrlFilename = (strings.size() == 0) ? getDefaultUrlFilename() : strings.get(0);
		if (strings.size() > 1) {
			markEmpty = strings.get(1).toLowerCase().equalsIgnoreCase(MARK_EMPTY);
		}
	}

	/** create filename to extract URLs to
	 */
	public void parseOutUrls(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		outUrlFilename = (strings.size() == 0) ? getDefaultUrlFilename() : strings.get(0);
		if (strings.size() > 1) {
			shuffle = strings.get(1).toLowerCase().equals(SHUFFLE);
		}
	}

	private String getDefaultUrlFilename() {
		return URLS_TXT;
	}
	
	/** 
	 */
	public void parseMergeProjects(ArgumentOption option, ArgIterator argIterator) {
		cProject2Name = argIterator.getString(option);
	}

	/** 
	 */
	public void parseMetadataType(ArgumentOption option, ArgIterator argIterator) {
		String metadataString = argIterator.getString(option);
		getMetadataType(metadataString);
	}

	/** 
	 */
	public void parseQuickscrapeLog(ArgumentOption option, ArgIterator argIterator) {
		quickscrapeLogName = argIterator.getString(option);
	}

	/** 
	 */
	public void parseQuickscrapeNo200(ArgumentOption option, ArgIterator argIterator) {
		quickscrapeNo200Filename = argIterator.getString(option);
	}

	/** 
	 */
	public void parseQuickscrapeCapture(ArgumentOption option, ArgIterator argIterator) {
		List<String> args = argIterator.getStrings(option);
		if (args.size() != 3) {
			throw new RuntimeException("Require 3 args for quickscrapeCapture (file, lo, hi)");
		}
		quickscrapeCaptureFilename = args.get(0);
		quickscrapeLowCapture = Integer.parseInt(args.get(1));
		quickscrapeHighCapture = Integer.parseInt(args.get(2));
	}

	/** 
	 */
	public void parseRemoveUrlsWithPrefixes(ArgumentOption option, ArgIterator argIterator) {
		removeUrlsWithPrefixesFilename = argIterator.getString(option);
	}

	/** 
	 */
	public void parseRenameFile(ArgumentOption option, ArgIterator argIterator) {
		renameFileOptions = argIterator.getStrings(option);
		if (renameFileOptions.size() == 2) {
			// rename file1 to file2
		} else {
			LOG.error("Illegal rename args length: "+renameFileOptions);
			renameFileOptions = null;
		}
	} 

	/** 
	 */
	public void parseRenameCTree(ArgumentOption option, ArgIterator argIterator) {
		renameTreeOptions = argIterator.getStrings(option);
	}

	/** 
	 */
	public void parseRenamePDF(ArgumentOption option, ArgIterator argIterator) {
		argIterator.getStrings(option);
		renamePDF = true;
	}

	/** shuffle URLs
	 */
	public void parseShuffle(ArgumentOption option, ArgIterator argIterator) {
		shuffle = argIterator.getBoolean(option);
	}

	// ----------- RUN -----------
	
	/** rename files in cTree
	 */
	public void runRenameFile(ArgumentOption option) {
		if (renameFileOptions != null) {
			renameFiles();
		}
	}

	/** delete files in cTree
	 */
	public void runDeleteFile(ArgumentOption option) {
		if (deleteFiles != null) {
			deleteFiles();
		}
	}

	/** rename CTrees
	 */
	public void runRenameCTree(ArgumentOption option) {
		if (renameTreeOptions != null) {
			if (renameTreeOptions.contains(NO_HTTP)) {
				currentCTree.normalizeDOIBasedDirectory();
			}
			// perhaps more options here...
		}
	}
	
	/** rename PDFs
	 */
	public void runRenamePDF(ArgumentOption option) {
		if (renamePDF) {
			List<File> pdfFiles = new ArrayList<File>(FileUtils.listFiles(currentCTree.getDirectory(), new String[]{"pdf", "PDF"}, true));
			renameNonPDFContent(pdfFiles);
		}
	}
	
	// --------- FINAL ------------

	/** create CTrees from raw files
	 * 
	 */
	public void finalCreateCTreesFrom(ArgumentOption option) {
		createCTrees();
	}

	private void createCTrees() {
		checkAndCreateDirectories();
		List<File> fulltexts = new ArrayList<File>
		    (FileUtils.listFiles(rawDirectory, new String[]{"htm", "html", "PDF", "pdf", "xml"}, false));
		for (File file : fulltexts) {
			createAndWriteFulltext(file);
		}
	}

	private void createAndWriteFulltext(File file) {
		String base = FilenameUtils.getBaseName(file.toString());
		base = CMineUtil.stripChars(base, CMineUtil.SPACE_PUNCT, "_");
		String fulltextType = getFulltextType(file);
		if (fulltextType != null) {
			File cTreeDirectory = getUniqueDirectory(base);
			if (cTreeDirectory == null) {
				LOG.warn("Too many identical files of form: "+file);
			} else {
				createAndAddTree(file, base, fulltextType, cTreeDirectory);
			}
		}
	}

	private void createAndAddTree(File file, String base, String fulltextType, File cTreeDirectory) {
		cTreeDirectory.mkdirs();
		try {
			FileUtils.copyFile(file, new File(cTreeDirectory, fulltextType));
		} catch (IOException e) {
			LOG.error("Cannot create CTree "+base);
		}
		CTree cTree = new CTree(cTreeDirectory);
		cProject.add(cTree);
	}

	private File getUniqueDirectory(String base) {
		File cTreeDirectory = new File(cProject.getDirectory(), base);
		if (cTreeDirectory.exists()) {
			cTreeDirectory = null;
			for (int i = 0; i < 100; i++) {
				File temp = new File(cProject.getDirectory(), base+"__"+i);
				if (!temp.exists()) {
					cTreeDirectory = temp;
					break;
				}
			}
		}
		return cTreeDirectory;
	}

	private String getFulltextType(File file) {
		String ext = FilenameUtils.getExtension(file.toString()).toLowerCase();
		String name = null;
		if (CTree.PDF.equals(ext)) {
			name = CTree.FULLTEXT_PDF;
		} else if (CTree.HTML.equals(ext) || "htm".equals(name)) {
			name = CTree.FULLTEXT_HTML;
		} else if (CTree.XML.equals(ext)) {
			name = CTree.FULLTEXT_XML;
		}
		return name;
	}

	private void checkAndCreateDirectories() {
		if (rawDirectory == null) {
			throw new RuntimeException("no directory given for raw files");
		}
		if (!rawDirectory.exists() || !rawDirectory.isDirectory()) {
			throw new RuntimeException(rawDirectory + " is not an existing directory");
		}
		if (cProject == null) {
			throw new RuntimeException("no cproject");
		}
		File directory = cProject.getDirectory();
		if (directory == null) {
			throw new RuntimeException("no cproject directory");
		}
		if (directory.exists()) {
			if (!directory.isDirectory()) {
				throw new RuntimeException("existing cProject is not a directory: "+directory);
			}
			if (cProject.getOrCreateCTreeList().size() > 0) {
				LOG.info("creating in non-empty directory");
			}
		} else {
			directory.mkdirs();
		}
	}
	
	/** final extract to CSV
	 */
	public void finalCSV(ArgumentOption option) {
		if (csvFilename == null) {
			throw new RuntimeException("must give csvFile");
		}
		try {
			File csvFile = new File(cProject.getDirectory(), csvFilename);
			AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
			crossrefAnalyzer.addRowsToTable(csvHeadings, AbstractMetadata.Type.CROSSREF);
			crossrefAnalyzer.createMultisets();
			crossrefAnalyzer.writeCsvFile(csvFile);
		} catch (IOException e) {
			throw new RuntimeException("cannot write CSV: "+csvFilename, e);
		}
	}

	/** final duplicate
	 */
	public void finalDuplicates(ArgumentOption option) {
		if (duplicatesDir != null) {
			CTreeList duplicateList = cProject.getOrCreateDuplicateMergeList();
			CProject duplicateProject = writeProject(duplicateList);
		}
	}

	private CProject writeProject(CTreeList duplicateList) {
		CProject duplicateProject = new CProject(new File(duplicatesDir));
		duplicateProject.addCTreeList(duplicateList);
		try {
			duplicateProject.writeProjectAndCTreeList();
		} catch (IOException e) {
			throw new RuntimeException("Cannot write project: "+duplicatesDir, e);
		}
		return duplicateProject;
	}

	/** final extractPrefixes
	 */
	public void finalExtractPrefixes(ArgumentOption option) {
		if (prefixOutFilename != null) {
			try {
				createAndOutputCTreeListsElement();
			} catch (IOException e) {
				throw new RuntimeException("Cannot write prefix file", e);
			}
		}
	}

	private void createAndOutputCTreeListsElement() throws IOException, FileNotFoundException {
		Multimap<String, CTree> cTreesByPrefix = cProject.getCTreeListsByPrefix();
		List<String> prefixes = new ArrayList<String>(cTreesByPrefix.keySet());
		Collections.sort(prefixes);
		Element cTreeListsElement = new Element("cTreeLists");
		for (String prefix : prefixes) {
			Element cTreeListElement = new Element("cTreeList");
			cTreeListsElement.appendChild(cTreeListElement);
			cTreeListElement.addAttribute(new Attribute("prefix", prefix));
			CTreeList cTreeList = new CTreeList(new ArrayList<CTree>(cTreesByPrefix.get(prefix)));
			for (CTree cTree : cTreeList) {
				Element cTreeElement = new Element("cTree");
				cTreeElement.addAttribute(new Attribute("name", cTree.getDirectory().getName()));
				cTreeListElement.appendChild(cTreeElement);
			}
		}
		File prefixOutFile = prefixOutFilename.contains(File.separator) ? new File(prefixOutFilename) :
			new File(cProject.getDirectory(), prefixOutFilename);
		FileUtils.forceMkdir(prefixOutFile.getParentFile());
		XMLUtil.debug(cTreeListsElement, new FileOutputStream(prefixOutFile), 1);
	}

	/** final extractByPrefix
	 */
	public void finalExtractByPrefix(ArgumentOption option) {
		if (prefixList != null) {
			if (prefixList.size() == 0) {
				if (inputList != null && inputList.size() == 1) {
					File inputFile = new File(inputList.get(0));
					try {
						prefixList = FileUtils.readLines(inputFile);
					} catch (IOException e) {
						throw new RuntimeException("Cannot read prefix list from "+inputFile, e);
					}
				} else {
					LOG.warn("no prefixes gives and no (single) inputFile (--input) ");
					return;
				}
 			}
			LOG.trace("prefix list "+prefixList);
			if (output == null) {
				throw new RuntimeException("must give output directory (--output)");
			}
			writeSubProjectsByPrefix();
		}
	}

	private void writeSubProjectsByPrefix() {
		File outputDir = output.contains(File.separator) ? new File(output) : new File(cProject.getDirectory(), output);
		try {
			FileUtils.forceMkdir(outputDir);
		} catch (IOException e) {
			throw new RuntimeException("cannot make directory: "+outputDir, e);
		}
		
		for (String prefix : prefixList) {
			LOG.trace("Making subdirectory: "+prefix);
			CProject subProject = new CProject(new File(outputDir, prefix));
			CTreeList cTreeList = cProject.getCTreesWithDOIPrefix(prefix);
			subProject.addCTreeListAndCopyContents(cTreeList);
			try {
				subProject.writeProjectAndCTreeList();
			} catch (IOException e) {
				LOG.error("Cannot write subProject: "+cProject);
				continue;
			}
		}
	}



	/** final input Urls
	 */
	public void finalInUrls(ArgumentOption option) {
		String name = FilenameUtils.getName(inUrlFilename);
		File inUrlFile = new File(cProject.getDirectory(), name);
		inUrls = null;
		try {
			inUrls = FileUtils.readLines(inUrlFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read input URLS", e);
		}
		
		cTreeList = cProject.getOrCreateCTreeList();
		Map<String, String> urlByCTreeName = createUrlByCTreeMap();
		removeUrlsForCtreesWithQuickscrapeMD(urlByCTreeName);
		inUrls = new ArrayList<String>(urlByCTreeName.values());
	}

	private Map<String, String> createUrlByCTreeMap() {
		Map<String, String> urlByCTreeName = new HashMap<String, String>();
		for (int i = inUrls.size() - 1; i >= 0; i--) {
			String inUrl = inUrls.get(i);
			String s = CMineUtil.stripHttpDOI(inUrl).replaceAll("/", "_");
			urlByCTreeName.put(s, inUrl);
		}
		return urlByCTreeName;
	}

	private void removeUrlsForCtreesWithQuickscrapeMD(Map<String, String> urlByCTreeName) {
		for (CTree cTree : cTreeList) {
			if (CTree.getExistingQuickscrapeMD(cTree) != null) {
				String name1 = cTree.getDirectory().getName();
				urlByCTreeName.remove(name1); 
			}
		}
	}

	/** final merge
	 */
	public void finalMergeProjects(ArgumentOption option) {
		if (cProject == null) {
			throw new RuntimeException("mergeProjects must have existing CProject");
		}
		CProject cProject2 = new CProject(new File(cProject2Name));
		try {
			cProject.mergeProject(cProject2);
		} catch (IOException e) {
			throw new RuntimeException("Cannot merge projects: "+e);
		}
	}
			
	/** final output Urls
	 */
	public void finalOutUrls(ArgumentOption option) {
		try {
			File outUrlFile = new File(cProject.getDirectory(), outUrlFilename);
			if (inUrls != null) {
				FileUtils.writeLines(outUrlFile, inUrls, "\n");
			} else {
				cProject.extractShuffledUrlsFromCrossrefToFile(outUrlFile);
			}
		} catch (IOException e) {
			throw new RuntimeException("cannot write urls: "+outUrlFilename, e);
		}
	}
	
	/** final merge
	 */
	public void finalQuickscrapeLog(ArgumentOption option) {
		File quickscrapeLogFile = new File(cProject.getDirectory(), quickscrapeLogName);
		QuickscrapeLog quickscrapeLog = null;
		try {
			quickscrapeLog = QuickscrapeLog.readLog(quickscrapeLogFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read quickscrape log: "+quickscrapeLogFile, e);
		}
		List<QSRecord> allRecords = quickscrapeLog.getQSURLRecords();
		if (quickscrapeNo200Filename != null) {
			try {
				quickscrapeLog.writeNo200Records(this);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write No200s "+quickscrapeNo200Filename, e);
			}
		}
		if (quickscrapeCaptureFilename != null) {
			try {
				quickscrapeLog.writeNoCaptureRecords(this);
			} catch (IOException ee) {
				throw new RuntimeException("Cannot write NoCapturess "+quickscrapeCaptureFilename, ee);
			}
		}
	}

	public void finalRemoveUrlsWithPrefixes(ArgumentOption option) {
		if (removeUrlsWithPrefixesFilename != null) {
			if (inUrlFilename == null || outUrlFilename == null) {
				throw new RuntimeException("Must have inUrls and outUrls");
			}
			List<String> inUrls = null;
			try {
				inUrls = FileUtils.readLines(new File(inUrlFilename));
			} catch (Exception e) {
				throw new RuntimeException("Cannot read inUrls", e);
			}
			Set<String> prefixSet = createPrefixSet();
			try {
				writeOutUrls(inUrls, prefixSet);
			} catch (Exception e) {
				throw new RuntimeException("Cannot read inUrls");
			}
		}
	}

	private Set<String> createPrefixSet() {
		List<Element> prefixes = readPrefixElements(removeUrlsWithPrefixesFilename);
		Set<String> prefixSet = new HashSet<String>();
		for (Element prefix : prefixes) {
			String prf = prefix.getAttributeValue("prefix");
			prefixSet.add(prf);
		}
		return prefixSet;
	}

	private void writeOutUrls(List<String> inUrls, Set<String> prefixSet) throws IOException {
		List<String> outUrls = new ArrayList<String>();
		for (String inUrl : inUrls) {
			String prefix = CMineUtil.getDOIPrefix(inUrl);
			if (!prefixSet.contains(prefix)) {
				outUrls.add(prefix);
			}
		}
		FileUtils.writeLines(new File(outUrlFilename), outUrls);
	}

	private List<Element> readPrefixElements(String removeUrlsWithPrefixesFilename) {
		Element element = null;
		try {
			element = XMLUtil.parseQuietlyToDocument(
					new FileInputStream(removeUrlsWithPrefixesFilename)).getRootElement();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot read prefixes File "+removeUrlsWithPrefixesFilename, e);
		}
		List<Element> elements = XMLUtil.getQueryElements(element, "doiPrefixes/prefix");
		return elements;
	}
	
			

	// ---------------
	
	private void renameFiles() {
		if (renameFileOptions.size() == 2) {
			String inputRegex = renameFileOptions.get(0);
			String outputRegex = renameFileOptions.get(1);
			File oldFile = new File(currentCTree.getDirectory(), inputRegex);
			File newFile = new File(currentCTree.getDirectory(), outputRegex);
			if (oldFile.exists()) {
				// FIXME rename should use regex
				boolean renamed = oldFile.renameTo(newFile);
				if (!renamed) {
					LOG.error("could not rename "+oldFile+" to "+newFile);
				} else {
					LOG.trace("renamed "+oldFile + " to " +newFile);
				}
			}
		}
	}
	
	private void deleteFiles() {
		if (deleteFiles != null) {
			for (String deleteFile : deleteFiles) {
				File file = new File(currentCTree.getDirectory(), deleteFile);
				if (file.exists()) {
					if (file.isDirectory()) {
						try {
							FileUtils.deleteDirectory(file);
						} catch (IOException e) {
							LOG.warn("Cannot delete directory: "+file);
						}
					} else {
						FileUtils.deleteQuietly(file);
					}
				}
			}
		}
	}
	
	private void touchQuickscrapeMDInEmptyDirectories(CTree cTree) {
		if (markEmpty) {
			if (cTree.getExistingQuickscrapeMD() == null) {
				cTree.createFile(AbstractMetadata.Type.QUICKSCRAPE.getCTreeMDFilename());
			}
		}
	}
	
	private void renameNonPDFContent(List<File> pdfFiles) {
		for (File pdfFile : pdfFiles) {
			String type = CMineUtil.getTypeOfContent(pdfFile);
			if (type == null) {
				// continue
			} else if (CMineUtil.PDF_TYPE.equals(type)) {
					// continue
			} else if (CMineUtil.HTML_TYPE.equals(type)) {
				String newName = pdfFile.getAbsolutePath() + ".html";
				pdfFile.renameTo(new File(newName));
				LOG.debug("renamed "+pdfFile+" to "+newName);
			}
		}
	}


	
	//=================

	private void csvHelp() {
		if (metadataType == null) {
			AbstractMetadata.csvHelp();
		} else if (Type.CROSSREF == metadataType) {
			CrossrefMD.csvHelp();
		} else if (Type.EPMC == metadataType) {
			EpmcMD.csvHelp();
		} else if (Type.QUICKSCRAPE == metadataType) {
			QuickscrapeMD.csvHelp();
		}
	}


	private AbstractMetadata.Type getMetadataType(String metadataString) {
		if (metadataString != null) {
			metadataString = metadataString.toUpperCase();
			metadataType = AbstractMetadata.Type.valueOf(metadataString);
		}
		return metadataType;
	}


	
	// ===============
	@Override
	/** parse args and resolve their dependencies.
	 *
	 * (don't run any argument actions)
	 *
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

}
