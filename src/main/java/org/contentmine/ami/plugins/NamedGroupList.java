package org.contentmine.ami.plugins;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultElement;

/** the fields matched in a regex mapped to the field names.
 * 
 * @author pm286
 *
 */
public class NamedGroupList {

	
	private static final Logger LOG = Logger.getLogger(NamedGroupList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String POST = "post";
	private static final String PRE = "pre";
	private static final String NAME = "name";
	private static final String VALUE = "value";
	
	private List<NamedGroup> namedGroupList;

	public NamedGroupList() {
		
	}

	public void add(NamedGroup namedGroup) {
		ensureNamedGroupList();
		namedGroupList.add(namedGroup);
			
	}

	private void ensureNamedGroupList() {
		if (namedGroupList == null) {
			namedGroupList = new ArrayList<NamedGroup>();
		}
	}
	
	@Override
	public String toString() {
		return namedGroupList.get(0).toString();
	}

	public ResultElement createResultElement() {
		ResultElement resultElement = new ResultElement();
		int i = 0;
		for (NamedGroup namedGroup : namedGroupList) {
			String name = namedGroup.getName();
			String group = namedGroup.getGroup();
			Attribute att = null;
			if (PRE.equals(name) || POST.equals(name)) {
				att = new Attribute(name, group);
				resultElement.addAttribute(att);
			} else {
				att = new Attribute(NAME+i, name);
				resultElement.addAttribute(att);
				att = new Attribute(VALUE+i, group);
				resultElement.addAttribute(att);
			}
		}
		return resultElement;
	}

}
