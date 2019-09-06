package org.contentmine.eucl.euclid.euclid.testutil;

import static org.contentmine.eucl.euclid.EuclidConstants.S_RBRAK;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;

public class Real2TestUtil {
	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	
	public static void assertEquals(String msg, Real2 test, Real2 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test.getXY(), expected.getXY(),
				epsilon);
	}

}
