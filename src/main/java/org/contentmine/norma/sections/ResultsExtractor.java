package org.contentmine.norma.sections;

import java.util.List;

import org.contentmine.graphics.html.HtmlDiv;

public class ResultsExtractor implements DivListExtractor {
	public List<HtmlDiv> getDivList(JATSSectionTagger tagger) { 
		return tagger.getResults();
	}
}
