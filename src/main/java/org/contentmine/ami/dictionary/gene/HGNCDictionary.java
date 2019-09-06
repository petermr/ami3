package org.contentmine.ami.dictionary.gene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.norma.NAConstants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

/** Human gene nomenclature from
 * ftp://ftp.ebi.ac.uk/pub/databases/genenames/new/json/hgnc_complete_set.json
 * 
 * @author pm286
 *
 */
public class HGNCDictionary extends DefaultAMIDictionary {

	private static final String HGNC = "hgnc";
	private static final Logger LOG = Logger.getLogger(HGNCDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File HGNC_DIR = new File(GENE_DIR, HGNC);
	private final static File HGNC_JSON_FILE = new File(HGNC_DIR, "hgnc_complete_set.json");
	private final static File HGNC_JSON_FILE1 = new File(HGNC_DIR, "hgnc_complete_set_readable.json");
	private final static File HGNC_XML_FILE = new File(HGNC_DIR, "hgnc.xml");
	
	public static HGNCDictionary DEFAULT_HGNCDictionary = null;
	
	private JsonObject hgncJson;
	
	public HGNCDictionary() {
		init();
	}
	
	private void init() {
		readHGNCXML();
	}

	private void readHGNCXML() {
		ClassLoader cl = getClass().getClassLoader();
		String hgnc = NAConstants.GENE_HGNC+"/hgnc.xml";
		LOG.debug("is "+hgnc);
//		InputStream hgncIs = cl.getResourceAsStream(hgnc);
		InputStream hgncIs = this.getClass().getResourceAsStream(hgnc);
		readDictionary(hgncIs);
	}

	private void readHGNCJson() {
		try {
			createFromInputStream(HGNC, this.getClass().getClassLoader().getResourceAsStream(
					NAConstants.GENE_HGNC+"/hgnc_complete_set.json"));
			String resultsJsonString = IOUtils.toString(inputStream, UTF_8);
		    JsonParser parser = new JsonParser();
		    hgncJson = (JsonObject) parser.parse(resultsJsonString);
		    try {
		        StringWriter stringWriter = new StringWriter();
		        JsonWriter jsonWriter = new JsonWriter(stringWriter);
		        jsonWriter.setLenient(true);
		        jsonWriter.setIndent("  ");
		        Streams.write(hgncJson, jsonWriter);
		        FileUtils.write(HGNC_JSON_FILE1, stringWriter.toString());
		      } catch (IOException e) {
		        throw new AssertionError(e);
		      }
		} catch (Exception e) {
			throw new RuntimeException("Cannot read HGNC file: "+HGNC_JSON_FILE, e);
		}
		
		JsonObject response = (JsonObject) hgncJson.get("response");
		int numTerms = response.get("numFound").getAsInt();
		JsonArray docs = response.get("docs").getAsJsonArray();
		createIds(docs, numTerms);
	}

	private void createIds(JsonArray docs, int numTerms) {
		namesByTerm = new HashMap<DictionaryTerm, String>();
		/**
{
    "gene_family":["Immunoglobulin-like domain containing"],
	"date_approved_reserved":"1989-06-30",
	"vega_id":"OTTHUMG00000183507",
	"locus_group":"protein-codingbgene",
	"status":"Approved",
	"_version_":1522770583154065408,
	"uuid":"550c4cad-7f6a-4f85-b872-3889c9afe302",
	"merops":"I43.950",
	"refseq_accession":["NM_130786"],
	"locus_type":"gene with protein product",
	"gene_family_id":[594],
	"cosmic":"A1BG",
	"hgnc_id":"HGNC:5",
	"rgd_id":["RGD:69417"],
	"ensembl_gene_id":"ENSG00000121410",
	"entrez_id":"1",
	"omim_id":[138670],
	"symbol":"A1BG",
	"location":"19q13.43",
	"name":"alpha-1-B glycoprotein",
	"date_modified":"2015-07-13",
	"mgd_id":["MGI:2152878"],
	"ucsc_id":"uc002qsd.4",
	"uniprot_ids":["P04217"],
	"ccds_id":["CCDS12976"],
	"pubmed_id":[2591067],
	"location_sortable":"19q13.43"
	},
		 */
		for (int i = 0; i < numTerms; i++) {
			JsonObject doc = (JsonObject) docs.get(i);
			String term = doc.get("symbol").getAsString();
			String name = doc.get("name").getAsString();
			namesByTerm.put(new DictionaryTerm(term), name);
		}
	}
	
	// ===============

	// only for developing
	private void debug(JsonObject jsonObject) {
		Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
		LOG.debug(set.size());
		Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String , JsonElement> element = iterator.next();
			LOG.debug("IT "+element.getKey());
		}
	}
	
	
}
