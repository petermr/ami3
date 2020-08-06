package org.contentmine.ami.tools.dictionary;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

/** transform dictionary
 * 
 * @author pm286
 *
 */
public class DictionaryTransformer {

	private static Pattern TRANSFORM_PATTERN = Pattern.compile("(EXTRACT|JUNK)\\(([^,]+),(.*)\\)");
	
	private String operation;
	private String variableName;
	private Pattern pattern;
	private String newVariableName;
	
	public DictionaryTransformer(String newVariableName, String rawTransform) {
		this.newVariableName = newVariableName;
		readAndParse(rawTransform);
	}

	private void readAndParse(String rawTransform) {
//		System.out.println(TRANSFORM_PATTERN);
		Matcher matcher = TRANSFORM_PATTERN.matcher(rawTransform);
		if (!matcher.matches()) {
			throw new RuntimeException("bad rawTransform "+rawTransform);
		}
		operation = matcher.group(1);
		variableName = matcher.group(2);
		String regexString = matcher.group(3);
		pattern = Pattern.compile(regexString);
	}
	
	public String getOperation() {
		return operation;
	}

	public String getVariableName() {
		return variableName;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void transform(SimpleDictionary simpleDictionary) {
		List<Element> entryList = simpleDictionary.getEntryList();
		for (Element entry : entryList) {
//			System.out.println(variableName + ": "+entry.toXML());
			String value = entry.getAttributeValue(variableName);
//			System.out.println("pattern: "+pattern+" value: "+value);
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) {
				String newValue = matcher.group(1);
//				System.out.println("g "+newValue);
				entry.addAttribute(new Attribute(newVariableName, newValue));
				System.out.println(entry.toXML());
			}
		}
	}


}
