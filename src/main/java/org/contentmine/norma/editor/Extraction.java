package org.contentmine.norma.editor;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** holds name-value from Editor matching.
 * 
 * @author pm286
 *
 */
public class Extraction {

	private static final Logger LOG = LogManager.getLogger(Extraction.class);
private String name;
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	private String value;

	public Extraction(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return name+"="+value;
	}

	/** finds Extraction with given name.
	 * 
	 * @param extractionList
	 * @param name
	 * @return
	 */
	public static Extraction find(List<Extraction> extractionList, String name) {
		for (Extraction extraction : extractionList) {
			if (extraction.getName().equals(name)) {
				return extraction;
			}
		}
		return null;
	}

}
