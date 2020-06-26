package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.ami.plugins.search.SearchArgProcessor;
import org.contentmine.ami.plugins.search.SearchPluginOption;
import org.contentmine.ami.tools.option.search.DictionaryOption;
import org.contentmine.ami.tools.option.search.DictionarySuffixOption;
import org.contentmine.ami.tools.option.search.DictionaryTopOption;
import org.contentmine.ami.tools.option.search.IgnorePluginsOption;
import org.contentmine.cproject.files.CProject;
import org.contentmine.norma.NAConstants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */
// MAYBE MERGE WITH WordsTool 
// Currently use this for dictionaries

@Command(
//		subcommands = {
//	    AMIRegexTool.class,
//	    AMIWordsTool.class,
//	},
name = "search",
description = {
		"Searches text (and maybe SVG).",
		//"Has specialist subcommands"
})
public class AMISearchTool extends AbstractAMISearchTool {
	private static final Logger LOG = LogManager.getLogger(AMISearchTool.class);
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

    @Option(names = {"--dictionary"},
    		arity = "1..*",
            description = "symbolic names of dictionaries (likely to be obsoleted). Good values are (country, disease, funders)")
    private List<String> dictionaryList;

    @Option(names = {"--dictionarySuffix"},
    		arity = "1",
    		defaultValue = "xml",
            description = "suffix for search dictionary")
    private List<String> dictionarySuffix;

    @Option(names = {"--dictionaryTop"},
    		arity = "1",
            description = " local dictionary home directory")
    private List<String> dictionaryTopList;


	// These may be required in subclasses
//    @Mixin DictionaryOption dictionaryOption = new DictionaryOption();
//    @Mixin DictionaryTopOption dictionaryTopOption = new DictionaryTopOption();
//    @Mixin DictionarySuffixOption dictionarySuffixOption = new DictionarySuffixOption();
    @Mixin IgnorePluginsOption ignorePluginsOption = new IgnorePluginsOption();
    
    private File dictionaryFile;
	private InputStream dictionaryInputStream;
	
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMISearchTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMISearchTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMISearchTool().runCommands(args);
    }


    @Override
	protected void parseSpecifics() {
    	super.parseSpecifics();
	}

//    public void createOldStyleCmd() {
//    	// probably taken care of by 
//    }
//    @Override
//    protected void runSpecifics() {
//    	abstractSearchArgProcessor = getOrCreateSearchProcessor();
//    	
//		createOldStyleCmd();
//    	populateArgProcessorFromCLI();
//    	createWordListInWordCollectionFactory();
//    	
//    	if (false) {
//    	} else if (processTree && processTrees()) { 
//    	} else {
//    		LOG.debug("old style search command); change");
//			if (cProject == null) {
//				DebugPrint.errorPrintln(Level.ERROR, "requires cProject");
//			} else if (projectExists(cProject)) {
//				processProject();
//			}
//    	}
//    }

//	private void createWordListInWordCollectionFactory() {
//		wordCollectionFactory = abstractSearchArgProcessor.getOrCreateWordCollectionFactory();
//		
////		wordCollectionFactory.setMinRawWordLength(wordLengthRange.getMin());
////		wordCollectionFactory.setMaxRawWordLength(wordLengthRange.getMax());
////		wordCollectionFactory.setMinCountInSet(minCountInSet); // what is this?
////		wordCollectionFactory.setStripNumbers(stripNumbers);
//	}

	protected void populateArgProcessorFromCLI() {
		List<AMISearcher> amiSearcherList = abstractSearchArgProcessor.createSearcherList(dictionaryList);
		LOG.warn("amiSearchers: "+amiSearcherList);
	}

	
//	public AbsectetractSearchArgProcessor getOrCreateSearchProcessor() {
//		if (searchArgProcessor == null) {
//			searchArgProcessor = new AbstractSearchArgProcessor(this);
//		}
//		return searchArgProcessor;
//	}

//	private boolean projectExists(CProject cProject) {
//		return cProject == null || cProject.getDirectory() == null ? false : cProject.getDirectory().isDirectory();
//	}
//	

	/** change this 
	 * @return */
	protected boolean processTree() {
		processedTree = false;
		LOG.info("AMIWords processTree");
		abstractSearchArgProcessor.setCTree(cTree);
		processedTree = runTreeSearch();
		return processedTree;
	}
		
	private boolean runTreeSearch() {
		processedTree = false;
		LOG.error("treeSearch NYI");
		return processedTree;
	}

	public void processProject() {
		LOG.warn("cProject: "+cProject.getName());
		runProjectSearch();
	}



	protected void runProjectSearch() {
		AMIProcessor amiProcessor = AMIProcessor.createProcessorFromCProjectDir(cProject.getDirectory());
		String cmd = buildCommandFromBuiltinsAndFacets();
		/** this uses SearchArgProcessor.runSearch()
		 * this should be called directly.
		 */
		if (verbosity().length > 0) {
			LOG.debug("************************* search "+cmd);
		}
		runLegacyCommandProcessor(cmd);
		amiProcessor.defaultAnalyzeCooccurrence(dictionaryList);
	}

	protected void runLegacyCommandProcessor(String cmd) {
		super.runLegacyCommandProcessor(cmd);
	}

	protected String buildCommandFromBuiltinsAndFacets() {
		
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt";
		String cmd1 = cmd;
		if (dictionaryList != null) {
			for (String facet : dictionaryList) {
				if (facet.equals("gene")) {
					cmd1 += " gene(human)";
				} else if (facet.equals("species")) {
					cmd1 += " species(binomial)";
				} else {
					// add names dictionary
					checkDictionaryExists(facet);
					cmd1 += " "+AMIProcessor.SEARCH + "("+facet+")";
				}
			}
		}
		LOG.warn("created COMMAND: {}", cmd1);
		return cmd1;
	}

	private void checkDictionaryExists(String facet) {
		/** builtin? */
		if (false) {
		} else if (getLocalDictionaryInputStream(facet) != null) {
		} else if (getBuiltinDictionaryInputStream(facet) != null) {
		} else {
			LOG.info("cannot find dictionary: "+facet);
		}
	}

	private InputStream getBuiltinDictionaryInputStream(String dictionary) {
		String resource = SearchPluginOption.createSearchDictionaryResourceString(dictionary);
		dictionaryInputStream = this.getClass().getResourceAsStream(resource);
		if (dictionaryInputStream == null) {
			File builtinFile = new File(NAConstants.PLUGINS_DICTIONARY_DIR, dictionary+".xml");
			try {
				dictionaryInputStream = new FileInputStream(builtinFile);
			} catch (FileNotFoundException e) {
				// cannot find file
			}
		}
		if (dictionaryInputStream == null) {
			LOG.trace("cannot find builtin dictionary: " + dictionary);
		}
		return dictionaryInputStream;
	}

	private InputStream getLocalDictionaryInputStream(String facet) {
		if (dictionaryTopList != null) {
			for (String dictTop : dictionaryTopList) {
				dictionaryFile = new File(dictTop, facet+"."+dictionarySuffix.get(0));
				if (dictionaryFile.exists()) {
//					LOG.debug("exists: "+dictionaryFile);
					try {
						dictionaryInputStream = new FileInputStream(dictionaryFile);
					} catch (FileNotFoundException e) {
						// 
					}
					break;
				} else {
					LOG.debug("cannot find: "+dictionaryFile);
				}
			}
		}
		return dictionaryInputStream;
	}

	@Override
	protected AbstractSearchArgProcessor getOrCreateSearchProcessor() {
		if (abstractSearchArgProcessor == null) {
			abstractSearchArgProcessor = new SearchArgProcessor();
		}
		return abstractSearchArgProcessor;
	}

}
