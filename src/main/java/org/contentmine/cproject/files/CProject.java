package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
//import org.apache.http.HttpException;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CProjectArgProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.args.FileXPathSearcher;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.metadata.ProjectAnalyzer;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.cache.CorpusCache;
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.contentmine.pdf2svg2.PDFDocumentProcessor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import nu.xom.Element;
import nu.xom.Node;

public class CProject extends CContainer {

	public static final Logger LOG = Logger.getLogger(CProject.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String PROJECT_TEMPLATE_XML = "cProjectTemplate.xml";
	public static final String TREE_TEMPLATE_XML    = "cTreeTemplate.xml";
	public static final String URL_LIST             = "urlList.txt";
	// attempt to capture history
	public static final String LOGFILE_JSON         = "log.json";
	public static final String MAKE_PROJECT_JSON    = "make_project.json";
	private static final String OLD_FILE = "oldFile";
	private static final String NEW_FILE = "newFile";
	
	public final static String IMAGE   = "image";
	public final static String RESULTS = "results";
	public final static String TABLE   = "table";

	// suffixes
	private static final String HTML = "html";

	// move these to plugin subdirs later
	public static final String SPECIES_GENUS_SNIPPETS_XML = "species.genus.snippets.xml";
	public static final String SPECIES_BINOMIAL_SNIPPETS_XML = "species.binomial.snippets.xml";
	public static final String GENE_HUMAN_SNIPPETS_XML = "gene.human.snippets.xml";
	public static final String SEQUENCE_DNAPRIMER_SNIPPETS_XML = "sequence.dnaprimer.snippets.xml";
	public static final String WORD_FREQUENCIES_SNIPPETS_XML = "word.frequencies.snippets.xml";
	
	public static final String DATA_TABLES_HTML = "dataTables.html";
	// will relocate later
	private static final String SUPPNAME = "suppname";
	private static final String C_TREE = "cTree";
	public static final String HTTP_ACS_SUPPDATA = 
			"https?://pubs\\.acs\\.org/doi/suppl/10\\.1021/(?<"+C_TREE+">.*)/suppl_file/(?<"+SUPPNAME+">.*)\\.pdf";
	public static final String MAKE_PROJECT = "makeProject";
	public static final String FILE_FILTER  = "fileFilter";

	protected static final String[] ALLOWED_METADATA_NAMES = new String[] {
			// these are messy, 
			AbstractMetadata.Type.CROSSREF.getCProjectMDFilename(),
			AbstractMetadata.Type.EPMC.getCProjectMDFilename(),
			AbstractMetadata.Type.QUICKSCRAPE.getCProjectMDFilename(),
	};

	protected static final String[] ALLOWED_FILE_NAMES = new String[] {
			
		MANIFEST_XML,
		LOG_XML,
		URL_LIST,
		
		AbstractMetadata.Type.CROSSREF.getCProjectMDFilename(),
		AbstractMetadata.Type.EPMC.getCProjectMDFilename(),
		AbstractMetadata.Type.QUICKSCRAPE.getCProjectMDFilename(),

	};
	
	protected static final Pattern[] ALLOWED_FILE_PATTERNS = new Pattern[] {
	};
	
	protected static final String[] ALLOWED_DIR_NAMES = new String[] {
		RESULTS,
		TABLE,
		IMAGE,
	};
	
	protected static final Pattern[] ALLOWED_DIR_PATTERNS = new Pattern[] {
	};
	public static final String OMIT_EMPTY = "omitEmpty";
	
	/** contains --makeProject and standard PDF args	*/
	public static String MAKE_PROJECT_PDF = 
			" --" + MAKE_PROJECT + " (\\1)/" + CTree.FULLTEXT + "." + CTree.PDF + " --" + FILE_FILTER + " .*/(.*)\\.pdf";
//	public static String MAKE_PROJECT_CMD = 
//			" --makeProject (\\1)/fulltext.pdf --fileFilter .*/(.*)\\.pdf";


	private CTreeList cTreeList;
	private ProjectSnippetsTree projectSnippetsTree;
	private ProjectFilesTree projectFilesTree;
	private ResultsElementList summaryResultsElementList;
	private ArrayList<File> scholarlyList;
	private ProjectAnalyzer projectAnalyzer;
	private CTreeList duplicateMergeList;
	private DefaultArgProcessor argProcessor;

	private CProjectIO projectIO;
	private CorpusCache corpusCache;
	private CTreeList includeCTreeList;
	private BiMap<File, File> newFileByOld;
	private JsonArray renamedFileFileArray;
	private PDFDocumentProcessor pdfDocumentProcessor;
	private List<String> omitRegexList;
	
	public static void main(String[] args) {
		CProject cProject = new CProject();
		cProject.run(args);
	}

	public void run(String[] args) {
		argProcessor = new CProjectArgProcessor(args);
		argProcessor.runAndOutput();
	}

	public void run(String args) {
		run(args.split("\\s+"));
	}

	public DefaultArgProcessor getArgProcessor() {
		return argProcessor;
	}

	/** mainly served for running commandlines
	 * 
	 */
	public CProject() {
		super();
		init();
	}

	public CProject(File cProjectDir) {
		super();
		this.directory = cProjectDir;
		init();
	}
	
	public CorpusCache getOrCreateCorpusCache() {
		if (corpusCache == null) {
			corpusCache = new CorpusCache(this);
			corpusCache.setCProject(this);
		}
		return corpusCache;
	}
	
	protected void init() {
		super.init();
		getOrCreateProjectIO();
	}

	public CProjectIO getOrCreateProjectIO() {
		if (projectIO == null) {
			this.projectIO = new CProjectIO();
		}
		return projectIO;
	}
	
	@Override
	protected CManifest createManifest() {
		manifest = new CProjectManifest(this);
		return manifest;
	}
	
	@Override
	protected void calculateFileAndCTreeLists() {
		cTreeList = new CTreeList();
		int i = 0;
		for (File directory : allChildDirectoryList) {
			if (false) {
			} else if (
				(isAllowedFile(directory, ALLOWED_DIR_PATTERNS) ||
				isAllowedFileName(directory, ALLOWED_DIR_NAMES))) {
				allowedChildDirectoryList.add(directory);
				// don't consider for CTree
			} else if (isCTree(directory)) {
				CTree cTree = new CTree(directory);
				cTreeList.add(cTree);
			} else {
				unknownChildDirectoryList.add(directory);
			}
		}
		return;
	}

	@Override
	protected void getAllowedAndUnknownFiles() {
		for (File file : allChildFileList) {
			if (false) {
			} else if (
				isAllowedFile(file, ALLOWED_FILE_PATTERNS) ||
				isAllowedFileName(file, ALLOWED_FILE_NAMES) ||
				includeAllDirectories()) {
				if (!allowedChildFileList.contains(file)) {
					allowedChildFileList.add(file);
				}
			} else {
				if (!unknownChildFileList.contains(file)) {
					unknownChildFileList.add(file);
				}
			}
		}
	}
	
	public List<File> getAllNonDirectoryFiles() {
		getAllowedAndUnknownFiles();
		List<File> allFiles = new ArrayList<File>(allowedChildFileList);
		for (File file : unknownChildFileList) {
			if (!allFiles.contains(file)) {
				allFiles.add(file);
			}
		}
		return allFiles;
	}
	
	private boolean isAllowedFilename(String filename) {
		return (Arrays.asList(ALLOWED_FILE_NAMES).contains(filename));
	}

	/** currently just take a simple approach.
	 * 
	 * if manifest.xml or fulltext.* or results.json is present this should be OK
	 * later we'll use manifest templates
	 * 
	 * @param directory
	 * @return
	 */
	private boolean isCTree(File directory) {
		getTreesAndDirectories();
		CTree testTree = new CTree(directory);
		testTree.getOrCreateChildDirectoryAndChildFileList();
		// put filenames first to eliminate matching
		boolean allowed = 
				isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_NAMES) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_NAMES) ||
				isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_PATTERNS) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_PATTERNS) ||
				includeAllDirectories()
				;
		return allowed;
	}

	/** getCTreeList after recalculating from current Files.
	 * also sorts it
	 * to get Current CTreeList, use getCTreeList()
	 * @return
	 */
	public CTreeList getOrCreateCTreeList() {
		this.getOrCreateFilesDirectoryCTreeLists();
		if (cTreeList != null) {
			cTreeList.sort();
		} else {
			cTreeList = new CTreeList();
		}
		return cTreeList;
	}
		

	public List<File> getResultsXMLFileList() {
		List<File> resultsXMLList = new ArrayList<File>();
		this.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			List<File> resultsXMLList0 = cTree.getResultsXMLFileList();
			resultsXMLList.addAll(resultsXMLList0);
		}
		return resultsXMLList;
	}

	public List<File> getResultsXMLFileList(String control) {
		List<File> resultsXMLList = getResultsXMLFileList();
		if (CProject.OMIT_EMPTY.equals(control)) {
			for (int i = resultsXMLList.size() - 1; i >= 0; i--) {
				File f = resultsXMLList.get(i);
				if (ResultsElement.isEmpty(f)) {
					resultsXMLList.remove(i);
				}
			}
		}
		return resultsXMLList;
	}

	public CTree getCTreeByName(String name) {
		CTree cTree = null;
		if (name != null) {
			getOrCreateCTreeList();
			if (cTreeList != null) {
				cTree = cTreeList.get(name);
			}
		}
		return cTree;
	}
	/** outputs filenames relative to project directory.
	 * 
	 * normalizes to UNIX separator
	 * 
	 * i.e. file.get(i) should be equivalent to new File(cprojectDirectory, paths.get(i))
	 * 
	 * @param files
	 * @return list of relative paths
	 */
	public List<String> getRelativeProjectPaths(List<File> files) {
		List<String> fileNames = new ArrayList<String>();
		for (File file : files) {
			String fileName = getRelativeProjectPath(file);
			if (fileName != null) {
				fileNames.add(fileName);
			}
		}
		return fileNames;
	}

	/** outputs filenams relative to project directory.
	 * 
	 * normalizes to UNIX separator
	 * 
	 * i.e. file should be equivalent to new File(cprojectDirectory, path)
	 * 
	 * @param file
	 * @return relative path; null if cannot construct it.
	 */
	public String getRelativeProjectPath(File file) {
		String directoryName = FilenameUtils.normalize(directory.getAbsolutePath(), true);
		String fileName = FilenameUtils.normalize(file.getAbsolutePath(), true);
		String pathName = null;
		if (fileName.startsWith(directoryName)) {
			pathName = fileName.substring(directoryName.length() + 1); // includes separator
		}
		return pathName;
	}

	/**
	 * 
	 * @param glob (e.g. ** /word/ ** /result.xml) [spaces to escape comments so remove spaces a]
	 * @return
	 */
	public ProjectFilesTree extractProjectFilesTree(String glob) {
		ProjectFilesTree projectFilesTree = new ProjectFilesTree(this);
		CTreeList cTreeList = this.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			CTreeFiles cTreeFiles = cTree.extractCTreeFiles(glob);
			projectFilesTree.add(cTreeFiles);
		}
		return projectFilesTree;
	}

	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public ProjectSnippetsTree extractProjectSnippetsTree(String glob, String xpath) {
		projectSnippetsTree = new ProjectSnippetsTree(this);
		CTreeList cTreeList = this.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			SnippetsTree snippetsTree = cTree.extractXPathSnippetsTree(glob, xpath);
			if (snippetsTree.size() > 0) {
				projectSnippetsTree.add(snippetsTree);
			}
		}
		return projectSnippetsTree;
	}
	
	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public ProjectSnippetsTree extractProjectSnippetsTree(String searchExpression) {
		FileXPathSearcher fileXPathSearcher = new FileXPathSearcher(searchExpression);
		String glob = fileXPathSearcher.getCurrentGlob();
		String xpath = fileXPathSearcher.getCurrentXPath();
		projectSnippetsTree = extractProjectSnippetsTree(glob, xpath);
		return projectSnippetsTree;
	}

	public ProjectSnippetsTree getProjectSnippetsTree() {
		return projectSnippetsTree;
	}
	
	public ProjectFilesTree getProjectFilesTree() {
		return projectFilesTree;
	}

	public void add(CTreeFiles cTreeFiles) {
		ensureProjectFilesTree();
		projectFilesTree.add(cTreeFiles);
	}

	private void ensureProjectFilesTree() {
		if (projectFilesTree == null) {
			projectFilesTree = new ProjectFilesTree(this);
		}
	}

	public void add(SnippetsTree snippetsTree) {
		ensureProjectSnippetsTree();
		projectSnippetsTree.add(snippetsTree);
	}

	private void ensureProjectSnippetsTree() {
		if (projectSnippetsTree == null) {
			projectSnippetsTree = new ProjectSnippetsTree(this);
		}
	}

	public void outputProjectSnippetsTree(File outputFile) {
		outputTreeFile(projectSnippetsTree, outputFile);
	}

	public void outputProjectFilesTree(File outputFile) {
		outputTreeFile(projectFilesTree, outputFile);
	}

	private void outputTreeFile(Element tree, File outputFile)  {
		if (tree != null) {
			try {
				XMLUtil.debug(tree, outputFile, 1);
				LOG.trace("wrote: "+outputFile);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write output: ", e);
			}
		}
	}

	public void addSummaryResultsElement(ResultsElement summaryResultsElement) {
		ensureSummaryResultsElementList();
		LOG.trace("> "+summaryResultsElement.toXML());
		summaryResultsElementList.addToMultiset(summaryResultsElement);
	}

	private void ensureSummaryResultsElementList() {
		if (this.summaryResultsElementList == null) {
			this.summaryResultsElementList = new ResultsElementList();
		}
	}
	
	public Multiset<String> getMultiset() {
		return summaryResultsElementList == null ? null : summaryResultsElementList.getMultisetSortedByCount();
	}

	/** requires all cTrees to have scholarlyHtml
	 * This is a BAD idea!
	 * @param fraction of CTrees that need to have scholarly.html
	 * 
	 * @return
	 */
	public boolean hasScholarlyHTML(double fractionRequired) {
		cTreeList = this.getOrCreateCTreeList();
		
		int hasNot = 0;
		int total = 0;
		for (CTree cTree : cTreeList) {
			if (!cTree.hasScholarlyHTML()) {
				// if require all, then quit immediately
				if (Math.abs(fractionRequired - 1.0) > 0.001) {
					return false;
				}
				hasNot++;
			}
			total++;
		}
		double fractionWithout = (double) hasNot / (double) total;
		return  fractionWithout <=  (1.0 - fractionRequired);  
	}

	/** heuristic lists all CProjects under projectTop directory.
	 * finds descendant files through glob and tests them for conformity with CProject
	 * globbing through CMineGlobber
	 * 
	 * @param projectTop
	 * @param glob - allows selection of possible projects
	 * @return
	 */
	public static List<CProject> globCProjects(File projectTop, String glob) {
		List<CProject> projectList = new ArrayList<CProject>();
		List<File> possibleProjectFiles = CMineGlobber.listGlobbedFilesQuietly(projectTop, glob);
		for (File possibleProjectFile : possibleProjectFiles) {
			if (possibleProjectFile.isDirectory()) {
				CProject cProject = CProject.createPossibleCProject(possibleProjectFile);
				if (cProject != null) {
					projectList.add(cProject);
				}
			}
		}
		return projectList;
	}

	public Set<String> extractMetadataItemSet(AbstractMetadata.Type sourceType, String type) {
		CTreeList cTreeList = getOrCreateCTreeList();
		Set<String> set = new HashSet<String>();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, sourceType);
			String typeValue = metadata.getJsonStringByPath(type);
			set.add(typeValue);
		}
		return set;
	}

	public Multimap<String, String> extractMetadataItemMap(AbstractMetadata.Type sourceType, String key, String type) {
		CTreeList cTreeList = getOrCreateCTreeList();
		Multimap<String, String> map = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, sourceType);
			if (metadata != null) {
				String keyValue = metadata.getJsonStringByPath(key);
				String typeValue = metadata.getJsonStringByPath(type);
				map.put(keyValue, typeValue);
			}
		}
		return map;
	}
	
	/** gets all files relating to a reserved name.
	 * 
	 * result is a multimap, The file list is map.values()
	 * 
	 * @param reservedName
	 * @return
	 */
	public Multimap<CTree, File> extractCTreeFileMapContaining(String reservedName) {
		CTreeList cTreeList = getOrCreateCTreeList();
		Multimap<CTree, File> map = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			File file = cTree.getExistingReservedFile(reservedName);
			if (file != null && file.exists()) {
				map.put(cTree, file);
			}
		}
		return map;
	}
	
	public File createAllowedFile(String filename) {
		File file = null;
		if (isAllowedFilename(filename)) {
			file = new File(directory, filename);
		}
		return file;
	}
	
	// ====================
	
	public ArrayList<File> getOrCreateScholarlyHtmlList() {
		List<File> files = new ArrayList<File>(FileUtils.listFiles(
				getDirectory(), new String[]{HTML}, true));
		scholarlyList = new ArrayList<File>();
		for (File file : files) {
			if (file.getName().equals(CTree.SCHOLARLY_HTML)) {
				scholarlyList.add(file);
			}
		}
		return scholarlyList;
	}

	public Multiset<String> getOrCreateHtmlBiblioKeys() {
		getOrCreateScholarlyHtmlList();
		Multiset<String> keySet = HashMultiset.create();
		for (File scholarly : scholarlyList) {
			HtmlElement htmlElement = HtmlElement.create(XMLUtil.parseQuietlyToDocument(scholarly).getRootElement());
			List<Node> nodes = XMLUtil.getQueryNodes(htmlElement, "//*[local-name()='meta']/@name");
			for (Node node : nodes) {
				String name = node.getValue().toLowerCase();
				name = name.replace("dcterms", "dc");
				keySet.add(name);
			}
		}
		return keySet;
	}

	private static CProject createPossibleCProject(File possibleProjectFile) {
		CProject project = new CProject(possibleProjectFile);
		CTreeList cTreeList = project.getOrCreateCTreeList();
		return (cTreeList.size() == 0) ? null : project;
		
	}

	public CTreeList getCTreeList(CTreeExplorer explorer) {
		CTreeList cTreeListOld = this.getOrCreateCTreeList();
		CTreeList cTreeList = new CTreeList();
		for (CTree cTree : cTreeListOld) {
			if (cTree.matches(explorer)) {
				cTreeList.add(cTree);
			}
		}
		return cTreeList;
	}

	/** return cTrees with given names.
	 * 
	 * @param treeNames
	 * @return
	 */
	public CTreeList createCTreeList(List<String> treeNames) {
		Set<String> treeNameSet = new HashSet<String>(treeNames);
		CTreeList cTreeList = this.getOrCreateCTreeList();
		CTreeList cTreeListNew = new CTreeList();
		for (CTree cTree : cTreeList) {
			if (treeNameSet.contains(cTree.getName())) {
				cTreeListNew.add(cTree);
			}
		}
		return cTreeListNew;
	}

	public void normalizeDOIBasedDirectoryCTrees() {
		getOrCreateCTreeList();
		for (int i = cTreeList.size() - 1; i >= 0; i--) {
			CTree cTree = cTreeList.get(i);
			cTree.normalizeDOIBasedDirectory();
		}
	}

	public List<String> extractShuffledCrossrefUrls() {
		ProjectAnalyzer projectAnalyzer = this.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		List<String> urls = projectAnalyzer.extractURLs();
		return urls;
	}

	
	public void extractShuffledUrlsFromCrossrefToFile(File file) throws IOException {
		ProjectAnalyzer projectAnalyzer = this.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		projectAnalyzer.extractURLsToFile(file);
	}
	
	public void setProjectAnalyzer(ProjectAnalyzer projectAnalyzer) {
		this.projectAnalyzer = projectAnalyzer;
	}

	public ProjectAnalyzer getOrCreateProjectAnalyzer() {
		if (this.projectAnalyzer == null) {
			this.projectAnalyzer = new ProjectAnalyzer(this);
		}
		return projectAnalyzer;
	
	}

	/** get DOIPrefixes.
	 * 
	 * (not unique) may be multiple entries 
	 * @return
	 */
	public List<String> getDOIPrefixList() {
		List<String> doiPrefixList = new ArrayList<String>();
		CTreeList cTreeList = this.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			String doiPrefix = cTree.extractDOIPrefix();
			doiPrefixList.add(doiPrefix);
		}
		return doiPrefixList;
	}

	/** get CTrees with given DOIPrefixe.
	 * 
	 * @return
	 */
	public CTreeList getCTreesWithDOIPrefix(String prefix) {
		CTreeList cTreeList = this.getOrCreateCTreeList();
		CTreeList treesWithPrefix = new CTreeList();
		if (prefix != null) {
			for (CTree cTree : cTreeList) {
				String doiPrefix = cTree.extractDOIPrefix();
				if (doiPrefix == null) {
					LOG.warn("null DOI prefix: "+cTree.getDirectory());
				} else if (prefix.equals(doiPrefix)) {
					treesWithPrefix.add(cTree);
				}
			}
		}
		return treesWithPrefix;
	}

	public int size() {
		getOrCreateCTreeList();
		return (cTreeList == null) ? 0 : cTreeList.size();
	}

	public List<String> extractShuffledFlattenedCrossrefUrls() {
		List<String> urls = extractShuffledCrossrefUrls();
		List<String> flattenedUrls = new ArrayList<String>();
		for (int j = 0; j < urls.size(); j++) {
			String url = urls.get(j);
			String flattenedURL = CMineUtil.denormalizeDOI(url);
			flattenedUrls.add(flattenedURL);
		}
		return flattenedUrls;
	}

	public File getMetadataFile(AbstractMetadata.Type type) {
		return (directory == null) ? null : new File(this.getDirectory(), type.getCProjectMDFilename());
	}

	public File getExistingMetadataFile(AbstractMetadata.Type type) {
		File resultsJson = getMetadataFile(type);
		return (resultsJson == null || !resultsJson.exists()) ? null : resultsJson;
	}

	/** gets a list of all metadataTypes which have been used to create or manage the CProject.
	 * 
	 *  based on whether the metadata files exist
	 * 
	 * @return
	 */
	public List<AbstractMetadata.Type> getExistingMetadataTypes() {
		List<Type> types = new ArrayList<Type>();
		for (Type type : Type.values()) {
			if (this.getExistingMetadataFile(type) != null) {
				types.add(type);
			}
		}
		return types;
	}

	/** merges one Cproject into another.
	 * 
	 * @param cProject2
	 * @throws IOException 
	 */
	public void mergeProject(CProject project2) throws IOException {
		CTreeList cTreeList2 = project2.getOrCreateCTreeList();
		copyCTrees(cTreeList2);
		cTreeList = null;
		copyFiles(project2);
		resetFileLists();
	}

	private void copyFiles(CProject project2) throws IOException {
		List<File> projectFiles2 = project2.getAllNonDirectoryFiles();
//		List<File> projectFiles = this.getAllNonDirectoryFiles();
//		JsonParser jsonParser = new JsonParser();
		for (File file2 : projectFiles2) {
			String name2 = file2.getName();
			File thisFile = this.getFileWithName(name2);
			if (thisFile == null) {
				FileUtils.copyFile(file2, new File(this.directory, name2));
			} else if (Type.getTypeFromCProjectFile(file2) != null) {
				Type.mergeMetadata(thisFile, file2);
			} else {
				LOG.debug("existing file, so not copied: "+file2);
			}
		}
	}

	/** finds file with given name.
	 * 
	 * @param name
	 * @return
	 */
	public File getFileWithName(String name) {
		File[] files = directory == null ? null : directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().equals(name)) {
					return file;
				}
			}
		}
		return null;
	}

	private void copyCTrees(CTreeList cTreeList2) {
		for (CTree cTree2 : cTreeList2) {
			try {
				this.ingestCopy(cTree2);
			} catch (IOException ioe) {
				LOG.warn("Cannot ingest CTree: "+cTree2);
			}
		}
	}

	/** creates a copy of CTree within this.cProject.
	 * 
	 * keeps a list of duplicate trees 
	 * 
	 * @param cTree2
	 * @throws IOException 
	 */
	public void ingestCopy(CTree cTree2) throws IOException {
		getOrCreateCTreeList();
		getOrCreateDuplicateMergeList();
  		if (!cTreeList.containsName(cTree2)) {
			copyCTreeAndUpdateNonOverlappingCTreeList(cTree2);
		} else {
			duplicateMergeList.add(cTree2);
		}
	}

	private void copyCTreeAndUpdateNonOverlappingCTreeList(CTree cTree2) throws IOException {
		File directory2 = cTree2.getDirectory();
		String name2 = directory2.getName();
		File cTreeDirectory = new File(this.directory, name2);
		FileUtils.copyDirectory(directory2, cTreeDirectory);
		CTree thisCTree = new CTree(cTreeDirectory);
		this.cTreeList.add(thisCTree);
	}
	
	public CTreeList getOrCreateDuplicateMergeList() {
		if (duplicateMergeList == null) {
			duplicateMergeList = new CTreeList();
		}
		return duplicateMergeList;
	}

	public void add(CTree cTree) {
		ensureCTreeList();
		cTreeList.add(cTree);
	}
	
	public void addCTreeList(CTreeList cTreeList) {
		ensureCTreeList();
		this.cTreeList = this.cTreeList.or(cTreeList);
	}

	public void addCTreeListAndCopyContents(CTreeList cTreeList1) {
		addCTreeList(cTreeList1);
		for (int i = 0; i < cTreeList1.size(); i++) {
			try {
				File directory1 = cTreeList1.get(i).getDirectory();
				File thisDirectory = new File(this.getDirectory(), directory1.getName());
				FileUtils.copyDirectory(directory1, thisDirectory);
			} catch (IOException e) {
				LOG.error("Cannot copy Directory: "+cTreeList.get(i).getDirectory()+" "+e);
			}
		}
	}

	protected CTreeList ensureCTreeList() {
		if (cTreeList == null) {
			cTreeList = new CTreeList();
		}
		return cTreeList;
	}

	public void writeProjectAndCTreeList() throws IOException {
		LOG.warn("*****CTreeList is memory intensive - write every CTree and clear memory*****");
		if (directory != null) {
			directory.mkdirs();
			if (cTreeList != null) {
				for (CTree cTree : cTreeList) {
					cTree.write(directory);
				}
			}
		}
	}

	/** get Multimap of CTrees indexed by DOIPrefix.
	 * 
	 * @return 
	 */
	public Multimap<String, CTree> getCTreeListsByPrefix() {
		CTreeList cTreeList = this.getOrCreateCTreeList();
		Multimap<String, CTree> treeListsbyPrefix = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			String doiPrefix = cTree.extractDOIPrefix();
			treeListsbyPrefix.put(doiPrefix, cTree);
		}
		return treeListsbyPrefix;
	}

	public void convertPDF2SVG() {
		getOrCreateCTreeList();
	    for (CTree cTree : cTreeList) {
	    	try {
	    		LOG.debug(">> "+cTree);
	    		cTree.convertPDF2SVG();
	    	} catch (Exception e) {
	    		throw new RuntimeException("PDF2SVG threw", e);
	    	}
	    }
	}

	public void convertPDF2HTML() {
		ensureCTreeList();
	    for (CTree cTree : cTreeList) {
	    	try {
	    		LOG.debug(">> "+cTree);
	    		cTree.convertPDF2HTML();
	    	} catch (Exception e) {
	    		throw new RuntimeException("PDF2HTML threw", e);
	    	}
	    }
		
	}
	
	/** NYI
	 * 
	 */
	public void convertSVG2HTML() {
		LOG.error("convertSVG2HTML NYI");
		for (CTree cTree : getOrCreateCTreeList()) {
			LOG.debug("============="+cTree.getDirectory()+"=============");
			DocumentCache documentCache = cTree.getOrCreateDocumentCache();			
			HtmlDiv element = documentCache.convertSVGPages2HTML();
		}
	}

	public void setCorpusCache(CorpusCache corpusCache) {
		this.corpusCache = corpusCache;
	}

	/** creates SVG and extracts Images from PDF in CTrees.
	 * 
	 * Maybe shouldn't be in CProject, but that would need redesign
	 * 
	 */
	public void convertPDFOutputSVGFilesImageFiles() {
		CTreeList cTreeList = getIncludeCTreeList();
		cTreeList = cTreeList != null ? cTreeList : getOrCreateCTreeList();
		getOrCreatePDFDocumentProcessor();
		pdfDocumentProcessor.setMinimumImageBox(100, 100);
		for (CTree cTree : cTreeList) {
			cTree.setDebugLevel(debugLevel);
			String name = cTree.getName();
	        System.out.println(">cTree>: "+name);
	        cTree.setPDFDocumentProcessor(pdfDocumentProcessor);
			cTree.processPDFTree();
		}
		LOG.trace("Finished PDFSVG");
	}

	public PDFDocumentProcessor getOrCreatePDFDocumentProcessor() {
		if (pdfDocumentProcessor == null) {
			pdfDocumentProcessor = new PDFDocumentProcessor();
		}
		return pdfDocumentProcessor;
	}

	/** get files specifically included */
	private CTreeList getIncludeCTreeList() {
		return includeCTreeList;
	}

	/** simple "make" - to be enhanced later 
	 * 
	 * @param outputDir
	 * @return
	 */
	private boolean skipExistingDir(File outputDir) {
		boolean skip = false;
		if (outputDir.exists()) {
			skip = true;
		}
		return skip;
	}

	/** converts SVG from PDF files to HTML.
	 */
	public void convertPSVGandWriteHtml() {
		CTreeList cTreeList = getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			DebugPrint.debugPrint(Level.INFO, "cTree: "+cTree);
			cTree.createAndWriteScholorlyHtml();
		}
	}

	/** extracts project from URLs.
	 * */
	public static CProject makeProjectFromURLs(File projectDir, List<String> urlSList, String httpPattern) {
		Pattern pattern = Pattern.compile(httpPattern);
		CProject cProject = null;
		if (urlSList != null) {
			if (!projectDir.exists()) projectDir.mkdirs();
			cProject = new CProject(projectDir);
			for (String urlS : urlSList) {
				Matcher matcher = pattern.matcher(urlS);
				String cTreeName =  null;
				String suppdata = null;
				if (matcher.matches()) {
					cTreeName = matcher.group(C_TREE);
					suppdata = matcher.group(SUPPNAME);
					CTree cTree = cProject.getExistingCTreeOrCreateNew(cTreeName);
					if (cTree != null) {
						readURL(urlS, cTree);  
					}
				} else {
					LOG.error("Fails match: "+urlS+"\n"+pattern);
				}
			}
		}
		return cProject;
	}

	private static void readURL(String urlS, CTree cTree) /*throws MalformedURLException */{
		
		/**
		File pdfFile = new File(cTree.getDirectory(), CTree.FULLTEXT_PDF);
		URL url = new URL(urlS);
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(url);
		
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				new DefaultHttpMethodRetryHandler(3, false));

		try {
		  // Execute the method.
		  int statusCode = client.executeMethod(method);

		  if (statusCode != HttpStatus.SC_OK) {
		    System.err.println("Method failed: " + method.getStatusLine());
		  }

		  // Read the response body.
		  byte[] responseBody = method.getResponseBody();

		  // Deal with the response.
		  // Use caution: ensure correct character encoding and is not binary data
		  System.out.println(new String(responseBody));

		} catch (HttpException e) {
		  System.err.println("Fatal protocol violation: " + e.getMessage());
		  e.printStackTrace();
		} catch (IOException e) {
		  System.err.println("Fatal transport error: " + e.getMessage());
		  e.printStackTrace();
		} finally {
		  // Release the connection.
		  method.releaseConnection();
		}
		*/
	}

	public CTree getExistingCTreeOrCreateNew(String cTreeName) {
		CTree cTree = getCTreeByName(cTreeName);
		if (cTree == null) {
			File cTreeFile = new File(directory, cTreeName);
			cTree = new CTree(cTreeFile);
			this.add(cTree);
			if (!cTreeFile.exists()) {
				cTreeFile.mkdirs();
			}
		}
		return cTree;
	}

	/** turns foo.suffix into foo/fulltext.suffix for each suffix */	
	public void makeProject(List<String> suffixes, int compress) {
		if (suffixes != null) {
			renamedFileFileArray = new JsonArray();
			for (String suffix : suffixes) {
				makeProject(suffix, compress);
			}
		}
		return;
	}

	/** turns foo.suffix into foo/fulltext.suffix.
	 * 
	 * if more than one suffix use makeProject(String[] suffixes, int compress)
	 * 
	 * */	
	public void makeProject(String suffix, int compress) {
		List<File> files = saveRaw(suffix);
		if (files.size() == 0) {
			return;
		}
		CMFileUtil fileUtil = new CMFileUtil();
		fileUtil.add(files);
		fileUtil.compressFileNames(compress);
		newFileByOld = fileUtil.getOrCreateNewFileByOldFile();
		List<File> oldFiles = new ArrayList<File>(newFileByOld.keySet());
		List<File> newFiles = new ArrayList<File>();
		for (File oldFile : oldFiles) {
			boolean omit = false;
			if (omitRegexList != null) {
				for (String omitRegex : omitRegexList) {
					String name = oldFile.getName();
					if (Pattern.matches(omitRegex, name)) {
						omit = true;
						break;
					}
				}
			}
			if (!omit) {
				File newFile = newFileByOld.get(oldFile);
				newFiles.add(newFile);
				addMappedFilesToRenamedFileArray(oldFile, newFile);
			}
		}
		
		if (newFiles.size() > 0) {
			runMakeProjectCommandLine(suffix);
		}
		return;
	}

	private void runMakeProjectCommandLine(String suffix) {
//				Collections.sort(newFiles);
		String args = "--project "+this.getDirectory()+" --" +
            MAKE_PROJECT + " (\\1)/" + CTree.FULLTEXT + "."+suffix + " --fileFilter .*/(.*)\\." + suffix;
		this.run(args);
	}

	private void addMappedFilesToRenamedFileArray(File oldFile, File newFile) {
		JsonObject newOldFileObject = new JsonObject();
		newOldFileObject.addProperty(NEW_FILE, newFile.toString());
		newOldFileObject.addProperty(OLD_FILE, oldFile.toString());
		renamedFileFileArray.add(newOldFileObject);
	}

	private List<File> saveRaw(String suffix) {
		File rawDir = this.directory;
		List<File> files =  CMineGlobber.listSortedChildFiles(rawDir, suffix);
		files = removeFulltext(suffix, files);
		if (files.size() > 0) {
			if (!rawDir.exists()) {
				LOG.debug("Made dir for copies: "+rawDir);
				rawDir.mkdirs();
			}
			for (File file : files) {
				File movedFile = new File(rawDir, file.getName());
				if (!movedFile.exists()) {
					try {
						FileUtils.moveFile(file, movedFile);
					} catch (IOException e) {
						LOG.debug("failed to save file: " + file + ", " + e);
					}
				}
			}
		}
		return files;
	}

	public void deleteCTrees(CTreeList treeList) throws IOException {
		for (CTree cTree : treeList) {
			deleteCTree(cTree);
		}
	}
	
	public void deleteCTree(CTree cTree) throws IOException {
		if (cTree != null) {
			File cTreeDirectory = cTree.getDirectory();
			// make sure this is a child of this, just to be safe
			File parentFile = cTreeDirectory != null ? cTreeDirectory.getParentFile() : null;
			if (this.directory.toString().equals(parentFile.toString())) {
				LOG.debug("deleting: "+cTreeDirectory);
				FileUtils.forceDelete(cTreeDirectory);
			}
		}
	}

	/** this doesn't look right!!
	 * 
	 * @param suffix
	 * @param files
	 * @return
	 */
	private List<File> removeFulltext(String suffix, List<File> files) {
		List<File> newFiles = new ArrayList<File>();
		for (File file : files) {
			if (!file.toString().endsWith(CTree.FULLTEXT+CTree.DOT+suffix)) {
				newFiles.add(file);
			}
		}
		files = newFiles;
		return files;
	}

	public void setIncludeTreeList(List<String> treeNames) {
		includeCTreeList = this.createCTreeList(treeNames);
		return;
	}

	public void setCTreelist(CTreeList cTreeList) {
		this.cTreeList = cTreeList;
	}

//	public static CProject createProjectFromPDFsAndMakeCTrees(File sourceDir) throws IOException {
//		File targetDir = sourceDir;
//		CProject cProject = new CProject(targetDir);
//		MakeProject.makeProject(sourceDir);
//		CTreeList cTreeList = cProject.getOrCreateCTreeList();
//		for (CTree cTree : cTreeList) {
//			LOG.debug("******* "+cTree+" **********");
//		    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
//		    documentProcessor.setMinimumImageBox(100, 100);
//		    documentProcessor.readAndProcess(cTree.getExistingFulltextPDF());
//		    File outputDir = new File(targetDir, cTree.getName());
//			documentProcessor.writeSVGPages(outputDir);
//	    	documentProcessor.writePDFImages(outputDir);
//		}
//		return cProject;
//	}

	/** tidy images in Ctrees but also look for commonailty at Documrnt level.
	 * 
	 */
	public void tidyImages() {
		CTreeList cTreeList = getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			cTree.tidyImages();
		}
	}

	/** clean files of given name.
	 * 
	 * @param arg
	 */
	public void cleanTrees(String filename) {
		for (CTree cTree : this.getOrCreateCTreeList()) {
			cTree.cleanFileOrDirs(filename);
		}
	}

	/** clean files of given name.
	 * 
	 * @param arg
	 */
	public void cleanRegex(String arg) {
		for (CTree cTree : this.getOrCreateCTreeList()) {
			cTree.cleanRegex(arg);
		}
	}

	public File getMakeProjectLogfile(String filename) {
		File file = new File(this.directory, filename);
		try {
			JsonObject log = new JsonObject();
			log.addProperty("date", new Date().toString());
			log.add("log from "+this.getDirectory(), renamedFileFileArray);
			
			String prettyString = Util.prettyPrintJson(log);
			FileUtils.write(file, prettyString, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("cannnot write log file: "+file, e);
		}
		return file;
	}
	
	public File getMakeProjectLogfile() {
		return getMakeProjectLogfile(CProject.MAKE_PROJECT_JSON);
	}
	
	public String toString() {
		String s = super.toString();
		return s;
	}

	public String getName() {
		return directory == null ? null : directory.getName();
	}

	public void setOmitRegexList(List<String> omitRegexList) {
		this.omitRegexList = omitRegexList;
	}


}
