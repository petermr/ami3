package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ScraperSet {

	public static final Logger LOG = Logger.getLogger(ScraperSet.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String JSON = ".json";
	public static final String ELEMENTS = "elements";

	public static final String SCRAPERS_CSV = "scrapers.csv";

	private File directory;
	private List<File> scraperFiles;
	private Multiset<String> scraperKeys;
	private List<Multiset.Entry<String>> sortedKeys;
	private Map<File, JsonElement> jsonElementByFile;

	public ScraperSet() {
	}

	public ScraperSet(File dir) {
		this();
		this.directory = dir;
		readScraperFiles();
	}
	
	private void readScraperFiles() {
		File[] scrapers = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name != null && name.endsWith(JSON);
			}
		});
		scraperFiles = scrapers == null ? new ArrayList<File>() : Arrays.asList(scrapers);
	}

	public Multiset<String> getOrCreateScraperKeys() {
		if (scraperKeys == null) {
			readScraperFiles();
			scraperKeys = HashMultiset.create();
			jsonElementByFile = new HashMap<File, JsonElement>();
			for (File scraperFile : scraperFiles) {
				JsonElement jsonElement = null;
				try {
					jsonElement = new JsonParser().parse(FileUtils.readFileToString(scraperFile, Charset.forName("UTF-8")));
				} catch (Exception e) {
					LOG.debug("Cannot read file: "+scraperFile+"; "+e);
					continue;
				}
				jsonElementByFile.put(scraperFile, jsonElement);
				JsonObject elementsObject  = jsonElement.getAsJsonObject().get(AbstractMetadata.SCRAPER_ELEMENTS).getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entries = elementsObject.entrySet();
				for (Map.Entry<String, JsonElement> entry : entries) {
					scraperKeys.add(entry.getKey());
				}
			}
		}
		return scraperKeys;
	}
	
	public List<Multiset.Entry<String>> getOrCreateScraperElementsByCount() {
		sortedKeys = CMineUtil.getEntryListSortedByCount(scraperKeys);
		return sortedKeys;
	}

	public List<File> getScraperFiles() {
		getOrCreateScraperKeys();
		return scraperFiles;
	}

	public Map<File, JsonElement> getJsonElementByFile() {
		getOrCreateScraperKeys();
		return jsonElementByFile;
	}



}
