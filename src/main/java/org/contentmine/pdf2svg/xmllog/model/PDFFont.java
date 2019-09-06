package org.contentmine.pdf2svg.xmllog.model;

public class PDFFont {

	private String name = null;
	private String family = null;
	private String type = null;
	private String encoding = null;
	private String fontencoding = null;
	private String basefont = null;
	private boolean bold = false;
	private boolean italic = false;
	private boolean symbol = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFontencoding() {
		return fontencoding;
	}

	public void setFontencoding(String fontencoding) {
		this.fontencoding = fontencoding;
	}

	public String getBasefont() {
		return basefont;
	}

	public void setBasefont(String basefont) {
		this.basefont = basefont;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public boolean isSymbol() {
		return symbol;
	}

	public void setSymbol(boolean symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return name;
	}

}
