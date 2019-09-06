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

import nu.xom.Node;


/** 
 *  @author pm286
 */
public class HtmlSpan extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlSpan.class);
	public final static String TAG = "span";
	public final static String ALL_SPAN_XPATH = ".//h:span";

	/** constructor.
	 * 
	 */
	public HtmlSpan() {
		super(TAG);
	}
	
	/** makes a new list composed of the spans in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlSpan> extractSpans(List<HtmlElement> elements) {
		List<HtmlSpan> spanList = new ArrayList<HtmlSpan>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlSpan) {
				spanList.add((HtmlSpan) element);
			}
		}
		return spanList;
	}

	/** convenience method to extract list of HtmlSpan in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlSpan> extractSelfAndDescendantSpans(HtmlElement htmlElement) {
		return HtmlSpan.extractSpans(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_SPAN_XPATH));
	}

	/** convenience method to extract list of HtmlSpan in element
	 * 
	 * @param htmlElement
	 * @param xpath
	 * @return
	 */

	public static List<HtmlSpan> extractSpans(HtmlElement htmlElement, String xpath) {
		return HtmlSpan.extractSpans(HtmlUtil.getQueryHtmlElements(htmlElement, xpath));
	}

	public static HtmlSpan createSpanWithContent(String string) {
		HtmlSpan span = new HtmlSpan();
		span.appendChild(string);
		return span;
	}

	public static String toString(List<HtmlSpan> spanList) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < spanList.size(); i++) {
			HtmlSpan span = spanList.get(i);
//			sb.append(" ["+span.getXY().getX()+"]");
			sb.append(" | ");
			sb.append(span.getValue());
		}
		return sb.toString();
	}



}
