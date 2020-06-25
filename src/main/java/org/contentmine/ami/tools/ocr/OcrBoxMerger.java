package org.contentmine.ami.tools.ocr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIOCRTool.OcrType;

/** merges contents of boxes fond after OCR .
 * currently HOCR and GOCR
 * @author pm286
 *
 */
public class OcrBoxMerger extends OcrMerger {
	private static final Logger LOG = LogManager.getLogger(OcrBoxMerger.class);
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
