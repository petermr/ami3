package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BlackList {

	private static final Logger LOG = LogManager.getLogger(BlackList.class);
private List<Pattern> omitRegexList;
	public BlackList() {
		
	}
	public BlackList(File file) {
		readBlackList(file);
	}
	private void readBlackList(File file) {
		omitRegexList = new ArrayList<Pattern>();
		try {
			List<String> lines = FileUtils.readLines(file);
			for (String line : lines) {
				line = line.split("\\#")[0].trim();
				if (line.length() > 0) {
					Pattern omitRegex = Pattern.compile(line);
					omitRegexList.add(omitRegex);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Bad blacklist: "+file, e);
		}
	}
	
	public List<String> omitLines(List<String> lines) {
		List<String> filtered = new ArrayList<String>();
		for (String line : lines) {
			if (!omit(line)) {
				filtered.add(line);
			}
		}
		return filtered;
	}
	private boolean omit(String line) {
		for (Pattern omitRegex : omitRegexList) {
			if (omitRegex.matcher(line).matches()) {
				LOG.trace("omit: "+line);
				return true;
			}
		}
		return false;
	}

}
