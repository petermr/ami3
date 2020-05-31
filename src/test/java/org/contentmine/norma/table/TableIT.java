package org.contentmine.norma.table;

import java.io.File;
import java.io.IOException;

import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

public class TableIT {

	@Test
		public void testMenu() {
	//		File targetDir = new File("target/pdftable1/");
			File targetDir = new File("../../cm-ucl/corpus-oa-pmr-v02/");
			/**  */
			new CProject().run("--project "+targetDir
					+ " --output tableViewList.html"
					+ " --projectMenu .*/tables/tableView.html");
	
		}

	/** align rows and columns
	 * 
	 * @param inputDir
	 * @return
	 * @throws IOException 
	 */
	@Test
	public void testRowAndColumns() throws IOException {
		File inputFile = new File(NormaFixtures.TEST_TABLE_DIR, "svg/10.1007_s00213-015-4198-1.svg");
		SVGTable2HTMLConverter converter = new SVGTable2HTMLConverter();
		converter.readInput(inputFile);
		HtmlElement htmlElement = converter.convert();
		File file = new File(NormaFixtures.TARGET_DIR, "table/svg/10.1007_s00213-015-4198-1.svg.html");
		XMLUtil.debug(htmlElement, file, 1);
	}

}
