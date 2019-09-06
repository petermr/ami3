package org.contentmine.ami.lookups;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.lookup.AbstractLookup;

public class ENALookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(ENALookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public ENALookup() {
	}

	/**
http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&term="Gorilla+gorilla"&retmode=text
	http://www.ebi.ac.uk/ena/data/view/Taxon:Gorilla%20gorilla,Taxon:Erithacus&display=xml
*/
	
	public String lookupTaxonomy(String genbankId) throws IOException {
		return null;
	}

//	http://www.ebi.ac.uk/Tools/dbfetch/dbfetch?db=livelists&id=JN556047&style=raw
// ERROR 1 Unknown database [livelists].
	/** lookup Genbank/ENAIds.
	 * 
	 * seems to work at EBI. retrieves whole entry
	 * 
	 * @param genbankId
	 * @return
	 * @throws IOException
	 */
	public String lookupGenbankIds(List<String> genbankId) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("http://www.ebi.ac.uk/Tools/dbfetch/dbfetch?id=");
		for (int i = 0; i < genbankId.size(); i++) {
			if (i > 0) sb.append(",");
			sb.append(genbankId.get(i));
		}
		setOutputFormat("&retmode=xml");
		urlString = sb.toString();
		return getResponse(url);
	}

	@Override
	public String lookup(String key) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

		
}
