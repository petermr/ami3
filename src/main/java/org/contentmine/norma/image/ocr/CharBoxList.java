package org.contentmine.norma.image.ocr;

import java.util.Collection;

import org.contentmine.eucl.euclid.Int2;
import org.contentmine.graphics.svg.cache.GenericAbstractList;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class CharBoxList extends GenericAbstractList<CharBox> {

	public CharBoxList() {
		
	}
	
	public CharBoxList(Collection<CharBox> collection) {
		this();
		for (CharBox charBox : collection) {
			this.add(charBox);
		}
	}

	@Override 
	/** ensures sorted by lowest x.
	 * not optimized (could use binary chop or sorted list)
	 * 
	 */
	public boolean add(CharBox charBox) {
		if (charBox == null) return false;
		ensureGenericList();
		int xmin = charBox.getBoundingBox().getXRange().getMin();
		
		for (int i = 0; i < genericList.size(); i++) {
			CharBox charBox1 = genericList.get(i);
			if (charBox1.getXMin() > xmin) {
				this.add(i, charBox);
				return true;
			}
		}
		return super.add(charBox);
	}
	
	@Override
	public String toString() {
//		StringBuilder sb = new StringBuilder();
		return genericList.toString();
	}

	public Multiset<Int2> getBBoxSizeMultiset() {
		Multiset<Int2> bboxSizeSet = HashMultiset.create();
		for (CharBox charBox : this) {
			bboxSizeSet.add(charBox.getBoundingBoxSize());
		}
		return bboxSizeSet;
	}
}
