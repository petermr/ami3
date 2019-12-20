package org.contentmine.pdf2svg2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDShadingPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDSoftMask;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/**
 * Example PageDrawer subclass with custom rendering.
 * John Hewson
 * modified by pm286
 * 
 */
public class MyPageDrawer extends PageDrawer
{
    MyPageDrawer(PageDrawerParameters parameters) throws IOException
    {
        super(parameters);
    }

    /**
     * Color replacement.
     */
    @Override
    protected Paint getPaint(PDColor color) throws IOException
    {
    	System.err.println("PDColor "+color+"/"+color.getComponents()[0]);
    	// JUST AN EXAMPLE
        // if this is the non-stroking color
        if (getGraphicsState().getNonStrokingColor() == color)
        {
            // find red, ignoring alpha channel
            if (color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF))
            {
            	System.err.println("BLUE!");
                // replace it with blue
                return Color.BLUE;
            }
        }
        return super.getPaint(color);
//        PDColorSpace colorSpace = color.getColorSpace();
//        if (!(colorSpace instanceof PDPattern))
//        {
//            float[] rgb = colorSpace.toRGB(color.getComponents());
//            return new Color(clampColor(rgb[0]), clampColor(rgb[1]), clampColor(rgb[2]));
//        }
//        else
//        {
//            PDPattern patternSpace = (PDPattern)colorSpace;
//            PDAbstractPattern pattern = patternSpace.getPattern(color);
//            if (pattern instanceof PDTilingPattern)
//            {
//                PDTilingPattern tilingPattern = (PDTilingPattern) pattern;
//
//                if (tilingPattern.getPaintType() == PDTilingPattern.PAINT_COLORED)
//                {
//                    // colored tiling pattern
//                    return tilingPaintFactory.create(tilingPattern, null, null, xform);
//                }
//                else
//                {
//                    // uncolored tiling pattern
//                    return tilingPaintFactory.create(tilingPattern, 
//                            patternSpace.getUnderlyingColorSpace(), color, xform);
//                }
//            }
//            else
//            {
//                PDShadingPattern shadingPattern = (PDShadingPattern)pattern;
//                PDShading shading = shadingPattern.getShading();
//                if (shading == null)
//                {
//                    LOG.error("shadingPattern is null, will be filled with transparency");
//                    return new Color(0,0,0,0);
//                }
//                return shading.toPaint(Matrix.concatenate(getInitialMatrix(),
//                                                          shadingPattern.getMatrix()));
//
//            }
//        }
    }

    /**
     * Glyph bounding boxes.
     */
    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                             Vector displacement) throws IOException
    {

    	System.err.println("glyph "+font+"/"+code+"/"+unicode+"/"+displacement);
    	// JUST AN EXAMPLE

        // draw glyph
        super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);
        
        // draw box round glyph
        // bbox in EM -> user units
        Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
        AffineTransform at = textRenderingMatrix.createAffineTransform();
        bbox = at.createTransformedShape(bbox);
        
        // save
        Graphics2D graphics = getGraphics();
        System.err.println("Graphics "+graphics);
        Color color = graphics.getColor();
        Stroke stroke = graphics.getStroke();
        Shape clip = graphics.getClip();

        // draw
        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(.5f));
        graphics.draw(bbox);

        // restore
        graphics.setStroke(stroke);
        graphics.setColor(color);
        graphics.setClip(clip);
    }

//    /**
//     * Filled path bounding boxes.
//     */
//    @Override
//    public void fillPath(int windingRule) throws IOException
//    {
//    	System.out.println("super.fillPath(windingRule)");
//    	super.fillPath(windingRule);
//    	GeneralPath generalPath = this.getLinePath();
//    	System.err.println("general path "+generalPath);
//    	// JUST AN EXAMPLE
//
//        // bbox in user units
//        Shape bbox = getLinePath().getBounds2D();
//        
//        // draw path (note that getLinePath() is now reset)
//        super.fillPath(windingRule);
//        
//        // save
//        Graphics2D graphics = getGraphics();
//        Color color = graphics.getColor();
//        Stroke stroke = graphics.getStroke();
//        Shape clip = graphics.getClip();
//
//        // draw
//        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
//        graphics.setColor(Color.GREEN);
//        graphics.setStroke(new BasicStroke(.5f));
//        graphics.draw(bbox);
//
//        // restore
//        graphics.setStroke(stroke);
//        graphics.setColor(color);
//        graphics.setClip(clip);
//    }

    

}
