package org.contentmine.graphics.html.util;

public class HTMLTagReplacement {

	private String oldTag;
	private String newTag;
	private String oldStartTag;
	private String oldEndTag;
	private StringBuilder sb;
	private int idx;
	private int emptyIdx;
	private int endTagIdx;

	/**
	 * 
	 * @param oldTag
	 * @param newTag if null delete tag and all content
	 */
	public HTMLTagReplacement(String oldTag, String newTag) {
		this.oldTag = oldTag;
		this.newTag = newTag;
		this.oldStartTag = "<"+oldTag;
		this.oldEndTag = "</"+oldTag+">";
	}
	
	/**
	 * 
	 * @param oldTag
	 */
	public HTMLTagReplacement(String oldTag) {
		this(oldTag, null);
	}
	
	public void replaceAll(StringBuilder sb) {
		this.sb = sb;
		int ptr = 0;
		while (true) {
			idx = sb.indexOf(oldStartTag, ptr);
			if (idx == -1) break;
			emptyIdx = sb.indexOf("/>", idx);
			endTagIdx = sb.indexOf(oldEndTag, idx);
			if (emptyIdx == -1 && endTagIdx == -1) {
				throw new RuntimeException("badly formed tag: "+oldStartTag+" at "+idx);
			}
			if (newTag != null) {
				replace();
			} else {
				delete();
			}
		}
	}

	private void replace() {
		// start tag
		sb.replace(idx, idx+oldStartTag.length(), "<"+newTag);
		// because buffer changed
		endTagIdx = sb.indexOf(oldEndTag, idx);
		if (endTagIdx != -1 && (emptyIdx == -1 || endTagIdx < emptyIdx)) {
			sb.replace(endTagIdx, endTagIdx+oldEndTag.length(), "</"+newTag+">");
		}
	}
	
	private void delete() {
		endTagIdx = sb.indexOf(oldEndTag, idx);
		if (endTagIdx != -1 && (emptyIdx == -1 || endTagIdx < emptyIdx)) {
			sb.delete(idx, endTagIdx+oldEndTag.length());
		} else {
			sb.delete(idx, emptyIdx + 2);
		}
	}
}
