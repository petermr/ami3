package org.contentmine.norma.txt;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.IntArray;

/** holds TableOfContents.
 * 
 * @author pm286
 *
 */
public class Toc extends AbstractSection {

	private static final Logger LOG = LogManager.getLogger(Toc.class);
public Toc() {
		
	}
	
	public boolean checkToc() {
		IntArray lastSection = null;
		if (parentLineContainer != null) {
			for (int i = 1; i < parentLineContainer.size(); i++) {
				AnnotatedLine annotatedLine = parentLineContainer.get(i);
				LOG.trace(annotatedLine);
				IntArray section = annotatedLine.getLeftSection();
				if (lastSection == null) {
					lastSection = section;
				} else {
					if (!AnnotatedLine.checkArrayIncrement(annotatedLine.getLineNumber(), lastSection, section)) {
	//					LOG.warn("possible bad increment "+lastSection+" => "+section);
					}
				}
				lastSection = section;
			}
		}
		return true;
	}
	
}
