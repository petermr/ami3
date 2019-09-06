package org.contentmine.svg2xml.text;

import java.util.Arrays;
import java.util.List;

import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGTSpan;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.words.TypedNumber;
import org.contentmine.graphics.svg.words.TypedNumberList;
import org.junit.Assert;
import org.junit.Test;

public class TypedNumberTest {

	private double EPS = 0.0001;
	
	@Test
	public void testCreateFromNumbers() {
		String abscissaText = "1.23x10";
		Integer power = 4;
		TypedNumber typedNumber = TypedNumber.createAndParseExponentialForm(abscissaText, power);
		Assert.assertNotNull("typedNumber", typedNumber);
		Assert.assertEquals("typedNumber type", XMLConstants.XSD_DOUBLE, typedNumber.getDataType());
		Double d = (Double) typedNumber.getNumber();
		Assert.assertEquals("abscissa", 12300.0, d, EPS);
	}
	
	@Test
	public void testCreateFromNumbers1() {
		String abscissaText = "1.23x10";
		Integer power = -4;
		TypedNumber typedNumber = TypedNumber.createAndParseExponentialForm(abscissaText, power);
		Assert.assertNotNull("typedNumber", typedNumber);
		Assert.assertEquals("typedNumber type", XMLConstants.XSD_DOUBLE, typedNumber.getDataType());
		Double d = (Double) typedNumber.getNumber();
		Assert.assertEquals("abscissa", 0.000123, d, EPS);
	}
	
	@Test
	public void testCreateFromNumbers2() {
		String abscissaText = "1.23x5";
		Integer power = -4;
		TypedNumber typedNumber = TypedNumber.createAndParseExponentialForm(abscissaText, power);
		Assert.assertNull("typedNumber", typedNumber);
	}
	
	@Test
	public void testCreateFromTSpans() {

		SVGTSpan[] tSpans = new SVGTSpan[2];
//		tSpans[0] = new SVGTSpan(new Real2(1.0, 2.0), "1.23x10");
//		tSpans[1] = new SVGTSpan(new Real2(11.0, 2.0), "4");
//		List<SVGTSpan> spanList = Arrays.asList(tSpans);
//		SubSupAnalyzer.markSubSup(SubSup.SUPERSCRIPT, tSpans[1]);
//		TypedNumber typedNumber = TypedNumber.interpretExponentialNotation(spanList);
//		Assert.assertNotNull("typedNumber", typedNumber);
//		Assert.assertEquals("typedNumber type", XMLConstants.XSD_DOUBLE, typedNumber.getDataType());
//		Double d = (Double) typedNumber.getNumber();
//		Assert.assertEquals("abscissa", 12300.0, d, EPS);
	}
	
	@Test
	public void testCreateFromTSpans1() {

		SVGTSpan[] tSpans = new SVGTSpan[2];
//		tSpans[0] = new SVGTSpan(new Real2(1.0, 2.0), "1.23x10");
//		tSpans[1] = new SVGTSpan(new Real2(11.0, 2.0), "4");
//		List<SVGTSpan> spanList = Arrays.asList(tSpans);
//		SubSupAnalyzer.markSubSup(SubSup.SUBSCRIPT, tSpans[1]);
//		TypedNumber typedNumber = TypedNumber.interpretExponentialNotation(spanList);
//		Assert.assertNull("typedNumber", typedNumber);
	}
	
	@Test
	public void testCreateListFromTSpans1() {

		SVGTSpan[] tSpans = new SVGTSpan[] {
			new SVGTSpan(new Real2(1.0, 2.0), "3"),
			new SVGTSpan(new Real2(11.0, 2.0), "4"),
			new SVGTSpan(new Real2(21.0, 2.0), "5"),
		};
		List<SVGTSpan> spanList = Arrays.asList(tSpans);
		TypedNumberList typedNumberList = TypedNumberList.createList(spanList);
		Assert.assertNotNull("typedNumberList", typedNumberList);
		Assert.assertEquals("typedNumberList", 3, (int) typedNumberList.size());
		Assert.assertEquals("typedNumberList", XMLConstants.XSD_INTEGER, typedNumberList.getDataType());
		Assert.assertEquals("typedNumberList", XMLConstants.XSD_INTEGER, typedNumberList.get(0).getDataType());
		IntArray intArray = typedNumberList.getIntArray();
		Assert.assertNull("reals", typedNumberList.getRealArray());
		Assert.assertNotNull("integers", intArray);
		Assert.assertEquals("integers", 3, intArray.size());
		Assert.assertEquals("integers", "(3,4,5)", intArray.toString());
	}
	
	@Test
	public void testCreateListFromTSpans2() {

		SVGTSpan[] tSpans = new SVGTSpan[] {
			new SVGTSpan(new Real2(1.0, 2.0), "3.1"),
			new SVGTSpan(new Real2(11.0, 2.0), "4"),
			new SVGTSpan(new Real2(21.0, 2.0), "5"),
		};
		List<SVGTSpan> spanList = Arrays.asList(tSpans);
		TypedNumberList typedNumberList = TypedNumberList.createList(spanList);
		Assert.assertNotNull("typedNumberList", typedNumberList);
		Assert.assertEquals("typedNumberList", 3, (int) typedNumberList.size());
		Assert.assertEquals("typedNumberList", XMLConstants.XSD_DOUBLE, typedNumberList.getDataType());
		Assert.assertEquals("typedNumberList", XMLConstants.XSD_DOUBLE, typedNumberList.get(0).getDataType());
		Assert.assertNull("integers", typedNumberList.getIntArray());
		RealArray realArray = typedNumberList.getRealArray();
		Assert.assertNotNull("reals", realArray);
		Assert.assertEquals("reals", 3, realArray.size());
		Assert.assertEquals("reals", "(3.1,4.0,5.0)", realArray.toString());
	}
	
	@Test
	public void testCreateListFromTSpans3() {

		SVGTSpan[] tSpans = new SVGTSpan[] {
			new SVGTSpan(new Real2(1.0, 2.0), "3"),
			new SVGTSpan(new Real2(11.0, 2.0), "4"),
			new SVGTSpan(new Real2(21.0, 2.0), "5.1"),
		};
		List<SVGTSpan> spanList = Arrays.asList(tSpans);
		TypedNumberList typedNumberList = TypedNumberList.createList(spanList);
		Assert.assertNotNull("typedNumberList", typedNumberList);
		Assert.assertEquals("typedNumberList", 3, (int) typedNumberList.size());
		Assert.assertEquals("typedNumberList", XMLConstants.XSD_DOUBLE, typedNumberList.getDataType());
		Assert.assertEquals("typedNumberList", XMLConstants.XSD_DOUBLE, typedNumberList.get(0).getDataType());
	}
	
	@Test
	public void testCreateListFromTSpans4() {

		SVGTSpan[] tSpans = new SVGTSpan[] {
			new SVGTSpan(new Real2(1.0, 2.0), "3"),
			new SVGTSpan(new Real2(11.0, 2.0), "four"),
			new SVGTSpan(new Real2(21.0, 2.0), "5.1"),
		};
		List<SVGTSpan> spanList = Arrays.asList(tSpans);
		TypedNumberList typedNumberList = TypedNumberList.createList(spanList);
		Assert.assertNull("typedNumberList", typedNumberList);
	}
	
	@Test
	public void testCreateNumberFromText() {
		SVGText text = new SVGText();
		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "3"));
		TypedNumber typedNumber = TypedNumber.createFromText(text);
		Assert.assertNotNull("typedNumber", typedNumber);
		Assert.assertEquals("typedNumber", 3, (int) (Integer) typedNumber.getNumber());
		Assert.assertEquals("typedNumber", XMLConstants.XSD_INTEGER, typedNumber.getDataType());
	}
	
	@Test
	public void testCreateNumberFromText1() {
		SVGText text = new SVGText();
		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "3.1"));
		TypedNumber typedNumber = TypedNumber.createFromText(text);
		Assert.assertNotNull("typedNumber", typedNumber);
		Assert.assertEquals("typedNumber", 3.1, (double) (Double) typedNumber.getNumber(), EPS);
		Assert.assertEquals("typedNumber", XMLConstants.XSD_DOUBLE, typedNumber.getDataType());
	}
	
	@Test
	public void testCreateNumberFromText2() {
		SVGText text = new SVGText();
		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "three"));
		TypedNumber typedNumber = TypedNumber.createFromText(text);
		Assert.assertNull("typedNumber", typedNumber);
	}
	
	@Test
	public void testCreateNumberFromText3() {
		SVGText text = new SVGText();
		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "1.23x10"));
		AbstractCMElement tSpan = new SVGTSpan(new Real2(11.0, 2.0), "4");
		text.appendChild(tSpan);
		TypedNumber typedNumber = TypedNumber.createFromText(text);
		Assert.assertNull("typedNumber", typedNumber);
	}
	
	@Test
	public void testCreateNumberFromText4() {
		AbstractCMElement text = new SVGText();
//		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "1.23x10"));
//		SVGText tSpan = new SVGTSpan(new Real2(11.0, 2.0), "4");
//		SubSupAnalyzer.markSubSup(SubSup.SUPERSCRIPT, tSpan);
//		text.appendChild(tSpan);
//		TypedNumber typedNumber = TypedNumber.createFromText(text);
//		Assert.assertNotNull("typedNumber", typedNumber);
//		Assert.assertEquals("superscript", 12300.0, (Double) typedNumber.getNumber(), EPS); 
	}
	
	@Test
	public void testCreateTypedNumberListFromText() {
		SVGText text = new SVGText();
		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "1"));
		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "2"));
		text.appendChild(new SVGTSpan(new Real2(1.0, 2.0), "3"));
		TypedNumber typedNumber = TypedNumber.createFromText(text);
		Assert.assertNull("typedNumber", typedNumber);
		TypedNumberList typedNumberList = TypedNumberList.createFromTextSpans(text);
		Assert.assertEquals("typedNumberList", "1 2 3", typedNumberList.getNumberString()); 
		Assert.assertNotNull("typedNumberList", typedNumberList); 
		Assert.assertEquals("typedNumberList", 3, (int) typedNumberList.size()); 
		Assert.assertEquals("typedNumberList", 1, (int) (Integer) typedNumberList.get(0).getNumber()); 
	}
	
}
