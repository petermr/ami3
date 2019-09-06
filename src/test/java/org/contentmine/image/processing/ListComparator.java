package org.contentmine.image.processing;

import java.util.Comparator;
import java.util.List;

public class ListComparator implements Comparator<List<Integer>> {

	public int compare(List<Integer> l0, List<Integer> l1) {
		return (l0 == null || l1 == null || l0.size() == 0 || l1.size() == 0) ? 0 : l0.get(0) - l1.get(0);
	}

}
