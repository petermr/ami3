package org.contentmine.ami.dictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.dictionary.TermPhrase;
import org.contentmine.ami.wordutil.LuceneUtils;
import org.junit.Assert;
import org.junit.Test;

/** multiword phrases in dictionary.
 * 
 * @author pm286
 *
 */
public class MultiwordTest {

	private static final Logger LOG = Logger.getLogger(MultiwordTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static File MULTIWORD_SYNBIO = new File(DefaultAMIDictionary.SYNBIO_DIR, "synbio.xml");
	public final static String EXAMPLE = ""
			+ "This is rubbish for a bacterial article about an "
			  + "artificial gene with a biological circuit and some dna synthesis with a riboswitch for photosynthesis";


	@Test
	public void testReadMultiwordDictionary() {
		DefaultAMIDictionary dictionary = new DefaultAMIDictionary();
		dictionary.readDictionary(MULTIWORD_SYNBIO);
		Assert.assertEquals("synbio", 71, dictionary.size());
	}
	

	@Test
	public void testReadMultiwordDictionaryStrings() {
		DefaultAMIDictionary dictionary = DefaultAMIDictionary.createSortedDictionary(MULTIWORD_SYNBIO);
		List<DictionaryTerm> dictionaryTerms = dictionary.getDictionaryTermList();
		Assert.assertEquals("synbio", 71, dictionary.size());
		Assert.assertEquals("synbio", 
			"["
			+ "[bacterial], "
			+ "[gene], "
	        + "[microfluidics], "
	        + "[photosynthesis], "
	        + "[photosynthetic], "
	        + "[riboswitch], "
	        + "[artificial, cell], "
	        + "[artificial, gene], "
	        + "[artificial, genome], "
	        + "[artificial, nucleotide], "
	        + "[artificial, sequence], "
	        + "[artificial, shRNA], "
	        + "[biological, circuit], "
	        + "[biological, parts], "
	        + "[cell, chassis], "
	        + "[computational, design], "
	        + "[dna, assembly], "
	        + "[dna, nanostructure], "
	        + "[dna, origami], "
	        + "[dna, synthesis], "
	        + "[gene, oscillator], "
	        + "[gene, switch], "
	        + "[gene, synthesis], "
	        + "[genetic, circuit], "
	        + "[genome, engineering], "
	        + "[metabolic, engineering], "
	        + "[metabolomic, model], "
	        + "[minimal, cell], "
	        + "[minimal, genome], "
	        + "[molecular, machine], "
	        + "[multiple, amplification], "
	        + "[multiplex, gene], "
	        + "[multiplex, ligation], "
	        + "[rational, design], "
	        + "[rna, design], "
	        + "[rna, nanostructure], "
	        + "[signalling, pathway], "
	        + "[synthetic, biology], "
	        + "[synthetic, cell], "
	        + "[synthetic, genetic], "
	        + "[synthetic, genome], "
	        + "[synthetic, nucleotide], "
	        + "[synthetic, protocell], "
	        + "[synthetic, sequence], "
	        + "[synthetic, shRNA], "
	        + "[systems, biology], "
	        + "[transcriptomic, model], "
	        + "[vesicle, bioreactor], "
	        + "[vesicular, bioreactor], "
	        + "[RNA, folding, model], "
	        + "[RNA, folding, prediction], "
	        + "[artificial, amino, acid], "
	        + "[artificial, base, pair], "
	        + "[artificial, gene, network], "
	        + "[artificial, nucleic, acids], "
	        + "[computational, protein, design], "
	        + "[gene, circuit, design], "
	        + "[heterologous, nucleic, acid], "
	        + "[noncanonical, amino, acid], "
	        + "[protein, folding, model], "
	        + "[protein, folding, prediction], "
	        + "[rational, protein, design], "
	        + "[synthetic, amino, acid], "
	        + "[synthetic, base, pair], "
	        + "[synthetic, gene, cluster], "
	        + "[synthetic, gene, network], "
	        + "[synthetic, nucleic, acids], "
	        + "[synthetic, regulatory, network], "
	        + "[systems, biology, model], "
	        + "[unnatural, amino, acid], "
	        + "[de, novo, enzyme, design]"
	        + "]",
        dictionaryTerms.toString());
		Assert.assertEquals(4, dictionary.getMaximumTermSize());
		
	}

	@Test
	public void testStemMultiwordDictionary() {
		DefaultAMIDictionary dictionary = DefaultAMIDictionary.createSortedDictionary(MULTIWORD_SYNBIO);
		List<TermPhrase> stemmedWordsList = new ArrayList<TermPhrase>();
		List<DictionaryTerm> multiwordTerms= dictionary.getDictionaryTermList();
		for (DictionaryTerm multiwordTerm : multiwordTerms) {
			TermPhrase termPhrase = multiwordTerm.getStemmedTermPhrase();
			stemmedWordsList.add(termPhrase);
		}
		Assert.assertEquals("stemmed phrases", ""
		+ "["
		+ "[bacteri], "
		+ "[gene], "
        + "[microfluid], "
        + "[photosynthesi], "
        + "[photosynthet], "
        + "[riboswitch], "
        + "[artifici, cell], "
        + "[artifici, gene], "
        + "[artifici, genom], "
        + "[artifici, nucleotid], "
        + "[artifici, sequenc], "
        + "[artifici, shRNA], "
        + "[biolog, circuit], "
        + "[biolog, part], "
        + "[cell, chassi], "
        + "[comput, design], "
        + "[dna, assembl], "
        + "[dna, nanostructur], "
        + "[dna, origami], "
        + "[dna, synthesi], "
        + "[gene, oscil], "
        + "[gene, switch], "
        + "[gene, synthesi], "
        + "[genet, circuit], "
        + "[genom, engin], "
        + "[metabol, engin], "
        + "[metabolom, model], "
        + "[minim, cell], "
        + "[minim, genom], "
        + "[molecular, machin], "
        + "[multipl, amplif], "
        + "[multiplex, gene], "
        + "[multiplex, ligat], "
        + "[ration, design], "
        + "[rna, design], "
        + "[rna, nanostructur], "
        + "[signal, pathwai], "
        + "[synthet, biologi], "
        + "[synthet, cell], "
        + "[synthet, genet], "
        + "[synthet, genom], "
        + "[synthet, nucleotid], "
        + "[synthet, protocel], "
        + "[synthet, sequenc], "
        + "[synthet, shRNA], "
        + "[system, biologi], "
        + "[transcriptom, model], "
        + "[vesicl, bioreactor], "
        + "[vesicular, bioreactor], "
        + "[RNA, fold, model], "
        + "[RNA, fold, predict], "
        + "[artifici, amino, acid], "
        + "[artifici, base, pair], "
        + "[artifici, gene, network], "
        + "[artifici, nucleic, acid], "
        + "[comput, protein, design], "
        + "[gene, circuit, design], "
        + "[heterolog, nucleic, acid], "
        + "[noncanon, amino, acid], "
        + "[protein, fold, model], "
        + "[protein, fold, predict], "
        + "[ration, protein, design], "
        + "[synthet, amino, acid], "
        + "[synthet, base, pair], "
        + "[synthet, gene, cluster], "
        + "[synthet, gene, network], "
        + "[synthet, nucleic, acid], "
        + "[synthet, regulatori, network], "
        + "[system, biologi, model], "
        + "[unnatur, amino, acid], "
        + "[de, novo, enzym, design]"
		+ "]"
        , stemmedWordsList.toString());
	}
	
	@Test
	public void testRawTermSearch() {
		List<String> wordList = LuceneUtils.createWhitespaceList(EXAMPLE);
		DefaultAMIDictionary dictionary = DefaultAMIDictionary.createSortedDictionary(MULTIWORD_SYNBIO);
		List<DictionaryTerm> dictionaryTerms = dictionary.getDictionaryTermList();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < wordList.size(); i++) {
			for (int j = dictionaryTerms.size() - 1; j >= 0; j--) {
				DictionaryTerm dictionaryTerm = dictionaryTerms.get(j);
				if (dictionaryTerm.match(wordList, i)) {
					sb.append("; ("+i+")"+dictionaryTerm);
					break;
				}
			}
		}
		Assert.assertEquals(""
				+ "; (5)[bacterial];"
				+ " (9)[artificial, gene];"
				+ " (10)[gene];"
				+ " (13)[biological, circuit];"
				+ " (17)[dna, synthesis];"
				+ " (21)[riboswitch];"
				+ " (23)[photosynthesis]", sb.toString());
	}

}
