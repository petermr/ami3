package org.contentmine.cproject.metadata.crossref;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.AbstractMDAnalyzer;

public class CrossrefAnalyzer extends AbstractMDAnalyzer {
	
	private static final Logger LOG = Logger.getLogger(CrossrefAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public CrossrefAnalyzer() {
	}

	public CrossrefAnalyzer(File directory) {
		this.setCProject(directory);
	}

	public CrossrefAnalyzer(CProject cProject) {
		this.setCProject(cProject);
	}
	
	

}
