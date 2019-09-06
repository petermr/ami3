package org.contentmine.cproject.args;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.log.AbstractLogElement;
import org.contentmine.cproject.args.log.AbstractLogElement.LogLevel;
import org.contentmine.cproject.args.log.CMineLog;
import org.contentmine.cproject.files.AbstractSearcher;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeFiles;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.files.ProjectFilesTree;
import org.contentmine.cproject.files.ProjectSnippetsTree;
import org.contentmine.cproject.files.RegexPathFilter;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.files.SnippetsTree;
import org.contentmine.cproject.lookup.DefaultStringDictionary;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlBr;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.html.HtmlH1;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlP;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;


/** base class for all arg processing. Also contains the workflow logic:
 * 
 * the list of CTrees is created in 
 * 
 * parseArgs(String[]) or
 * parseArgs(String)
 * 
 * calls 
 * 	protected void addArgumentOptionsAndRunParseMethods(ArgIterator argIterator, String arg) throws Exception {
 * 		which iterates through the args, loking for init* and parse* methods 
			for (ArgumentOption option : argumentOptionList) {
				if (option.matches(arg)) {
					LOG.trace("OPTION>> "+option);
					String initMethodName = option.getInitMethodName();
					if (initMethodName != null) {
						runInitMethod(option, initMethodName);
					}
					String parseMethodName = option.getParseMethodName();
					if (parseMethodName != null) {
						runParseMethod(argIterator, option, parseMethodName);
					}
					processed = true;
					chosenArgumentOptionList.add(option);
					break;
				}
			}
		}
	}
	
	this will generate CTreeList 
	after that 

 * 
 * 
 * runAndOutput() iterates through each CTree
 * 
		for (int i = 0; i < CTreeList.size(); i++) {
			currentCTree = CTreeList.get(i);
			// generateLogFile here
			currentCTree.getOrCreateLog();
			// each CTree has a ContentProcessor
			currentCTree.ensureContentProcessor(this);
			// possible initFooOption
			runInitMethodsOnChosenArgOptions();
			// possible runFooOption
			runRunMethodsOnChosenArgOptions();
			// possible outputFooOptions
			runOutputMethodsOnChosenArgOptions();
		}
		// a "reduce" or "gather" method to run over many CTrees (e.g summaries)
		runFinalMethodsOnChosenArgOptions();
		// includes the finalAnalysis (--filter) 
	}

 * NOTE: changed all *internal* CTree-type names to CTree. 2015-09-15
 * 
 * @author pm286
 *
 */
public class DefaultArgProcessor {
	
	public static final String ORG_CONTENTMINE_CPROJECT = "/org/contentmine/cproject";
	private static final Logger LOG = Logger.getLogger(DefaultArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static String ARGS_TOP = ORG_CONTENTMINE_CPROJECT + "/args";
	private static final String ARGS2HTML_XSL = ARGS_TOP+ "/args2html.xsl";
	protected static final String ARGS_XML = "args.xml";
	public static String ARGS_XML_RESOURCE = ARGS_TOP+"/"+ARGS_XML;
	public static String FILES_TOP = ORG_CONTENTMINE_CPROJECT + "/files";
	public static String SCHEMA_TOP = ORG_CONTENTMINE_CPROJECT + "/schema";
	
	private static final File MAIN_RESOURCES = new File("src/main/resources");
	public static final String MINUS = "-";
	public static final String[] DEFAULT_EXTENSIONS = {"html", "xml", "pdf"};
	public final static String H = "-h";
	public final static String HELP = "--help";
	private static final String NAME = "name";
	private static final String VERSION = "version";
	
	public static final String WHITESPACE = "\\s+";
	public static final VersionManager DEFAULT_VERSION_MANAGER = new VersionManager();
	public static final String LOGFILE = "target/log.xml";
	
	/** creates a list of tokens that are found in an allowed list.
	 * 
	 * @param allowed
	 * @param tokens
	 * @return list of allowed tokens
	 */
	protected static List<String> getChosenList(List<String> allowed, List<String> tokens) {
		List<String> chosenTokens = new ArrayList<String>();
		for (String method : tokens) {
			if (allowed.contains(method)) {
				chosenTokens.add(method);
			} else {
				LOG.error("Unknown token: "+method+"; allowed are: "+allowed);
			}
		}
		return chosenTokens;
	}

	// arg values
	protected String output;
	protected List<String> extensionList = null;
	private boolean recursive = false;
	public List<String> inputList;
	public String inputDirName;
	protected String logfileName;
	public String update;
	
	public List<ArgumentOption> argumentOptionList;
	public List<ArgumentOption> chosenArgumentOptionList;
	
	protected CTreeList cTreeList;
	// change protection later
	protected CTree currentCTree;
	protected String summaryFileName;
	protected String dfFileName;
	// variable processing
	protected Map<String, String> variableByNameMap;
	private VariableProcessor variableProcessor;
	private AbstractLogElement cTreeLog;
	private AbstractLogElement projectLog;
	private boolean unzip = false;
	List<List<String>> renamePairs;
	protected List<DefaultStringDictionary> dictionaryList;
	private String filterExpression;
	private File outputFile;
	private String outputDirName;
	protected CProject cProject;
	
	private ProjectSnippetsTree projectSnippetsTree;
	private ProjectFilesTree projectFilesTree;
	private ProjectAndTreeFactory projectAndTreeFactory;
	protected String projectDirString;
	private String includePatternString;
	private ArgumentExpander argumentExpander;
	private CTreeFiles cTreeFiles;
	protected XPathProcessor xPathProcessor;
	private Multiset<String> documentMultiset;
	private Level exceptionLevel;
	protected Pattern fileFilterPattern;
	private IOFileFilter ioFileFilter;
	protected String outputFileRegex;
	protected CHESRunner runner;
	private boolean debug;
	protected AbstractTool abstractTool;

	protected List<ArgumentOption> getArgumentOptionList() {
		return argumentOptionList;
	}

	public DefaultArgProcessor() {
		ensureDefaultLogFiles();
		readArgumentOptions(getArgsResource());
	}

	public DefaultArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public DefaultArgProcessor(Class clazz) {
		ensureDefaultLogFiles();
		readArgumentOptions(clazz, getArgsResource());
	}

	public DefaultArgProcessor(AbstractTool abstractTool) {
		this.abstractTool = abstractTool;
	}

	private void ensureDefaultLogFiles() {
		TREE_LOG();
		PROJECT_LOG();
	}

	public final static  AbstractLogElement CM_LOG = new CMineLog(new File("target/defaultLog.xml"));
	
	public AbstractLogElement PROJECT_LOG() {
		if (projectLog == null) {
			createProjectLog(new File("target/defaultProjectLog.xml"));
		}
		return projectLog;
	}

	public AbstractLogElement TREE_LOG() {
		if (cTreeLog == null) {
			createCTreeLog(new File("target/defaultCTreeLog.xml"));
		}
		return cTreeLog;
	}

	public void createCTreeLog(File logFile) {
		cTreeLog = new CMineLog(logFile);
	}

	public void createProjectLog(File logFile) {
		projectLog = new CMineLog(logFile);
	}

	protected static VersionManager getVersionManager() {
		return DEFAULT_VERSION_MANAGER;
	}
	
	
	private String getArgsResource() {
		return ARGS_XML_RESOURCE;
	}
	
	public void readArgumentOptions(Class clazz, String resourceName) {
		ensureArgumentOptionList();
		try {
//			InputStream is = clazz.getClassLoader().getResourceAsStream(resourceName);
			InputStream is = clazz.getResourceAsStream(resourceName);
			if (is == null) {
				String message = "Cannot find input resource stream: class("+clazz+"); "+resourceName;
				//LOG.debug("************************* "+message);
				throw new RuntimeException(message);
			}
			Element argListElement = new Builder().build(is).getRootElement();
			projectLog = this.getOrCreateLog(logfileName);
			getVersionManager().readNameVersion(argListElement);
			LOG.trace("VERSION "+getVersionManager());
			createArgumentOptions(argListElement);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/process args file "+resourceName, e);
		}
	}

	public void readArgumentOptions(String resourceName) {
		readArgumentOptions(DefaultArgProcessor.class, resourceName);
	}

	private void createArgumentOptions(Element argElement) {
		List<Element> elementList = XMLUtil.getQueryElements(argElement, "/*/*[local-name()='arg']");
		for (Element element : elementList) {
			ArgumentOption argOption = ArgumentOption.createOption(this.getClass(), element);
			LOG.trace("created ArgumentOption: "+argOption);
			argumentOptionList.add(argOption);
		}
	}
	
	private void ensureArgumentOptionList() {
		if (this.argumentOptionList == null) {
			this.argumentOptionList = new ArrayList<ArgumentOption>();
		}
	}

	public AbstractLogElement getOrCreateLog(String logfileName) {
		AbstractLogElement cMineLog = null;
		if (logfileName == null) {
			logfileName = DefaultArgProcessor.LOGFILE;
		}
		File file = new File(logfileName);
		cMineLog = new CMineLog(file);
		return cMineLog;
	}
	

	// ============ METHODS ===============

	public void parseVersion(ArgumentOption option, ArgIterator argIterator) {
		argIterator.createTokenListUpToNextNonDigitMinus(option);
		printVersion();
	}

	public void parseExtensions(ArgumentOption option, ArgIterator argIterator) {
		List<String> extensions = argIterator.createTokenListUpToNextNonDigitMinus(option);
		setExtensions(extensions);
	}

	/** obsolete name */
	@Deprecated
	public void parseQSNorma(ArgumentOption option, ArgIterator argIterator) {
		parseCTree(option, argIterator);
	}

	/** create a CTreeList from --ctree argument
	 */
	public void parseCTree(ArgumentOption option, ArgIterator argIterator) {
		List<String> cTreeNames = argIterator.createTokenListUpToNextNonDigitMinus(option);
		ensureProjectAndTreeFactory();
		projectAndTreeFactory.createCTreeListFrom(cTreeNames);
	}

	/** create a CTreeList from --ctree argument
	 */ 
	 @Deprecated // old name
	public void parseCMDir(ArgumentOption option, ArgIterator argIterator) {
		parseCTree(option, argIterator);
	}

	public void parseException(ArgumentOption option, ArgIterator argIterator) {
		String levelS = argIterator.getString(option).toUpperCase();
		if (levelS.equals(Level.ERROR.toString())) {
			exceptionLevel = Level.ERROR;
		} else if (levelS.equals(Level.WARN.toString())) {
			exceptionLevel = Level.WARN;
		} else if (levelS.equals(Level.INFO.toString())) {
			exceptionLevel = Level.INFO;
		} else {
			throw new RuntimeException("Bad exception level: "+levelS);
		}
	}

	public void parseFileFilter(ArgumentOption option, ArgIterator argIterator) {
		String fileFilterS = argIterator.getString(option);
		fileFilterPattern = Pattern.compile(fileFilterS);
		ioFileFilter = new RegexPathFilter(fileFilterPattern);
	}

	public void parseInput(ArgumentOption option, ArgIterator argIterator) {
		List<String> inputs = argIterator.createTokenListUpToNextNonDigitMinus(option);
		inputList = ensureArgumentExpander().expandAllWildcards(inputs);
	}

	public void parseInputDirectory(ArgumentOption option, ArgIterator argIterator) {
		inputDirName = argIterator.getString(option);
	}

	public void parseLogfile(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		logfileName = (strings.size() == 0) ? CTree.LOGFILE : strings.get(0);
		LOG.trace("log file: "+logfileName);
	}

	public void parseMakeProject(ArgumentOption option, ArgIterator argIterator) {
		String defalt = option.getDefaultString();
		outputFileRegex = argIterator.getString(option);
	}

	public void parseOutput(ArgumentOption option, ArgIterator argIterator) {
		output = argIterator.getString(option);
	}

	public void parseOutputDirectory(ArgumentOption option, ArgIterator argIterator) {
		outputDirName = argIterator.getString(option);
	}

	public void parseProject(ArgumentOption option, ArgIterator argIterator) {
		projectDirString = argIterator.getString(option);
		ensureProjectAndTreeFactory().createProject();
		ensureProjectAndTreeFactory().createCTreeListFromProject();
	}

	public void parseProjectMenu(ArgumentOption option, ArgIterator argIterator) {
		fileFilterPattern = Pattern.compile(argIterator.getString(option));
	}

	public void parseRecursive(ArgumentOption option, ArgIterator argIterator) {
		recursive = argIterator.getBoolean(option);
	}

	public void parseSummaryFile(ArgumentOption option, ArgIterator argIterator) {
		summaryFileName = argIterator.getString(option);
	}

	public void parseDFFile(ArgumentOption option, ArgIterator argIterator) {
		dfFileName = argIterator.getString(option);
	}

	public void parseUnzip(ArgumentOption option, ArgIterator argIterator) {
		setUnzip(true);
	}

	public void parseInclude(ArgumentOption option, ArgIterator argIterator) {
		setIncludePatternString(option, argIterator);
	}

	public void parseRename(ArgumentOption option, ArgIterator argIterator) {
		setRenamePairs(option, argIterator);
	}
	
	public void parseAnalysis(ArgumentOption option, ArgIterator argIterator) {
		LOG.warn("DEPRECATED: use --filter");
		List<String> analyzeStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		setFilter(analyzeStrings);
	}

	public void parseFilter(ArgumentOption option, ArgIterator argIterator) {
		List<String> filterStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		setFilter(filterStrings);
	}

	public void runMakeDocs(ArgumentOption option) {
		transformArgs2html();
	}

	public void runAnalysis(ArgumentOption option) {
		LOG.warn("DEPRECATED: use --filter");
		filterCTree();
	}

	public void runFilter(ArgumentOption option) {
		filterCTree();
	}

	public void runProjectMenu(ArgumentOption option) {
		createProjectMenu();
	}

	public void runSummaryFile(ArgumentOption option) {
		runSummaryModule();
	}

	public void runDFFile(ArgumentOption option) {
//		no-op
//		runDFModule();
	}

	public void runTest(ArgumentOption option) {
		String name = new Object(){}.getClass().getEnclosingMethod().getName();
		cTreeLog.info("testing");
	}

	public void outputMethod(ArgumentOption option) {
		//LOG.warn("output method not written");
	}

	public void outputAnalysis(ArgumentOption option) {
		LOG.warn("DEPRECATED: use --filter");
		outputFilterRoutine();
	}

	public void outputFilter(ArgumentOption option) {
		outputFilterRoutine();
	}

	// ============= FINAL =============
	

	public void finalAnalysis(ArgumentOption option) {
		LOG.warn("DEPRECATED: use --filter");
		finalFilterRoutine();
	}

	public void finalDependency(ArgumentOption option) {
		finalDependency();
	}

	public void finalDFFile(ArgumentOption option) {
		finalDFRoutine();
	}

	public void finalFilter(ArgumentOption option) {
		finalFilterRoutine();
	}

	public void finalMakeProject(ArgumentOption option) {
		finalMakeProject();
	}

	public void finalSummaryFile(ArgumentOption option) {
		finalFilterRoutine();
	}

	// =====================================

	private void outputFilterRoutine() {
		String output = getOutput();
		if (currentCTree != null) {
			outputFile = output == null ? null : new File(currentCTree.getDirectory(), output);
			LOG.trace("DBG outputFile "+outputFile.getParentFile().getName()+"/"+FilenameUtils.getName(outputFile.toString()));
			if (currentCTree.getSnippetsTree() != null) {
				outputSnippetsTree(outputFile);
			} else if (currentCTree.getCTreeFiles() != null) {
				outputCTreeFiles(outputFile);
			} else {
				LOG.warn("Analysis: No snippets or files to output");
			}
		}
	}

	private void finalFilterRoutine() {
		if (cProject == null) {
			LOG.warn("no project to analyze");
			return;
		}
		File directory = cProject.getDirectory();
		if (directory == null) {
			LOG.warn("no directory to analyze");
			return;
		}
		if (filterExpression != null && output != null) {
			outputFilterSnippets(cProject.getDirectory());
		} else if (summaryFileName != null) {
			outputFilterCounts();
		}
	}

	private void finalDependency() {
		if (inputDirName == null) {
			LOG.error("dependencyGraph requires --inputDir");
		} else {
			LOG.debug("dependencies in: "+inputDirName);
			LOG.error("NYI");
		}
	}
	
	private void finalDFRoutine() {
		if (dfFileName != null) {
			outputDFCounts();
		}
	}
	
	private void finalMakeProject() {
		if (fileFilterPattern == null) {
			throw new RuntimeException("must have --fileFilter for makeProject");
			// make default fileFilterPattern
		}
		getCProject();
		if (cProject == null) {
			throw new RuntimeException("missing --project");
		}
		moveFilesIntoMatchedFieldPattern(cProject.getDirectory(), fileFilterPattern, outputFileRegex);		
	}

	private void outputDFCounts() {
		ResultsElement resultsElement = ResultsElement.getResultsElementSortedByCount(documentMultiset);
		resultsElement.setTitle(ResultsElement.DOCUMENTS);
		writeResultsToSummaryFile(resultsElement, new File(cProject.getDirectory(), dfFileName));
	}

	private void outputFilterCounts() {
		Multiset<String> stringSet = cProject.getMultiset();
		ResultsElement resultsElement = ResultsElement.getResultsElementSortedByCount(stringSet);
		resultsElement.setTitle(ResultsElement.FREQUENCIES);
		writeResultsToSummaryFile(resultsElement, new File(cProject.getDirectory(), summaryFileName));
	}

	private void outputFilterSnippets(File directory) {
		File outputFile = new File(directory, output);
		LOG.trace("filterSnippets "+outputFile);
		ProjectSnippetsTree projectSnippetsTree = cProject.getProjectSnippetsTree();
		ProjectFilesTree projectFilesTree = cProject.getProjectFilesTree();
		if (projectSnippetsTree != null) {
			cProject.outputProjectSnippetsTree(outputFile);
		} else if (projectFilesTree != null) {
			cProject.outputProjectFilesTree(outputFile);
		}
	}
	
	private void createProjectMenu() {
		File pDirectory = cProject.getDirectory();
		List<File> files = new RegexPathFilter(fileFilterPattern).listNonDirectoriesRecursively(pDirectory);
		HtmlHtml html = new HtmlHtml();
		HtmlH1 h1 = new HtmlH1();
		html.appendChild(h1);
		h1.appendChild(pDirectory.toString());
		HtmlBody body = html.getOrCreateBody();
		for (File file : files) {
//			String relative = Util.getRelativeFilename(file, pDirectory, null);
			String relative = Util.getRelativeFilename(pDirectory, file, null);
			HtmlA a = new HtmlA();
			a.setHref(relative);
			a.setContent(relative);
			body.appendChild(a);
			body.appendChild(new HtmlBr());
		}
		File outFile = new File(pDirectory, output);
		try {
			XMLUtil.debug(html, outFile, 1);
		} catch (IOException e) {
			throw new RuntimeException("cannot write menu", e);
		}
	}


	
	private void runSummaryModule() {
		ensureDocumentMutiset();
		LOG.trace("RUN SUMMARY; input: "+inputList);
		
		if (xPathProcessor == null) {
//			throw new RuntimeException("Must give xpath");
		} 
		if (inputList == null ||inputList.size() == 0) {
			LOG.warn("No input specified");
			return;
		}
		if (currentCTree == null) {
			throw new RuntimeException("Null currentTree");
		}
		ResultsElement summaryResultsElement = createAggregatedSortedResultsCount();
		if (summaryFileName != null) {
			File file = new File(currentCTree.getDirectory(), summaryFileName);
			LOG.trace("file: "+file);
			writeResultsToSummaryFile(summaryResultsElement, file);
			List<ResultElement> resultsElementList = summaryResultsElement.getOrCreateResultElementList();
			for (ResultElement resultElement : resultsElementList) {
				documentMultiset.add(resultElement.getValue());
			}
		}
		cProject.addSummaryResultsElement(summaryResultsElement);
	}

	private void ensureDocumentMutiset() {
		if (documentMultiset == null) {
			documentMultiset = HashMultiset.create();
		}
	}

	private void writeResultsToSummaryFile(ResultsElement resultsElement, File summaryFile) {
		try {
			XMLUtil.debug(resultsElement, summaryFile, 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write summary: ", e);
		}
	}

	private ResultsElement createAggregatedSortedResultsCount() {
		Multiset<String> matchSet = createMultisetFromInputList();
		ResultsElement resultsElement = matchSet == null ? null : ResultsElement.getResultsElementSortedByCount(matchSet);
		return resultsElement;
	}


	private Multiset<String> createMultisetFromInputList() {
		String xpath = xPathProcessor.getXPath();
		Multiset<String> matchSet = HashMultiset.create();
		File directory = currentCTree.getDirectory();
		if (directory == null) {
			throw new RuntimeException("No current directory in CTree");
		}
		for (String input : inputList) {
			File f = new File(directory, input);
			if (f != null && f.exists()) {
				Document document = XMLUtil.parseQuietlyToDocument(f);
				List<Node> resultNodes = XMLUtil.getQueryNodes(document, xpath);
				for (Node node : resultNodes) {
					String value = node.getValue();
					int count = getCountValue(node);
					matchSet.add(value, count);
				}
			}
		}
		return matchSet;
	}

	private int getCountValue(Node node) {
		int count = 1;
		if (node instanceof Attribute) {
			Node parent = node.getParent();
			if (parent != null && parent instanceof Element) {
				String countValue = ((Element)parent).getAttributeValue("count");
				if (countValue != null) {
					count = Integer.parseInt(countValue);
				}
			}
		}
		return count;
	}

	private void outputSnippetsTree(File outputFile) {
		SnippetsTree snippetsTree = currentCTree.getSnippetsTree();
		if (snippetsTree != null && outputFile != null) {
			try {
				XMLUtil.debug(snippetsTree, outputFile, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot output snippets", e);
			}
		}
	}

	private void outputCTreeFiles(File outputFile) {
		CTreeFiles cTreeFiles = currentCTree.getCTreeFiles();
		if (cTreeFiles != null && outputFile != null) {
			try {
				XMLUtil.debug(cTreeFiles, outputFile, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot output cTreeFiles", e);
			}
		}
	}

	// =====================================


	public void printHelp(ArgumentOption option, ArgIterator argIterator) {
		printHelp();
	}
	
	private ProjectAndTreeFactory ensureProjectAndTreeFactory() {
		if (projectAndTreeFactory == null) {
			projectAndTreeFactory = new ProjectAndTreeFactory(this);
		}
		return projectAndTreeFactory;
	}
	
	private void setIncludePatternString(ArgumentOption option, ArgIterator argIterator) {
		List<String> includeStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		includePatternString = includeStrings != null && includeStrings.size() == 1 ? includeStrings.get(0) : null;
	}

	private void setFilter(List<String> filterStrings) {
		if (filterStrings != null && filterStrings.size() == 1) {
			filterExpression = filterStrings.get(0);
		} else {
			LOG.error("--filter requires 1 expression");
		}
	}

	private void setRenamePairs(ArgumentOption option, ArgIterator argIterator) {
		List<String> renameStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (renameStrings != null && renameStrings.size() %2 == 0) {
			renamePairs = new ArrayList<List<String>>();
			for (int i = 0; i < renameStrings.size(); i += 2) {
				List<String> stringPair = new ArrayList<String>();
				stringPair.add(renameStrings.get(i));
				stringPair.add(renameStrings.get(i+1));
				renamePairs.add(stringPair);
			}
		} else {
			LOG.warn("--rename requires an even number of arguments");
		}
	}

	/** this called once per CTree and write the output.
	 * 
	 */
	private void filterCTree() {
		FileXPathSearcher fileXPathSearcher = new FileXPathSearcher(filterExpression);
		String xpath = fileXPathSearcher.getCurrentXPath();
		LOG.trace("DBG filterCTree "+filterExpression);
		if (currentCTree != null) {
			fileXPathSearcher = new FileXPathSearcher(currentCTree, filterExpression);
			fileXPathSearcher.search();
			CTreeFiles cTreeFiles = fileXPathSearcher.getCTreeFiles();
			if (cProject != null) {
				cProject.add(cTreeFiles);
			}
			if (xpath != null) {
				SnippetsTree snippetsTree = fileXPathSearcher.getSnippetsTree();
				if (cProject != null) {
					cProject.add(snippetsTree);
				}
			}
		}
	}


	protected void printVersion() {
		DefaultArgProcessor.getVersionManager().printVersion();
	}


	private void transformArgs2html() {
		InputStream transformStream = this.getClass().getResourceAsStream(ARGS2HTML_XSL);
		if (transformStream == null) {
			throw new RuntimeException("Cannot find argsXml2Html file");
		}
		List<File> argsList = getArgs2HtmlList();
		for (File argsXmlFile : argsList) {
			File argsHtmlFile = getHtmlFromXML(argsXmlFile);
			if (argsHtmlFile == null) {
				LOG.error("Cannot create HTML file "+argsHtmlFile+"; skipping");
				continue;
			}
			try {
				
				FileInputStream argsXmlIs = new FileInputStream(argsXmlFile);
				String xmlString = transformArgsXml2Html(argsXmlIs, transformStream);
				if (xmlString == null) {
					LOG.error("Cannot transform "+argsXmlFile+"; skipping");
					continue;
				}
				FileUtils.writeStringToFile(argsHtmlFile, xmlString, Charset.forName("UTF-8"));
			} catch (Exception e) {
				LOG.debug("Cannot transform to HTML "+argsXmlFile+"; "+e.getMessage());
			}
		}
		try {
			transformStream.close();
		} catch (IOException e) {
			LOG.debug("cannot close stream");
		}
	}

	private String transformArgsXml2Html(InputStream argsXmlIs, InputStream argsXml2HtmlXslIs) throws Exception {
		TransformerFactory tfactory = TransformerFactory.newInstance();
	    StreamSource source = new StreamSource(argsXml2HtmlXslIs);
	    if (source == null) {
	    	LOG.debug("null transformer source");
	    	return null;
	    }
		Transformer javaxTransformer = tfactory.newTransformer(source);
		OutputStream baos = new ByteArrayOutputStream();
		javaxTransformer.transform(new StreamSource(argsXmlIs),  new StreamResult(baos));
		return baos.toString();
	}

	private File getHtmlFromXML(File argsXml) {
		String xmlPath = argsXml.getPath();
		String htmlPath = xmlPath.replaceAll("\\.xml", ".html");
		return new File(htmlPath);
	}

	private List<File> getArgs2HtmlList() {
		List<File> argsList = new ArrayList<File>(FileUtils.listFiles(MAIN_RESOURCES, new String[]{"xml"}, true));
		for (int i = argsList.size() - 1; i >= 0; i--) {
			File file = argsList.get(i);
			if (!(ARGS_XML.equals(file.getName()))) {
				argsList.remove(i);
			}
		}
		return argsList;
	}

	// =====================================
	public void setExtensions(List<String> extensions) {
		this.extensionList = extensions;
	}

	public void setInputPatternString(String includePatternString) {
		this.includePatternString = includePatternString;
	}

	public List<String> getInputList() {
		ensureInputList();
		return inputList;
	}

	public IOFileFilter getIOFileFilter() {
		return ioFileFilter;
	}

	public String getInputDirName() {
		return inputDirName;
	}

	public String getSingleInputName() {
		ensureInputList();
		return (inputList.size() != 1) ? null : inputList.get(0);
	}
	
	void ensureInputList() {
		if (inputList == null) {
			inputList = new ArrayList<String>();
		}
	}

	public String getOutput() {
		return output;
	}

	public String getOutputDirName() {
		return outputDirName;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public String getSummaryFileName() {
		return summaryFileName;
	}

	public CTreeList getCTreeList() {
		ensureCTreeList();
		return cTreeList;
	}

	protected void ensureCTreeList() {
		if (cTreeList == null) {
			cTreeList = new CTreeList();
		}
	}
	
	// --------------------------------
	
	public void parseArgs(String[] commandLineArgs) {
		if (commandLineArgs == null || commandLineArgs.length == 0) {
			printHelp();
		} else {
			String[] totalArgs = addDefaultsAndParsedArgs(commandLineArgs);
			ArgIterator argIterator = new ArgIterator(totalArgs);
			LOG.trace("args with defaults is: "+new ArrayList<String>(Arrays.asList(totalArgs)));
			while (argIterator.hasNext()) {
				String arg = argIterator.next();
				LOG.trace("arg> "+arg);
				try {
					addArgumentOptionsAndRunParseMethods(argIterator, arg);
				} catch (Exception e) {
					throw new RuntimeException("cannot process argument: "+arg+" ("+ExceptionUtils.getRootCauseMessage(e)+")", e);
				}
			}
			finalizeArgs();
		}
	}
	
	public void parseArgs(String args) {
		String[] args1 = args.trim().split("\\s+");
		parseArgs(args1);
	}

	private void finalizeArgs() {
		processArgumentDependencies();
		finalizeInputList();
	}

	private void processArgumentDependencies() {
		for (ArgumentOption argumentOption : chosenArgumentOptionList) {
			argumentOption.processDependencies(chosenArgumentOptionList);
		}
	}

	void finalizeInputList() {
		List<String> inputList0 = new ArrayList<String>();
		ensureInputList();
		for (String input : inputList) {
			File file = new File(input);
			if (file.isDirectory()) {
				LOG.trace("DIR: "+file.getAbsolutePath()+"; isDir "+file.isDirectory()+"; Exist "+file.exists()+"; "+file.getAbsolutePath());
				addDirectoryFiles(inputList0, file);
			} else {
				inputList0.add(input);
			}
		}
		inputList = inputList0;
		Collections.sort(inputList);
		LOG.trace("sorted input: "+inputList);
	}

	/** will return a sorted list
	 * 
	 * @param inputList0
	 * @param file
	 */
	private void addDirectoryFiles(List<String> inputList0, File file) {
		String[] extensions = getExtensions().toArray(new String[0]);
		List<File> files = new ArrayList<File>(
				FileUtils.listFiles(file, extensions, recursive));
		for (File file0 : files) {
			inputList0.add(file0.toString());
		}
		Collections.sort(inputList0);
		LOG.trace("sort: "+inputList0);
	}

	private String[] addDefaultsAndParsedArgs(String[] commandLineArgs) {
		List<String> totalArgList = new ArrayList<String>(Arrays.asList(createDefaultArgumentStrings()));
		List<String> commandArgList = Arrays.asList(commandLineArgs);
		totalArgList.addAll(commandArgList);
		String[] totalArgs = totalArgList.toArray(new String[0]);
		return totalArgs;
	}

	private String[] createDefaultArgumentStrings() {
		StringBuilder sb = new StringBuilder();
		for (ArgumentOption option : argumentOptionList) {
			String defalt = String.valueOf(option.getDefault());
			LOG.trace("default: "+defalt);
			if (defalt != null && defalt.toString().trim().length() > 0) {
				String command = getBriefOrVerboseCommand(option);
				sb.append(command+" "+option.getDefault()+" ");
			}
		}
		String s = sb.toString().trim();
		return s.length() == 0 ? new String[0] : s.split("\\s+");
	}

	private String getBriefOrVerboseCommand(ArgumentOption option) {
		String command = option.getBrief();
		if (command == null || command.trim().length() == 0) {
			command = option.getVerbose();
		}
		return command;
	}

	public List<String> getExtensions() {
		ensureExtensionList();
		return extensionList;
	}

	private void ensureExtensionList() {
		if (extensionList == null) {
			extensionList = new ArrayList<String>();
		}
	}
	
	public void setUnzip(boolean b) {
		this.unzip  = b;
	}

	public void runInitMethodsOnChosenArgOptions() {
		runMethodsOfType(ArgumentOption.INIT_METHOD);
	}
	
	public void runRunMethodsOnChosenArgOptions() {
		LOG.trace("run:");
		runMethodsOfType(ArgumentOption.RUN_METHOD);
	}

	public void runOutputMethodsOnChosenArgOptions() {
		LOG.trace("output: ");
		runMethodsOfType(ArgumentOption.OUTPUT_METHOD);
	}

	public void runFinalMethodsOnChosenArgOptions() {
		runMethodsOfType(ArgumentOption.FINAL_METHOD);
	}

	protected void runMethodsOfType(String methodNameType) {
		List<ArgumentOption> optionList = getOptionsWithMethod(methodNameType);
		for (ArgumentOption option : optionList) {
			LOG.trace("option "+option+" "+this.getClass());
			String methodName = null;
			try {
				methodName = option.getMethodName(methodNameType);
				if (methodName != null) {
//					AbstractTool.debug(abstractTool, 1, "method: "+methodName, LOG);
 					instantiateAndRunMethod(option, methodName);
				}
			} catch (IllegalArgumentException ee) {
				throw ee;
			} catch (Exception e) {
				LOG.debug(" exception in option: "+option.toString());
//				e.printStackTrace();
				throw new RuntimeException("cannot run ["+methodName+"] in "+option.getVerbose()+
						" ("+ExceptionUtils.getRootCauseMessage(e)+")", e);
			}
		}
	}

	private List<ArgumentOption> getOptionsWithMethod(String methodName) {
		List<ArgumentOption> optionList0 = new ArrayList<ArgumentOption>();
		for (ArgumentOption option : chosenArgumentOptionList) {
			LOG.trace("run "+option.getRunMethodName());
			if (option.getMethodName(methodName) != null) {
				LOG.trace("added run "+option.getRunMethodName());
				optionList0.add(option);
			}
		}
		return optionList0;
	}


	protected void addArgumentOptionsAndRunParseMethods(ArgIterator argIterator, String arg) throws Exception {
		ensureChosenArgumentList();
		boolean processed = false;
		if (!arg.startsWith(MINUS)) {
			LOG.error("Parsing failed at: ("+arg+"), expected \"-\" trying to recover");
		} else {
			for (ArgumentOption option : argumentOptionList) {
				if (option.matches(arg)) {
					LOG.trace("OPTION>> "+option);
					String initMethodName = option.getInitMethodName();
					if (initMethodName != null) {
						runInitMethod1(option, initMethodName);
					}
					String parseMethodName = option.getParseMethodName();
					if (parseMethodName != null) {
						runParseMethod1(argIterator, option, parseMethodName);
					}
					processed = true;
					chosenArgumentOptionList.add(option);
					break;
				}
			}
			if (!processed) {
				LOG.error("Unknown arg: ("+arg+"), trying to recover");
			}
		}
	}

	private void runInitMethod1(ArgumentOption option, String initMethodName) {
		runMethod(null, option, initMethodName);
	}

	private void runParseMethod1(ArgIterator argIterator, ArgumentOption option, String parseMethodName) {
		runMethod(argIterator, option, parseMethodName);
	}

	private void runMethod(ArgIterator argIterator, ArgumentOption option, String methodName) {
		Method method;
		try {
			if (argIterator == null) {
				method = this.getClass().getMethod(methodName, option.getClass());
			} else {
				method = this.getClass().getMethod(methodName, option.getClass(), argIterator.getClass());
			}
		} catch (NoSuchMethodException e) {
			debugMethods();
			throw new RuntimeException("Cannot find: "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";", e);
		}
		method.setAccessible(true);
		try {
			if (argIterator == null) {
					method.invoke(this, option);
			} else {
				method.invoke(this, option, argIterator);
			}
		} catch (Exception e) {
			LOG.trace("failed to run "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";"+e.getCause());
			throw new RuntimeException("Cannot run: "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";", e);
		}
	}

	private void debugMethods() {
		LOG.debug("methods for "+this.getClass());
		for (Method meth : this.getClass().getDeclaredMethods()) {
			LOG.debug(meth.toString());
		}
	}

	private void instantiateAndRunMethod(ArgumentOption option, String methodName)
			throws IllegalAccessException, InvocationTargetException {
		if (methodName != null) {
			Method method = null;
			try {
				method = this.getClass().getMethod(methodName, option.getClass()); 
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException(methodName+"; "+this.getClass()+"; "+option.getClass()+"; \nmethod not implemented: ", nsme);
			}
			try {
				LOG.trace("running meth/opt: "+method.getName() + " / "+option);
				method.setAccessible(true);
 				method.invoke(this, option);
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (Exception ee) {
				ee.printStackTrace();
				throw new RuntimeException("invoke "+methodName+" fails", ee);
			}
		}
	}

	private void ensureChosenArgumentList() {
		if (chosenArgumentOptionList == null) {
			chosenArgumentOptionList = new ArrayList<ArgumentOption>();
		}
	}

	protected void printHelp() {
		for (ArgumentOption option : argumentOptionList) {
			System.err.println(option.getHelp());
		}
	}
	
	public List<ArgumentOption> getChosenArgumentList() {
		ensureChosenArgumentList();
		return chosenArgumentOptionList;
	}
	
	public String createDebugString() {
		StringBuilder sb = new StringBuilder();
		getChosenArgumentList();
		for (ArgumentOption argumentOption : chosenArgumentOptionList) {
			sb.append(argumentOption.toString()+"\n");
		}
		return sb.toString();
	}

	/** MAIN CONTROL LOOP
	 * 
	 */
	public void runAndOutput() {
		LOG.trace("MAIN LOOP "+cTreeList);
		ensureCTreeList();
		if (cTreeList.size() == 0) {
			CProject cProject = null;
			if (runner != null) {
				cProject = runner.getCProject();
			}
			if (projectDirString == null) {
				File cProjectDirectory = cProject == null ? null : cProject.getDirectory();
				projectDirString = cProjectDirectory == null ? null : cProjectDirectory.toString();
			}
			if (output == null && projectDirString != null) {
				output = projectDirString;
			}
		
			if (output == null) {
				LOG.warn("no --output given");
			} else {
			}
			LOG.trace("treating as CTree creation under project "+projectDirString);
			runRunMethodsOnChosenArgOptions();
		} else {
			for (int i = 0; i < cTreeList.size(); i++) {
				currentCTree = cTreeList.get(i);
				if (debug) System.err.print(currentCTree.getName()+" "); 
				LOG.trace("running: "+currentCTree.getDirectory());
				cTreeLog = currentCTree.getOrCreateCTreeLog(this, logfileName);
				if (cTreeLog != null) cTreeLog.setLevel(LogLevel.ERROR);
				LOG.trace("TEST LOG "+this.hashCode());
				currentCTree.ensureContentProcessor(this);
				try {
					runInitMethodsOnChosenArgOptions();
					runRunMethodsOnChosenArgOptions();
					runOutputMethodsOnChosenArgOptions();
					if (cTreeLog != null) {
						cTreeLog.writeLog();
					}
				} catch (IllegalArgumentException e) {
					throw e;
				} catch (Exception e) {
					projectLog.error("error in running, terminated: "+e);
					e.printStackTrace();
					LOG.error("ERR! "+e);
					continue;
				}
				if (i % 10 == 0) System.out.print(".");
			}
			
		}
		runFinalMethodsOnChosenArgOptions();
		writeLog();
	}


	private void writeLog() {
		if (projectLog != null) {
			projectLog.writeLog();
		}
	}
	
	public Level getExceptionLevel() {
		return exceptionLevel;
	}

	protected void addVariableAndExpandReferences(String name, String value) {
		ensureVariableProcessor();
		try {
			variableProcessor.addVariableAndExpandReferences(name, value);
		} catch (Exception e) {
			LOG.error("add variable {"+name+", "+value+"} failed");
		}
	}

	public ArgumentExpander ensureArgumentExpander() {
		if (this.argumentExpander == null) {
			argumentExpander = new ArgumentExpander(this);
		}
		return argumentExpander;
	}

	public VariableProcessor ensureVariableProcessor() {
		if (variableProcessor == null) {
			variableProcessor = new VariableProcessor();
		}
		return variableProcessor;
	}

	public String getUpdate() {
		return update;
	}

	public List<DefaultStringDictionary> getDictionaryList() {
		if (dictionaryList == null) {
			dictionaryList = new ArrayList<DefaultStringDictionary>();
		}
		return dictionaryList;
	}

	public List<? extends Element> extractPSectionElements(CTree cTree) {
		List<? extends Element> elements = null;
		if (cTree != null) {
			HtmlElement htmlElement = cTree.ensureScholarlyHtmlElement();
			elements = HtmlP.extractSelfAndDescendantPs(htmlElement);
		}
		return elements;
	}

	/** gets the HtmlElement for ScholarlyHtml.
	 * 
	 * 
	 * @return
	 */
	public static HtmlElement getScholarlyHtmlElement(CTree cTree) {
		HtmlElement htmlElement = null;
		if (cTree != null && cTree.hasScholarlyHTML()) {
			File scholarlyHtmlFile = cTree.getExistingScholarlyHTML();
			try {
				Element xml = XMLUtil.parseQuietlyToDocument(scholarlyHtmlFile).getRootElement();
				htmlElement = new HtmlFactory().parse(scholarlyHtmlFile);
			} catch (Exception e) {
				LOG.error("Cannot create scholarlyHtmlElement: "+e);
			}
		}
		return htmlElement;
	}

	// HORRIBLE, REFACTOR
	public List<? extends AbstractSearcher> getSearcherList() {
		throw new RuntimeException("Can Only be used by sublassses of DefaultArgProcessor ");
	}

	public CTree getCTree() {
		return currentCTree != null ? currentCTree : 
			(cTreeList != null && cTreeList.size() == 1) ? cTreeList.get(0) : null;
	}
	
	public void setCTree(CTree cTree) {
		this.currentCTree = cTree;
	}
	
	public CProject getCProject() {
		if (cProject == null) {
			cProject = runner == null ? null : runner.getCProject();
		}
		return cProject;
	}

	public ProjectSnippetsTree getProjectSnippetsTree() {
		projectSnippetsTree = cProject == null ? null : cProject.getProjectSnippetsTree();
		return projectSnippetsTree;
	}

	public ProjectFilesTree getProjectFilesTree() {
		projectFilesTree = cProject == null ? null : cProject.getProjectFilesTree();
		return projectFilesTree;
	}

	public String getProjectDirString() {
		return projectDirString;
	}

	public String getIncludePatternString() {
		return includePatternString;
	}

	public void expandWildcardsExhaustively() {
		ensureArgumentExpander().expandWildcardsExhaustively();
	}

	public void parseXpath(ArgumentOption option, ArgIterator argIterator) {
			List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
			if (tokens.size() == 0) {
	//			LOG.debug(XPATH_OPTION).getHelp());
			} else if (tokens.size() > 1) {
				LOG.warn("Exactly one xpath required");
			} else {
				xPathProcessor = new XPathProcessor(tokens.get(0));
			}
		}

	/**
	 * renames numbered files (I think
	 * 
	 * @param directory
	 * @param inputFileFilterPattern
	 * @param outputFileRegex
	 */
	
	public static void moveFilesIntoMatchedFieldPattern(File directory, Pattern inputFileFilterPattern, String outputFileRegex) {
		// melts down to "(\d+)" after de-escaping!
		// captures the capture group numbers!
		Pattern bracketedCaptureGroupNumberPattern = Pattern.compile("\\(\\\\\\d+\\)"); 
		List<File> files = new RegexPathFilter(inputFileFilterPattern).listNonDirectoriesRecursively(directory);
//		LOG.debug("files "+directory+"; "+files);
		for (File file : files) {
			String inputPath;
			try {
				inputPath = file.getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException("cannot canonicalize "+file, e);
			}
			Matcher inputFileFilterMatcher = inputFileFilterPattern.matcher(inputPath);
			if (!inputFileFilterMatcher.matches()) {
				throw new RuntimeException("BUG: "+inputFileFilterPattern+ "  should match "+inputPath);
			}
//			String newFileName = replaceMatchingGroups(matchField, inputPath, inputMatcher, fileFilterPattern, outputFileRegex);
			String newFileName = replaceMatchingGroups(bracketedCaptureGroupNumberPattern, inputFileFilterMatcher, outputFileRegex);
			File newFile = new File(directory, newFileName);
			LOG.trace(file+" ==> "+newFile);
			if (!newFile.exists()) {
				try {
					FileUtils.moveFile(file, newFile);
				} catch (IOException e) {
					throw new RuntimeException("cannot moveFile "+file, e);
				}
			} else {
//				LOG.debug("skipped: "+file);
			}
		}
	}

	/**
	 * 
	 * @param bracketedCaptureGroupNumberPattern searches the outputFileRegex for the brackets
	 * @param inputMatcher already matched groups in input pattern
	 * @param outputFileRegex regex for injecting matched parts of names into output file
	 * @return
	 */
	private static String replaceMatchingGroups(Pattern bracketedCaptureGroupNumberPattern, Matcher inputMatcher, String outputFileRegex) {
		int idx = 0;
		StringBuilder sb = new StringBuilder();
		Matcher outputFileRegexMatcher = bracketedCaptureGroupNumberPattern.matcher(outputFileRegex);
		while (outputFileRegexMatcher.find(idx)) {
			int start = outputFileRegexMatcher.start();
			String chunk = outputFileRegex.substring(idx, start);
			LOG.trace("chunk "+chunk);
			sb.append(chunk);
			int end = outputFileRegexMatcher.end();
			String groupS = outputFileRegex.substring(start + 2, end - 1);
			LOG.trace("group "+groupS);
			Integer group = Integer.parseInt(groupS);
			if (group > inputMatcher.groupCount()) {
//				throw new RuntimeException("bad group match; cannot find "+groupS+" in "+inputPath);
				throw new RuntimeException("bad group match; cannot find "+groupS);
			}
			sb.append(inputMatcher.group(group));
			idx = end;
		}
		sb.append(outputFileRegex.substring(idx));
		return sb.toString();
	}

//	private static String replaceMatchingGroups(Pattern matchField, String inputPath, Matcher inputMatcher, Pattern fileFilterPattern, String template) {
//		int idx = 0;
//		StringBuilder sb = new StringBuilder();
//		Matcher fieldMatcher = matchField.matcher(template);
//		LOG.trace(inputPath+"; "+fileFilterPattern+"; "+template);
//		while (fieldMatcher.find(idx)) {
//			int start = fieldMatcher.start();
//			String chunk = template.substring(idx, start);
//			LOG.trace("chunk "+chunk);
//			sb.append(chunk);
//			int end = fieldMatcher.end();
//			String groupS = template.substring(start + 2, end - 1);
//			LOG.trace("group "+groupS);
//			Integer group = Integer.parseInt(groupS);
//			if (group > inputMatcher.groupCount()) {
//				throw new RuntimeException("bad group match; cannot find "+groupS+" in "+inputPath);
//			}
//			sb.append(inputMatcher.group(group));
//			idx = end;
//		}
//		sb.append(template.substring(idx));
//		return sb.toString();
//	}

	/** messy - refactor - creates directory.
	 * 
	 * @return
	 */
	protected File getOrCreateOutputDirectory() {
		File outputDir = null;
		if (output != null) {
			outputDir = new File(output);
			if (outputDir.exists()) {
				if (!outputDir.isDirectory()) {
					throw new RuntimeException("cTreeRoot "+outputDir+" must be a directory");
				}
			} else {
				outputDir.mkdirs();
			}
		}
		return outputDir;
	}

	public void setRunner(CHESRunner runner) {
		this.runner = runner;
	}

	public CHESRunner getRunner() {
		return this.runner;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public AbstractTool getAbstractTool() {
		return abstractTool;
	}

	public void setAbstractTool(AbstractTool abstractTool) {
		this.abstractTool = abstractTool;
	}

	public int getVerbosityInt() {
		return (getAbstractTool() == null) ? 0 : getAbstractTool().getVerbosityInt();
	}

}
