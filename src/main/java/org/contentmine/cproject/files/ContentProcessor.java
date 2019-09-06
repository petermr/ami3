package org.contentmine.cproject.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/** manages the processing by Norma or AMI.
 * 
 * important components are the CTree being processed and the ResultsElementList.
 * 
 * @author pm286
 *
 */
public class ContentProcessor {

	
	private static final Logger LOG = Logger.getLogger(ContentProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String OVERWRITE = "overwrite";
	private static final String NO_DUPLICATES = "noDuplicates";
	private static final String MERGE = "merge";


	private CTree cmTree;
	private ResultsElementList resultsElementList;
	private HashMap<String, ResultsElement> resultsBySearcherNameMap;
	private String duplicates = OVERWRITE;
	
	public ContentProcessor(CTree cmTree) {
		this.cmTree = cmTree;
	}
	
	private void ensureResultsElementList() {
		if (resultsElementList == null) {
			resultsElementList = new ResultsElementList();
		}
	}

	public void addResultsElement(ResultsElement resultsElement) {
		if (resultsElement != null) {
			this.ensureResultsElementList();
			String title = resultsElement.getTitle();
			if (title == null) {
				throw new RuntimeException("Results Element must have title: "+resultsElement.toXML());
			}
			checkNoDuplicatedTitle(title);
			resultsElementList.add(resultsElement);
		}
	}

	private void checkNoDuplicatedTitle(String title) {
			for (ResultsElement resultsElement : resultsElementList) {
				if (title.equals(resultsElement.getTitle())) {
					if (OVERWRITE.equals(duplicates)) {
						// carry on
					} else if (NO_DUPLICATES.equals(duplicates)) {
						throw new RuntimeException("Cannot have two ResultsElement with same title: "+title);
					} else if (MERGE.equals(duplicates)) {
						throw new RuntimeException("Merge not supported: Cannot have two ResultsElement with same title: "+title);
					}
				}
			}
	}
	
	public void outputResultElements(String namex, DefaultArgProcessor argProcessor ) {
		resultsElementList = new ResultsElementList();
		ensureResultsBySearcherNameMap();
		for (AbstractSearcher optionSearcher : argProcessor.getSearcherList()) {
			String name = optionSearcher.getName();
			ResultsElement resultsElement = resultsBySearcherNameMap.get(name);
			if (resultsElement != null) {
				resultsElement.setTitle(name);
				resultsElementList.add(resultsElement);
			}
		}
		this.createResultsDirectoriesAndOutputResultsElement(namex);
	}

	public void writeResults(String resultsFileName, String results) throws Exception {
		File resultsFile = new File(cmTree.getDirectory(), resultsFileName);
		FileUtils.writeStringToFile(resultsFile, results, Charset.forName("UTF-8"));
	}

	public void writeResults(File resultsFile, Element resultsXML) {
		try {
			XMLUtil.debug(resultsXML, new FileOutputStream(resultsFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("cannot write XML ", e);
		}
	}

	public void writeResults(String resultsFileName, Element resultsXML) {
		File resultsFile = new File(cmTree.getDirectory(), resultsFileName);
		LOG.trace("results file: "+resultsFile);
		writeResults(resultsFile, resultsXML);
	}
	
	/** creates a subdirectory of results/ and writes each result file to its own directory.
	 * 
	 * Example:
	 * 		ctree1_2_3/
	 * 			results/
	 * 				words/
	 * 					frequencies/
	 * 						results.xml
	 * 					lengths/
	 * 						results.xml
	 * 
	 * here the option is defined in an element in args.xml with name="words"
	 * 
	 * @param optionName 
	 * @param resultsElementList
	 * @param resultsDirectoryName
	 */
	public List<File> createResultsDirectoriesAndOutputResultsElement(String name) {
//		System.out.println("createResultsDirectoryAndOutputResultsElement: "+name);
		File optionDirectory = new File(cmTree.getResultsDirectory(), name);
		List<File> outputDirectoryList = new ArrayList<File>();
		for (ResultsElement resultsElement : resultsElementList) {
			File outputDirectory = createResultsDirectoryAndOutputResultsElement(optionDirectory, resultsElement);
			outputDirectoryList.add(outputDirectory);
		}
		return outputDirectoryList;
		
	}

	public File createResultsDirectoryAndOutputResultsElement(
			 String optionName,  ResultsElement resultsElement) {
//		System.out.println("createResultsDirectoryAndOutputResultsElement: "+optionName);
		File optionDirectory = new File(cmTree.getResultsDirectory(), optionName);
		File outputDirectory = createResultsDirectoryAndOutputResultsElement(optionDirectory, resultsElement);
		return outputDirectory;
		
	}

	private File createResultsDirectoryAndOutputResultsElement(File optionDirectory, ResultsElement resultsElement) {
//		System.out.println("createResultsDirectoryAndOutputResultsElement: dir "+optionDirectory);
		File resultsSubDirectory = null;
		String title = resultsElement.getTitle();
		if (title == null) {
			LOG.error("null title");
		} else {
			resultsSubDirectory = new File(optionDirectory, title);
			resultsSubDirectory.mkdirs();
			String resultsFileName = resultsElement.getChildElements().size() == 0 ? CTree.EMPTY_XML :  CTree.RESULTS_XML;
			File resultsFile = new File(resultsSubDirectory, resultsFileName);
			writeResults(resultsFile, resultsElement);
			LOG.trace("Wrote "+resultsFile.getAbsolutePath());
		}
		return resultsSubDirectory;
	}
	
	public String getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(String duplicates) {
		this.duplicates = duplicates;
	}

	public CTree getCmTree() {
		return cmTree;
	}

	public void setCmTree(CTree cmTree) {
		this.cmTree = cmTree;
	}

	public ResultsElementList getOrCreateResultsElementList() {
		if (resultsElementList == null) {
			resultsElementList = new ResultsElementList();
		}
		return resultsElementList;
	}

	public void setResultsElementList(ResultsElementList resultsElementList) {
		this.resultsElementList = resultsElementList;
	}

	public void put(String name, ResultsElement resultsElement) {
		ensureResultsBySearcherNameMap();
		resultsBySearcherNameMap.put(name, resultsElement);
	}

	private void ensureResultsBySearcherNameMap() {
		if (resultsBySearcherNameMap == null) {
			resultsBySearcherNameMap = new HashMap<String, ResultsElement>();
		}
	}

	public ContentProcessor clearResultsElementList() {
		this.resultsElementList = new ResultsElementList();
		return this;
	}
	
	
}
