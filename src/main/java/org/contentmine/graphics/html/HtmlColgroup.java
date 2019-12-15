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

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.html.util.HtmlUtil;


/** 
 *  @author pm286
 */
public class HtmlColgroup extends HtmlElement implements HasColspan {
	private final static Logger LOG = Logger.getLogger(HtmlColgroup.class);
	
	public final static String TAG = "colgroup";

	/** constructor.
	 * 
	 */
	public HtmlColgroup() {
		super(TAG);
	}
	
	@Override
	public int getColspan() {
		int colspan = 0;
		List<HtmlElement> colChildList = HtmlUtil.getQueryHtmlElements(this, "./*[local-name()='"+HtmlCol.TAG+"']");
		if (colChildList.size() == 0) {
			String colspanAttval = this.getAttributeValue(COLSPAN);
			colspan = colspanAttval == null ? 1 : Integer.parseInt(colspanAttval);
		} else {
			for (HtmlElement colChild : colChildList) {
				colspan += ((HtmlCol)colChild).getColspan();
			}
		}
		return colspan;
	}

	
}
