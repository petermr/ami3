package org.contentmine.svg2xml.cmucl;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class CMUCLIT {
	private static final Logger LOG = Logger.getLogger(CMUCLIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void Nature_SciRep_1() {
		String root = "Nature_SciRep_1";
		CMUCLTest.markupAndOutputTables(root);
	}

	@Test
	public void testAllTables() {
		File[] dirs = CMUCLTest.CMUCL0.listFiles();
		for (File dir : dirs) {
			try {
				CMUCLTest.extractTables(dir.getName());
			} catch (NullPointerException e) {
				LOG.error(dir+" NPE: "+e.getMessage());
			}
		}
	}

	@Test
	public void testBMCHTML() {
		String root = "BMC_Medicine";
		CMUCLTest.createHTML(root);
	}

	@Test
	public void testBMCMarkup() {
		String root = "BMC_Medicine";
		CMUCLTest.markupAndOutputTables(root);
	}

	@Test
	public void testInformaRotated() {
		String root = "Informa_ExpOpinInvestDrugsRot";
		CMUCLTest.markupAndOutputTables(root);
	}

	@Test
	/** need to hack the boxes */
	public void testLancet() {
		String root = "TheLancet_1";
		CMUCLTest.markupAndOutputTables(root);
	}

}
