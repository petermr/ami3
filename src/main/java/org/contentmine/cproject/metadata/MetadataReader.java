package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/** reads metadata such as EPMC and Crossref.
 * 
 * @author pm286
 *
 */
public interface MetadataReader {

	AbstractMetadata readEntry(File metadataFile) throws IOException;
	AbstractMetadata readEntry(InputStream metadataInputStream) throws IOException;

}
