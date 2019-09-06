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


/** HTML p element 
 *  @author pm286
 *
 */
public class HtmlP extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlP.class);
	public final static String TAG = "p";
	public final static String ALL_P_XPATH = ".//h:p";

	/** constructor.
	 * 
	 */
	public HtmlP() {
		super(TAG);
	}

	public HtmlP(String content) {
		this();
		this.appendChild(content);
	}

	/** makes a new list composed of the ps in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlP> extractPs(List<HtmlElement> elements) {
		List<HtmlP> pList = new ArrayList<HtmlP>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlP) {
				pList.add((HtmlP) element);
			}
		}
		return pList;
	}


	/** convenience method to extract list of HtmlP in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlP> extractSelfAndDescendantPs(HtmlElement htmlElement) {
		List<HtmlP> ps = HtmlP.extractPs(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_P_XPATH));
		return ps;
	}

	public List<HtmlSpan> getSpanList() {
		List<HtmlElement> spans = HtmlElement.getChildElements(this, HtmlSpan.TAG);
		List<HtmlSpan> spanList = new ArrayList<HtmlSpan>();
		for (HtmlElement span : spans) {
			spanList.add((HtmlSpan)span);
		}
		return spanList;
	}

	public HtmlSpan getLastSpan() {
		List<HtmlSpan> spanList = getSpanList();
		return spanList.size() == 0 ? null : spanList.get(spanList.size() - 1);
	}

}
