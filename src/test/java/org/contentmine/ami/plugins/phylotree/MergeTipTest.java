package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.lookups.ENALookup;
import org.contentmine.ami.plugins.phylotree.PhyloTreeArgProcessor;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

//@Ignore("problematic in Jenkins; uncomment for testing")
public class MergeTipTest {

	static final String PNG = ".png";

	static final String PBM_PNG = ".pbm.png";

	static final String X15GOODTREE = "target/phylo/combined/15goodtree/";

	public static final Logger LOG = LogManager.getLogger(HOCRPhyloTreeTest.class);
static String[] ROOTS = {
			"ijs.0.000174-0-000",
			"ijs.0.000364-0-004",
			"ijs.0.000406-0-000",
			"ijs.0.001362-0-002",
			"ijs.0.001420-0-000",
			"ijs.0.001537-0-001",
			"ijs.0.001966-0-000",
			"ijs.0.002048-0-001",
			"ijs.0.002113-0-000",
			"ijs.0.002220-0-000",
			"ijs.0.002444-0-000",
			"ijs.0.002469-0-000",
			"ijs.0.002618-0-001",
			"ijs.0.003228-0-001",
			"ijs.0.003723-0-000",
	};
	
	public final static Pattern IJSEM = Pattern.compile(""
			// probably leading garbage due to lines
//			+ "(?:([0-9]+[^~]*)~)*"
			// genus
//			+ "(?:(‘?[A-Z](?:[a-z]{2,}|[a-z]?\\.))~)"
			+ "\\s*(‘?[A-Z](?:[a-z]{2,}|[a-z]?\\.))"
			// species
			+ "\\s*([a-z]+’?)"
			// strain source
//			+ "(?:(?:(ATCC|DSM|HHQ|IFO|IMSNU|LMG|NBRC|NCDO|NCIMB|NRRL|YIM)~)?)"
			// strain
//			+ "(?:([A-Z0-9\\-]+T?)~?)"
			+ "\\s*.*\\s*"
			// ENA ID
			+ "\\(([^\\)]+)\\).*");



	@Test
	//FIXME // fails because of old Diagram Analyzer
	@Ignore
	public void testConvertPngToSemanticFiles() throws Exception {
		PhyloTreeArgProcessor.convertPngToHTML_SVG_NEXML_NWK(
			new File(NAConstants.TEST_AMI_DIR+"/phylo/15goodtree/ijs.0.000174-0-000.pbm.png"),
			new File("target/phylo/misc"));
	}

	@Test
	@Ignore
	public void testNearlyCorrect1420() throws Exception {
		String root = "ijs.0.001420-0-000";
		readAndCombineTopsAndLabels(root, new File("target/phylo/combined/15goodtree/"));
	}

	@Test
	@Ignore // FIXME uses old Diagram Analyzer
	public void testSplitPhrases364() throws Exception {
		String root = "ijs.0.000364-0-004";
		File combined = new File("target/phylo", "combined");
		readAndCombineTopsAndLabels(root, new File("target/phylo/combined/15goodtree/"));
	}

	@Test
	@Ignore("uses tesseract")
	public void testMerge() throws IOException, InterruptedException {
		File imageFile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/ijs.0.000174-0-000.pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) return;
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		XMLUtil.debug(nexml, new File("target/phylo/ijs.0.000174-0-000.xml"), 1);
	}
	
	@Test
	@Ignore("uses tesseract") // change debug to trace for committal
	// FIXME deal with spaces in IDs
	public void testLookup() throws IOException, InterruptedException {
		File imageFile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/ijs.0.000364-0-004.pbm.png");
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) return;
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		List<Element> nodes = XMLUtil.getQueryElements(nexml, "//*[local-name()='otu']");
		List<String> ids = new ArrayList<String>();
		for (Element node : nodes) {
			String value = node.getValue();
//			LOG.debug(value);
			Matcher matcher = IJSEM.matcher(value);
			if (matcher.matches()) {
				String genus = matcher.group(1);
				String species = matcher.group(2);
				String id = matcher.group(3);
				LOG.debug(genus+" + "+species+" + "+id);
				ids.add(id);
			}
		}
		String s = new ENALookup().lookupGenbankIds(ids);
		LOG.debug(">"+s);
	}
	
	@Test
	@Ignore
	public void testUnrootedEdge() throws Exception {
		File inputDir = new File(AMIFixtures.TEST_PHYLO_DIR, "problems1");
		Assert.assertTrue("problems1", inputDir.exists());
		testProblem00(inputDir, new File("target/phylo/problems1/"), "ijs.0.64938-0-000");
	}

	@Test
	@Ignore("fix later")
	public void testProblem2() throws Exception {
		testProblem0("ijs.0.65219-0-002");
	}

	// ===========================
	
	

	private void testProblem0(String root) throws Exception {
		File inputDir = new File(AMIFixtures.TEST_PHYLO_DIR, "problems/");
		File outputDir = new File("target/phylo/problems/");
		testProblem00(inputDir, outputDir, root);
	}

	@Test
	@Ignore // stack overflow
	public void testProblem() throws Exception {
		File imageFile = new File(AMIFixtures.TEST_PHYLO_DIR, "problems/ijs.0.65219-0-001.pbm.png");
		Assert.assertTrue("image file exists: "+imageFile, imageFile.exists());
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		phyloTreeArgProcessor.setSpeciesPatternInputString("ijsemSpeciesEditor.xml");
		if (phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) {
			NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
			XMLUtil.debug(nexml, new File("target/phylo/ijs.0.65219-0-001.xml"), 1);
		}
	}

	// =========================================
	private boolean readAndCombineTopsAndLabels(String root, File outputDir) throws IOException,
	InterruptedException, FileNotFoundException {
		File infile = new File(AMIFixtures.TEST_PHYLO_DIR, "15goodtree/"+root+PBM_PNG);
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		phyloTreeArgProcessor.setOutputRoot(root);
		phyloTreeArgProcessor.setOutputDir(outputDir);
		phyloTreeArgProcessor.setSpeciesPatternInputString("ijsemSpeciesEditor.xml");
		new File(X15GOODTREE).mkdirs();
		phyloTreeArgProcessor.createCTreeLog(new File(X15GOODTREE+"log.xml"));
		if (!phyloTreeArgProcessor.mergeOCRAndPixelTree(infile)) return false;
		NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
		XMLUtil.debug(nexml, new FileOutputStream(X15GOODTREE+root+".nexml.xml"), 1);
		FileUtils.write(new File(X15GOODTREE+root+".nwk"), nexml.createNewick());
		XMLUtil.debug(nexml.createSVG(), new FileOutputStream(X15GOODTREE+root+".svg"), 1);
		return true;
	}

	static void testProblem00(File dir, File outputDir, String root) throws Exception {
			File imageFile = new File(dir, root+".pbm.png");
			PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
			phyloTreeArgProcessor.setSpeciesPatternInputString("ijsemSpeciesEditor.xml");
			if (phyloTreeArgProcessor.mergeOCRAndPixelTree(imageFile)) {
				NexmlNEXML nexml = phyloTreeArgProcessor.getNexml();
				outputDir.mkdirs();
	//			FileUtils.write(new File(outputDir, root+".nwk"), nexml.createNewick());
				XMLUtil.debug(nexml.createSVG(), new File(outputDir, root+".svg"), 1);
				XMLUtil.debug(nexml, new File(outputDir, root+".xml"), 1);
			} else {
				LOG.error("failed to create tree");
			}
		}


}
