package org.contentmine.cproject.args;

import org.apache.log4j.Logger;

import nu.xom.Element;

/** this isn't working properly.
 * The class hierarchy and containment hierarchy are muddled, so
 * only the immediate instance retains the correct version
 * 
 * @author pm286
 *
 */
public class VersionManager {

	private static final Logger LOG = Logger.getLogger(VersionManager.class);
	static {LOG.setLevel(org.apache.log4j.Level.DEBUG);}

	private static final String VERSION = "version";
	private static final String NAME    = "name";
	
	// these are private so each level can have its own version and name
	private static String name;
	private static String version;
	
	public VersionManager() {
	}

	private void setNameVersion(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public void readNameVersion(Element argListElement) {
		setNameVersion(
				argListElement.getAttributeValue(NAME), 
				argListElement.getAttributeValue(VERSION));
	}

	public void printVersion() {
		System.err.println(getVersionString());
	}

	protected String getVersionString() {
		return ""+name + "("+version+")";
	}
	
	/**
http://stackoverflow.com/questions/2712970/get-maven-artifact-version-at-runtime
	 * @return
	 */
	public String getVersionNumber() {
		String version = this.getClass().getPackage().getImplementationVersion();
		if (version != null) {
			System.err.println("Version: "+version);
		}
		return version;
	}
}
