/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contentmine.pdf2svg;

import java.io.File;

import org.contentmine.pdf2svg.PDF2SVGConverter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** Not really tests.
 * Run over a large number of different PDFs to gather information and
 * a vague hope of catching regressions
 * Run manually
 * 
 * @author pm286
 *
 */

public class SemiTestIT {

	@Test
	@Ignore
	public void main() {
		new SemiTestIT().testPaperCollection();
	}
	/* not a JUNIT test
	 * 
	 */
	@Test
	@Ignore
	public void testPaperCollection() {
		testAJC();
		testAJCMany();
		testBMC();
		testE();
		testRSC();
		testRSC1();
		testRSCMany();
		testPsyc();
		testAPA();
		testSocDir();
		testACS();
		testNPG();
		testWiley();
		testBMJ();
		testElife();
		testJB();
		testPlosOne();
		testPlosOne1();
		testElsevier2();
		testWord();
		testWordMath();
		testThesis();
		testThesis1();
		testThesis2();
		testThesis5();
		testThesisMany();
		testArxivMany();
		testECU();
	}
	
	@Test
	@Ignore
	public void testAJC() {
		for (int pageNum = 1; pageNum <= 131; pageNum++) {
			File pageNFile = new File("target/ajc/xx-page" + pageNum + ".svg");
			if (pageNFile.exists()) {
				pageNFile.delete();
			}
		}

		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/ajc", "-pages", "1-131",
				"-pub", "ajc", "../pdfs/ajctest/xx.pdf");

		for (int pageNum = 1; pageNum <= 131; pageNum++) {
			File pageNFile = new File("target/ajc/xx-page" + pageNum + ".svg");
			Assert.assertTrue(pageNFile.exists());
		}
	}

	
	@Test
	@Ignore
	public void testAJCMany() {
		convertPDFsToSVG("../pdfs/ajc/many", "target/ajc/many");
	}
	
	public static void convertPDFsToSVG(String pdfDirName, String outdir) {
		File pdfDir = new File(pdfDirName);
		File[] files = pdfDir.listFiles();
		if (files != null) {
			for (File file : files){
				if (file.toString().endsWith(".pdf")) {
					PDF2SVGConverter converter = new PDF2SVGConverter();
					converter.run("-outdir", outdir, file.toString());
				}
			}
		}
	}
	
	
	 // do not normally run this
	@Test
	@Ignore
	public void testBMC() {
		for (int pageNum = 1; pageNum <= 14; pageNum++) {
			File pageNFile = new File("../pdfs/312-page" + pageNum + ".svg");
			if (pageNFile.exists()) {
				pageNFile.delete();
			}
		}

		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/bmc", "-pages", "1-14",
				"-pub", "bmc", "src/test/resources/org/xmlccontentmineml/graphics/pdf/312.pdf");

		for (int pageNum = 1; pageNum <= 14; pageNum++) {
			File pageNFile = new File("../pdfs/bmc/312-page" + pageNum + ".svg");
			Assert.assertTrue(pageNFile.exists());
		}
	}

	
	
	// do not normally run this
	@Test
	@Ignore
	public void testE() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/e", "-pub", "els", "../pdfs/e/6048.pdf");
	}

	
	
	// do not normally run this
	@Test
	@Ignore
	public void testRSC() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/rsc", "-pub", "rsc", "../pdfs/rsc/b306241d.pdf");
	}

	
	
	// do not normally run this
	@Test
	@Ignore
	public void testRSC1() {
		// this has very bad performance because of colour conversion in bitmap fonts
		PDF2SVGConverter converter = new PDF2SVGConverter();
//		converter.run("-outdir", "../pdfs/rsc", "-pages", "3", "../pdfs/rsc/problemChars.pdf");
		converter.run("-outdir", "../pdfs/rsc", "-pub", "pccp", "../pdfs/rsc/problemChars.pdf");
	}
	
	
	@Test
	@Ignore
	public void testRSCMany() {
		convertPDFsToSVG("../pdfs/rsc/many", "target/rsc/many");
	}

	
	
	// do not normally run this
	@Test
	@Ignore
	public void testPsyc() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/psyc", "-pub", "frpsyc","../pdfs/psyc/Holcombe2012.pdf");
	}

	
	
	// do not normally run this
	@Test
	@Ignore
	public void testAPA() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/apa", "-pub", "apa", "../pdfs/apa/Liu2005.pdf");
	}

	
	
	// do not normally run this
	@Test
	@Ignore
	public void testSocDir() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/socdir", "-pub", "socdir", "../pdfs/socdir/1-PB.pdf");
	}

	
	 // not behaving right
	// do not normally run this
	@Test
	@Ignore
	public void testACS() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/acs", "-pub", "acs", "../pdfs/acs/nl072516n.pdf");
	}

	
	 // do not normally run this
	@Test
	@Ignore
	public void testNPG() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/npg", "-pub", "npg", "../pdfs/npg/srep00778.pdf");
	}

	
	 // do not normally run this
	@Test
	@Ignore
	public void testWiley() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/wiley", "-pub", "wiley", "../pdfs/wiley/1032.pdf");
	}

	
	 // do not normally run this
	@Test
	@Ignore
	public void testBMJ() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/bmj", "-pub", "bmj", "../pdfs/bmj/e001553.pdf");
	}

	
	@Test
	@Ignore
	 // do not normally run this
	public void testElife() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/elife", "-pub", "elife", "src/test/resources/elife/00013.pdf");
	}

	@Test
	@Ignore
	 // do not normally run this
	public void testJB() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/jb", "-pub", "jb", "../pdfs/jb/100-14.pdf");
	}

	@Test
	@Ignore
	 // do not normally run this
	public void testPlosOne() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/plosone", "-pub", "plosone", "src/test/resources/plosone/0049149.pdf");
	}

	@Test
	@Ignore
	 // do not normally run this
	public void testPlosOne1() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/plosone", "-pages", "2", "src/test/resources/plosone/2009_rip_loop_conformations.pdf");
	}

	@Test
	@Ignore
	 // do not normally run this
	public void testElsevier2() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/els", "../pdfs/e2/1-s2.0-S2212877812000129-main.pdf");
	}

	@Test
	@Ignore
	 // do not normally run this
	public void testWord() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/word", "-pub", "word", "../pdfs/word/test.pdf");
	}

	@Test
	@Ignore
	 // do not normally run this
	public void testWordMath() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/word", "-pub", "word", "../pdfs/word/testmath.pdf");
	}

	@Test
	@Ignore
	 // do not normally run this
	public void testThesis() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/darmstadt.pdf");
	}
	
	@Test
	@Ignore
	 // do not normally run this
	public void testThesis1() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/keruzore.pdf");
	}
	
	@Test
	@Ignore
	 // do not normally run this
	public void testThesis2() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/Mawer.pdf");
	}
	
	@Test
	@Ignore
	 // do not normally run this
	public void testThesis5() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/zakrysphd.pdf");
	}
	
	@Test
	@Ignore
	public void testThesisMany() {
		convertPDFsToSVG("../pdfs/thesis", "target/thesis");
	}

	
	@Test
	@Ignore
	public void testArxivMany() {
		convertPDFsToSVG("../pdfs/arxiv", "target/arxiv");
	}
	
	
	@Test
	@Ignore
	public void testECU() {
		convertPDFsToSVG("../../documents/standalone/ecu2012", "target/ecu");
	}

	
	@Test
	@Ignore
	public void testPPT() {
		convertPDFsToSVG("../pdfs/ppt", "target/ppt");
	}

	
	@Test
	@Ignore
	public void testHelp() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run();
	}

}
