package org.contentmine.cproject.files.schema;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.schema.AbstractSchemaElement.IteratorType;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/** iterates over the schema for filenames.
 * This provides filenames against which the actual directory contents can be assessed.
 * 
 * @author pm286
 *
 */
public class FileSet  {
	private static final Logger LOG = LogManager.getLogger(FileSet.class);
private IteratorType iteratorType;
	private List<File> fileList;

	private FileSet() {
	}
	
	public FileSet(List<File> fileList) {
		this.fileList = fileList;
	}

}
