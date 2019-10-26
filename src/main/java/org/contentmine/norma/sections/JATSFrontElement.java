package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

public class JATSFrontElement extends JATSElement implements IsBlock , HasDirectory {

	static String TAG = "front";

	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSDivFactory.JOURNAL_META,
			JATSDivFactory.ARTICLE_META,
			JATSDivFactory.NOTES,
	});
	
	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	private JATSElement journalMeta;
	private JATSArticleMetaElement articleMeta;
	
	public JATSFrontElement(Element element) {
		super(element);
	}

	protected void applyNonXMLSemantics() {
		journalMeta = (JATSElement) this.getSingleChild(JATSJournalMetaElement.TAG);
		articleMeta = (JATSArticleMetaElement) this.getSingleChild(JATSArticleMetaElement.TAG);
	}
	
	public String getPMCID() {
		return articleMeta == null ? null : articleMeta.getPMCID();
	}

	public JATSElement getJournalMeta() {
		return journalMeta;
	}

	public JATSArticleMetaElement getArticleMeta() {
		return articleMeta;
	}

	@Override
	public String directoryName() {
		return TAG;
	}
}
