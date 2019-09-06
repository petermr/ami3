package org.contentmine.eucl.euclid;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.EuclidTestFixtures;

public class EuclidFixtures {
	
	private static final Logger LOG = Logger.getLogger(EuclidFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static File FILES_DIR = new File(EuclidTestFixtures.TEST_DIR, "files");
	public final static File TEST_PLOSONE_0115884_DIR = new File(EuclidFixtures.FILES_DIR, "journal.pone.0115884");
}
