package org.contentmine.cproject.util;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.PluginOption;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlElement.Target;
import org.contentmine.graphics.html.HtmlSpan;

import nu.xom.Attribute;

public class CellRenderer {

	private static final Logger LOG = Logger.getLogger(CellRenderer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String flag;
	private String value;
	private int characterCount;
	private boolean wordCount;
	private boolean visible;
	private String href0;
	private String href1;
	private int hrefWordCount;
	private String hrefJoinString;
	private PluginOption pluginOption;

	//@Deprecated // use CellRenderer(PluginOption pluginOption) if possible
	public CellRenderer(String flag) {
		this.flag = flag;
		setDefaults();
	}

	public CellRenderer(PluginOption pluginOption) {
		this.pluginOption = pluginOption;
		setDefaults();
	}

	private void setDefaults() {
		this.visible = true;
		hrefWordCount = 0;
		hrefJoinString = "";
	}

	public CellRenderer setBrief(int characterCount) {
		this.characterCount = characterCount;
		return this;
	}

	public CellRenderer setWordCount(boolean wordCount) {
		this.wordCount = wordCount;
		return this;
	}

	public CellRenderer setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public CellRenderer setHref0(String href0) {
		this.href0 = href0;
		return this;
	}

	public CellRenderer setHref1(String href1) {
		this.href1 = href1;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public String getValue() {
		return value;
	}

	public HtmlElement getHtmlElement() {
		HtmlElement element = new HtmlSpan();
		String aValue = value;
		if (characterCount != 0) {
			aValue = aValue.substring(0, Math.min(aValue.length(), characterCount));
			aValue += "...";
			element.addAttribute(new Attribute("title", value));
		}
		if (wordCount) {
			aValue = "["+aValue.length()+"]";
			element.addAttribute(new Attribute("title", value));
		}
		if (href0 != null || href1 != null) {
			element = createA(aValue);
		} else {
			element.appendChild(aValue);
		}
		return element;
	}

	private HtmlElement createA(String entityRef) {
		HtmlA a;
		a = new HtmlA();
		a.appendChild(entityRef);
		String href = createHref(entityRef);
		if (href != null) {
			a.setHref(href);
			a.setTarget(Target.separate);
		}
		return a;
	}

	private String createHref(String entityRef) {
		String href = null;
		if (href0 != null) {
			href = href0;
		};
		if (hrefWordCount >= 1) {
			String[] words = entityRef.split("\\s+");
			if (hrefWordCount == 1) {
				href += words[0];
			} else {
				href += words[0];
				for (int i = 1; i < Math.min(words.length, hrefWordCount); i++) {
					href += hrefJoinString;
					href += words[i];
				}
			}
		} else {
			href += entityRef;
		}
		if (href1 != null) {
			href += href1;
		}
		return href;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFlag() {
		return flag;
	}

	public void setWikipediaLink(int i) {
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(", f: "+ flag);
		sb.append(", v: "+ value);
		sb.append(", cc: "+ characterCount);
		sb.append(", w: "+ wordCount);
		sb.append(", v: "+ visible);
		sb.append(", h0: "+ href0);
		sb.append(", h1: "+ href1);
		return sb.toString();
	}

	public void setUseHrefWords(int hrefWords, String hrefJoin) {
		this.hrefWordCount = hrefWords;
		this.hrefJoinString = hrefJoin;
	}

	public String getHeading() {
		String heading = "?";
		if (pluginOption != null) {
			heading = pluginOption.getHeading(); 
		} else if (flag != null) {
			heading = flag;
		}
		return heading;
	}

	public void setFlag(String flag) {
		flag = flag.replace("search:", "dic:");
		this.flag = flag;
	}

}
