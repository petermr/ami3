package org.contentmine.cproject.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/** superclass of manifest objects.
 * 
 * @author pm286
 *
 */
public abstract class CManifest {

	private static final String MANIFEST = "manifest";
	private static final String MANIFEST_XML = "manifest.xml";

	private File manifestFile;
	private CContainer cContainer;
	private File containerDirectory;
	private Element manifestElement;

	public CManifest(CContainer cContainer) {
		this.cContainer = cContainer;
		getOrCreateProjectAndDirectory();
	}

	private void getOrCreateProjectAndDirectory() {
		if (cContainer == null) {
			throw new RuntimeException("Null cContainer file");
		}
		containerDirectory = cContainer.getDirectory();
		if (containerDirectory == null) {
			throw new RuntimeException("Null directory file for cContainer");
		}
	}

	public File getOrCreateManifestFile() {
		if (manifestFile == null) {
			createManifestFile();
		}
		if (manifestFile.exists()) {
			if (manifestFile.isDirectory()) {
				throw new RuntimeException("Manifest must not be directory: "+manifestFile);
			}
		} else {
			manifestElement = new Element(MANIFEST);
			try {
				FileUtils.write(manifestFile, manifestElement.toXML(), Charset.forName("UTF-8"));
			} catch (IOException e) {
				throw new RuntimeException("Cannot create manifest: "+manifestFile, e);
			}
		}
		return this.manifestFile;
	}
	
	public Element getOrCreateManifestElement() {
		if (manifestElement == null) {
			getOrCreateManifestFile();
			manifestElement = readManifestElementFromManifestFile();
		}
		return manifestElement;
	}

	public Element readManifestElementFromManifestFile() {
		try {
			InputStream is = new FileInputStream(manifestFile);
			manifestElement = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse manifestFile: "+manifestFile);
		}
		return manifestElement;
	}

	private void createManifestFile() {
		manifestFile = new File(containerDirectory, MANIFEST_XML);
	}

	// not yet ready
	public void resetCounts() {
//		List<Element> countNodes = 
	}
}
