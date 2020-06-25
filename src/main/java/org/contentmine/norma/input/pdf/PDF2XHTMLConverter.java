package org.contentmine.norma.input.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.svg2xml.PDF2SVGConverter;
import org.contentmine.svg2xml.pdf.PDFAnalyzer;

import nu.xom.Element;

/** 
 * Convert PDF to XHTML, SVG and PNG.
 * 
 * @author pm286
 */
public class PDF2XHTMLConverter {

	private final static Logger LOG = LogManager.getLogger(PDF2XHTMLConverter.class);
private static final String SVG = "svg";
	private List<SVGSVG> svgList;
	private File svgDirectory;

	private CTree cmTree;
	private PDFAnalyzer pdfAnalyzer;
	public PDF2XHTMLConverter() {
		
	}

	public PDF2XHTMLConverter(CTree cmTree) {
		this.cmTree = cmTree;
	}

	public HtmlElement readAndConvertToXHTML(File infile) throws Exception {
	     svgList = readAndConvertToSVGList(infile);
	 	 HtmlElement htmlElement = readAndConvertToXHTML(svgList);
	 	 return htmlElement;
	}

	public HtmlElement readAndConvertToXHTML(URL url) throws Exception {
	     List<SVGSVG> svgList = readAndConvertToSVGList(url);
	     HtmlElement htmlElement = readAndConvertToXHTML(svgList);
	 	 return htmlElement;
	}

	public HtmlElement readAndConvertToXHTML(InputStream is) throws Exception {
	     List<SVGSVG> svgList = readAndConvertToSVGList(is);
	     HtmlElement htmlElement = readAndConvertToXHTML(svgList);
	 	 return htmlElement;
	}

	public List<SVGSVG> readAndConvertToSVGList(File infile) throws Exception {
		LOG.warn("PDFAnalyzer not refactored into svghtml");
		PDF2SVGConverter converter = createAndSetConverter();
		LOG.error("FIXME");
//		converter.openPDFFile(infile);
//		List<SVGSVG> svgList = converter.getPageList();
//		return svgList;
		return null;
	}

	public List<SVGSVG> readAndConvertToSVGList(URL url) throws Exception {
		LOG.warn("PDFAnalyzer not refactored into svghtml");
		PDF2SVGConverter converter = createAndSetConverter();
		LOG.error("FIXME");
//		converter.openPDFURL(url);
//		List<SVGSVG> svgList = converter.getPageList();
//		return svgList;
		return null;
	}

	public List<SVGSVG> readAndConvertToSVGList(InputStream is) throws Exception {
		LOG.warn("PDFAnalyzer not refactored into svghtml");
		PDF2SVGConverter converter = createAndSetConverter();
		throw new RuntimeException("FIXME");
//		converter.openPDFInputStream(is);
//		List<SVGSVG> svgList = converter.getPageList();
//		return svgList;
	}

	private PDF2SVGConverter createAndSetConverter() {
		LOG.warn("PDFAnalyzer not refactored into svghtml");
		PDF2SVGConverter converter = new PDF2SVGConverter();
		throw new RuntimeException("FIXME");

//		converter.setWriteFile(false);
//		converter.setStoreSVG(true);
//		return converter;
	}

	public HtmlElement readAndConvertToXHTML(List<SVGSVG> svgList) throws Exception {
		HtmlElement element = null;
		ensurePDFAnalyzer();
		LOG.debug("svg "+svgDirectory);
		pdfAnalyzer.getPDFIO().setRawSVGDirectory(svgDirectory);
		pdfAnalyzer.createAndFillPageAnalyzers(svgList);
		element = pdfAnalyzer.forceCreateRunningHtml();
		element = normalizeEmptyParagraphs(element);
		return element;
	}

	private HtmlElement normalizeEmptyParagraphs(HtmlElement element) {
		List<HtmlElement> divEmptyPList = HtmlUtil.getQueryHtmlElements(element, 
				"//*[local-name()='div' and *[local-name()='p' and normalize-space(.)='']]");
		LOG.debug("emptyPList "+divEmptyPList.size());
		for (HtmlElement div : divEmptyPList) {
			List<Element> pList = XMLUtil.getQueryElements(div, "//*[local-name()='p' and normalize-space(.)='']");
			LOG.debug("p "+pList.size()+" "+pList.get(0)+" "+pList.get(0).getClass());
		}
		return element;
	}

//	private void writeSVGList() {
//		if (svgDirectory != null) {
//			svgDirectory.mkdirs();
//			int i = 1;
//			for (SVGSVG svg : svgList) {
//				File svgFile = new File(svgDirectory, getPageString()+(i++)+"."+SVG);
//				try {
//					FileUtils.write(svgFile, svg.toXML(), , CMineUtil.UTF8_CHARSET);
//				} catch (IOException e) {
//					throw new RuntimeException("Cannot write svg file", e);
//				}
//			}
//		}
//	}

	private String getPageString() {
		return "page_";
	}

	public File getSvgDirectory() {
		return svgDirectory;
	}

	public void setSvgDirectory(File svgDirectory) {
		this.svgDirectory = svgDirectory;
	}

	public HtmlElement readAndConvertToXHTML() {
		HtmlElement htmlElement = null;
		if (cmTree != null) {
			File fulltextHtmlFile = cmTree.getReservedFile(cmTree.FULLTEXT_XHTML);
			File fulltextPDF = cmTree.getExistingFulltextPDF();
			svgDirectory = cmTree.getReservedDirectory(SVG);
			LOG.debug("svg dir "+svgDirectory);			
			if (fulltextPDF != null) {
				try {
					ensurePDFAnalyzer();
					pdfAnalyzer.getPDFIO().setRawSVGDirectory(svgDirectory);
					htmlElement = this.readAndConvertToXHTML(fulltextPDF);
					if (htmlElement != null) {
						XMLUtil.debug(htmlElement, new FileOutputStream(fulltextHtmlFile), 1);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Cannot convert PDF", e);
				}
			}
		}
		return htmlElement;
	}

	private void ensurePDFAnalyzer() {
		if (this.pdfAnalyzer == null) {
			this.pdfAnalyzer = new PDFAnalyzer();
		}
	}
}
