package org.contentmine.norma.editor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Substitution {

	public static final Logger LOG = LogManager.getLogger(Substitution.class);
private String original;
	private String edited;

	public Substitution() {
		
	}
	
	public Substitution(String original, String edited) {
		this.original = original;
		this.edited = edited;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getEdited() {
		return edited;
	}

	public void setEdited(String edited) {
		this.edited = edited;
	}

	
}
