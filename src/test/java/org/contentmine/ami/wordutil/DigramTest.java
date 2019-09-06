package org.contentmine.ami.wordutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.word.WordCollectionFactory;
import org.contentmine.norma.NAConstants;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class DigramTest {

	
	private static final Logger LOG = Logger.getLogger(DigramTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final File TESSERACTED_DIR = new File("examples/peterijsem/tesseracted/");
	private static final String TESSERACTED = ""+NAConstants.SLASH_AMI_RESOURCE+"/word/peterijsem/tesseracted/";
	private static String PNG_TXT =TESSERACTED+"pngs.txt";
	private List<String> files;
	private List<NGramGenerator> ngramGeneratorList;
	private int[] ngramsLength = {
			1191,
			1643,
			1367,
			334,
			2467,
			686,
			752,
			1270,
			1168,
			757,
			};
	@Before
	public void setup() throws IOException {
		setup1();
//		setup2();
	}

	private void setup1() throws IOException {
		InputStream is = this.getClass().getResourceAsStream(PNG_TXT);
		if (is == null) {
			throw new RuntimeException("Cannot find "+PNG_TXT);
		}
		files = IOUtils.readLines(is);
		ngramGeneratorList = new ArrayList<NGramGenerator>();
		for (String filename : files) {
			InputStream fileis = this.getClass().getResourceAsStream(TESSERACTED+filename);
			NGramGenerator ngramGenerator = new NGramGenerator();
			ngramGenerator.read(fileis);
			ngramGeneratorList.add(ngramGenerator);
		}
	}
	
	private void setup2() throws IOException {
		List<File> txtFiles = new ArrayList<File>(FileUtils.listFiles(TESSERACTED_DIR, new String[]{"txt"}, false));
		ngramGeneratorList = new ArrayList<NGramGenerator>();
		for (File txtFile : txtFiles) {
			InputStream fileis = new FileInputStream(txtFile);
			NGramGenerator ngramGenerator = new NGramGenerator();
			ngramGenerator.read(fileis);
			ngramGeneratorList.add(ngramGenerator);
		}
	}
	
	@Test
	public void testRead() {
		Assert.assertEquals("ngramsList", 10, ngramGeneratorList.size());
		int i = 0;
		for (NGramGenerator generator : ngramGeneratorList) {
			//System.out.println(generator.size());
//			Assert.assertEquals("ngram", ngramsLength[i++], generator.size());
		}
	}
	
	
	@Test
	public void testDigrams() {
		List<String> digrams = ngramGeneratorList.get(0).getNGrams(2);
		//System.out.println(digrams);
		Multiset<String> digramSet = HashMultiset.create();
		for (String digram : digrams) {
			digramSet.add(digram);
		}
		//System.out.println(digramSet);
	}
	
	@Test
	public void testManyDiagrams() {
		Multiset<String> digramSet = HashMultiset.create();
		for (NGramGenerator generator : ngramGeneratorList) {
			List<String> digrams0 = generator.getNGrams(3);
			for (String digram0 : digrams0) {
				digram0 = digram0.replaceAll("[\\p{Punct}\\s]", "~");
				digram0 = digram0.replaceAll("~~", "~");
				if (digram0.toString().contains("?")) {
					continue;
				}
				if (digram0.contains(""+(char)65533)) {
					continue;
				}
				if (containsUnusual(digram0)) {
					LOG.trace(digram0);
				}
				if (inBlacklist(digram0)) continue;
				digramSet.add(digram0);
			}
		}
		Iterable<Multiset.Entry<String>> sortedSet = WordCollectionFactory.getEntriesSortedByCount(digramSet);
		for (Entry<String> entry : sortedSet) {
			String vv = entry.getElement();
			if (vv.contains("?")) {
				LOG.trace(vv);
			}
		}
		//System.out.println(sortedSet);
	}

	private boolean containsUnusual(String ss) {
		for (int i = 0; i < ss.length(); i++) {
			char ch = ss.charAt(i);
			if (!Character.isAlphabetic(ch) && !Character.isDigit(ch) && ch != '~' && ch != ' ') {
				LOG.trace(">"+(char)ch+">"+(int)ch);
				return true;
			}
		}
		return false;
	}

	private boolean inBlacklist(String digram0) {
		Pattern BLACKLIST = Pattern.compile("(000|~0?0|0?0~|~0~|0~0|~~|~\\?\\?|\\?\\?\\?)");
		return BLACKLIST.matcher(digram0).matches();
	}
}
