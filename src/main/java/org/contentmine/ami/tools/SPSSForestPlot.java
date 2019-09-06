package org.contentmine.ami.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.image.ocr.HOCRReader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Attribute;
import nu.xom.Element;

public class SPSSForestPlot {
	private static final Logger LOG = Logger.getLogger(SPSSForestPlot.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	Pattern GROUP_NAME = Pattern.compile("R\\(\\?<([^>]+).*");

	private HtmlElement htmlElement;
	private List<HtmlSpan> wordSpanList;
	private Multimap<Integer, HtmlSpan> spanByXMin;
	private Multimap<Integer, HtmlSpan> spanByXMax;
	private Multimap<Integer, HtmlSpan> spanByYMin;
	private Multimap<Integer, HtmlSpan> spanByYMax;
	private List<Multiset.Entry<Integer>> xminSortedByValueList;
	private List<Multiset.Entry<Integer>> xminSortedByCountList;
	private List<Multiset.Entry<Integer>> yminSortedByValueList;
	private List<Multiset.Entry<Integer>> yminSortedByCountList;
	private List<Multiset.Entry<Integer>> xmaxSortedByValueList;
	private List<Multiset.Entry<Integer>> xmaxSortedByCountList;
	private List<Entry<Integer>> yMinBundledSortedByValueList;
	private List<Entry<Integer>> xMinBundledSortedByValueList;
	private List<Entry<Integer>> xMaxBundledSortedByValueList;
	private IntArray xValues;
	private IntArray yValues;
	private Int2Range boundingBox;
	private int deltaY;
	private Multimap<Integer, HtmlSpan> wordSpanByYValue;
	private Map<Integer, List<HtmlSpan>> sortedWordSpansByY;
	
	public SPSSForestPlot() {
		
	}
	
	public SPSSForestPlot readHOCR(HtmlElement htmlElement) {
		SPSSForestPlot spssForestPlot = new SPSSForestPlot(); 
		int xmax = 770;
		int tableEnd = xmax; //kludge until we iterate over segmentation
		spssForestPlot.setBoundingBox(
				new Int2Range(new IntRange(0,tableEnd), new IntRange(0,1000)));
		spssForestPlot.extractTable(htmlElement);
		return spssForestPlot;
	}

	public void setBoundingBox(Int2Range bbox) {
		this.boundingBox = bbox;
	}

	private void extractTable(HtmlElement htmlElement) {
		if (htmlElement != null) {
			this.setHtmlElement(htmlElement);
			this.extractWordSpans();
			this.extractHOCRBBoxes();
			int tolerance = 2;
			this.extractMaxMinLists(tolerance);
			this.getTableXValues();
			yValues = this.getTableYValues();
			
			this.extractLines();
		} else {
			LOG.debug("null htmlElement");
		}
	}
	
	private IntArray getTableYValues() {
		yValues = MultisetUtil.extractSortedArrayOfValues(yMinBundledSortedByValueList, 4);
		deltaY = yValues.getCommonestDiff();
		
		List<Double> separation = Arrays.asList(new Double[] {1.0, 2.0, 1.15, 1.0});
		int tol = 2;
		int smallStep = 2 * tol;
		int yStart = 30;
		IntArray newYValues = null;
		try {
			newYValues = yValues.mapOntoTemplateDiffs(deltaY, separation, tol, smallStep, yStart);
		} catch (RuntimeException e) {
			LOG.debug("separation Values don't fit table: "+separation);
		}
		return newYValues;
		
	}

	private IntArray getTableXValues() {
		xValues = MultisetUtil.extractSortedArrayOfValues(xMinBundledSortedByValueList, 4);
		return xValues;
	}

	private void extractMaxMinLists(int tolerance) {
		yMinBundledSortedByValueList = MultisetUtil.bundleCounts(yminSortedByValueList, tolerance);
		xMaxBundledSortedByValueList = MultisetUtil.bundleCounts(xmaxSortedByValueList, tolerance);
		xMinBundledSortedByValueList = MultisetUtil.bundleCounts(xminSortedByValueList, tolerance);
		return;

	}

	public List<Entry<Integer>> getyMinBundledSortedByValueList() {
		return yMinBundledSortedByValueList;
	}

	public List<Entry<Integer>> getxMinBundledSortedByValueList() {
		return xMinBundledSortedByValueList;
	}

	public List<Entry<Integer>> getxMaxBundledSortedByValueList() {
		return xMaxBundledSortedByValueList;
	}


	private void extractWordSpans() {
		List<Element> elements = XMLUtil.getQueryElements(htmlElement, 
				"//*[local-name()='span' and @class='ocrx_word']");
		
		wordSpanList = new ArrayList<>();
		for (Element element : elements) {
			HtmlSpan spanElement = (HtmlSpan)element;
			String value = spanElement.getValue();
			
			wordSpanList.add(spanElement);
		}
		
	}

	private void extractHOCRBBoxes() {
		spanByXMin = HashMultimap.create();
		spanByXMax = HashMultimap.create();
		spanByYMin = HashMultimap.create();
		spanByYMax = HashMultimap.create();
		// make new list with only included boxes
		List<HtmlSpan> includedWordSpans = new ArrayList<HtmlSpan>();
		for (HtmlSpan wordSpan : wordSpanList) {
			Int2Range bbox = HOCRReader.getBboxFromTitle(wordSpan);
			String content = wordSpan.getValue();
			
//			bbox = HOCRReader.adjustForADescenders(bbox);
			if (boundingBox != null && !boundingBox.includes(bbox)) {
//				System.out.println("EXCLUDE: "+bbox+ " | "+boundingBox + " | "+content);
				continue;
			}
			includedWordSpans.add(wordSpan);
			spanByXMin.put(bbox.getXRange().getMin(), wordSpan);
			spanByXMax.put(bbox.getXRange().getMax(), wordSpan);
			spanByYMin.put(bbox.getYRange().getMin(), wordSpan);
			spanByYMax.put(bbox.getYRange().getMax(), wordSpan);
		}
		wordSpanList = includedWordSpans;
		yminSortedByValueList = MultisetUtil.createListSortedByValue(spanByYMin.keys());
		yminSortedByCountList = MultisetUtil.createListSortedByCount(spanByYMin.keys());
		xminSortedByValueList = MultisetUtil.createListSortedByValue(spanByXMin.keys());
		xminSortedByCountList = MultisetUtil.createListSortedByCount(spanByXMin.keys());
		xmaxSortedByValueList = MultisetUtil.createListSortedByValue(spanByXMax.keys());
		xmaxSortedByCountList = MultisetUtil.createListSortedByCount(spanByXMax.keys());
	}

	public void setHtmlElement(HtmlElement htmlElement) {
		this.htmlElement = htmlElement;
	}

	private void extractLines() {
		mapWordSpansToYLines();
		Element spssTable = createColumns();
		XMLUtil.debug(spssTable);
//		LOG.debug(">>"+wordSpanByYValue);
//		extractOddsRatio();
//		extractStudyOrSubgroup();
//		boolean subtotal = false;
//		extractTotal(subtotal);
//		subtotal = true;
//		extractTotal(subtotal);
//		extractTotalEvents(subtotal);
//		extractHeterogeneity();
//		extractTestForOverallEffect();
	}

	private Element createColumns() {
		if (yValues == null) {
			LOG.error("null yValues");
			return null;
		}
		Element tableElement = new Element("spssTable");
		int numColumns = -1;
		int irow = 0;
		for (; irow < yValues.size(); irow++) {
			List<HtmlSpan> spanList = extractSpanList(irow);
			String start = spanList.get(0).getValue();
			int size = spanList.size();
			if (start.equals("Total")) {
				break;
			}
			if (numColumns < 0) {
				numColumns = size;
			} else if (numColumns != size) {
				LOG.debug(start+"; size changed from "+numColumns+" to "+size + HtmlSpan.toString(spanList));
				
			}
		}
		Element subTotalElement = extractSubTotal(irow);
		irow = addChild(tableElement, irow, subTotalElement);
		Element totalElement = extractTotal(irow);
		irow = addChild(tableElement, irow, totalElement);
		Element totalEventsElement = extractTotalEvents(irow);
		irow = addChild(tableElement, irow, totalEventsElement);
		Element heterogeneityElement = extractHeterogeneity(irow);
		irow = addChild(tableElement, irow, heterogeneityElement);
		Element testForOverallEffect = extractTestForOverallEffect(irow);
		irow = addChild(tableElement, irow, testForOverallEffect);
		return tableElement;
	}

	private int addChild(Element tableElement, int irow, Element childElement) {
		if (childElement != null) {
			tableElement.appendChild(childElement);
			irow++;
		}
		return irow;
	}

	private List<HtmlSpan> extractSpanList(int irow) {
		if (irow >= yValues.size()) {
			LOG.debug("row "+irow+" > number of lines");
			return null;
		}
		int yValue = yValues.elementAt(irow);
		List<HtmlSpan> spanList = sortedWordSpansByY.get(yValue);
		return spanList;
	}

	private void mapWordSpansToYLines() {
		if (yValues == null) {
			LOG.debug("null yValues");
			return;
		}
		wordSpanByYValue = ArrayListMultimap.create();
		for (HtmlSpan wordSpan : wordSpanList) {
			Int2Range bbox = HOCRReader.getBboxFromTitle(wordSpan);
			int boxTop = bbox.getYRange().getMin();
			Integer yValue = null;
			for (int i = 0; i < yValues.size(); i++) {
				yValue = yValues.elementAt(i);
				if (Math.abs(boxTop - yValue) < (deltaY * 0.5)) {
					wordSpanByYValue.put(yValue, wordSpan);
					break;
				}
			}
			if (yValue == null) {
				System.out.println("can't match >"+yValue+"> "+wordSpan.getValue());
			}
		}
		
		sortedWordSpansByY = new HashMap<>();
		for (int i = 0; i < yValues.size(); i++) {
			int yValue = yValues.elementAt(i);
			Collection<HtmlSpan> c = wordSpanByYValue.get(yValue);
			List<HtmlSpan> wordSpanList = new ArrayList<HtmlSpan>(c);
			HOCRReader.sortWordSpansByX(wordSpanList);
			sortedWordSpansByY.put(yValue, wordSpanList);
		}
	}

	
	/**
    <span class='ocr_line' id='line_1_2' title="bbox 0 21 747 36; baseline 0 -1; x_size 19.5; x_descenders 5.5; x_ascenders 3">
     <span class='ocrx_word' id='word_1_5' title='bbox 0 0 1267 354; x_wconf 1'>Study</span>
     <span class='ocrx_word' id='word_1_6' title='bbox 0 0 1267 354; x_wconf 95'>or</span>
     <span class='ocrx_word' id='word_1_7' title='bbox 0 0 1267 354; x_wconf 91'>Subgroup</span>
     <span class='ocrx_word' id='word_1_8' title='bbox 0 0 1267 354; x_wconf 59'>__Events</span>
     <span class='ocrx_word' id='word_1_9' title='bbox 0 21 747 36; x_wconf 37'>_Total____Events</span>
     <span class='ocrx_word' id='word_1_10' title='bbox 0 0 1267 354; x_wconf 43'>_Total_Weight</span>
     <span class='ocrx_word' id='word_1_11' title='bbox 0 0 1267 354; x_wconf 61'>_M-H.</span>
     <span class='ocrx_word' id='word_1_12' title='bbox 0 0 1267 354; x_wconf 89'>Fixed.</span>
     <span class='ocrx_word' id='word_1_13' title='bbox 0 0 1267 354; x_wconf 96'>95%</span>
     <span class='ocrx_word' id='word_1_14' title='bbox 0 0 1267 354; x_wconf 72'>Cl</span>
     <span class='ocrx_word' id='word_1_15' title='bbox 0 0 1267 354; x_wconf 96'>Year</span>
    </span>
*/
	private void extractStudyOrSubgroup() {
		
	}
	
	private Element extractSubTotal(int irow) {
		String[] fields = {
				"Subtotal",
				"R(?<percent>\\((\\d+\\%))",
				"R(CI?l?)",
				"R(?<total1>(\\d+\\.\\d+))",
				"R(?<total2>(\\d+\\.\\d+))",
				"R(?<weight>(\\d+\\.\\d+)\\%)",
				"R(?<mean>(\\d+\\.\\d+))",
				"R(?<low>\\[(\\d+\\.\\d+)\\,)",
				"R(?<high>(\\d+\\.\\d+)\\])"
		};
		Element element = createXMLFromRow("subtotal", irow, fields, "Subtotal");
		return element;
	}

	private List<HtmlSpan> matchLineStart(int irow, String field) {
		String[] fields = field.trim().split("\\s+");
		List<HtmlSpan> spanList = extractSpanList(irow);
		if (spanList == null || fields.length > spanList.size()) return null;
		for (int i = 0; i < fields.length; i++) {
			String value = spanList.get(i).getValue();
			if (!fields[i].equals(value)) return null;
		}
		return spanList;
	}
	
	private Element extractTotal(int irow) {
		String[] fields = {
				"Total",
				"R(?<percent>\\((\\d+\\%))",
				"R(CI?l?\\))",
				"R(?<total1>\\d+)",
				"R(?<total2>\\d+)",
				"R(?<weight>(\\d+\\.\\d+)\\%)",
				"R(?<mean>(\\-?\\d+\\.\\d+))",
				"R(?<low>\\[?\\(?(\\-?\\d+\\.\\d+)\\,)",
				"R(?<high>(\\-?\\d+\\.\\d+)\\])"
		};
		Element element = createXMLFromRow("total", irow, fields, "Total");
		return element;
	}


	/**
<span class='ocr_line' id='line_1_14' title="bbox 13 289 390 302; baseline -0.003 0; x_size 23.125; x_descenders 5.5; x_ascenders 5.875">
      <span class='ocrx_word' id='word_1_70' title='bbox 13 289 47 302; x_wconf 96'>Total</span>
      <span class='ocrx_word' id='word_1_71' title='bbox 52 290 97 302; x_wconf 96'>events</span>
      <span class='ocrx_word' id='word_1_72' title='bbox 212 290 228 302; x_wconf 87'>69</span>
      <span class='ocrx_word' id='word_1_73' title='bbox 366 290 390 302; x_wconf 96'>107</span>
     </span>	 * 
	 * @param subtotal
	 */
	private Element extractTotalEvents(int irow) {
		String[] fields = {
				"Total",
				"events",
				"R(?<total1>\\d+)",
				"R(?<total2>\\d+)"
		};
		
		Element element = createXMLFromRow("totalEvents", irow, fields, "Total events");
		return element;

	}

	private Element createXMLFromRow(String name, int irow, String[] fields, String field) {
		Element fieldElement = null;
		List<HtmlSpan> spanList = matchLineStart(irow, field);
		if (spanList != null) {
			LOG.debug("....extract "+field);
			fieldElement = extractToXML(name, spanList, fields);
			irow++;
		}
		return fieldElement;
	}

	private void extractOddsRatio() {
		LOG.debug("extractOddsRatio");
	}

	

	/**
   <div class='ocr_carea' id='block_1_3' title="bbox 13 310 387 345">
    <p class='ocr_par' id='par_1_4' lang='eng' title="bbox 13 310 387 345">
     <span class='ocr_line' id='line_1_15' title="bbox 14 310 387 325; baseline 0 -3; x_size 24.25; x_descenders 5.5; x_ascenders 6.25">
      <span class='ocrx_word' id='word_1_74' title='bbox 14 310 112 325; x_wconf 96'>Heterogeneity:</span>
      <span class='ocrx_word' id='word_1_75' title='bbox 118 310 146 322; x_wconf 50'>Chi?</span>
      <span class='ocrx_word' id='word_1_76' title='bbox 151 313 159 319; x_wconf 95'>=</span>
      <span class='ocrx_word' id='word_1_77' title='bbox 165 310 205 324; x_wconf 95'>14.61,</span>
      <span class='ocrx_word' id='word_1_78' title='bbox 210 310 224 322; x_wconf 95'>df</span>
      <span class='ocrx_word' id='word_1_79' title='bbox 228 313 236 319; x_wconf 86'>=</span>
      <span class='ocrx_word' id='word_1_80' title='bbox 241 310 249 322; x_wconf 86'>9</span>
      <span class='ocrx_word' id='word_1_81' title='bbox 254 310 269 325; x_wconf 92'>(P</span>
      <span class='ocrx_word' id='word_1_82' title='bbox 274 313 282 319; x_wconf 91'>=</span>
      <span class='ocrx_word' id='word_1_83' title='bbox 286 310 325 325; x_wconf 91'>0.10);</span>
      <span class='ocrx_word' id='word_1_84' title='bbox 331 310 340 322; x_wconf 1'>F*</span>
      <span class='ocrx_word' id='word_1_85' title='bbox 344 313 352 319; x_wconf 92'>=</span>
      <span class='ocrx_word' id='word_1_86' title='bbox 357 310 387 322; x_wconf 92'>38%</span>
     </span>
    </p>
	 */
	private Element extractHeterogeneity(int irow) {
		String[] fields = {
				"Heterogeneity:",
				"R(Chi.*)",
				"=",
				"R(?<chi2>(\\d+\\.\\d+)\\,)",
				"df",
				"=",
				"R(?<df>\\d+)",
				"R(\\(P)",
				"R(?<op>=|<)",
				"R(?<p>(\\d+\\.\\d+)\\)\\;)",
				"R((I.?)|(F.?))",
				"=",
				"R(?<i2>(\\d+)\\%)"
		};
		Element element = createXMLFromRow("heterogeneity", irow, fields, "Heterogeneity:");
		return element;

	}
	

	private Element extractToXML(String tag, List<HtmlSpan> spanList, String[] fields) {
		Element element = null;
		LOG.debug("spans: "+HtmlSpan.toString(spanList));
		if (spanList.size() == fields.length) {
			element = new Element(tag);
			for (int i = 0; i < fields.length; i++) {
				String field = fields[i];
				String name = null;
				String group = null;
				Pattern fieldPattern = null;
				if (field.startsWith("R(")) {
					fieldPattern = Pattern.compile(field.substring(1));
					name = getNameOfGroup(field);
				}
				String span = spanList.get(i).getValue();
				boolean matches = false;
				if (fieldPattern == null) {
					matches = field.contentEquals(span);
				} else {
					Matcher fieldMatcher = fieldPattern.matcher(span);
					matches = fieldMatcher.matches();
					if (!matches) {
						LOG.debug("NO MATCH "+span+" | "+fieldPattern);
					}
					if (matches && name != null) {
						group = fieldMatcher.group(fieldMatcher.groupCount());
					}
					if (name != null) {
						Attribute att = new Attribute(name, String.valueOf(group));
						element.addAttribute(att);
					}
				}
				
//				System.out.println(field+" | "+span+" | "+matches);
			}
		} else {
			
			LOG.debug("cannot match: "+tag+">"+spanList.size()+" != "+fields.length+" | "+HtmlSpan.toString(spanList));
		}
		return element;
	}

	private String getNameOfGroup(String field) {
		Matcher matcher = GROUP_NAME.matcher(field);
		String name = null;
		if (matcher.matches()) {
			name = matcher.group(1);
		} else {
//			System.out.println("cannot match: "+field);
		}
		return name;
	}

		/** TestForOverallEffect
   <span class='ocr_line' id='line_1_16' title="bbox 13 330 303 345; baseline -0.003 -2; x_size 24.25; x_descenders 5.5; x_ascenders 6.25">
    <span class='ocrx_word' id='word_1_87' title='bbox 13 330 43 343; x_wconf 94'>Test</span>
    <span class='ocrx_word' id='word_1_88' title='bbox 47 330 66 343; x_wconf 94'>for</span>
    <span class='ocrx_word' id='word_1_89' title='bbox 70 330 114 343; x_wconf 93'>overall</span>
    <span class='ocrx_word' id='word_1_90' title='bbox 119 330 160 343; x_wconf 93'>effect:</span>
    <span class='ocrx_word' id='word_1_91' title='bbox 165 330 175 342; x_wconf 92'>Z</span>
    <span class='ocrx_word' id='word_1_92' title='bbox 180 334 188 340; x_wconf 87'>=</span>
    <span class='ocrx_word' id='word_1_93' title='bbox 193 330 222 343; x_wconf 87'>2.70</span>
    <span class='ocrx_word' id='word_1_94' title='bbox 227 330 242 345; x_wconf 91'>(P</span>
    <span class='ocrx_word' id='word_1_95' title='bbox 247 334 255 339; x_wconf 90'>=</span>
    <span class='ocrx_word' id='word_1_96' title='bbox 260 330 303 345; x_wconf 90'>0.007)</span>
   </span>
    */
	public Element extractTestForOverallEffect(int irow) {
		String[] fields = {
				"Test",
				"for",
				"overall",
				"effect:",
				"Z",
				"=",
				"R(?<Z>\\d+\\.\\d+)",
				"R(\\(P)",
				"R(?<op>=|<)",
				"R(?<p>(\\d+\\.\\d+)\\))",
		};
		
		Element element = createXMLFromRow("testForOverall", irow, fields, "Test for");
		return element;
		
	}

	/**
     <span class='ocr_line' id='line_1_53' title="bbox 834 339 995 354; baseline 0 -3; x_size 24.25; x_descenders 5.5; x_ascenders 6.25">
      <span class='ocrx_word' id='word_1_166' title='bbox 834 339 888 351; x_wconf 96'>Favours</span>
      <span class='ocrx_word' id='word_1_167' title='bbox 893 339 947 354; x_wconf 93'>[Pedicle</span>
      <span class='ocrx_word' id='word_1_168' title='bbox 952 339 995 354; x_wconf 91'>screw]</span>
     </span>
	 */
	private void getFavours() {
		
	}

}
