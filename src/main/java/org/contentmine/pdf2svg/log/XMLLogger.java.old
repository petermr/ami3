package org.contentmine.pdf2svg.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.pdfbox.encoding.Encoding;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.pdf2svg.AMIFont;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

public class XMLLogger {



	private final static Logger LOG = Logger.getLogger(XMLLogger.class);

	private static final String BASEFONT = "basefont";
	private static final String CHARACTER = "character";
	private static final String CODE = "code";
	private static final String ENCODING = "encoding";
	private static final String FAMILY = "family";
	private static final String FILENAME = "filename";
	private static final String FONT = "font";
	private static final String FONT_LIST = "fontList";
	private static final String FONTENCODING = "fontencoding";
	private static final String GLYPHS = "glyphs";
	private static final String NAME = "name";
	private static final String NULL = "null";
	private static final String NUM = "num";
	private static final String PAGE = "page";
	private static final String PAGE_COUNT = "pageCount";
	private static final String PDF = "pdf";
	private static final String PDF_LOG = "pdfLog";
	private static final String TYPE = "type";

	private static final String BOLD = "bold";
	private static final String ITALIC = "italic";
	private static final String SYMBOL = "symbol";

	private static final String UTF_8 = "UTF-8";

	private Element root;
	private Element fontlist;
	private Element file;
	private Element page;

	private List<String> fontnames; // names of all fonts in the fontlist
	private Map<String, AMIFont> fontmap; // only valid for the current PDF
	private final boolean logGlyphs;

	public XMLLogger() {
		logGlyphs = false;
		reset();
	}

	public XMLLogger(boolean logGlyphs) {
		this.logGlyphs = logGlyphs;
		reset();
	}

	public void reset() {
		root = new Element(PDF_LOG);
		root.addAttribute(new Attribute(GLYPHS, Boolean.toString(logGlyphs)));

		fontlist = new Element(FONT_LIST);
		root.appendChild(fontlist);

		file = null;
		page = null;

		fontnames = new ArrayList<String>();
		if (logGlyphs)
			fontmap = null;
	}

	public void newPDFFile(String fileName, int pageCount) {
		file = new Element(PDF);
		file.addAttribute(new Attribute(FILENAME, fileName));
		file.addAttribute(new Attribute(PAGE_COUNT, Integer
				.toString(pageCount)));
		root.appendChild(file);

		if (logGlyphs)
			fontmap = new HashMap<String, AMIFont>();
	}

	public void newPDFPage(int pageNumber) {
		if (file == null)
			throw new RuntimeException("no current PDF file!");
		page = new Element(PAGE);
		page.addAttribute(new Attribute(NUM, Integer.toString(pageNumber)));
		file.appendChild(page);
	}

	public void newFont(AMIFont amiFont) {
		String fontName = amiFont.getFontName();
		if (fontName == null)
			return;
		if (logGlyphs)
			fontmap.put(fontName, amiFont);

		if (fontnames.contains(fontName))
			return;
		fontnames.add(fontName);

		Element font = new Element(FONT);

		font.addAttribute(new Attribute(NAME, fontName));
		String fontFamilyName = amiFont.getFontFamilyName();
		font.addAttribute(new Attribute(FAMILY,
				fontFamilyName == null ? NULL : fontFamilyName));

		String fontType = amiFont.getFontType();
		font.addAttribute(new Attribute(TYPE, fontType == null ? NULL
				: fontType));
		Encoding encoding = amiFont.getEncoding();
		font.addAttribute(new Attribute(ENCODING, encoding == null ? NULL
				: encoding.getClass().getSimpleName()));
		String fontEncoding = amiFont.getFontEncoding();

		font.addAttribute(new Attribute(FONTENCODING,
				fontEncoding == null ? NULL : fontEncoding));
		String baseFont = amiFont.getBaseFont();
		font.addAttribute(new Attribute(BASEFONT, baseFont == null ? NULL
				: baseFont));

		addAttribute(font, BOLD, amiFont.isForceBold());		
		addAttribute(font, ITALIC, amiFont.isItalic());
		addAttribute(font, SYMBOL, amiFont.isSymbolic());

		fontlist.appendChild(font);
	}

	private void addAttribute(Element font, String attName, Boolean value) {
		if (value != null) {
			font.addAttribute(new Attribute(attName, Boolean.toString(value)));
		}
	}

	public void newCharacter(String fontName, String fontFamilyName, String charName, int charCode) {
		if (file == null || page == null)
			throw new RuntimeException("no current PDF file or page!");

		if (fontName == null) {
			LOG.error("fontName is null! (charName=" + charName + ",charValue="
					+ charCode + ")");
			return;
		}

		if (!fontnames.contains(fontName)) {
			LOG.error("new character (" + charName + "," + charCode
					+ ") specifies font name '" + fontName
					+ "' - which doesn't exist!");
		}

		Element character = new Element(CHARACTER);

		character.addAttribute(new Attribute(FONT, fontName));
		character.addAttribute(new Attribute(FAMILY,
				fontFamilyName == null ? NULL : fontFamilyName));
		character.addAttribute(new Attribute(NAME, charName == null ? NULL
				: charName));
		character
				.addAttribute(new Attribute(CODE, Integer.toString(charCode)));

		if (logGlyphs) {
			AMIFont amiFont = fontmap.get(fontName);
			if (amiFont == null) {
				LOG.error(String.format("no AMIFont available for (%s,%s,%d)",
						fontName, charName, charCode));
			} else {
				String key = charName;
				if (key == null)
					key = "" + charCode;
				String d = amiFont.getPathStringByCharnameMap().get(key);
				if (d != null) {
					SVGPath path = new SVGPath(d);
					path.setStrokeWidth(0.005);
					character.appendChild(path);
				}
			}
		}

		page.appendChild(character);
	}

	public void newException(Exception e) {
		if (file == null || page == null) {
			throw new RuntimeException("no current PDF file or page!");
		}

		Element exceptionElement = new Element("exception");
		StackTraceElement[] steArray = e.getStackTrace();
		for (StackTraceElement ste : steArray) {
			Element ste0 = new Element("stackTrace");
			exceptionElement.appendChild(ste0);
			ste0.appendChild(ste.toString());
		}
		page.appendChild(exceptionElement);
	}

	public void writeXMLFile(OutputStream outputStream) {
		Document doc = new Document(root);
		try {
			Serializer serializer = new Serializer(outputStream, UTF_8);
			serializer.setIndent(4);
			serializer.setMaxLength(50);
			serializer.write(doc);
			serializer.flush();
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public void writeXMLFile(String outdir, String pdfname) {
		String logname = pdfname.replaceFirst("(?i)\\.pdf$", "") + "-log.xml";

		File outputFile = new File(outdir, logname);
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"caught File Not Found exception while creating logfile '"
							+ outputFile.getAbsolutePath() + "'.");
		}

		writeXMLFile(outputStream);
	}
}
