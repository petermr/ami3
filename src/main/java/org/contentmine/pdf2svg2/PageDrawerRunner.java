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

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.contentmine.graphics.svg.SVGElement;

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

	public enum DrawerType {
		AMI_BRIEF,
		AMI_MEDIUM,
		ORIGINAL,
		
	}

	private DrawerType drawerType = DrawerType.ORIGINAL;
	private BufferedImage image;
	private MyPDFRenderer myPdfRenderer;
	private PDDocument doc;
	private PDPage currentPage;

	public PageDrawerRunner() {
		
	}

	public PageDrawerRunner(PDDocument doc, DrawerType drawerType) {
		this.doc = doc;
		this.setDrawerType(drawerType);
	}
	
	public PageDrawerRunner(File inputFile, DrawerType drawerType) {
		try {
			doc = PDDocument.load(inputFile);
		} catch (IOException e) {
			throw new RuntimeException("cannot read PDF", e);
		}
		this.setDrawerType(drawerType);
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
		return svgElement;
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
        PageDrawerRunner drawerExample = new PageDrawerRunner(inputFile, DrawerType.AMI_BRIEF);
        drawerExample.runExample(inputFile, outputFile, pageSerial, DrawerType.AMI_BRIEF);
//        drawerExample.runExample(inputFile, outputFile, pageSerial);
	}

	private static void example2() throws IOException {
		File LICHTENBURG = new File(OMAR_TEST_DIR, "lichtenburg19a/");
		File imgDir = new File(LICHTENBURG, "img");
		imgDir.mkdirs();
		File inputFile = new File(LICHTENBURG, "fulltext.pdf");
        PageDrawerRunner drawerExample = new PageDrawerRunner(inputFile, DrawerType.ORIGINAL);
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
        		amiPageDrawer.getDebugParameters().showCurrentPoint=false;
//        		amiPageDrawer.getDebugParameters().showFontGlyph=true;
        		amiPageDrawer.getDebugParameters().showForm=false;
//        		amiPageDrawer.getDebugParameters().showEndPath=false;
//        		amiPageDrawer.getDebugParameters().showEndText=false;
//        		amiPageDrawer.getDebugParameters().showLineTo=false;
//        		amiPageDrawer.getDebugParameters().showMoveTo=false;
        		amiPageDrawer.getDebugParameters().showStrokePath=false;
				pageDrawer = amiPageDrawer;
        	}
        	return pageDrawer;
        }
    }

}
