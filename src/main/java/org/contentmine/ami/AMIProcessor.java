package org.contentmine.ami;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.EntityAnalyzer;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.DataTablesTool;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.euclid.util.CMStringUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.Norma;

import nu.xom.Element;

/** runs operations on CProject , mainly transformations. 
 * Might evolve to a set of default operations as an alternative to commandline
 * 
 * probably obsolete
 * 
 * @author pm286
 *
 */
public class AMIProcessor {

	private static final String PROGRAM2 = "program";
	private static final String PROGRAMS = "programs";
	private static final String CONFIGURATION = "configuration";
	private static final String ARTIFACT_ID = "artifactId";
	private static final String PLUGIN = "plugin";
	private static final String LOCAL_NAME_BR = "local-name()";
	public static final Logger LOG = LogManager.getLogger(AMIProcessor.class);
// move to AMISearch...
	public static final String SEARCH = "search";
	public static final String HELP = "help";
	private static final String MAIN_CLASS = "mainClass";
	private static final String ID = "id";
	private static final String DESCRIPTION = "description:";

	private CProject cProject;
	private Level debugLevel;

	private AMIProcessor() {
		
	}
	
	private static AMIProcessor createProcessor(CProject cProject) {
		AMIProcessor amiProcessor = null;
		if (cProject != null) {
			amiProcessor = new AMIProcessor();
			amiProcessor.cProject = cProject;
		}
		return amiProcessor;
	}
	
	/** creates project from name and user.dir
	 * 
	 * @param projectName
	 * @return
	 */
	public static AMIProcessor createProcessor(String projectName) {
		
		File userDir = new File(System.getProperty("user.dir"));
		LOG.debug("project name: "+projectName+" "+userDir);
		File projectDir = new File(userDir, projectName);
		return createProcessorFromCProjectDir(projectDir);
	}

	public static AMIProcessor createProcessorFromCProjectDir(File projectDir) {
		if (!projectDir.exists() || !projectDir.isDirectory()) {
			System.err.println("Project does not exist or is not directory:");
			System.err.println("    "+projectDir);
		}
		return createProcessor(projectDir);
	}

	public static AMIProcessor createProcessor(File projectDir) {
		AMIProcessor amiProcessor = new AMIProcessor();
		amiProcessor.cProject = new CProject(projectDir);
		return amiProcessor;
	}
	
	public void defaultAnalyzeCooccurrence(List<String> facets) {
		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(cProject.getDirectory());
		entityAnalyzer.defaultAnalyzeCooccurrence(facets);
	}
	
	public void runSearches(List<String> facetList) {
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt";
		cmd = addSearches(facetList, cmd);
		try {
			
			String argString = /*cProject.getDirectory()+" "+*/cmd;
			CommandProcessor commandProcessor = new CommandProcessor(cProject.getDirectory());
			commandProcessor.processCommands(argString);
			commandProcessor.createDataTables();

		} catch (IOException e) {
			throw new RuntimeException("Cannot run command: "+cmd, e);
		}
	}

	public String addSearches(List<String> facetList, String cmd) {
		for (String facet : facetList) {
			if (facet.equals("gene")) {
				cmd += " gene(human)";
			} else if (facet.equals("species")) {
				cmd += " species(binomial)";
			} else {
				cmd += " "+SEARCH + "("+facet+")";
			}
		}
		return cmd;
	}

	public void makeProject() {
		cProject.makeProject(Arrays.asList(CTree.PDF, CTree.XML, CTree.HTML), 99);
// flush old CProject as CTreeList needs to be reset		
		cProject = new CProject(cProject.getDirectory());
		cProject.setDebugLevel(debugLevel);
	}

	public void convertPDFOutputSVGFilesImageFiles() {
		cProject.setCTreelist(null);
		cProject.convertPDFOutputSVGFilesImageFiles();
		return;
	}

	public void convertPSVGandWriteHtml() {
		cProject.convertPSVGandWriteHtml();
	}

	public void convertJATSXMLandWriteHtml() {
		Norma norma = new Norma();
		String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project "+cProject.getDirectory();
		norma.run(args);
	}

	public void convertHTMLsToProjectAndRunCooccurrence(List<String> facetList) {
		makeProject(/*CTree.HTML*/);
		convertRawHTMLToScholarly();
		runSearchesAndCooccurrence(facetList);
	}

	private void convertRawHTMLToScholarly() {
		Norma norma = new Norma();
		String args = " -i fulltext.html -o scholarly.html --html jsoup --project "+cProject.getDirectory();
		norma.run(args);
	}

	public void convertPDFsToProjectAndRunCooccurrence(List<String> facetList) {
		convertPDFstoProjectAndMakeHtml();
		runSearchesAndCooccurrence(facetList);
	}

	public void convertPDFstoProjectAndMakeHtml() {
		convertPDFsToProjectAndMakeSVG();
		convertPSVGandWriteHtml();
	}

	public void convertPDFsToProjectAndMakeSVG() {
		makeProject();
		convertPDFOutputSVGFilesImageFiles();
	}

	public void runSearchesAndCooccurrence(List<String> facetList) {
		if (facetList.size() == 0) {
			LOG.debug("no facets/searches/dictionaries given");
		} else {
			runSearches(facetList);
//			facetList = addSubFacets(facetList);
			defaultAnalyzeCooccurrence(facetList);
		}
	}

	/** this is a mess. add brackets for subfacets (species, gene) 
	 * depends on the file structure.
	 * 
	 * @param facetList
	 * @return
	 */
	private List<String> addSubFacets(List<String> facetList) {
		List<String> facetList1 = new ArrayList<String>();
		for (String facet : facetList) {
			if ("species".equals(facet)) {
				facet += "(binomial)";
			}
			facetList1.add(facet);
		}
		return facetList1;
	}

	public void setIncludeCTrees(String... treeNames) {
		if (cProject != null) {
			cProject.setIncludeTreeList(Arrays.asList(treeNames));
		}
		LOG.debug("CP "+cProject);
	}


	public static void main(String[] args) {
		List<String> argList = new ArrayList<String>(Arrays.asList(args));
		if (argList.size() == 0 || HELP.equals(argList.get(0))) {
//			runHelp(argList);
			listCommands();
		} else {
//			runAMISearches(argList);
		}
	}

	public static void listCommands() {
		String xpath = ""
			+ "//*[" + LOCAL_NAME_BR + "='" + PLUGIN + "' and *[" + LOCAL_NAME_BR + "='" + ARTIFACT_ID + "' and .= 'appassembler-maven-plugin']]"
			+ "/*[" + LOCAL_NAME_BR + "='" + CONFIGURATION + "']"
			+ "/*[" + LOCAL_NAME_BR + "='" + PROGRAMS + "']"
			+ "/*[" + LOCAL_NAME_BR + "='" + PROGRAM2 + "']"
			;
		URL pomUrl = AMIProcessor.class.getResource("/"+NAConstants.POM_XML);
		List<AMICommandLineComponent> commandList = createCommandList(xpath, pomUrl);
		for (AMICommandLineComponent component : commandList) {
			System.err.println(component.toString());
		}
	}

	private static List<AMICommandLineComponent> createCommandList(String xpath, URL pomUrl) {
		List<AMICommandLineComponent> commandList = new ArrayList<AMICommandLineComponent>();
		List<Element> programList;
		try {
			programList = XMLUtil.getQueryElements(XMLUtil.parseQuietlyToDocument(pomUrl.openStream()).getRootElement(), xpath);
			if (programList != null) {
				for (Element program : programList) {
					String id = XMLUtil.getSingleValue(program, "./*[" + LOCAL_NAME_BR + "='" + ID + "']");
					String description = XMLUtil.getSingleValue(program, "./comment()[contains(.,'" + DESCRIPTION + "')]");
					if (description != null) {
						int idx = description.indexOf(DESCRIPTION);
						if (idx != -1) {
							description = description.substring(idx + DESCRIPTION.length()).trim();
						}
					}
					String mainClass = XMLUtil.getSingleValue(program, "./*[" + LOCAL_NAME_BR + "='" + MAIN_CLASS + "']");
					AMICommandLineComponent commandLineComponent = new AMICommandLineComponent(id, mainClass, description);
					commandList.add(commandLineComponent);
//					System.err.println(CMStringUtil.addPaddedSpaces(id, 20) + " " + description + " (" + mainClass + ")");
				}
			}
		} catch (IOException ioe) {
			throw new RuntimeException("cannot read POM", ioe);
		}
		return commandList;
	}

	public static void runHelp(List<String> argList) {
		if (argList.size() > 0) argList.remove(0);
//		AMIDictionary dictionaries = new AMIDictionary();
//		dictionaries.help(argList);
	}

	public void run(String cmd) {
		try {
			CommandProcessor.main((cProject.getDirectory()+" "+cmd).split("\\s+"));
		} catch (IOException e) {
			throw new RuntimeException("Cannot run command: "+cmd, e);
		}
	}

	/** creates directory ffrom first argument; if not exists, return null
	 * 
	 * @param argList
	 * @return
	 */
	public static File createProjectDirAndTrimArgs(List<String> argList) {
		File projectDir = null;
		if (argList != null && argList.size() > 0) {
			String pathname = argList.get(0);
			argList.remove(0);
			projectDir = new File(pathname);
			if (!projectDir.exists() || !projectDir.isDirectory()) {
				projectDir = new File(System.getProperty("user.dir"), pathname);
			}
			if (!projectDir.exists() || !projectDir.isDirectory()) {
				LOG.error("cannot find directory: "+projectDir);
				projectDir = null;
			}
		}
		return projectDir;
	}

	/** copies POM into top of resources
	 * 
	 */
	public static void updatePOMinMainResources() {
		if (NAConstants.NORMAMI_DIR.exists()) {
			LOG.debug(NAConstants.SRC_MAIN_RESOURCES_POM_XML+"; "+NAConstants.SRC_MAIN_RESOURCES_POM_XML.exists());
			if (CMFileUtil.shouldMake(
					NAConstants.SRC_MAIN_RESOURCES_POM_XML,
					NAConstants.ORIGINAL_POM_XML
					)) {
				try {
					FileUtils.copyFile(NAConstants.ORIGINAL_POM_XML, NAConstants.SRC_MAIN_RESOURCES_POM_XML);
				} catch (IOException e) {
					throw new RuntimeException("cannot copy POM", e);
				}
				LOG.debug("updated POM");
			}
		}
	}

	@Deprecated
	public void setDebugLevel(Level debug) {
		this.debugLevel = debug;
	}
}
