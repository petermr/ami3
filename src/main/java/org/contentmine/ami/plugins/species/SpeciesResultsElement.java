package org.contentmine.ami.plugins.species;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;

public class SpeciesResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(SpeciesResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String SPECIES = "species";
	private SpeciesResultsElement(String title) {
		super(title);
	}

	public SpeciesResultsElement() {
		this(SPECIES);
	}

	@Override
	public List<String> getExactList() {
		if (matchList == null) {
			matchList = new ArrayList<String>();
			for (ResultElement resultElement : this) {
				SpeciesResultElement speciesResultElement = (SpeciesResultElement)resultElement;
				String match = speciesResultElement.getExact();
				matchList.add(match);
				LOG.trace("match "+match);
			}
		}
		LOG.trace("matchList "+matchList);
		return matchList;
	}

	public void addMatchAttributes(List<String> matchList) {
		if (this.size() != matchList.size()) {
			throw new RuntimeException("name list wrong length ("+matchList.size()+") rather than ("+this.size()+")");
		}
		int i = 0;
		for (ResultElement resultElement : this) {
			resultElement.setMatch(matchList.get(i));
			// cosmetic - keeps attributes in natural order
			resultElement.setPost(resultElement.getPost());
			i++;
		}
	}

}
