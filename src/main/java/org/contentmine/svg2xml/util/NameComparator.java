package org.contentmine.svg2xml.util;

import java.util.Comparator;
import java.util.List;


public class NameComparator implements Comparator<Object> {

	/** compare by tokens.
	 * split name into integers and strings
	 * Integer has precedence over String, else lexical order
	 * after than shortest number of tokens takes precedence.
	 */
	public int compare(Object o0, Object o1) {
		List<Object> tokens0 = TextFlattener.splitAtIntegers(o0.toString());
		List<Object> tokens1 = TextFlattener.splitAtIntegers(o1.toString());
		for (int i = 0; i < tokens0.size() && i < tokens1.size(); i++) {
			int compare = 0;
			Object token0 = tokens0.get(i);
			Object token1 = tokens1.get(i);
			if (token0 instanceof String) {
				if (token1 instanceof String) {
					compare = ((String) token0).compareTo((String)token1);
				} else {
					compare = -1;
				}
			} else {
				if (token1 instanceof Integer) {
					compare = ((Integer) token0).compareTo((Integer)token1);
				} else {
					compare = 1;
				}
			}
			if (compare != 0) {
				return compare;
			}
		}
		return (tokens0.size() - tokens1.size());
	}

}
