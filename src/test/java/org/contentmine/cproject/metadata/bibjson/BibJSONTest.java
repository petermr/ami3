package org.contentmine.cproject.metadata.bibjson;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BibJSONTest {
	
	private static final Logger LOG = Logger.getLogger(BibJSONTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	Gson gson;
	@Before
	public void setup() {
		gson = new GsonBuilder()
	        .disableHtmlEscaping()
//	        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
	        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
	        .setPrettyPrinting()
//	        .serializeNulls()
	        .create();
	
}

	/**
	 {
    "title": "Open Bibliography for Science, Technology and Medicine",
    "author":[
        {"name": "Richard Jones"},
        {"name": "Mark MacGillivray"},
        {"name": "Peter Murray-Rust"},
        {"name": "Jim Pitman"},
        {"name": "Peter Sefton"},
        {"name": "Ben O'Steen"},
        {"name": "William Waites"}
    ],
    "type": "article",
    "year": "2011",
    "journal": {"name": "Journal of Cheminformatics"},
    "link": [{"url":"http://www.jcheminf.com/content/3/1/47"}],
    "identifier": [{"type":"doi","id":"10.1186/1758-2946-3-47"}]
}

	 */
	@Test
	public void testCreateBibJSONFromObjects() {
		BibJSON bibJson = new BibJSON();
		bibJson.setType("article");
		bibJson.addAuthor("Richard Jones");
        bibJson.addAuthor("Mark MacGillivray");
        bibJson.addAuthor("Peter Murray-Rust");
        bibJson.addAuthor("Jim Pitman");
        bibJson.addAuthor("Peter Sefton");
        bibJson.addAuthor("Ben O'Steen");
        bibJson.addAuthor("William Waites");
        bibJson.setTitle("Open Bibliography for Science, Technology and Medicine");
        bibJson.setDate("2011");
        bibJson.setType("article");
        bibJson.setJournal("Journal of Cheminformatics");
        bibJson.setUrl("http://www.jcheminf.com/content/3/1/47");
        bibJson.setDOI("10.1186/1758-2946-3-47");
        
//		 System.out.println(gson.toJson(bibJson));
	}
	
	@Test
	public void testCreateBibJSONFromJson() {
		String bibJsonString = ""
				+ "{"+
    "\"title\": \"Open Bibliography for Science, Technology and Medicine\","+
    "\"author\":["+
    "    {\"name\": \"Richard Jones\"},"+
    "   {\"name\": \"Mark MacGillivray\"},"+
    "   {\"name\": \"Peter Murray-Rust\"},"+
    "   {\"name\": \"Jim Pitman\"},"+
    "   {\"name\": \"Peter Sefton\"},"+
    "   {\"name\": \"Ben O'Steen\"},"+
    "   {\"name\": \"William Waites\"}"+
    "],"+
    "\"type\": \"article\","+
    "\"date\": \"2011\","+
    "\"journal\": {\"name\": \"Journal of Cheminformatics\"},"+
    "\"link\": [{\"url\":\"http://www.jcheminf.com/content/3/1/47\"}],"+
    "\"identifier\": [{\"type\":\"doi\",\"id\":\"10.1186/1758-2946-3-47\"}]"+
    "}";
		 BibJSON bibJSON = gson.fromJson(bibJsonString, BibJSON.class);
//		 System.out.println(">>>"+bibJSON+"<<<");
	}

	@Test
	public void testCreateBibJSONFromJsonSmall() {
		String bibJsonString = ""
				+ "{"+
    "\"title\": \"Open Bibliography for Science, Technology and Medicine\","+
    "\"author\":["+
//    "    {\"name\": \"Richard Jones\"},"+
//    "   {\"name\": \"Mark MacGillivray\"},"+
//    "   {\"name\": \"Peter Murray-Rust\"},"+
//    "   {\"name\": \"Jim Pitman\"},"+
//    "   {\"name\": \"Peter Sefton\"},"+
//    "   {\"name\": \"Ben O'Steen\"},"+
    "   {\"name\": \"William Waites\"}"+
    "],"+
//    "\"type\": \"article\","+
    // not a field
    "\"year\": \"2011\""+
    ""+
//    "\"journal\": {\"name\": \"Journal of Cheminformatics\"},"+
//    "\"link\": [{\"url\":\"http://www.jcheminf.com/content/3/1/47\"}],"+
//    "\"identifier\": [{\"type\":\"doi\",\"id\":\"10.1186/1758-2946-3-47\"}]"+
    "}";
		 Object obj = gson.fromJson(bibJsonString, BibJSON.class);
//		 System.out.println(obj);
	}

	@Test
	public void testUnknownFields() {
		String bibJsonString = ""
				+ "{"+
    "\"title\": \"Open Bibliography for Science, Technology and Medicine\","+
    "\"author\":["+
    "   {\"name\": \"Author1\","+
    // not a known field
    "    \"foo\": \"bar\""
    + "},"+
    "   {\"name\": \"Author2\"}"+
    "],"+
    "\"journal\": {\"name\": \"Journal of Cheminformatics\"},"+
    // not a known field
    "\"year\": \"2011\""+
    ""+
    "}";

		GsonBuilder builder = new GsonBuilder();
		Map<String, Object> map = (Map<String, Object>) builder.create().fromJson(bibJsonString, Object.class);
		for (String key : map.keySet()) {
			String className = this.getClass().getPackage().getName()+"."+"BJ"+key.substring(0, 1).toUpperCase()+key.substring(1);
			try {
				Class.forName(className);
//				LOG.debug(key+"; "+className);
			} catch (ClassNotFoundException e) {
				LOG.debug("not found: "+key);
			}
			Object obj = map.get(key);
//			LOG.debug("OBJ "+obj.getClass()+"; "+obj);
			if (obj instanceof Map) {
//				LOG.debug("MAP "+obj.getClass()+"; "+obj);
//				LOG.debug("map "+gson.toJson(obj));
			}
		}
//		LOG.debug(gson.toJson(map));
	}
}
