package org.contentmine.ami.dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.wordutil.PorterStemmer;

public class TermPhrase implements Comparable<TermPhrase> {
	
	private static final Logger LOG = LogManager.getLogger(TermPhrase.class);
private List<String> wordList;
	
	public static TermPhrase createTermPhrase(List<String> wordList) {
		if (wordList == null) return null;
		return new TermPhrase(wordList);
	}
	
	private TermPhrase(List<String> wordList) {
		this.wordList = new ArrayList<String>(wordList);
	}
	
	public static TermPhrase createTermPhrase(String word) {
		if (word == null) return null;
		return new TermPhrase(word);
	}
	
	private TermPhrase(String phrase) {
		wordList = Arrays.asList(phrase.trim().split("\\s+"));
		this.wordList = new ArrayList<String>(wordList);
	}
	
	public List<String> getWords() {
		return wordList;
	}

	public String getString() {
		return wordList == null ? null : StringUtils.join(wordList, " ");
	}

	public void applyPorterStemming() {
		PorterStemmer porterStemmer = new PorterStemmer();
		List<String> newWordList = new ArrayList<String>();
		for (String word : wordList) {
			newWordList.add(porterStemmer.stem(word));
		}
		wordList = newWordList;
	}
	
	public static void applyPorterStemming(List<TermPhrase> phraseList) { 
		for (TermPhrase phrase : phraseList) {
			phrase.applyPorterStemming();
		}
	}
	
	public void toLowerCase() {
		List<String> newWordList = new ArrayList<String>();
		for (String word : wordList) {
			newWordList.add(word.toLowerCase());
		}
		wordList = newWordList;
	}
	
	public String toString() {
		return wordList.toString();
	}

	/** sorts by length and then alphabetically.
	 * 
	 * @param termPhrase
	 * @return
	 */
	public int compareTo(TermPhrase termPhrase) {
		int result = 0;
		
		int sizeDiff = wordList.size() -  termPhrase.wordList.size();
		if (sizeDiff != 0) {
			return sizeDiff;
		}
		for (int i = 0; i < wordList.size(); i++) {
			int wordDiff = wordList.get(i).compareTo(termPhrase.wordList.get(i));
			if (wordDiff != 0) {
				return wordDiff;
			}
		}
		return 0;
	}
	
	public List<String> getWordList() {
		return wordList;
	}

}
