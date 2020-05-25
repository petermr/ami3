package org.contentmine.ami.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.word.WordCollectionFactory;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.IntRange;

import picocli.CommandLine.Command;
/**
 * 
 * @author pm286
 *
 */
import picocli.CommandLine.Option;

/** probably wont use these */
@Command(
name = "ami-abstract-search", 
aliases = "abstract-search",
version = "ami-abstract-search 0.1",
description = "toplevel for all searches"
)

public abstract class AbstractAMISearchTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AbstractAMISearchTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	// These may be required in subclasses
//    @Mixin DictionaryOption dictionaryOption = new DictionaryOption();
//    @Mixin DictionaryTopOption dictionaryTopOption = new DictionaryTopOption();
//    @Mixin DictionarySuffixOption dictionarySuffixOption = new DictionarySuffixOption();
//    private File dictionaryFile;
//	private InputStream dictionaryInputStream;

	/** historical
	 * // search
		parseMethod="parseSearch"
		runMethod="runSearch"
		outputMethod="outputSearch"
		
		// regex
		args="location [location ...]"
		countRange="{1,*}"
		parseMethod="parseRegex"
		runMethod="runRegex"
		outputMethod="outputResultElements"

		// sequence
		initMethod="initSequences"
		runMethod="runExtractSequences"
		outputMethod="outputSequences"
	    args="type"
	    pattern="(dna|dnaraw|dnaprimer|rna|rnaraw|prot|prot1|prot3|carb3)"
	    parseMethod="parseTypes"
	    parseMethod="parseLength"
	    
	    // species
		initMethod="initSpecies"
		runMethod="runExtractSpecies"
		outputMethod="outputSpecies"
		<value name="binomial">&lt;i>\s*(([A-Z][a-z]?\.|[A-Z][a-z]{3,})\s+[a-z]+)\s*&lt;/i></value> 
		<value name="genus">&lt;i>\s*([A-Z][a-z]{3,})\s*&lt;/i>(?=\s+[^s])</value> 
		<value name="genussp">&lt;i>\s*([A-Z][a-z]+)\s*&lt;/i>\s*spp?(?=\p{Punct}|\s)</value> 
	    args="type"
	    pattern="(binomial|genus|genussp)"
	
	    parseMethod="parseAbbreviations"
	    
	    // identifiers
		runMethod="runExtractIdentifiers"
		outputMethod="outputIdentifiers"
		<value fields="bio.ena" url="http://www.ncbi.nlm.nih.gov/Sequin/acc.html" 
		    lookup="http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&amp;term=${match}GENBANK_ID" >[A-Z]{2}\d{6}|[A-Z]{1}\d{5}</value>
		<value fields="bio.enaprot" url="http://www.ncbi.nlm.nih.gov/Sequin/acc.html">[A-Z]{3}\d{5}</value>
		<value fields="bio.pdb">\w[A-Z]{3}\d</value>
		<value fields="meta.orcid" url="http://support.orcid.org/knowledgebase/articles/116780-structure-of-the-orcid-identifier">http://orcid\.org/(\d{4}-){3}\d{3}[\dX]</value>
		<value fields="clin.nct">NCT\d{8}</value>
		<value fields="clin.isrctn">ISRCTN.{0,20}\d{8}</value>
		<!-- example AB_1004986 -->
		<value fields="rrid.ab" lookup="https://scicrunch.org/resources/Antibodies/search?q=${match}">RRID:(AB_\d+)</value>

		// gene
		runMethod="runExtractGene"
		outputMethod="outputGene"
		<value   name="human">&lt;i>\s*([A-Z][A-Z0-9]{2,10})\s*&lt;/i></value> 
		<!--  Brca1-->
		<value name="mouse">&lt;i>\s*([A-Z][a-z\d]{2,10})\s*&lt;/i></value> 
		<value name="hgnc">org/contentmine/ami/plugins/gene/hgnc/hgnc_complete_set.xml</value>
	    args="type"
	    pattern="(human|mouse)"
	    parseMethod="parseTypes"
	
	 */
    @Option(names = {"--stripNumbers"},
    		arity = "0",
            description = "Strip numbers from words")
    private Boolean stripNumbers = false;

    @Option(names = {"--wordCount"},
    		converter = IntRangeConverter.class,
    		arity = "1",
            description = "count range for words for frequencies (comma-separated); ${DEFAULT_VALUE}")
	protected IntRange wordCountRange = new IntRange(20,1000000);

    @Option(names = {"--wordLength"},
    		converter = IntRangeConverter.class,
    		arity = "1",
            description = "length range for words for wordlengths (comma-separated); ${DEFAULT_VALUE}")
    private IntRange wordLengthRange = new IntRange(1,20);

    @Option(names = {"--wikidataBiblio"},
    		arity = "0",
            description = " lookup wikidata biblographic object")
    protected Boolean wikidataBiblio = false;

	@Option(names = {"--no-oldstyle"},
			description = "(A) use oldstyle style of processing (project based) for unconverted tools; "
					+ "new style is per tree")
	protected boolean oldstyle = true;

	protected AbstractSearchArgProcessor abstractSearchArgProcessor;
	protected WordCollectionFactory wordCollectionFactory;
	
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	protected AbstractAMISearchTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	protected AbstractAMISearchTool() {
	}
	
	protected abstract void populateArgProcessorFromCLI();
	protected abstract AbstractSearchArgProcessor getOrCreateSearchProcessor();
	protected abstract void processProject();
	protected abstract String buildCommandFromBuiltinsAndFacets();



    @Override
	protected void parseSpecifics() {
    	super.parseSpecifics();
////		System.out.println("dictionaryList       " + dictionaryOption.getDictionaryList());
//		System.out.println("oldstyle             " + oldstyle);
//		System.out.println("strip numbers        " + stripNumbers);
//		System.out.println("wordCountRange       " + wordCountRange);
//		System.out.println("wordLengthRange      " + wordLengthRange);
//		System.out.println();
	}

    @Override
    protected void runSpecifics() {
    	abstractSearchArgProcessor = getOrCreateSearchProcessor();
    	
    	if (oldstyle) {
    		// this is the complete command including "words" and "search"
        	String cmd = buildCommandFromBuiltinsAndFacets();
        	LOG.trace("cmd: "+cmd);
    	} else {
    		populateArgProcessorFromCLI();
    	}
    	createWordListInWordCollectionFactory();
    	
    	if (false) {
    	} else if (oldstyle) {
    		LOG.debug("old style search command); to be changed");
			if (cProject == null) {
				DebugPrint.errorPrintln(Level.ERROR, "requires cProject");
			} else if (projectExists(cProject)) {
				processProject();
			}
    	} else if (processTrees()) { 
    		LOG.debug("New style, processTrees()");
    		// 
    	}
    }

    /** this prepares but ? does not run
     * 
     */
	protected void createWordListInWordCollectionFactory() {
    	abstractSearchArgProcessor = getOrCreateSearchProcessor();
		wordCollectionFactory = abstractSearchArgProcessor.getOrCreateWordCollectionFactory();
		
		wordCollectionFactory.setMinRawWordLength(wordLengthRange.getMin());
		wordCollectionFactory.setMaxRawWordLength(wordLengthRange.getMax());
		wordCollectionFactory.setStripNumbers(stripNumbers);
		LOG.trace("wordCollection Factory");
	}

	private boolean projectExists(CProject cProject) {
		return cProject == null || cProject.getDirectory() == null ? false : cProject.getDirectory().isDirectory();
	}
			
	
	protected abstract void runProjectSearch();

/**
 * 			commandProcessor.parseCommands(cmdList);
			commandProcessor.runNormaIfNecessary();
			commandProcessor.runJsonBibliography();
			commandProcessor.runLegacyPluginOptions(this);
			commandProcessor.createDataTables(wikidataBiblio);

 * @param cmd
 */
	protected void runLegacyCommandProcessor(String cmd) {
//		System.out.println("SEARCH running legacy processors");
		try {
			
			CommandProcessor commandProcessor = new CommandProcessor(cProject.getDirectory());
			List<String> cmdList = Arrays.asList(cmd.trim().split("\\s+"));
			for (String cmd0 : cmdList) {
				System.out.println("legacy cmd> "+cmd0);
			}
			commandProcessor.parseCommands(cmdList);
			commandProcessor.runNormaIfNecessary();
			commandProcessor.runJsonBibliography();
			commandProcessor.runLegacyPluginOptions(this);
			commandProcessor.createDataTables(wikidataBiblio);
		
		} catch (IOException e) {
			throw new RuntimeException("Cannot run command: "+cmd, e);
		}
	}


	/**
	 * 
	 */

}
