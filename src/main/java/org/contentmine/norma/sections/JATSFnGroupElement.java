package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

public class JATSFnGroupElement extends JATSElement implements IsBlock {

	/**
	 <fn-group>

   <fn id="tfn00001">
    <label>A</label>
    <p>From Benz et al. <bold>(<xref rid="pr00032">32</xref>)</bold>, 
     Stoltzfus <bold>(<xref rid="pr00044">35</xref>)</bold>, specimens 
     3.2 mm ( 1/8 in.) in diameter by 127 mm (5 in.) long.</p>
   </fn>

   <fn id="tfn00002">
    <label>B</label>
    <p>See <xref rid="tx00008" ref-type="table">Table X1.8</xref>
     for alloy compositions.</p>
   </fn>
 * 
	 */
	static String TAG = "fn-group";

	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSDivFactory.FN,
			JATSDivFactory.TITLE,
	});
	
	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	public JATSFnGroupElement() {
		super(TAG);
	}

	public JATSFnGroupElement(Element element) {
		super(element);
	}

	protected void applyNonXMLSemantics() {
//		journalMeta = (JATSJournalMetaElement) this.getSingleChild(JATSJournalMetaElement.TAG);
//		articleMeta = (JATSArticleMetaElement) this.getSingleChild(JATSArticleMetaElement.TAG);
	}
}
