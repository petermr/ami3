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
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.contentmine.ami.tools.AMIPDFTool;
import org.contentmine.ami.tools.AMIPDFTool.PDFTidySVG;
import org.contentmine.graphics.svg.SVGConstants;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;

/**
 * Example showing custom rendering by subclassing PageDrawer.
 * 
 * <p>If you want to do custom graphics processing rather than Graphics2D rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a Graphics2D surface.
 *
 * 
 * @author Peter Murray-Rust
 */
public class PageParserRunner
{
	private static final Logger LOG = Logger.getLogger(PageParserRunner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String TEST_PDFBOX_DIR = 
			"src/test/resources/org/contentmine/graphics/svg/pdfbox";
	private static final String PROJECTS_DIR = 
			"src/test/resources/org/contentmine/projects";
	private static final File OMAR_TEST_DIR = new File(PROJECTS_DIR, "omar/test");
	private static double Y_EPS = 1.0E-5;

	public enum ParserDebug {
		AMI_BRIEF,
		AMI_MEDIUM,
		AMI_FULL,
		ORIGINAL, // Don't use
		AMI_ONE,
		AMI_TWO,
		AMI_ZERO,

	}

	private ParserDebug parserDebug = ParserDebug.ORIGINAL;
	private BufferedImage image;
	private RendererExtractor myPdfRenderer;
	private PDDocument doc;
//	private PDPage currentPage;
	private boolean debug;
	private boolean tidySVG = true;
	private TextParameters lastTextParameters;
	private double minBoldWeight = 500.;
	private File outputPngFile;
	private File outputDir;
    private PageSerial pageSerial;

	private RendererExtractor rendererExtractor;
	private List<PDFTidySVG> tidySVGList;


	public PageParserRunner() {
		
	}

	public PageParserRunner(PDDocument doc, ParserDebug drawerType) {
		this.doc = doc;
		this.setParserDebug(drawerType);
	}
	
	public PageParserRunner(File inputFile, ParserDebug parserDebug, boolean debug) {
		if (inputFile == null || !inputFile.exists()) {
			throw new RuntimeException("null or non-existent file " + inputFile);
		}
		try {
			doc = PDDocument.load(inputFile);
		} catch (IOException e) {
			throw new RuntimeException("cannot read PDF", e);
		}
		this.setParserDebug(parserDebug);
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

	public void setParserDebug(ParserDebug drawerType) {
		this.parserDebug = drawerType;
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
	public BufferedImage createImageAndSVG(int pageIndex) throws IOException, IllegalArgumentException {
		int count = doc.getPages().getCount();
		if (pageIndex < 0 || pageIndex >= count) {
			throw new IllegalArgumentException(
					"bad page index: " + pageIndex + " not in 0-" + (count - 1));
		}
//		currentPage = doc.getPage(pageIndex);
        image = myPdfRenderer.renderImage(pageIndex);
        return image;
	}

	private PDFRenderer readFile(File file) throws IOException {
		doc = PDDocument.load(file);
        myPdfRenderer = new RendererExtractor(doc, parserDebug);
		return myPdfRenderer;
	}

	public void writeImage(File output) throws IOException {
		ImageIO.write(image, "PNG", output);
	}

	public void processPage(int pageIndex) {
		this.setPageSerial(PageSerial.createFromZeroBasedPage(pageIndex));
		myPdfRenderer = new RendererExtractor(doc, parserDebug, pageIndex, this);
		if (pageIndex < 0 || pageIndex >= doc.getNumberOfPages()) {
			throw new IllegalArgumentException("Page out of bounds "+pageSerial);
		}
		// currentPage = doc.getPage(pageIndex);
		
		try {
			image = myPdfRenderer.renderImage(pageIndex);
		} catch (IOException e) {
			throw new RuntimeException("Cannot create image", e);
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public SVGElement getSVG() {
		SVGElement svgElement = null;
		if (parserDebug == ParserDebug.ORIGINAL) {
			// null;
		} else {
			PageDrawer pageDrawer = ((RendererExtractor)myPdfRenderer).getPageDrawer();
			svgElement = ((AbstractPageParser) pageDrawer).getSVGG();
			
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
		List<SVGElement> gTextElements = SVGUtil.getQuerySVGElements(
				svgElement, "//*[local-name()='"+SVGG.TAG+"' and @begin='text']");
		List<SVGG> gTextList = SVGG.extractGs(gTextElements);
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
		runExample(inputFile, outputFile, pageSerial, ParserDebug.ORIGINAL);
	}

	public void runExample(File inputFile, File outputFile, int pageSerial, ParserDebug drawerType)
			throws IOException {
		setParserDebug(drawerType);
	    readFile(inputFile);
	    createImageAndSVG(pageSerial);
	    writeImage(outputFile);
	    close();
	}


	/**
     * Example PDFRenderer subclass, uses MyPageDrawer for custom rendering.
     * (Names changed to RendererExtractor and PageParser to reflect their subclassed roles)
     */
    /*public*/ private static class RendererExtractor extends PDFRenderer
    {
        private ParserDebug parserDebug;
        private PageDrawer pageParser;
		private int pageIndex;
		private PageParserRunner pageParserRunner;

		public PageDrawer getPageDrawer() {
			return pageParser;
		}

		RendererExtractor(PDDocument document, ParserDebug drawer)
        {
			this(document, drawer, -1, null);
        }

		RendererExtractor(PDDocument document, ParserDebug drawer, int pageIndex, PageParserRunner parserRunner)
        {
            super(document);
            this.parserDebug = drawer;
            this.pageIndex = pageIndex;
            this.pageParserRunner = parserRunner;
            pageParserRunner.setRendererExtractor(this);
        }

		@Override
	    public BufferedImage renderImage(int pageIndex) throws IOException
	    {
			this.setPageIndex(pageIndex);
	        BufferedImage renderImage = renderImage(pageIndex, 1);
			AbstractPageParser pageParser = (AbstractPageParser) this.getPageDrawer();
			pageParser.createPageSerial(pageIndex);
			return renderImage;
	    }

		private void setPageIndex(int pageIndex) {
			this.pageIndex = pageIndex;
		}

		/** this actually creates a PageParser but we can't change the signature
		 * 
		 */
        @Override
        protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
        {
        	pageParser = null;
        	if (ParserDebug.ORIGINAL.equals(parserDebug)) {
        		pageParser = new PageParserZero(parameters, -1, AMIDebugParameters.getDefaultParameters());
        	} else if (ParserDebug.AMI_BRIEF.equals(parserDebug)) {
        		AbstractPageParser amiPageDrawer = new PageParserOne(parameters, AMIDebugParameters.getDefaultParameters());
        		amiPageDrawer.getDebugParameters().showAnnotation=false;
        		amiPageDrawer.getDebugParameters().showAppendRectangle=false;
        		amiPageDrawer.getDebugParameters().showBeginText=false;
        		amiPageDrawer.getDebugParameters().showChar=false;
        		amiPageDrawer.getDebugParameters().showClip=false;
        		amiPageDrawer.getDebugParameters().showClosePath=false;
        		amiPageDrawer.getDebugParameters().showColor=false;
        		amiPageDrawer.getDebugParameters().showCurrentPoint=false;
        		amiPageDrawer.getDebugParameters().showDrawPage=false;
        		amiPageDrawer.getDebugParameters().showFillPath=false;
        		amiPageDrawer.getDebugParameters().showFontGlyph=false;
        		amiPageDrawer.getDebugParameters().showForm=false;
        		amiPageDrawer.getDebugParameters().showEndPath=false;
        		amiPageDrawer.getDebugParameters().showEndText=false;
        		amiPageDrawer.getDebugParameters().showLineTo=false;
        		amiPageDrawer.getDebugParameters().showMoveTo=false;
        		amiPageDrawer.getDebugParameters().showStrokePath=false;
				pageParser = amiPageDrawer;
        	} else if (ParserDebug.AMI_ONE.equals(parserDebug)) {
        		AbstractPageParser amiPageDrawer = new PageParserOne(parameters, AMIDebugParameters.getDefaultParameters());
        		amiPageDrawer.getDebugParameters().showAnnotation=false;
        		amiPageDrawer.getDebugParameters().showAppendRectangle=false;
        		amiPageDrawer.getDebugParameters().showBeginText=false;
        		amiPageDrawer.getDebugParameters().showChar=false;
        		amiPageDrawer.getDebugParameters().showClip=false;
        		amiPageDrawer.getDebugParameters().showClosePath=false;
        		amiPageDrawer.getDebugParameters().showColor=false;
        		amiPageDrawer.getDebugParameters().showCurrentPoint=false;
        		amiPageDrawer.getDebugParameters().showDrawPage=false;
        		amiPageDrawer.getDebugParameters().showFillPath=false;
        		amiPageDrawer.getDebugParameters().showFontGlyph=false;
        		amiPageDrawer.getDebugParameters().showForm=false;
        		amiPageDrawer.getDebugParameters().showEndPath=false;
        		amiPageDrawer.getDebugParameters().showEndText=false;
        		amiPageDrawer.getDebugParameters().showLineTo=false;
        		amiPageDrawer.getDebugParameters().showMoveTo=false;
        		amiPageDrawer.getDebugParameters().showStrokePath=false;
				pageParser = amiPageDrawer;
        	} else if (ParserDebug.AMI_TWO.equals(parserDebug)) {
        		AbstractPageParser amiPageDrawer = new PageParserTwo(parameters, pageIndex, AMIDebugParameters.getDefaultParameters());
        		amiPageDrawer.getDebugParameters().showAnnotation=false;
        		amiPageDrawer.getDebugParameters().showAppendRectangle=false;
        		amiPageDrawer.getDebugParameters().showBeginText=false;
        		amiPageDrawer.getDebugParameters().showChar=false;
        		amiPageDrawer.getDebugParameters().showClip=false;
        		amiPageDrawer.getDebugParameters().showClosePath=false;
        		amiPageDrawer.getDebugParameters().showColor=false;
        		amiPageDrawer.getDebugParameters().showCurrentPoint=false;
        		amiPageDrawer.getDebugParameters().showDrawPage=false;
        		amiPageDrawer.getDebugParameters().showFillPath=false;
        		amiPageDrawer.getDebugParameters().showFontGlyph=false;
        		amiPageDrawer.getDebugParameters().showForm=false;
        		amiPageDrawer.getDebugParameters().showEndPath=false;
        		amiPageDrawer.getDebugParameters().showEndText=false;
        		amiPageDrawer.getDebugParameters().showLineTo=false;
        		amiPageDrawer.getDebugParameters().showMoveTo=false;
        		amiPageDrawer.getDebugParameters().showStrokePath=false;
				pageParser = amiPageDrawer;
        	} else if (ParserDebug.AMI_ZERO.equals(parserDebug)) {
        		AbstractPageParser amiPageDrawer = new PageParserZero(parameters, pageIndex, AMIDebugParameters.getDefaultParameters());
        		amiPageDrawer.getDebugParameters().showAnnotation=false;
        		amiPageDrawer.getDebugParameters().showAppendRectangle=false;
        		amiPageDrawer.getDebugParameters().showBeginText=false;
        		amiPageDrawer.getDebugParameters().showChar=false;
        		amiPageDrawer.getDebugParameters().showClip=false;
        		amiPageDrawer.getDebugParameters().showClosePath=false;
        		amiPageDrawer.getDebugParameters().showColor=false;
        		amiPageDrawer.getDebugParameters().showCurrentPoint=false;
        		amiPageDrawer.getDebugParameters().showDrawPage=false;
        		amiPageDrawer.getDebugParameters().showFillPath=false;
        		amiPageDrawer.getDebugParameters().showFontGlyph=false;
        		amiPageDrawer.getDebugParameters().showForm=false;
        		amiPageDrawer.getDebugParameters().showEndPath=false;
        		amiPageDrawer.getDebugParameters().showEndText=false;
        		amiPageDrawer.getDebugParameters().showLineTo=false;
        		amiPageDrawer.getDebugParameters().showMoveTo=false;
        		amiPageDrawer.getDebugParameters().showStrokePath=false;
				pageParser = amiPageDrawer;
        	} else if (ParserDebug.AMI_MEDIUM.equals(parserDebug)) {
        		AbstractPageParser amiPageDrawer = new PageParserOne(parameters, AMIDebugParameters.getDefaultParameters());
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
				pageParser = amiPageDrawer;
        	} else if (ParserDebug.AMI_FULL.equals(parserDebug)) {
        		AbstractPageParser amiPageDrawer = new PageParserOne(parameters, AMIDebugParameters.getDefaultParameters());
				pageParser = amiPageDrawer;
        	} else {
        		throw new RuntimeException("Cannot create PageParser: "+parserDebug);
        	}
        	((AbstractPageParser) pageParser).setPageSerial(this.pageParserRunner.getPageSerial());
        	return pageParser;
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

	public void setRendererExtractor(RendererExtractor rendererExtractor) {
		this.rendererExtractor = rendererExtractor;
	}
	
	public RendererExtractor getRendererExtractor() {
		return rendererExtractor;
	}

	public void run(String root, int pageIndex) throws IllegalArgumentException{

		this.setPageSerial(PageSerial.createFromZeroBasedPage(pageIndex));
		processPage(pageIndex);
		
//		outputDir = AbstractAMITest.PDF2SVG2;
		outputDir = new File("target/pageParser/");
		LOG.warn("creating target/pageParser/ dir - probably contains test data");
		outputDir.mkdirs();
		outputPngFile = new File(outputDir, root+"."+pageIndex+".png");
		
		try {
			ImageIO.write(getImage(), "PNG", outputPngFile);
		} catch (IOException e) {
			throw new RuntimeException("cannot write PNG", e);
		}
		LOG.debug("wrote PNG "+outputPngFile);
		SVGElement svgElement = getSVG();
		SVGSVG.wrapAndWriteAsSVG(svgElement, new File(outputDir, root+"."+pageIndex+".svg"));
	}

    private void setPageSerial(PageSerial pageSerial) {
    	this.pageSerial = pageSerial;
	}
    
	public PageSerial getPageSerial() {
		return pageSerial;
	}


	public void runPages(String root, int pageIndex) {
		if (pageIndex < 0) {
			while(true) {
				try {
					run(root, ++pageIndex);
				} catch (IllegalArgumentException e) {
					System.out.println("quit");
					break;
				}
				if (pageIndex > 100) {
					throw new RuntimeException("shouldn't be here");
				}
			}
		} else {
			run(root, pageIndex);
		}
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
        PageParserRunner drawerExample = new PageParserRunner(inputFile, ParserDebug.AMI_BRIEF, true);
        drawerExample.runExample(inputFile, outputFile, pageSerial, ParserDebug.AMI_BRIEF);
//        drawerExample.runExample(inputFile, outputFile, pageSerial);
	}

	private static void example2() throws IOException {
		File LICHTENBURG = new File(OMAR_TEST_DIR, "lichtenburg19a/");
		File imgDir = new File(LICHTENBURG, "img");
		imgDir.mkdirs();
		File inputFile = new File(LICHTENBURG, "fulltext.pdf");
        PageParserRunner drawerExample = new PageParserRunner(inputFile, ParserDebug.ORIGINAL, false);
        for (int pageSerial = 0; pageSerial < 10; pageSerial++) {
        	File outputFile = new File(imgDir, "fulltext."+pageSerial+".png");
        	System.out.println("wrote: "+outputFile);
        	drawerExample.runExample(inputFile, outputFile, pageSerial);
        }
	}

	public void setTidySVGList(List<AMIPDFTool.PDFTidySVG> tidySVGList) {
		this.tidySVGList = tidySVGList;
	}



}
