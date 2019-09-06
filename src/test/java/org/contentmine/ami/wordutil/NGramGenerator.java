package org.contentmine.ami.wordutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class NGramGenerator {

	
	private static final Logger LOG = Logger.getLogger(NGramGenerator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<String> ngrams;
	private String inputString;
	private String normalizedString;
	
	public NGramGenerator() {
	}

	public String read(InputStream input) throws IOException {
		List<String> stringList0 = IOUtils.readLines(input, "iso-8859-1");
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		for (String string : stringList0) {
			sb.append(string);
			sb.append(" "); // replace newlines
		}
		inputString = sb.toString();
		inputString = inputString.replaceAll("[\\s]+", " ");
		return inputString;
	}

	
	public int size() {
		return getNormalizedString().length();
	}
	public List<String> getNGrams(int n) {
		if (ngrams == null) {
			ngrams = getNGrams(inputString, n); 
		}
		return ngrams;
	}
	
	public String getNormalizedString() {
		if (normalizedString == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < inputString.length(); i++) {
				sb.append(normalize(inputString.charAt(i)));
			}
			normalizedString = sb.toString();
		}
		
		return normalizedString;
		
	}
	
	private List<String> getNGrams(String string, int n) {
		List<String> digrams = new ArrayList<String>();
		for (int i = 0; i < string.length()-(n-1); i++) {
			StringBuffer digram = new StringBuffer();
			for (int j = 0; j < n; j++) {
				digram.append(normalize(string.charAt(i+j)));
			}
			digrams.add(digram.toString());
		}
		return digrams;
	}
	
	private char normalize(char ch) {
		if (Character.isWhitespace(ch)) {
			ch = ' ';
		} else if (Character.isDigit(ch)) {
			ch = '0';
		} else if (ch < 32) {
			LOG.debug("<< "+ch+": "+(int)ch);
			ch = ' ';
		} else if (ch =='?') {
			ch = '?';
		} else if (ch > 127) {
			ch = '?';
		} else if ((int)ch == 65533) { // default decoding error
			LOG.debug("decode "+ch+": "+(int)ch);
			ch = 127;
		}
		return ch;
	}

}
