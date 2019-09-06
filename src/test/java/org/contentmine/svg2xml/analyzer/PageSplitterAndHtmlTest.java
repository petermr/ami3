package org.contentmine.svg2xml.analyzer;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

public class PageSplitterAndHtmlTest {

	private final static Logger LOG = Logger.getLogger(PageSplitterAndHtmlTest.class);
	
	@Test
	public void testTransformChunksToXML() {
		Element svg = SVGElement.readAndCreateSVG(SVG2XMLFixtures.SVG_AJC_PAGE6_SPLIT_SVG);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		Assert.assertEquals("chunks", 14, gList.size());
	}
	
	
	/*@Test
	public void testTransformChunksToXMLAndAnalyzeText0() {
		Element svg = SVGElement.readAndCreateSVG(Fixtures.SVG_AJC_PAGE6_SPLIT_SVG);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		TextAnalyzerX textAnalyzer = (TextAnalyzerX) AbstractAnalyzer.createSpecificAnalyzer(gList.get(0));
		List<TextLine> textLines = textAnalyzer.getLinesInIncreasingY();
		Assert.assertEquals("lines0 ", 1, textLines.size());
		TextLine textLine = textLines.get(0);
		Element element = textLine.createHtmlLine();
		Assert.assertEquals("lines0 ", "380", element.getValue());
	}
	
	@Test
	public void testTransformChunksToXMLAndAnalyzeText1() {
		testSingleLineInChunk(1, "G. Moad, E. Rizzardo and S. H. Thang");
	}

	@Test
	public void testTransformChunksToXMLAndAnalyzeText4() {
		testSingleLineInChunk(4, "Scheme 1.");
	}
	
	@Test
	public void testTransformChunksToXMLAndAnalyzeText6() {
		testSingleLineInChunk(6, "Scheme 2. Reversible deactivation.");
	}
	
	@Test
	public void testTransformChunksToXMLAndAnalyzeText8() {
		testSingleLineInChunk(8, "Scheme 3. Reversible chain transfer.");
	}*/
	
	@Test
	@Ignore
	//Needs revisiting
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple2() {
		Element ref = XMLUtil.parseXML(
                "<div xmlns='http://www.w3.org/1999/xhtml'>\n"+
                "<p>\n"+
                "<span style='font-size:7.0px;font-family:Helvetica;'>Initiation</span>\n"+
                "</p>\n"+
                "<p>\n"+
                "<span style='font-size:7.0px;font-family:Helvetica;'>Termination</span>\n"+
                "</p>\n"+
                "<p>\n"+
                "<span style='font-size:7.0px;font-family:Helvetica;'>P</span>\n"+
                "<sub>\n"+
                "<span style='font-size:5.25px;font-style:italic;font-family:Helvetica;'>n</span>\n"+
                "</sub>\n"+
                "</p>\n"+
                "</div>\n"
				);
		testMultipleLineInMixedChunk(2, 4, ref);
	}
	
	@Test
	@Ignore 
	//Needs revisiting
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple3() {
		Element ref = XMLUtil.parseXML("<p xmlns='http://www.w3.org/1999/xhtml'>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>Monomer</span>" +
				"</p>");
		testMultipleLineInMixedChunk(3, 1, ref);
	}
	
	@Test
	@Ignore
	//Needs revisiting
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple5() {
		Element ref = XMLUtil.parseXML("" +
				"<div xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"<sub>" +
				"<span style='font-size:5.25px;font-style:italic;font-family:Helvetica;'>n</span>" +
				"</sub>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;'>+ </span>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>X</span>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;' />" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"<sub>" +
				"<span style='font-size:5.25px;font-style:italic;font-family:Helvetica;'>n</span>" +
				"</sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>X</span>" +
				"</p>" +
				"<p>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>Monomer</span>" +
				"</p>" +
				"</div>" +
				"");
				testMultipleLineInMixedChunk(5, 3, ref);
	}
	
	@Test
	@Ignore
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple7() {
		Element ref = XMLUtil.parseXML("" +
				"<div xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>" +
				"<sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"</sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"<sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>X</span>" +
				"</sub>" +
				"<sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P </span>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;'>+</span>" +
				"</sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"<sub>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;'>+</span>" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"</sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>X P </span>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;'>+</span>" +
				"<sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"</sup>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;'>+</span>" +
				"<sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>X</span>" +
				"</sup>" +
				"<sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P </span>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;'>+</span>" +
				"</sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"<sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>X</span>" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:7.0px;color:red;font-family:MathematicalPi-One;'>+</span>" +
				"</sup>" +
				"<sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>P</span>" +
				"</sup>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>X</span>" +
				"<sub>" +
				"<span style='font-size:5.25px;font-style:italic;font-family:Helvetica;'>n</span>" +
				"</sub>" +
				"<span style='font-size:7.0px;font-family:Helvetica;'>Monomer Monomer</span>" +
				"</p>" +
				"</div>" +
				"");
		testMultipleLineInMixedChunk(7, 10, ref);
	}
	

	/** 
	 * Graph annotations (vertical text not yet treated)
	 */
	@Test
	@Ignore
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple9() {
		Element ref = XMLUtil.parseXML("" +
				"<div xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>t h g</span>" +
				"<sub>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>i</span>" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>g</span>" +
				"</sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>i e w</span>" +
				"<sub>" +
				"<span style='font-size:8.0px;font-family:Helvetica;' />" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>w</span>" +
				"</sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;' />" +
				"<sub>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>r</span>" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;' />" +
				"</sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>r a</span>" +
				"<sub>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>l</span>" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>a</span>" +
				"</sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>l u c e</span>" +
				"<sub>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>l</span>" +
				"</sub>" +
				"<sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>e</span>" +
				"</sup>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>l o M 0 20 40 60 80 100</span>" +
				"</p>" +
				"<p>" +
				"<span style='font-size:8.0px;font-family:Helvetica;'>Conversion [%]</span>" +
				"</p>" +
				"</div>" +
				"");
		testMultipleLineInMixedChunk(9, 18, ref);
	}

	/** 
	 * Figure caption
	 */
	@Test
	@Ignore
	//Needs revisiting
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple10() {
		Element ref = XMLUtil.parseXML("" +
				"<div xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;'>Fig. 1. </span>" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>Evolution</span>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;' />" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>of</span>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;' />" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>molecular</span>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;' />" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>weight</span>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;' />" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>with</span>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;' />" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>monomer</span>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;' />" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>conversion</span>" +
				"<span style='font-size:8.468px;font-style:bold;font-family:TimesNewRoman;' />" +
				"<span style='font-size:8.468px;font-family:TimesNewRoman;'>for a conventional radical polymerization with constant rate of initiation (��� ��� ���) and a living radical polymerization ( ).</span>" +
				"</p>" +
				"</div>" +
				"");
		testMultipleLineInMixedChunk(10, 3, ref);
	}
	
	/** 
	 * Figure caption
	 */
	@Test
	@Ignore
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple11() {
		Element ref = XMLUtil.parseXML("" +
				"<div xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>the synthesis of blocks, stars, or other polymers of complex architecture. New materials that have the potential of revolutionizing a large part of the polymer industry are beginning to appear. Possible applications range from novel surfactants, dispersants, coatings, and adhesives, to biomaterials, membranes, drug delivery media, and materials for microelectronics.</span>" +
				"</p>" +
				"<p>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>To understand how RAFT and other forms of living radical polymerization work, we first need to consider the mechanism of the conventional process.</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>[1]</span>" +
				"</sup>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>Radical polymerization is a chain reaction. The chains are initiated by radicals (formed from an initiator) adding to monomer. Chain propagation then involves the sequential addition of monomer units to the radical (P</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;font-family:Times-Roman;'>���</span>" +
				"</sup>" +
				"<sub>" +
				"<span style='font-size:7.472px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"</sub>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>) so formed. Chain termination occurs when the propagating radicals react by combination or disproportionation. A much simplified mechanism is shown in Scheme 1.</span>" +
				"</p>" +
				"<p>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>In conventional radical polymerization, the steady-state concentration of propagating species is about 10</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;color:red;font-family:MTSYN;'>"+(char)8722+" </span>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>7</span>" +
				"</sup>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>M, and individual chains grow for 5���10 s before terminating. Chains are continuously formed, propagate, and are terminated by radical���radical reaction. The molecular weight of chains formed in the early stages of polymerization is high and will reduce with conversion because of monomer depletion (Fig. 1). The breadth of the molecular-weight distribution and polydispersity is governed by statistical factors. The polydispersity, expressed in terms of the ratio of weight to number average molecular weights,</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;color:red;font-family:MTSYN;'>"+(char)8727+"</span>" +
				"</sup>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>is broad (</span>" +
				"<span style='font-size:9.963px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>w</span>" +
				"</sub>" +
				"<span style='font-size:9.963px;color:red;font-style:italic;font-family:MTMI;'>/</span>" +
				"<span style='font-size:9.963px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>n</span>" +
				"</sub>" +
				"<span style='font-size:9.963px;color:red;font-style:italic;font-family:MTMI;'>&gt; </span>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>1</span>" +
				"<span style='font-size:9.963px;color:red;font-style:italic;font-family:MTMI;'>.</span>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>5; see Fig. 2).</span>" +
				"</p>" +
				"<p>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>In an ideal living polymerization, all chains are initiated at the beginning, grow at the same rate, and survive the polymerization (there is no termination). The propensity of</span>" +
				"</p>" +
				"</div>" +
				"");
		testMultipleLineInMixedChunk(11, 37, ref);
	}

	/** 
	 * Figure caption
	 */
	@Test
	@Ignore
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple12() {
		Element ref = XMLUtil.parseXML("" +
				"<div xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>free radicals to undergo radical���radical termination means that, for the case of radical polymerization, all chains cannot be simultaneously active. To confer living character on a radical polymerization, it is necessary to suppress or render insignificant all processes that terminate chains irreversibly. Thus, living radical polymerization only becomes possible in the presence of reagents that react with the propagating radicals (P</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;font-family:Times-Roman;'>���</span>" +
				"</sup>" +
				"<sub>" +
				"<span style='font-size:7.472px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"</sub>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>) by reversible deactivation (Scheme 2) or reversible chain transfer (Scheme 3) so that the majority of chains are maintained in a dormant form (P</span>" +
				"<sub>" +
				"<span style='font-size:7.472px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"</sub>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>X). The average concentration of the active propagating species in a living radical polymerization may be similar to that for the conventional process although the cumulative lifetime of an individual chain as an active species will be lower. Rapid equilibration between the active and dormant forms ensures that all chains possess an equal chance for growth and that all chains will grow, albeit intermittently. Under these conditions, the molecular weight increases linearly with conversion (Fig. 1) and the molecular weight distribution can be very narrow (e.g. </span>" +
				"<span style='font-size:9.963px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>w</span>" +
				"</sub>" +
				"<span style='font-size:9.963px;color:red;font-style:italic;font-family:MTMI;'>/</span>" +
				"<span style='font-size:9.963px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>n</span>" +
				"</sub>" +
				"<span style='font-size:9.963px;color:red;font-family:MTSYN;'>"+(char)8776+" </span>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>1</span>" +
				"<span style='font-size:9.963px;color:red;font-style:italic;font-family:MTMI;'>.</span>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>1,</span>" +
				"<span style='font-size:9.963px;color:red;font-family:MTSYN;' />" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>Fig.</span>" +
				"<span style='font-size:9.963px;color:red;font-family:MTSYN;' />" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>2).</span>" +
				"</p>" +
				"<p>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>The living radical polymerization techniques that have recently received greatest attention are nitroxide-mediated polymerization (NMP), atom-transfer radical polymerization (ATRP), and reversible addition���fragmentation chain transfer (RAFT). The NMP technique was devised in our laboratories in the early 1980s,</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>[10]</span>" +
				"</sup>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>and in recent years has been exploited extensively for the synthesis of narrow molecular-weight distribution homopolymers and block copolymers of styrene and acrylates.</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>[2,11,12]</span>" +
				"</sup>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>Recent developments have made NMP applicable to a wider, though still restricted, range of monomers.</span>" +
				"<sup>" +
				"<span style='font-size:7.472px;font-family:TimesNewRoman;'>[2]</span>" +
				"</sup>" +
				"<span style='font-size:9.963px;font-family:TimesNewRoman;'>ATRP is substantially</span>" +
				"</p>" +
				"</div>" +
				"");
		testMultipleLineInMixedChunk(12, 38, ref);
	}
	
	/** 
	 * Figure caption
	 */
	@Test
	@Ignore
	public void testTransformChunksToXMLAndAnalyzeTextLinesinMultiple13() {
		Element ref = XMLUtil.parseXML("" +
				"<div xmlns='http://www.w3.org/1999/xhtml'>" +
				"<p>" +
				"<sup>" +
				"<span style='font-size:5.978px;color:red;font-family:MTSYN;'>"+(char)8727+"</span>" +
				"</sup>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>The n</span>" +
				"<sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>umber av</span>" +
				"<sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>erage m</span>" +
				"<sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>olecul</span>" +
				"<sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>ar weight or molar mass is simply the total weight of the sample divided by the number of molecules in the sample:</span>" +
				"</p>" +
				"<p>" +
				"<sup>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>The n</span>" +
				"</sup>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"<sup>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>umber av</span>" +
				"</sup>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"<sup>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>erage m</span>" +
				"</sup>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"<sup>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>olecul</span>" +
				"</sup>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+"</span>" +
				"<sup>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>ar weight or molar mass is simply the total weight of the sample divided by the number of molecules in the sample:</span>" +
				"</sup>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-family:TimesNewRoman;'>n</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTSYN;'>= </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-style:italic;font-family:MTMI;'>/ </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTSYN;'>= </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>w</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-style:italic;font-family:MTMI;'>/ </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>, where </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>is the number of chains of length </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>, </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>w</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>is the weight of chains of length </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>, and </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>is the molecular weight of a chain of length </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>. The weight average molecular weight is the sum of the weights of chains of each molecular weight multiplied by their molecular</span>" +
				"</p>" +
				"<p>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+" "+(char)8721+" "+(char)8721+"</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-family:TimesNewRoman;'>2</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTEX;'>"+(char)8721+" weight divided by the total weight of the sample: </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-family:TimesNewRoman;'>w</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTSYN;'>= </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>w</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-style:italic;font-family:MTMI;'>/ </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>w</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;color:red;font-family:MTSYN;'>= </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sup>" +
				"<span style='font-size:5.978px;font-family:TimesNewRoman;'>2</span>" +
				"</sup>" +
				"<span style='font-size:7.97px;color:red;font-style:italic;font-family:MTMI;'>/ </span>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>n</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-style:italic;font-family:TimesNewRoman;'>M</span>" +
				"<sub>" +
				"<span style='font-size:5.978px;font-style:italic;font-family:TimesNewRoman;'>i</span>" +
				"</sub>" +
				"<span style='font-size:7.97px;font-family:TimesNewRoman;'>. The weight average is always greater than the number average molecular weight. The polydispersity is the ratio of the weight average to the number average molecular weight and, for an ideal radical polymerization, will be 2 for termination by disproportionation or chain transfer, or 1.5 for termination by combination.</span>" +
				"</p>" +
				"</div>" +
				"");
		testMultipleLineInMixedChunk(13, 13, ref);
	}
	

	//====================================================================
	
	/*private void testSingleLineInChunk(int chunk, String expected) {
		// note this has been split already
		Element svg = SVGElement.readAndCreateSVG(Fixtures.SVG_AJC_PAGE6_SPLIT_SVG);
		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
		TextAnalyzerX textAnalyzer = (TextAnalyzerX) AbstractAnalyzer.createSpecificAnalyzer(gList.get(chunk));
		List<TextLine> textLines = textAnalyzer.getLinesInIncreasingY();
		Assert.assertEquals("line"+chunk, 1, textLines.size());
		TextLine textLine = textLines.get(0);
		Element element = textLine.createHtmlLine();
		Assert.assertEquals("line"+chunk, expected, element.getValue());
	}*/

	private void testMultipleLineInMixedChunk(int chunk, int nlines, Element ref) {
		Element svg = SVGElement.readAndCreateSVG(SVG2XMLFixtures.SVG_AJC_PAGE6_SPLIT_SVG);
		PageSplitterAndHtmlTest.analyzeChunkInSVGPage(chunk, nlines, ref, svg);
	}

	public static void analyzeChunkInSVGPage(int chunk, int nlines, Element ref, Element svg) {
//		List<SVGElement> gList = SVGG.generateElementList(svg, "svg:g/svg:g/svg:g[@edge='YMIN']");
//		SVGElement g = gList.get(chunk);
//		if (!(g instanceof SVGG)) {
//			throw new RuntimeException("BUG: g should be SVGG");
//		}
//		PageAnalyzer pageAnalyzer = new PageAnalyzer((SVGSVG)g);
//		MixedAnalyzer mixedAnalyzer = (MixedAnalyzer) pageAnalyzer.createSpecificAnalyzer(g);
//		LOG.trace("MixedAnalyzer "+mixedAnalyzer);
//		TextAnalyzer textAnalyzer = mixedAnalyzer.getTextAnalyzer();
//		LOG.trace("TextAnalyzer "+textAnalyzer);
//		List<TextLine> textLines = textAnalyzer.getLinesInIncreasingY();
//		for (TextLine textLine : textLines) {
//			LOG.trace(textLine);
//		}
//		Assert.assertEquals("lines"+chunk, nlines, textLines.size());
//		Element element = textAnalyzer.getTextStructurer().createHtmlElement();
//		LOG.trace(ref.toXML()+"\n\n"+element.toXML());
//		TestUtils.assertEqualsIncludingFloat("chunk"+chunk, ref, element, true, 0.001);
//		try {
//			Nodes nodes = element.query(".//@style");
//			for (int i = 0; i < nodes.size(); i++) {
//				nodes.get(i).detach();
//			}
//			File file = new File("target/");
//			file.mkdirs();
//			SVGUtil.debug(element, new FileOutputStream("target/chunk"+chunk+".html"), 1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
