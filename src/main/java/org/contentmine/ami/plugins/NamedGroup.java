package org.contentmine.ami.plugins;

import nu.xom.Attribute;
import nu.xom.IllegalNameException;

/** holds a named captureGroup.
 * 
 * @author pm286
 *
 */
public class NamedGroup {

	private String name;
	private String group;

	public NamedGroup(String name, String group) {
		this.name = name;
		this.group = group;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGroup() {
		return group;
	}
	
	public String toString() {
		return name+" = "+group;
	}

	public Attribute createAttribute() {
		Attribute att = null;
		if (group != null) {
			try {
				att = new Attribute(name, group);
			} catch (IllegalNameException e) {
				throw new RuntimeException("cannot make attribute "+name+"="+group, e);
			}
		}
		return att;
	}

}
