package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */
@Command(name = "summary",
		description = {
		"Summarizes the CTree files into a single toplevel CProject directory tree."
		+ "Used to be hardcoded , but now can be controlled by glob "
				
})
public class AMISummaryTool extends AbstractAMITool {
	private static final String _SUMMARY = "_summary";
	private static final String SNIPPETS_TREE = "snippetsTree";
	private static final String SNIPPETS = "snippets";
	private static final String RESULT = "result";
	
	private static final String TREE        = "tree";
	private static final String COUNT       = "count";
	private static final String EXACT       = "exact";
	private static final String FACET       = "facet";
	private static final String FILE        = "file";
	private static final String GENE        = "gene";
	private static final String FREQUENCIES = "frequencies";
	private static final String MATCH       = "match";
	private static final String PLUGIN      = "plugin";
	private static final String SEARCH      = "search";
	private static final String SPECIES     = "species";
	private static final String WORD        = "word";

	private static final Logger LOG = LogManager.getLogger(AMISummaryTool.class);
	public enum SummaryType {
		count,
		documents,
		snippets,
		;
		private SummaryType() {
			
		}
	}
	public enum OutputType {
		table,
		countSet,
		fullSet,
		;
		private OutputType() {
		}
	}
//    @Option(names = {"--dictionary"},
//    		arity = "1..*",
//            description = "dictionaries to summarize. Probably OBSOLETE")
//    private List<String> dictionaryList = new ArrayList<>();

    @Option(names = {"--flatten"},
    	    defaultValue = "false",
            description = "collect all leaf nodes in single directory; default=true")
    private boolean flatten = true;

//    @Option(names = {"--gene"},
//    		arity = "0..*",
//    	    defaultValue = "human",
//            description = "genes to summarize. OBSOLETE")
//    private List<String> geneList = new ArrayList<>();

    @Option(names = {"--glob"},
    		split=",",
            description = "files to summarize (as glob)")
    private List<String> globList = new ArrayList<>();
    
    @Option(names = {"--output-types"},
    		arity = "1..*",
            description = "output type/s. Not sure how useful this is. `table` creates a CSV table")
    private List<OutputType> outputTypes = new ArrayList<>();
    
//    @Option(names = {"--species"},
//    		arity = "0..*",
//    		defaultValue = "binomial",
//            description = "species to summarize. OBSOLETE")
//    private List<String> speciesList = new ArrayList<>();
    
    @Option(names = {"--word"},
    		arity = "0",
            description = "analyze word frequencies. Probably OBSOLETE.")
    private boolean word = false;

	private RectangularTable table;
	private Multiset<String> totalWordSet;
	private Multiset<String> documentWordSet;

	public AMISummaryTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMISummaryTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMISummaryTool().runCommands(args);
    }


    @Override
	protected void parseSpecifics() {
    	super.parseSpecifics();
//		LOG.info("dictionaryList       {}", dictionaryList);
//		LOG.info("geneList             {}", geneList);
//		LOG.info("speciesList          {}", speciesList);
//		LOG.info("word                 {}", word);
	}

    @Override
    protected void runSpecifics() {
    	if (cProject == null) {
			LOG.error(DebugPrint.MARKER, "requires cProject");
    	} else if (projectExists(cProject)) {
    		processProject();
    	}
    }

	private boolean projectExists(CProject cProject) {
		return cProject == null || cProject.getDirectory() == null ? false : cProject.getDirectory().isDirectory();
	}

	public void processProject() {
		LOG.info("cProject: "+cProject.getName());
		runSummary();
		outputTables();
	}

	private void runSummary() {
		initializeTable();
		summarizeWords();
		analyzePaths();
	}

	private void initializeTable() {
		totalWordSet = HashMultiset.create();
		documentWordSet = HashMultiset.create();
		table = new RectangularTable();
		List<String> header = new ArrayList<>();
		header.add(TREE);
		header.add(PLUGIN);
		header.add(FACET);
		
		header.add(WORD);
		header.add(EXACT);
		header.add(MATCH);
		header.add(COUNT);
		
		table.setHeader(header);
	}

//	private void summarizeDictionaries() {
//		for (String dictionary : dictionaryList) {
//			summarize(SEARCH, dictionary, SummaryType.snippets);
//		}
//	}

//	private void summarizeGenes() {
//		for (String gene : geneList) {
//			summarize(GENE, gene, SummaryType.snippets);
//		}
//	}

//	private void summarizeSpecies() {
//		for (String species : speciesList) {
//			summarize(SPECIES, species, SummaryType.snippets);
//		}
//	}

	private void summarizeWords() {
		if (word) {
			summarize(WORD, FREQUENCIES, SummaryType.snippets);
		}
	}

	private void summarize(String plugin, String facet, SummaryType summaryType) {
		LOG.debug(">> "+plugin+"/"+facet);
		File summaryFile = new File(getCProjectDirectory(), plugin + "." + facet +"." + summaryType + "." + "xml");
		if (!summaryFile.exists()) {
			LOG.warn("no summary file: " + summaryFile);
			return;
		}
		Element summaryElement = XMLUtil.parseQuietlyToDocument(summaryFile).getRootElement();
		List<Element> snippetsList = XMLUtil.getQueryElements(summaryElement, 
				XMLUtil.localNameXPath(SNIPPETS_TREE) + "/" + XMLUtil.localNameXPath(SNIPPETS));
		createSummaryMultisetAndTable(snippetsList, plugin, facet);
	}

	/**
	 * <projectSnippetsTree>
 <snippetsTree>
  <snippets file="osanctum200/PMC1397864/results/word/frequencies/results.xml">
   <result title="frequency" word="control" count="48"/>
   <result title="frequency" word="group" count="43"/>
..
   <result title="frequency" word="significant" count="21"/>
  </snippets>
 </snippetsTree>

	 * @param snippetsList
	 */
	private void createSummaryMultisetAndTable(List<Element> snippetsList, String plugin, String facet) {
		for (Element snippets : snippetsList) {
			String filename = snippets.getAttributeValue(FILE);
			String[] directoryNames = filename.split("/");
			String treename = directoryNames[1];
			List<Element> resultList = XMLUtil.getQueryElements(snippets, XMLUtil.localNameXPath(RESULT));
			for (Element resultElement : resultList) {
				addAttributeValuesToTableAndMaps(plugin, facet, treename, resultElement);
			}
		}
	}

	private void addAttributeValuesToTableAndMaps(String plugin, String facet, String treename, Element resultElement) {
		String word = extractTrimmedValue(resultElement, WORD);
		String exact = extractTrimmedValue(resultElement, EXACT);
		String match = extractTrimmedValue(resultElement, MATCH);
		String countValue = extractTrimmedValue(resultElement, COUNT);
		Integer count = countValue != null ? Integer.parseInt(countValue) : 1;
		List<String> row = new ArrayList<String>();
		row.add(treename);
		row.add(plugin);
		row.add(facet);
		row.add(word);
		row.add(exact);
		row.add(match);
		row.add(String.valueOf(count));
		totalWordSet.add(word, count);
		documentWordSet.add(word);
		table.addRow(row);
	}

	private String extractTrimmedValue(Element resultElement, String attName) {
		String value = resultElement.getAttributeValue(attName);
		value = value == null ? null : value.trim();
		return value;
	}

	private void analyzePaths() {
		for (String glob : globList) {
			summarizeGlob(glob);
		}

	}

	private void summarizeGlob(String glob) {
		File outputDir = flatten ? new File(cProject.getDirectory(), _SUMMARY+"/"+output()) :
			new File(cProject.getDirectory(), _SUMMARY);
		Path outputDirPath = Paths.get(outputDir.getAbsolutePath());
		outputDir.mkdirs();
		List<File> fileList = new CMineGlobber().setGlob(glob)
				.setLocation(cProject.getDirectory()).setRecurse(true).listFiles();
		LOG.info("Sum>files: "+fileList.size());
//		System.out.println(fileList);
		makeSubtreeFiles(outputDir, outputDirPath, fileList);
		
	}

	private List<File> makeSubtreeFiles(File outputDir, Path outputDirPath, List<File> fileList) {
		List<File> subtreeFileList = new ArrayList<>();
		for (int count = 0; count < fileList.size(); count++) {
			File file = fileList.get(count);
			File subTreeFile = makeSubtreeFile(outputDir, outputDirPath, count, file);
			try {
				FileUtils.copyFile(file, subTreeFile);
			} catch (IOException e) {
				throw new RuntimeException("cannot copy", e);
			}
			count++;
		}
		return subtreeFileList;
	}

	private File makeSubtreeFile(File outputDir, Path outputDirPath, int count, File file) {
		Path filePath = Paths.get(file.getAbsolutePath());
		Path invRelPath = outputDirPath.relativize(filePath);
		Path invRelPath1 = invRelPath.subpath(1, invRelPath.getNameCount());
		Path resolvePath = outputDirPath.resolve(invRelPath1);

		File subtreeFile = flatten ? new File(outputDir, count+"_"+file.getName()) : resolvePath.toFile();
		return subtreeFile;
	}

	private void outputTables() {
		if (outputTypes.contains(OutputType.table)) {
			File file = new File(cProject.getDirectory(), "tidySummary.csv");
			LOG.debug("TABLE "+file);
			try {
				table.writeCsvFile(file);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write CSV: ", e);
			}
		}
	}


}
