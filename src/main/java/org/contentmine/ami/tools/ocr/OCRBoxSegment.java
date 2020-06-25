package org.contentmine.ami.tools.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIOCRTool;
import org.contentmine.ami.tools.AMIOCRTool.OcrType;

/** holds a list of SVG OCRBoxes from tools like Tessearct and GOCR
 * 
 * @author pm286
 *
 */
public class OCRBoxSegment {
	private static final Logger LOG = LogManager.getLogger(OCRBoxSegment.class);
private OcrType ocrType;
	private File svgFile;

	public OCRBoxSegment(OcrType ocrType) {
		this.ocrType = ocrType;
	}
	
	public void readSVGFile(File svgFile) {
		this.svgFile = svgFile;
	}

	public static OCRBoxSegment createBoxContainer(File boxesFile) {
		OCRBoxSegment boxContainer = null;
		if (boxesFile != null) {
			File parentFile = boxesFile.getParentFile();
			if (parentFile != null) {
				String parentS = parentFile.toString();
				OcrType ocrType = OcrType.getType(parentS);
				if (ocrType != null) {
					boxContainer = new OCRBoxSegment(ocrType);
					boxContainer.readSVGFile(boxesFile);
				}
			}
		}
		return boxContainer;
	}
}
