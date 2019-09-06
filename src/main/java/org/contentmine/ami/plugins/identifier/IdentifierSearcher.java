package org.contentmine.ami.plugins.identifier;

import org.apache.log4j.Level;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.NamedPattern;

public class IdentifierSearcher extends AMISearcher {

	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public IdentifierSearcher(AMIArgProcessor argProcessor, NamedPattern namePattern) {
		super(argProcessor, namePattern);
	}


}
