package org.contentmine.eucl.euclid.euclid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntegerMultisetList;
import org.junit.Test;

import junit.framework.Assert;

public class IntegerMultisetListTest {
	private static final Logger LOG = LogManager.getLogger(IntegerMultisetListTest.class);
@Test
	public void testBins() {
		IntArray intArray = new IntArray(new int[]{
				4, 8, 6, 23, 24, 12, 24, 25, 17, 15, 12, 7, 4, 5, 9,
				4, 5, 6
		});
		IntegerMultisetList intMultisetList = new IntegerMultisetList();
		intMultisetList.createMultisets(intArray, 3);
		Assert.assertEquals("bins", 
			"[[4 x 3, 5 x 2], [6 x 2, 7, 8], [9], [12 x 2], [17, 15], [], [23], [24 x 2, 25]]",
			intMultisetList.toString());
	}
	
	
}
