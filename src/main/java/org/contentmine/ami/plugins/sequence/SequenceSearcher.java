package org.contentmine.ami.plugins.sequence;

import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.NamedPattern;
import org.contentmine.cproject.files.ResultElement;

public class SequenceSearcher extends AMISearcher {

	public SequenceSearcher(AMIArgProcessor argProcessor,NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}
	
	/**
	 *  //PLUGIN
	 */
	@Override
	public ResultElement createResultElement() {
		return new SequenceResultElement();
	}

}
