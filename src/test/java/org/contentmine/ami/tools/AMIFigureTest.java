package org.contentmine.ami.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class AMIFigureTest extends AbstractAMITest {

	private static final Logger LOG = LogManager.getLogger(AMIFigureTest.class);

	@Test
	// NYI
	public void testCreate() {
		CTree tree = new CProject(TEST_BATTERY10).getCTreeByName("PMC3463005");
//		String cmd = "-p "+TEST_BATTERY10
		String cmd = "-t "+tree.getDirectory()
				+ " -v"
				+ " figure"
				+ " --panels grey,white border "
				+ " --gutter x,10,y,10"
				+ " --letters top.left.white.50.75"
				+ " --caption "
				;
		LOG.warn("fig "+cmd);
		AMI.execute(cmd);

	}
	
	
	
	

}
