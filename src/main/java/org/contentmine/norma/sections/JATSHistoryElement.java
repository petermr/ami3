package org.contentmine.norma.sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class JATSHistoryElement extends JATSElement implements IsBlock {

	/**
	<history>
		<date date-type="received">
			<day>15</day>
			<month>8</month>
			<year>2011</year>
		</date>
		<date date-type="accepted">
			<day>3</day>
			<month>12</month>
			<year>2011</year>
		</date>
	</history>
	 */
	private static final Logger LOG = LogManager.getLogger(JATSHistoryElement.class);
static final String TAG = "history";
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSDivFactory.DATE,
	});

	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	private List<JATSDateElement> dateList;

	public JATSHistoryElement(Element element) {
		super(element);
	}

	protected void applyNonXMLSemantics() {
		dateList = new ArrayList<JATSDateElement>();
		List<Element> dateElements = XMLUtil.getQueryElements(this, "*[local-name()='"+JATSDateElement.TAG+"']");
		for (Element element : dateElements) {
			dateList.add((JATSDateElement)element);
		}
	}


}
