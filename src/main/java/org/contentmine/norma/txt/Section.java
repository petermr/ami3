package org.contentmine.norma.txt;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

public class Section extends AbstractSection {

	private static final Logger LOG = LogManager.getLogger(Section.class);
private List<AnnotatedLine> lines;

	public Section(AnnotatedLineContainer lineContainer) {
		this.parentLineContainer = lineContainer;
	}

	public int getFirstLineNumber() {
		return localLineContainer.getFirstLineNumber();
	}

	public HtmlElement getOrCreateHtmlElement() {
		HtmlDiv htmlDiv = new HtmlDiv();
//		for (int i = 0; i < loc)
		return htmlDiv;
	}

}
