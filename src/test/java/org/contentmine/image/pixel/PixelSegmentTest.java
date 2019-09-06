package org.contentmine.image.pixel;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageAnalysisFixtures;
import org.junit.Assert;
import org.junit.Test;

public class PixelSegmentTest {

	private final static Logger LOG = Logger.getLogger(PixelSegmentList.class);

	private final static String EDGE_STRING = 
			"{(44,130)(43,129)(42,129)(41,128)(40,128)(39,127)(38,126)(37,126)(36,125)(35,125)(34,124)(33,124)(32,123)(31,122)(30,122)(29,121)(28,121)(27,120)(26,120)(25,119)(24,118)(23,118)(22,117)(21,117)(20,116)(19,115)(18,115)(17,114)(16,114)(15,113)(14,113)(13,112)(13,111)(13,110)(13,109)(13,108)(13,107)(13,106)(13,105)(13,104)(13,103)(13,102)(13,101)(13,100)(13,99)(13,98)(13,97)(13,96)(13,95)(13,94)(13,93)(13,92)(13,91)(13,90)(13,89)(13,88)(13,87)(13,86)(13,85)(13,84)(13,83)(13,82)(13,81)(13,80)(13,79)(13,78)(13,77)(13,76)(13,75)(14,74)(15,74)(16,73)(17,72)(18,72)(19,71)(20,71)(21,70)(22,69)(23,69)(24,68)(25,68)(26,67)(27,67)(28,66)(29,65)(30,65)(31,64)(32,64)(33,63)(34,63)(35,62)(36,61)(37,61)(38,60)(39,60)(40,59)(41,58)(42,58)(43,57)(44,57)(45,56)}/[(45,130)(45,56)]";

	@Test
	public void testSegmentFromPixelList() {
		PixelList pixelList = ImageAnalysisFixtures.CREATE_ZIGZAG_ISLAND().getPixelList();
		PixelSegmentList segmentList = PixelSegmentList.createSegmentList(pixelList, 1.0);
		Assert.assertEquals("segments", 5, segmentList.size());
		Assert.assertEquals("segments", "PixelSegmentList [segmentList=["
				+ "PixelSegment [line=line: from((0.0,0.0)) to((5.0,5.0)) v((5.0,5.0))], "
				+ "PixelSegment [line=line: from((5.0,5.0)) to((10.0,0.0)) v((5.0,-5.0))], "
				+ "PixelSegment [line=line: from((10.0,0.0)) to((15.0,5.0)) v((5.0,5.0))], "
				+ "PixelSegment [line=line: from((15.0,5.0)) to((20.0,0.0)) v((5.0,-5.0))], "
				+ "PixelSegment [line=line: from((20.0,0.0)) to((20.0,5.0)) v((0.0,5.0))]]]", 
				segmentList.toString());
	}
	
	@Test
	public void testSegmentFromEdge() {
		PixelIsland island = new PixelIsland();
		PixelEdge pixelEdge = island.createEdge(EDGE_STRING);
		PixelList pixelList = pixelEdge.getPixelList();
		PixelSegmentList segmentList = PixelSegmentList.createSegmentList(pixelList, 1.0);
		Assert.assertEquals("segments", 3, segmentList.size());
		Assert.assertEquals("segments", "PixelSegmentList [segmentList=["
				+ "PixelSegment [line=line: from((44.0,130.0)) to((13.0,112.0)) v((-31.0,-18.0))], "
				+ "PixelSegment [line=line: from((13.0,112.0)) to((13.0,75.0)) v((0.0,-37.0))], "
				+ "PixelSegment [line=line: from((13.0,75.0)) to((45.0,56.0)) v((32.0,-19.0))]]]", 
				segmentList.toString());
		
	}
	@Test
	public void testSegmentFromEdgeToSVG() {
		PixelIsland island = new PixelIsland();
		PixelEdge pixelEdge = island.createEdge(EDGE_STRING);
		PixelList pixelList = pixelEdge.getPixelList();
		PixelSegmentList segmentList = PixelSegmentList.createSegmentList(pixelList, 1.0);
		List<SVGLine> lineList = segmentList.getSVGLineList();
		Assert.assertEquals("lines", 3, lineList.size());
		Assert.assertTrue("line", new Real2(44.0, 130.0).isEqualTo(lineList.get(0).getXY(0), 0.001));
		SVGG g = new SVGG();
		g.setCSSStyle("stroke:red;stroke-width:1;");
		for (SVGElement line : lineList) {
			g.appendChild(line.copy());
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/segment/edge.svg"));
		
	}
}
