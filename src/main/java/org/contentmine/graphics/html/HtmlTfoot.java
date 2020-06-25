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
import org.contentmine.graphics.html.util.HtmlUtil;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTfoot extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = LogManager.getLogger(HtmlTfoot.class);
	public final static String TAG = "tfoot";

	/** constructor.
	 * 
	 */
	public HtmlTfoot() {
		super(TAG);
	}
	
	public List<HtmlTr> getRows() {
		List<HtmlTr> trList = new ArrayList<>();
		List<HtmlElement> rows = HtmlUtil.getQueryHtmlElements(this, "./*[local-name()='"+HtmlTr.TAG+"']");
		for (HtmlElement row : rows) {
			trList.add((HtmlTr) row);
		}
		return trList;
	}
	
}
