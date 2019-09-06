package org.contentmine.ami.plugins.phylotree;

import java.util.List;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;

/** dummy to allow compilation until Phylo stuff is rewritten
 * 
 * @author pm286
 *
 */
public class Word {

	public Real2 getBoundingBox() {
		throw new RuntimeException("getBoundingBox() NYI");
	}

	public static List<Real2Range> createBBoxList(List<Word> wordList) {
		throw new RuntimeException("createBBoxList() NYI");
	}

	public String getValue() {
		throw new RuntimeException("getValue NYI");
	}

	public Real2 getXY() {
		throw new RuntimeException("getXY() NYI");
	}

}
