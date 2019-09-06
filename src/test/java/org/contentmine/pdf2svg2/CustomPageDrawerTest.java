package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CustomPageDrawerTest {
	private static final Logger LOG = Logger.getLogger(CustomPageDrawerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore // doesn't call renderer properly // better examples exist
	public void testCombinedAWTSVG() throws IOException {
//        File file = new File("src/main/resources/org/apache/pdfbox/examples/rendering/",
//                "custom-render-demo.pdf");
        File file = new File("src/main/resources/org/contentmine/pdf2svg2/",
                "custom-render-demo.pdf");
        Assert.assertTrue(file.exists());
        
        PDDocument doc = PDDocument.load(file);
        PDFRenderer renderer = new MyPDFRenderer(doc);
        BufferedImage image = renderer.renderImage(0);
        ImageIO.write(image, "PNG", new File("custom-render.png"));
        doc.close();
	}

}
