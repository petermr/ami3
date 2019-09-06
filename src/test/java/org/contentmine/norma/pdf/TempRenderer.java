package org.contentmine.norma.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

/**
 * Example PDFRenderer subclass, uses WellDrawer for custom rendering.
 */
public class TempRenderer extends PDFRenderer {
    TempRenderer(PDDocument document)
    {
        super(document);
    }

    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
    {
        return new TempDrawer(parameters);
    }
}
