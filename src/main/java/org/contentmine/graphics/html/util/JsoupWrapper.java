package org.contentmine.graphics.html.util;

import org.jsoup.Jsoup;

public class JsoupWrapper {

	public JsoupWrapper() {
		
	}
	
	/** parses with Jsoup and tries to correct any bad XML
	 * 
	 * see tests for examples
	 * 
	 * @param s
	 * @return well-formed XML, maybe with "JUNK" inserted locally in place of bad html
	 */
	public static String parseAndCorrect(String s) {
		org.jsoup.nodes.Document doc= Jsoup.parse(s);
		String ss = doc.toString().replaceAll("\\\"[^\\\"]*\\\"=\\\"", "JUNK");
		return ss;
	}
}
