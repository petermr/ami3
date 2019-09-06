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
import org.contentmine.graphics.html.util.HtmlUtil;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlDiv extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlDiv.class);
	public final static String TAG = "div";
	public final static String ALL_DIV_XPATH = ".//h:div";

	/** constructor.
	 * 
	 */
	public HtmlDiv() {
		super(TAG);
	}

	/** makes a new list composed of the divs in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlDiv> extractDivs(List<HtmlElement> elements) {
		List<HtmlDiv> divList = new ArrayList<HtmlDiv>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlDiv) {
				divList.add((HtmlDiv) element);
			}
		}
		return divList;
	}

	/** convenience method to extract list of HtmlDiv in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlDiv> extractSelfAndDescendantDivs(HtmlElement htmlElement) {
		return HtmlDiv.extractDivs(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_DIV_XPATH));
	}

	/** convenience method to extract list of HtmlDiv in element
	 * 
	 * @param htmlElement
	 * @param xpath
	 * @return
	 */

	public static List<HtmlDiv> extractDivs(HtmlElement htmlElement, String xpath) {
		return HtmlDiv.extractDivs(HtmlUtil.getQueryHtmlElements(htmlElement, xpath));
	}


	public List<HtmlDiv> getDivChildList() {
		List<HtmlElement> childDivs = HtmlElement.getChildElements(this, HtmlDiv.TAG);
		List<HtmlDiv> divList = new ArrayList<HtmlDiv>();
		for (HtmlElement childDiv : childDivs) {
			divList.add((HtmlDiv)childDiv);
		}
		return divList;
	}

	public List<HtmlP> getPList() {
		List<HtmlElement> ps = HtmlElement.getChildElements(this, HtmlP.TAG);
		List<HtmlP> pList = new ArrayList<HtmlP>();
		for (HtmlElement p : ps) {
			pList.add((HtmlP)p);
		}
		return pList;
	}

	public static void debugListList(List<List<HtmlDiv>> divListList) {
		for (List<HtmlDiv> divList : divListList) {
			debugList(divList);
		}
	}

	public static void debugList(List<HtmlDiv> divList) {
		for (HtmlDiv div : divList) {
			LOG.debug(div.toXML());
		}
		
	}



}
