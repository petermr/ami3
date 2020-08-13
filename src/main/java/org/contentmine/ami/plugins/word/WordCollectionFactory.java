package org.contentmine.ami.plugins.word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.tools.AMIWordsTool;
import org.contentmine.ami.tools.AMIWordsTool.WordMethod;
import org.contentmine.ami.tools.lucene.LuceneUtils;
import org.contentmine.ami.wordutil.WordSetWrapper;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.files.ResultsElementList;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

import nu.xom.Attribute;
import nu.xom.IllegalCharacterDataException;

/** creates collections of words.
 * 
 * ultimately move to wordutil
 * 
 * @author pm286
 *
 */
public class WordCollectionFactory {
	private static final Logger LOG = LogManager.getLogger(WordCollectionFactory.class);
	
	private static final String _PR = "WCF>";
	private static final String COUNT = "count";
	private static final String VALUE = "value";
	private static final String LENGTH = "length";
	private static final String LENGTHS = "lengths";
	private static final String FREQUENCIES_ATT = "frequencies";
	private static final String FREQUENCY_ATT = "frequency";
	private static final String PROPERTY = "property";

	private static final String DOCUMENT_FREQUENCY   = "documentFrequency";
	private static final String BOOLEAN_FREQUENCIES = "booleanFrequencies";
	private static final String TFIDF_FREQUENCY      = "tfidfFrequency";
	private static final String TFIDF_FREQUENCIES    = "tfidfFrequencies";

	private static final int DEFAULT_MIN_COUNT_IN_SET = 4;            // for set
	private static final int DEFAULT_MIN_RAW_WORD_LENGTH = 3;
	private static final int DEFAULT_MAX_RAW_WORD_LENGTH = 99999;
	
	private WordSetWrapper stopwords;
	private AMIArgProcessor amiArgProcessor;
	private List<String> abbreviations;
	private List<String> capitalized;
	private int minCountInSet;
	private int minRawWordLength;
	private int maxRawWordLength;
	private String sortControl = COUNT;
	private Iterable<Multiset.Entry<String>> entriesSortedByCount;
	private Iterable<Multiset.Entry<String>> entriesSortedByValue;
	private WordResultsElement frequenciesElement;
	private WordResultsElement aggregatedFrequenciesElement;
	private WordResultsElement booleanFrequenciesElement;
	private boolean stripNumbers;
	private List<String> rawWords;
	private AMIWordsTool wordsTool;

	private WordArgProcessor wordArgProcessor;

	private List<String> wordList;

	public WordCollectionFactory(AMIArgProcessor argProcessor) {
		this.amiArgProcessor = argProcessor;
		if (amiArgProcessor instanceof WordArgProcessor) {
			this.wordArgProcessor = (WordArgProcessor) amiArgProcessor;
		}
		setDefaults();
	}

	private void setDefaults() {
		minCountInSet = DEFAULT_MIN_COUNT_IN_SET;
		minRawWordLength = DEFAULT_MIN_RAW_WORD_LENGTH;
		maxRawWordLength = DEFAULT_MAX_RAW_WORD_LENGTH;
		stripNumbers = true;
	}

	public boolean isStripNumbers() {
		return stripNumbers;
	}

	/**
	 * remove numbers from words.
	 * 
	 * @param stripNumbers
	 */
	public void setStripNumbers(boolean stripNumbers) {
		this.stripNumbers = stripNumbers;
	}

	/** entry point from WordArgProcessor but could be changed to AMIWordsTool */
	void extractWords() {

		wordList = createWordList();
		if (wordList == null) {
			LOG.debug("no words found to extract");
			return;
		}
		LOG.debug(_PR+"created words: "+wordList.size());
		wordsTool = wordArgProcessor.getWordsTool();
		List<String> chosenMethods = wordArgProcessor.getChosenWordAggregationMethods();
		if (wordArgProcessor.getVerbosityInt() > 0) System.out.println("chosen createWordmethods: "+chosenMethods);
		writeWordLengthsToResultsElement(chosenMethods);
		writeWordFrequenciesToResultsElement(chosenMethods);
	}

	private void writeWordFrequenciesToResultsElement( List<String> chosenMethods) {
		if ((wordsTool != null && wordsTool.getWordMethods().contains(WordMethod.frequencies)) ||
				chosenMethods.contains(WordArgProcessor.WORD_FREQUENCIES) ||
				chosenMethods.contains(WordArgProcessor.FREQUENCIES)) {
			LOG.trace("calculating word frequencies");
			ResultsElement resultsElement = getWordFrequencies(wordList);
			wordArgProcessor.addResultsElement(resultsElement);
		}
	}

	private void writeWordLengthsToResultsElement(List<String> chosenMethods) {
		if ((wordsTool != null && wordsTool.getWordMethods().contains(WordMethod.wordLengths)) ||
				chosenMethods.contains(WordArgProcessor.WORD_LENGTHS)) {
			LOG.trace("calculating word lengths");
			ResultsElement resultsElement = createWordLengthsResultsElement(wordList);
			wordArgProcessor.addResultsElement(resultsElement);
		}
	}

	public List<String> createWordList() {
		CTree currentCTree = amiArgProcessor.getCurrentCTree();
		wordList = null;
		if (currentCTree == null) {
			LOG.warn("No current tree");
		} else {
			List<String> rawWords = currentCTree.extractWords();
			wordList = (rawWords == null) ? null : transformWordStream(rawWords);
			if (wordsTool != null && wordsTool.getVerbosityInt() >= 2) {
				LOG.debug("wordsTool " + wordList.size());
			}
		}
//		AbstractTool.debug(amiArgProcessor.getAbstractTool(), 0, "createWordList "+words.size(), LOG);
		return wordList;
	}

	// OLD version 
	private List<String> transformWordStream(List<String> transformedWords) {
//		AbstractTool.debug(amiArgProcessor.getAbstractTool(), 1, "transformWordStream", LOG);
		AMIArgProcessor wordArgProcessor = (AMIArgProcessor) amiArgProcessor;
		if (amiArgProcessor.getChosenWordTypes().contains(AMIArgProcessor.ABBREVIATION)) {
			transformedWords = createAbbreviations(transformedWords);
		}
		if (amiArgProcessor.getChosenWordTypes().contains(AMIArgProcessor.CAPITALIZED)) {
			transformedWords = createCapitalized(transformedWords);
		} 
		if (amiArgProcessor.getWordCaseList().contains(AMIArgProcessor.IGNORE)) {
			transformedWords = toLowerCase(transformedWords);
		}
		List<WordSetWrapper> stopWordSetList = wordArgProcessor.getStopwordSetList();
		for (WordSetWrapper stopWordSet : stopWordSetList) {
			transformedWords = applyStopwordFilter(stopWordSet, transformedWords);
		}
		if (amiArgProcessor.getStemming()) {
			transformedWords = LuceneUtils.applyPorterStemming(transformedWords);
		}
		return transformedWords;
	}

	/** AMIWordsTool version */
	private List<String> transformWordStream(AMIWordsTool wordsTool, List<String> transformedWords) {
		if (wordsTool.isAbbreviation()) {
			transformedWords = createAbbreviations(transformedWords);
		}
		if (wordsTool.isCapital()) {
			transformedWords = createCapitalized(transformedWords);
		} 
		if (wordsTool.isIgnoreCase()) {
			transformedWords = toLowerCase(transformedWords);
		}
		List<WordSetWrapper> stopWordSetList = wordsTool.getStopWordsSetList();
		for (WordSetWrapper stopWordSet : stopWordSetList) {
			transformedWords = applyStopwordFilter(stopWordSet, transformedWords);
		}
		if (wordsTool.isStemming()) {
			transformedWords = LuceneUtils.applyPorterStemming(transformedWords);
		}
		return transformedWords;
	}

	private List<String> createAbbreviations(List<String> inputWords) {
		abbreviations = new ArrayList<String>();
		for (String word : inputWords) {
			if (isAbbreviation(word)) {
				abbreviations.add(word);
			}
		}
		return abbreviations;
	}

	private List<String> createCapitalized(List<String> inputWords) {
		capitalized = new ArrayList<String>();
		for (String word : inputWords) {
			if (isCapitalized(word)) {
				capitalized.add(word);
			}
		}
		return capitalized;
	}

	/** includes nonleading uppercase characters or numbers or '-'.
	 * 
	 * @param word
	 * @return
	 */
	private boolean isAbbreviation(String word) {
		if (word == null || word.length() == 0) return false;
		// must start with Uppercase
		if (!Character.isUpperCase(word.charAt(0))) return false;
		int nupper = 0;
		int nlower = 0;
		int npunct = 0;
		int ndigit = 0;
		for (int i = 1; i < word.length(); i++) {
			char c = word.charAt(i);
			if (Character.isUpperCase(c)) {
				nupper++;
			} else if (Character.isLowerCase(c)) {
				nlower++;
			} else if (Character.isDigit(c)) {
				ndigit++;
			} else if (c == '-') {
				npunct++;
			} else {
				return false;
			}
		}
		// very crude
		if (nupper > nlower) {
			return true;
		}
		return false;
	}

	/** starts with uppercase characters and then only lower alpha.
	 * 
	 * @param word
	 * @return
	 */
	private boolean isCapitalized(String word) {
		if (!Character.isUpperCase(word.charAt(0))) return false;
		for (int i = 1; i < word.length(); i++) {
			char c = word.charAt(i);
			if (!Character.isLowerCase(c)) {
				return false;
			}
		}
		return true;
	}

	private List<String> applyStopwordFilter(WordSetWrapper stopwords, List<String> rawWords) {
		List<String> transformedWords = new ArrayList<String>();
		for (String word : rawWords) {
			word = word.trim();
			if (!stopwords.contains(word.toLowerCase())) {
				transformedWords.add(word);
			}
		}
		LOG.trace("stopwords "+stopwords.size()+"; current words: "+transformedWords.size());
		return transformedWords;
	}
	
	private List<String> toLowerCase(List<String> words) {
		ArrayList<String> newList = new ArrayList<String>();
		for (String word : words) {
			newList.add(word.toLowerCase());
		}
		return newList;
	}

	private WordResultsElement createWordLengthsResultsElement(List<String> words) {
		Multiset<Integer> lengthSet = HashMultiset.create();
		for (String word : words) {
			lengthSet.add(word.length());
		}
		return getWordLengthsResultsElement(lengthSet);
	}

	private WordResultsElement getWordLengthsResultsElement(Multiset<Integer> lengthSet) {
		WordResultsElement lengthsElement = new WordResultsElement(LENGTHS);
		for (Entry<Integer> entry : lengthSet.entrySet()) {
			WordResultElement lengthElement = new WordResultElement(LENGTH);
			lengthElement.setLength(entry.getElement().intValue());
			lengthElement.setCount(entry.getCount());
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
	}
	
	private WordResultsElement getWordFrequencies(List<String> words) {
		Multiset<String> wordSet = HashMultiset.create();
		if (words == null) {
			LOG.warn("No current words ");
			frequenciesElement = null;
		} else {
			for (String rawWord : words) {
	//			rawWord = rawWord.toLowerCase(); // normalize case
				// BAD !
				if (stripNumbers) {
					rawWord = removeNumbersFromWord(rawWord);
				}
				if (stopwords != null && stopwords.contains(rawWord.toLowerCase())) {
					continue;
				}
				if (rawWord.length() >= minRawWordLength 
						&& rawWord.length() <= maxRawWordLength) { //remove stopwords and short strings
					wordSet.add(rawWord);
				}
			}		
			// these are not completely sorted because several words may have same frequency
			entriesSortedByCount = getEntriesSortedByCount(wordSet);
			entriesSortedByValue = getEntriesSortedByValue(wordSet);
			Iterable<Multiset.Entry<String>> sortedEntries = getSortedEntries();
			frequenciesElement =  createFrequenciesElement(sortedEntries);
		}
		return frequenciesElement;
			
	}

	private String removeNumbersFromWord(String rawWord) {
		rawWord = rawWord.replaceAll("[\\d+]", ""); // remove numbers
		return rawWord;
	}

	private WordResultsElement createFrequenciesElement(
			Iterable<Multiset.Entry<String>> sortedEntries) {
		frequenciesElement = new WordResultsElement(FREQUENCIES_ATT);
		List<WordResultElement> frequenciesElementList = new ArrayList<WordResultElement>();
		for (Entry<String> entry : sortedEntries) {
			int count = +entry.getCount();
			if (count < minCountInSet) continue;
			WordResultElement frequencyElement = new WordResultElement(FREQUENCY_ATT);
			// some values are not allowed in attribute values
			try {
				frequencyElement.addAttribute(new Attribute(WordResultElement.WORD_ATT, String.valueOf(entry.getElement())));
			} catch (IllegalCharacterDataException e) {
				continue;
			}
			frequencyElement.addAttribute(new Attribute(WordResultElement.COUNT_ATT, String.valueOf(count)));
			frequenciesElementList.add(frequencyElement);
		}
		if (COUNT.equals(sortControl)) {
			frequenciesElementList = sortByValue(frequenciesElementList);
		}
		for (WordResultElement frequencyElement : frequenciesElementList) {
//			LOG.debug(frequencyElement.toXML());
			frequenciesElement.appendChild(frequencyElement);
		}
		return frequenciesElement;
	}

	private List<WordResultElement> sortByValue(List<WordResultElement> frequenciesElementList) {
		// assume already sorted by count
		List<WordResultElement> newList = new ArrayList<WordResultElement>();
		int lastCount = -1;
		List<List<WordResultElement>> sameCountListList = new ArrayList<List<WordResultElement>>();
		List<WordResultElement> sameCountList = null;
		for (WordResultElement wordResultElement : frequenciesElementList) {
			int count = wordResultElement.getCount();
			if (count != lastCount) {
				sameCountList = new ArrayList<WordResultElement>();
				sameCountListList.add(sameCountList);
				lastCount = count;
			} 
			sameCountList.add(wordResultElement);
		}
		for (List<WordResultElement> sameCountList0 : sameCountListList) {
			sameCountList0 = sortByValue0(sameCountList0);
//			LOG.debug("count: "+sameCountList0.size()+": freq "+sameCountList0.get(0).getCount());
			for (WordResultElement resultElement : sameCountList0) {
//				LOG.debug(">adding> "+resultElement.toXML());
				newList.add(resultElement);
			}
		}
		return newList;
	}

	private List<WordResultElement> sortByValue0(List<WordResultElement> sameCountList0) {
		List<WordResultElement> newList = new ArrayList<WordResultElement>();
		Map<String, WordResultElement> sameCountElementByValue = new HashMap<String, WordResultElement>();
		List<String> words = new ArrayList<String>();
		for (WordResultElement sameCountElement : sameCountList0) {
			String word = sameCountElement.getWord();
			words.add(word);
			sameCountElementByValue.put(word, sameCountElement);
		}
		Collections.sort(words);
		for (String word : words) {
			WordResultElement resultElement = sameCountElementByValue.get(word);
			newList.add(resultElement);
		}
		return newList;
	}

	/** convenience method (for my memory!) */
	public static Iterable<Entry<String>> getEntriesSortedByValue(Multiset<String> wordSet) {
//		return  com.google.common.collect.ImmutableSortedMultiset.copyOf(wordSet).entrySet();
		return  ImmutableSortedMultiset.copyOf(wordSet).entrySet();
	}

	/** convenience method (for my memory!) */
	public static Iterable<Multiset.Entry<String>> getEntriesSortedByCount(Multiset<String> wordSet) {
		return Multisets.copyHighestCountFirst(wordSet).entrySet();
	}

	/** sort by count or value, depends on sortControl
	 * 
	 * @return
	 */
	private Iterable<Multiset.Entry<String>> getSortedEntries() {
		Iterable<Multiset.Entry<String>> sortedEntries = null;
		if (sortControl.equals(COUNT)) {
			sortedEntries = entriesSortedByCount;
		} else if (sortControl.equals(VALUE)) {
			sortedEntries = entriesSortedByValue;
		}
		return sortedEntries;
	}

	public int getMinCountInSet() {
		return minCountInSet;
	}

	public void setMinCountInSet(int minCountInSet) {
		this.minCountInSet = minCountInSet;
	}

	public int getMinRawWordLength() {
		return minRawWordLength;
	}

	public void setMinRawWordLength(int minRawWordLength) {
		this.minRawWordLength = minRawWordLength;
	}

	public int getMaxRawWordLength() {
		return maxRawWordLength;
	}

	public void setMaxRawWordLength(int maxRawWordLength) {
		this.maxRawWordLength = maxRawWordLength;
	}

	public WordSetWrapper getStopwords() {
		return stopwords;
	}

//	public List<String> getCurrentWords() {
//		return currentWords;
//	}

	public List<String> getAbbreviations() {
		return abbreviations;
	}

	public List<String> getCapitalized() {
		return capitalized;
	}

	/** aggregate frequencies for each ResultsElement.
	 * Never used?
	 * @param resultsElementList
	 */
//	public void createAggregateFrequenciesElement(ResultsElementList resultsElementList) {
//		LOG.debug("createAggregateFrequencies");
//		if (amiArgProcessor.getVerbosityInt() >= 2) System.out.println("createAggregateFrequenciesElement");
//		Multiset<String> aggregateSet = this.createAggregateSet(resultsElementList);
//		Iterable<Entry<String>> sortedEntries = getEntriesSortedByCount(aggregateSet);
//		this.createWordResultElementsAndAddToAggregateFrequenciesElement(sortedEntries);
//	}

	private Multiset<String> createAggregateSet(ResultsElementList resultsElementList) {
		if (amiArgProcessor.getVerbosityInt() >= 2) {
			System.out.println("createAggregateSet(ResultsElementList)");
		}
		Multiset<String> aggregateSet = HashMultiset.create();
		for (ResultsElement resultsElement : resultsElementList) {
			String title = resultsElement.getTitle();
			if (!WordArgProcessor.FREQUENCIES.equals(title)) {
				LOG.debug("Skipped non:frequencies result: "+title);
				continue;
			}
			addResultsToSet(aggregateSet, resultsElement);
		}
		return aggregateSet;
	}

	private WordResultsElement createWordResultElementsAndAddToAggregateFrequenciesElement(Iterable<Entry<String>> sortedEntries) {
		if (amiArgProcessor.getVerbosityInt() >= 2) System.out.println("createWordResultElementsAndAddToAggregateFrequenciesElement(Iterable<Entry<String>>");
		aggregatedFrequenciesElement = new WordResultsElement(WordArgProcessor.FREQUENCIES);
		for (Entry<String> entry : sortedEntries) {
			WordResultElement wordResultElement = new WordResultElement(FREQUENCY_ATT);
			wordResultElement.setWord(entry.getElement());
			wordResultElement.setCount(entry.getCount());
			aggregatedFrequenciesElement.appendChild(wordResultElement);
		}
		return aggregatedFrequenciesElement;
	}

	private void addResultsToSet(Multiset<String> aggregateSet, ResultsElement resultsElement) {
		for (ResultElement resultElement : resultsElement) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			String word = wordResultElement.getWord();
			Integer count = wordResultElement.getCount();
			aggregateSet.add(word, count);
		}
	}

	WordResultsElement createAggregatedFrequenciesElement(WordResultsElementList frequenciesElementList) {
		Multiset<String> aggregateSet = createAggregateSet(frequenciesElementList);
		Iterable<Entry<String>> sortedEntries = WordCollectionFactory.getEntriesSortedByCount(aggregateSet);
		aggregatedFrequenciesElement = createWordResultElementsAndAddToAggregateFrequenciesElement(sortedEntries);
		return aggregatedFrequenciesElement;
	}

	WordResultsElement createBooleanFrequencies(DefaultArgProcessor wordArgProcessor, WordResultsElementList frequenciesElementList) {
		aggregatedFrequenciesElement = createAggregatedFrequenciesElement(frequenciesElementList);
		booleanFrequenciesElement = new WordResultsElement(BOOLEAN_FREQUENCIES);
		for (ResultElement termElement : aggregatedFrequenciesElement) {
			String word = ((WordResultElement)termElement).getWord();
			int documentCount = frequenciesElementList.getSingleCountsOfWord(word);
			if (documentCount > 0) {
				WordResultElement documentFrequencyElement = new WordResultElement(DOCUMENT_FREQUENCY);
				documentFrequencyElement.setWord(word);
				documentFrequencyElement.setCount(documentCount);
				booleanFrequenciesElement.appendChild(documentFrequencyElement);
			}
		}
		return booleanFrequenciesElement;
	}
	
	/** NYI
	 * 
	 * @param wordArgProcessor
	 * @param frequenciesElementList
	 * @return
	 */
	WordResultsElement createTFIDFFrequencies(DefaultArgProcessor wordArgProcessor, WordResultsElementList frequenciesElementList) {
//		WordResultsElement aggregatedFrequenciesElement = createAggregatedFrequenciesElement(frequenciesElementList);
		WordResultsElement booleanFrequencyElement = new WordResultsElement(TFIDF_FREQUENCIES);
		LOG.error("TFIDF NYI");
//		for (ResultElement termElement : aggregatedFrequenciesElement) {
//			String word = ((WordResultElement)termElement).getWord();
//			int documentCount = frequenciesElementList.getSingleCountsOfWord(word);
//			if (documentCount > 0) {
//				WordResultElement documentFrequencyElement = new WordResultElement(TFIDF_FREQUENCY);
//				documentFrequencyElement.setWord(word);
//				documentFrequencyElement.setCount(documentCount);
//				booleanFrequencyElement.appendChild(documentFrequencyElement);
//			}
//		}
		return booleanFrequencyElement;
	}
	
}
