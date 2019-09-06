package org.contentmine.svg2xml.pdf;

/** options for processing in PDFAnalyzer
 * 
 * @author pm286
 *
 */
public class PDFAnalyzerOptions {

	public static final int PLACES = 6;
	
	boolean summarize = false;
	PDFAnalyzer pdfAnalyzer;
// output 	
	boolean outputAnnotatedSvgPages = true;
	boolean outputChunks = false;
	boolean outputHtmlChunks = false;
	boolean outputRawFigureHtml = false;
	boolean outputFooters = false;
	boolean outputHeaders = false;
	boolean outputImages = true;
	boolean outputRawTableHtml = false;
	boolean outputRunningText = true;
	public boolean debugHtmlPage = true;
	public boolean outputHtmlPage = true;

	public boolean annotateChunks = true;

	public boolean skipOutput = true;

	public PDFAnalyzerOptions(PDFAnalyzer pdfAnalyzer) {
		this.pdfAnalyzer = pdfAnalyzer;
	}

	public boolean isOutputAnnotatedSvgPages() {
		return outputAnnotatedSvgPages;
	}

}
