package org.contentmine.ami;

import java.io.File;
import java.io.IOException;

import org.contentmine.norma.NAConstants;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LargeIT {

	@Test
		public void testZika10() throws IOException {
			LargeTestsIT.runBioscienceDefault("zika10", new File(NAConstants.TEST_AMI_DIR+"/zika10/"));
		}

}
