package org.contentmine.eucl.pom;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

/** holds Mvn project info (groupId/artifactId/version)
 * 
 * @author pm286
 *
 */
public class MvnProject {
	private static final Logger LOG = Logger.getLogger(MvnProject.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String GROUP_ID = "groupId";
	private static final String VERSION = "version";
	private static final String ARTIFACT_ID = "artifactId";
	
	public String version;
	public String artifactId;
	public String groupId;
	
	public MvnProject() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MvnProject other = (MvnProject) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	public String toString() {
		return this.groupId+"/"+artifactId+"/"+version;
	}

	void addAttributes(Pom pom, Element element) {
		version = pom.getAttributeFor(element, VERSION);
		groupId = pom.getAttributeFor(element, GROUP_ID);
		artifactId = pom.getAttributeFor(element, ARTIFACT_ID);
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getVersion() {
		return version;
	}


}
