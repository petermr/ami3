package org.contentmine.norma.sections;

import org.contentmine.graphics.html.HtmlElement;

public class FrontMatterExtractor implements HtmlElementExtractor {
	public HtmlElement getHtmlElement(JATSSectionTagger tagger) { 
		return tagger.getFrontMatter();
	}
}
