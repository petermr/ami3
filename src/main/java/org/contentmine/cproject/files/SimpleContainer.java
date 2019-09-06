package org.contentmine.cproject.files;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** simple container to manage branches of the CProject and CTree
 * 
 * @author pm286
 *
 */
public class SimpleContainer extends CContainer {
	private static final Logger LOG = Logger.getLogger(SimpleContainer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
