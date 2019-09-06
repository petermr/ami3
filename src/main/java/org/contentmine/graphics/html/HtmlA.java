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
public class HtmlA extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlA.class);
	public final static String TAG = "a";
	public final static String ALL_A_XPATH = ".//h:a";

	private static final String HREF = "href";
	private static final String TARGET = "target";
	
	/** constructor.
	 * 
	 */
	public HtmlA() {
		super(TAG);
	}
	
	public static HtmlA createFromHrefAndContent(String href, String value) {
		HtmlA a = new HtmlA();
		a.setHref(href);
		a.setValue(value);
		return a;
	}
	
	public void setHref(String href) {
		this.setAttribute(HREF, href);
	}
	
	public String getTarget() {
		return this.getAttributeValue(TARGET);
	}

	public void setTarget(Target target) {
		this.setTarget(target.toString());
	}

	public void setTarget(String s) {
		this.setAttribute(TARGET, s);
	}

	public String getHref() {
		return this.getAttributeValue(HREF);
	}
	
	/** convenience method to extract list of HtmlA in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlA> extractSelfAndDescendantAs(HtmlElement htmlElement) {
		return HtmlA.extractAs(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_A_XPATH));
	}

	/** makes a new list composed of the A's in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlA> extractAs(List<HtmlElement> elements) {
		List<HtmlA> aList = new ArrayList<HtmlA>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlA) {
				aList.add((HtmlA) element);
			}
		}
		return aList;
	}

	public static String getDescendantHref(HtmlElement htmlElement) {
		List<HtmlA> aList = HtmlA.extractSelfAndDescendantAs(htmlElement);
		return aList.size() == 1 ? aList.get(0).getHref() : null;
	}
}
