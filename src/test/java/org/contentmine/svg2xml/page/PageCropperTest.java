package org.contentmine.svg2xml.page;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.page.PageCropper.Units;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** tests cropping of pages
 * 
 * @author pm286
 *
 */
public class PageCropperTest {
	private static final Logger LOG = Logger.getLogger(PageCropperTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testMaterialsCrop() {
		PageCropper cropper = new PageCropper();
		cropper.setTLBRUserMediaBox(new Real2(0, 800), new Real2(600, 0));
		Assert.assertEquals("cropToLocalTransformation", 
			"(1.0,0.0,0.0,\n"
			+ "0.0,-1.0,800.0,\n"
			+ "0.0,0.0,1.0,)",
			cropper.getCropToLocalTransformation().toString());
		// clip a table - cropping coordinates, 
		cropper.setTLBRUserCropBox(new Real2(30, 580), new Real2(570, 310));
		String fileroot = "materials-05-00027-page7";
		File inputFile = new File(SVG2XMLFixtures.MDPI_DIR, fileroot + ".svg");
		Assert.assertTrue(""+inputFile+" exists", inputFile.exists());
		SVGElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		List<SVGElement> descendants = cropper.extractDescendants(svgElement);
		Assert.assertEquals("contained ", 2315, descendants.size());
		SVGSVG.wrapAndWriteAsSVG(descendants, new File(new File("target/crop/"), "materials-05-00027-page7.raw.svg"));
		List<SVGElement> contained = cropper.extractContainedElements(descendants);
		Assert.assertEquals("contained ", 995, contained.size());
		SVGSVG.wrapAndWriteAsSVG(contained, new File(new File("target/crop/"), "materials-05-00027-page7.crop.svg"));
	}
	
	@Test
	public void testMaterialsCropToElement() throws Exception {
		String fileroot = "materials-05-00027-page7";
		File inputFile = new File(SVG2XMLFixtures.MDPI_DIR, fileroot + ".svg");
		
		PageCropper cropper = new PageCropper();
		cropper.readSVG(inputFile);
		cropper.setTLBRUserMediaBox(new Real2(0, 800), new Real2(600, 0));
		cropper.setTLBRUserCropBox(new Real2(30, 580), new Real2(570, 320));
		// just for display
		cropper.displayCropBox(new File(new File("target/crop/"), fileroot + ".raw.box.svg"));
		cropper.detachElementsOutsideBox();
		Assert.assertEquals("contained ", 992, cropper.extractDescendants(cropper.getSVGElement()).size());
		SVGSVG.wrapAndWriteAsSVG(cropper.getSVGElement(), new File(new File("target/crop/"), fileroot + ".crop2.svg"));
	}
	
	@Test
	public void testBioCropToElement() throws Exception {
		String fileroot = "nature-page3";
		File inputFile = new File(SVG2XMLFixtures.BIO_DIR, fileroot + ".svg");
		Real2 tl = new Real2(30, 750);
		Real2 br = new Real2(150, 650);
		
		PageCropper cropper = new PageCropper();
		SVGElement svgElement = cropper.cropFile(fileroot, inputFile, tl, br);
		SVGSVG.wrapAndWriteAsSVG(svgElement, new File(new File("target/crop/bio/"), fileroot + ".crop2.svg"));
		
	}
	
	@Test
	@Ignore
	public void testCroppingUnits() throws FileNotFoundException {

		/**
		 * initial units are mm and resolution is 72 dpi (72 pixels?)
		{page: 1, top: 25.9, left: 97.8, width: 15.0, height: 8.5},  transforms to
		(277, 727) (320, 703)  'Type'
		
		{page: 1, top: 117.3, left: 17.6, width: 85.6, height: 79.5} transforms to
		(50, 467) (293, 242)   [abstract]
		*/
		PageCropper pageCropper = new PageCropper();
		String fileroot = "materials-05-00027-page7";
		File inputFile = new File(SVG2XMLFixtures.MDPI_DIR, fileroot + ".svg");
		
		PageCropper cropper = new PageCropper();
		cropper.readSVG(inputFile);
		cropper.setTLBRUserMediaBox(new Real2(0, 800), new Real2(600, 0));
		SVGElement svgElement = cropper.cropElementTLBR(new Real2(30, 580), new Real2(570, 320), Units.PX);
		SVGSVG.wrapAndWriteAsSVG(svgElement, new File(new File("target/crop/bio/"), fileroot + ".cropUnits.svg"));

	}
	
}
