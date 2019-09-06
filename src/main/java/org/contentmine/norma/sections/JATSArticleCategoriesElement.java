package org.contentmine.norma.sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class JATSArticleCategoriesElement extends JATSElement {

	private static final Logger LOG = Logger.getLogger(JATSArticleCategoriesElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/**
	<article-categories>
		<subj-group subj-group-type="heading">
			<subject>Research Article</subject>
		</subj-group>
		<subj-group subj-group-type="Discipline-v2">
			<subject>Medicine</subject>
			<subj-group> ...
			</subj-group>
		</subj-group>
	</article-categories>
	 */
	final static String TAG = "article-categories";

	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSDivFactory.SUBJ_GROUP,
	});

	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}
	
	private List<JATSSubjGroupElement> subjGroupList;

	public JATSArticleCategoriesElement(Element element) {
		super(element);
	}
	
	protected void applyNonXMLSemantics() {
		subjGroupList = new ArrayList<JATSSubjGroupElement>();
		List<Element> contribElements = XMLUtil.getQueryElements(this, "*[local-name()='"+JATSSubjGroupElement.TAG+"']");
		for (Element element : contribElements) {
			JATSSubjGroupElement contribElement = (JATSSubjGroupElement)element;
			subjGroupList.add((JATSSubjGroupElement)element);
		}
	}

	
}
