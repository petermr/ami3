package org.contentmine.graphics.svg.image;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGImage;
import org.contentmine.graphics.svg.SVGUtil;
import org.junit.Assert;
import org.junit.Test;


public class ImageConverterTest {

	private final static Logger LOG = Logger.getLogger(ImageConverterTest.class);
	
	@Test
	public void testExtractImageStringBoundaries() {
		ImageConverter imageConverter = new ImageConverter();
		imageConverter.readSVGString(SVGHTMLFixtures.IMAGE_SVG);
		List<IntRange> intRangeList = imageConverter.extractImageStringBoundaries();
		Assert.assertEquals("image", 1, intRangeList.size());
		Assert.assertEquals("image", "(83,315)", intRangeList.get(0).toString());
	}
	
	@Test
	public void testExtractImageString() {
		ImageConverter imageConverter = new ImageConverter();
		imageConverter.readSVGString(SVGHTMLFixtures.IMAGE_SVG);
		List<String> imageStringList = imageConverter.extractImageStrings();
		Assert.assertEquals("image", 1, imageStringList.size());
		Assert.assertEquals("image", "<image  x=\"0.0\" y=\"0.0\" width=\"16.0\" height=\"16.0\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR42mP4J8LwHx0zAAE2cWzyeBUSgxnw2UwMnzouINVmnF4YwmEwmg7Is3kYhQEA6pzZRchLX5wAAAAASUVORK5CYII=\"/>",
				imageStringList.get(0));
	}
	
	@Test
	public void testExtractHrefString() {
		ImageConverter imageConverter = new ImageConverter();
		imageConverter.readSVGString(SVGHTMLFixtures.IMAGE_SVG);
		List<String> hrefStringList = imageConverter.extractHrefStrings();
		Assert.assertEquals("href", 1, hrefStringList.size());
		Assert.assertEquals("href", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR42mP4J8LwHx0zAAE2cWzyeBUSgxnw2UwMnzouINVmnF4YwmEwmg7Is3kYhQEA6pzZRchLX5wAAAAASUVORK5CYII=",
				hrefStringList.get(0));
	}
	
	@Test
	public void testCreateImageFromHref() throws IOException {
		ImageConverter imageConverter = new ImageConverter();
		imageConverter.readSVGString(SVGHTMLFixtures.IMAGE_SVG);
		imageConverter.setImageDirectory(new File(new File("target"), "images/"));
		imageConverter.setFileroot("image");
		imageConverter.setMimeType(SVGImage.IMAGE_PNG);
		List<String> imageFilenameList = imageConverter.createImageFiles();
		Assert.assertEquals("image filename", 1, imageFilenameList.size());
		Assert.assertEquals("image filename", "target/images/image.1.png",
				imageFilenameList.get(0));
		// check file has been written
		AbstractCMElement svgElement = SVGImage.createSVGFromImage(new File(imageFilenameList.get(0)), SVGImage.IMAGE_PNG);
		// unstable
//		Assert.assertEquals("image", "<image xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR42mP4J8LwHx0zAAE2cWzyeBUSgxnw2UwMnzouINVmnF4YwmEwmg7Is3kYhQEA6pzZRchLX5wAAAAASUVORK5CYII=\" />",
//				svgElement.toXML());
		Assert.assertTrue(svgElement != null && svgElement.toXML().length() > 100);

	}
	
	@Test
	public void testCreateHref() throws IOException {
		ImageConverter imageConverter = new ImageConverter();
		imageConverter.readSVGString(SVGHTMLFixtures.IMAGE_SVG);
		imageConverter.setImageDirectory(new File(new File("target"), "images/"));
		imageConverter.setFileroot("image");
		imageConverter.setMimeType(SVGImage.IMAGE_PNG);
		List<String> imageFilenameList = imageConverter.createImageFiles();
		imageConverter.replaceHrefDataWithFileRef("../");
		String svgString = imageConverter.getSVGString();
		Assert.assertEquals("svgString", 
				"<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><image  x=\"0.0\" y=\"0.0\" width=\"16.0\" height=\"16.0\" xlink:href=\"../target/images/image.1.png\"/></svg>",
				svgString);
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(XMLUtil.parseXML(svgString));
		SVGUtil.debug(svgElement, "target/newSvgElement.svg", 1);
		
	}
	
	@Test
	public void testLargeFile() throws IOException {
		ImageConverter imageConverter = new ImageConverter();
		imageConverter.readSVGFile(SVGHTMLFixtures.LARGE_IMAGE_SVG);
		imageConverter.setImageDirectory(new File(new File("target"), "images/"));
		imageConverter.setFileroot("imagex");
		imageConverter.setMimeType(SVGImage.IMAGE_PNG);
		imageConverter.createImageFiles();
		imageConverter.replaceHrefDataWithFileRef("../");
		String svgString = imageConverter.getSVGString();
		Assert.assertEquals("svg", 247712, svgString.length());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(XMLUtil.parseXML(svgString));
		SVGUtil.debug(svgElement, "target/largeElement.svg", 1);
	}
	
}
