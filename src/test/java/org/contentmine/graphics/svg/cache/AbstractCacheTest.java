package org.contentmine.graphics.svg.cache;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMITest;

/** superclass of CacheTests, for communal software/constants.
 * 
 * @author pm286
 *
 */
public class AbstractCacheTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AbstractCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static File CACHE_TEST = new File(SRC_TEST_SVG, "cache");

	
}
