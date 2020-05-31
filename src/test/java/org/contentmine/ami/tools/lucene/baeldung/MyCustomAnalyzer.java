package org.contentmine.ami.tools.lucene.baeldung;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/** this will fail */
public class MyCustomAnalyzer extends Analyzer {

	public MyCustomAnalyzer() {
		super();
	}
    @Override
    protected TokenStreamComponents createComponents(String str) {
        TokenStream tokenStream = null;
        List<String> stringList = new ArrayList<>();
        tokenStream  = this.tokenStream(null, new StringReader(str));
        try {
			tokenStream.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			while (tokenStream.incrementToken()) {
			    stringList.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream,  EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new CapitalizationFilter(tokenStream);
        Consumer<Reader> src = null;
        Tokenizer tokenizer = null;
        new TokenStreamComponents(tokenizer, tokenStream);
		return new TokenStreamComponents(src , tokenStream);
    }

}