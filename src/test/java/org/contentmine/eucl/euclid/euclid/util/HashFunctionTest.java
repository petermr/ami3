package org.contentmine.eucl.euclid.euclid.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.HashFunction;
import org.junit.Test;

import junit.framework.Assert;

public class HashFunctionTest {
	private static final Logger LOG = Logger.getLogger(HashFunctionTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testGetHashSHA512() {
		String out = HashFunction.getSHA512Hash("in");
		Assert.assertEquals(""
				+ "e884a3a10f4c921d370c4b70f5af451e"
				+ "6e12ffad35c96f6e61221f9cf2efbabd"
				+ "d06db3edeb93bdd1182549d9d94bd86d"
				+ "baa4205ba9b26721524e9c420e58c834", out);
	}

	@Test
	public void testGetHashMD5() {
		String out = HashFunction.getMD5Hash("in");
		Assert.assertEquals("13b5bfe96f3e2fe411c9f66f4a582adf", out);
	}
}
