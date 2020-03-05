package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

/**
 * content
 * (journal-meta?, article-meta, (def-list | list | ack | bio | fn-group | glossary | notes)*)
 * 
 * @author pm286
 *
 */
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

	private JATSJournalMetaElement journalMeta;
	private JATSArticleMetaElement articleMeta;
	
	public JATSFrontElement() {
		super(TAG);
	}

	public JATSFrontElement(Element element) {
		super(element);
	}

	protected void applyNonXMLSemantics() {
		journalMeta = (JATSJournalMetaElement) this.getSingleChild(JATSJournalMetaElement.TAG);
		articleMeta = (JATSArticleMetaElement) this.getSingleChild(JATSArticleMetaElement.TAG);
	}
	
	public String getPMCID() {
		return articleMeta == null ? null : articleMeta.getPMCID();
	}

	public JATSElement getJournalMeta() {
		return journalMeta;
	}
	
	public JATSJournalMetaElement getOrCreateSingleJournalMetaChild() {
		journalMeta = (JATSJournalMetaElement) getSingleChild(JATSJournalMetaElement.TAG);
		if (journalMeta ==  null) {
			journalMeta = new JATSJournalMetaElement();
			this.appendElement(journalMeta);
		}
		return journalMeta;
	}

	public JATSArticleMetaElement getArticleMeta() {
		return articleMeta;
	}

	public JATSArticleMetaElement getOrCreateSingleArticleMetaChild() {
		articleMeta = (JATSArticleMetaElement) getSingleChild(JATSArticleMetaElement.TAG);
		if (articleMeta ==  null) {
			articleMeta = new JATSArticleMetaElement();
			this.appendElement(articleMeta);
		}
		return articleMeta;
	}


	@Override
	public String directoryName() {
		return TAG;
	}
}
