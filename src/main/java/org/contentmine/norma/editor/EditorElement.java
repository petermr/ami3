package org.contentmine.norma.editor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class EditorElement extends AbstractEditorElement {

	public static final Logger LOG = LogManager.getLogger(EditorElement.class);
public static final String TAG = "editor";
	private SubstitutionEditor substitutionEditor;

	public EditorElement() {
		super(TAG);
	}

	public void setSubstitutionEditor(SubstitutionEditor substitutionEditor) {
		this.substitutionEditor = substitutionEditor;
	}
	
	protected SubstitutionEditor getSubstitutionEditor() {
		return substitutionEditor;
	}


}
