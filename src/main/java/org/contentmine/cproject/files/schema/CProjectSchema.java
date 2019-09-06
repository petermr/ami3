package org.contentmine.cproject.files.schema;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CProjectSchema extends AbstractSchemaElement {
	private static final Logger LOG = Logger.getLogger(CProjectSchema.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "cProject";
	
	public CProjectSchema() {
		super(TAG);
	}
	
}
