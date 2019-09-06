package org.contentmine.pdf2svg.xmllog.model;

public class PDFPage {

	private int pagenum = -1;
	private PDFCharList charlist = new PDFCharList();

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public PDFCharList getCharlist() {
		return charlist;
	}

	public void setCharlist(PDFCharList charlist) {
		this.charlist = charlist;
	}

	@Override
	public String toString() {
		return Integer.toString(pagenum);
	}

}
