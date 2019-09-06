package org.contentmine.ami.plugins.phylotree;

import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.eucl.xml.XMLUtil;

public class PhyloResultsElement extends ResultsElement {

	public PhyloResultsElement(String title) {
		super(title);
	}

	public PhyloResultsElement(ResultsElement resultsElement) {
		if (resultsElement == null) {
			throw new RuntimeException("Null ResultsElement");
		}
		XMLUtil.copyAttributesFromTo(resultsElement, this);
		for (ResultElement resultElement : resultsElement) {
			PhyloResultElement wordResultElement = new PhyloResultElement(resultElement);
			this.appendChild(wordResultElement);
		}
	}

}
