package org.contentmine.norma;

import org.contentmine.norma.pubstyle.DefaultPubstyleReader;

public class DefaultPubstyle extends Pubstyle {
	
	public DefaultPubstyle() {
		super("default", "defaultTaggerLocation", new DefaultPubstyleReader());
	}

}
