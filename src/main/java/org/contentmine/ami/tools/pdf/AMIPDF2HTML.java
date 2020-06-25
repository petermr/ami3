package org.contentmine.ami.tools.pdf;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.PDFText2HTML;

public class AMIPDF2HTML extends PDFText2HTML {
	private static final Logger LOG = LogManager.getLogger(AMIPDF2HTML.class);
public AMIPDF2HTML() throws IOException {
		super();
	}
	
	public static AMIPDF2HTML createAMIPDF2HTML() {
		AMIPDF2HTML amipdf2html = null;
		try {
			amipdf2html = new AMIPDF2HTML();
		} catch (IOException e) {
			throw new RuntimeException("BUG initialising PDFText2HTML", e);
		}
		return amipdf2html;
	}

	public void readDocument(PDDocument document) throws IOException {
		LOG.debug(">DOC>"+document);
		super.startDocument(document);
		System.err.println("TITLE: "+super.getTitle());
		super.endDocument(document);
	}
}
