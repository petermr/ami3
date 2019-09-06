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

package org.contentmine.graphics.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Element;
import nu.xom.Node;

/** draws text.
 * 
 * NOTE: Text can be rotated and the additional fields manage some of the
 * metrics for this. Still very experimental
 * 
 * @author pm286
 *
 */
public class SVGTSpan extends SVGText {
	private static Logger LOG = Logger.getLogger(SVGTSpan.class);
	public final static String TAG = "tspan";
	
	
	/** constructor
	 */
	public SVGTSpan() {
		super(TAG);
		init();
	}
	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	
	
	/** constructor
	 */
	public SVGTSpan(SVGTSpan element) {
        super(element, TAG);
	}
	
	/** constructor
	 */
	public SVGTSpan(Element element) {
        super((SVGElement) element, TAG);
	}
	
    public SVGTSpan(Real2 real2, String string) {
    	super(real2, string, TAG);
	}
    
	/**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGTSpan(this);
    }


	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}


	/** makes a new list composed of the tSpans in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGTSpan> extractTSpans(List<SVGElement> elements) {
		List<SVGTSpan> tSpanList = new ArrayList<SVGTSpan>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGTSpan) {
				tSpanList.add((SVGTSpan) element);
			}
		}
		return tSpanList;
	}
	
	public void removeAttributes() {
		int natt = this.getAttributeCount();
		for (int i = 0; i < natt; i++) {
			this.getAttribute(0).detach();
		}
	}
}
