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


/** 
 *  @author pm286
 */
public class HtmlI extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlSpan.class);
	public final static String TAG = "i";
	public final static String ALL_I_XPATH = ".//h:i";

	/** constructor.
	 * 
	 */
	public HtmlI() {
		super(TAG);
	}

	/** convenience method to extract list of HtmlI in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlI> extractSelfAndDescendantIs(HtmlElement htmlElement) {
		return HtmlI.extractIs(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_I_XPATH));
	}

	/** makes a new list composed of the is in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlI> extractIs(List<HtmlElement> elements) {
		List<HtmlI> iList = new ArrayList<HtmlI>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlI) {
				iList.add((HtmlI) element);
			}
		}
		return iList;
	}
}
