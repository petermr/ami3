/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contentmine.pdf2svg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
// 1.8
//import org.apache.pdfbox.encoding.DictionaryEncoding;
//import org.apache.pdfbox.encoding.Encoding;
//import org.apache.pdfbox.pdmodel.common.PDMatrix;
// 2.0
//import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
//import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
//import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.util.Matrix;
import org.contentmine.font.NonStandardFontFamily;
import org.contentmine.font.NonStandardFontManager;

/** wrapper for PDType1Font. is meant to manage the bad Fontnames, other
 * fontTypes, etc and try to convert them to a standard approach. 
 * Splits all Italic, etc. to attributes of AMIFont. 
 * May ultimately be unneccessary
 * 
 * @author pm286
 *
 */
public class AMIFont {

	private final static Logger LOG = Logger.getLogger(AMIFont.class);
	
	private static final String SYMBOL = "Symbol";
	public static final String N_NAME = "Name";
	public static final String N_BASE_FONT = "BaseFont";

	
	// order may matter - longest/unique strings first
//	private static final String[] BOLD_SUFFIXES = new String[]{
//		"-SemiBold",
//		"SemiBold",
//		"-Bold", 
//		".Bold", 
//		".B", 
//		"-B", 
//		"Bold",
//		};
//	private static final String[] ITALIC_SUFFIXES = new String[]{
//		"-Italic", 
//		".Italic", 
//		".I", 
//		"-I", 
//		"Italic",
//		"-Oblique", 
//		".Oblique", 
//		"Oblique",
//		"-Inclined", 
//		".Inclined", 
//		};
	
//	public static final String MONOTYPE_SUFFIX = "MT";  // rubbish - means MathType!
//	public static final String POSTSCRIPT_SUFFIX = "PS";
	
	public static final String ENCODING = "Encoding";
	static Pattern LEADER_PATTERN = Pattern.compile("^([A-Z]{6})\\+(.*)$");
	
	private String fontFamilyName;
	private String fontName;
	
	private PDFont pdFont;
	private PDFontDescriptor fontDescriptor;
	private String fontType;
	
	private String finalSuffix;
	private Encoding encoding;
	private String fontEncoding;
	private String baseFont;
	private Map<String, String> pathStringByCharnameMap;
	
	private COSDictionary dictionary;
	private COSArray dictionaryArray;
	private COSName dictionaryName;
	private COSDictionary dictionaryDictionary;
	private COSInteger dictionaryInteger;

	private PDFont firstDescendantFont;
	private NonStandardFontFamily nonStandardFontFamily;
	private Boolean forceBold;
	private String fontFamilyString;
	
	/**
        addFontMapping("Times-Roman","TimesNewRoman");
        addFontMapping("Times-Italic","TimesNewRoman,Italic");
        addFontMapping("Times-Italic","TimesNewRoman,Italic");
        addFontMapping("Times-ItalicItalic","TimesNewRoman,Italic,Italic");
        addFontMapping("Helvetica-Oblique","Helvetica,Italic");
        addFontMapping("Helvetica-ItalicOblique","Helvetica,Italic,Italic");
        addFontMapping("Courier-Oblique","Courier,Italic");
        addFontMapping("Courier-ItalicOblique","Courier,Italic,Italic");
        
and
    // TODO move the Map to PDType1Font as these are the 14 Standard fonts
    // which are definitely Type 1 fonts
    private static Map<String, FontMetric> getAdobeFontMetrics()
    {
        Map<String, FontMetric> metrics = new HashMap<String, FontMetric>();
        addAdobeFontMetric( metrics, "Courier-Italic" );
        addAdobeFontMetric( metrics, "Courier-ItalicOblique" );
        addAdobeFontMetric( metrics, "Courier" );
        addAdobeFontMetric( metrics, "Courier-Oblique" );
        addAdobeFontMetric( metrics, "Helvetica" );
        addAdobeFontMetric( metrics, "Helvetica-Italic" );
        addAdobeFontMetric( metrics, "Helvetica-ItalicOblique" );
        addAdobeFontMetric( metrics, "Helvetica-Oblique" );
        addAdobeFontMetric( metrics, "Symbol" );
        addAdobeFontMetric( metrics, "Times-Italic" );
        addAdobeFontMetric( metrics, "Times-ItalicItalic" );
        addAdobeFontMetric( metrics, "Times-Italic" );
        addAdobeFontMetric( metrics, "Times-Roman" );
        addAdobeFontMetric( metrics, "ZapfDingbats" );
        return metrics;
    }
        
	 */
	
	/** try to create font from name
	 * usually accessed through createFontFromName()
	 * @param fontName
	 */
	private AMIFont(String fontName) {
		this();
		fontFamilyName = null;
		this.fontName = fontName;
		stripFontNameComponents();
	}

	/** create font from family and key attributes
	 * currently used when compiling an external table
	 */
	public AMIFont(String fontFamilyName, String encoding, String type) {
		this();
		this.fontFamilyName = fontFamilyName;
		this.fontEncoding = encoding;
		this.fontType = type;
	}

	/** create font from family and key attributes
	 * currently used when compiling an external table
	 */
	public AMIFont(String fontFamilyName, String encoding, String type, COSDictionary dictionary) {
		this(fontFamilyName, encoding, type);
		this.dictionary = dictionary;
		analyzeDictionary();
	}

	private void analyzeDictionary() {
		Set<COSName> keySet = dictionary.keySet();
		for (COSName key : keySet) {
			COSBase object = dictionary.getDictionaryObject(key);
			if (object instanceof COSArray) {
				dictionaryArray = (COSArray) object;
				for (int i = 0; i < dictionaryArray.size(); i++) {
					LOG.trace(dictionaryArray.getName(i)+": "+dictionaryArray.getObject(i));
				}
			} else if (object instanceof COSName) {
				this.dictionaryName = (COSName) object;
			} else if (object instanceof COSDictionary) {
				this.dictionaryDictionary = (COSDictionary) object;
			} else if (object instanceof COSInteger) {
				this.dictionaryInteger = (COSInteger) object;
			} else {
				LOG.debug(object.getClass());
			}
		}
	}

	public AMIFont(PDFont pdFont) {
		fontDescriptor = getFontDescriptorOrDescendantFontDescriptor(pdFont);
		this.firstDescendantFont = getFirstDescendantFont(pdFont);
		// this doesn't work anymore
//		this.baseFont = fontDescriptor.getBaseFont();
//		this.baseFont = (pdFont instanceof PDSimpleFont) ? ((PDSimpleFont)pdFont).getBaseFont() : null;
		this.fontType = pdFont.getType();
		this.encoding = (pdFont instanceof PDSimpleFont) ? ((PDSimpleFont)pdFont).getEncoding() : null;
		if (encoding == null && pdFont instanceof PDType0Font) {
			pdFont = firstDescendantFont;
			this.encoding = (pdFont instanceof PDSimpleFont) ? ((PDSimpleFont)pdFont).getEncoding() : null;
//			encoding = pdFont.getFontEncoding();
		}
		fontEncoding = (encoding == null) ? null : encoding.getClass().getSimpleName();
		this.pdFont = pdFont;
		fontFamilyName = null;
		if (fontDescriptor != null) {
			fontName = fontDescriptor.getFontName();
			
			stripFontNameComponents();
			if (fontFamilyName == null) {
				fontFamilyName = createFontFamilyFromFontName(fontName);
			}
			LOG.trace("FFFFF "+fontFamilyName);
			
			fontName = fontDescriptor.getFontName();
			LOG.trace("name="+fontName+" fam="+
			fontFamilyName+" type="+pdFont.getSubType()+" bold="+forceBold +
			" it="+isItalic()+" face="+finalSuffix+" sym="+isSymbolic()+
			" enc="+(encoding == null ? "null" : encoding.getClass().getSimpleName()));
		} else {
			fontName = baseFont;
			stripFontNameComponents();
			if (fontFamilyName == null) {
				fontFamilyName = fontName;
			}
			LOG.trace(this.toString());
			LOG.warn("font had no descriptor: "+baseFont+" / "+fontFamilyName);
		}
	}

	public static String createFontFamilyFromFontName(String fontName) {
		String fontFamily = null;
		if (fontName == null) {
		} else if (fontName.length() >= 7 && fontName.charAt(6) == '+') {
			fontFamily = fontName.substring(7);
		} else {
			fontFamily = fontName;
		}
		return fontFamily;
	}

	/** do not call without fontName or PDType1Font
	 * 
	 */
	private AMIFont() {
		encoding = null;
	}

	public static AMIFont createAMIFontFromName(String fontName) {
		AMIFont amiFont = new AMIFont(fontName);
		return amiFont;
	}
	
	private void stripFontNameComponents() {
		processInitialPrefix();
//		processStandardFamilies();
//		processIsBoldInName();
//		processIsItalicInName();
//		processFinalSuffix();
	}

	private void processInitialPrefix() {
		String initialPrefix = null;
		if (fontName != null){
			Matcher matcher = LEADER_PATTERN.matcher(fontName);
			if (matcher.matches()) {
				initialPrefix = matcher.group(1);
			}
		}
	}

//	private void processFinalSuffix() {
//		finalSuffix = null;
//		if (fontName != null) {
//			if (fontName.endsWith(MONOTYPE_SUFFIX)) {
//				finalSuffix = MONOTYPE_SUFFIX;
//			} else if (fontName.endsWith(POSTSCRIPT_SUFFIX)) {
//				finalSuffix = POSTSCRIPT_SUFFIX;
//			}
//		}
//	}
//
//	private Boolean isIncluded(String suffix) {
//		boolean isIncluded = false;
//		if (fontName != null) {
//			String fontNameLower = fontName.toLowerCase();
//			int currentIndex = fontNameLower.indexOf(suffix.toLowerCase());
//			if (currentIndex != -1) {
//				isIncluded = true;
//			}
//		}
//		return isIncluded;
//	}
//
//	private void removeFromFontName(String subName, int idx) {
//		if (fontName.substring(idx, idx+subName.length()).equalsIgnoreCase(subName)) {
//			fontName = fontName.substring(0, idx)+fontName.substring(idx+subName.length());
//		}
//	}

	public Encoding getEncoding() {
		return encoding;
	}

	public String getFontEncoding() {
		return fontEncoding;
	}

	public DictionaryEncoding getDictionaryEncoding() {
		return (encoding instanceof DictionaryEncoding) ? (DictionaryEncoding) encoding : null;
	}

	public String getFontName() {
		return fontName;
	}
	
	public String getFontFamilyName() {
		return fontFamilyName;
	}
	
	public String getFontType() {
		return fontType;
	}
	
//	public boolean isSymbol() {
//		return (isSymbol == null) ? false : isSymbol;
//	}
	
	public String getBaseFont() {
		return baseFont;
	}

//	public Boolean isItalic() {
//		return isItalic;
//	}
	
//	public Boolean isBold() {
//		return ;isBold
//	}

	public Map<String, String> getPathStringByCharnameMap() {
		ensurePathStringByCharnameMap();
		return pathStringByCharnameMap;
	}

	private void ensurePathStringByCharnameMap() {
		if (this.pathStringByCharnameMap == null) {
			pathStringByCharnameMap = new HashMap<String, String>();
		}
	}
	
	public static String getFontName(COSDictionary dict) {
		String fontName = null;
		String baseFontS = null;
		for (COSName key : dict.keySet()) {
			String keyName = key.getName();
			if (keyName == null) {
				LOG.error("Null key");
				continue;
			} else if (!(key instanceof COSName)) {
				LOG.error("key not COSName");
				continue;
			}
			String cosNameName = null;
			COSBase cosBase = dict.getDictionaryObject(key);
			if (cosBase instanceof COSName) {
				COSName cosName = (COSName) cosBase;
				cosNameName = cosName.getName();
				LOG.trace("Name:"+cosNameName);
			} else if (cosBase instanceof COSInteger) {
				COSInteger cosInteger = (COSInteger) cosBase;
				LOG.trace("Integer: "+cosInteger.intValue());
			} else if (cosBase instanceof COSArray) {
				COSArray cosArray = (COSArray) cosBase;
				LOG.trace("Array: "+cosArray.size()+" / "+cosArray);
			} else if (cosBase instanceof COSDictionary) {
				COSDictionary cosDictionary = (COSDictionary) cosBase;
				LOG.trace("Dictionary: "+cosDictionary);
			} else{
				LOG.error("COS "+cosBase);
			}
			if (cosNameName != null && keyName.equals(N_NAME)) {
				fontName = cosNameName;
			} else if(cosNameName != null && keyName.equals(N_BASE_FONT)) {
				baseFontS = cosNameName;
			}
		}
		if (fontName == null) {
			fontName = baseFontS;
		}
		return fontName;
	}

	public static PDFontDescriptor getDescendantFontDescriptor(PDFont pdFont) {
		PDFontDescriptor fd = null;
		PDFont descendantFont = getFirstDescendantFont(pdFont);
		fd = (descendantFont == null) ? null : descendantFont.getFontDescriptor();
		LOG.trace("fd ("+fd.getFontName()+") "+fd);
		return fd;
	}

	public static PDFont getFirstDescendantFont(PDFont pdFont) {
		COSDictionary dict = (COSDictionary) pdFont.getCOSObject();
		COSArray array = dict == null ? null : (COSArray) dict.getDictionaryObject(COSName.DESCENDANT_FONTS);
		PDFont descendantFont = null;
		try {
			descendantFont = array == null ? null : PDFontFactory.createFont((COSDictionary) array.getObject(0));
		} catch (IOException e) {
			LOG.error("****************** Can't create descendant font! for "+pdFont);
		}
		return descendantFont;
	}

	public static PDFontDescriptor getFontDescriptorOrDescendantFontDescriptor(PDFont pdFont) {
		PDFontDescriptor fd = pdFont.getFontDescriptor();
//		getToUnicode(pdFont);
		if (fd == null && pdFont instanceof PDType0Font) {
			fd = AMIFont.getDescendantFontDescriptor(pdFont);
		}
		return fd;
	}

	/**
	private COSDictionary getToUnicode() {
		if (true) throw new RuntimeException("PDF2");
//		COSDictionary cosDictionary = null;
		cosDictionary = (COSDictionary) ((PDSimpleFont) pdFont).getToUnicode();
		return cosDictionary;
	}
*/
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("isBold: ");
		sb.append(forceBold);
		sb.append("; isItalic: ");
		sb.append(isItalic());
		sb.append("; isSymbol: ");
		sb.append(isSymbolic());
		sb.append("; fontFamilyName: ");
		sb.append(fontFamilyName);
		sb.append("; fontName: ");
		sb.append(fontName);
		sb.append("; pdFont: ");
		sb.append(pdFont);
		sb.append("; fontDescriptor: ");
		sb.append(fontDescriptor);
		sb.append("; fontType: ");
		sb.append(fontType);
		sb.append("; encoding: ");
		sb.append(encoding);
		sb.append("; fontEncoding: ");
		sb.append(fontEncoding);
		sb.append("; baseFont: ");
		sb.append(baseFont);
		sb.append("\n");
		sb.append("; dictionary: ");
		sb.append(dictionary);
		sb.append("; dictionaryName: ");
		sb.append(dictionaryName);
		sb.append("; dictionaryArray: ");
		sb.append(dictionaryArray);
		sb.append("; dictionaryDictionary: ");
		sb.append(dictionaryDictionary);
		sb.append("; dictionaryInteger: ");
		sb.append(dictionaryInteger);
		sb.append("\n");
		sb.append("; isFixedPitch(): ");
		sb.append(isFixedPitch());
		sb.append("\n");
		sb.append("; isHeuristicBold(): ");
		sb.append(isHeuristicBold());
		sb.append("; isHeuristicFixedPitch(): ");
		sb.append(isHeuristicFixedPitch());
		
		return sb.toString();
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public void setNonStandardFontFamily(NonStandardFontFamily amiFontFamily) {
		this.nonStandardFontFamily = amiFontFamily;
	}
	
	public PDFont getPDFont() {
		return pdFont;
	}

	public PDFontDescriptor getFontDescriptor() {
		return fontDescriptor;
	}

	/** delegates from dictionary
	 */
	public COSDictionary getDictionaryDictionary() {
		return dictionaryDictionary;
	}

	public COSInteger getDictionaryInteger() {
		return dictionaryInteger;
	}

	public COSName getDictionaryName() {
		return dictionaryName;
	}
	
	/** delegates from PDFont
	 */
	/** OLD
	public Float getFontWidth(byte[] c, int offset, int length)
			throws IOException {
		return pdFont == null ? null : pdFont.getFontWidth(c, offset, length);
	}
	*/

	/** OLD
	public Float getFontHeight(byte[] c, int offset, int length)
			throws IOException {
		return pdFont == null ? null : pdFont.getFontHeight(c, offset, length);
	}
	*/

	public Float getStringWidth(String string) throws IOException {
		return pdFont == null ? null : pdFont.getStringWidth(string);
	}

	public Float getAverageFontWidth() throws IOException {
		return pdFont == null ? null : pdFont.getAverageFontWidth();
	}

//	public String encode(byte[] c, int offset, int length) throws IOException {
//		return pdFont == null ? null : pdFont.encode(c, offset, length);
//	}
//
//	public Integer encodeToCID(byte[] c, int offset, int length) throws IOException {
//		return pdFont == null ? null : pdFont.encodeToCID(c, offset, length);
//	}

	public String getSubType() {
		return pdFont == null ? null : pdFont.getSubType();
	}

	public List<Float> getWidths() {
		List<Float> widthArray = new ArrayList<Float>();
// PDFBox2 
//		return pdFont == null ? null : pdFont.getWidths();
		if (true) throw new RuntimeException("PDF2");
		return widthArray;
	}

	public Matrix getFontMatrix() {
		return pdFont == null ? null : pdFont.getFontMatrix();
	}

	public PDRectangle getFontBoundingBox() throws IOException {
		PDRectangle pdRect = null;
		pdRect = fontDescriptor == null ? null : fontDescriptor.getFontBoundingBox();
		return pdRect;
//		return pdRect != null ? pdRect : ((pdFont == null) ? null : pdFont.getBoundingBox());
	}

	public Float getFontWidth(int charCode) {
//		return pdFont == null ? null : pdFont.getFontWidth(charCode);
		// PDFBox2 
		if (true) throw new RuntimeException("PDF2");
		return null;
	}

	/** delegates from fontDescriptor */
	public String getFontStretch() {
		return fontDescriptor == null ? null : fontDescriptor.getFontStretch();
	}

	public Float getFontWeightFloat() {
		return fontDescriptor == null ? null : fontDescriptor.getFontWeight();
	}

	public String getFontFamilyString() {
		if (this.fontFamilyString == null) {
			fontFamilyString = (fontDescriptor == null) ? null : fontDescriptor.getFontFamily();
			if (fontFamilyString == null) {
				LOG.trace("No font family for: "+fontFamilyName);
			}
			fontFamilyString = fontFamilyName;
		}
		return fontFamilyString;
	}

	public Integer getFlags() {
		return fontDescriptor == null ? null : fontDescriptor.getFlags();
	}

	public Boolean isFixedPitch() {
		return fontDescriptor == null ? null : fontDescriptor.isFixedPitch();
	}

	public Boolean isSerif() {
		return fontDescriptor == null ? null : fontDescriptor.isSerif();
	}

	public Boolean isSymbolic() {
		return fontDescriptor == null ? null : fontDescriptor.isSymbolic();
	}

	public Boolean isScript() {
		return fontDescriptor == null ? null : fontDescriptor.isScript();
	}

	public Boolean isNonSymbolic() {
		return fontDescriptor == null ? null : fontDescriptor.isNonSymbolic();
	}

	public Boolean isItalic() {
		return fontDescriptor == null ? null : fontDescriptor.isItalic();
	}

	public Boolean isAllCap() {
		return fontDescriptor == null ? null : fontDescriptor.isAllCap();
	}

	public Boolean isSmallCap() {
		return fontDescriptor == null ? null : fontDescriptor.isSmallCap();
	}

	public Boolean isForceBold() {
		if (forceBold == null) {
			// override by AMIFont
			if (nonStandardFontFamily != null) {
				forceBold = nonStandardFontFamily.isForceBold();
				if (forceBold != null && forceBold) {
					LOG.trace(fontFamilyName+" BOLD");
				}
			}
			if (forceBold == null || !forceBold) {
				if (fontDescriptor != null) {
					forceBold = fontDescriptor.isForceBold();
					LOG.trace(fontFamilyName+" "+forceBold);
				} else {
					forceBold = false;
				}
			} 
		}
		return forceBold;
	}

	public Float getItalicAngle() {
		return fontDescriptor == null ? null : fontDescriptor.getItalicAngle();
	}

	public Float getAscent() {
		return fontDescriptor == null ? null : fontDescriptor.getAscent();
	}

	public Float getDescent() {
		return fontDescriptor == null ? null : fontDescriptor.getDescent();
	}

	public Float getLeading() {
		return fontDescriptor == null ? null : fontDescriptor.getLeading();
	}

	public Float getCapHeight() {
		return fontDescriptor == null ? null : fontDescriptor.getCapHeight();
	}

	public Float getXHeight() {
		return fontDescriptor == null ? null : fontDescriptor.getXHeight();
	}

	public Float getStemV() {
		return fontDescriptor == null ? null : fontDescriptor.getStemV();
	}

	public Float getStemH() {
		return fontDescriptor == null ? null : fontDescriptor.getStemH();
	}

	public Float getAverageWidth() throws IOException {
		return fontDescriptor == null ? null : fontDescriptor.getAverageWidth();
	}

	public Float getMaxWidth() {
		return fontDescriptor == null ? null : fontDescriptor.getMaxWidth();
	}

	public String getCharSet() {
		return fontDescriptor == null ? null : fontDescriptor.getCharSet();
	}

	public Float getMissingWidth() {
		return fontDescriptor == null ? null : fontDescriptor.getMissingWidth();
	}

	public NonStandardFontFamily getOrCreateNonStandardFontFamily(NonStandardFontManager amiFontManager) {
		if (this.nonStandardFontFamily == null) {
			String fontFamilyString = getFontFamilyString();
			if (fontFamilyString != null) {
				nonStandardFontFamily = amiFontManager.getFontFamilyByFamilyName(fontFamilyString);
			}
		}
		return nonStandardFontFamily;
	}

	/** guesses bold from name
	 * 
	 * @return
	 */
	public boolean isHeuristicBold() {
		boolean bold = fontName.toLowerCase().contains("bold") || fontName.toLowerCase().contains(".b");
		return bold;
	}

	/** guesses italic from name
	 * 
	 * @return
	 */
	public boolean isHeuristicItalic() {
		boolean bold = fontName.toLowerCase().contains("ital") || fontName.toLowerCase().contains(".i");
		return bold;
	}

	/** guesses bold from name
	 * 
	 * @return
	 */
	public boolean isHeuristicFixedPitch() {
		boolean fixed = fontFamilyName.toLowerCase().contains("cmtt") || fontName.toLowerCase().contains("cmtt") ;
		return fixed;
	}

	public NonStandardFontFamily getNonStandardFontFamily() {
		return nonStandardFontFamily;
	}

}
