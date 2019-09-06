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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;

/**
 * Example showing custom rendering by subclassing PageDrawer.
 * 
 * <p>If you want to do custom graphics processing rather than Graphics2D rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a Graphics2D surface.
 *
 * @author John Hewson
 * @author P Murray-Rust
 * 
 * DocumentProcessor also includes a pageIncluder which 
 */
public class PDFDocumentProcessor {
	private static final String PAGES = "pages";
	public static final Logger LOG = Logger.getLogger(PDFDocumentProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private PageParser pageParser;
	private PDDocument currentDoc;
	private DocumentParser documentParser;
	private File currentFile;
	private Int2 minImageBox;
	private PageIncluder pageIncluder;
	private CTree cTree;
	private String svgDirname = CTree.SVG;
	private String pdfImagesDirname = CTree.PDF_IMAGES_DIR;
	private int maxPages;
	private boolean outputSVG = true;
	private boolean outputPDFImages = true;

	public PDFDocumentProcessor() {
		init();
	}
	
	private void init() {
		clean();
	}
	

	void clean() {
		pageParser = null;
		currentDoc = null;
		documentParser = null;
		currentFile = null;
		minImageBox = null;
		pageIncluder = null;
	}

	public PDFDocumentProcessor readAndProcess(File file) throws IOException {
		if (file != null && file.exists() && !file.isDirectory()) {
			clean();
			PDDocument doc = readDocument(file);
			readAndProcess(doc);
		}
        return this;
	}

	public void readAndProcess(PDDocument doc) throws IOException {
		currentDoc = doc;
		getOrCreateDocumentParser();
		documentParser = new DocumentParser(currentDoc);
		documentParser.clean();
		documentParser.parseDocument(this, currentDoc);
	}

	private DocumentParser getOrCreateDocumentParser() {
		if (documentParser == null) {
			documentParser = new DocumentParser(currentDoc);
		}
		return documentParser;
	}

	private PDDocument readDocument(File file) throws IOException {
		this.currentFile = file;
		currentDoc = PDDocument.load(file);
		return currentDoc;
	}


//	private Map<String, BufferedImage> createRawSubImageList() {
//		this.pageParser = documentParser.getPageParser();
//        return pageParser.getOrCreateRawImageList();
//	}

//	private PDDocument updateCurrentDoc() {
//		if (currentDoc == null && currentFile != null) {
//			try {
//				currentDoc = PDDocument.load(currentFile);
//			} catch (IOException e) {
//				throw new RuntimeException("Cannot read currentFile", e);
//			}
//		}
//		return currentDoc;
//	}

	public PDFDocumentProcessor setFile(File file) {
		this.currentFile = file;
		return this;
	}

	public void writeSVGPages() {
		if(cTree != null) {
			writeSVGPages(cTree.getDirectory());
		}
	}

	public void writeSVGPages(File parent) {
		File svgDir = getOutputSVGDirectory(parent);
		if (documentParser != null && outputSVG) {
			LOG.trace("\nwriting SVG to: "+svgDir);
			Map<PageSerial, SVGG> svgPageBySerial = documentParser.getOrCreateSvgPageBySerial();
			Set<Entry<PageSerial, SVGG>> entrySet = svgPageBySerial.entrySet();
			if (entrySet != null) {
				for (Map.Entry<PageSerial, SVGG> entry : entrySet) {
					PageSerial key = entry.getKey();
					SVGSVG.wrapAndWriteAsSVG(entry.getValue(), new File(svgDir, 
					CTree.FULLTEXT_PAGE+CTree.DOT+key.getZeroBasedSerialString()+CTree.DOT+CTree.SVG));
				}
			}
			LOG.trace("\nwrote SVG to: "+svgDir);
		}
	}

	public File getOutputSVGDirectory(File parent) {
		return new File(parent, getSVGDirname() + "/");
	}

	public String getSVGDirname() {
		return svgDirname;
	}

	public void setSVGDirname(String svgDirname) {
		this.svgDirname = svgDirname;
	}

	public File getOutputImagesDirectory(File parent) {
		return new File(parent, CTree.IMAGES + "/");
	}

	/** creates images from content
	 * probably not mainstream
	 * 
	 * @param parent
	 * @throws IOException
	 */
	public void writePageImages(File parent) throws IOException {
		List<BufferedImage> imageList = documentParser.getOrCreateRenderedImageList();
		File pageDir = new File(parent, CTree.PAGES + "/");
		pageDir.mkdirs();
		int imageCount = imageList.size();
		for (int i = 0; i < imageCount; i++) {
			BufferedImage im = imageList.get(i);
			if (im != null) {
				if (isLargerThanImageBox(im)) {
					ImageIO.write(im, CTree.PNG, new File(pageDir, 
							CTree.createNumberedFullTextPageBasename(i)+CTree.DOT+CTree.PNG));
				}
			}
		}
	}

	public void writePDFImages() throws IOException {
		if (cTree != null) {
			writePDFImages(cTree.getDirectory());
		}
	}
	public void writePDFImages(File parent) throws IOException {
		if (outputPDFImages) {
			File imagesDir = new File(parent, getPDFImagesDirname() + "/");
			imagesDir.mkdirs();
			if (documentParser != null) {
				Map<String, BufferedImage> rawImageByPageSerial = documentParser.getRawImageMap1();
				for (String title : rawImageByPageSerial.keySet()) {
					BufferedImage image = rawImageByPageSerial.get(title);
					File outputFile = new File(imagesDir, title+"."+CTree.PNG);
					// only write if necessary as this is very time consuming. Unlikely to be different 
					if (outputFile.exists()) {
						System.out.print(" skip");
					} else {
						ImageIO.write(image, CTree.PNG, outputFile);
						System.out.print(" img ");
					}
				}
			} else {
				LOG.error("Null document parser");
			}
		}
	}

	public String getPDFImagesDirname() {
		return pdfImagesDirname;
	}
	
	public void setPDFImagesDirname(String pdfImagesDirname) {
		this.pdfImagesDirname =  pdfImagesDirname;
	}

	private boolean isLargerThanImageBox(BufferedImage image) {
		return minImageBox == null || image.getHeight() >= minImageBox.getX() || image.getHeight() >= minImageBox.getY();
	}
	
	/** this runs a test for sanity checking
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
        pdfBox2Test();
    }

	/** tests against regression.
	 * 
	 * @throws IOException
	 */
	private static void pdfBox2Test() throws IOException {
		File file = new File("src/test/resources" + "/org/contentmine/pdf2svg2" + "/",
                "custom-render-demo.pdf");
        
        PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
        documentProcessor.readAndProcess(file);
       List<SVGG> pages = documentProcessor.getOrCreateSVGPageList();
        SVGSVG.wrapAndWriteAsSVG(pages, new File("target/pdf2svg2/examples/custom.svg"));
        BufferedImage renderedImage = documentProcessor.getRenderedImageList().get(0);
        ImageIO.write(renderedImage, "PNG", new File("target/pdf2svg2/examples/custom.ami.png"));
	}

	private List<BufferedImage> getRenderedImageList() {
		return getOrCreateDocumentParser().getOrCreateRenderedImageList();
	}

	public List<SVGG> getOrCreateSVGPageList() {
		return getOrCreateDocumentParser().getOrCreateSVGList();
	}

	public PageIncluder getOrCreatePageIncluder() {
		if (pageIncluder == null) {
			pageIncluder = new PageIncluder();
		}
		return pageIncluder;
	}


	/** smallest box allowed for images.
	 * 
	 * image will be rejected if fits within the box
	 * @param i
	 * @param j
	 */
	public void setMinimumImageBox(int width, int height) {
		this.minImageBox = new Int2(width, height);
	}

	public void setCTree(CTree cTree) {
		this.cTree = cTree;
	}

	public void setMaxPages(int maxpages) {
		this.maxPages = maxpages;
	}

	public int getMaxPages() {
		return maxPages;
	}

	public boolean isOutputSVG() {
		return outputSVG;
	}

	public void setOutputSVG(boolean outputSVG) {
		this.outputSVG = outputSVG;
	}

	public boolean isOutputPDFImages() {
		return outputPDFImages;
	}

	public void setOutputPDFImages(boolean outputPDFImages) {
		this.outputPDFImages = outputPDFImages;
	}
	

}
