package org.contentmine.pdf2svg.xmllog.model;

public class PDFCharPath {

	private String stroke;
	private String strokewidth;
	private String fill;
	private String d;

	public String getStroke() {
		return stroke;
	}

	public void setStroke(String stroke) {
		this.stroke = stroke;
	}

	public String getStrokewidth() {
		return strokewidth;
	}

	public void setStrokewidth(String strokewidth) {
		this.strokewidth = strokewidth;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	@Override
	public String toString() {
		return "[glyph]";
	}

}
