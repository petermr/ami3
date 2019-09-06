package org.contentmine.image.colour;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

/** enforces all RGB values to be 24-bit
 * i.e. removes alpha channel
 * also allows easier indexing in maps and diffing and String values
 * 
 * @author pm286
 *
 */
public class RGBColor {
	private static final Logger LOG = Logger.getLogger(RGBColor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static int HEX_BLACK = 0x000000;
	public static RGBColor RGB_BLACK = new RGBColor(HEX_BLACK);
	public static int HEX_WHITE = 0xffffff;
	public static RGBColor RGB_WHITE = new RGBColor(HEX_WHITE);
	
	private int rgb; // int representing rgb
	private Integer grayValue; // only 24-bit so no sign problem
	
	private RGBColor() {
		
	}
	public RGBColor(int r, int g, int b) {
		this.rgb = (r << 16) + (g << 8) + b;
	}

	public RGBColor(int rgb) {
		this.rgb = ColorUtilities.removeAlpha(rgb);
	}
	
	public RGBColor(Color color) {
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rgb;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RGBColor other = (RGBColor) obj;
		if (rgb != other.rgb)
			return false;
		return true;
	}
	
	public static Map<RGBColor, Integer> createRGBColorFrequencyMap(Multiset<RGBColor> set) {
		Map<RGBColor, Integer> countByRGBColor = new HashMap<RGBColor, Integer>();
		for (Entry<RGBColor> entry : set.entrySet()) {
			countByRGBColor.put(entry.getElement(), entry.getCount());
		}
		return countByRGBColor;
	}
	
	public static List<Entry<RGBColor>> createRGBListSortedByCount(Multiset<RGBColor> lengthSet) {
		return RGBColor.createRGBEntryList(RGBColor.getRGBEntriesSortedByCount(lengthSet));
	}
	
	public static Iterable<Multiset.Entry<RGBColor>> getRGBEntriesSortedByCount(Multiset<RGBColor> colorSet) {
		return Multisets.copyHighestCountFirst(colorSet).entrySet();
	}
	
	public static List<Entry<RGBColor>> createRGBEntryList(Iterable<Entry<RGBColor>> iterable) {
		List<Entry<RGBColor>> entries = new ArrayList<Entry<RGBColor>>();
		for (Entry<RGBColor> entry : iterable) {
			entries.add(entry);
		}
		return entries;
	}

	public int getRGBInteger() {
		return rgb;
	}

	public String toString() {
		return ColorUtilities.createPaddedHex(rgb);
	}

	public boolean isGray() {
		int r = ColorUtilities.getRed(rgb);
		int g = ColorUtilities.getGreen(rgb);
		int b = ColorUtilities.getBlue(rgb);
		return r == g && g == b;
	}

	/** gets absolute diff between two colours.
	 * returns setRGB(
	 abs(ii.getRed()-jj.getRed()),
	 abs(ii.getGreen()-jj.getGreen()),
	 abs(ii.getBlue()-jj.getBlue())
	 );
	The result is NOT a colour but has RGB structure

	 * @param ii
	 * @param jj
	 * @return abs(ii.getRed()
	 */
	public Integer absDiff(RGBColor jj, int maxStep) {
		int rdiff = round(Math.abs(this.getRed() - jj.getRed()));
		int gdiff = round(Math.abs(this.getGreen() - jj.getGreen()));
		int bdiff = round(Math.abs(this.getBlue() - jj.getBlue()));
		return ColorUtilities.createRGB(Math.abs(rdiff), Math.abs(gdiff), Math.abs(bdiff));
	}

	private static int round(int val) {
//		if (val == 0xff) val = 0xff; 
		if (val == 0xc0) val = 0xbf;
		if (val == 0x80) val = 0x7f;
		if (val == 0x40) val = 0x3f;
		return val;
	}


	public int getRed() {
		return ColorUtilities.getRed(rgb);
	}

	public int getBlue() {
		return ColorUtilities.getBlue(rgb);
	}

	public int getGreen() {
		return ColorUtilities.getGreen(rgb);
	}

	public String getHex() {
		return "#"+ColorUtilities.createPaddedHex(rgb);
	}

	public RGBColor calculateAverageGray() {
		int gray = (int) ((getRed() + getGreen() + getBlue()) / 3.0);
		RGBColor grayColor = new RGBColor(gray, gray, gray);
		return grayColor;
	}

	/**
	 * gets best perceived gray.
	 * 
	 * 	0.299*r + 0.587*g + 0.114*b.

	 * @return
	 */
	public RGBColor calculateWeightedGray() {
		RGBColor grayColor = new RGBColor(
			(int)(0.299 * getRed()),
			(int)(0.587 * getGreen()),
			(int)(0.114 * getBlue())
			);
		return grayColor;
	}
	public int getOrCreateAverageGrayValue() {
		if (grayValue == null) {
			RGBColor gray = calculateAverageGray();
			grayValue = gray.getRGBInteger();
		}
		return grayValue;
	}


}
