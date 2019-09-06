package org.contentmine.pdf2svg2;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

/**
 * Example PDFRenderer subclass, uses MyPageDrawer for custom rendering.
 */
public class MyPDFRenderer extends PDFRenderer
{
    MyPDFRenderer(PDDocument document)
    {
        super(document);
    }

    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
    {
//        return new CustomPageDrawerNew(parameters);
    	throw new RuntimeException("NYI");
    }
}

