package org.contentmine.norma;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.norma.Norma;
import org.contentmine.norma.pubstyle.util.RegionFinder;
import org.contentmine.svg2xml.page.PageCropper;
import org.contentmine.svg2xml.page.PageCropper.Units;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** creates complete workflow for extracting clipped tables.
 * 
 * @author pm286
 *
 */
public class TableClippingDemoTest {
	private static final String LANCET_BOX_FILL = "#b30838";
	public static final Logger LOG = Logger.getLogger(TableClippingDemoTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String FULLTEXT_PAGE = "fulltext-page";
	
	@Test
	@Ignore
	public void testGlobbing() throws IOException {
		File svgDir = new File("target/clipping/tracemonkey-pldi-09", "svg");
		Assert.assertTrue(""+svgDir+" is existing dir", svgDir.exists() && svgDir.isDirectory());
		CMineGlobber globber = new CMineGlobber();
		globber.setLocation(svgDir.toString());
		globber.setRegex(".*/fulltext\\-page.*\\.svg");
		List<File> fulltextFiles = globber.listFiles();
		LOG.debug(fulltextFiles);
		Assert.assertEquals(14, fulltextFiles.size());
	}
	
	@Test
	@Ignore
	public void testCropping() {
		PageCropper cropper = new PageCropper();
		cropper.setTLBRUserMediaBox(new Real2(0, 800), new Real2(600, 0));
		Assert.assertEquals("cropToLocalTransformation", 
			"(1.0,0.0,0.0,\n"
			+ "0.0,-1.0,800.0,\n"
			+ "0.0,0.0,1.0,)",
			cropper.getCropToLocalTransformation().toString());
		// clip a table - cropping coordinates, 
		cropper.setTLBRUserCropBox(new Real2(50, 467), new Real2(293, 242));
		String fileroot = FULLTEXT_PAGE+1;
		File svgDir = new File("target/clipping/tracemonkey-pldi-09/svg/");
		File inputFile = new File(svgDir, fileroot + ".svg");
		Assert.assertTrue(""+inputFile+" exists", inputFile.exists());
		SVGElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		List<SVGElement> descendants = cropper.extractDescendants(svgElement);
		Assert.assertEquals("contained ", 4287, descendants.size());
		SVGSVG.wrapAndWriteAsSVG(descendants, new File(new File("target/crop/"), fileroot+".raw.svg"));
		List<SVGElement> contained = cropper.extractContainedElements(descendants);
		Assert.assertEquals("contained ", 950, contained.size());
		SVGSVG.wrapAndWriteAsSVG(contained, new File(new File("target/crop/"), fileroot+".crop.svg"));
		
		/**
top: 117.3, left: 17.6, width: 85.6, height: 79.5
		 */
		cropper = new PageCropper();
		svgElement = SVGElement.readAndCreateSVG(inputFile);
		cropper.setSVGElementCopy(svgElement);
		double x0 = 117.3;
		double width = 85.6;
		double x1 = x0 + width;
		double y0 = 17.6;
		double height = 79.5;
		double y1 = y0 + height;
		svgElement = cropper.cropElementTLBR(new Real2(x0, y0), new Real2(x1, y1), Units.MM);
		Assert.assertNotNull(svgElement);
		SVGSVG.wrapAndWriteAsSVG(svgElement, new File(new File("target/crop/"), fileroot+".cropmmx.svg"));

	}
	
	@Test
	/** may move elsewhere later
	 * assumes SVG files have been created in target.
	 */
	@Ignore("missing files")
	public void testCroppingArguments() {
		File projectDir = new File("target/clipping/tracemonkey-pldi-09/");
		File svgDir = new File("target/clipping/tracemonkey-pldi-09/svg/");
		String fileroot = FULLTEXT_PAGE+1;
		File inputFile = new File(svgDir, fileroot + ".svg");
		Assert.assertTrue(""+inputFile+" exists", inputFile.exists());
//		SVGElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		/**
		double MM2PX = 72 / 25.4;
		double x0 = 17.6; // mm
		double width = 85.6; // mm
		double x1 = x0 + width;
//		double y0 = 117.3;
		double y0 = (800 / MM2PX) - 117.3; // coordinate system wrong way up // mm
		double height = 79.5; // mm
		double y1 = y0 - height;
		 */
		String cmd = "--project "+projectDir +
				" --cropbox x0 17.6 y0 117.3 width 85.6 height 79.5 ydown units mm "+
				" --page 1 "+
				" --mediabox x0 0 y0 0 width 600 height 800 ydown units px " +
				" --output svg/crop1.2.svg"
		;
		Norma norma = new Norma();
		norma.run(cmd);
	}
	
	@Test
	@Ignore // NYworking
	public void testGetBoxesByXPath() throws IOException {
		Norma norma = new Norma();

		File projectDir = new File(NormaFixtures.TEST_DEMOS_DIR, "lancet");
		File targetDir = new File("target/demos/lancet/");
		CMineTestFixtures.cleanAndCopyDir(projectDir, targetDir);
		norma.convertRawPDFToProjectToSVG(targetDir);
		
		RegionFinder regionFinder = new RegionFinder();
		 // lancet
		regionFinder.setOutputDir("target/clipping/lancet/");
		regionFinder.setRegionPathFill(LANCET_BOX_FILL);
		File[] ctreeDirectories = targetDir.listFiles();
		Assert.assertEquals(3,  ctreeDirectories.length);
		for (File ctreeDirectory : ctreeDirectories) {
			regionFinder.findRegions(ctreeDirectory);
		}
		
	}


}
