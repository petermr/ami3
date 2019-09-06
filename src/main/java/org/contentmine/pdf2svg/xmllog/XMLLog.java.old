package org.contentmine.pdf2svg.xmllog;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.contentmine.pdf2svg.xmllog.model.PDFChar;
import org.contentmine.pdf2svg.xmllog.model.PDFCharPath;
import org.contentmine.pdf2svg.xmllog.model.PDFFile;
import org.contentmine.pdf2svg.xmllog.model.PDFFileList;
import org.contentmine.pdf2svg.xmllog.model.PDFFont;
import org.contentmine.pdf2svg.xmllog.model.PDFFontList;
import org.contentmine.pdf2svg.xmllog.model.PDFPage;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class XMLLog {

	private final static Logger LOG = Logger.getLogger(XMLLog.class);

	private static final String BASEFONT = "basefont";
	private static final String BOLD = "bold";
	private static final String CHARACTER = "character";
	private static final String CODE = "code";
	private static final String D = "d";
	private static final String ENCODING = "encoding";
	private static final String FAMILY = "family";
	private static final String FILENAME = "filename";
	private static final String FILL = "fill";
	private static final String FONT = "font";
	private static final String FONTENCODING = "fontencoding";
	private static final String FONT_LIST = "fontList";
	private static final String GLYPHS = "glyphs";
	private static final String ITALIC = "italic";
	private static final String NAME = "name";
//	private static final String NONE = "<none>";
	private static final String NONE = null;
	private static final String NUM = "num";
	private static final String PAGE = "page";
	private static final String PAGE_COUNT = "pageCount";
	private static final String PATH = "path";
	private static final String PDF = "pdf";
	private static final String STROKE = "stroke";
	private static final String STROKE_WIDTH = "stroke-width";
	private static final String SYMBOL = "symbol";
	private static final String TYPE = "type";
	
	private boolean logglyphs = false;
	private PDFFontList fonts = new PDFFontList();
	private PDFFileList pdfs = new PDFFileList();
	private String deletePath;
	private Element root;

	public boolean isLogglyphs() {
		return logglyphs;
	}

	public void setLogglyphs(boolean logglyphs) {
		this.logglyphs = logglyphs;
	}

	public PDFFontList getFonts() {
		return fonts;
	}

	public void setFonts(PDFFontList fonts) {
		this.fonts = fonts;
	}

	public PDFFileList getPdfs() {
		return pdfs;
	}

	public void setPdfs(PDFFileList pdfs) {
		this.pdfs = pdfs;
	}

	void setDeletePath(String deletePath) {
		this.deletePath = deletePath;
	}

	public void load(String filename) throws ValidityException,
			ParsingException, IOException {

		fonts.clear();
		pdfs.clear();

		Document doc = new Builder().build(new File(filename));

		root = doc.getRootElement();
		deleteNodes(deletePath);

		Elements fontlists = root.getChildElements(FONT_LIST);
		assert (fontlists.size() == 1);

		Elements fontlist = fontlists.get(0).getChildElements(FONT);

		logglyphs = Boolean.parseBoolean(root.getAttributeValue(GLYPHS));

		int nfonts = fontlist.size();
		for (int i = 0; i < nfonts; i++) {
			Element font = fontlist.get(i);

			PDFFont loggerFont = new PDFFont();

			loggerFont.setName(font.getAttributeValue(NAME));
			loggerFont.setFamily(font.getAttributeValue(FAMILY));
			loggerFont.setType(font.getAttributeValue(TYPE));
			loggerFont.setEncoding(font.getAttributeValue(ENCODING));
			loggerFont.setFontencoding(font.getAttributeValue(FONTENCODING));
			loggerFont.setBasefont(font.getAttributeValue(BASEFONT));
			loggerFont.setBold(Boolean.parseBoolean(font
					.getAttributeValue(BOLD)));
			loggerFont.setItalic(Boolean.parseBoolean(font
					.getAttributeValue(ITALIC)));
			loggerFont.setSymbol(Boolean.parseBoolean(font
					.getAttributeValue(SYMBOL)));

			this.fonts.add(loggerFont);
		}

		Elements pdfs = root.getChildElements(PDF);

		int npdfs = pdfs.size();
		for (int i = 0; i < npdfs; i++) {
			Element pdf = pdfs.get(i);

			PDFFile loggerPDFFile = new PDFFile();

			loggerPDFFile.setFilename(pdf.getAttributeValue(FILENAME));
			loggerPDFFile.setPagecount(Integer.parseInt(pdf
					.getAttributeValue(PAGE_COUNT)));

			Elements pages = pdf.getChildElements(PAGE);

			int npages = pages.size();
			for (int j = 0; j < npages; j++) {
				Element page = pages.get(j);

				PDFPage loggerPDFPage = new PDFPage();

				loggerPDFPage.setPagenum(Integer.parseInt(page
						.getAttributeValue(NUM)));

				Elements chars = page.getChildElements(CHARACTER);

				int nchars = chars.size();
				for (int k = 0; k < nchars; k++) {
					Element character = chars.get(k);

					PDFChar loggerChar = new PDFChar();

					loggerChar.setFont(character.getAttributeValue(FONT));
					loggerChar.setFamily(character.getAttributeValue(FAMILY));
					loggerChar.setName(character.getAttributeValue(NAME));
					loggerChar.setCode(Integer.parseInt(character
							.getAttributeValue(CODE)));

					if (logglyphs) {
						Elements paths = character.getChildElements(PATH,
								"http://www.w3.org/2000/svg");

						PDFCharPath loggerCharpath = new PDFCharPath();

						if (paths.size() > 0) {
							assert (paths.size() == 1);

							Element path = paths.get(0);

							loggerCharpath.setStroke(path
									.getAttributeValue(STROKE));
							loggerCharpath.setStrokewidth(path
									.getAttributeValue(STROKE_WIDTH));
							loggerCharpath.setFill(path
									.getAttributeValue(FILL));
							String d = path.getAttributeValue(D).trim();
							d = d.replaceAll("\\s+", " ");
							d = d.replaceAll("(\\p{Lu}+) (?!\\p{Lu})", "$1");
							loggerCharpath.setD(d);
						} else {
							loggerCharpath.setStroke(NONE);
							loggerCharpath.setStrokewidth(NONE);
							loggerCharpath.setFill(NONE);
							loggerCharpath.setD(NONE);
						}

						loggerChar.setPath(loggerCharpath);
					}

					loggerPDFPage.getCharlist().add(loggerChar);
				}

				loggerPDFFile.getPagelist().add(loggerPDFPage);
			}

			this.pdfs.add(loggerPDFFile);
		}
	}

	private void deleteNodes(String deletePath) {
		if (deletePath != null) {
			Nodes nodes = root.query(deletePath);
			LOG.debug("Nodes "+nodes.size());
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
	}

	@Override
	public String toString() {
		return String.format("xml log (glyphs? %s)", logglyphs ? "yes" : "no");
	}

}
