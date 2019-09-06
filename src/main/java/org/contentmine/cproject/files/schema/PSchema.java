package org.contentmine.cproject.files.schema;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class PSchema extends AbstractSchemaElement {
	private static final Logger LOG = Logger.getLogger(PSchema.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "p";
	
	public PSchema() {
		super(TAG);
	}
	
	
}
