package org.contentmine.eucl.euclid.test;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TestUtil {
	private static final Logger LOG = LogManager.getLogger(TestUtil.class);
/** mainly used for tests with rough answers.
	 * returns true if abs(expected - actual) / expected < maxErrorRatio
	 * @param expected
	 * @param actual
	 * @param maxErrorRatio 
	 * @return
	 */
	public static boolean roughlyEqual(int expected, int actual, double maxErrorRatio) {
		return roughlyEqual((double)expected, (double)actual, maxErrorRatio);
	}

	/** mainly used for tests with rough answers.
	 * returns true if abs(expected - actual) / expected < maxErrorRatio
	 * @param expected
	 * @param actual
	 * @param maxErrorRatio 
	 * @return
	 */ 
	public static boolean roughlyEqual(double expected, double actual, double maxErrorRatio) {
		double diffRatio = Math.abs(expected - actual) / (double) expected;
		boolean roughlyEqual = diffRatio < maxErrorRatio;
		if (!roughlyEqual) {
			LOG.warn("not roughly equal: expected: "+expected+"; actual: "+actual); 
		}
		return roughlyEqual;
	}
	
	/** checks whether file exists and LOGs a warning
	 * 
	 * @param file
	 * @return
	 */
	public static boolean checkForeignDirExists(File file) {
		if (!file.exists()) {
			LOG.warn("Cannot find "+file+" Skipping");
			return false;
		}
		return true;
	}


}
