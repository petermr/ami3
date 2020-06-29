package org.contentmine.graphics.svg.cache;

import java.awt.image.BufferedImage;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITest;

/** superclass of CacheTests, for communal software/constants.
 * 
 * @author pm286
 *
 */
public class AbstractCacheTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AbstractCacheTest.class);
public final static File CACHE_TEST = new File(SRC_TEST_SVG, "cache");

	
}
