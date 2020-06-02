package org.contentmine.ami.wordutil;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.contentmine.ami.dictionary.TermPhrase;
import org.contentmine.eucl.euclid.Util;

public class LuceneUtils {
	
	private static final Logger LOG = Logger.getLogger(LuceneUtils.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** convenience method for creating TokenStream from a String.
	 * (mainly to save me looking it up)
	 * calls tokenStream.reset() // seems to be necessary
	 * NEVER call this again (I think)
	 * 
	 * @param analyzer
	 * @param string
	 * @return
	 * @throws IOException
	 */
	public static TokenStream createTokenStreamQuietly(Analyzer analyzer, String string) {
		TokenStream tokenStream = null;
//		try {
			tokenStream = analyzer.tokenStream(null, new StringReader(string));
			LuceneUtils.resetTokenStreamQuietly(tokenStream);
//		} catch (IOException e) {
//			throw new RuntimeException("cannot create tokenStream", e);
//		}
		return tokenStream;
	}

	public static TokenStream createWhitespaceTokenStreamQuietly(String string) {
		Analyzer analyzer = new WhitespaceAnalyzer();
		TokenStream tokenStream = null;
		tokenStream = analyzer.tokenStream(null, new StringReader(string));
		LuceneUtils.resetTokenStreamQuietly(tokenStream);
		analyzer.close();
		return tokenStream;
	}

	/** convenience method to create list of unigram tokens 
	 * 
	 * uses Lucene WhitespaceAnalyzer
	 * 
	 * @param analyzer
	 * @param string
	 * @return
	 */
	public static List<String> whitespaceTokenize(String string) {
		WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
	    return tokenize(string, analyzer);
	}

	public static List<String> tokenize(String string, Analyzer analyzer) {
		List<String> result = new ArrayList<String>();
		try {
	      TokenStream stream = LuceneUtils.createTokenStreamQuietly(analyzer, string);
	      while (stream.incrementToken()) {
	        result.add(stream.getAttribute(CharTermAttribute.class).toString());
	      }
	    } catch (IOException e) {
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
		return result;
	}

	public static List<String> createShingleStream(String input, int min, int max, Analyzer analyzer) throws IOException {
		TokenStream tokenStream = LuceneUtils.createTokenStreamQuietly(analyzer, input);
		ShingleFilter shingleFilter = new ShingleFilter(tokenStream, min, max);
		CharTermAttribute charTermAttribute = shingleFilter.addAttribute(CharTermAttribute.class);
		shingleFilter.setOutputUnigrams(false); // no single words
		List<String> shingles = new ArrayList<String>();
		while (shingleFilter.incrementToken()) {
			shingles.add(charTermAttribute.toString());
		}
		shingleFilter.end();
		shingleFilter.close();
		tokenStream.close();
		return shingles;
	}

	/** tokenize and create Shingles
		 * uses Whitespace analyze
		 * @param input to tokenize
		 * @param min
		 * @param max
		 * @return
		 * @throws IOException
		 */
		public static List<String> createWhitespaceShingleStream(String input, int min, int max) throws IOException {
			WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
			return 	createShingleStream(input, min, max, analyzer);
	}

	/** have to reset() tokenStream after creating it - no idea why.
	 * this traps exception
	 * @param tokenStream
	 */
	public static void resetTokenStreamQuietly(TokenStream tokenStream) {
		try {
			tokenStream.reset();
		} catch (IOException e) {
			try {
				tokenStream.close();
			} catch (IOException e1) {
			}
			throw new RuntimeException("Cannot reset stream", e);
		}
	}

	/** extracts a list of Strings from  a tokenStream
	 * 
	 * @param tokenStream
	 * @return
	 */
	public static TermPhrase createPhraseFromTokenStream(TokenStream tokenStream) {
		return TermPhrase.createTermPhrase(createListFromTokenStream(tokenStream));
	}

	/** extracts a list of Strings from  a tokenStream
	 * 
	 * @param tokenStream
	 * @return
	 */
	public static List<String> createListFromTokenStream(TokenStream tokenStream) {
        CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class);
		List<String> transformedWords = new ArrayList<String>();
		try {
			while (tokenStream.incrementToken()) {
			    transformedWords.add(charTermAttr.toString());
			}
	        tokenStream.close();
		} catch (IOException e) {
			throw new RuntimeException("token stream failed", e);
		}
		return transformedWords;
	}

//	/** concatenates words, uses StandardTokenizer, creates a TokenStream, and returns a list of stemmed words.
//	 * 
//	 * @param currentWords
//	 * @return
//	 */
//	public static TermPhrase applyWhitespaceTokenizedLucenePorterStemming(String words) {
//		TermPhrase stemmedTerm = null;
//		if (words != null) {
//			List<String> splitWords = Arrays.asList(words.split("\\s+"));
//			stemmedTerm = LuceneUtils.applyWhitespaceTokenizedLucenePorterStemming(splitWords);
//		}
//	    return stemmedTerm;
//	}

	/** iterates over individual strings and returns a list of stemmed words.
	 * 
	 * @param words
	 * @return
	 */
	public static List<String> applyPorterStemming(List<String> words) {
		PorterStemmer porterStemmer = new PorterStemmer();
		List<String> tokenList = new ArrayList<String>();
		for (String word : words) {
			tokenList.add(porterStemmer.stem(word));
		}
	    return tokenList;
	}
	
	/** splits a (short) phrase and creates a stemmed Phrase.
	 * 
	 * @param phrase
	 * @return
	 */

	public static TermPhrase applyPorterStemming(String phrase) {
		List<String> tokenList = Arrays.asList(phrase.split("\\s+"));
		tokenList = LuceneUtils.applyPorterStemming(tokenList);
		return TermPhrase.createTermPhrase(tokenList);
	}

	public static List<String> createWhitespaceList(String string) {
		TokenStream tokenStream = LuceneUtils.createTokenStreamQuietly(new WhitespaceAnalyzer(), string);
		List<String> tokenList = LuceneUtils.createListFromTokenStream(tokenStream);
		return tokenList;
	}

	/** uses suffix to determine type of file
	 * 
	 * 		List<String> nonTexts = Arrays.asList(new String[] {
				"png", "jpg", "pdf", "bin", "doc", "docx","zip", "gz", "ppt", "pptx"});
		List<String> texts = Arrays.asList(new String[] {
				"txt", "xml", "svg", "json", "csv"});

	 * @param path
	 * @return
	 */
	public static boolean isTextFile(Path path) {
//		System.out.println(path+" | "+ Util.isBinaryPath(path)); // don't think this works
		List<String> nonTexts = Arrays.asList(new String[] {
				"bin", "doc", "docx", "gz", "html", "jpg", "pdf", "png", "ppt", "pptx", "zip"});
		List<String> texts = Arrays.asList(new String[] {
				"csv", "json", "log", "svg", "txt", "xml"});
		String extension = FilenameUtils.getExtension(path.toString());
		if (nonTexts.contains(extension)) {
			return false;
		}
		if (texts.contains(extension)) {
			return true;
		}
		if (true)throw new RuntimeException("Unknown suffix: "+extension);
		// defaults to false
		return false;
	}
	
	

//	/** concatenates words, uses StandardTokenizer, creates a TokenStream, and returns a list of stemmed words.
//	 * 
//	 * @param words
//	 * @return
//	 */
//	public static List<String> applyStandardTokenizedLucenePorterStemming(List<String> words) {
//		String input = StringUtils.join(words.iterator(), " ");
//	
//	    TokenStream tokenStream = new StandardTokenizer(new StringReader(input)); // this doesm't compile
//		LuceneUtils.resetTokenStreamQuietly(tokenStream);
//	    tokenStream = new PorterStemFilter(tokenStream);
//	
//	    List<String> tokenList = LuceneUtils.createListFromTokenStream(tokenStream);
//	    return tokenList;
//	}

}
