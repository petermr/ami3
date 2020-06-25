package org.contentmine.cproject.metadata.crossref;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.AbstractMDAnalyzer;

public class CrossrefAnalyzer extends AbstractMDAnalyzer {
	
	private static final Logger LOG = LogManager.getLogger(CrossrefAnalyzer.class);
public CrossrefAnalyzer() {
	}

	public CrossrefAnalyzer(File directory) {
		this.setCProject(directory);
	}

	public CrossrefAnalyzer(CProject cProject) {
		this.setCProject(cProject);
	}
	
	

}
