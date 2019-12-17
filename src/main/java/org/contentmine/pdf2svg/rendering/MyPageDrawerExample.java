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

package org.contentmine.pdf2svg.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.eclipse.jetty.util.log.Log;

/**
 * Example showing custom rendering by subclassing PageDrawer.
 * 
 * <p>If you want to do custom graphics processing rather than Graphics2D rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a Graphics2D surface.
 *
 * @author John Hewson
 */
public class MyPageDrawerExample
{
	private static final Logger LOG = Logger.getLogger(MyPageDrawerExample.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String TEST_PDFBOX_DIR = 
			"src/test/resources/org/contentmine/graphics/svg/pdfbox";
	private static final String PROJECTS_DIR = 
			"src/test/resources/org/contentmine/projects";
	private static final File OMAR_TEST_DIR = new File(PROJECTS_DIR, "omar/test");

	public enum Drawer {
		AMI,
		ORIGINAL,
		
	}

	private Drawer drawer = Drawer.ORIGINAL;
	private BufferedImage image;
	private PDFRenderer renderer;
	private PDDocument doc;

	public void setPageDrawer(Drawer drawer) {
		this.drawer = drawer;
	}

	public BufferedImage createImage(int pageSerial) throws IOException, IllegalArgumentException {
		int count = doc.getPages().getCount();
		if (pageSerial < 0 || pageSerial >= count) {
			throw new IllegalArgumentException(
					"bad page index: " + pageSerial + " not in 0-" + (count - 1));
		}
        image = renderer.renderImage(pageSerial);
        return image;
	}

	private PDFRenderer readFile(File file) throws IOException {
		doc = PDDocument.load(file);
        renderer = new MyPDFRenderer(doc, drawer);
		return renderer;
	}

	public void writeImage(File output) throws IOException {
		ImageIO.write(image, "PNG", output);
	}

    public static void main(String[] args) throws IOException
    {
        example1();
        example2();
    }

	private static void example1() throws IOException {
		File inputFile = new File(TEST_PDFBOX_DIR, "custom-render-demo.pdf");
        File outputFile = new File(TEST_PDFBOX_DIR, "custom-render-demo.png");
        int pageSerial = 0;
        MyPageDrawerExample drawerExample = new MyPageDrawerExample();
        drawerExample.runExample(inputFile, outputFile, pageSerial, Drawer.ORIGINAL);
	}

	private static void example2() throws IOException {
		File LICHTENBURG = new File(OMAR_TEST_DIR, "lichtenburg19a/");
		File imgDir = new File(LICHTENBURG, "img");
		imgDir.mkdirs();
		File inputFile = new File(LICHTENBURG, "fulltext.pdf");
        MyPageDrawerExample drawerExample = new MyPageDrawerExample();
        for (int pageSerial = 0; pageSerial < 10; pageSerial++) {
        	File outputFile = new File(imgDir, "fulltext."+pageSerial+".png");
        	System.out.println("wrote: "+outputFile);
        	drawerExample.runExample(inputFile, outputFile, pageSerial, Drawer.ORIGINAL);
        }
	}

	private void close() throws IOException {
    	doc.close();
	}

	public void runExample(File inputFile, File outputFile, int pageSerial) throws IOException {
		runExample(inputFile, outputFile, pageSerial, Drawer.ORIGINAL);
	}

	public void runExample(File inputFile, File outputFile, int pageSerial, Drawer drawer)
			throws IOException {
		setPageDrawer(drawer);
	    readFile(inputFile);
	    createImage(pageSerial);
	    writeImage(outputFile);
	    close();
	}

	/**
     * Example PDFRenderer subclass, uses MyPageDrawer for custom rendering.
     */
    private static class MyPDFRenderer extends PDFRenderer
    {
        private Drawer drawer;

		MyPDFRenderer(PDDocument document, Drawer drawer)
        {
            super(document);
            this.drawer = drawer;
        }

        @Override
        protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
        {
        	if (Drawer.ORIGINAL.equals(drawer)) {
        		return new MyPageDrawer(parameters);
        	}
        	if (Drawer.AMI.equals(drawer)) {
        		return new AmiPageDrawer(parameters);
        	}
        	return null;
        }
    }


}
