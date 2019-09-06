package org.contentmine.norma.sections;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.util.HtmlUtil;

/** defines an abstractor of a single div
 * normally required to be exactly one (or possibly none)
 * examples are, <front> <body> <back> which should never be more than one
 * @author pm286
 *
 */
public interface SingleDivExtractor {
	public HtmlDiv getSingleDiv(JATSSectionTagger tagger);

//	public HtmlDiv getSingleDivAndAddAsFile(JATSSectionTagger jatsSectionTagger, String sectionName);

	void extractAndOutput(HtmlDiv body, String dirName);

}
	
