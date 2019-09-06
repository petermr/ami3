package org.contentmine.pdf2svg.cmap;

public class CMapPointRange {

	private Integer low;
	private Integer high;
	private String code;
	private Integer unicode;

	public CMapPointRange(String s1, String s2) {
		this.low = (Integer) CharMapEntry.toCmapPoint(s1.trim());
		this.high = (Integer) CharMapEntry.toCmapPoint(s2.trim());
	}

	public String toString() {
		return String.valueOf(low)+"-"+high;
	}
	
}
