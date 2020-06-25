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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;


/** 
 * @author pm286
 *
 <table border="1">
  <thead>
    <tr>
      <th>Month</th>
      <th>Savings</th>
    </tr>
  </thead>
  <tfoot>
    <tr>
      <td>Sum</td>
      <td>$180</td>
    </tr>
  </tfoot>
  <tbody>
    <tr>
      <td>January</td>
      <td>$100</td>
    </tr>
    <tr>
      <td>February</td>
      <td>$80</td>
    </tr>
  </tbody>
</table>
 */
public class HtmlTable extends HtmlElement {
	private final static Logger LOG = LogManager.getLogger(HtmlTable.class);
	public final static String TAG = "table";
	public final static String ALL_TABLE_XPATH = ".//h:table";

	/** constructor.
	 * 
	 */
	public HtmlTable() {
		super(TAG);
	}
	
	public List<HtmlTr> getRows() {
		HtmlTbody tbody = this.getTbody();
		List<HtmlTr> rowList = new ArrayList<HtmlTr>();
		List<HtmlElement> rows = (tbody != null) ? tbody.getRows() : getChildElements(this, HtmlTr.TAG);
		for (HtmlElement el : rows) {
			rowList.add((HtmlTr) el);
		}
		return rowList;
	}

	public HtmlTbody getTbody() {
		return (HtmlTbody) getSingleChildElement(this, HtmlTbody.TAG); 
	}

	public HtmlTfoot getTfoot() {
		return (HtmlTfoot) getSingleChildElement(this, HtmlTfoot.TAG); 
	}

	public HtmlThead getThead() {
		return (HtmlThead) getSingleChildElement(this, HtmlThead.TAG); 
	}

	/** at present order depends on order in calling code
	 * 
	 * @return
	 */
	public HtmlTable ensureHeadBodyFoot() {
		getOrCreateThead();
		getOrCreateTbody();
		getOrCreateTfoot();
		
		return this;
	}

	public HtmlThead getOrCreateThead() {
		HtmlThead thead = getThead(); 
		if (thead == null) {
			thead = new HtmlThead();
			HtmlTbody body = getTbody(); 
			int idx = body ==  null ? 0 : this.indexOf(body);
			this.insertChild(thead, idx);
		}
		return thead;
	}

	public HtmlTbody getOrCreateTbody() {
		HtmlTbody tbody = getTbody(); 
		if (tbody == null) {
			tbody = new HtmlTbody();
			this.appendChild(tbody);
		}
		return tbody;
	}

	public HtmlTfoot getOrCreateTfoot() {
		HtmlTfoot tfoot = getTfoot(); 
		if (tfoot == null) {
			tfoot = new HtmlTfoot();
			this.appendChild(tfoot);
		}
		return tfoot;
	}

	public HtmlTr getSingleLeadingTrThChild() {
		List<HtmlTr> rows = getRows();
		HtmlTr tr = null;
		if (rows.size() > 0) {
			// might be a <tbody>
			Nodes trthNodes = this.query(".//*[local-name()='tr' and *[local-name()='th']]");
			// some tables have more than one th row
			if (trthNodes.size() >= 1) {
//				Element elem = (Element) this.getChildElements().get(0);
//				if (elem.equals(trthNodes.get(0))) {
					tr = (HtmlTr) trthNodes.get(0);
//				}
			}	
		}
		return tr;
	}

	/** collect all rows which have only tr[tag], tag=either th or td 
	 * 
	 * can be used to separate a header using first=true
	 * does not yet cater for row headers (e.g. first cell in row is "th").
	 * 
	 * @param tag either "th" or "td"
	 * @return empty list of bad parameter or no suitable rows
	 */

	public List<HtmlTr> getFirstPureTagRows(String tag) {
		return getPureTagRows(tag, true);
	}
	
	/** collect all rows which have only tr[tag], tag=either th or td 
	 * 
	 * can be used to separate a header using first=true
	 * does not yet cater for row headers (e.g. first cell in row is "th").
	 * 
	 * @param tag either "th" or "td"
	 * @param first if true take only the first continuously true rows
	 * @return empty list of bad parameters or no suitable rows
	 * 
	 * */ 
	public List<HtmlTr> getPureTagRows(String tag, boolean first) {
		List<HtmlTr> trList = this.getRows();
		if (tag == null && !(tag.equals(HtmlTh.TAG)) && !(tag.equals(HtmlTd.TAG))) {
			return trList;
		}
		List<HtmlTr> tagOnlyList = new ArrayList<>();
		for (HtmlTr tr : trList) {
			if (tr.isPureRow(tag)) {
				tagOnlyList.add(tr);
			} else if (first) {
				break;
			}
		}
		return tagOnlyList;
	}

	public void setBorder(int i) {
		this.addAttribute(new Attribute("border", ""+i));
	}

	public void addRow(HtmlTr row) {
		this.appendChild(row);
	}

	/** convenience method to extract list of HtmlTable in element
	 * requires HTML namespace
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlTable> extractSelfAndDescendantTables(HtmlElement htmlElement) {
		return HtmlTable.extractTables(HtmlUtil.getQueryHtmlElements(htmlElement, ALL_TABLE_XPATH));
	}

	/** convenience method to extract list of HtmlTable in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlTable> extractSelfAndDescendantTablesIgnoreNamespaces(HtmlElement htmlElement) {
		List<Element> elements = XMLUtil.getQueryElements(htmlElement, "descendant-or-self::*[local-name()='"+HtmlTable.TAG+"']");
		return HtmlTable.extractTables(elements);
	}

	/** makes a new list composed of the tables in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlTable> extractTables(List<? extends Element> elements) {
		List<HtmlTable> tableList = new ArrayList<HtmlTable>();
		for (Element element : elements) {
			if (element instanceof HtmlTable) {
				tableList.add((HtmlTable) element);
			}
		}
		return tableList;
	}

	public static HtmlTable getFirstDescendantTable(HtmlElement htmlElement) {
		List<HtmlTable> tables = extractSelfAndDescendantTables(htmlElement);
		return (tables.size() == 0) ? null : tables.get(0);
	}

	/** convenience method to extract list of HtmlTables in element
	 * 
	 * @param htmlElement
	 * @param xpath
	 * @return
	 */

	public static List<HtmlTable> extractTables(HtmlElement htmlElement, String xpath) {
		return HtmlTable.extractTables(HtmlUtil.getQueryHtmlElements(htmlElement, xpath));
	}

	public static List<HtmlTable> extractTables(File tableFile) {
		HtmlElement htmlElement = HtmlElement.create(tableFile);
		return extractSelfAndDescendantTables(htmlElement);
	}

	public static List<HtmlTable> extractTablesIgnoreNamespace(File tableFile) {
		HtmlElement htmlElement = HtmlElement.create(tableFile);
		List<HtmlTable> tables = extractSelfAndDescendantTablesIgnoreNamespaces(htmlElement);
		return tables;
	}

	/** gets a row of header values either from Thead or first tr[th] row.
	 *  MESSY because there are two different approaches 
	 * @return
	 */
	@Deprecated // use ensureThead 
	public HtmlTr getHeaderRow() {
		HtmlTr tr = null;
		HtmlThead thead = this.getThead();
		if (thead != null) {
			tr = thead.getOrCreateChildTr();
		} else {
			tr = this.getSingleLeadingTrThChild();
		}
		return tr;
	}

	public String getCaptionValue() {
		List<Element> captions = XMLUtil.getQueryElements(this, "//*[local-name()='"+HtmlCaption.TAG+"']");
		Element element = captions.get(0);
//		LOG.debug("el "+element.toXML());
		String value = XMLUtil.getSpaceSeparatedChildValues(element);
//		LOG.debug("Cap "+value);
		return captions.size() == 0 ? null : value.replaceAll("\n", " ").replaceAll("\\s+", " ");
	}
	

	public HtmlCaption addCaption(String captionValue) {
		HtmlCaption caption = this.getOrCreateFirstCaption();
		caption.setContent(captionValue);
		return caption;
	}

	private HtmlCaption getOrCreateFirstCaption() {
		List<Element> captions = XMLUtil.getQueryElements(this, "//*[local-name()='"+HtmlCaption.TAG+"']");
		HtmlCaption caption = null;
		if (captions.size() == 0) {
			caption = new HtmlCaption();
			this.insertChild(caption, 0);
		} else {
			caption = (HtmlCaption) captions.get(0);
		}
		return caption;
	}


	public void normalizeWhitespace() {
		List<Node> textNodes = XMLUtil.getQueryNodes(this, ".//text()");
		for (Node textNode : textNodes) {
			ParentNode parentNode = textNode.getParent();
			String value = textNode.getValue();
			if (value != null) {
				value = Util.normalizeWhitespace(value);
				parentNode.replaceChild(textNode, new Text(value));
			}
		}
	}

	public void addFooterSummary() {
		HtmlTfoot tfoot = this.getOrCreateTfoot();
//		RectangularTable rectTab = RectangularTable.
//		List<HtmlTr> trList = tfoot.getTrList();
//		if (trList.size() == 0) {
//			this.getCo
//		}
		
	}

	public void tidy() {
		HtmlTrContainer thead = ensureThSeparateThead();
		int colcount = this.getColColgroupCount();
		thead.denormalizeSpans();
//		analyzeRowLabels();
	}

	public int getColColgroupCount() {
		List<HtmlElement> colColgroupList = HtmlUtil.getQueryHtmlElements(
				this, "./*[local-name()='"+HtmlCol.TAG+"' or local-name()='"+HtmlColgroup.TAG+"']");
		int colCount = 0;
		for (HtmlElement colx : colColgroupList) {
			if (colx instanceof HtmlCol) {
				colCount += ((HtmlCol)colx).getColspan();
			} else if (colx instanceof HtmlColgroup) {
				colCount += ((HtmlColgroup)colx).getColspan();
			}
		}
		return colCount;
	}

	/** ensures we have a thead (before tbody).
	 * moves any pure tr[th] rows into thead.
	 * thead may be empty 
	 * @return thead
	 * 
	 * 
	 */
	public HtmlTrContainer ensureThSeparateThead() {
		HtmlThead thead = this.getThead();
		HtmlTbody tbody = this.getTbody();
		if (thead == null) {
			thead = this.getOrCreateThead();
			// move ThRows => head
			List<HtmlTr> trList = this.getFirstPureTagRows(HtmlTh.TAG);
			if (trList.size() == 0) {
				addDefaultColumnHeadings(thead, tbody);
			} else {
				for (HtmlTr tr : trList) {
					tr.detach();
					thead.appendChild(tr);
				}
			}
		}
		return thead;
	}

	private void addDefaultColumnHeadings(HtmlThead thead, HtmlTbody tbody) {
		if (tbody != null) {
			List<HtmlTr> bodyTrs = tbody.getOrCreateChildTrs();
			if (bodyTrs.size() == 0) {
				LOG.warn("? empty table");
			} else {
				HtmlTr bodyTr0 = bodyTrs.get(0);
				int cols = bodyTr0.getTCellChildren().size();
				thead.addDefaultColumnHeaders(cols);
			}
		}
	}

	public boolean isTidy() {
		HtmlTbody tbody = getTbody();
		if (tbody == null) {
			LOG.error("tidy requires a <body>");
			return false;
		}
		HtmlTrContainer thead = getThead();
		if (thead == null) {
			LOG.error("tidy requires a <thead>");
			return false;
		} else if (thead.getChildTrs().size() == 0) {
			LOG.error("thead requires <tr>");
			return false;
		}
		return true;
	}

	public void ensureTidy() {
		if (!isTidy()) {
			tidy();
		}
	}

	/** gets rows in tidy-ed body
	 * 
	 * @return
	 */
	public List<HtmlTr> getOrCreateChildTrs() {
		return this.getOrCreateTbody().getOrCreateChildTrs();
	}

	public HtmlTable getDenormalizedHeader() {
		ensureTidy();
		HtmlThead thead = getThead();
		return thead == null ? null : thead.getDenormalizedTable();
	}


}
