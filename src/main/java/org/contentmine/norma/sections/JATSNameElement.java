package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Util;

import nu.xom.Element;

/**
    <name>
     <surname>Weinbren</surname>
     <given-names>MP</given-names>
    </name>
 * @author pm286
 *
 */
public class JATSNameElement extends JATSElement implements IsBlock {

	private static final Logger LOG = LogManager.getLogger(JATSNameElement.class);
static final String TAG = "name";

	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSSpanFactory.SURNAME,
			JATSSpanFactory.GIVEN_NAMES,
			JATSSpanFactory.PREFIX,
			JATSSpanFactory.SUFFIX,
	});

	private String surname;
	private String givenNames;
	private String suffix;
	
	public JATSNameElement(Element element) {
		super(element);
	}
	
	public String getSurname() {
		return surname;
	}
	
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	public String getGivenNames() {
		return givenNames;
	}

	public String getSuffix() {
		return suffix;
	}

	protected void applyNonXMLSemantics() {
		this.surname = this.getSingleChildValue(JATSSurnameElement.TAG);
		this.givenNames = this.getSingleChildValue(JATSGivenNamesElement.TAG);
		this.suffix = this.getSingleChildValue(JATSSpanFactory.LABEL);
	}
	
	@Override
	public String debugString(int level) {
		StringBuilder sb = new StringBuilder();
		addNonNull(sb, getSingleChildValue(JATSSurnameElement.TAG));
		sb.append(" ");
		addNonNull(sb, getSingleChildValue(JATSGivenNamesElement.TAG));
		return /*Util.spaces(level)+*/ /*TAG+":"+*/sb.toString();
		
	}


}
