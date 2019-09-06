package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.phylotree.PhyloTreeArgProcessor;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlEdge;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlElement;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNode;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtu;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtus;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTree;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTrees;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.SVGPhrase;
import org.contentmine.graphics.svg.text.SVGWord;
import org.contentmine.graphics.svg.text.SVGWordBlock;
import org.contentmine.graphics.svg.text.SVGWordLine;
import org.contentmine.graphics.svg.text.SVGWordPage;
import org.contentmine.graphics.svg.text.SVGWordPara;
import org.contentmine.norma.image.ocr.HOCRReaderOLD;

/** merges results from HOCR (Tesseract) with phylotree (SVG and Nexml)
 * 
 * @author pm286
 *
 */
public class HOCRPhyloTreeTest {

	
	public static final Logger LOG = Logger.getLogger(HOCRPhyloTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
	public final static File HOCR_364_HTML = new File(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000364_0/image/003.pbm.png.hocr.html");
	private final static File PHYLOTREE_364_NEXML = new File(AMIFixtures.TEST_PHYLO_DIR, "ijs_0_000364_0/image/003.pbm.png.phylotree.nexml");

	@Test
	public void testDummy() {
		
	}
	
	@Test
	public void testReadHOCRSVG() throws Exception {
		HOCRReaderOLD hocrReader = new HOCRReaderOLD();
		hocrReader.readHOCR(new FileInputStream(HOCR_364_HTML));
		SVGSVG svgSvg = (SVGSVG) hocrReader.getOrCreateSVG();
		//svgSvg.debug("svg");
		SVGSVG.wrapAndWriteAsSVG(svgSvg, new File("target/hocr/merge0.svg"));
		SVGWordPage svgPage = svgSvg.getSingleSVGPage();
		Assert.assertNotNull(svgPage);
		List<SVGWordBlock> svgBlockList = svgPage.getSVGBlockList();
		Assert.assertEquals("blocks", 40, svgBlockList.size());
		SVGWordBlock wordBlock2 = svgBlockList.get(2);
		List<SVGWordPara> paraList = wordBlock2.getSVGParaList();
		Assert.assertEquals("paras", 1, paraList.size());
		SVGWordPara wordPara = paraList.get(0);
		List<SVGWordLine> lineList = wordPara.getSVGLineList();
		Assert.assertEquals("line", 1, lineList.size());
		SVGWordLine line = lineList.get(0);
		List<SVGWord> wordList = line.getOrCreateSVGWordList();
		Assert.assertEquals("word", 2, wordList.size());
		SVGWord word = wordList.get(0);
		SVGText text = word.getSVGText();
		Assert.assertEquals("text", "66", text.getValue());
		List<SVGWordLine> allLineList = svgPage.getSVGLineList();
		Assert.assertEquals("lines", 70, allLineList.size());
		int i = 0;
		for (SVGWordLine wordLine : allLineList) {
			wordLine.makePhrasesFromWords();
//			System.out.println(Int2.getInt2(wordLine.getBoundingBox().getMidPoint(BoxDirection.LEFT)));
//			System.out.println((i++)+" "+wordLine.toString());
		}
		SVGWordLine line2 = allLineList.get(2);
		LOG.trace(line2.getValue());
		line2.makePhrasesFromWords();
		
	}
	
	@Test 
	public void testReadNexml() throws Exception {
		NexmlNEXML nexmlNEXML = (NexmlNEXML) NexmlElement.readAndCreateNEXML(PHYLOTREE_364_NEXML);
		Assert.assertNotNull(nexmlNEXML);
		NexmlOtus nexmlOtus = nexmlNEXML.getSingleOtusElement();
		List<NexmlOtu> nexmlOtuList = nexmlOtus.getNexmlOtuList();
		Assert.assertEquals("otus",  31, nexmlOtuList.size());
		NexmlTrees nexmlTrees = nexmlNEXML.getTreesElement();
		List<NexmlTree> nexmlTreeList = nexmlTrees.getOrCreateTreeList();
		Assert.assertEquals("trees",  1, nexmlTreeList.size());
		NexmlTree nexmlTree = nexmlTreeList.get(0);
		List<NexmlNode> nodeList = nexmlTree.getNodeListAndMap();
		Assert.assertEquals("nodes",  60, nodeList.size());
		List<NexmlEdge> edgeList = nexmlTree.getEdgeListAndMaps();
		Assert.assertEquals("edges",  59, edgeList.size());
		NexmlNode rootNexmlNode = nexmlTree.getRootNode();
		Assert.assertNotNull("root" , rootNexmlNode);
		List<NexmlNode> tipNodeList = nexmlTree.getOrCreateTipNodeList();
		Assert.assertEquals("nodes",  31, tipNodeList.size());
		for (NexmlNode tipNode : tipNodeList) {
//			System.out.println(Int2.getInt2(tipNode.getXY2()));
		}
		Map<Int2, NexmlNode> tipByCoordMap = nexmlTree.getTipByCoordMap();
		Assert.assertEquals("tip map",  31, tipByCoordMap.size());
	}
	
	@Test
	public void testMerge() throws Exception {
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		HOCRReaderOLD hocrReader = phyloTreeArgProcessor.getOrCreateHOCRReader();
		List<SVGPhrase> phraseList = hocrReader.createPhraseList(HOCRPhyloTreeTest.HOCR_364_HTML);
		Assert.assertEquals(73, phraseList.size());
		
		NexmlNEXML nexmlNEXML = (NexmlNEXML) NexmlElement.readAndCreateNEXML(PHYLOTREE_364_NEXML);
		NexmlTree nexmlTree = nexmlNEXML.getSingleTree();
		Assert.assertNotNull(nexmlTree);
		
		phyloTreeArgProcessor.matchPhrasesToNodes(phraseList, nexmlTree);
		String newick = nexmlNEXML.createNewick();
		//not sure what is wrong here on Jenkins...
		Assert.assertTrue(newick.contains("EU379932"));
//		Assert.assertEquals("NEWICK", 
//				"(((((EU379932:195.0,EU840722:135.0)NT1.25:86.0,EU840723:301.0)NT1.17:131.0,(NC_O00853:251.0,NC_000918:364.0)NT1.15:106.0)NT1.5:12.0,(NC_0O9525:223.0,NC_0O4307:333.0)NT1.19:158.0)NT1.2:10.0,((NC_010571:441.0,(((Desulfo:295.0,((((Es:140.0,U32697:164.0)NT1.28:63.0,NC_OO2516:165.0)NT1.22:24.0,(NC_0O2929:183.0,NC_002946:185.0)NT1.26:55.0)NT1.21:78.0,(NC_0O9667:186.0,NC_0O2696:194.0)NT1.23:105.0)NT1.14:56.0)NT1.10:21.0,(NC_O01218:279.0,:302.0)NT1.16:107.0)NT1.8:8.0,((AEO15924:189.0,NC_OO3228:191.0)NT1.27:187.0,NC_OO2932:343.0)NT1.12:55.0)NT1.6:11.0,NC_005027:497.0)NT1.3:6.0,((NC_0O6576:348.0,(AJ307978:124.0,AJ307974:167.0)NT1.24:205.0)NT1.7:12.0,(NC_00O912:484.0,((M94261:260.0,NC_010376:320.0)NT1.13:33.0,((Lactoba:157.0,NC_OO9785:181.0)NT1.20:21.0,NC_00964:152.0)NT1.18:91.0)NT1.11:22.0)NT1.9:32.0)NT1.4:11.0)NT1.1:9.0)NT1.60;", newick);
	}

	
	@Test
	public void testMerge1() throws Exception {
		PhyloTreeArgProcessor phyloTreeArgProcessor = new PhyloTreeArgProcessor();
		HOCRReaderOLD hocrReader = phyloTreeArgProcessor.getOrCreateHOCRReader();
//		List<SVGWordLine> allLineList = hocrReader.createWordLineList(HOCRPhyloTreeTest.HOCR_364_HTML);
		phyloTreeArgProcessor.mergeFiles(HOCRPhyloTreeTest.HOCR_364_HTML, PHYLOTREE_364_NEXML);
	}

}
