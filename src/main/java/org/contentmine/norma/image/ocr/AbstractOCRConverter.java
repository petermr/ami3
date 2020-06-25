package org.contentmine.norma.image.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIOCRTool;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.norma.util.CommandRunner;

import com.google.common.collect.Multimap;

public abstract class AbstractOCRConverter extends CommandRunner {
	private static final Logger LOG = LogManager.getLogger(AbstractOCRConverter.class);
private String configName;
	protected AMIOCRTool amiOcrTool;
	protected File imageFile;
	protected TextLineAnalyzer textLineAnalyzer;
	protected boolean disambiguate;
	protected File ocrBaseDir;
	protected String inputFilename;
	protected BufferedImage inputImage;
	protected SVGElement svgElement;
	protected List<String> replaceList;
	protected Multimap<String, String> replaceMap;
	
	/** not yet worked out...*/
	
	/** name of config file */
	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public boolean isDisambiguate() {
		return disambiguate;
	}

	public void setDisambiguate(boolean disambiguate) {
		this.disambiguate = disambiguate;
	}

}
