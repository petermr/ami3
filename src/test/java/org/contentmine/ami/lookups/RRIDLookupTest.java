package org.contentmine.ami.lookups;

import java.io.IOException;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.contentmine.ami.lookups.GenbankLookup;
import org.contentmine.ami.lookups.GenbankResultAnalyzer;
import org.contentmine.cproject.lookup.AbstractLookup;
import org.contentmine.eucl.xml.XMLUtil;

@Ignore // uses web
public class RRIDLookupTest {

	private static final Logger LOG = Logger.getLogger(RRIDLookupTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	@Ignore("Not yet working") // LOOKUP 
	public void getGenbankForTaxon() throws Exception {
		
		GenbankLookup genbankLookup = new GenbankLookup();
		String result = genbankLookup.lookupTaxonomy("352165");
		LOG.debug("result: "+result);
		
//		Assert.assertEquals("mouse", "(83310)", intArray.toString());
	}

	/**
Pyramidobacter piscolens W5455
Taxonomy ID: 352165
Inherited blast name: bacteria
Rank: no rank
Genetic code: Translation table 11 (Bacterial, Archaeal and Plant Plastid)
	 * @throws Exception
	 */
	@Test
	public void getTaxonomyForGenbankId() throws Exception {
		AbstractLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookup("EU379932");
		GenbankResultAnalyzer gra = new GenbankResultAnalyzer();
		gra.readGBSeq(resultXML);
		String taxon = gra.getTaxonFromGBSeq();
		if (taxon != null) {
			Assert.assertEquals("taxon",  "taxon:352165", taxon);
		}
	}

	@Test
	public void getTaxonomyForGenbankIdBSubt() throws Exception {
		AbstractLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookup("CAB15547");
		GenbankResultAnalyzer gra = new GenbankResultAnalyzer();
		gra.readGBSeq(resultXML);
		String taxon = gra.getTaxonFromGBSeq();
		if (taxon != null) {
			Assert.assertEquals("taxon",  "taxon:224308", taxon);
		}
	}

	@Test
	public void getOrganismForGenbankId() throws Exception {
		AbstractLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookup("EU379932");
		GenbankResultAnalyzer gra = new GenbankResultAnalyzer();
		gra.readGBSeq(resultXML);
		String species = gra.getOrganismFromGBSeq();
		if (species != null) {
			Assert.assertEquals("species",  "Pyramidobacter piscolens", species);
		}
		String taxon = gra.getTaxonFromGBSeq();
		if (taxon != null) {
			Assert.assertEquals("taxon",  "taxon:352165", taxon);
		}
	}

	@Test
	public void getGenbankForErithacus() throws Exception {
		AbstractLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookup("JX170864");
		GenbankResultAnalyzer gra = new GenbankResultAnalyzer();
		gra.readGBSeq(resultXML);
		String species = gra.getOrganismFromGBSeq();
		if (species != null) {
			Assert.assertEquals("species",  "Erithacus rubecula", species);
		}
		String taxon = gra.getTaxonFromGBSeq();
		if (taxon !=  null) {
			Assert.assertEquals("taxon",  "taxon:37610", taxon);
		}
	}

	/**
	 * @throws IOException 
	 *     
Uniprot:
Mnemonic i	-
Taxon identifier i	638849
Scientific name i	Pyramidobacter piscolens
Common name i	-
Synonym i	-
	 * 
	 */
	@Test
	public void testLookupSpeciesPyramidobacter() throws IOException {
		GenbankLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookupTaxonomyWithEsearch("Pyramidobacter", "piscolens");
		GenbankResultAnalyzer gra = new GenbankResultAnalyzer();
		gra.readEsearch(resultXML);
		String id = gra.getIdFromEsearch();
		if (id != null) {
			Assert.assertEquals("id", "638849", id);
		}
	}
	
	@Test
	public void testLookupBSubtilis() throws IOException {
		GenbankLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookupTaxonomyWithEsearch("Bacillus", "subtilis");
		GenbankResultAnalyzer gra = new GenbankResultAnalyzer();
		gra.readEsearch(resultXML);
		String id = gra.getIdFromEsearch();
		if (id != null) {
			Assert.assertEquals("id", "1423", id);
		}
	}
	
	@Test
	public void testLookupErithacus() throws IOException {
		GenbankLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookupTaxonomyWithEsearch("Erithacus", "rubecula");
		GenbankResultAnalyzer gra = new GenbankResultAnalyzer();
		gra.readEsearch(resultXML);
		String id = gra.getIdFromEsearch();
		if (id != null) {
			Assert.assertEquals("id", "37610", id);
		}
	}
	
	/**
http://www.ncbi.nlm.nih.gov/taxonomy/?term=Pyramidobacter+piscolens
	 * @throws IOException 
	 */
	@Test
	@Ignore("superseded")
	public void testLookupGenus() throws IOException {
		GenbankLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookupTaxonomy("Pyramidobacter");
		LOG.debug(resultXML);
	}
	
	/**
	 * @throws IOException 
	 */
	@Test
	@Ignore("superseded")
	public void testLookupGenusInDatabase() throws IOException {
		GenbankLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookupTaxonomyInDatabase("pubmed", "mouse");
		LOG.debug(resultXML);
	}
	
	/**
	 * @throws IOException 
	 */
	@Test
	@Ignore("superseded")
	public void testLookupSpeciesInDatabase() throws IOException {
		GenbankLookup genbankLookup = new GenbankLookup();
		String resultXML = genbankLookup.lookupTaxonomyInDatabase("pubmed", "Pyramidobacter", "piscolens");
		LOG.debug(resultXML);
	}
}
