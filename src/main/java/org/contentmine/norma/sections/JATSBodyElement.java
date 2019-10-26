package org.contentmine.norma.sections;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.contentmine.cproject.files.CTree;

import nu.xom.Element;

public class JATSBodyElement extends JATSElement implements IsBlock , HasDirectory {

	static String TAG = "body";
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSDivFactory.SEC,
			JATSDivFactory.FIG,
			JATSDivFactory.TABLE_WRAP,
			JATSDivFactory.BOXED_TEXT,
			JATSSpanFactory.P,
			JATSDivFactory.SUPPLEMENTARY_MATERIAL,
	});
	
	@Override
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	public JATSBodyElement(Element element) {
		super(element);
	}
	
//	protected void writeSections(JATSElement parent) {
//		this.currentDir = new File(cTree.getSectionsDirectory(), JATSBodyElement.TAG);
//		super.writeSections(this, currentDir);
//
//	}

	public String directoryName() {
		return this.TAG;
	}



}
