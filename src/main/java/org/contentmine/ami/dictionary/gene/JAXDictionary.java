package org.contentmine.ami.dictionary.gene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.norma.NAConstants;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

/** Human gene nomenclature from
 * ftp://ftp.ebi.ac.uk/pub/databases/genenames/new/json/hgnc_complete_set.json
 * 
 * @author pm286
 *
 *1. MGI accession id	2. marker type	3. marker symbol	4. marker name	5. genome build	
 *6. Entrez gene id	7. NCBI gene chromosome	8. NCBI gene start	9. NCBI gene end	10. NCBI gene strand	
 *11. Ensembl gene id	12. Ensembl gene chromosome	13. Ensembl gene start	14. Ensembl gene end	15. Ensembl gene strand	
 *16. VEGA gene id	17. VEGA gene chromosome	18. VEGA gene start	19. VEGA gene end	20. VEGA gene strand
MGI:87853	Gene	a	nonagouti	GRCm38	50518	2	154950162	155051012	+	ENSMUSG00000027596	2	154791402	155051012	+	OTTMUSG00000015981	2	154791402	155051012	+	
MGI:87854	Gene	Pzp	pregnancy zone protein	GRCm38	11287	6	128483567	128526720	-	ENSMUSG00000030359	6	128483567	128526720	-	OTTMUSG00000022212	6	128483573	128526720	-

 * probably only (3) and (4) are useful at this stage
 */
public class JAXDictionary extends DefaultAMIDictionary {

	private static final Logger LOG = LogManager.getLogger(JAXDictionary.class);
private final static File JAX_DIR = new File(GENE_DIR, "jax");
	private final static File JAX_TSV_FILE = new File(JAX_DIR, "MGI_Gene_Model_Coord.tsv");
	private final static File JAX_XML_FILE = new File(JAX_DIR, "jax.xml");
	
	public JAXDictionary() {
		init();
	}
	
	private void init() {
		readJAX_XML();
	}

	private void readJAX_XML() {
		ClassLoader cl = getClass().getClassLoader();
		String jax = NAConstants.AMI_RESOURCE+"/plugins/gene/jax/jax.xml";
		jax = "org/contentmine/ami/plugins/gene/jax/jax.xml";
		InputStream JAX_XML_RES = cl.getResourceAsStream(jax);
		if (JAX_XML_RES == null) {
			throw new RuntimeException("Cannot read resource: "+jax);
			///normami/src/main/resources/org/contentmine/ami/plugins/gene/jax/jax.xml
		}
		readDictionary(JAX_XML_RES);
	}

	private void readJAXTSV(File file) {
		TsvParserSettings settings = new TsvParserSettings();
	    //the file used in the example uses '\n' as the line separator sequence.
	    //the line separator sequence is defined here to ensure systems such as MacOS and Windows
	    //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
	    settings.getFormat().setLineSeparator("\n");
	    TsvParser parser = new TsvParser(settings);
	    List<String[]> allRows = null;
	    try {
			allRows = parser.parseAll(new BufferedReader(new FileReader(file)));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read: "+file, e);
		}
	    namesByTerm = new HashMap<DictionaryTerm, String>();
	    for (String[] row : allRows) {
	    	String symbol = row[2];
	    	String name = row[3];
	    	namesByTerm.put(new DictionaryTerm(symbol), name);
	    }
	}

	@Override
	public List<List<String>> getTrailingWords(String key) {
		throw new RuntimeException("cannot use for HGNC");
	}


}
