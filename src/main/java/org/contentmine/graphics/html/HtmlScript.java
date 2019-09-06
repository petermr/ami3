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
import nu.xom.Text;

public class HtmlScript  extends HtmlElement {

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlScript.class);
	public final static String TAG = "script";
	
	private static final String SRC = "src";
	public final static String ALL_SCRIPT_XPATH = ".//h:script";
	
	/** constructor.
	 * 
	 */
	public HtmlScript() {
		super(TAG);
	}

	public void setSrc(String src) {
		if (src != null) {
			this.addAttribute(new Attribute(SRC, src));
		}
	}
	
	public String getSrc() {
		return this.getAttributeValue(SRC);
	}
	
	@Override
	public HtmlElement setContent(String content) {
		addSplitLines(content);
		return this;
	}

	public void addSplitLines(String content) {
		String[] lines = content.split("\\n");
		for (String line : lines) {
			if (!line.trim().startsWith("//")) {
				this.appendChild(new Text(line));
			}
		}
	}

	/** makes a new list composed of the Scripts in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlScript> extractScripts(List<HtmlElement> elements) {
		List<HtmlScript> scriptList = new ArrayList<HtmlScript>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlScript) {
				scriptList.add((HtmlScript) element);
			}
		}
		return scriptList;
	}

	/** convenience method to extract list of HtmlScript in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlScript> extractSelfAndDescendantScripts(HtmlElement htmlElement) {
		return HtmlScript.extractScripts(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_SCRIPT_XPATH));
	}

	public static HtmlScript getFirstDescendantScript(HtmlElement htmlElement) {
		List<HtmlScript> scripts = extractSelfAndDescendantScripts(htmlElement);
		return (scripts.size() == 0) ? null : scripts.get(0);
	}
	

	
}
