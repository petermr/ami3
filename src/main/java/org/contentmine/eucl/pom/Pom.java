package org.contentmine.eucl.pom;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.JodaDate;
import org.contentmine.eucl.xml.XMLUtil;
import org.joda.time.DateTime;

import nu.xom.Element;

/** represents a POM file. 
 * limited functionality - mainly version numbers and dependencies
 * This shouldn't have to be written :-(
 * 
 * @author pm286
 *
 */
public class Pom extends Element {
	
	private static final String PROPERTIES = "properties";
	private static final Logger LOG = Logger.getLogger(Pom.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String DEPENDENCY = "dependency";
	private static final String DEPENDENCIES = "dependencies";
	private static final String POM = "pom";
	private static final String PARENT = "parent";
	
	private File file;
	private DateTime dateTime;
	private MvnProject mvnProject;
	private List<MvnProject> dependencies;
	private Map<String, String> properties;

	private Pom() {
		super(POM);
	}
	
	public Pom(File file) {
		this();
		setFile(file);
		parseFile(file);
	}

	private void parseFile(File file) {
		Element elem = XMLUtil.parseQuietlyToDocument(file).getRootElement();
		XMLUtil.transferChildren(elem, this);
		XMLUtil.copyAttributes(elem, this);
		mvnProject = createProject(this);
	}


	private void setFile(File file) {
		this.file = file;
		getDateTime();
	}

	public DateTime getDateTime() {
		if (dateTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = sdf.format(file.lastModified()).replace(" ", "T")+"Z";
			dateTime = JodaDate.parseDate(dateString);
		}
		return dateTime;
	}
	
	public MvnProject getMvnProject() {
		return mvnProject;
	}
	
	public List<MvnProject> getOrCreateDependencies() {
		if (dependencies == null) {
			dependencies = new ArrayList<MvnProject>();
			List<Element> dependencyElements = XMLUtil.getQueryElements(this, 
					".//*[local-name()='"+DEPENDENCIES+"']/*[local-name()='"+DEPENDENCY+"']");
			for (Element dependencyElement : dependencyElements) {
				MvnProject dependency = createProject(dependencyElement);
				if (dependency == null) {
					LOG.error("Cannot parse Dependency: "+dependencyElement.toXML());
				} else {
					dependencies.add(dependency);
				}
			}
		}
		return dependencies;
	}

	public MvnProject getParentPom() {
		Element parentElement = XMLUtil.getSingleElement(this, ".//*[local-name()='"+PARENT+"']");
		MvnProject parent = createProject(parentElement);
		return parent;
	}

	public MvnProject getDependency(String groupId, String artifactId) {
		getOrCreateDependencies();
		for (MvnProject dependency : dependencies) {
			if (dependency.getArtifactId().equals(artifactId) && dependency.getGroupId().equals(groupId)) {
				return dependency;
			}
		}
		return null;
	}

	public Map<String, String> getOrCreateProperties() {
		if (properties == null) {
			properties = new HashMap<String, String>();
			List<Element> propertyList = XMLUtil.getQueryElements(this, "./*[local-name()='" + PROPERTIES + "']/*");
			for (Element property : propertyList) {
				String name = property.getLocalName();
				String value = property.getValue().trim();
				properties.put(name, value);
			}
		}
		return properties;
	}
	
	public String getValue(String property) {
		getOrCreateProperties();
		return properties.get(property);
	}

	String getValueFromStringOrSymbol(String value) {
		value = value.trim();
		if (value.startsWith("${") && value.endsWith("}")) {
			value = value.substring(2, value.length() - 1);
			value = getValue(value);
		}
		return value;
	}

	String getAttributeFor(Element element, String name) {
		String value = XMLUtil.getSingleValue(element, "./*[local-name()='" + name + "']");
		if (value == null) {
			throw new RuntimeException("Cannot find "+name);
		}
		value = getValueFromStringOrSymbol(value);
		return value;
	}

	public MvnProject createProject(Element element) {
		MvnProject project = null;
		if (element != null) {
			project = new MvnProject();
			project.addAttributes(this, element);
		}
		return project;
	}
		
	public String toString() {
		return mvnProject == null ? null : mvnProject.toString();
	}
	
}
