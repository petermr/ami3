package org.contentmine.eucl.euclid;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.EuclidTestFixtures;

public class EuclidFixtures {
	
	private static final Logger LOG = LogManager.getLogger(EuclidFixtures.class);
public final static File FILES_DIR = new File(EuclidTestFixtures.TEST_DIR, "files");
	public final static File TEST_PLOSONE_0115884_DIR = new File(EuclidFixtures.FILES_DIR, "journal.pone.0115884");
}
