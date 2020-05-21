package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.tools.AMISearchTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.junit.Test;

public class AMISearchToolTest {
	private static final Logger LOG = Logger.getLogger(AMISearchToolTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	static File TIGR2ESS = new File("/Users/pm286/workspace/Tigr2essDistrib/tigr2ess");
	private static final File DICTIONARY_EXAMPLES = new File(TIGR2ESS, "dictionaries/examples/");
	static File OSANCTUM200 = new File(TIGR2ESS, "osanctum200");
	static File OSANCTUM2000 = new File(TIGR2ESS, "scratch/ocimum2019027");

	@Test
	public void testZikaSearch2() {
		String project = "zika2";
//		String project = "zika1";
		File rawDir = new File(NAConstants.TEST_AMI_DIR, project);
		File targetDir = new File("target/cooccurrence/"+project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, targetDir);
		String args = 
				"-p "+targetDir
//				"-t "+new File(targetDir, "PMC2640145")
				+ " search"
				+ " --dictionaryTop /Users/pm286/ContentMine/dictionary/dictionaries"
				+ " --dictionary country "
				+ " --oldstyle"  // old style
//				+ " --ignorePlugins word"
//				+ " -v"
			;
		LOG.debug("args "+args);
//		new AMISearchTool().runCommands(args);
		AMI.execute(args);
	}
	

	@Test
	/** this effectively runs
	 * ami -p <targetDir> search --dictionary country
	 */
	public void testZikaCooccurrence0() {
		System.out.println(AMIFixtures.TEST_ZIKA10_DIR);
		File targetDir = new File("target/cooccurrence/zika10a");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		String args = 
				" -p "+targetDir
				+ " -v"
				+ " search"
//				+ " --no-oldstyle"
				+ " --dictionary "
				+ " country disease"
			;
		AMI.main(args);
	}
	
	@Test
	public void testZikaCooccurrence() {
		File targetDir = new File("target/cooccurrence/zika10");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		String args = 
				" -p /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10"
				+ " --dictionary species gene country disease funders "
			;
		new AMISearchTool().runCommands(args);
	}

	@Test
	public void testAMISearchNew() {
		File targetDir = new File("target/cooccurrence/ocimum");
		CMineTestFixtures.cleanAndCopyDir(OSANCTUM200, targetDir);
		String args = ""
				+ " -p "+targetDir 
				+ " --ignorePlugins word"
				+ " --dictionary "+TIGR2ESS.toString()+"/dictionaries/examples/monoterpenes"
				;
		new AMISearchTool().runCommands(args);
	}
	
	@Test
	public void testAMISearchLarge() {
//		File targetDir = new File("target/cooccurrence/ocimum");
//		CMineTestFixtures.cleanAndCopyDir(OSANCTUM2000, targetDir);
		File targetDir = OSANCTUM2000;
//		CMineTestFixtures.cleanAndCopyDir(OSANCTUM2000, targetDir);
		LOG.debug(OSANCTUM2000 + "; "+new CProject(targetDir).getOrCreateCTreeList().size());
		String args = ""
				+ " -p "+targetDir 
//				+ " --ignorePlugins word"
				+ " --dictionary "+TIGR2ESS.toString()+"/dictionaries/examples/monoterpenes"
				;
		new AMISearchTool().runCommands(args);
	}
	
	@Test
	/** serious performance problems on PMC3390897 - */
	public void testAMISearchNewSpecies() {
		File targetDir = new File("target/cooccurrence/species");
		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/scratch/centaurea"), targetDir);
		String args = /*ami-search-new*/""
				+ " -p "+targetDir 
				+ " --ignorePlugins word"
				+ " --dictionary species "
				;
		new AMISearchTool().runCommands(args);
	}

	@Test
	public void testAMISearchBug() {
//		File targetDir = new File("target/cooccurrence/ocimum");
//		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/osanctum200"), targetDir);
		File targetDir = OSANCTUM200;
//		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/osanctum200"), targetDir);
		String args = /*ami-search-new*/""
				+ " -p "+targetDir 
				+ " --ignorePlugins word"//				
				+ " --dictionary "+DICTIONARY_EXAMPLES+"/monoterpenes country species plantparts" 
				;
		new AMISearchTool().runCommands(args);
	}
	
	@Test
	public void testAMISearchSubCommands() {
		String args = " ami-words --help ";
		new AMISearchTool().runCommands(args);
	}
	
	@Test
	public void testAMIWordsCommands() {
		String args = " ami-words --help ";
		new AMIWordsTool().runCommands(args);
	}
	
	@Test
	public void testSubCommands() {
		String args = " test --dummyx value --write";
		new AMISearchTool().runCommands(args);
	}
	

}
