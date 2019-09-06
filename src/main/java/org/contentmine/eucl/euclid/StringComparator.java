package org.contentmine.eucl.euclid;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
	public int compare(String obj1, String obj2) {
	    return obj1.compareTo(obj2);
    }
}
