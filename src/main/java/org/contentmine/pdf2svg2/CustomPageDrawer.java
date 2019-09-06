/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.contentmine.pdf2svg2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDShadingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGPath;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Example showing custom rendering by subclassing PageDrawer.
 * 
 * <p>If you want to do custom graphics processing rather than Graphics2D rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a Graphics2D surface.
 *
 * @author John Hewson
 */
public class CustomPageDrawer {
	static final Logger LOG = Logger.getLogger(CustomPageDrawer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
    public static void main(String[] args) throws IOException
    {
        File indir = new File("src/main/resources/org/apache/pdfbox/examples/rendering/");
        String fileroot = "custom-render-demo";
        File outdir = indir;
        CustomPageDrawer customPageDrawer = new CustomPageDrawer();
        customPageDrawer.renderPage0(indir, outdir, fileroot);
    }

	public void renderPage0(File indir, File outdir, String fileroot) throws IOException {
		File infile = new File(indir, fileroot + ".pdf");
        File outfile = new File(outdir, fileroot + ".png");
		PDDocument doc = PDDocument.load(infile);
        PDFRenderer renderer = new MyPDFRenderer(doc);
        BufferedImage image = renderer.renderImage(0);
		ImageIO.write(image, "PNG", outfile);
        doc.close();
	}

	public void renderFile(File indir, File outdir) throws IOException {
		File infile = new File(indir, "fulltext" + ".pdf");
		outdir.mkdirs();
	    try {
	    	PDDocument doc = PDDocument.load(infile);
	    	PDFRenderer renderer = new MyPDFRenderer(doc);
	    	for (int i = 0; i < doc.getNumberOfPages(); i++) {
	    		BufferedImage image = renderer.renderImage(i);
	        	ImageIO.write(image, "PNG", new File(outdir, "page" + "." + i + ".png"));
	    	}
	    } catch (Exception e) {
	    	throw new RuntimeException("exception", e);
	    }
	}

    /**
     * Example PDFRenderer subclass, uses MyPageDrawer for custom rendering.
     */
    private static class MyPDFRenderer extends PDFRenderer
    {
        MyPDFRenderer(PDDocument document)
        {
            super(document);
        }

        @Override
        protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
        {
            return new MyPageDrawer(parameters);
        }
    }

    /**
     * Example PageDrawer subclass with custom rendering.
     */
    private static class MyPageDrawer extends PageDrawer
    {
    	private Multiset<String> fillColorSet;
    	private Multiset<String> strokeColorSet;

    	private void init() {
    		fillColorSet = HashMultiset.create();
    		strokeColorSet = HashMultiset.create();    	}

        MyPageDrawer(PageDrawerParameters parameters) throws IOException
        {
            super(parameters);
            init();
        }

        /**
         * Color replacement.
         */
        @Override
        protected Paint getPaint(PDColor color) throws IOException
        {
            // if this is the non-stroking color
            if (getGraphicsState().getNonStrokingColor() == color)
            {
                // find red, ignoring alpha channel
                if (color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF))
                {
                    // replace it with blue
                    return Color.BLUE;
                }
            }
            return super.getPaint(color);
        }

        /**
         * Glyph bounding boxes.
         */
        @Override
        protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException
        {
            // draw glyph
            super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);
            
            // bbox in EM -> user units
            Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
            AffineTransform at = textRenderingMatrix.createAffineTransform();
            bbox = at.createTransformedShape(bbox);
            
            // save
            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Stroke stroke = graphics.getStroke();
            Shape clip = graphics.getClip();

            // draw
            graphics.setClip(graphics.getDeviceConfiguration().getBounds());
            graphics.setColor(Color.RED);
            graphics.setStroke(new BasicStroke(.5f));
            graphics.draw(bbox);
//            System.out.println("BB "+bbox.getBounds2D()+graphics.getStroke()+"/"+graphics.getColor());

            // restore
            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.setClip(clip);
        }

        /**
         * Filled path bounding boxes.
         */
        @Override
        public void fillPath(int windingRule) throws IOException
        {
            // bbox in user units
            Shape bbox = getLinePath().getBounds2D();
            
            // draw path (note that getLinePath() is now reset)
            super.fillPath(windingRule);
            
            // save
            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Stroke stroke = graphics.getStroke();
            Shape clip = graphics.getClip();

            // draw
            graphics.setClip(graphics.getDeviceConfiguration().getBounds());
            graphics.setColor(Color.GREEN);
            graphics.setStroke(new BasicStroke(.5f));
            graphics.draw(bbox);

          String hexString = Integer.toHexString(color.getRGB());
          if (!fillColorSet.contains(hexString)) {
        	  LOG.debug("new color: "+hexString+"/"+fillColorSet.elementSet().size());
          }
          fillColorSet.add(hexString);
        BasicStroke basicStroke = (BasicStroke) stroke;
      float[] dashArray = basicStroke.getDashArray();
	if (dashArray != null) LOG.debug(basicStroke.getLineWidth()+"/"+dashArray);
      GeneralPath generalPath = getLinePath();
  		SVGPath currentSvgPath = new SVGPath(generalPath);
		Real2Range boundingBox = currentSvgPath.getBoundingBox();
		if (boundingBox != null) {
			LOG.debug("SP "+boundingBox);
		}

            // restore
            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.setClip(clip);
            
//            String hexString = Integer.toHexString(javaColor.getRGB());
//            if (!hexString.equals("ff000000"))		LOG.debug(hexString);
//            BasicStroke stroke = (BasicStroke) getGraphics().getStroke();
////            LOG.debug(stroke.getLineWidth()+"/"+stroke.getDashArray());
//            GeneralPath generalPath = getLinePath();
// 	   		SVGPath currentSvgPath = new SVGPath(generalPath);
// 			Real2Range boundingBox = currentSvgPath.getBoundingBox();
// 			if (boundingBox != null) {
// 				LOG.debug("SP "+boundingBox);
// 			}
//            getLinePath().reset();

        }

        /**
         * Custom annotation rendering.
         */
        @Override
        public void showAnnotation(PDAnnotation annotation) throws IOException
        {
            // save
            saveGraphicsState();
            
            // 35% alpha
            getGraphicsState().setNonStrokeAlphaConstant(0.35);
            super.showAnnotation(annotation);
            
            // restore
            restoreGraphicsState();
        }
        
        @Override
        public void beginText() throws IOException
        {
        	super.beginText();
//        	System.err.print("BT");
        }

        @Override
        public void endText() throws IOException
        {
        	super.endText();
//        	System.err.print("ET");
        }

        @Override
        protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                     Vector displacement) throws IOException
        {
        	super.showFontGlyph(textRenderingMatrix, font, code, unicode, displacement);
//        	System.err.print("FG");
        }
        
        @Override
        public void strokePath() throws IOException
        {
//            graphics.setComposite(getGraphicsState().getStrokingJavaComposite());
//            graphics.setPaint(getStrokingPaint());
//            graphics.setStroke(getStroke());
//            setClip();
//            //TODO bbox of shading pattern should be used here? (see fillPath)
//            graphics.draw(linePath);
//            linePath.reset();
           super.strokePath();
           Composite javaComposite = getGraphicsState().getStrokingJavaComposite();
           Color color = getGraphics().getColor();
           String hexString = Integer.toHexString(color.getRGB());
           if (!strokeColorSet.contains(hexString)) {
         	  LOG.debug("new stroke: "+hexString+"/"+strokeColorSet.elementSet().size());
           }
           strokeColorSet.add(hexString);
           BasicStroke stroke = (BasicStroke) getGraphics().getStroke();
//           LOG.debug(stroke.getLineWidth()+"/"+stroke.getDashArray());
           GeneralPath generalPath = getLinePath();
	   		SVGPath currentSvgPath = new SVGPath(generalPath);
	   		String d = currentSvgPath.getDString();
	   		if (!"".equals(d)) {
	   			LOG.debug(currentSvgPath.toXML());
	   		}
			Real2Range boundingBox = currentSvgPath.getBoundingBox();
			if (boundingBox != null) {
				LOG.debug("SP "+boundingBox);
			}
           getLinePath().reset();
        }

//        @Override
//        public void fillPath(int windingRule) throws IOException
//        {
//            graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//            graphics.setPaint(getNonStrokingPaint());
//            setClip();
//            getLinePath().setWindingRule(windingRule);
//
//            // disable anti-aliasing for rectangular paths, this is a workaround to avoid small stripes
//            // which occur when solid fills are used to simulate piecewise gradients, see PDFBOX-2302
//            // note that we ignore paths with a width/height under 1 as these are fills used as strokes,
//            // see PDFBOX-1658 for an example
//            Rectangle2D bounds = linePath.getBounds2D();
//            boolean noAntiAlias = isRectangular(linePath) && bounds.getWidth() > 1 &&
//                                                             bounds.getHeight() > 1;
//            if (noAntiAlias)
//            {
//                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                                          RenderingHints.VALUE_ANTIALIAS_OFF);
//            }
//
//            if (!(graphics.getPaint() instanceof Color))
//            {
//                // apply clip to path to avoid oversized device bounds in shading contexts (PDFBOX-2901)
//                Area area = new Area(linePath);
//                area.intersect(new Area(graphics.getClip()));
//                intersectShadingBBox(getGraphicsState().getNonStrokingColor(), area);
//                graphics.fill(area);
//            }
//            else
//            {
//                graphics.fill(linePath);
//            }
//            
//            linePath.reset();
//
//            if (noAntiAlias)
//            {
//                // JDK 1.7 has a bug where rendering hints are reset by the above call to
//                // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
//                setRenderingHints();
//            }
//        }

        // checks whether this is a shading pattern and if yes,
        // get the transformed BBox and intersect with current paint area
        // need to do it here and not in shading getRaster() because it may have been rotated
        private void intersectShadingBBox(PDColor color, Area area) throws IOException
        {
            if (color.getColorSpace() instanceof PDPattern)
            {
                PDColorSpace colorSpace = color.getColorSpace();
                PDAbstractPattern pat = ((PDPattern) colorSpace).getPattern(color);
                if (pat instanceof PDShadingPattern)
                {
                    PDShading shading = ((PDShadingPattern) pat).getShading();
                    PDRectangle bbox = shading.getBBox();
                    if (bbox != null)
                    {
                        Matrix m = Matrix.concatenate(getInitialMatrix(), pat.getMatrix());
                        Area bboxArea = new Area(bbox.transform(m));
                        area.intersect(bboxArea);
                    }
                }
            }
        }
    }
}
