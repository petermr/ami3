package org.contentmine.ami.tools.lucene.demo;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.contentmine.ami.tools.lucene.LengthFilter;

/** from https://lucene.apache.org/core/8_0_0/core/org/apache/lucene/analysis/package-summary.html
 * 
 * @param args
 * @throws IOException
 */
public class JavadocAnalyzer extends Analyzer {

	/**
	In this example we will create a WhiteSpaceTokenizer and use a LengthFilter to suppress all words that have only two or fewer characters. The LengthFilter is part of the Lucene core and its implementation will be explained here to illustrate the usage of the TokenStream API.

	Then we will develop a custom Attribute, a PartOfSpeechAttribute, and add another filter to the chain which utilizes the new custom attribute, and call it PartOfSpeechTaggingFilter.

	Whitespace tokenization
	*/
	 
	   private Version matchVersion = Version.LUCENE_8_5_1;
	   
	   public JavadocAnalyzer(Version matchVersion) {
	     this.matchVersion = matchVersion;
	   }
	 
	   @Override
	   protected TokenStreamComponents createComponents(String fieldName) {
	     final Tokenizer source = new WhitespaceTokenizer();
	     TokenStream tokenStream = new LengthFilter(matchVersion, source, 3, Integer.MAX_VALUE);
//	       result = new PartOfSpeechTaggingFilter(result);
	        tokenStream = new LowerCaseFilter(tokenStream);
	        tokenStream = new StopFilter(tokenStream,  EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
	        tokenStream = new PorterStemFilter(tokenStream);
	        tokenStream = new CapitalizationFilter(tokenStream);

	     return new TokenStreamComponents(source, tokenStream);
	     
	   }
	   
	   public static void main(String[] args) throws IOException {
	     // text to tokenize
	     final String text = "This is a demo of the TokenStream API";
	     
	     Version matchVersion = Version.LUCENE_8_5_1; // Substitute desired Lucene version for XY
	     JavadocAnalyzer analyzer = new JavadocAnalyzer(matchVersion);
	     TokenStream stream = analyzer.tokenStream("field", new StringReader(text));
	     
	     // get the CharTermAttribute from the TokenStream
	     CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
	     // get the PartOfSpeechAttribute from the TokenStream
//	     PartOfSpeechAttribute posAtt = stream.addAttribute(PartOfSpeechAttribute.class);
	 
	     try {
	       stream.reset();
	     
	       // print all tokens until stream is exhausted
	       while (stream.incrementToken()) {
	           System.out.println(termAtt.toString() /*+ ": " + posAtt.getPartOfSpeech()*/);
	       }
	     
	       stream.end();
	     } finally {
	       stream.close();
	     }
	   }

  }

