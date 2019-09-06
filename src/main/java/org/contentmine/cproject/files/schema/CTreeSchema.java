package org.contentmine.cproject.files.schema;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CTreeSchema extends AbstractSchemaElement {
	private static final Logger LOG = Logger.getLogger(CTreeSchema.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "cTree";
	
	public CTreeSchema() {
		super(TAG);
	}
	
	
}
