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


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlHead extends HtmlElement {
	
	private final static Logger LOG = Logger.getLogger(HtmlHead.class);
	public final static String TAG = "head";

	private HtmlStyle style;
	
	/** constructor.
	 * 
	 */
//	@Deprecated // use html.ensureBody()
	public HtmlHead() {
		super(TAG);
	}

	public HtmlStyle getOrCreateHtmlStyle() {
		if (style == null) {
			getStyle();
			style = new HtmlStyle();
			this.appendChild(style);
		}
		return style;
	}
	
	public HtmlStyle getStyle() {
		return (HtmlStyle) getSingleChildElement(this, HtmlStyle.TAG);
	}

	public List<HtmlMeta> getMetaElements() {
		List<HtmlMeta> metaElements = new ArrayList<HtmlMeta>();
		List<HtmlElement> htmlElements = getChildElements(this, HtmlMeta.TAG);
		for (HtmlElement htmlElement : htmlElements) {
			metaElements.add((HtmlMeta)htmlElement);
		}
		return metaElements;
	}

	public void addCSSStylesheetLink(String target) {
		HtmlLink link = new HtmlLink();
		link.setRel(STYLESHEET);
		link.setType(TEXT_CSS);
		link.setHref(target);
		this.appendChild(link);
	}

	public void addTitle(String string) {
		HtmlTitle title = new HtmlTitle(string);
		this.appendChild(title);
	}

	public HtmlStyle addCssStyle(String string) {
		HtmlStyle style = new HtmlStyle();
		this.appendChild(style);
		style.setCssTypeDefault();
		style.addCss(string);
		return style;
	}

	public HtmlScript addJavascriptLink(String src) {
		HtmlScript script = new HtmlScript();
		script.setSrc(src);
		script.setCharset(UTF_8);
		script.setType(TEXT_JAVASCRIPT);
		script.appendChild(" "); // bug in Chrome which doesnt like <script .../>
		this.appendChild(script);
		return script;
	}

	public void addScript(String content) {
		HtmlScript script = getOrCreateScript();
		// bug in Chrome which doesnt like <script .../>
		if (content.equals("")) content = " ";
		script.setContent(content);
		script.setCharset(UTF_8);
		script.setType(TEXT_JAVASCRIPT);
	}

	public HtmlScript getOrCreateScript() {
		HtmlScript script = getScript();
		if (script == null) {
			script = new HtmlScript();
			this.appendChild(script);
		}
		return script;
	}

	private HtmlScript getScript() {
		return (HtmlScript) getSingleChildElement(this, HtmlScript.TAG);
	}

	public void addUTF8Charset() {
		HtmlElement meta = new HtmlMeta();
		meta.setCharset(UTF_8);
		this.appendChild(meta);
	}


}
