package org.contentmine.norma.pubstyle.hindawi;

import java.util.ArrayList;
import java.util.List;

import org.contentmine.norma.InputFormat;
import org.contentmine.norma.pubstyle.PubstyleReader;
import org.contentmine.norma.tagger.hindawi.HTMLHindawiTagger;

public class HindawiReader extends PubstyleReader {

	public HindawiReader() {
		super();
	}

	public HindawiReader(InputFormat type) {
		super(type);
	}

	@Override
	protected void addTaggers() {
		this.addTagger(InputFormat.HTML, new HTMLHindawiTagger());
	}

	@Override
	protected List<String> getExtraneousXPaths() {
		return new ArrayList<String>();
	}

}
