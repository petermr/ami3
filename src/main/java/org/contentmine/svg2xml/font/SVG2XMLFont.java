package org.contentmine.svg2xml.font;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGText;

public class SVG2XMLFont {
	
	private final static Logger LOG = Logger.getLogger(SVG2XMLFont.class);
	private static final PrintStream SYSOUT = System.out;
	public final static String TAG = "svgFont";

	private String fontName;
	private List<Long> unicodeList;
	private Map<Long, SVG2XMLCharacter> characterMap;
	
	
	public SVG2XMLFont() {
//		super(TAG);
	}

	public SVG2XMLFont(String fontName) {
		this();
		this.fontName = fontName;
		this.characterMap = new HashMap<Long, SVG2XMLCharacter>();
	}

	public void add(SVG2XMLCharacter character) {
		this.characterMap.put(character.getUnicodePoint(), character);
	}

//	private Element createElement() {
//		ensureSortedUnicodeList();
//		return null;
//	}

	public List<Long> ensureSortedUnicodeList() {
		if (unicodeList == null) {
			unicodeList = new ArrayList<Long>();
			for (Long unicode : characterMap.keySet()) {
				unicodeList.add(unicode);
			}
			LOG.trace(unicodeList.size());
			Long[] unicodes = unicodeList.toArray(new Long[0]);
			Arrays.sort(unicodes);
			unicodeList = Arrays.asList(unicodes);
			LOG.trace("U "+unicodeList.size());
		}
		return unicodeList;
	}

	public void debug(String msg) {
		ensureSortedUnicodeList();
		for (int i = 0; i < unicodeList.size(); i++) {
			Long unicodeLong = unicodeList.get(i);
			SVG2XMLCharacter character = characterMap.get(unicodeLong);
			if (character != null) {
				character.debug("char");
			}
		}
	}

	public SVG2XMLCharacter getSVG2XMLCharacter(AbstractCMElement svgText) {
		String value = (svgText == null) ? null : svgText.getValue();
		Long unicodePoint = value == null || value.length() != 1 ? null : (long) value.charAt(0); 
		SVG2XMLCharacter character = characterMap.get(unicodePoint);
		return character;
	}

	public SVG2XMLCharacter getOrCreateSVG2XMLCharacter(SVGText svgText) {
		SVG2XMLCharacter character = getSVG2XMLCharacter(svgText);
		if (character == null) {
			character = new SVG2XMLCharacter(svgText, this);
			add(character);
		}
		return character;
	}

	public void addTextListAndGenerateSizes(List<SVGText> textList) {
		for (int i = 0; i < textList.size(); i++) {
			SVG2XMLCharacter character = getOrCreateSVG2XMLCharacter(textList.get(i));
			if (i < textList.size() - 1) {
				SVG2XMLCharacter character1 = new SVG2XMLCharacter(textList.get(i+1), this);
				character.addWidthCalculatedFrom(character1);
			}
		}
		ensureSortedUnicodeList();
	}
}
