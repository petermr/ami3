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
public class HtmlBody extends HtmlElement {

	private final static Logger LOG = Logger.getLogger(HtmlBody.class);
	public final static String TAG = "body";
	public final static String ALL_BODY_XPATH = ".//h:body";
	
	/** constructor.
	 * 
	 * 
	 */
//	@Deprecated // use html.ensureBody
	public HtmlBody() {
		super(TAG);
	}

	/** convenience method to extract list of HtmlBody in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlBody> extractSelfAndDescendantBodys(HtmlElement htmlElement) {
		return HtmlBody.extractBodys(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_BODY_XPATH));
	}

	/** makes a new list composed of the Bodys in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlBody> extractBodys(List<HtmlElement> elements) {
		List<HtmlBody> BodyList = new ArrayList<HtmlBody>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlBody) {
				BodyList.add((HtmlBody) element);
			}
		}
		return BodyList;
	}

	public static HtmlBody getFirstDescendantBody(HtmlElement htmlElement) {
		List<HtmlBody> bodys = extractSelfAndDescendantBodys(htmlElement);
		return (bodys.size() == 0) ? null : bodys.get(0);
	}

	public List<HtmlDiv> getDivList() {
		List<HtmlElement> elements = HtmlElement.getChildElements(this, HtmlDiv.TAG);
		List<HtmlDiv> divList = new ArrayList<HtmlDiv>();
		for (HtmlElement element : elements) {
			divList.add((HtmlDiv) element);
		}
		return divList;
	}

}
