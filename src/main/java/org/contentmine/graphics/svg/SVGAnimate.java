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

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGAnimate extends AbstractAnimate {

	public final static String TAG ="animate";
	public static final String SUM = "sum";

	/** constructor
	 */
	public SVGAnimate() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGAnimate(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGAnimate(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGAnimate(this);
    }

    public void setOpacity(double from, double to) {
    	setAttribute(OPACITY, from, to);
    }
    public void setAttribute(String name, double from, double to) {
		this.setAttributeName(name);
    	this.setFrom(from);
    	this.setTo(to);
	}
	public void setFrom(Double from) {
		this.setFrom(String.valueOf(from));
	}

	public void setTo(Double to) {
		this.setTo(String.valueOf(to));
	}
}