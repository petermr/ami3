package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

public class BackExtractor extends SectionExtractor {
	private static final Logger LOG = Logger.getLogger(BackExtractor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private BackExtractor() {
		super();
	}
	
	public BackExtractor(CTree cTree) {
		this();
		this.cTree = cTree;
	}
	

	public HtmlDiv getSingleDiv(JATSSectionTagger tagger) { 
		return tagger.getBack();
	}

	@Override
	public String getSection() {return CTree.BACK;}

	@Override
	public String getDirectoryName() {return CTree.BACK_DIR;}
	


	
}
