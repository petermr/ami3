package org.contentmine.ami.tools.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.Version;

public abstract class FilteringTokenFilter extends TokenFilter {
	 
     private final PositionIncrementAttribute posIncrAtt = 
    		 addAttribute(PositionIncrementAttribute.class);
 
     /**
        * Create a new FilteringTokenFilter.
        * @param in            the TokenStream to consume
        */
     public FilteringTokenFilter(Version version, TokenStream in) {
         super(in);
     }
 
     /** Override this method and return if the current input token should be returned by incrementToken. */
     protected abstract boolean accept() throws IOException;
 
     @Override
     public final boolean incrementToken() throws IOException {
         int skippedPositions = 0;
         while (input.incrementToken()) {
             if (accept()) {
                 if (skippedPositions != 0) {
                     posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
                 }
                 return true;
             }
             skippedPositions += posIncrAtt.getPositionIncrement();
         }
         // reached EOS -- return false
         return false;
     }
 
     @Override
     public void reset() throws IOException {
         super.reset();
     }
 
 }
