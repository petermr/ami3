/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contentmine.pdf2svg;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.svg.SVGPath;

/**
 * a graphics2D for PDFBox applications to write to traps all Java2D graphics
 * calls mainly diagnostic - if pdf2svg is comprehensive, these methods should
 * not be required

  * a new PDFGraphics object is created to capture glyphVectors 
 */
public class PDFGraphics2D extends Graphics2D {

	private final static Logger LOG = Logger.getLogger(PDFGraphics2D.class);
	private String currentPathString;
	private AMIFont amiFont;
	private SVGPath svgPath;
	
	public PDFGraphics2D(AMIFont amiFont) {
//		System.out.println("PDFGraphics");
		this.amiFont = amiFont;
	}

	@Override
	public void draw(Shape s) {
		// TODO Auto-generated method stub
		System.out.printf("draw(shape=%s)%n", s.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// TODO Auto-generated method stub
//		System.out.printf("drawImage(img=%s,xform=%s,obs=%s)%n",
//				img.toString(), xform.toString(), obs.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		// TODO Auto-generated method stub
		System.out.printf("drawImage(img=%s,op=%s,x=%d,y=%d)%n",
				img.toString(), op.toString(), x, y);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
		System.out.printf("drawRenderedImage(img=%s,xform=%s)%n",
				img.toString(), xform.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
		System.out.printf("drawRenderableImage(img=%s,xform=%s)%n",
				img.toString(), xform.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawString(String str, int x, int y) {
		// TODO Auto-generated method stub
//		System.out.printf("drawString(str=%s,x=%d,y=%d)%n", str.toString(), x,
//				y);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawString(String str, float x, float y) {
		// TODO Auto-generated method stub
//		System.out.printf("drawString(str=%s,x=%f,y=%f)%n", str.toString(), x,
//				y);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub
		System.out.printf("drawString(iterator=%s,x=%d,y=%d)%n",
				iterator.toString(), x, y);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		// TODO Auto-generated method stub
		System.out.printf("drawString(iterator=%s,x=%f,y=%f)%n",
				iterator.toString(), x, y);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// TODO Auto-generated method stub
//		System.out.printf("drawGlyphVector(g=%s,x=%f,y=%f)%n", g.toString(), x,
//				y);
		
		Shape shape = g.getOutline();
		AffineTransform at = new AffineTransform();
		this.currentPathString = SVGPath.getPathAsDString(shape.getPathIterator(at));
		svgPath = new SVGPath(this.currentPathString);
		
//		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void fill(Shape s) {
		// TODO Auto-generated method stub
		System.out.printf("fill(shape=%s)%n", s.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// TODO Auto-generated method stub
		System.out.printf("hit(rect=%s,shape=%s,onStroke=%s)%n",
				rect.toString(), s.toString(), Boolean.toString(onStroke));
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		System.out.printf("getDeviceConfiguration()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setComposite(Composite comp) {
		// TODO Auto-generated method stub
		System.out.printf("setComposite(comp=%s)%n", comp.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setPaint(Paint paint) {
		// TODO Auto-generated method stub
		System.out.printf("setPaint(paint=%s)%n", paint.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setStroke(Stroke s) {
		// TODO Auto-generated method stub
		System.out.printf("setStroke(stroke=%s)%n", s.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// TODO Auto-generated method stub
		LOG.trace(hintKey.toString()+" = "+ hintValue.toString());
//		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		// TODO Auto-generated method stub
		System.out.printf("getRenderingHint(hintKey=%s)%n", hintKey.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub
		System.out.printf("setRenderingHints(hints=%s)%n", hints.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub
		System.out.printf("addRenderingHints(hints=%s)%n", hints.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public RenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		System.out.printf("getRenderingHints()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub
		System.out.printf("translate(x=%d, y=%d)%n", x, y);
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public void translate(double tx, double ty) {
		// TODO Auto-generated method stub
		System.out.printf("translate(tx=%lf, ty=%lf)%n", tx, ty);
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public void rotate(double theta) {
		// TODO Auto-generated method stub
		System.out.printf("rotate(theta=%lf)%n", theta);
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public void rotate(double theta, double x, double y) {
		// TODO Auto-generated method stub
		System.out.printf("rotate(theta=%lf, x=%lf, y=%lf)%n", theta, x, y);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void scale(double sx, double sy) {
		// TODO Auto-generated method stub
		System.out.printf("scale(sx=%lf, sy=%lf)%n", sx, sy);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void shear(double shx, double shy) {
		// TODO Auto-generated method stub
		System.out.printf("shear(shx=%lf, shy=%lf)%n", shx, shy);
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public void transform(AffineTransform Tx) {
//		// TODO Auto-generated method stub
//		System.out.printf("transform(Tx=%s)%n", Tx.toString());
		boolean unusual = reportUnusualTransforms(Tx);
		if (unusual && svgPath != null) {
			LOG.debug("Character path: "+svgPath.toXML());
		}
//		throw new RuntimeException("OVERRIDE this");

	}

	private boolean reportUnusualTransforms(AffineTransform Tx) {
		boolean unusual = false;
		Transform2 t2 = new Transform2(Tx);
		RealArray scales = t2.getScales();
		double scalex = scales.get(0);
		double scaley = scales.get(1);
		double scaleRatio = scalex/scaley;
		Real2 translate = t2.getTranslation();
		// trap non-square or translated
		if (!Real.isEqual(scaleRatio, 1.0, 0.3) 
				|| !translate.isEqualTo(new Real2(0., 0.0), 0.001)) {
//			System.out.printf("transform(Tx=%s)%n", t2.toString());
			System.out.println("transform "+ new RealArray(t2.getMatrixAsArray()));
			unusual = true;
		}
		return unusual;
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		// TODO Auto-generated method stub
		// trap non-square or translated
		reportUnusualTransforms(Tx);
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public AffineTransform getTransform() {
		// TODO Auto-generated method stub
		System.out.printf("getTransform()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Paint getPaint() {
		// TODO Auto-generated method stub
		System.out.printf("getPaint()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Composite getComposite() {
		// TODO Auto-generated method stub
		System.out.printf("getComposite()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setBackground(Color color) {
		// TODO Auto-generated method stub
		System.out.printf("setBackground(color=%s)%n", color.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Color getBackground() {
		// TODO Auto-generated method stub
		System.out.printf("getBackground()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Stroke getStroke() {
		// TODO Auto-generated method stub
		System.out.printf("getStroke()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void clip(Shape s) {
		// TODO Auto-generated method stub
		System.out.printf("clip(shape=%s)%n", s.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		// TODO Auto-generated method stub
		System.out.printf("getFontRenderContext()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		System.out.printf("create()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		System.out.printf("getColor()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setColor(Color c) {
		// TODO Auto-generated method stub
		System.out.printf("setColor(color=%s)%n", c.toString());
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public void setPaintMode() {
		// TODO Auto-generated method stub
		System.out.printf("setPaintMode()%n");
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public void setXORMode(Color c1) {
		// TODO Auto-generated method stub
		System.out.printf("setXORMode(color=%s)%n", c1.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		System.out.printf("getFont()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setFont(Font font) {
		// TODO Auto-generated method stub
		System.out.printf("setFont(font=%s)%n", font.toString());
		throw new RuntimeException("OVERRIDE this");

	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		// TODO Auto-generated method stub
		System.out.printf("getFontMetrics(font=%s)%n", f.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Rectangle getClipBounds() {
		// TODO Auto-generated method stub
		System.out.printf("getClipBounds()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("clipRect(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
//		System.out.printf("setClip(x=%d, y=%d, width=%d, height=%d)%n", x, y,
//				width, height);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public Shape getClip() {
		// TODO Auto-generated method stub
		System.out.printf("getClip()%n");
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void setClip(Shape clip) {
		// TODO Auto-generated method stub
		//System.out.printf("setClip(shape=%s)%n", clip.toString());
		SVGPath path = new SVGPath(clip);
		LOG.trace("Clip: "+path.getDString());
//		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
		System.out.printf(
				"copyArea(x=%d, y=%d, width=%d, height=%d, dx=%d, dy=%d)%n", x,
				y, width, height, dx, dy);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub
		System.out.printf("drawLine(x1=%d, y1=%d, x2=%d, y2=%d)%n", x1, y1, x2,
				y2);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("fillRect(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("clearRect(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawRoundRect(x=%d, y=%d, width=%d, height=%d, arcWidth=%d, arcHeight=%d)%n",
						x, y, width, height, arcWidth, arcHeight);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		System.out
				.printf("fillRoundRect(x=%d, y=%d, width=%d, height=%d, arcWidth=%d, arcHeight=%d)%n",
						x, y, width, height, arcWidth, arcHeight);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("drawOval(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("fillOval(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawArc(x=%d, y=%d, width=%d, height=%d, startAngle=%d, arcAngle=%d)%n",
						x, y, width, height, startAngle, arcAngle);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub
		System.out
				.printf("fillArc(x=%d, y=%d, width=%d, height=%d, startAngle=%d, arcAngle=%d)%n",
						x, y, width, height, startAngle, arcAngle);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub
		System.out.printf("drawPolyline(xPoints=%s, yPoints=%s, nPoints=%d)%n",
				Arrays.toString(xPoints), Arrays.toString(yPoints), nPoints);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub
		System.out.printf("drawPolygon(xPoints=%s, yPoints=%s, nPoints=%d)%n",
				Arrays.toString(xPoints), Arrays.toString(yPoints), nPoints);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub
		System.out.printf("fillPolygon(xPoints=%s, yPoints=%s, nPoints=%d)%n",
				Arrays.toString(xPoints), Arrays.toString(yPoints), nPoints);
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out.printf("drawImage(img=%s, x=%d, y=%d, observer=%s)%n",
				img.toString(), x, y, observer.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, x=%d, y=%d, width=%d, height=%d, observer=%s)%n",
						img.toString(), x, y, width, height,
						observer.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out.printf(
				"drawImage(img=%s, x=%d, y=%d, bgcolor=%s, observer=%s)%n",
				img.toString(), x, y, bgcolor.toString(), observer.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, x=%d, y=%d, width=%d, height=%d, bgcolor=%s, observer=%s)%n",
						img.toString(), x, y, width, height,
						bgcolor.toString(), observer.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, dx1=%d, dy1=%d, dx2=%d, dy2=%d, sx1=%d, sy1=%d, sx2=%d, sy2=%d, observer=%s)%n",
						img.toString(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
						observer.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, dx1=%d, dy1=%d, dx2=%d, dy2=%d, sx1=%d, sy1=%d, sx2=%d, sy2=%d, bgcolor=%s, observer=%s)%n",
						img.toString(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
						bgcolor.toString(), observer.toString());
		throw new RuntimeException("OVERRIDE this");
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
//		System.out.printf("dispose()%n");
	}

	public String getCurrentPathString() {
		return this.currentPathString;
	}

}
