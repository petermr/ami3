package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.html.HtmlDiv;

public class BodyExtractor extends SectionExtractor {
	private static final Logger LOG = Logger.getLogger(BodyExtractor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private BodyExtractor() {
		super();
	}
	
	public BodyExtractor(CTree cTree) {
		this();
		this.cTree = cTree;
	}
	
	public HtmlDiv getSingleDiv(JATSSectionTagger tagger) { 
		return tagger.getBody();
	}
	
	protected String getControlledTitle(String title) {
		return title == null ? null : JATSSectionTagger.SECTION_BY_SYNONYM.get(title.toLowerCase());
	}

	@Override
	public String getSection() {return CTree.BODY;}

	@Override
	public String getDirectoryName() {return CTree.BODY_DIR;}
	

}
