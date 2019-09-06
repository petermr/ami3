package org.contentmine.cproject.metadata.crossref;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CRPerson {

	private static final String SUFFIX = "suffix";
	private static final String ORCID = "ORCID";
	private static final String GIVEN = "given";
	private static final String FAMILY = "family";
	private static final String AFFILIATION = "affiliation";
	
	private static final Logger LOG = Logger.getLogger(CRPerson.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private JsonArray affiliation;
	private String family;
	private String given;
	private String orcid;
	private String suffix;

	public CRPerson() {
		
	}

	public static CRPerson createFrom(JsonObject jsonAuthor) {
		CRPerson author = new CRPerson();
		for (Map.Entry<String, JsonElement> entry : jsonAuthor.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if (AFFILIATION.equals(key)) {
				author.affiliation = value.getAsJsonArray();
			} else if (FAMILY.equals(key)) {
				author.family = value.getAsString();
			} else if (GIVEN.equals(key)) {
				author.given = value.getAsString();
			} else if (ORCID.equals(key)) {
				author.orcid = value.getAsString();
			} else if (SUFFIX.equals(key)) {
				author.orcid = value.getAsString();
			} else {
				throw new RuntimeException("unknown author field: "+key);
			}
		}
		return author;
	}

	public JsonArray getAffiliation() {
		return affiliation;
	}

	public String getFamily() {
		return family;
	}

	public String getGiven() {
		return given;
	}
	
	public String toString() {
		String s = "";
		if (family != null) {
			s = family;
		}
		if (given != null) {
			s += " "+given;
		}
		if (affiliation.size() > 0) {
			s += " "+affiliation.toString()+"\n";
		}
		return s;
	}
	

}
