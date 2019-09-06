package org.contentmine.norma.sections;

import java.util.List;

import org.contentmine.graphics.html.HtmlDiv;

public class AbbreviationExtractor implements DivListExtractor {
	public List<HtmlDiv> getDivList(JATSSectionTagger tagger) { 
		return tagger.getAbbreviations();
	}
	public HtmlDiv getSingleDiv(JATSSectionTagger tagger) { 
		return null;
	}
}
