package org.contentmine.ami.plugins;

import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.regex.RegexComponent;
import org.contentmine.cproject.args.DefaultArgProcessor;

/** holds lexical pattern and name for plugin.
 * 
 * still in development
 * 
 * may end up in plugin
 * 
 * @author pm286
 *
 */
public class NamedPattern {

	
	private static final Logger LOG = LogManager.getLogger(NamedPattern.class);
	private static final String NAME = "name";
protected String name;
	protected Pattern pattern;

	public NamedPattern(String name, String patternString) {
		this.name = name;
		createPattern(patternString);
	}

	public static NamedPattern createFromRegexElement(Element regexElement) {
		NamedPattern namedPattern = null;
		String fields = regexElement.getAttributeValue(RegexComponent.FIELDS);
		if (fields != null) {
			String field[] = fields.trim().split(DefaultArgProcessor.WHITESPACE);
			String name = field.length == 1 ? field[0] : null;
			String patternString = regexElement.getValue();
			if (name != null && patternString != null) {
				namedPattern = new NamedPattern(name, patternString);
			}
		}
		return namedPattern;
	}

	public static NamedPattern createFromValueElement(Element valueElement) {
		NamedPattern namedPattern = null;
		String name = valueElement.getAttributeValue(NAME);
		String patternString = valueElement.getValue();
		if (name != null && patternString != null) {
			namedPattern = new NamedPattern(name, patternString);
		}
		return namedPattern;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getName() {
		return name;
	}

	private void createPattern(String patternString) {
		try {
			this.pattern = Pattern.compile(patternString);
		} catch (Exception e) {
			LOG.debug("Cannot parse regex: "+patternString+"; "+e);
			throw new RuntimeException("BAD REGEX: "+patternString, e);
		}
	}

}
