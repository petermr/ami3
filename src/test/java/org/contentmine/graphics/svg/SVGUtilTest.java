package org.contentmine.graphics.svg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.image.ImageUtil;
import org.junit.Assert;
import org.junit.Test;

public class SVGUtilTest {

	private final static Logger LOG = Logger.getLogger(SVGUtilTest.class);

	@Test
	public void testInterposeGBetweenChildren() {
		SVGSVG svg = new SVGSVG();
		svg.appendChild(new SVGCircle(new Real2(10, 20), 5));
		svg.appendChild(new SVGText(new Real2(40, 50), "test"));
		Assert.assertEquals("before child", 2, svg.getChildCount());
		Assert.assertEquals("before child", SVGCircle.class, svg.getChild(0).getClass());
		AbstractCMElement g = SVGUtil.interposeGBetweenChildren(svg);
		Assert.assertEquals("after child", 1, svg.getChildCount());
		Assert.assertEquals("after child", 2, g.getChildCount());
		Assert.assertEquals("after child", SVGCircle.class, g.getChild(0).getClass());
	}
	
	@Test
	public void testAffineTransformationAngle() {
		double[] matrix = new double[6];
		SVGG svgG = new SVGG();
		Transform2 transform2 = null;
		transform2 = new Transform2(new Angle(1.0));
		AffineTransform affineTransform = transform2.getAffineTransform();
		affineTransform.getMatrix(matrix);
		Assert.assertEquals(matrix[0], 0.5403023058681398, 0.000000001);
		Assert.assertEquals(matrix[1], -0.8414709848078965, 0.000000001);
		Assert.assertEquals(matrix[2], 0.8414709848078965, 0.000000001);
		Assert.assertEquals(matrix[3], 0.5403023058681398, 0.000000001);
		Assert.assertEquals(matrix[4], 0.0, 0.000000001);
		Assert.assertEquals(matrix[5], 0.0, 0.000000001);
		svgG.setTransform(transform2);
		LOG.trace(svgG.toXML());
	}
	
	@Test
	public void testAffineTransformationRotate() throws Exception { 
		double[] matrix = new double[6];
		AbstractCMElement svgG = new SVGG();
		Transform2 transform2 = new Transform2(new Angle(0.3));
		AffineTransform affineTransform = transform2.getAffineTransform();
		LOG.trace(affineTransform);
		int width = 300;
		int height = 300;
		BufferedImage img = ImageUtil.createARGBBufferedImage(width, height);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(Color.RED);
		g2d.setTransform(affineTransform);
		g2d.drawString("ABC", 10, 20);
		SVGHTMLFixtures.writeImageQuietly(img,  new File("target/affine2.png"));
	}
	
	@Test
	public void testAffineTransformationScale() throws Exception { 
		double[] matrix = new double[6];
		SVGG svgG = new SVGG();
		SVGElement rect = new SVGRect(new Real2(100.,  100.), new Real2(150., 170.));
		svgG.appendChild(rect);
		int width = 300;
		int height = 300;
		BufferedImage img = ImageUtil.createARGBBufferedImage(width, height);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		svgG.draw(g2d);
		SVGHTMLFixtures.writeImageQuietly(img, new File("target/rect0.png"));
		Transform2 transform2 = new Transform2(new Vector2(20., -70.));
//		Transform2 transform2 = new Transform2(new Angle(0.2));
		svgG.setTransform(transform2);
		img = ImageUtil.createARGBBufferedImage(width, height);
		g2d = (Graphics2D) img.getGraphics();
		svgG.draw(g2d);
		SVGHTMLFixtures.writeImageQuietly(img, new File("target/rect1.png"));
	}

	
	@Test
	public void testAffineTransformationConcat() throws Exception { 
		int width = 300;
		int height = 400;
		double[] matrix = new double[6];
		SVGSVG svg = new SVGSVG();
		svg.appendChild(new SVGRect(new Real2(0., 0.), new Real2(width, height)));
		AbstractCMElement g = new SVGG();
		svg.appendChild(g);
		Real2 xy00 = new Real2(10.,  10.);
		SVGCircle circle0 = createCircle(xy00, 20., "red", "black", 2.0);
		Real2 xy01 = new Real2(50.,  30.);
		SVGElement rect0 = createRect(xy00, xy01, "red", "black", 1.0);
		svg.appendChild(circle0);
		svg.appendChild(rect0);
		
		Real2 translate = new Real2(100., 50.);
		Transform2 t2 = new Transform2(new Vector2(translate));
		SVGCircle circle1 = createCircle(xy00, 20., "blue", "yellow", 3.0);
		circle1.setTransform(t2);
		SVGRect rect1 = createRect(xy00, xy01,  "blue", "yellow", 3.0);
		rect1.setTransform(t2);
		g.appendChild(circle1);
		g.appendChild(rect1);
		SVGUtil.debug(svg, new FileOutputStream("target/concat0.svg"), 1);
//		g.setTransform(new Transform2(new Vector2(100., 50.)));
		BufferedImage img = ImageUtil.createARGBBufferedImage(width+1, height+1);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		svg.draw(g2d);
		SVGUtil.debug(svg, new FileOutputStream("target/concat0a.svg"), 1);
		ImageIO.write(img, "png", new File("target/concat0.png"));
	}
	
	@Test
	public void testBoundingBoxIncludes() {
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		SVGRect rect1 = new SVGRect(new Real2(100., 200.), new Real2(150., 250.));
		elementList.add(rect1);
		rect1.setId("r1");
		SVGRect rect2 = new SVGRect(new Real2(300., 400.), new Real2(350., 450.));
		elementList.add(rect2);
		rect2.setId("r2");
		Assert.assertEquals("list", 2, elementList.size());
		List<SVGElement> includedList = SVGUtil.findElementsWithin(new Real2Range(new RealRange(0., 400.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("both", 2, includedList.size());
		includedList = SVGUtil.findElementsWithin(new Real2Range(new RealRange(50., 200.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("r1", 1, includedList.size());
		includedList = SVGUtil.findElementsWithin(new Real2Range(new RealRange(200., 500.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("r2", 1, includedList.size());
		includedList = SVGUtil.findElementsWithin(new Real2Range(new RealRange(200., 250.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("neither", 0, includedList.size());
		// edges - this is bad practice as it includes FP equality
		includedList = SVGUtil.findElementsWithin(new Real2Range(new RealRange(100., 350.), new RealRange(200., 450.)), elementList);
		Assert.assertEquals("both just", 2, includedList.size());
	}

	
	@Test
	public void testBoundingBoxIntersectsWith() {
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		SVGRect rect1 = new SVGRect(new Real2(100., 200.), new Real2(150., 250.));
		elementList.add(rect1);
		rect1.setId("r1");
		SVGRect rect2 = new SVGRect(new Real2(300., 400.), new Real2(350., 450.));
		elementList.add(rect2);
		rect2.setId("r2");
		Assert.assertEquals("list", 2, elementList.size());
		List<SVGElement> includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(0., 400.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("both", 2, includedList.size());
		includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(50., 200.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("r1", 1, includedList.size());
		includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(50., 320.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("both", 2, includedList.size());
		includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(120., 500.), new RealRange(0., 500.)), elementList);
		Assert.assertEquals("both", 2, includedList.size());
		includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(120., 500.), new RealRange(220., 420.)), elementList);
		Assert.assertEquals("both", 2, includedList.size());
		includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(120., 320.), new RealRange(220., 420.)), elementList);
		Assert.assertEquals("both", 2, includedList.size());
		includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(200., 250.), new RealRange(400., 500.)), elementList);
		Assert.assertEquals("none", 0, includedList.size());
		// edges - this is bad practice as it includes FP equality
		includedList = SVGUtil.findElementsIntersecting(new Real2Range(new RealRange(150., 300.), new RealRange(250., 400.)), elementList);
		Assert.assertEquals("both just", 2, includedList.size());
	}

	// ================================================================
	
	private SVGRect createRect(Real2 xy0, Real2 xy1, String fill, String stroke, double strokeWidth) {
		SVGRect rect = new SVGRect(xy0, xy1);
		if (fill != null) rect.setFill(fill);
		if (stroke != null) rect.setStroke(stroke);
		rect.setStrokeWidth(strokeWidth);
		return rect;
	}

	private SVGCircle createCircle(Real2 rect00, double rad, String fill, String stroke, double strokeWidth) {
		SVGCircle circle = new SVGCircle(rect00, rad);
		if (fill != null) circle.setFill(fill);
		if (stroke != null) circle.setStroke(stroke);
		circle.setStrokeWidth(strokeWidth);
		return circle;
	}

}
