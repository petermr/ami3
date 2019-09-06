package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class NWKName {
	
	private static final Logger LOG = Logger.getLogger(NWKName.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static Pattern NAME_PATTERN = Pattern.compile("([a-zA-Z \\._]+).*");
	
	private String nameString;

	public NWKName(String s) {
		this.nameString = s;
	}

	public static NWKName createName(StringBuilder sb) {
		NWKName name = null;
		Matcher matcher = NAME_PATTERN.matcher(sb.toString());
		if (matcher.matches()) {
		    name = new NWKName(matcher.group(1));
		    sb.delete(0,  name.length());
		}
		return name;
	}

	public String getValue() {
		return nameString;
	}

	public String toString() {
		return "[name: "+String.valueOf(nameString)+"]";
	}

	public int length() {
		return nameString == null ? 0 : nameString.length();
	}
	
	public void createNewick(StringBuilder sb) {
		if (nameString != null) {
			sb.append(nameString);
		}
	}

	public String getNameString() {
		return nameString;
	}


}
