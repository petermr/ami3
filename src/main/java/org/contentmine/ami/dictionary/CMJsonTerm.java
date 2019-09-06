package org.contentmine.ami.dictionary;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 *      {"identifiers": 
 *        {"contentmine": "CM.cochrane17",
 *         "wikidata": "Q123456789"
 *        }, 
 *        "term": "adverse events", 
 *        "name": "adverse events"
 *      }

 * @author pm286
 *
 */
public class CMJsonTerm {

	private static final Logger LOG = Logger.getLogger(CMJsonTerm.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String CONTENTMINE = "contentmine";
	private static final String IDENTIFIERS = "identifiers";
	private static final String NAME = "name";
	private static final String TERM = "term";
	private static final String WIKIDATA = "wikidata";
	
	private JsonObject termObject;
	private CMJsonDictionary cmJsonDictionary;

	public CMJsonTerm() {
		this.termObject = new JsonObject();
	}

	public CMJsonTerm(CMJsonDictionary cmJsonDictionary) {
		this();
		this.cmJsonDictionary = cmJsonDictionary;
	}
	
	public static CMJsonTerm createTerm(JsonElement jsonElement) {
		CMJsonTerm jsonTerm = null;
		if (jsonElement != null && jsonElement instanceof JsonObject) {
			jsonTerm = new CMJsonTerm();
			jsonTerm.termObject = (JsonObject) jsonElement;
		}
		return jsonTerm;
	}

	public void addCMIdentifier(String cmIdentifier) {
		if (cmJsonDictionary.containsTerm(cmIdentifier)) {
			throw new RuntimeException("Dictionary "+cmJsonDictionary.getId()+" already contains identifier "+cmIdentifier);
		}
		JsonElement identifiers = this.termObject.get(IDENTIFIERS);
		if (identifiers != null && ((JsonObject)identifiers).get(CONTENTMINE) != null) {
			throw new RuntimeException("Cannot add CM identifier twice: "+cmIdentifier);
		}
		addIdentifier(CONTENTMINE, cmIdentifier);
	}
	
	public void addIdentifier(String name, String value) {
		if (name != null && value != null) {
			JsonObject identifiers = ensureIdentifiers();
			identifiers.add(name, new JsonPrimitive(value));
		}
	}

	private JsonObject ensureIdentifiers() {
		JsonElement identifiers = this.termObject.get(IDENTIFIERS);
		if (identifiers == null) {
			identifiers = new JsonObject();
			this.termObject.add(IDENTIFIERS, identifiers);
		}
		return (JsonObject) identifiers;
	}

	public void addTerm(String value) {
		JsonElement termElement = this.termObject.get(TERM);
		if (termElement != null) {
			throw new RuntimeException("Cannot add term twice: "+value);
		}
		termObject.add(TERM, new JsonPrimitive(value));
	}
	
	public void addName(String value) {
		JsonElement nameElement = this.termObject.get(NAME);
		if (nameElement != null) {
			throw new RuntimeException("Cannot add name twice: "+value+" ("+nameElement+")");
		}
		termObject.add(NAME, new JsonPrimitive(value));
	}

	public String getId() {
		String id = null;
		JsonElement identifiers = this.termObject.get(IDENTIFIERS);
		if (identifiers != null) {
			JsonElement idElem = ((JsonObject)identifiers).get(CONTENTMINE);
			id = (idElem == null) ? null : idElem.getAsString();
		}
		return id;
	}
	
	public JsonObject getTermObject() {
		return termObject;
	}

	public String getName() {
		JsonElement elem = termObject.get(NAME);
		return elem == null ? null : elem.getAsString();
	}

	public String getTerm() {
		JsonElement elem = termObject.get(TERM);
		return elem == null ? null : elem.getAsString();
	}

	public void addWikidata(String q) {
		if (!q.startsWith("Q")) {
			q = "Q"+q;
		}
		addIdentifier(WIKIDATA, q);
	}
}
