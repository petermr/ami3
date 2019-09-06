package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

/**
 * 		<article-meta>
			<article-id pub-id-type="pmcid">3289602</article-id>
			...
			<title-group>
				<article-title>Genetic Characterization of Zika Virus Strains:
					Geographic Expansion of the Asian Lineage</article-title>
				<alt-title alt-title-type="running-head">Zika Virus Expansion in Asia</alt-title>
			</title-group>

 * @author pm286
 *
 */
public class JATSArticleTitleGroupElement extends JATSElement {

	/** NOTE!! this should really be called article-title-group but it seems to
	 * be unique in the document
	 */
	
	static String TAG = "title-group";
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSSpanFactory.ARTICLE_TITLE,
	});

	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}


	public JATSArticleTitleGroupElement(Element element) {
		super(element);
	}


}
