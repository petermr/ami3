package org.contentmine.cproject.metadata.bibjson;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
"identifier": [{"type":"doi","id":"10.1186/1758-2946-3-47"}]
 * 
 * @author pm286
 *
 */
public class BJIdentifier extends AbstractBibJSON {

	private static final Logger LOG = Logger.getLogger(BJIdentifier.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String type;
	private String id;
	
	public BJIdentifier(String type, String id) {
		this.type = type;
		this.id = id;
	}

}
