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

import nu.xom.Attribute;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTd extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlTd.class);
	public final static String TAG = "td";
	public final static String ALL_TD_XPATH = ".//h:td";

	/** constructor.
	 * 
	 */
	public HtmlTd() {
		super(TAG);
	}
	/**
	 * create a Td with the included text
	 * @param content
	 * @return
	 */
	public static HtmlElement createAndWrapText(String content) {
		HtmlElement td = new HtmlTd();
		td.appendChild(content);
		return td;
	}


	/** convenience method to extract list of HtmlTd in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlTd> extractSelfAndDescendantTds(HtmlElement htmlElement) {
		return HtmlTd.extractTds(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_TD_XPATH));
	}

	/** makes a new list composed of the tds in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlTd> extractTds(List<HtmlElement> elements) {
		List<HtmlTd> tdList = new ArrayList<HtmlTd>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlTd) {
				tdList.add((HtmlTd) element);
			}
		}
		return tdList;
	}
	
	public static HtmlElement getFirstDescendantTd(HtmlElement htmlElement) {
		List<HtmlTd> tds = extractSelfAndDescendantTds(htmlElement);
		return (tds.size() == 0) ? null : tds.get(0);
	}
	
	public void setWidth(double imgWidth) {
		this.setWidth(String.valueOf(imgWidth));
	}
	public void setWidth(String string) {
		this.addAttribute(new Attribute(HtmlImg.WIDTH, string));
	}


	
}
