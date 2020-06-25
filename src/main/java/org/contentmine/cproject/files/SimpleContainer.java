package org.contentmine.cproject.files;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** simple container to manage branches of the CProject and CTree
 * 
 * @author pm286
 *
 */
public class SimpleContainer extends CContainer {
	private static final Logger LOG = LogManager.getLogger(SimpleContainer.class);
public SimpleContainer(File directory) {
		this.directory = directory;
	}

	@Override
	protected CManifest createManifest() {
		throw new RuntimeException("NYI");
	}
	@Override
	protected void calculateFileAndCTreeLists() {
		throw new RuntimeException("NYI");
	}
	@Override
	protected void getAllowedAndUnknownFiles() {
		throw new RuntimeException("NYI");
	}

}
