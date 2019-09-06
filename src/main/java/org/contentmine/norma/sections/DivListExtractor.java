package org.contentmine.norma.sections;

import java.util.List;

import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.html.HtmlDiv;

/** defines an abstractor of divs
 * 
 * @author pm286
 *
 */
public interface DivListExtractor {
	public List<HtmlDiv> getDivList(JATSSectionTagger tagger);
}
	
