package org.contentmine.svg2xml.pdf;

import java.io.File;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Test;

/** make this an IT till it is solved.
 * 
 * @author pm286
 *
 */
public class SVGDocumentProcessorIT {
	private static final Logger LOG = Logger.getLogger(SVGDocumentProcessorIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testJoinSpans() throws Exception {
		File joinSpansHtmlFile = new File(new File(SVG2XMLFixtures.SVG2XML_DIR, "pdf"), "joinSpans.html");
		HtmlElement joinSpansElement = new HtmlFactory().parse(joinSpansHtmlFile);
	}
	
	@Test
	public void testJoinSpansInSVGPage() throws Exception {
		File page5SvgFile = new File(new File(SVG2XMLFixtures.SVG2XML_DIR, "pdf"), "fulltext-page.5.svg");
		SVGSVG page5Svg = (SVGSVG) SVGElement.readAndCreateSVG(page5SvgFile);
		SVGDocumentProcessor documentProcessor = new SVGDocumentProcessor();
		HtmlDiv htmlPage = documentProcessor.convertToHtml(page5Svg);
		File htmloutFile = new File(SVG2XMLFixtures.HTMLOUTDIR, "joins.html");
		XMLUtil.debug(htmlPage, htmloutFile, 1);
		Assert.assertTrue("outfile", htmloutFile.exists());

	}
	
	@Test
	public void testNormalizeUnicode() {
		String textSVG = "<text x='158.741,164.621,167.351,171.131,174.701,177.431,183.311,187.931,"
				+ "191.501,194.441,195.056,200.726,204.611,210.491,213.326,216.16,218.89,223.72,227.5,"
				+ "230.44,231.055,234.205,240.085,243.865,246.805,247.42,252.25,256.87,261.49,267.37,"
				+ "270.31,270.926,274.706,280.586,286.466,289.616,294.236,303.056,305.786,308.516,"
				+ "313.346,316.286,316.901,322.781,327.401,332.231,337.061,340.001,340.616,344.186,"
				+ "350.066,353.006,353.622,359.502,364.332,367.272,367.887,373.557,378.387,384.267,"
				+ "389.097,392.877,397.497,401.067,405.897,411.777,414.717,417.657,418.272,424.362,"
				+ "430.242,434.022,436.962,437.578,441.148,447.028,449.758,453.538' "
				+ "y='161.97199999999998' svg_rhmargin='456.478' "
				+ "style='fill:#000000;font-family:Galliard-Roman;font-size:10.5px;font-style:NORMAL;font-weight:normal;'>"
				+ "distinct proﬁ les for each subfamily have to be generated. For this "
				+ "</text>";
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(textSVG));
//		LOG.debug(text.toXML());
		String textString = text.getValue();
		LOG.debug("none" + textString + "/" + textString.length() );
		String textString0 = Normalizer.normalize(textString, Form.NFC);
		LOG.debug("NFC " + textString0 + "/" + textString0.length()+ "/" + Normalizer.isNormalized(textString, Form.NFC));
		textString0 = Normalizer.normalize(textString, Form.NFKC);
		LOG.debug("NFKC " + textString0 + "/" + textString0.length()+ "/" + Normalizer.isNormalized(textString, Form.NFKC));
		textString0 = Normalizer.normalize(textString, Form.NFD);
		LOG.debug("NFD " + textString0 + "/" + textString0.length()+ "/" + Normalizer.isNormalized(textString, Form.NFD));
		textString0 = Normalizer.normalize(textString, Form.NFKD);
		LOG.debug("NFKD " + textString0 + "/" + textString0.length()+ "/" + Normalizer.isNormalized(textString, Form.NFKD));
	}
}
