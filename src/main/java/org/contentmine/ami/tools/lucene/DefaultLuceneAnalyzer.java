package org.contentmine.ami.tools.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/** from https://lucene.apache.org/core/8_0_0/core/org/apache/lucene/analysis/package-summary.html
 * 
 * @param args
 * @throws IOException
 */
public class DefaultLuceneAnalyzer extends Analyzer {

	
	/**
	In this example we will create a WhiteSpaceTokenizer and use a 
	LengthFilter to suppress all words that have only two or fewer 
	characters. The LengthFilter is part of the Lucene core and its 
	implementation will be explained here to illustrate the usage of 
	the TokenStream API.

	Then we will develop a custom Attribute, a PartOfSpeechAttribute,
	 and add another filter to the chain which utilizes the new custom 
	 attribute, and call it PartOfSpeechTaggingFilter.

	Whitespace tokenization
	*/
	 
	   private Version matchVersion = null;
	   
	   public DefaultLuceneAnalyzer(Version matchVersion) {
	     this.matchVersion = matchVersion;
	   }
	 
	   @Override
	   protected TokenStreamComponents createComponents(String fieldName) {
	     final Tokenizer source = new WhitespaceTokenizer();
	     TokenStream tokenStream = new LengthFilter(matchVersion, source, 3, Integer.MAX_VALUE);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream,  EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new LengthFilter(tokenStream, 3, 8);
        tokenStream = new CapitalizationFilter(tokenStream);

	     return new TokenStreamComponents(source, tokenStream);
	     
	   }
	   
	   public static void main(String[] args) throws IOException {
	     final String text = "This is a demo of the TokenStream API";
	     DefaultLuceneAnalyzer analyzer = new DefaultLuceneAnalyzer(Version.LUCENE_8_5_1);
	     analyzer.tokenize(text);
	     analyzer.close();
	   }

	private void tokenize(final String text) throws IOException {
	     TokenStream stream = this.tokenStream("field", new StringReader(text));
	     CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
	 
	     try {
	       stream.reset();
	       while (stream.incrementToken()) {
	           System.out.println(termAtt.toString());
	       }
	       stream.end();
	     } finally {
	       stream.close();
	     }
	}




  }

