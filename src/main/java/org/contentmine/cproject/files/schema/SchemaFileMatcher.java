package org.contentmine.cproject.files.schema;

import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** matchesFies against schema.
 * supports either equality or regex match
 *  
 * @author pm286
 *
 */
public class SchemaFileMatcher {
    

	private static final Logger LOG = Logger.getLogger(SchemaFileMatcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	private AbstractSchemaElement schemaElement;
	private String schemaFilename;
	private Pattern schemaPattern;
	
	public SchemaFileMatcher(AbstractSchemaElement schemaElement) {
		this.schemaElement = schemaElement;
		schemaFilename = schemaElement.getName();
		schemaPattern = schemaElement.getPattern();
	}
	
	/** checks for equality and match against regex.
	 * 
	 * @param filename
	 * @return
	 */
	public boolean matches(String filename) {
		return filename.equals(schemaFilename) || 
				schemaPattern != null && schemaPattern.matcher(filename).matches();
	}


}
