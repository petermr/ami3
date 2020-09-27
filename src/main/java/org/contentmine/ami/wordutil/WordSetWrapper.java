package org.contentmine.ami.wordutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.cproject.files.ResourceLocation;
import org.contentmine.norma.NAConstants;

import com.google.common.collect.Multiset;

/** a wrapper for a set of words.
 * 
 * Can hold both  Set<String> and Multiset<String>
 * 
 * @author pm286
 *
 */
public class WordSetWrapper {

	
	private static final String DOT_TXT = ".txt";
	private static final String DOT_XML = ".xml";
	private static final Logger LOG = LogManager.getLogger(WordSetWrapper.class);
private static WordSetWrapper COMMON_ENGLISH_STOPWORDS;
	public static final String COMMON_ENGLISH_STOPWORDS_TXT = NAConstants.AMI_WORDUTIL+"/stopwords.txt";
	
	private Set<String> wordSet;
	private Multiset<String> multiset;

	public WordSetWrapper() {
	}
	
	public WordSetWrapper(Set<String> wordSet) {
		this();
		this.wordSet = wordSet;
	}
	
	public Set<String> getWordSet() {
		return wordSet;
	}

	public void setWordSet(Set<String> wordSet) {
		this.wordSet = wordSet;
	}

	public Multiset<String> getMultiset() {
		return multiset;
	}

	public void setMultiset(Multiset<String> multiset) {
		this.multiset = multiset;
	}

	public WordSetWrapper(Multiset<String> multiset) {
		this();
		this.multiset = multiset;
	}

	public static WordSetWrapper createStopwordSet(String stopwordLocation) {
		WordSetWrapper stopwordSet = null;
		Set<String> stopwords = getStopwords(stopwordLocation); 
		if (stopwords != null) {
			stopwordSet = new WordSetWrapper(stopwords);
		}
		return stopwordSet;
	}

	public static WordSetWrapper getCommonEnglishStopwordSet() {
		if (COMMON_ENGLISH_STOPWORDS == null) {
			Set<String> stopwords = getStopwords(COMMON_ENGLISH_STOPWORDS_TXT);
			COMMON_ENGLISH_STOPWORDS = new WordSetWrapper(stopwords);
		}
		return COMMON_ENGLISH_STOPWORDS;
	}
	
	private static Set<String> getStopwords(String stopwordsResource) {
		Set<String> stopwords0 = new HashSet<String>();
		// symbolic name located in package
		if (!stopwordsResource.contains("/")) {
			String packagex = WordSetWrapper.class.getPackage().toString().replaceAll("\\.", "/").replace("package ", "");
			stopwordsResource = "/"+packagex+"/"+stopwordsResource;
			stopwordsResource = addTxt(stopwordsResource);
			LOG.trace("symbol expands to: "+stopwordsResource);
		}
		InputStream stopwordsStream = AMIArgProcessor.class.getResourceAsStream(stopwordsResource);
		if (stopwordsStream == null) {
			if (stopwordsResource.endsWith(DOT_XML)) {
				LOG.warn("WSW> Cannot read XML stopwords yet");
			} else {
				stopwordsResource = addTxt(stopwordsResource);
				stopwordsStream = new ResourceLocation().getInputStreamHeuristically(stopwordsResource);
			}
		}
		if (stopwordsStream == null) {
			LOG.warn("WSW> Cannot read stopword stream: "+stopwordsResource);
		} else {
			try {
				List<String> lines = IOUtils.readLines(stopwordsStream);
				for (String line : lines) {
					stopwords0.add(line.trim());
				}
			} catch (IOException e) {
				throw new RuntimeException("cannot find stopwords "+stopwordsResource);
			}
		}
		LOG.trace("stopword set: "+stopwords0.size());
		return stopwords0;
	}

	private static String addTxt(String stopwordsResource) {
		if (stopwordsResource.indexOf(".") == -1) {
			stopwordsResource += DOT_TXT;
		}
		return stopwordsResource;
	}

	public boolean contains(String word) {
		return wordSet == null ? false : wordSet.contains(word);
	}

	public int size() {
		return wordSet == null ? 0 : wordSet.size();
	}


}
