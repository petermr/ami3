/**
 * Parser for PDF events based on CustomPageDrawer from pdfbox2
 * we have subclassed and renamed the classes
 * 
 * CustomPageDrawerFramework (was CustomPageDrawer) 
 * ------------------------------------------------
 * Contains:
 *   MyPageDrawer
 *   MyPageRenderer
 *   
 *   main() calls renderDoc on examples. This can be used to parse your own PDFs
 *   
 *   renderDoc() 
 *      creates a PDDocument (load(file))
 *      renders it through MyPDFRenderer
 *      
 *      iterate through pages of the document 
 *      
 *      File targetDir = new File("target/pdf2svg2/", path);
        targetDir.mkdirs();
		PDDocument doc = PDDocument.load(new File(file, path+".pdf"));
        PDFRenderer renderer = new MyPDFRenderer(doc);
        
        for (int i : pages) {
        	LOG.debug(">> "+i);
        	// we trap the image-drawing part to capture the graphics strokes! 
        	//this will get routed to create SVG as well 
	        BufferedImage image = renderer.renderImage(i);
			ImageIO.write(image, "PNG", new File(targetDir, "page"+i+".png"));
        }
        doc.close();

        MyPageRenderer creates MyPageDrawer
 *   
 * 
 * 
 */
/**
 * @author pm286
 *
 */
package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
