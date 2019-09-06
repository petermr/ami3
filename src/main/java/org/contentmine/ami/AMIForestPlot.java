package org.contentmine.ami;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.processing.HilditchThinning;
import org.contentmine.image.processing.Thinning;
import org.contentmine.image.processing.ZhangSuenThinning;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import boofcv.io.image.UtilImageIO;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */
@Command(
name = "ami-pixel", 
aliases = "pixel",
version = "ami-pixel 0.1",
description = "analyzes bitmaps - generally binary, but may be oligochrome. Creates pixelIslands "
)

public class AMIForestPlot extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMIForestPlot.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
	protected void parseSpecifics() {
		LOG.error("AMIForest NYI");
//		System.out.println("maxislands           " + maxislands);
//		System.out.println();
	}
//
//    @Override
    protected void runSpecifics() {
		LOG.error("AMIForest NYI");
//    	if (cProject != null) {
//    		for (CTree cTree : cProject.getOrCreateCTreeList()) {
//    			runPixel(cTree);
//    		}
//    	} else if (cTree != null) {
//   			runPixel(cTree);
//    	} else if (imageFilenames != null) {
//    		for (String imageFilename : imageFilenames) {
//    			runPixel(new File(imageFilename));
//    		}
//   		    		
//    	} else {
//			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree ie imageFile");
//	    }
    }

}
