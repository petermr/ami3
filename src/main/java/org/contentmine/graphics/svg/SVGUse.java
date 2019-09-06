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

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/** supports use/symbol
  <defs>
    <symbol id="MySymbol" viewBox="0 0 20 20">
      <desc>MySymbol - four rectangles in a grid</desc>
      <rect x="1" y="1" width="8" height="8"/>
      <rect x="11" y="1" width="8" height="8"/>
      <rect x="1" y="11" width="8" height="8"/>
      <rect x="11" y="11" width="8" height="8"/>
    </symbol>
  </defs>
 * 
	<use x="45" y="10" width="10" height="10" xlink:href="#MySymbol" />

 *  * @author pm286
 *
 */
public class SVGUse extends SVGElement {

	final public static String TAG ="use";
	private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	private static final String HREF = "href";
	private static final String XLINK = "xlink";

	/** constructor
	 */
	public SVGUse() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGUse(SVGElement element) {
		super(element);
	}
	
	public SVGUse(Real2 orig, SVGSymbol symbol) {
		this();
		setXyAndSymbol(orig, symbol);
	}

	public void setXyAndSymbol(Real2 orig, SVGSymbol symbol) {
		setXY(orig);
		String id = symbol.getId();
		if (id != null) {
			this.addNamespaceDeclaration(XLINK, XLINK_NS);
			this.addAttribute(new Attribute(XLINK+":"+HREF, XLINK_NS, "#"+id));
		}
	}

	/** constructor
	 */
	public SVGUse(Element element) {
        super((SVGElement) element);
	}
	
	protected void init() {
	}

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGUse(this);
    }


    public void setSymbol(SVGSymbol symbol) {
    	
    }
    
    /** return null
     * 
     */
	public Real2Range getBoundingBox() {
		return null;
	}
	
	/** makes a new list composed of the use elements in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGUse> extractUses(List<SVGElement> elements) {
		List<SVGUse> useList = new ArrayList<SVGUse>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGUse) {
				useList.add((SVGUse) element);
			}
		}
		return useList;
	}
	


}
