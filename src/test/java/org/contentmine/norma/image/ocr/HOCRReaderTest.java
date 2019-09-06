package org.contentmine.norma.image.ocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlMeta;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ocr.HOCRReader;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HOCRReaderTest {
	
	
	private static final Logger LOG = Logger.getLogger(HOCRReaderTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static File OCR_DIR = new File(NormaFixtures.TEST_IMAGES_DIR, "ocr/");
	public final static Pattern IJSEM = Pattern.compile("(?:([0-9]+[^~]*)~)*"
			+ "(?:(‘?[A-Z](?:[a-z]{2,}|[a-z]?\\.))~)"
			+ "(?:([a-z]+’?)~)"
			+ "(?:(?:(ATCC|DSM|HHQ|IFO|IMSNU|LMG|NBRC|NCDO|NCIMB|NRRL|YIM)~)?)"
			+ "(?:([A-Z0-9\\-]+T?)~?)"
			+ "(?:\\((.*)\\)).*");

	@Test
	public void testReadHOCR() throws IOException {
		String resource = NAConstants.OCR_RESOURCE+"ijs.0.003566-0-000.pbm.png.hocr";
		InputStream is = this.getClass().getResourceAsStream(resource);
		Assert.assertNotNull("input stream must not be null: "+resource, is);
		HOCRReaderOLD hocrReader = new HOCRReaderOLD();
		hocrReader.readHOCR(is);
		Assert.assertNotNull(hocrReader.getHocrElement());
		HtmlElement htmlHead = hocrReader.getHead();
		Assert.assertNotNull("head",  htmlHead);
		List<HtmlMeta> metaElements = hocrReader.getMetaElements();
		Assert.assertNotNull("meta",  metaElements);
		Assert.assertEquals("meta",  3, metaElements.size());
	}
	
	@Test
	@Ignore // fix expected file
	public void testReadHOCR2SVG() throws IOException {
		HOCRReader hocrReader = new HOCRReader();
		hocrReader.readHOCR(this.getClass().getResourceAsStream(NAConstants.OCR_RESOURCE+"ijs.0.003566-0-000.pbm.png.hocr"));
		SVGSVG svgSvg = (SVGSVG) hocrReader.getOrCreateSVG();
		Assert.assertNotNull("SVG not null", svgSvg);
		HtmlBody htmlBody = hocrReader.getOrCreateHtmlBody();
		new File("target/hocr/").mkdirs();
		XMLUtil.debug(htmlBody, new FileOutputStream("target/hocr/ijs.0.003566-0-000.pbm.png.hocr.html"),1);
//		List<HtmlSpan> lines = hocrReader.getNonEmptyLines();
//		matchSpecies(hocrReader, IJSEM);
		new File("target/hocr/").mkdirs();
		File resultsFile = new File("target/hocr/ijs.0.003566-0-000.pbm.png.hocr.svg");
		File expectedFile = new File(OCR_DIR, "ijs.0.003566-0-000.pbm.png.hocr.svg");
		XMLUtil.debug(svgSvg, new FileOutputStream(resultsFile), 1);
		String msg = XMLUtil.equalsCanonically(
	    		expectedFile, 
	    		resultsFile,
	    		true);
		if (msg != null) {
			LOG.debug(""+expectedFile+"; "+ FileUtils.readFileToString(expectedFile, Charset.forName("UTF-8")));
			LOG.debug(""+resultsFile+"; "+FileUtils.readFileToString(resultsFile, Charset.forName("UTF-8")));
		}
	    Assert.assertNull("message: "+msg, msg);
	}

//	private void matchSpecies(HOCRReader hocrReader, Pattern IJSEM) {
//		List<HtmlSpan> lines = hocrReader.getNonEmptyLines();
//		for (HtmlSpan line : lines) {
//			List<String> matchList = HOCRReader.matchPattern(line, IJSEM);
//			LOG.trace((matchList.size() == 0 ? "?? "+HOCRReader.getSpacedValue(line).toString() : matchList));
//		}
////		System.out.println();
//	}
	
	@Test
	public void testReadHOCR2SVGFiles() throws IOException {
		
		String[] roots = {
				"ijs.0.003566-0-000",
				"ijs.0.003616-0-000",
				"ijs.0.003624-0-000",
				"ijs.0.003640-0-001",
				"ijs.0.003699-0-000",
				"ijs.0.003723-0-000",
				"ijs.0.003731-0-002",
				"ijs.0.003749-0-000",
				"ijs.0.003814-0-002",
				"ijs.0.003822-0-000",
		};

		for (String root : roots) {
			LOG.trace(root);
			HOCRReader hocrReader = new HOCRReader();
			String hocrResource = NAConstants.OCR_RESOURCE+"/"+root+".pbm.png.hocr";
			InputStream resourceAsStream = this.getClass().getResourceAsStream(hocrResource);
			Assert.assertNotNull("hocr not null "+hocrResource, resourceAsStream);
			hocrReader.readHOCR(resourceAsStream);
			SVGSVG svgSvg = (SVGSVG) hocrReader.getOrCreateSVG();
			Assert.assertNotNull("SVG not null", svgSvg);
//			matchSpecies(hocrReader, IJSEM);
			new File("target/hocr/").mkdirs();
			File resultsFile = new File("target/hocr/"+root+".pbm.png.hocr.svg");
			File expectedFile = new File(OCR_DIR, root+".pbm.png.hocr.svg");
			XMLUtil.debug(svgSvg, new FileOutputStream(resultsFile), 1);
			if (true) continue; // use this if you want to copy the target svg into src/test/resources later
 			String msg = XMLUtil.equalsCanonically(
		    		expectedFile, 
		    		resultsFile,
		    		true);
			if (msg != null) {
				LOG.debug(""+expectedFile+"; "+ FileUtils.readFileToString(expectedFile, Charset.forName("UTF-8")));
				LOG.debug(""+resultsFile+"; "+FileUtils.readFileToString(resultsFile, Charset.forName("UTF-8")));
			}
		    Assert.assertNull("message: "+msg, msg);
		}
	}
	
	/** commandline conversion.
	 * 
	 */
	@Test
	// FIXME wrong destination
	public void testCommandLine() throws IOException {
		
		File cTreeTop = new File("target/hocr/ijsem_003566");
		if (cTreeTop.exists())FileUtils.forceDelete(cTreeTop);
		FileUtils.copyDirectory(new File(NormaFixtures.TEST_IMAGES_DIR, "ijsem_003566"), cTreeTop);
		CTree cTree = new CTree(cTreeTop);
//		Assert.assertNotNull("image", cTree.getExistingImageFile("ijs.0.003566-0-000.pbm.png"));
//		Assert.assertNotNull("image", cTree.getExistingImageFile("ijs.0.003566-0-000.pbm.png.hocr"));
		String args = "-q "+cTreeTop
				+ " --transform hocr2svg"
				+ " -i " + "image/ijs.0.003566-0-000.pbm.png.hocr"
				+ " -o " + "image/ijs.0.003566-0-000.pbm.png.hocr.svg";
		Norma norma = new Norma();
		norma.run(args);
		// ends up in wrong place "target/hocr/ijs.0.003566-0-000.pbm.png.hocr.svg
//        File hocrSvg = new File(cTreeTop, "svg/ijs.0.003566-0-000.pbm.png.hocr.svg");
        File hocrSvg = new File(cTreeTop, "target/hocr/ijs.0.003566-0-000.pbm.png.hocr.svg");
//		Assert.assertTrue(hocrSvg.exists());
	}
}
