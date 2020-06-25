package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class NWKLength {

	/**
	   Length --> empty | ":" number
	 */
	
	private static final Logger LOG = LogManager.getLogger(NWKLength.class);
final static Pattern LENGTH_PATTERN = Pattern.compile("\\s*\\:([\\+\\-]?[0-9]+\\.?[0-9]*).*");

	private Double length;

	public NWKLength(String s) {
		LOG.trace("length: "+s);
		this.length = Double.parseDouble(s);
	}

	public NWKLength(Double length) {
		this.length = length;
	}

	public static NWKLength createLengthAndEatSB(StringBuilder sb) {
		NWKLength length = null;
		Matcher matcher = LENGTH_PATTERN.matcher(sb.toString());
		if (matcher.matches()) {
			NWKTree.trim(sb);
			sb.deleteCharAt(0); // remove ":"
			String lengthString = matcher.group(1);
			Double lengthValue = null;
			try {
				lengthValue = Double.parseDouble(lengthString);
			} catch (NumberFormatException e) {
				LOG.error("length is not a number: "+lengthString);
			}
			sb.delete(0, lengthString.length());
			if (lengthValue != null) {
				length = new NWKLength(lengthValue);
			}
		}
		return length;
	}


	public String toString() {
		return "(len: "+String.valueOf(length)+")";
	}

	public void createNewick(StringBuilder sb) {
		if (length != null) {
			sb.append(":"+length);
		}
	}
	
	public Double getLength() {
		return length;
	}
}
