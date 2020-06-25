/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Attribute;
import nu.xom.Element;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTr extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = LogManager.getLogger(HtmlTr.class);
	public final static String TAG = "tr";
	public final static String ALL_TR_XPATH = ".//h:tr";

	/** constructor.
	 * 
	 */
	public HtmlTr() {
		super(TAG);
	}

	public HtmlTr(List<String> row) {
		this();
		for (String cell : row) {
			this.appendChild(new HtmlTd(cell));
		}
	}

	/** get th children specifically. Could occur in tbody as well as thead.
	 * will NOT necessarily map onto the column number
	 * 
	 * @return
	 */
	public List<HtmlTh> getThChildren() {
		List<HtmlElement> ths = HtmlElement.getChildElements(this, HtmlTh.TAG);
		List<HtmlTh> thList = new ArrayList<HtmlTh>();
		for (HtmlElement th : ths) {
			thList.add((HtmlTh) th);
		}
		return thList;
	}

	/** some people mix TD and TH in rows.
	 * gets all th or td children.
	 * this should map onto columns
	 * 
	 * @return
	 */
	public List<HtmlTCell> getTCellChildren() {
		List<HtmlTCell> children = new ArrayList<>();
		List<Element> elements = XMLUtil.getQueryElements(this, 
				"./*[local-name()='" + HtmlTd.TAG + "' or local-name()='" + HtmlTh.TAG + "']");
		for (Element element : elements) {
			children.add((HtmlTCell)element);
		}
		return children;
	}
	
	/** get td children specifically. 
	 * Shouldn't occur in thead but who knows.
	 * will NOT necessarily map onto the column number
	 * 
	 * @return
	 */

	public List<HtmlTd> getTdChildren() {
		List<HtmlElement> tds = HtmlElement.getChildElements(this, HtmlTd.TAG);
		List<HtmlTd> tdList = new ArrayList<HtmlTd>();
		for (HtmlElement td : tds) {
			tdList.add((HtmlTd) td);
		}
		return tdList;
	}
	
	/** get nth td.
	 * if there is a mixture of th and td ignores the th. 
	 * Be careful. If you want the nth column, use getTCell(n)
	 * 
	 * @param n
	 * @return
	 */
	public HtmlTd getTd(int n) {
		List<HtmlTd> cells = getTdChildren();
		HtmlTd htmlTd = cells.get(n);
		return (n < 0 || n >= cells.size()) ? null : (HtmlTd) htmlTd;
	}
	
	public HtmlTh getTh(int col) {
		List<HtmlTh> cells = getThChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTh) cells.get(col);
	}

	/** convenience method to extract list of HtmlTr in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlTr> extractSelfAndDescendantTrs(HtmlElement htmlElement) {
		return HtmlTr.extractTrs(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_TR_XPATH));
	}

	/** makes a new list composed of the trs in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlTr> extractTrs(List<HtmlElement> elements) {
		List<HtmlTr> trList = new ArrayList<HtmlTr>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlTr) {
				trList.add((HtmlTr) element);
			}
		}
		return trList;
	}
	
	public static HtmlTr getFirstDescendantTr(HtmlElement htmlElement) {
		List<HtmlTr> trs = extractSelfAndDescendantTrs(htmlElement);
		return (trs.size() == 0) ? null : trs.get(0);
	}

	public List<String> getThCellValues() {
		List<HtmlTh> thList = this.getThChildren();
		List<String> strings = new ArrayList<String>();
		for (HtmlTh th : thList) {
			strings.add(th.getValue());
		}
		return strings;
	}

	public List<String> getTdCellValues() {
		List<HtmlTd> tdList = this.getTdChildren();
		List<String> strings = new ArrayList<String>();
		for (HtmlTd td : tdList) {
			strings.add(td.getValue());
		}
		return strings;
	}

	boolean isPureRow(String tag) {
		boolean isPure = true;
		int childElementCount = getChildElements().size();
		for (int i = 0; i < childElementCount; i++) {
			Element child = getChildElements().get(i);
			if (!tag.equals(child.getLocalName())) {
				isPure = false;
				break;
			}
		}
		return isPure;
	}

	public List<String> getChildThTdValues() {
		List<String> stringValues = XMLUtil.getQueryValues(
				this, "./*[local-name()='"+HtmlTd.TAG+"' or local-name()='"+HtmlTh.TAG+"']");
		return stringValues;
	}

	public void addDefaultRowColspans() {
		List<HtmlTCell> cellList = this.getTCellChildren();
		for (HtmlTCell cell : cellList) {
			cell.addDefault(HasColspan.COLSPAN, "1");
			cell.addDefault(HtmlTCell.ROWSPAN, "1");
		}
	}

	public int getTotalColspan() {
		List<HtmlTCell> cellList = this.getTCellChildren();
		int totalColspan = 0;
		for (int icell = 0; icell < cellList.size(); icell++) {
			HtmlTCell cell = cellList.get(icell);
			int colspan = cell.getColspan();
			totalColspan += colspan;
		}
		return totalColspan;
	}
	
	public int getMaxColspan() {
		return getMaxspan(HasColspan.COLSPAN);
	}

	public int getMaxRowspan() {
		return getMaxspan(HtmlTCell.ROWSPAN);
	}

	private int getMaxspan(String spanName) {
		List<HtmlTCell> cellList = this.getTCellChildren();
		int maxspan = 1;
		for (HtmlTCell cell : cellList) {
			maxspan = cell.getMaxspan(spanName, maxspan);
		}
		return maxspan;
	}



}
