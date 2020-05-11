package org.contentmine.ami.tools.dictionary;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.tools.AMIUtil;
import org.contentmine.ami.tools.AbstractAMIDictTool;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		name = "search",
		description = {
				"searches within dictionaries",
				"TBD"
				+ ""
		})
public class DictionarySearchTool extends AbstractAMIDictTool {
	private static final Logger LOG = Logger.getLogger(DictionarySearchTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    @Option(names = {"--search"}, 
    		arity="1..*",
    	    paramLabel = "search",
    		description = "search dictionary for these terms (experimental)"
    		)
    protected Set<String> searchTerms;
    
    @Option(names = {"--searchfile"}, 
    		arity="1..*",
    	    paramLabel = "searchfile",
    		description = "search dictionary for terms in these files (experimental)"
    		)
    protected List<String> searchTermFilenames;

	private Set<String> foundTerms;

	public DictionarySearchTool() {
		super();
	}
	
	@Override
	protected void parseSpecifics() {
		AMIUtil.printNameValue("search", searchTerms);
		AMIUtil.printNameValue("searchfile", searchTermFilenames);
		System.out.println();
	}
	
	@Override
	public void runSub() {
		for (String filename : parent.getDictionaryList()) {
			searchDictionaries(new File(filename));
		}
	}

	private void searchDictionaries(File file) {
		DefaultAMIDictionary amiDictionary = file == null ? null : AbstractAMIDictTool.readDictionary(file);
		if (amiDictionary != null) {
			if (searchTerms != null) {
				foundTerms = amiDictionary.searchDictionaryForTerms(searchTerms);
			} else if (searchTermFilenames != null) {
				foundTerms = amiDictionary.searchTermsInFiles(searchTermFilenames);	
			}
//			System.out.println("\nfound "+foundTerms.size());
		}
	}

	public Set<String> getOrCreateFoundTerms() {
		if (foundTerms == null) {
			foundTerms = new HashSet<>();
		}
		return foundTerms;
	}

	
}
