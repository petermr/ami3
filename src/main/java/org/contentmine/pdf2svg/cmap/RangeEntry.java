package org.contentmine.pdf2svg.cmap;

public class RangeEntry {

	private Integer low;
	private Integer high;
	private String code;
	private Integer unicode;

	public RangeEntry(String s1, String s2) {
		extractLowHigh(s1, s2);
	}


	public RangeEntry(String s1, String s2, String s3) {
		extractLowHigh(s1, s2);
		extractUnicode(s3);
	}


	private void extractUnicode(String s3) {
		s3 = s3.trim();
		Object obj = CharMapEntry.toCmapPoint(s3);
		if (obj instanceof String) {
			code = (String) obj;
		} else if (obj instanceof Integer) {
			unicode = (Integer) obj;
		} else {
			throw new RuntimeException("Cannot parse: "+s3);
		}
	}


	private void extractLowHigh(String s1, String s2) {
		this.low = (Integer) CharMapEntry.toCmapPoint(s1.trim());
		this.high = (Integer) CharMapEntry.toCmapPoint(s2.trim());
	}

	public int getHigh() {
		return high;
	}
	
	public int getLow() {
		return low;
	}
	
	public String getCode() {
		return code;
	}

	public Integer getUnicode() {
		return unicode;
	}

	public String toString() {
		return String.valueOf(low)+"-"+high+" > "+((unicode != null) ? (String.valueOf(unicode)+"("+((char)(int)unicode)+")") : ("code: "+code));
	}

}
