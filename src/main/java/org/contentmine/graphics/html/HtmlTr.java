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

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTr extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlTr.class);
	public final static String TAG = "tr";
	public final static String ALL_TR_XPATH = ".//h:tr";

	/** constructor.
	 * 
	 */
	public HtmlTr() {
		super(TAG);
	}

	public List<HtmlTh> getThChildren() {
		List<HtmlElement> ths = HtmlElement.getChildElements(this, HtmlTh.TAG);
		List<HtmlTh> thList = new ArrayList<HtmlTh>();
		for (HtmlElement th : ths) {
			thList.add((HtmlTh) th);
		}
		return thList;
	}

	/** some people mix TD and TH in rows.
	 * 
	 * @return
	 */
	public List<HtmlElement> getTdOrThChildren() {
		List<HtmlElement> children = new ArrayList<HtmlElement>();
		List<Element> elements = XMLUtil.getQueryElements(this, 
				"./*[local-name()='" + HtmlTd.TAG + "' or local-name()='" + HtmlTh.TAG + "']");
		for (Element element : elements) {
			children.add((HtmlElement)element);
		}
		return children;
	}
	
	public List<HtmlTd> getTdChildren() {
		List<HtmlElement> tds = HtmlElement.getChildElements(this, HtmlTd.TAG);
		List<HtmlTd> tdList = new ArrayList<HtmlTd>();
		for (HtmlElement td : tds) {
			tdList.add((HtmlTd) td);
		}
		return tdList;
	}
	
	public HtmlTd getTd(int col) {
		List<HtmlTd> cells = getTdChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTd) cells.get(col);
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

}
