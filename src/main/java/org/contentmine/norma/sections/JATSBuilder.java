package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** build JATS elements from components
 * can take non-JATS inputs (e.g. HTML)
 * 
 * @author pm286
 *
 */
public class JATSBuilder {
	private static final Logger LOG = Logger.getLogger(JATSBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum BuilderType {
		HTML,
		JATS
		;
	}

	private BuilderType type;

	public void setType(BuilderType type) {
		this.type = type;
	}

}
