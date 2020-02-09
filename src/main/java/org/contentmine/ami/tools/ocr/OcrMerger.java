package org.contentmine.ami.tools.ocr;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGUtil;

/**
 * 
 * @author pm286
 *
 */
public class OcrMerger {
	private static final Logger LOG = Logger.getLogger(OcrMerger.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum MeanType {
		arithmetic,
		geometric,
	}
	
	private List<OcrSegment> segmentList;
	
	public OcrMerger() {
		ensureLists();
	}
	
	private void ensureLists() {
		if (segmentList == null) {
			segmentList = new ArrayList<>();
		}
	}
	public void addFile(File svgFile) {
		ensureLists();
		OcrSegment ocrSegment = new OcrSegment(svgFile);
		segmentList.add(ocrSegment);
	}

	public void merge() {
		ensureLists();
		segmentList.get(0).prepareMerge();
		segmentList.get(1).prepareMerge();
		merge(segmentList.get(0), segmentList.get(1));
		return;
		
	}

	private void merge(OcrSegment ocrSegment, OcrSegment ocrSegment2) {
		LOG.debug("merge NYI");
	}

}
