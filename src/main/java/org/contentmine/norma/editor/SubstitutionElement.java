package org.contentmine.norma.editor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SubstitutionElement extends AbstractEditorElement {

	public static final Logger LOG = LogManager.getLogger(SubstitutionElement.class);
private static final String ORIGINAL = "original";
	private static final String EDITED = "edited";

	public static final String TAG = "substitution";
	private String original;
	private String edited;
	private EditList editRecord;

	public SubstitutionElement() {
		super(TAG);
	}

	public String apply(String group) {
		if (original == null) {
			original = this.getAttributeValue(ORIGINAL);
			edited = this.getAttributeValue(EDITED);
		}
		SubstitutionEditor substitutionEditor = this.getSubstitutionEditor();
		if (substitutionEditor != null) {
			group = substitutionEditor.substituteAllAndRecordEdits(group, original, edited);
			editRecord = substitutionEditor.getEditRecord();
			if (editRecord.size() > 0) {
				LOG.trace("er "+editRecord);
			}
		}
		return group;
	}

	public EditList getEditRecord() {
		return editRecord;
	}



}
