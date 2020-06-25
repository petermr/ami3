package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.html.HtmlDiv;

public class BodyExtractor extends SectionExtractor {
	private static final Logger LOG = LogManager.getLogger(BodyExtractor.class);
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
