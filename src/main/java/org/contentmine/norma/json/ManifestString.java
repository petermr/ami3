package org.contentmine.norma.json;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** holds a string value with a key.
 *  
 * @author pm286
 *
 */
public class ManifestString {

	
	private static final Logger LOG = LogManager.getLogger(ManifestString.class);
private String key;
	private String value;

	public ManifestString(String key) {
		this.setKey(key);
	}

	private void setKey(String key) {
		this.key = key;
	}

	public ManifestString(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	private void setValue(String value) {
		this.value = value;
	}


}
