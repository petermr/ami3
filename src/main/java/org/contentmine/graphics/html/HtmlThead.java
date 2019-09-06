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


/** 
 <thead>
    <tr>
      <th>Month</th>
      <th>Savings</th>
    </tr>
  </thead>

 * @author pm286
 *
 */
public class HtmlThead extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlThead.class);
	public final static String TAG = "thead";

	/** constructor.
	 * 
	 */
	public HtmlThead() {
		super(TAG);
	}
	
    public List<HtmlTr> getChildTrs() {
        List<HtmlTr> rowList = new ArrayList<HtmlTr>();
        List<HtmlElement> rows = getChildElements(this, HtmlTr.TAG);
        for (HtmlElement el : rows) {
            rowList.add((HtmlTr) el);
        }
        return rowList;
    }

    /** gets first (usually only) Tr.
     * creates and adds if not present.
     * @return first row (created empty if absent)
     */
	public HtmlTr getOrCreateChildTr() {
		List<HtmlTr> rows = getChildTrs();
		if (rows.size() == 0) {
			HtmlTr row = new HtmlTr();
			this.appendChild(row);
			rows = getChildTrs();
		}
		return rows.get(0);
	}

}
