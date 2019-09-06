package org.contentmine.ami.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIOCRTool.OcrType;

/** merges contents of boxes fond after OCR .
 * currently HOCR and GOCR
 * @author pm286
 *
 */
public class OcrBoxMerger extends OcrMerger {
	private static final Logger LOG = Logger.getLogger(OcrBoxMerger.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<OCRBoxSegment> ocrBoxesList;
	
	public OcrBoxMerger() {
		
	}

	private void ensureLists() {
		if (this.ocrBoxesList == null) {
			this.ocrBoxesList = new ArrayList<>();
		}
	}

	public void mergeBoxes() {
		LOG.debug("mergeBoxes");
		
	}

	public void addBoxFile(File boxesFile) {
		ensureLists();
		if (boxesFile == null) {
			throw new RuntimeException("null boxFile");
		}
		OCRBoxSegment boxContainer = OCRBoxSegment.createBoxContainer(boxesFile);
		if (boxContainer == null) {
			throw new RuntimeException("cannot create boxContainer: "+boxesFile);
		}
		ocrBoxesList.add(boxContainer);
	}

	
}
