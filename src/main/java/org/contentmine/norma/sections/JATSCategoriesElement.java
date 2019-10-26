package org.contentmine.norma.sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class JATSCategoriesElement extends JATSElement implements IsBlock, IsNonStandard {

	private static final Logger LOG = Logger.getLogger(JATSCategoriesElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	final static String TAG = "contribGroup";

	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSDivFactory.CONTRIB,
	});

	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}
	
	private List<JATSContribElement> contribList;
	private String contribType;

	public JATSCategoriesElement(Element element) {
		super(element);
	}
	
	protected void applyNonXMLSemantics() {
		contribList = new ArrayList<JATSContribElement>();
		contribType = null;
		List<Element> contribElements = XMLUtil.getQueryElements(this, "*[local-name()='"+JATSContribElement.TAG+"']");
		boolean first = true;
		for (Element element : contribElements) {
			JATSContribElement contribElement = (JATSContribElement)element;
			String contribType1 = contribElement.getContribType();
			if (first) {
				if (contribType1 != null) {
					contribType = contribType1;
				}
				first = false;
			} else if (contribType == null || (contribType != contribType1)) {
				LOG.warn("inconsistent contribTypes " + contribType1 + " != " + contribType);
				contribType = null;
			}
			contribList.add((JATSContribElement)element);
		}
	}

	
}
