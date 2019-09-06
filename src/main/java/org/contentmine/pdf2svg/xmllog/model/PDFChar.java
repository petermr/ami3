package org.contentmine.pdf2svg.xmllog.model;

public class PDFChar {

	private String font = null;
	private String family = null;
	private String name = null;
	private int code = -1;
	private PDFCharPath path = null;

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public PDFCharPath getPath() {
		return path;
	}

	public void setPath(PDFCharPath path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return String.format("%s (%d)", name, code);
	}

}
