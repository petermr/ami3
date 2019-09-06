package org.contentmine.graphics.svg.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** manages named capture groups as Java can't
 * see https://stackoverflow.com/questions/15588903/get-group-names-in-java-regex
 * for some background.
 * All workarounds are dodgy. Here we assume groups are all matched and all in order
 * 
 * @author pm286
 *
 */
public class NamePattern {
	private static final Logger LOG = Logger.getLogger(NamePattern.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static Pattern NAME_REGEX_PATTERN = Pattern.compile("\\?<([A-Za-z]+)>");

	private Pattern pattern;
	private List<String> nameList;
	
	public NamePattern(Pattern pattern, List<String> nameList) {
		this.pattern = pattern;
		this.nameList = nameList;
	}
	
	public static List<String> makeCaptureNameList(String regex) {
		Matcher matcher = NAME_REGEX_PATTERN.matcher(regex);
		List<String> captureNameList = new ArrayList<String>();
		while (matcher.find()) {
			String ss = matcher.group(1);
			captureNameList.add(ss);
		}
		return captureNameList;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public List<String> getNameList() {
		return nameList;
	}

	@Override
	public String toString() {
		return "NamePattern [pattern=" + pattern + ", nameList=" + nameList + "]";
	}
	
	
}
