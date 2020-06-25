package org.contentmine.ami.tools.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Node;

public abstract class AbstractTTElement extends Element {
	private static final Logger LOG = LogManager.getLogger(AbstractTTElement.class);
public static final String NAME = "name";
	public static final String ID = "id";

	
	protected TTemplateList templateList;

	protected AbstractTTElement(String tag, TTemplateList templateList) {
		super(tag);
		this.templateList = templateList;
	}
	
	public static AbstractTTElement create(Element element, TTemplateList templateList) {
		AbstractTTElement ttElement = null;
		String tag = element.getLocalName();
		if (false) {
		} else if(CellMatcher.TAG.equalsIgnoreCase(tag)) {
			ttElement = new CellMatcher(templateList);
		} else if(ColumnMatcher.TAG.equalsIgnoreCase(tag)) {
			ttElement = new ColumnMatcher(templateList);
		} else if(FileMatcher.TAG.equalsIgnoreCase(tag)) {
			ttElement = new FileMatcher(templateList);
		} else if(FooterMatcher.TAG.equalsIgnoreCase(tag)) {
			ttElement = new FooterMatcher(templateList);
		} else if(TitleMatcher.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TitleMatcher(templateList);
		} else if(TTemplate.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTemplate(templateList);
		} else if(TTemplateList.TAG.equalsIgnoreCase(tag)) {
			if (templateList != null) {
				throw new RuntimeException("can only have one templateList in tree");
			}
			ttElement = new TTemplateList();
			templateList = (TTemplateList)ttElement;
		} else if(QueryMatcher.TAG.equalsIgnoreCase(tag)) {
			ttElement = new QueryMatcher(templateList);
		} else if(VariableMatcher.TAG.equalsIgnoreCase(tag)) {
			ttElement = new VariableMatcher(templateList);

		} else {
			throw new RuntimeException("Unknown tag "+tag);
		}
		XMLUtil.copyAttributes(element, ttElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				AbstractTTElement tableChild = 
						AbstractTTElement.create((Element)child, templateList);
				if (ttElement != null) {	
					ttElement.appendChild(tableChild);
				}
			} else {
				ttElement.appendChild(child.copy());
			}
		}
		return ttElement;
	}

	public TTemplateList getTemplateList() {
		return this.templateList;
	}
	public String getName() {
		return getNonNullAttribute(this, NAME);
	}

	private String getNonNullAttribute(Element element, String name) {
		String value = element.getAttributeValue(name);
		if (value == null) {
			throw new RuntimeException("require attribute: "+name+" on element: "+element.getLocalName());
		}
		return value;
	}

	public String getId() {
		return this.getAttributeValue(ID);
	}


			
}
