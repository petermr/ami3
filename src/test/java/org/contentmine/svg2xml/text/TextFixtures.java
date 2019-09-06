package org.contentmine.svg2xml.text;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.text.line.StyleSpans;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.container.ScriptContainerTest;
import org.junit.Assert;

public class TextFixtures {

	public final static File ACS_072516_6_4SB_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "acs072516.chunk6.4Sb.svg");
	public final static File ACS_072516_6_5SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "acs072516.chunk6.5Sa.svg");
	public final static File AJC_01182_2_5SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "ajc01182.chunk2.5Sa.svg");
	public final static File BMC_174_1_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.page1.svg");
	public final static File BMC_174_1_6_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.page1.6.svg");
	public final static File BMC_174_1_8_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.page1.8.svg");
	public final static File BMC_174_4_3_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.page4.3.svg");
	public final static File BMC_174_5_2_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.page5.2.svg");
	public final static File BMC_174_5_3SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.chunk5.3Sa.svg");
	public final static File BMC_174_6_3SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.chunk6.3Sa.svg");
	public final static File BMC_174_6_4_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.page6.4.svg");
	public final static File BMC_174_9_3_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc174.page9.3.svg");
	public final static File BMC_312_1_4SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk1.4Sa.svg");
	public final static File BMC_312_1_7DA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk1.7Da.svg");
	public final static File BMC_312_1_10SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk1.10Sa.svg");
	public final static File BMC_312_2_4SC_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk2.4Sc.svg");
	public final static File BMC_312_6_0SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.0Sa.svg");
	public final static File BMC_312_6_0SA0_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.0Sa0.svg");
	public final static File BMC_312_6_0SA1_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.0Sa1.svg");
	public final static File BMC_312_6_1SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.1Sa.svg");
	public final static File BMC_312_6_3SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.3Sa.svg");
	public final static File BMC_312_6_4SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.4Sa.svg");
	public final static File BMC_312_6_4SB_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.4Sb.svg");
	public final static File BMC_312_6_4SB3_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk6.4Sb3.svg");
	public final static File BMC_312_12_7SB_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "bmc312.chunk12.7Sb.svg");
	public final static File MDPI_27_4_1SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "mdpi27.chunk4.1Sa.svg");
	public final static File MDPI_27_4_1SA0_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "mdpi27.chunk4.1Sa0.svg");
	public final static File MDPI_27_18_7SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "mdpi27.chunk18.7Sa.svg");
	
	public final static File NPG_00788_5_3SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "npg00778.chunk5.3Sa.svg");
	public final static File NPG_00778_5_4SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "npg00778.chunk5.4Sa.svg");


	public final static File PEERJ_50_12_6SB_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "peerj50.chunk12.6Sb.svg");
	public final static File RSC_B306241d_6_8SA_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "rscb306241d.chunk6.8Sa.svg");
	
	public final static File BMC_312MULT_1_0SA_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "chunk1.0Sa.svg");
	public final static File BMC_312MULT_1_1PA_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "chunk1.1Pa.svg");
	public final static File BMC_312MULT_1_2DA_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "chunk1.2Da.svg");
	public final static File BMC_312MULT_1_3SA_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "chunk1.3Sa.svg");
	public static final String[][] BMC_312MULT_1_0_HTML = {
		{"Hiwatashi ", "<I>et al</I>", ". ", "<I>BMC Evolutionary Biology </I>", "2011, ", "<B>11</B>", ":312"}, 
		{"http://www.biomedcentral.com/1471-2148/11/312"}	
	};
	public static final String[][] BMC_312MULT_1_1_HTML = {};
	public static final String[][] BMC_312MULT_1_2_HTML = {
		{"<B>RESEARCH ARTICLE Open Access</B>"}
	};
	
	public static final File BMC_312MULT_8_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "page8.svg");
	public static final File BMC_312MULT_8_0_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "chunk8.0Sa.svg");
	public static final File BMC_312MULT_8_1_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "chunk8.1Sa.svg");	
	public static final File BMC_312MULT_8_2_SVG = new File(SVG2XMLFixtures.MULTIPLE312_DIR, "chunk8.2Da.svg");	
	
	public static final String[][] BMC_312MULT_8_0_HTML = {
		{"Hiwatashi ", "<I>et al</I>", ". ", "<I>BMC Evolutionary Biology </I>", "2011, ", "<B>11</B>", ":312"}, 
		{"http://www.biomedcentral.com/1471-2148/11/312"}	
	};
	public static final String[][] BMC_312MULT_8_1_HTML = {{"Page 8 of 14"}};	
	public static final String[][] BMC_312MULT_8_2_HTML = {
		{" Hpi L"}, {"A"},	{" Ssy L"}, {"89"}, {" Hla L"},
		{"99"}, {" Hag L"},	{"95"}, {" Nle L"}, {" Human L"},
		{" Macaque L"}, {" Hpi M"},	{" Ssy M"}, {"92"}, {" Hag M"},
		{"99"}, {" Hla M"},	{"95"}, {" Nle M"}, {" Human M"},
		{" Macaque M"}, {" Mouse M"},	{"0.01"}, {" Hag L"}, {"B"}, {" Hla L"},
		{" Hpi L"}, {"81"},	{" Hag M"}, {" Hla M"}, {" Hpi M"},
		{"100"}, {" Ssy L"},	{"99"}, {" Ssy M"}, 
		{" Nle L"},	{"95"},  {" Nle M"}, {" Human L"}, {" Human M"},
		{"0.005"},  
		{"<B>Figure 3 </B>", "<B>The among-group phylogenetic trees of exons (A) and introns (B) of the L/M opsin genes of gibbons</B>", ". (A) The combined"},	
		{"sequences of exons 3, 4 and 5 are considered. The human L and M, the crab-eating macaque L and M, and the mouse M opsin gene"}, 
		{"sequences are included. (B) The combined intron 3 and 4 sequences are considered. The human L and M opsin gene sequences are included."}, 
		{"Bootstrap values over 80% are indicated at the branch nodes. Scale bars indicate the number of nucleotide substitution per site. Hag, ", "<I>H. agilis</I>", ";"},
		{"Hla, ","<I>H. lar</I>","; Hpi, ","<I>H. pileatus</I>","; Nle, ","<I>N. leucogenys</I>","; Ssy, ","<I>S. syndactylus</I>",".",}, 
	};	
	// ==========================================================================================
	private static final Logger LOG = Logger.getLogger(TextFixtures.class);
	
	
	public static void testSpans(String[][] values, File file) {
		List<StyleSpans> styleSpansList = ScriptContainerTest.getStyleSpansList(file);
		Assert.assertEquals("lists", values.length, styleSpansList.size());
		for (int i = 0; i < values.length; i++) {
			StyleSpans styleSpans = styleSpansList.get(i);
			if (values[i].length > 0) {
				if (values[i].length != styleSpans.size()) {
					for (int j = 0; j < styleSpans.size(); j++) {
						LOG.trace(styleSpans.get(j).toString());
					}
				}
				Assert.assertEquals("line"+i, values[i].length, styleSpans.size());
				for (int j = 0; j < values[i].length; j++) {
					try {
						Assert.assertEquals("line"+i+","+j, values[i][j], styleSpans.get(j).toString());
					} catch (RuntimeException e) {
						throw e;
					}
				}
			}
		}
	}
	
	public static void testSpans(String[][][] valuesArray, File[] files) {
		Assert.assertEquals("files", valuesArray.length, files.length);
		for (int i = 0; i < valuesArray.length; i++) {
			testSpans(valuesArray[i], files[i]);
		}
	}
	
	public static void testPage(String[][][] valuesArray, File file) {
//		Assert.assertEquals("files", valuesArray.length, files.length);
//		for (int i = 0; i < valuesArray.length; i++) {
//			testSpans(valuesArray[i], files[i]);
//		}
	}

	public static SVGSVG createSVG(File svgFile) {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
		return svg;
	}
	
}
