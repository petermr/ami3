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


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlLink extends HtmlElement {
	private static final String REL = "rel";
	private static final String HREF = "href";
	private final static Logger LOG = Logger.getLogger(HtmlLink.class);
	public final static String TAG = "link";

	private HtmlStyle style;
	
	/** constructor.
	 * 
	 */
	public HtmlLink() {
		super(TAG);
	}

	public void setRel(String target) {
		this.addAttribute(new Attribute(REL, target));
	}

	public void setHref(String target) {
		this.addAttribute(new Attribute(HREF, target));
	}

}
