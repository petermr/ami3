package org.contentmine.cproject.metadata.crossref;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;

/** depend on local files and may take time
 * 
 * analyzes the bulk CSV data cretaed from Crossref JSON files
 * 
 * @author pm286
 *
 */
public class CrossrefCSVTest {

	private static final Logger LOG = Logger.getLogger(CrossrefCSVTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** GET HEADERS FROM CROSSREF SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetpapers() throws IOException {
		RectangularTable table = RectangularTable.readCSVTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		Assert.assertEquals(12141, table.size());
		Assert.assertEquals("[License, Title, DOI, Publisher, Prefix, Date, Keywords]", table.getHeader().toString());
	}

	/** ANALYZE DOI COLUMN FROM CROSSREF SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAnalyzeDOIColumn() throws IOException {
		String colHead = "DOI";
		RectangularTable table = RectangularTable.readCSVTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		List<String> columnValues = table.getColumn(table.getIndexOfColumn(colHead));
		Assert.assertEquals(12141, columnValues.size());
		List<Multiset.Entry<String>> multisetList = table.extractSortedMultisetList(colHead);
		Assert.assertEquals(12141, multisetList.size());
		List<Multiset.Entry<String>> uniqueMultisetList = table.extractUniqueMultisetList(colHead);
		Assert.assertEquals(12141, uniqueMultisetList.size());
		List<Multiset.Entry<String>> duplicateMultisetList = table.extractDuplicateMultisetList(colHead);
		Assert.assertEquals(0, duplicateMultisetList.size());
		
	}
	
	/** ANALYZE LICENSE COLUMN FROM CROSSREF SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore //fails on test ordering

	public void testAnalyzeLicenseColumn() throws IOException {
		String colHead = "License";
		RectangularTable table = RectangularTable.readCSVTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		List<String> columnValues = table.getColumn(table.getIndexOfColumn(colHead));
		Assert.assertEquals(12141, columnValues.size());
		List<Multiset.Entry<String>> multisetList = table.extractSortedMultisetList(colHead);
		Assert.assertEquals(33, multisetList.size());
		List<Multiset.Entry<String>> uniqueMultisetList = table.extractUniqueMultisetList(colHead);
		Assert.assertEquals(2, uniqueMultisetList.size());
		Assert.assertEquals("["
				+ "3: [http://doi.wiley.com/10.1002/tdm_license_1,"
				+ " http://creativecommons.org/licenses/by/4.0/,"
				+ " http://creativecommons.org/licenses/by/4.0/],"
				+ " 3: [http://doi.wiley.com/10.1002/tdm_license_1,"
				+ " http://creativecommons.org/licenses/by-nc-nd/4.0/,"
				+ " http://creativecommons.org/licenses/by-nc-nd/4.0/]]", uniqueMultisetList.toString());
		List<Multiset.Entry<String>> duplicateMultisetList = table.extractDuplicateMultisetList(colHead);
		Assert.assertEquals(31, duplicateMultisetList.size());
		Assert.assertEquals("["
				+ "0: [] x 7537,"
				+ " 1: [http://www.elsevier.com/tdm/userlicense/1.0/] x 2116,"
				+ " 1: [http://www.springer.com/tdm] x 1023,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions] x 607,"
				+ " 2: [http://www.elsevier.com/tdm/userlicense/1.0/, http://creativecommons.org/licenses/by-nc-nd/4.0/] x 130,"
				+ " 1: [http://www.acm.org/publications/policies/copyright_policy#Background] x 125,"
				+ " 1: [http://link.aps.org/licenses/aps-default-license] x 91,"
				+ " 1: [http://creativecommons.org/licenses/by/4.0/] x 65,"
				+ " 2: [http://iopscience.iop.org/info/page/text-and-data-mining, http://iopscience.iop.org/page/copyright] x 62,"
				+ " 2: [http://iopscience.iop.org/info/page/text-and-data-mining, http://creativecommons.org/licenses/by/3.0/] x 54,"
				+ " 1: [http://creativecommons.org/licenses/by/3.0/] x 50,"
				+ " 2: [http://link.aps.org/licenses/aps-default-license, http://link.aps.org/licenses/aps-default-accepted-manuscript-license] x 45,"
				+ " 1: [http://creativecommons.org/licenses/by-nc/4.0] x 31,"
				+ " 2: [http://www.elsevier.com/tdm/userlicense/1.0/, http://creativecommons.org/licenses/by/4.0/] x 30,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://creativecommons.org/licenses/by/4.0/] x 24,"
				+ " 1: [http://www.bmj.org/licenses/tdm/1.0/terms-and-conditions.html] x 20,"
				+ " 1: [http://doi.wiley.com/10.1002/tdm_license_1] x 20,"
				+ " 1: [http://creativecommons.org/licenses/by-nc-nd/4.0] x 16,"
				+ " 2: [http://creativecommons.org/licenses/by/2.5/za/, http://creativecommons.org/licenses/by/2.5/za/] x 16,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://creativecommons.org/licenses/by-nc-nd/4.0/] x 15,"
				+ " 1: [http://creativecommons.org/licenses/by/4.0] x 13,"
				+ " 3: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions, http://onlinelibrary.wiley.com/termsAndConditions] x 9,"
				+ " 1: [http://creativecommons.org/licenses/by-nc/3.0/] x 8, 1: [http://pubs.acs.org/page/policy/authorchoice_termsofuse.html] x 8,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://creativecommons.org/licenses/by-nc/4.0/] x 5,"
				+ " 1: [https://publishing.aip.org/authors/rights-and-permissions] x 4,"
				+ " 3: [http://iopscience.iop.org/info/page/text-and-data-mining, http://iopscience.iop.org/page/copyright, http://creativecommons.org/licenses/by-nc-nd/3.0] x 4,"
				+ " 3: [http://creativecommons.org/licenses/by/4.0/, http://creativecommons.org/licenses/by/4.0/, http://creativecommons.org/licenses/by/4.0/] x 4,"
				+ " 1: [http://creativecommons.org/licenses/by-sa/4.0] x 3,"
				+ " 1: [https://www.osapublishing.org/submit/licenses/license_v1.cfm#vor] x 2,"
				+ " 3: [http://iopscience.iop.org/info/page/text-and-data-mining, http://creativecommons.org/licenses/by/3.0/, http://creativecommons.org/licenses/by/3.0] x 2]",
				duplicateMultisetList.toString());
		
	}

	/** ANALYZE PUBLISHERS FROM SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore //fails on test ordering
	public void testAnalyzePublishers() throws IOException {
		String colHead = "Publisher";
		RectangularTable table = RectangularTable.readCSVTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		List<String> columnValues = table.getColumn(table.getIndexOfColumn(colHead));
		Assert.assertEquals(12141, columnValues.size());
		List<Multiset.Entry<String>> multisetList = table.extractSortedMultisetList(colHead);
		Assert.assertEquals(325, multisetList.size());
		Assert.assertEquals("[Elsevier BV x 2271,"
		+ " Springer Science + Business Media x 1038,"
		+ " Wiley-Blackwell x 682,"
		+ " Hamad bin Khalifa University Press (HBKU Press) x 456,"
		+ " Informa UK Limited x 438,"
		+ " Clute Institute x 364,"
		+ " Logos Medi", multisetList.toString().substring(0,  200));
		List<Multiset.Entry<String>> uniqueMultisetList = table.extractUniqueMultisetList(colHead);
		Assert.assertEquals(53, uniqueMultisetList.size());
		Assert.assertEquals("[Association Palaeovertebrata,"
				+ " Federal Reserve Bank of Kansas City,"
				+ " Associacao Sergipana de Ciencia,"
				+ " University of South Florida Libraries,"
				+ " Science and Education Centre of North America,"
				+ " Rubber Divisi", uniqueMultisetList.toString().substring(0,  200));
		List<Multiset.Entry<String>> duplicateMultisetList = table.extractDuplicateMultisetList(colHead);
		Assert.assertEquals(272, duplicateMultisetList.size());
		Assert.assertEquals("[Elsevier BV x 2271,"
				+ " Springer Science + Business Media x 1038,"
				+ " Wiley-Blackwell x 682,"
				+ " Hamad bin Khalifa University Press (HBKU Press) x 456,"
				+ " Informa UK Limited x 438,"
				+ " Clute Institute x 364,"
				+ " Logos Medi", duplicateMultisetList.toString().substring(0,  200));
	}
	


}
