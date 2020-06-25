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

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;


/** 
 <thead>
    <tr>
      <th>Month</th>
      <th>Savings</th>
    </tr>
  </thead>

NOTE: <thead> can be empty.
otherwise contains one or more <tr>
we discourage multiple <tr> and try to denormalize header structure
In general only the first <tr> will be used

 * @author pm286
 *
 */
public class HtmlThead extends HtmlTrContainer {
	@SuppressWarnings("unused")
	public final static Logger LOG = LogManager.getLogger(HtmlThead.class);
	public final static String TAG = "thead";
	private static final String COL = "col";
	
	private HtmlTr tr;

	/** constructor.
	 * 
	 */
	public HtmlThead() {
		super(TAG);
	}
	
	protected List<String> allowedChildren() {
		return Arrays.asList(new String[] {HtmlTr.TAG});
	}
	
    /** gets first (and often only) Tr.
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

//	public HtmlTr getOrCreateTr() {
//		List<Element> trList = XMLUtil.getQueryElements(this, "./*[local-name()='"+HtmlTr.TAG+"']");
//		tr = null;
//		if (trList.size() == 0) {
//			tr = new HtmlTr();
//			this.appendChild(tr);
//		} else {
//			tr = (HtmlTr) trList.get(0);
//		}
//		return tr;
//	}

	public HtmlTrContainer addHeader(List<String> header) {
		HtmlTr tr = getOrCreateChildTr();
		for (String h : header) {
			tr.appendChild(new HtmlTh(h));
		}
		return this;
	}

	public List<String> getColumnLabels() {
		return (tr == null) ? null : tr.getChildThTdValues();
	}

	/** adds Tr with Th columns with default headers
	 * 
	 * @param cols
	 */
	public void addDefaultColumnHeaders(int cols) {
		this.appendChild(HtmlThead.createDefaultColumnHeaders(cols));
	}

	/** create default headers
	 * 
	 * @param cols
	 * @return
	 */
	public static HtmlTr createDefaultColumnHeaders(int cols) {
		HtmlTr tr = new HtmlTr();
		for (int i = 0; i < cols; i++) {
			HtmlTh th = new HtmlTh(COL+i);
			tr.appendChild(th);
		}
		return tr;
	}

}
