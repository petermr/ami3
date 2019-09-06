package org.contentmine.pdf2svg.xmllog.model;

public class PDFFile {

	private String filename = null;
	private int pagecount = -1;
	private PDFPageList pagelist = new PDFPageList();

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getPagecount() {
		return pagecount;
	}

	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}

	public PDFPageList getPagelist() {
		return pagelist;
	}

	public void setPagelist(PDFPageList pagelist) {
		this.pagelist = pagelist;
	}

	@Override
	public String toString() {
		return String.format("%s (%d pages)", filename, pagecount);
	}

}
