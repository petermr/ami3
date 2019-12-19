package org.contentmine.pdf2svg2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
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
public class AmiPageDrawer extends PageDrawer
{
    AmiPageDrawer(PageDrawerParameters parameters) throws IOException
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

    /**
     * Filled path bounding boxes.
     */
    @Override
    public void fillPath(int windingRule) throws IOException
    {
    	GeneralPath generalPath = this.getLinePath();
    	System.err.println("general path "+generalPath);
    	// JUST AN EXAMPLE

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

        // restore
        graphics.setStroke(stroke);
        graphics.setColor(color);
        graphics.setClip(clip);
    }

    /**
     * Custom annotation rendering.
     */
    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException
    {
    	// JUST AN EXAMPLE

        // save
        saveGraphicsState();
        
        // 35% alpha
        getGraphicsState().setNonStrokeAlphaConstant(0.35);
        super.showAnnotation(annotation);
        
        // restore
        restoreGraphicsState();
    }
}
