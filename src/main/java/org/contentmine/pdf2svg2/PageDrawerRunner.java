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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.svg.GraphicsElement.FontStyle;
import org.contentmine.graphics.svg.GraphicsElement.FontWeight;
import org.contentmine.graphics.svg.SVGConstants;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;

import nu.xom.Element;

/**
 * Example showing custom rendering by subclassing PageDrawer.
 * 
 * <p>If you want to do custom graphics processing rather than Graphics2D rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a Graphics2D surface.
 *
 * @author John Hewson
 */
public class PageDrawerRunner
{
	private static final Logger LOG = Logger.getLogger(PageDrawerRunner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String TEST_PDFBOX_DIR = 
			"src/test/resources/org/contentmine/graphics/svg/pdfbox";
	private static final String PROJECTS_DIR = 
			"src/test/resources/org/contentmine/projects";
	private static final File OMAR_TEST_DIR = new File(PROJECTS_DIR, "omar/test");
	private static double Y_EPS = 1.0E-5;

	public enum DrawerType {
		AMI_BRIEF,
		AMI_MEDIUM,
		AMI_FULL,
		ORIGINAL, // Don't use
	}

	private DrawerType drawerType = DrawerType.ORIGINAL;
	private BufferedImage image;
	private MyPDFRenderer myPdfRenderer;
	private PDDocument doc;
	private PDPage currentPage;
	private boolean debug;
	private boolean tidySVG = true;
//	private SVGText currentSVGText;
	private TextParameters lastTextParameters;
	private double minBoldWeight = 500.;

	public PageDrawerRunner() {
		
	}

	public PageDrawerRunner(PDDocument doc, DrawerType drawerType) {
		this.doc = doc;
		this.setDrawerType(drawerType);
	}
	
	public PageDrawerRunner(File inputFile, DrawerType drawerType, boolean debug) {
		try {
			doc = PDDocument.load(inputFile);
		} catch (IOException e) {
			throw new RuntimeException("cannot read PDF", e);
		}
		this.setDrawerType(drawerType);
		this.setDebug(debug);
		
	}

	public boolean isTidySVG() {
		return tidySVG;
	}

	public void setTidySVG(boolean tidySVG) {
		this.tidySVG = tidySVG;
	}

	private void setDebug(boolean debug) {
		this.debug= debug;
	}

	public void setDrawerType(DrawerType drawerType) {
		this.drawerType = drawerType;
	}
	
	public void closeDoc() {
		try {
			doc.close();
		} catch (IOException e) {
			throw new RuntimeException("cannot close doc ", e);
		}
	}

	/** parses page, creates Image and SVG
	 * 
	 * @param pageSerial
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public BufferedImage createImageAndSVG(int pageSerial) throws IOException, IllegalArgumentException {
		int count = doc.getPages().getCount();
		if (pageSerial < 0 || pageSerial >= count) {
			throw new IllegalArgumentException(
					"bad page index: " + pageSerial + " not in 0-" + (count - 1));
		}
		currentPage = doc.getPage(pageSerial);
        image = myPdfRenderer.renderImage(pageSerial);
        return image;
	}

	private PDFRenderer readFile(File file) throws IOException {
		doc = PDDocument.load(file);
        return createPDFRenderer(doc, drawerType);
	}

	public void writeImage(File output) throws IOException {
		ImageIO.write(image, "PNG", output);
	}

	/** creates a MyPDFRenderer */
	public MyPDFRenderer createPDFRenderer(PDDocument doc, DrawerType drawerType) {
		myPdfRenderer = new MyPDFRenderer(doc, drawerType);
		return myPdfRenderer;
	}
	
	public void processPage(int pageSerial) throws IOException {
		createPDFRenderer(doc, drawerType);
		if (pageSerial < 0 || pageSerial >= doc.getNumberOfPages()) {
			throw new IllegalArgumentException("Page out of bounds "+pageSerial);
		}
		currentPage = doc.getPage(pageSerial);
		PageDrawer pageDrawer = ((MyPDFRenderer)myPdfRenderer).getPageDrawer();
		if (pageDrawer instanceof AMIPageDrawer) {
			((AMIPageDrawer)pageDrawer).setCurrentPage(currentPage);
		}
		image = myPdfRenderer.renderImage(pageSerial);
	}

	public BufferedImage getImage() {
		return image;
	}

	public SVGElement getSVG() {
		SVGElement svgElement = null;
		if (drawerType == DrawerType.ORIGINAL) {
			// null;
		} else {
			PageDrawer pageDrawer = ((MyPDFRenderer)myPdfRenderer).getPageDrawer();
			svgElement = ((AMIPageDrawer) pageDrawer).getSVGElement();
			
		}
		if (debug) {
			// output intermediate
		}
		if (tidySVG) {
			tidyGTextDescendants(svgElement);
		}
		return svgElement;
	}


	private void tidyGTextDescendants(SVGElement svgElement) {
		LOG.debug("SVG: "+svgElement.toXML());
		List<SVGElement> gTextElements = SVGUtil.getQuerySVGElements(
				svgElement, "//*[local-name()='"+SVGG.TAG+"' and @begin='text']");
		List<SVGG> gTextList = SVGG.extractGs(gTextElements);
		LOG.debug("GText descendants: "+gTextElements.size());
		for (SVGG gText : gTextList) {
			tidyGText(gText);
		}
		svgElement.addNamespaceDeclaration(SVGConstants.SVGX_PREFIX,SVGConstants.SVGX_NS);

	}

	private void tidyGText(SVGG gText) {
		List<SVGElement> textList0 = SVGUtil.getQuerySVGElements(gText, "*[local-name()='"+SVGText.TAG+"']");
		List<SVGText> textList = new ArrayList<>(); 
		Iterator<SVGElement> textIterator = textList0.iterator();
		lastTextParameters = null;
		SVGText lastText = null;
		SVGText newText = null;
		while (textIterator.hasNext()) {
			SVGText text = (SVGText) textIterator.next();
			TextParameters textParameters = new TextParameters(text); 
			if (mustBreakText(lastTextParameters, textParameters, lastText, text)) {
				text.detach();
	    		newText = text;
	    		gText.appendChild(newText);
			} else {
				text.detach();
				newText.appendText(text.getValue());
		    	newText.appendFontWidth(text.getSVGXFontWidth());
		    	newText.appendX(text.getX());
		    	newText.setY(text.getY());
			}

//	        // bbox in EM -> user units
//	        Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
//	        AffineTransform at = textRenderingMatrix.createAffineTransform();
//	        bbox = at.createTransformedShape(bbox);
	                
//	        saveUpdatedParameters(scales, x, y);
	        lastText = text;
	        lastTextParameters = textParameters;
		}
	}
	private boolean mustBreakText(TextParameters lastTextParameters, TextParameters textParameters,
			SVGText lastText, SVGText text) {
    	if (lastText == null || lastTextParameters == null) {
    		return true;
    	} else if (!lastText.hasSameStyle(text)) {
    		return true;
    	} else if (!textParameters.hasNormalOrientation()) {
    		return true;
    	} else if (!text.hasSameY(lastText, Y_EPS)) {
    		return true;
    	} else if (textParameters.isScaleChanged(lastTextParameters)) {
    		return true;
    	} else {
    		return false;
    	}
	}

	private void close() throws IOException {
    	doc.close();
	}

	public void runExample(File inputFile, File outputFile, int pageSerial) throws IOException {
		runExample(inputFile, outputFile, pageSerial, DrawerType.ORIGINAL);
	}

	public void runExample(File inputFile, File outputFile, int pageSerial, DrawerType drawerType)
			throws IOException {
		setDrawerType(drawerType);
	    readFile(inputFile);
	    createImageAndSVG(pageSerial);
	    writeImage(outputFile);
	    close();
	}

    public static void main(String[] args) throws IOException
    {
        example1();
        example2();
    }

	public static void example1() throws IOException {
		File inputFile = new File(TEST_PDFBOX_DIR, "custom-render-demo.pdf");
        File outputFile = new File(TEST_PDFBOX_DIR, "custom-render-demo.png");
        int pageSerial = 0;
        PageDrawerRunner drawerExample = new PageDrawerRunner(inputFile, DrawerType.AMI_BRIEF, true);
        drawerExample.runExample(inputFile, outputFile, pageSerial, DrawerType.AMI_BRIEF);
//        drawerExample.runExample(inputFile, outputFile, pageSerial);
	}

	private static void example2() throws IOException {
		File LICHTENBURG = new File(OMAR_TEST_DIR, "lichtenburg19a/");
		File imgDir = new File(LICHTENBURG, "img");
		imgDir.mkdirs();
		File inputFile = new File(LICHTENBURG, "fulltext.pdf");
        PageDrawerRunner drawerExample = new PageDrawerRunner(inputFile, DrawerType.ORIGINAL, false);
        for (int pageSerial = 0; pageSerial < 10; pageSerial++) {
        	File outputFile = new File(imgDir, "fulltext."+pageSerial+".png");
        	System.out.println("wrote: "+outputFile);
        	drawerExample.runExample(inputFile, outputFile, pageSerial);
        }
	}


	/**
     * Example PDFRenderer subclass, uses MyPageDrawer for custom rendering.
     */
    private static class MyPDFRenderer extends PDFRenderer
    {
        private DrawerType drawerType;
        private PageDrawer pageDrawer;

		public PageDrawer getPageDrawer() {
			return pageDrawer;
		}

		MyPDFRenderer(PDDocument document, DrawerType drawer)
        {
            super(document);
            this.drawerType = drawer;
        }

        @Override
        protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
        {
        	pageDrawer = null;
        	if (DrawerType.ORIGINAL.equals(drawerType)) {
        		pageDrawer = new MyPageDrawer(parameters);
        	} else if (DrawerType.AMI_BRIEF.equals(drawerType)) {
        		AMIPageDrawer amiPageDrawer = new AMIPageDrawer(parameters, AMIDebugParameters.getDefaultParameters());
        		amiPageDrawer.getDebugParameters().showAnnotation=false;
        		amiPageDrawer.getDebugParameters().showAppendRectangle=false;
        		amiPageDrawer.getDebugParameters().showBeginText=false;
        		amiPageDrawer.getDebugParameters().showChar=false;
        		amiPageDrawer.getDebugParameters().showClip=false;
        		amiPageDrawer.getDebugParameters().showClosePath=false;
        		amiPageDrawer.getDebugParameters().showColor=false;
        		amiPageDrawer.getDebugParameters().showCurrentPoint=false;
        		amiPageDrawer.getDebugParameters().showDrawPage=false;
        		amiPageDrawer.getDebugParameters().showFontGlyph=true;
        		amiPageDrawer.getDebugParameters().showForm=false;
        		amiPageDrawer.getDebugParameters().showEndPath=false;
        		amiPageDrawer.getDebugParameters().showEndText=false;
        		amiPageDrawer.getDebugParameters().showLineTo=false;
        		amiPageDrawer.getDebugParameters().showMoveTo=false;
        		amiPageDrawer.getDebugParameters().showStrokePath=false;
				pageDrawer = amiPageDrawer;
        	} else if (DrawerType.AMI_MEDIUM.equals(drawerType)) {
        		AMIPageDrawer amiPageDrawer = new AMIPageDrawer(parameters, AMIDebugParameters.getDefaultParameters());
        		amiPageDrawer.getDebugParameters().showAnnotation=false;
//        		amiPageDrawer.getDebugParameters().showAppendRectangle=false;
//        		amiPageDrawer.getDebugParameters().showBeginText=false;
//        		amiPageDrawer.getDebugParameters().showChar=false;
//        		amiPageDrawer.getDebugParameters().showClip=false;
//        		amiPageDrawer.getDebugParameters().showClosePath=false;
//        		amiPageDrawer.getDebugParameters().showColor=false;
//        		amiPageDrawer.getDebugParameters().showDrawPage=false;
        		amiPageDrawer.getDebugParameters().showCurrentPoint=false;
//        		amiPageDrawer.getDebugParameters().showFontGlyph=true;
        		amiPageDrawer.getDebugParameters().showForm=false;
//        		amiPageDrawer.getDebugParameters().showEndPath=false;
//        		amiPageDrawer.getDebugParameters().showEndText=false;
//        		amiPageDrawer.getDebugParameters().showLineTo=false;
//        		amiPageDrawer.getDebugParameters().showMoveTo=false;
        		amiPageDrawer.getDebugParameters().showStrokePath=false;
				pageDrawer = amiPageDrawer;
        	} else if (DrawerType.AMI_FULL.equals(drawerType)) {
        		AMIPageDrawer amiPageDrawer = new AMIPageDrawer(parameters, AMIDebugParameters.getDefaultParameters());
				pageDrawer = amiPageDrawer;
        	}
        	return pageDrawer;
        }
    }
    
	private void addCurrentTextAttributes(SVGText text) {
//		setFillAndStrokeFromGraphics2D(text);
//		Real2 scales = textParameters.getScales();
//		// at present I think only the PDFont matters
//		PDTextState textState = getGraphicsState().getTextState();
//		PDFont font = textState.getFont();
//		PDFontDescriptor fontDescriptor = font.getFontDescriptor();
//
//		/** not sure how this works
//		 * sometimes fontSize2 is 1.0, and we need scales
//		 */
//		setFontSize(text, scales, textState);
//
//		String fontName = font.getName();
//		fontName = removeArbitraryPrefix(fontName);
//		text.setFontFamily(fontName);
//		
//		// these *might* be useful
//		try {
//			font.getDisplacement(codepoint);
//			font.getBoundingBox();
////			font.getPositionVector(code); // pnly vertical
//		} catch (Exception e) {throw new RuntimeException(e);}
//		
//		// these *might* be useful
//		if (fontDescriptor != null) {
//			fontDescriptor.getItalicAngle();
//			fontDescriptor.isAllCap();
//			fontDescriptor.isForceBold();
//			fontDescriptor.isItalic();
//			fontDescriptor.isSmallCap();
//		}
	}

	public void run(String root, int pageSerial) throws IOException , IllegalArgumentException{
		processPage(pageSerial);
		
		File outputPng = new File(AbstractAMITest.PDF2SVG2, root+"."+pageSerial+".png");
		ImageIO.write(getImage(), "PNG", outputPng);
		CustomPageDrawerTest.LOG.debug("wrote PNG "+outputPng);
		SVGElement svgElement = getSVG();
		SVGSVG.wrapAndWriteAsSVG(svgElement, new File(AbstractAMITest.PDF2SVG2, root+"."+pageSerial+".svg"));
	}


}
