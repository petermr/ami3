package org.contentmine.ami.lookups;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.contentmine.cproject.lookup.AbstractLookup;

public class GenbankLookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(GenbankLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public GenbankLookup() {
	}

	/*
http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=34577062,24475906&rettype=fasta&retmode=text
http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term="+genbank_id+"GENBANK_ID	 */
	
	public String lookup(String genbankId) throws IOException {
		urlString = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id="+genbankId;
		setOutputFormat("&retmode=xml");
		url = createUrl();
		return getResponse(url);
	}

	public String lookupGenbankIds(List<String> genbankId) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=");
		for (int i = 0; i < genbankId.size(); i++) {
			if (i > 0) sb.append(",");
			sb.append(genbankId.get(i));
		}
		setOutputFormat("&retmode=xml");
		url = createUrl();
		return getResponse(url);
	}

/**
	http://www.ebi.ac.uk/ena/data/view/Taxon:Gorilla%20gorilla,Taxon:Erithacus&display=xml
*/
	
	public String lookupTaxonomy(String genus) throws IOException {
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&term="+genus); // fails
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=science[journal]+AND+breast+cancer+AND+2008[pdat]"); // works
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=science[journal]"); // works
		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/egquery.fcgi?term="+genus+"[orgn]"); // works
		
		return getResponse(url);
	}

	public String lookupTaxonomyNucleotide(String genus) throws IOException {
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&term="+genus); // fails
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=science[journal]+AND+breast+cancer+AND+2008[pdat]"); // works
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=science[journal]"); // works
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/egquery.fcgi?db=gss&term=mouse[orgn]"); // works
		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/egquery.fcgi?db=nucleotide&term="+genus+"[orgn]"); // works
		
		return getResponse(url);
	}

	public String lookupTaxonomy(List<String> queryList) throws IOException {
		return null;
	}

	public String lookupTaxonomyWithEsearch(String genus, String specific) throws IOException {
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?term="+genus+"%20"+specific+"&retmode=xml");
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/egquery.fcgi?term="+genus+"+"+specific+"[orgn]"); 
		
//		URL url = new URL("https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?name="+genus+"+"+specific+"&retmode=xml");
//		URL url = new URL("https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?name="+genus+"+"+specific+"&lvl=1");
		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=taxonomy&term="+genus+"+"+specific+"&retmode=xml");		
		return getResponse(url);
	}


	public String lookupTaxonomyInDatabase(String database, String genus) throws IOException {
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?term="+genus+"%20"+specific+"&retmode=xml");
		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/egquery.fcgi?db="+database+"&term="+genus); 
		return getResponse(url);
	}

	public String lookupTaxonomyInDatabase(String database, String genus, String specific) throws IOException {
//		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?term="+genus+"%20"+specific+"&retmode=xml");
		URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/egquery.fcgi?db="+database+"&term="+genus+"+"+specific+"[orgn]"); 
		return getResponse(url);
	}

		
}
