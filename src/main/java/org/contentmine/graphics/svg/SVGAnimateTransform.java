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

import org.contentmine.eucl.euclid.Real2;

import nu.xom.Element;
import nu.xom.Node;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGAnimateTransform extends AbstractAnimate {

	public final static String TAG ="animateTransform";
	private static final String ATTRIBUTE_TYPE = "attributeType";
	private static final String XML = "XML";
	private static final String TYPE = "type";
	private static final String ADDITIVE = "additive";

	/** constructor
	 */
	public SVGAnimateTransform() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGAnimateTransform(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGAnimateTransform(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGAnimateTransform(this);
    }

	public void setTransform(String type, Real2 from, Real2 to) {
		setAttribute(TRANSFORM, from, to);
    	setAttribute(ATTRIBUTE_TYPE, XML);
    	setAttribute(TYPE, type);
	}

     public void setAttribute(String name, Real2 from, Real2 to) {
		this.setAttributeName(name);
    	this.setFrom(from);
    	this.setTo(to);
	}

 	public void setFrom(Real2 from) {
		this.setFrom(String.valueOf(from.getX())+","+from.getY());
	}

	public void setTo(Real2 to) {
		this.setTo(String.valueOf(to.getX())+","+to.getY());
	}

	public void setAdditive(String value) {
		this.setAttribute(ADDITIVE, value);
	}
}