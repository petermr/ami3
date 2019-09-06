package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResultsJsonTest {

	private static final Logger LOG = Logger.getLogger(ResultsJsonTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testReadResultsJson() throws IOException {
		File file = new File(CMineFixtures.TEST_FILES_DIR, CMineFixtures.QUICKSCRAPE_MD);
		LOG.debug(file.getAbsolutePath());
		CTree ctree = new CTree(file);
		File resultsJson = ctree.getExistingQuickscrapeMD();
		Assert.assertNotNull("QMD is null: "+file, resultsJson);
		String resultsJsonString = FileUtils.readFileToString(resultsJson, Charset.forName("UTF-8"));
	    JsonParser parser = new JsonParser();
	    JsonObject jsonObject = (JsonObject) parser.parse(resultsJsonString);
		Assert.assertEquals("{\"value\":[\"Trials\"]}", jsonObject.get("journal").toString());
	}
	
	@Test
	public void testReadResultsJsonKeys() throws IOException {
		File file = new File(CMineFixtures.TEST_FILES_DIR, CMineFixtures.QUICKSCRAPE_MD);
		Assert.assertNotNull("file "+file, file);
		String resultsJsonString = readResultsJsonString(file);
	    JsonParser parser = new JsonParser();
	    JsonObject jsonObject = (JsonObject) parser.parse(resultsJsonString);
	    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
	    Assert.assertEquals(19,  entrySet.size());
	    Iterator<Map.Entry<String, JsonElement>> entryIterator = entrySet.iterator();
	    Map<String, String> valueByKey = new HashMap<String, String>();
	    while(entryIterator.hasNext()) {
	    	Map.Entry<String, JsonElement> entry = entryIterator.next();
	    	String key = entry.getKey();
	    	JsonArray array = ((JsonObject)entry.getValue()).get("value").getAsJsonArray();
	    	if (array.size() == 0) {
	    		LOG.trace(key+"=null");
	    		continue;
	    	} else if (array.size() == 1) {
	    		String value = array.get(0).getAsString();
	    		LOG.trace(key+"="+value);
	    		valueByKey.put(key, value);
	    	} else {
	    		LOG.trace(key+"="+array.size()+"/"+array);
	    	}
	    }
	    Assert.assertEquals(11, valueByKey.size());
	}

	private String readResultsJsonString(File file) throws IOException {
		String resultsJsonString = null;
		CTree ctree = new CTree(file);
		File resultsJson = ctree.getExistingQuickscrapeMD();
		if (resultsJson != null) {
			resultsJsonString = FileUtils.readFileToString(resultsJson, Charset.forName("UTF-8"));
		}
		return resultsJsonString;
	}
	
}
