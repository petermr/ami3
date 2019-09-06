package org.xmlcml.args;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** holds symbolic variabls for processing input.
 * 
 * @author pm286
 *
 */
@Deprecated

public class VariableProcessor {
	
	
	private static final Logger LOG = Logger.getLogger(VariableProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	// ${ ... }
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{[^\\}]*\\}");
	private HashMap<String, String> variableByNameMap;

	public VariableProcessor() {
		this.variableByNameMap = new HashMap<String, String>();
	}
	
	public String getVariable(String name) {
		return variableByNameMap.get(name);
	}

	public boolean addVariableAndExpandReferences(String name, String value) {
		boolean added = true;
		if (variableByNameMap.containsKey(name)) {
			LOG.error("Duplicate variable name: "+name);
			added = false;
		} else {
			try {
				value = substituteVariables(value);
			} catch (Exception e) {
				added = false;
			}
		}
		return added;
	}

	public String substituteVariables(String value) {
		StringBuilder sb = new StringBuilder();
		Matcher matcher = VARIABLE_PATTERN.matcher(value);
		int start = 0;
		int end = 0;
		while (matcher.find()) {
			start = matcher.start();
			sb.append(value.substring(end, start));
			end = matcher.end();
			String variableRef = value.substring(start + 2, end - 1);
			String variableValue = variableByNameMap.get(variableRef);
			if (variableValue == null) {
				throw new RuntimeException("Cannot resolve variable ${"+variableRef+"} in "+value);
			}
			sb.append(variableValue);
		}
		sb.append(value.substring(end));
		return sb.toString();
	}

}
