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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Attribute;


/** 
 * @author pm286
 *
 */
public class HtmlImg extends HtmlElement {

	private final static Logger LOG = LogManager.getLogger(HtmlImg.class);
	public final static String TAG = "img";
	
	private static final String ALT = "alt";
	private static final String HEIGHT = "height";
	private static final String SRC = "src";
	static final String WIDTH = "width";

	/** constructor.
	 * 
	 */
	public HtmlImg() {
		super(TAG);
	}

	public HtmlImg setAlt(String src) {
		this.addAttribute(new Attribute(ALT, src));
		return this;

	}

	public String getAlt() {
		return this.getAttributeValue(ALT);
	}
	
	public HtmlImg setSrc(String src) {
		this.addAttribute(new Attribute(SRC, src));
		return this;
	}

	public String getSrc() {
		return this.getAttributeValue(SRC);
	}

	public HtmlImg setHeight(double imgHeight) {
		this.addAttribute(new Attribute(HEIGHT, String.valueOf(imgHeight)));
		return this;
	}

	public HtmlImg setWidth(double imgWidth) {
		this.addAttribute(new Attribute(WIDTH, String.valueOf(imgWidth)));
		return this;
	}

}
