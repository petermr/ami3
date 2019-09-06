package org.contentmine.norma.sections;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.util.HtmlUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public abstract class SectionExtractor {
	private static final Logger LOG = Logger.getLogger(SectionExtractor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected CTree cTree;

	protected abstract HtmlDiv getSingleDiv(JATSSectionTagger tagger);
	protected String getControlledTitle(String title) {return null;}
	
	public HtmlDiv getSingleDivAndAddAsFile(JATSSectionTagger jatsSectionTagger, String sectionName) {
		HtmlDiv div = jatsSectionTagger.getSingleDiv(this);
		if (div != null) {
			cTree.writeFulltextHtmlToChildDirectory(div, sectionName);
		}
		return div;
	}
	
	public void extractAndOutputSections(HtmlDiv div, String dirName) {
//		List<HtmlElement> childSections = HtmlUtil.getQueryHtmlElements(div, "./*[local-name()='div' and @class]");
		List<HtmlElement> childSections = HtmlUtil.getQueryHtmlElements(div, "./*[local-name()='div']");
		File dir = new File(cTree.getDirectory(), dirName); 
		Multiset<String> classSet = HashMultiset.create();
		for (int section = 0; section < childSections.size(); section++) {
			HtmlElement childSection = childSections.get(section);
			String title = getControlledTitle(childSection) ;
			int count = 0;
			if (title == null) {
				title = childSection.getClassAttribute();
				title = title == null ? "section" : title;
				classSet.add(title);
				count = classSet.count(title); 
			}
			String filename = title + ((count <= 1) ? "" : count)+ "."+ CTree.HTML;
			File file = new File(dir, filename);
			try {
				XMLUtil.debug(childSection, file, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write section: " + file, e);
			}
		}
	}
	
	protected String getControlledTitle(HtmlElement childSection) {
		String title = XMLUtil.getSingleValue(childSection, "./*[local-name()='div' and @class='title']");
		String controlledTitle = getControlledTitle(title);
		return controlledTitle;
	}
	
	public abstract String getSection();

	public abstract String getDirectoryName();




}
