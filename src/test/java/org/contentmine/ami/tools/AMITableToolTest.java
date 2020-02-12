package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class AMITableToolTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMITableToolTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testSummaryTableOil() {
		File dir = OIL5;
//		File dir = OIL186;
//		new AMISectionTool().runCommands("-p " + dir +" --extract fig table supplementary");
		String args = ""
				+ "-p " + dir + ""
				+ " --summarytable __table/summary.html"
				+ " --tabledir sections/tables"
				
				+ " --templatefile "+CEV+"/templates/phytomedchem.xml"
				+ " --template composition"
				
			;
		new AMITableTool().runCommands(args);
	}

	@Test
	public void testColumnTypesOil() {
//		File dir = OIL5;
		File dir = OIL186;
//		File dir = OIL1000;
		String cmd = "-p " + dir +" --forcemake "
				+ " --extract fig table supplementary"
				+ " --summary table figure supplementary";
		new AMISectionTool().runCommands(cmd);
		String args = ""
				+ "-p " + dir + ""
				+ " --tabledir sections/tables"
				+ " --columntypes"
				+ " --templatefile "+CEV+"/templates/phytomedchem.xml"
//				+ " --templatefile "+CEV+"/templates/phytomedchem1.xml"  // uses chemical lookup
				+ " --template composition"
				+ " --multiset compound"
				
			;
		new AMITableTool().runCommands(args);
	}

	@Test
	public void testColspans() {
		File dir = OIL5;
//		File dir = OIL186;
//		File dir = OIL1000;
		String cmd = "-p " + dir +" --forcemake "
				+ " --extract fig table supplementary"
				+ " --summary table figure supplementary";
		new AMISectionTool().runCommands(cmd);
	}


	@Test
	public void testActivity() {
		File dir = OIL5;
//		File dir = OIL186;
//		File dir = OIL1000;
		String cmd = "-p " + dir +" --forcemake "
				+ " --extract fig table supplementary"
				+ " --summary table figure supplementary";
		new AMISectionTool().runCommands(cmd);
//		if (true) return;
		String args = ""
				+ "-p " + dir + ""
				+ " --tabledir sections/tables"
				+ " --columntypes"
				+ " --templatefile "+CEV+"/templates/phytomedchem.xml"
				+ " --template activity"
				+ " --multiset activity"
				+ " -vv"
				
			;
		new AMITableTool().runCommands(args);
	}



	@Test
	public void testSummaryTableClimate() {
//		System.out.println("DIR "+OIL186+" X "+OIL186.exists());
		CMIP200 = new File("/Users/pm286/workspace/projects/climate/searches/climatechange");
		String args = ""
				+ "-p " + CMIP200 + ""
				+ " --summarytable __table/summary.html"
				+ " --tabledir sections/tables"
				
//				+ " --templatefile "+CLIM_SEARCH+"/../templates/climate.xml"
//				+ " --templatefile "+"workspace/projects/climate/templates/climate.xml"
//				+ " --template composition"
			;
		new AMITableTool().runCommands(args);
	}

}
