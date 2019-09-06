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
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlHtml extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlHtml.class);
	public final static String TAG = "html";

	private HtmlBody body;
	private HtmlStyle htmlStyle;
	private HtmlHead head;
	
	/** constructor.
	 * 
	 */
//	@Deprecated // because it doesn't require UTF-8; @see {createUTF8Html()}
	public HtmlHtml() {
		super(TAG);
	}

	/** creates a stub HTML element with HtmlHead and HtmlMetaCharset.
	 * 
	 * @return
	 */
	public static HtmlHtml createUTF8Html() {
		HtmlHtml html = new HtmlHtml();
		html.getOrCreateHead().setUTF8Charset("");
		return html;
	}
	

	public HtmlHead getOrCreateHead() {
		if (head == null) {
			head = getHead();
			if (head == null) {
				head = new HtmlHead();
				this.insertChild(head, 0);
			}
		}
		return head;
	}

	public HtmlBody getOrCreateBody() {
		if (body == null) {
			getBody();
			if (body == null) {
				body = new HtmlBody();
				this.appendChild(body);
			}
		}
		return body;
	}
	
	public void addCSS(String cssStyle) {
		getOrCreateHead();
		htmlStyle = head.getOrCreateHtmlStyle();
		htmlStyle.addCss(cssStyle);
	}

	public HtmlHead getHead() {
		return (HtmlHead) getSingleChildElement(this, HtmlHead.TAG);
	}

	public HtmlBody getBody() {
		return (HtmlBody) getSingleChildElement(this, HtmlBody.TAG);
	}
	
	public static void wrapAndWriteAsHtml(HtmlElement htmlElement, File file) {
		if (htmlElement == null) {
			LOG.error("Cannot write null element");
			return;
		} 
		if (htmlElement instanceof HtmlHtml) {
		} else {
			HtmlHtml html = HtmlHtml.createUTF8Html();
			html.getOrCreateBody().appendChild(htmlElement.copy());
		}
		try {
			XMLUtil.debug(htmlElement, file, 1);
		} catch (IOException e) {
			LOG.error("cannot write html ",  e);
		}
		
	}

	public static void wrapAndWriteAsHtml(List<HtmlElement> htmlElementList, File dir) {
		if (htmlElementList == null) {
			LOG.debug("null element list");
		} else if (dir == null) {
			LOG.debug("null dir");
		} else if (!dir.isDirectory()) {
			LOG.debug("requires directory");
		} else {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			int doc = 0;
			for (HtmlElement htmlElement : htmlElementList) {
				doc++;
				if (htmlElement != null) {
					String title = htmlElement.getTitle();
					if (title == null) {
						title = "html"+doc;
					}
					File file = new File(dir, title+".html");
					LOG.debug("wrote: "+file);
					HtmlHtml.wrapAndWriteAsHtml(htmlElement, file);
				} else {
					LOG.debug("null HTML output");
				}
			}
		}
	}
}
