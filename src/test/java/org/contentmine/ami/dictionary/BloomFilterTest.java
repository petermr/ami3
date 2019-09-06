package org.contentmine.ami.dictionary;

import org.junit.Test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

public class BloomFilterTest {

   
   Funnel<String> stringFunnel = new Funnel<String>() {
	   public void funnel(String person, PrimitiveSink into) {
	       into.putUnencodedChars(person);
	   }
	 };
private BloomFilter<String> f;




	@Test
	public void runTest() {
		int expectedInsertions = 10;
		f = BloomFilter.create(stringFunnel, expectedInsertions);
		f.put("Junk");
		f.put("M");
		for (int i = 96; i <= 122; i++) {
			String s = String.valueOf((char) i);
			f.put(s);
		}
		
		printMightContain("fred");
		printMightContain("Junk");
		for (int i = 65; i <= 90; i++) {
			String s = String.valueOf((char) i);
			printMightContain(s);
		}
	}
	
	private void printMightContain(String s) {
//		System.out.println(s+": mightContain: "+f.mightContain(s)+" "+f.expectedFpp());
	}
	
}
