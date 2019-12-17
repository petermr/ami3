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

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Util;

/**
 * Example of a custom PDFGraphicsStreamEngine subclass. Allows text and graphics to be processed
 * in a custom manner. This example simply prints the operations to stdout.
 *
 * <p>See {@link PDFStreamEngine} for further methods which may be overridden.
 * 
 * @author John Hewson
 */
public class CustomGraphicsStreamEngine extends PDFGraphicsStreamEngine
{
	private static final Logger LOG = Logger.getLogger(CustomGraphicsStreamEngine.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    public static void main(String[] args) throws IOException
    {
//        File file = new File("src/main/resources/org/apache/pdfbox/examples/rendering/",
//                "custom-render-demo.pdf");
        File file = new File("src/main/resources/org/contentmine/pdf2svg2",
                "custom-render-demo.pdf");

        PDDocument doc = PDDocument.load(file);
        PDPage page = doc.getPage(0);
        CustomGraphicsStreamEngine engine = new CustomGraphicsStreamEngine(page);
        engine.run();
        doc.close();
    }
    
    /**
     * Constructor.
     *
     * @param page PDF Page
     */
    protected CustomGraphicsStreamEngine(PDPage page)
    {
        super(page);
    }

    /**
     * Runs the engine on the current page.
     *
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void run() throws IOException
    {
        processPage(getPage());

        for (PDAnnotation annotation : getPage().getAnnotations())
        {
            showAnnotation(annotation);
        }
    }
    
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException
    {
        System.out.printf("appendRectangle %.2f %.2f, %.2f %.2f, %.2f %.2f, %.2f %.2f\n",
                p0.getX(), p0.getY(), p1.getX(), p1.getY(),
                p2.getX(), p2.getY(), p3.getX(), p3.getY());
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException
    {
        System.out.println("drawImage");
    }

    @Override
    public void clip(int windingRule) throws IOException
    {
        System.out.println("clip");
    }

    @Override
    public void moveTo(float x, float y) throws IOException
    {
        System.out.printf("moveTo %.2f %.2f\n", x, y);
    }

    @Override
    public void lineTo(float x, float y) throws IOException
    {
        System.out.printf("lineTo %.2f %.2f\n", x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException
    {
        System.out.printf("curveTo %.2f %.2f, %.2f %.2f, %.2f %.2f\n", x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint() throws IOException
    {
        // if you want to build paths, you'll need to keep track of this like PageDrawer does
        return new Point2D.Float(0, 0);
    }

    @Override
    public void closePath() throws IOException
    {
        System.out.println("closePath");
    }

    @Override
    public void endPath() throws IOException
    {
        System.out.println("endPath");
    }

    @Override
    public void strokePath() throws IOException
    {
        System.out.println("strokePath");
    }

    @Override
    public void fillPath(int windingRule) throws IOException
    {
        System.out.println("fillPath");
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException
    {
        System.out.println("fillAndStrokePath");
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException
    {
        System.out.println("shadingFill " + shadingName.toString());
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    public void showTextString(byte[] string) throws IOException
    {
        System.out.println("showTextString \"");
        super.showTextString(string);
        System.out.println("\"");
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    public void showTextStrings(COSArray array) throws IOException
    {
        System.out.println("showTextStrings \"");
        super.showTextStrings(array);
        System.out.println("\"");
    }

    /**
     * Overridden from PDFStreamEngine.
     */
    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                             Vector displacement) throws IOException
    {
        System.out.println(unicode+"/"+font+"/"+textRenderingMatrix+"/"+displacement);
        super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);
    }
    

    /**
     * This will initialize and process the contents of the stream.
     *
     * @param page the page to process
     * @throws IOException if there is an error accessing the stream
     */
    public void processPage(PDPage page) throws IOException
    {
    	System.out.println("super.processPage(page)");
    	super.processPage(page);
    }

    /**
     * Shows a transparency group from the content stream.
     *
     * @param form transparency group (form) XObject
     * @throws IOException if the transparency group cannot be processed
     */
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException
    {
    	System.out.println("showTransparencyGroup "+form);
    	super.showTransparencyGroup(form);
    }

    /**
     * Shows a form from the content stream.
     *
     * @param form form XObject
     * @throws IOException if the form cannot be processed
     */
    public void showForm(PDFormXObject form) throws IOException
    {
    	System.out.println("showForm "+form);
    	super.showForm(form);
    }

    /**
     * Processes a soft mask transparency group stream.
     * 
     * @param group the transparency group.
     * 
     * @throws IOException
     */
    protected void processSoftMask(PDTransparencyGroup group) throws IOException
    {
    	System.out.println("processSoftMask "+group);
    	super.processSoftMask(group);
    }

    /**
     * Processes a transparency group stream.
     * 
     * @param group the transparency group.
     * 
     * @throws IOException
     */
    protected void processTransparencyGroup(PDTransparencyGroup group) throws IOException
    {
    	System.out.println("processTransparencyGroup "+group);
    	super.processTransparencyGroup(group);
    }

    /**
     * Processes a Type 3 character stream.
     *
     * @param charProc Type 3 character procedure
     * @param textRenderingMatrix the Text Rendering Matrix
     * @throws IOException if there is an error reading or parsing the character content stream.
     */
    protected void processType3Stream(PDType3CharProc charProc, Matrix textRenderingMatrix)
            throws IOException
    {
    	System.out.println("processType3Stream "+charProc+"/"+textRenderingMatrix);
    	super.processType3Stream(charProc, textRenderingMatrix);
    }

    /**
     * Process the given annotation with the specified appearance stream.
     *
     * @param annotation The annotation containing the appearance stream to process.
     * @param appearance The appearance stream to process.
     * @throws IOException If there is an error reading or parsing the appearance content stream.
     */
    protected void processAnnotation(PDAnnotation annotation, PDAppearanceStream appearance)
            throws IOException
    {
    	System.out.println("processAnnotation "+annotation+"/"+appearance);
    	super.processAnnotation(annotation, appearance);
    }



    /**
     * Shows the given annotation.
     *
     * @param annotation An annotation on the current page.
     * @throws IOException If an error occurred reading the annotation
     */
    public void showAnnotation(PDAnnotation annotation) throws IOException
    {
    	System.out.println("showAnnotation "+annotation);
    	super.showAnnotation(annotation);
    }

    /**
     * Returns the appearance stream to process for the given annotation. May be used to render
     * a specific appearance such as "hover".
     *
     * @param annotation The current annotation.
     * @return The stream to process.
     */
    public PDAppearanceStream getAppearance(PDAnnotation annotation)
    {
    	System.out.println("getAppearance "+annotation);
    	return super.getAppearance(annotation);
    }

    /**
     * Process a child stream of the given page. Cannot be used with {@link #processPage(PDPage)}.
     *
     * @param contentStream the child content stream
     * @param page the current page
     * 
     * @throws IOException if there is an exception while processing the stream
     */
    protected void processChildStream(PDContentStream contentStream, PDPage page) throws IOException
    {
    	System.out.println("processChildStream "+contentStream+"/"+page);
    	super.processChildStream(contentStream, page);
    }

    /**
     * Called when the BT operator is encountered. This method is for overriding in subclasses, the
     * default implementation does nothing.
     *
     * @throws IOException if there was an error processing the text
     */
    public void beginText() throws IOException
    {
    	System.out.println("beginText ");
    	super.beginText();
        // overridden in subclasses
    }

    /**
     * Called when the ET operator is encountered. This method is for overriding in subclasses, the
     * default implementation does nothing.
     *
     * @throws IOException if there was an error processing the text
     */
    public void endText() throws IOException
    {
    	System.out.println("beginText ");
    	super.beginText();
        // overridden in subclasses
    }

    /**
     * Applies a text position adjustment from the TJ operator. May be overridden in subclasses.
     *
     * @param tx x-translation
     * @param ty y-translation
     * 
     * @throws IOException if something went wrong
     */
    protected void applyTextAdjustment(float tx, float ty) throws IOException
    {
    	System.out.println("applyTextAdjustment "+tx+"/"+ty);
    	super.applyTextAdjustment(tx, ty);
    }

    /**
     * Process text from the PDF Stream. You should override this method if you want to
     * perform an action when encoded text is being processed.
     *
     * @param string the encoded text
     * @throws IOException if there is an error processing the string
     */
    protected void showText(byte[] string) throws IOException
    {
    	System.out.println("showText: "+new String(string));
    	super.showText(string);
    }

    /**
     * Called when a glyph is to be processed.This method is intended for overriding in subclasses,
     * the default implementation does nothing.
     *
     * @param textRenderingMatrix the current text rendering matrix, T<sub>rm</sub>
     * @param font the current font
     * @param code internal PDF character code for the glyph
     * @param unicode the Unicode text for this glyph, or null if the PDF does provide it
     * @param displacement the displacement (i.e. advance) of the glyph in text space
     * @throws IOException if the glyph cannot be processed
     */
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException
    {
    	System.out.println("showFontGlyph "+textRenderingMatrix+"/"+font+"/"+code+"/"+unicode+displacement);
    	super.showFontGlyph(textRenderingMatrix, font, code, unicode, displacement);
    }

    /**
     * Called when a glyph is to be processed.This method is intended for overriding in subclasses,
     * the default implementation does nothing.
     *
     * @param textRenderingMatrix the current text rendering matrix, T<sub>rm</sub>
     * @param font the current font
     * @param code internal PDF character code for the glyph
     * @param unicode the Unicode text for this glyph, or null if the PDF does provide it
     * @param displacement the displacement (i.e. advance) of the glyph in text space
     * @throws IOException if the glyph cannot be processed
     */
    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code,
                                  String unicode, Vector displacement) throws IOException
    {
    	System.out.println("showType3Glyph "+textRenderingMatrix+"/"+font+"/"+code+"/"+unicode+displacement);
    	super.showType3Glyph(textRenderingMatrix, font, code, unicode, displacement);
    }

    /**
     * This is used to handle an operation.
     * 
     * @param operation The operation to perform.
     * @param arguments The list of arguments.
     * @throws IOException If there is an error processing the operation.
     */
    public void processOperator(String operation, List<COSBase> arguments) throws IOException
    {
    	System.out.println("processOperator "+operation+"["+arguments.size()+"]");
    	super.processOperator(operation, arguments);
    	System.out.println();
    }

    /**
     * This is used to handle an operation.
     * 
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     * @throws IOException If there is an error processing the operation.
     */
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
    {
    	System.out.println("processOperator "+operator+"["+operands.size()+"]");
    	super.processOperator(operator, operands);
    	System.out.println();
    }

    /**
     * Called when an unsupported operator is encountered.
     *
     * @param operator The unknown operator.
     * @param operands The list of operands.
     * 
     * @throws IOException if something went wrong
     */
    protected void unsupportedOperator(Operator operator, List<COSBase> operands) throws IOException
    {
    	System.out.println("unsupportedOperator "+operator+"/"+operands);
    	super.unsupportedOperator(operator, operands);
    }

    /**
     * Called when an exception is thrown by an operator.
     *
     * @param operator The unknown operator.
     * @param operands The list of operands.
     * @param e the thrown exception.
     * 
     * @throws IOException if something went wrong
     */
    protected void operatorException(Operator operator, List<COSBase> operands, IOException e)
            throws IOException
    {
    	System.out.println("operatorException "+operator+"/"+operands+"/"+e);
    	super.operatorException(operator, operands, e);
    }

    /**
     * Pushes the current graphics state to the stack.
     */
    public void saveGraphicsState()
    {
    	System.out.println("saveGraphicsState");
    	super.saveGraphicsState();
    }

    /**
     * Pops the current graphics state from the stack.
     */
    public void restoreGraphicsState()
    {
    	System.out.println("restoreGraphicsState");
    	super.restoreGraphicsState();
    	System.out.println("GR: "+toString(this.getGraphicsState()));
    }

    private String toString(PDGraphicsState graphicsState) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("lwid:" + graphicsState.getLineWidth() + ";");
    	sb.append("nscol:" + toString(graphicsState.getNonStrokingColor().getComponents()) + ";");
    	sb.append("scol:" + toString(graphicsState.getStrokingColor().getComponents()) + ";");
    	sb.append("textsp:" + graphicsState.getTextState().getCharacterSpacing() + ";");
    	sb.append("fsize:" + graphicsState.getTextState().getFontSize() + ";");
    	return sb.toString();
	}

	private String toString(float[] components) {
		StringBuilder sb = new StringBuilder();
		for (float component : components) {
			sb.append(Util.format(component, 3) + " ");
		}
		return sb.toString();
	}

	/**
     * @param array dash array
     * @param phase dash phase
     */
    public void setLineDashPattern(COSArray array, int phase)
    {
    	System.out.println("setLineDashPattern "+array+"/"+phase);
    	super.setLineDashPattern(array, phase);
    }

    /**
     * Transforms a point using the CTM.
     * 
     * @param x x-coordinate of the point to be transformed.
     * @param y y-coordinate of the point to be transformed.
     * 
     * @return the transformed point.
     */
    public Point2D.Float transformedPoint(float x, float y)
    {
    	System.out.println("transformedPoint "+Util.format(x, 2)+"/"+Util.format(y, 2));
    	return super.transformedPoint(x, y);
    }

    /**
     * Transforms a width using the CTM.
     * 
     * @param width the width value to be transformed.
     * 
     * @return the transformed width value.
     */
    protected float transformWidth(float width)
    {
    	System.out.println("transformWidth "+width);
    	return super.transformWidth(width);
    }
}
