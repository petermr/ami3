package org.contentmine.ami.lookups;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.lookups.TaxdumpLookup;
import org.junit.Assert;
import org.junit.Test;

public class TaxdumpLookupTest {

	
	private static final Logger LOG = LogManager.getLogger(TaxdumpLookupTest.class);
@Test
	public void testGenus() throws Exception {
		TaxdumpLookup taxdumpLookup = new TaxdumpLookup();
		Assert.assertTrue("Mus", taxdumpLookup.isValidGenus("Mus"));
	}
	
	@Test
	public void testInvalidGenus() throws Exception {
		TaxdumpLookup taxdumpLookup = new TaxdumpLookup();
		Assert.assertFalse("Mickey", taxdumpLookup.isValidGenus("Mickey"));
	}
	
	@Test
	public void testBinomial() throws Exception {
		TaxdumpLookup taxdumpLookup = new TaxdumpLookup();
		Assert.assertTrue("Mus musculus", taxdumpLookup.isValidBinomial("Mus", "musculus"));
	}
	
	@Test
	public void testInvalidBinomial() throws Exception {
		TaxdumpLookup taxdumpLookup = new TaxdumpLookup();
		Assert.assertFalse("Mickey mouse", taxdumpLookup.isValidBinomial("Mickey", "mouse"));
	}
	
	@Test
	public void testSpeciesForGenus() throws Exception {
		TaxdumpLookup taxdumpLookup = new TaxdumpLookup();
		List<String> speciesList = taxdumpLookup.lookupSpeciesList("Zyzzyzus");
		Assert.assertEquals("Zyzzyzus", "[calderi, warreni]", speciesList.toString());
	}
	
}
