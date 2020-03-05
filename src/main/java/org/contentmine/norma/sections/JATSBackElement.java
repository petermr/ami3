package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

public class JATSBackElement extends JATSElement implements IsBlock, HasDirectory {

	static String TAG = "back";
	
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSDivFactory.FN_GROUP,
			JATSDivFactory.REF_LIST,
			JATSDivFactory.ACK,
			JATSDivFactory.SEC,
			JATSDivFactory.BIO,
			JATSDivFactory.APP_GROUP,
			JATSDivFactory.GLOSSARY,
			JATSDivFactory.NOTES,
			
	});
	
	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}
	
	private JATSRefListElement refList;
	private JATSFnGroupElement fnGroup;

	public JATSBackElement() {
		super(TAG);
	}

	public JATSBackElement(Element element) {
		super(element);
	}

	public JATSRefListElement getRefList() {
		return refList;
	}

	public JATSFnGroupElement getFnGroup() {
		return fnGroup;
	}

	protected void applyNonXMLSemantics() {
		fnGroup = getFnGroupElement();
		refList = getRefList();
	}

	public JATSRefListElement getOrCreateSingleRefListChild() {
		refList = (JATSRefListElement) getSingleChild(JATSRefListElement.TAG);
		if (refList ==  null) {
			refList = new JATSRefListElement();
			this.appendElement(refList);
		}
		return refList;
	}

	public JATSFnGroupElement getFnGroupElement() {
		return (JATSFnGroupElement) getSingleChild(JATSFnGroupElement.TAG);
	}

	public JATSFnGroupElement getOrCreateSingleFnGroupChild() {
		fnGroup = (JATSFnGroupElement) getSingleChild(JATSFnGroupElement.TAG);
		if (fnGroup ==  null) {
			fnGroup = new JATSFnGroupElement();
			this.appendElement(fnGroup);
		}
		return fnGroup;
	}

	public String directoryName() {
		return this.TAG;
	}



}
