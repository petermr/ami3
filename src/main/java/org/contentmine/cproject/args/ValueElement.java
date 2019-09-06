package org.contentmine.cproject.args;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/**
 * Method of parameterising arg's in args.xml.
 * 
 *  child of <arg>.
 * 
 * @author pm286
 *
 */
public class ValueElement extends Element {

	private static final Logger LOG = Logger.getLogger(ValueElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String TAG = "value";
	
	public static final String CLASS_NAME_ATT = "className";
	public static final String FIELDS_ATT = "fields";
	public static final String INPUT_ATT   = "input";
	public static final String LOOKUP_ATT   = "lookup";
	public static final String NAME_ATT   = "name";
	public static final String OUTPUT_ATT   = "output";
	public static final String URL_ATT    = "url";

	public static final String TEXT_CHILD = "#text";
	
	public static final List<String> ATT_NAMES;
	public static final List<String> CHILD_NAMES;
	static {
		ATT_NAMES = new ArrayList<String>();
		ATT_NAMES.add(CLASS_NAME_ATT);
		ATT_NAMES.add(FIELDS_ATT);
		ATT_NAMES.add(INPUT_ATT);
		ATT_NAMES.add(LOOKUP_ATT);
		ATT_NAMES.add(NAME_ATT);
		ATT_NAMES.add(OUTPUT_ATT);
		ATT_NAMES.add(URL_ATT);
		
		CHILD_NAMES = new ArrayList<String>();
		CHILD_NAMES.add(TEXT_CHILD);
	};
	

	public ValueElement() {
		super(TAG);
	}
	
	public static ValueElement createValueElement(Element element) {
		ValueElement valueElement = null;
		if (element != null && TAG.equals(element.getLocalName())) {
			valueElement = new ValueElement();
			XMLUtil.copyAttributes(element, valueElement);
			XMLUtil.transferChildren(element, valueElement);
			XMLUtil.checkAttributeNames(valueElement, ATT_NAMES);
			XMLUtil.checkChildElementNames(valueElement, CHILD_NAMES);
		}
		return valueElement;
	}

	public String getClassName() {
		return this.getAttributeValue(CLASS_NAME_ATT);
	}
	
	public String getInput() {
		return this.getAttributeValue(INPUT_ATT);
	}

	public String getLookup() {
		return this.getAttributeValue(LOOKUP_ATT);
	}

	public String getName() {
		return this.getAttributeValue(NAME_ATT);
	}

	public String getOutput() {
		return this.getAttributeValue(OUTPUT_ATT);
	}

	public String getUrl() {
		return this.getAttributeValue(URL_ATT);
	}

}
