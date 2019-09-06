package org.contentmine.ami.plugins.sequence;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.sequence.SequenceArgProcessor;
import org.contentmine.ami.plugins.sequence.SequencePlugin;
import org.contentmine.cproject.args.DefaultArgProcessor;

public class SequenceArgProcessorTest {
	
	private static final Logger LOG = Logger.getLogger(SequenceArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore // mend the test
	public void testSequenceArgProcessor() throws Exception {
		File newDir = new File("target/plosone/sequences/");
		FileUtils.copyDirectory(AMIFixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--sq.sequence --sq.length {6,20} --context 35 50 --sq.type dna prot -q "+newDir+" -i scholarly.html"; 
		DefaultArgProcessor sequenceArgProcessor = new SequenceArgProcessor(args);
		sequenceArgProcessor.runAndOutput();
		Assert.assertTrue("results dir: ", new File(newDir, "results").exists());
		Assert.assertTrue("sequences dir: ", new File(newDir, "results/sequence").exists());
		Assert.assertTrue("dna dir ", new File(newDir, "results/sequence/dna").exists());
		File dnaFile = new File(newDir, "results/sequence/dna/results.xml");
		Assert.assertTrue("dna file ", dnaFile.exists());
		Element dnaElement = new Builder().build(dnaFile).getRootElement();
		String dnaXml = dnaElement.toXML().replaceAll(DefaultArgProcessor.WHITESPACE, " ");
		Assert.assertEquals("dna file ", "<results title=\"dna\">"+
" <result pre=\"used by Sweet et al, 2010; (357F) (\" match=\"5’-CCTACGGGAGGCAGCAG-3’\" post=\") and (518R) (5’ATTACCGCGGCTGCTGG-3’), a segment o\" />"+
" <result pre=\"-CCTACGGGAGGCAGCAG-3’) and (518R) (\" match=\"5’ATTACCGCGGCTGCTGG-3’\" post=\"), a segment of the bacterial 16S rRNA gene was am\" />"+
" <result pre=\"ythell [ 18]; forward primer CilF (\" match=\"5’-TGGTAGTGTATTGGACWACCA-3’\" post=\") with a 36 bp GC clamp [ 31] attached to the 5’ e\" />"+
" <result pre=\" and the reverse primer CilDGGE-r (\" match=\"5’-TGAAAACATCCTTGGCAACTG-3’\" post=\"). PCR reaction mixtures were made up to 10 μl vol\" />"+
" <result pre=\"mples using the primers BrB-F-171 (\" match=\"5’-TCAAACCCGACTTTACGGAAG-3’\" post=\") and BrB-R-1721 (5’-TGCAGGTTCACCTACGGAAAC-3’) [ 3\" />"+
" <result pre=\"CGACTTTACGGAAG-3’) and BrB-R-1721 (\" match=\"5’-TGCAGGTTCACCTACGGAAAC-3’\" post=\") [ 34]. These primers were designed to amplify th\" />"+
" </results>",
		dnaXml);
		Assert.assertTrue("prot: ", new File(newDir, "results/sequence/prot").exists());
		File protFile = new File(newDir, "results/sequence/prot/results.xml");
		Assert.assertTrue("prot file ", protFile.exists());
		Element protElement = new Builder().build(protFile).getRootElement();
		String protXml = protElement.toXML().replaceAll(DefaultArgProcessor.WHITESPACE, " ");
		Assert.assertEquals("prot file ", "<results title=\"prot\" />", protXml);

	}
	
	@Test
	public void testSequenceHarness() throws Exception {
		// SHOWCASE
		String cmd = "--sq.sequence --context 35 50 --sq.type dnaprimer prot1 -q target/plosone/sequences/ -i scholarly.html"; 
		AMIFixtures.runStandardTestHarness(
				AMIFixtures.TEST_PLOSONE_SEQUENCE_0121780, 
				new File("target/plosone/sequences/"), 
				new SequencePlugin(),
				cmd,
				"sequence/dnaprimer/", "sequence/prot1/");
	}


}
