package org.contentmine.ami.dictionary;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * Json representation of a CM dictionary
 * 
 * {
 *   "id": "CM.cochrane", 
 *   "entries": [
 *     {"identifiers": 
 *       {"contentmine": "CM.cochrane0"},
 *      "term": "Cochrane Library", 
 *      "name": "cochrane library"
 *     }, 
 *     {"identifiers": 
 *     	 {"contentmine": "CM.cochrane1",
 *        "wikidata", "Q123456789"
 *        }, 
 *      "term": "Cochrane Reviews",
 *       "name": "cochrane reviews"
 *       }, 
 *      {"identifiers": 
 *        {"contentmine": "CM.cochrane17"
 *        }, 
 *        "term": "adverse events", 
 *        "name": "adverse events"
 *      }
 *        ],
 *       "name": "cochrane"
 *    }

 * @author pm286
 *
 */
public class CMJsonDictionary {

	private static final Logger LOG = Logger.getLogger(CMJsonDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String EXT_ID = "ext_id";
	private static final String Q = "q";
	private static final String DICTIONARY = "dictionary";
	private static final String NAME = "name";
	private static final String ENTRIES = "entries";
	public final static String ID = "id";
	
	private String id;
	private JsonArray entries;
	private String name;
	private Map<String, CMJsonTerm> termsByCMId;
	private JsonObject jsonObject;

	public CMJsonDictionary() {
		termsByCMId = new HashMap<String, CMJsonTerm>();
	}

	public final static CMJsonDictionary readJsonDictionary(String jsonString) {
		CMJsonDictionary cmJsonDictionary =  null;
		if (jsonString != null) {
			cmJsonDictionary = new CMJsonDictionary();
			JsonObject jsonObject = null;
			try {
				JsonParser jsonParser = new JsonParser();
				jsonObject = (JsonObject) jsonParser.parse(jsonString);
			} catch (JsonSyntaxException mje) {
				// Caused by: com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 5 path $
				LOG.debug("Malformed JSON: " + jsonString.substring(0, Math.min(100, jsonString.length())));
				LOG.debug(mje);
				throw new RuntimeException("malformed JSON", mje);
			}
			JsonElement jsonId = jsonObject.get(ID);
			if (jsonId == null) {
				throw new RuntimeException("JsonDictionary must have ID");
			}
			cmJsonDictionary.setId(jsonId.getAsString());
			JsonElement entries = jsonObject.get(ENTRIES);
			if (entries == null || !(entries instanceof JsonArray)) {
				throw new RuntimeException("Dictionary must have 'entries' object");
			}
			cmJsonDictionary.entries = (JsonArray) entries;
			if (cmJsonDictionary.entries.size() == 0) {
				LOG.warn("Dictionary has no entries");
			}
			JsonElement nameElement = jsonObject.get(NAME);
			if (nameElement != null) {
				cmJsonDictionary.setName(((JsonPrimitive)nameElement).getAsString());
			}
			
		}
		return cmJsonDictionary;
	}
	
	public String getId() {
		return id;
	}

	public boolean containsTerm(String cmIdentifier) {
		return termsByCMId.containsKey(cmIdentifier);
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public JsonObject getJsonObject() {
		if (this.jsonObject == null) {
			this.jsonObject = new JsonObject();
		}
		if (id != null) {
			this.jsonObject.add(ID, new JsonPrimitive(id));
		}
		if (entries != null) {
			this.jsonObject.add(ENTRIES, entries);
		}
		if (name != null) {
			this.jsonObject.add(NAME, new JsonPrimitive(name));
		}
		return jsonObject;
 	}
	
	public String toString() {
		return String.valueOf(this.getJsonObject());
	}

	public void addTerm(CMJsonTerm entry) {
		String termId = entry.getId();
		if (termId == null) {
			throw new RuntimeException("term must have CM identifier");
		}
		String name = entry.getName();
		if (name == null) {
			throw new RuntimeException("term must have name");
		}
		String ter = entry.getTerm();
		if (termId == null) {
			throw new RuntimeException("term must have CM identifier");
		}
		if (this.containsTerm(termId)) {
			throw new RuntimeException("Cannot add duplicate ID: "+termId);
		} else {
			if (entries == null) {
				entries = new JsonArray();
			}
			entries.add(entry.getTermObject());
			termsByCMId.put(termId, entry);
		}
	}

	public static CMJsonDictionary convertXMLToJson(DefaultAMIDictionary xmlDictionary) {
		return (xmlDictionary == null) ? null :
			createCMJsonDictionary( xmlDictionary.getDictionaryElement());
	}

	public static CMJsonDictionary createCMJsonDictionary(Element dictionaryElement) {
		CMJsonDictionary cmJsonDictionary = null;
		if (dictionaryElement != null) {
			cmJsonDictionary = new CMJsonDictionary();
			cmJsonDictionary.setId(dictionaryElement.getAttributeValue(ID));
			List<Element> elements = XMLUtil.getQueryElements(dictionaryElement, DefaultAMIDictionary.ENTRY);
			for (Element entryElement : elements) {
				CMJsonTerm term = new CMJsonTerm(cmJsonDictionary);
				term.addCMIdentifier(entryElement.getAttributeValue(DefaultAMIDictionary.ID));
				term.addName(entryElement.getAttributeValue(DictionaryTerm.NAME));
				term.addTerm(entryElement.getAttributeValue(DictionaryTerm.TERM));
				cmJsonDictionary.addTerm(term);
			}
		}
		return cmJsonDictionary;
	}


	public static DefaultAMIDictionary convertJsonToXML(CMJsonDictionary cmJsonDictionary) {
		DefaultAMIDictionary amiDictionary = null;
		if (cmJsonDictionary != null) {
			amiDictionary = new DefaultAMIDictionary();
			amiDictionary.dictionaryElement = new Element(DICTIONARY);
			String id = cmJsonDictionary.getId();
			if (id == null) {
				throw new RuntimeException("Must give ID in JsonDictionary");
			}
			amiDictionary.dictionaryElement.addAttribute(new Attribute(ID, id));
			String name = cmJsonDictionary.getName();
			if (name != null) {
				amiDictionary.dictionaryElement.addAttribute(new Attribute(NAME, name));
			}
			JsonArray entries = cmJsonDictionary.getEntries();
			if (entries == null || entries.size() == 0) {
				throw new RuntimeException("Json dictionary has no entries");
			}
//			LOG.warn("should use DictionaryTerm");
			for (int i = 0; i < entries.size(); i++) {
//				JsonElement element = entries.get(i);
				CMJsonTerm jsonTerm = CMJsonTerm.createTerm(entries.get(i));
				if (jsonTerm == null) {
					throw new RuntimeException("Cannot read as CMJsonTerm");
				}
				Element entry = new Element(DefaultAMIDictionary.ENTRY);
				amiDictionary.dictionaryElement.appendChild(entry);
				String jsonId = jsonTerm.getId();
				if (jsonId == null) {
					throw new RuntimeException("entries must have JsonIds");
				}
				entry.addAttribute(new Attribute(DefaultAMIDictionary.ID, jsonId));
				String termName = jsonTerm.getName();
				if (termName != null) {
					entry.addAttribute(new Attribute(DictionaryTerm.NAME, termName));
				}
				String termTerm = jsonTerm.getTerm();
				if (termTerm == null) {
					throw new RuntimeException("Json dictionary must have term");
				}
				entry.addAttribute(new Attribute(DictionaryTerm.TERM, termTerm));
//				LOG.debug(entry.toXML());
			}
		}
		return amiDictionary;
	}

	public JsonArray getEntries() {
		return entries;
	}

	public void addMixMatchIds(RectangularTable table) {
		Map<String, String> qByExtId = new HashMap<String, String>();
		List<String> ids = table.getColumn(EXT_ID);
		List<String> qs = table.getColumn(Q);
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			String q = qs.get(i);
			qByExtId.put(id, q);
		}
		for (int j = 0; j < entries.size(); j++) {
			CMJsonTerm entry = CMJsonTerm.createTerm(entries.get(j));
			String id = entry.getId();
			String q = qByExtId.get(id);
			if (q != null) {
				entry.addWikidata(q);
			}
		}
	}
	
	/** udpates files with WikidataIds
	 * 
	 * note: deliberately overwrites contents 
	 * 
	 * @param dictionaries
	 * @throws IOException 
	 */
	public void UpdateDictionariesWithMixMatch(List<File> dictionaries, File mapping) {
//		File mapping = new File(AMIFixtures.TEST_DICTIONARY_DIR, "mixmatch.tsv");
		boolean useHeader = true;
		RectangularTable table;
		try {
			table = RectangularTable.readCSVTable(mapping, useHeader);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read mixMatch", e);
		}

		for (File dictionary : dictionaries) {
			CMJsonDictionary cmJsonDictionary;
			try {
				cmJsonDictionary = CMJsonDictionary.readJsonDictionary(FileUtils.readFileToString(dictionary, Charset.forName("UTF-8")));
			} catch (IOException e) {
				throw new RuntimeException("Cannot read dictionary "+dictionary, e);
			}
			cmJsonDictionary.addMixMatchIds(table);
			try {
				FileUtils.write(dictionary, cmJsonDictionary.toString());
			} catch (IOException e) {
				throw new RuntimeException("Cannot write dictionary "+dictionary);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		File dictionaryDir = new File("/Users/pm286/workspace", "dictionaries");
		File jsonDir = new File(dictionaryDir, "json");
		File[] dictionaries = jsonDir.listFiles();
		RectangularTable mapping = RectangularTable.readCSVTable(new File(dictionaryDir, "mixnmatch/2016-09-09.tsv"), true);
		for (File dictionary : dictionaries) {
			CMJsonDictionary cmJsonDictionary = CMJsonDictionary.readJsonDictionary(FileUtils.readFileToString(dictionary, Charset.forName("UTF-8")));
			cmJsonDictionary.addMixMatchIds(mapping);
			File newDictionaryFile = new File(new File(dictionaryDir, "test"), dictionary.getName());
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(cmJsonDictionary);
			FileUtils.write(newDictionaryFile, json);
		}
	}
	
	public JsonObject getDictionaryAsJsonObject() {
		return jsonObject;
	}
		
}
