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
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Elements;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTbody extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlTbody.class);
	public final static String TAG = "tbody";
	private HtmlTr headerRow;
	private List<HtmlTr> rowList;
	
	/** constructor.
	 * 
	 */
	public HtmlTbody() {
		super(TAG);
	}
	
    public void addRow(HtmlTr row) {
		this.appendChild(row);
	}
	
    @Deprecated // use getChildTrs
	public List<HtmlElement> getRows() {
		return getChildElements(this, HtmlTr.TAG);
	}
     
	/** also extracts HeaderTr
	 * 
	 * @return
	 */
    public List<HtmlTr> getOrCreateChildTrs() {
    	if (rowList == null) {
	        rowList = new ArrayList<HtmlTr>();
	        List<HtmlElement> rows = getChildElements(this, HtmlTr.TAG);
	        for (HtmlElement el : rows) {
	            HtmlTr row = (HtmlTr) el;
				rowList.add(row);
				// onlt get first Header
	            if (row.getChildCount() > 0 && headerRow == null) {
	            	if (XMLUtil.getQueryElements(row, "./*[local-name()='"+HtmlTh.TAG+"']").size() > 0) {
	            		headerRow = row;
	            	}
	            }
	        }
    	}
        return rowList;
    }
    
    public List<HtmlElement> getChildElementsList() {
        Elements elts = this.getChildElements();
        
        List<HtmlElement> elements = new ArrayList<HtmlElement>();
        for (int i = 0; i < elts.size(); i++) {
            elements.add((HtmlElement) elts.get(i));
        }
        return elements;
    }
    
    public HtmlTr getHeaderRow() {
    	getOrCreateChildTrs();
    	return headerRow;
    }

    /** gets cells in tr/th
     * 
     * @return
     */
    public List<HtmlTh> getHeaderCells() {
    	getOrCreateChildTrs();
    	List<HtmlTh> cellList = new ArrayList<HtmlTh>();
    	if (headerRow != null) {
    		cellList = headerRow.getThChildren();
    	}
    	return cellList;
    }
    
    /** gets index of column by th value.
     * 
     * @param nameRegex
     * @return -1 if not found
     */
    public int getColumnIndex(String nameRegex) {
    	getOrCreateChildTrs();
    	if (headerRow != null) {
    		List<String> thCellValues = headerRow.getThCellValues();
			for (int i = 0; i < thCellValues.size(); i++) {
    			String colName = thCellValues.get(i).trim();
				if (colName.matches(nameRegex)) {
    				return i;
    			}
    		}
    	}
    	return -1;
    }

	public List<HtmlTr> getRowList() {
		return rowList;
	}

	
}
