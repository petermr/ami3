package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.phylotree.PhyloTreeArgProcessor;
import org.contentmine.cproject.files.CTree;

@Ignore("requires tesseract")
public class PhyloArgProcessorTest {
	
	private static final Logger LOG = Logger.getLogger(PhyloArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testPhyloArgProcessor() throws Exception {
		File newDir = new File("target/ijsem/phylo");
	}
	
	@Test
	@Ignore // accesses net
	public void testPhyloArgProcessorLookup() throws Exception {
	}

	
	@Test
	/** very thick lines - behaves badly in thinning. But overall works.
	 * Needs 0- and 2-connected nodes merging...
	 * 
	 * 
	 * @throws Exception
	 */
	@Ignore("requires tesseract")
	public void testPhyloHarness() throws Exception {
		CTree cTree = new CTree(new File(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000174_0"));
		File normaTemp = new File("target/phylo/ijs_0_000174_0");
		cTree.copyTo(normaTemp, true);
		String cmd = "--ph.phylo -q target/phylo/ijs_0_000174_0 -i image/000.pbm.png -o target/phylo/junk.xml"; 
		PhyloTreeArgProcessor argProcessor = new PhyloTreeArgProcessor(cmd);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"mend me\">"
				);

	}

	@Test
	/** well behaved tree a few edge ends not joined to nodes, probably due to bad nodes.
	 * 
	 * @throws Exception
	 */
	@Ignore("requires tesseract") // uncomment later
	public void testCommandLine() throws Exception {
		String name = "ijs_0_000364_0"; String img = "003";
		CTree cTree = new CTree(new File(AMIFixtures.TEST_PHYLO_DIR, name));
		File normaTemp = new File("target/phylo/"+name);
		cTree.copyTo(normaTemp, true);
//		String cmd = "--ph.phylo -q target/phylo/"+name+" -i image/"+img+".pbm.png -o target/phylotest/"+name; 
		String cmd = "--ph.phylo -q target/phylo/"+name+" -i image/"+img+".pbm.png"; 
		PhyloTreeArgProcessor argProcessor = new PhyloTreeArgProcessor(cmd);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"mend me\">"
				);
	}

	@Test
	/** 
	 * development of new options in ami-phylo
	 * 
	 * @throws Exception
	 */
//	@Ignore("requires tesseract")
	public void testFullCommandLine() throws Exception {
		String name = "ijs_0_000364_0"; 
		String img = "003";
		CTree cTree = new CTree(new File(AMIFixtures.TEST_PHYLO_DIR, name));
		File normaTemp = new File("target/phylo/"+name);
		cTree.copyTo(normaTemp, true);
		String cmd = "--ph.phylo -q target/phylo/"+name+
				" -i image/"+img+".pbm.png"+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+img+".hocr.html"+
				" --ph.hocr.svg image/"+img+".hocr.svg"+
				" --ph.svg image/"+img+".svg"+
				" --ph.newick image/"+img+".nwk"+
				" --ph.nexml image/"+img+".nexml.xml"+
				"";
		PhyloTreeArgProcessor argProcessor = new PhyloTreeArgProcessor(cmd);
		argProcessor.runAndOutput();
//		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
//				"<results title=\"mend me\">"
//				);
	}

	@Test
//	@Ignore("tesseract")
	public void testEditLabels() throws IOException {
		File dir = AMIFixtures.TEST_PHYLO_DIR;
		String name = "ijs_0_000364_0"; 
		runExtraction(dir, name, "003");

	}

	@Test
	@Ignore("tesseract")
	public void testEditLabels1() throws IOException {
		runExtraction(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000174_0", "000");
		runExtraction(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000265_0", "000");
	}

	private void runExtraction(File dir, String name, String img) throws IOException,
			FileNotFoundException {
		File cTreeDir = new File(dir, name);
		Assert.assertTrue("file exists: "+cTreeDir, cTreeDir.exists());
		CTree cTree = new CTree(cTreeDir);
		File normaTemp = new File("target/phylo/"+name);
		cTree.copyTo(normaTemp, true);
		Assert.assertTrue("file exists: "+normaTemp, normaTemp.exists());
		String cmd = "--ph.phylo -q target/phylo/"+name+
				" -i image/"+img+".pbm.png"+
				" --log"+
				" --ph.specpattern ijsemSpeciesEditor.xml"+
				" --ph.hocr.html image/"+img+".hocr.html"+
				" --ph.hocr.svg image/"+img+".hocr.svg"+
				" --ph.nexml image/"+img+".nexml.xml"+
				"";
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor(cmd);
		phyloTreeArgProcessor.runAndOutput();
	}

	


	
}
