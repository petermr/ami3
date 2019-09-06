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

import org.apache.log4j.Logger;

import nu.xom.Attribute;


/** mainly for embedding SVG
 * no checking - 
 * @author pm286
 *
 */
public class HtmlObject extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlObject.class);
	public final static String TAG = "object";

	private static final String SRC = "src";
	public static final String SVGTYPE = "image/svg+xml";
	private static final String HEIGHT = "height";
	static final String WIDTH = "width";

	/** constructor.
	 * 
	 */
	public HtmlObject() {
		super(TAG);
	}
	
	public void setSrc(String src) {
		this.addAttribute(new Attribute(SRC, src));
	}

	public String getSrc() {
		return this.getAttributeValue(SRC);
	}

	public void setHeight(double imgHeight) {
		this.addAttribute(new Attribute(HEIGHT, String.valueOf(imgHeight)));
	}

	public void setWidth(double imgWidth) {
		this.addAttribute(new Attribute(WIDTH, String.valueOf(imgWidth)));
	}


}
