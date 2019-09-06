package org.contentmine.svg2xml.words;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGTSpan;
import org.contentmine.graphics.svg.SVGText;

import nu.xom.Attribute;

/** may be obsolete, but not checked */
public class TypedNumber {

	private final static Logger LOG = Logger.getLogger(TypedNumber.class);
	
	Number number = null;
	private List<Number> numberList = null;
	private String dataType = null;

	public static final String DATA_TYPE = "dataType";

	public static final String NUMBER = "number";

	public static final String NUMBERS = "numbers";

	/** create either from the text value of Child TSpans
	 * 
	 * @param text
	 */
	TypedNumber(SVGText text) {
		if (text.getChildElements().size() == 0) {
			createFromString(text.getValue().trim());
		} else {
			TypedNumber typedNumber = createFromText(text);
			if (typedNumber != null) {
				this.number = typedNumber.number;
				this.dataType = typedNumber.dataType;
			}
		}
	}

	public TypedNumber(Double dubble) {
		this.number = dubble;
		dataType = XMLConstants.XSD_DOUBLE;
	}

	public TypedNumber(Double abscissa, Integer power) {
		Double exponentiated  =Math.pow(10.0, (double) power);
		this.number = abscissa * exponentiated;
		dataType = XMLConstants.XSD_DOUBLE;
	}

	private void createFromString(String value) {
		createInteger(value);
		createDouble(value);
	}

	/** create from SVGText 
	 * may have textString value
	 * 1 TSpan with value
	 * 2 tSpans with exponential SUPERSCRIPT
	 * @param text
	 * @return
	 */
	public static TypedNumber createFromText(SVGText text) {
		TypedNumber typedNumber = null;
		List<SVGTSpan> tSpans = text.getChildTSpans();
		if (tSpans.size() == 0) {
			typedNumber = new TypedNumber(text); 
		} else if (tSpans.size() == 1) {
			typedNumber = TypedNumber.createNumber(tSpans.get(0)); 
		} else if (tSpans.size() == 2) {
			typedNumber = interpretExponentialNotation(tSpans);
		}
		return typedNumber;
	}

	/** requires a list of exactly 2 
	 * 
	 * @param tSpans
	 * @return
	 */
	public static TypedNumber interpretExponentialNotation(List<SVGTSpan> tSpans) {
		TypedNumber typedNumber = null;
//		// of form 1.2x10<sup>34</sup>
//		if (tSpans.size() == 2) {
//			SVGTSpan tSpan0 = tSpans.get(0);
//			SVGTSpan tSpan1 = tSpans.get(1);
//			Integer power = null;
//			if (SubSup.SUPERSCRIPT.toString().equals(tSpan1.getAttributeValue(SubSupAnalyzerX.SCRIPT_TYPE))) {
//				try {
//					power = new Integer(tSpan1.getValue());
//					typedNumber = createAndParseExponentialForm(tSpan0.getValue().trim(), power);
//				} catch (Exception e) {
//				}
//			}
//		}
		return typedNumber;
	}

	public static TypedNumber createAndParseExponentialForm(String abscissaText, Integer power) {
		TypedNumber typedNumber = null;
		if (abscissaText.endsWith("10")) {
			abscissaText = abscissaText.substring(0,  abscissaText.length()-2);
			if (abscissaText.length() == 0) {
				abscissaText = abscissaText + "1.0";
			} else {
				// deal with multiplier (times) character
				if (abscissaText.endsWith("x") || abscissaText.endsWith("X")) {
					abscissaText = abscissaText.substring(0,  abscissaText.length()-1);
				}
			}
			abscissaText = abscissaText + "E";
			if (power >= 0) {
				abscissaText = abscissaText + "+";
			}
			abscissaText = abscissaText + power;
			LOG.trace("SUPERSCRIPTED NUMBER "+abscissaText);
			double dd = Real.parseDouble(abscissaText);
			if (!Double.isNaN(dd)) {                  
				typedNumber = new TypedNumber(new Double(dd));
			}
		}
		return typedNumber;
	}
	
	public static TypedNumber createNumber(SVGText text) {
		TypedNumber number = new TypedNumber(text);
		return number.number == null ? null : number;
	}

	public Number getNumber() {
		return number;
	}

	public String getDataType() {
		return dataType;
	}

	private void createDouble(String value) {
		if (number == null) {
			try {
				Double dubble = Double.valueOf(value);
				number = dubble;
				dataType = XMLConstants.XSD_DOUBLE;
			} catch (Exception e1) {
			}
		}
	}

	private void createInteger(String value) {
		try {
			Integer integer = new Integer(value);
			number = integer;
			dataType = XMLConstants.XSD_INTEGER;
		} catch (Exception e) {
		}
	}

	void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void convertToDouble() {
		if (!(number instanceof Double) || !XMLConstants.XSD_DOUBLE.equals(dataType)) {
			number = new Double((Integer)number);
			dataType = XMLConstants.XSD_DOUBLE;
		}
	}

	public static String getNumericValue(SVGElement numericText) {
		return numericText.getAttributeValue(NUMBER);
	}

	//	private void addNumericValues() {
	//		// not quite sure when the TSpans get added so this is messy
	//		List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgg, ".//svg:g[@class='word']/svg:text"));
	//		for (SVGText text : texts) {
	//			TypedNumberList typedNumberList = interpretTypedNumberList(text);
	//			TypedNumber typedNumber = interpretTypedNumber(text);
	//		}
	//	}
	
		private static TypedNumber interpretTypedNumber(SVGText text, boolean removeNumericTSpans) {
			TypedNumber typedNumber = TypedNumber.createNumber(text);
			if (typedNumber != null) {
				String number = String.valueOf(typedNumber.getNumber());
				text.addAttribute(new Attribute(NUMBER, number));
				text.addAttribute(new Attribute(DATA_TYPE, typedNumber.getDataType()));
				if (removeNumericTSpans) {
					removeNumericTSpans(text, number);
				}
			}
			return typedNumber;
		}

	private static TypedNumberList interpretTypedNumberList(SVGText text, boolean removeNumericTSpans) {
		TypedNumberList typedNumberList = TypedNumberList.createFromTextSpans(text);
		if (typedNumberList != null) {
			String numbers = typedNumberList.getNumberString();
			text.addAttribute(new Attribute(NUMBERS, numbers));
			text.addAttribute(new Attribute(DATA_TYPE, typedNumberList.getDataType()));
			if (removeNumericTSpans) {
				removeNumericTSpanList(text, numbers);
			}
		}
		return typedNumberList;
	}

	private static void removeNumericTSpanList(SVGText text, String number) {
		List<SVGTSpan> tSpans = text.getChildTSpans();
		text.setText(number);
		for (SVGTSpan tSpan : tSpans) {
			tSpan.detach();
		}
	}

	private static void removeNumericTSpans(SVGText text, String number) {
		List<SVGTSpan> tSpans = text.getChildTSpans();
		if (tSpans.size() == 1) {
			tSpans.get(0).detach();
			text.setText(number);
		} else if (tSpans.size() == 2) {
			tSpans.get(0).detach();
			tSpans.get(1).detach();
			text.setText(number);
		}
	}

}
