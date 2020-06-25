package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.JodaDate;

import nu.xom.Attribute;
import nu.xom.Element;

public class JATSDateElement extends JATSElement implements IsBlock {

	/**
	<date date-type="received">
		<day>15</day>
		<month>8</month>
		<year>2011</year>
	</date>
	 */
	private static final Logger LOG = LogManager.getLogger(JATSDateElement.class);
static final String TAG = "date";
	public static final String DATE_TYPE = "date-type";

	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSSpanFactory.DAY,
			JATSSpanFactory.MONTH,
			JATSSpanFactory.YEAR,
	});
	public static final String PUB = "pub";
	
	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	private String day;
	private String month;
	private String year;
	private JodaDate date; // created from day, month, year
	protected String pubType;

	public JATSDateElement() {
		super(TAG);
	}

	public JATSDateElement(String content) {
		this();
		this.appendElement(new JATSStringDateElement(content));
	}

	public JATSDateElement(Element element) {
		super(element);
	}

	protected void applyNonXMLSemantics() {
		day = this.getSingleChildValue(JATSSpanFactory.DAY);
		month = this.getSingleChildValue(JATSSpanFactory.MONTH);
		year = this.getSingleChildValue(JATSSpanFactory.YEAR);
//		JodaDate.parseDate(date, format);
		
	}

	public JATSElement setType(String type) {
		this.addAttribute(new Attribute(DATE_TYPE, type));
		return this;
	}


	

}
