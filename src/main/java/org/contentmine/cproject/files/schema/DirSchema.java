package org.contentmine.cproject.files.schema;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DirSchema extends AbstractSchemaElement {
	private static final Logger LOG = Logger.getLogger(DirSchema.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "dir";
	
	public DirSchema() {
		super(TAG);
	}
	
	
}
