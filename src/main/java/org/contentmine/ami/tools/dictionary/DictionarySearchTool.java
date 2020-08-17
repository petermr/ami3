package org.contentmine.ami.tools.dictionary;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.tools.AMIUtil;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.openqa.selenium.devtools.memory.Memory.GetDOMCountersResponse;

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
	private static final Logger LOG = LogManager.getLogger(DictionarySearchTool.class);
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
		System.err.println("Not yet written");
	}
	
	@Override
	public void runSub() {
		if (parent.getDirectory() != null) {
			List<File> fileList = collectDictionaryFiles(parent.getDirectory());
//			LOG.warn("files> "+fileList.size());
			foundTerms = new HashSet<>();
			for (File file : fileList) {
				searchDictionaryForTerms(file);
			}
		} else {
			for (String filename : parent.getDictionaryNameList()) {
				searchDictionaryForTerms(new File(filename));
			}
		}
	}

	private void searchDictionaryForTerms(File dictionaryFile) {
		DefaultAMIDictionary amiDictionary = dictionaryFile == null ? 
				null : AbstractAMIDictTool.readDictionary(dictionaryFile);
		if (amiDictionary != null) {
			if (searchTerms != null) {
				foundTerms.addAll(amiDictionary.searchDictionaryForTerms(searchTerms));
			} else if (searchTermFilenames != null) {
				foundTerms.addAll(amiDictionary.searchTermsInFiles(searchTermFilenames));	
			}
//			LOG.warn("\nfound "+foundTerms.size());
		}
	}

	public Set<String> getOrCreateFoundTerms() {
		if (foundTerms == null) {
			foundTerms = new HashSet<>();
		}
		return foundTerms;
	}

	
}
