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
public class SVGSymbol extends SVGElement {

	final public static String TAG ="symbol";

	/** constructor
	 */
	public SVGSymbol() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGSymbol(SVGElement element) {
		super(element);
	}
	
	/** constructor
	 */
	public SVGSymbol(Element element) {
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
        return new SVGSymbol(this);
    }


}
