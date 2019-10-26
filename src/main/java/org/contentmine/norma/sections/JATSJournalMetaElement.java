package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class JATSJournalMetaElement extends JATSElement implements IsBlock, HasDirectory {

	public static final String PUBLISHER_NAME = "publisher-name";
	public static final String PUBLISHER = "publisher";
	public static final String JOURNAL_TITLE_GROUP = "journal-title-group";
	public static final String JOURNAL_TITLE = "journal-title";
	/**
		<journal-meta>
			<journal-id journal-id-type="nlm-ta">PLoS Negl Trop Dis</journal-id>
			<journal-id journal-id-type="publisher-id">plos</journal-id>
			<journal-id journal-id-type="pmc">plosntds</journal-id>
			<journal-title-group>
				<journal-title>PLoS Neglected Tropical Diseases</journal-title>
			</journal-title-group>
			<issn pub-type="ppub">1935-2727</issn>
			<issn pub-type="epub">1935-2735</issn>
			<publisher>
				<publisher-name>Public Library of Science</publisher-name>
				<publisher-loc>San Francisco, USA</publisher-loc>
			</publisher>
		</journal-meta>
	 */
	static String TAG = "journal-meta";
	
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSSpanFactory.ISSN,
			JATSSpanFactory.JOURNAL_ID,
			JATSDivFactory.JOURNAL_TITLE_GROUP,
			JATSDivFactory.PUBLISHER,
			JATSSpanFactory.JOURNAL_TITLE,
			JATSSpanFactory.ISSN_L,
	});


	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}


	public JATSJournalMetaElement(Element element) {
		super(element);
	}

	public String getJournalTitle() {
		String journalTitle = getSingleValue("./*[local-name()='"+JOURNAL_TITLE_GROUP+"']/*[local-name()='"+JOURNAL_TITLE+"']");
		return journalTitle == null ? "" : journalTitle;
	}

	public String getPublisher() {
		String publisher = getSingleValue("./*[local-name()='"+PUBLISHER+"']/*[local-name()='"+PUBLISHER_NAME+"']");
		return publisher == null ? "" : publisher;
	}
	
	@Override
	public String debugString(int level) {
		StringBuilder sb = new StringBuilder();
		addNonNull(sb, getJournalTitle());
		sb.append("/");
		addNonNull(sb, getPublisher());
		return Util.spaces(level)+TAG+":"+sb.toString()+"\n";
	}

	public String directoryName() {
		return this.TAG;
	}


}
