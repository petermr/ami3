package org.contentmine.cproject.files.schema;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class FileSchema extends AbstractSchemaElement {
	private static final Logger LOG = Logger.getLogger(FileSchema.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "file";
	
	public FileSchema() {
		super(TAG);
	}
	
	
}
