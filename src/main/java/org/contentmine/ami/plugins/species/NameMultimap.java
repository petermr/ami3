package org.contentmine.ami.plugins.species;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.NAConstants;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class NameMultimap {

	public static final Logger LOG = LogManager.getLogger(NameMultimap.class);
private static final String SPECIES_LIST = NAConstants.PLUGINS_SPECIES+"/speciesFromLinnaeus.tsv";
	
	private Multimap<String, String> nameByKeyMap;
	private Multimap<String, String> keyByNameMap;
	
	public NameMultimap() {
		ensureMultimap();
	}
	
	private void ensureMultimap() {
		if (nameByKeyMap == null) {
			nameByKeyMap = ArrayListMultimap.create();
			keyByNameMap = ArrayListMultimap.create();
			readTSVIntoMap(SPECIES_LIST);
		}
	}

	private void readTSVIntoMap(String resourceName) {
		InputStream inputStream = this.getClass().getResourceAsStream(resourceName);
		if (inputStream == null) {
			throw new RuntimeException("Can't find stream "+resourceName);
		}
		try {
			List<String> lines = IOUtils.readLines(inputStream, NAConstants.UTF_8);
			for (String line : lines) {
				addLine(line);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot read species list: "+SPECIES_LIST);
		}
		LOG.trace(nameByKeyMap.keySet().size());
		LOG.trace(keyByNameMap.keySet().size());
	}

	private void addLine(String line) {
		int idx = line.indexOf("\t");
		if (idx == -1) {
			throw new RuntimeException("No tab: "+line);
		}
		String key = line.substring(0, idx).trim();
		String[] names = line.substring(idx).trim().split("\\|");
		Set<String> nameSet = new HashSet<String>();
		for (String name : names) {
			name = name.trim();
			if (name.length() < 5) continue;
			Matcher matcher = Pattern.compile("(.*)\\s+(\\(|subsp\\.|sp\\.).*").matcher(name);
			if (matcher.matches()) {
				name = matcher.group(1).trim();
			}
			if (Pattern.compile("([A-Z][a-z]+)").matcher(name).matches()) {
				// Abcdef
			} else if (Pattern.compile("([A-Z]([a-z]?\\.|[a-z]+)(\\s+[a-z]+)(\\s[a-z])*)").matcher(name).matches()) {
				// Abcdef or A. or Ab.
			} else {
				continue;
			}
			if (!nameSet.contains(name)) { 
				keyByNameMap.put(name, key);
				nameByKeyMap.put(key, name);
				nameSet.add(name);
			}
		}
	}

	public List<String> searchByKey(String key) {
		ensureMultimap();
		return new ArrayList<String>(nameByKeyMap.get(key));
	}

	public List<String> searchByNameValue(String nameValue) {
		ensureMultimap();
		return new ArrayList<String>(keyByNameMap.get(nameValue));
	}

	public List<String> getNames() {
		return new ArrayList<String>(keyByNameMap.keySet());
	}
}
