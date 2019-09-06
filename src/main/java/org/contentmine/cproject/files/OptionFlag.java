package org.contentmine.cproject.files;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** flags to add options or qualifucations to default PluginOptions.
 * 
 * @author pm286
 *
 */
public class OptionFlag {
	
	private static final Logger LOG = Logger.getLogger(OptionFlag.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static Pattern FLAG_PATTERN = Pattern.compile("([a-zA-Z\\.]+):(.*)");
	private String key;
	private String value;
	
	public OptionFlag(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public static List<OptionFlag> createOptionFlags(List<String> flags) {
		List<OptionFlag> optionFlags = new ArrayList<OptionFlag>();
		for (String flag : flags) {
			if (!flag.trim().equals("")) {
				Matcher matcher = FLAG_PATTERN.matcher(flag);
				if (!matcher.matches()) {
					throw new RuntimeException("Flag must match: "+FLAG_PATTERN+"; found: "+flag);
				}
				OptionFlag optionFlag = new OptionFlag(matcher.group(1), matcher.group(2));
				LOG.trace("adding: "+optionFlag);
				optionFlags.add(optionFlag);
			}
		}
		return optionFlags;
	}
	
	public String toString() {
		return "{"+key+":"+value+"}";
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}
	

}
