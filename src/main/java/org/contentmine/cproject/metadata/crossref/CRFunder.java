package org.contentmine.cproject.metadata.crossref;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.JsonUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
{
        "award": [
          "DNRF-93"
        ],
        "doi-asserted-by": "crossref",
        "name": "Danmarks Grundforskningsfond",
        "DOI": "10.13039/501100001732"
      },
      {
        "award": [],
        "name": "DANSCATT"
      }
      
 * @author pm286
 *
 */
public class CRFunder {

	private static final String DOI_ASSERTED_BY = "doi-asserted-by";
	private static final String DOI = "DOI";
	private static final String NAME = "name";
	private static final String AWARD = "award";
	private static final Logger LOG = Logger.getLogger(CRFunder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String award;
	private String name;
	private String doi;
	
	public CRFunder() {
		
	}

	public static CRFunder createFrom(JsonObject jsonAuthor) {
		CRFunder funder = new CRFunder();
		for (Map.Entry<String, JsonElement> entry : jsonAuthor.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if (AWARD.equals(key)) {
				funder.award = JsonUtils.getString(value.getAsJsonArray());
			} else if (NAME.equals(key)) {
				funder.name = value.getAsString();
			} else if (DOI.equals(key)) {
				funder.doi = value.getAsString();
			} else if (DOI_ASSERTED_BY.equals(key)) {
				// ignore
			} else {
				throw new RuntimeException("unknown funder field: "+key);
			}
		}
		return funder;
	}


	public String toString() {
		String s = "";
		if (award != null) {
			s += award;
		}
		if (name != null) {
			s += " "+name;
		}
		if (doi != null) {
			s += " "+doi;
		}
		return s;
	}
	

}
