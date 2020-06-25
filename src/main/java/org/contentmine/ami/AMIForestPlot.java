package org.contentmine.ami;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITool;

import picocli.CommandLine.Command;

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
	private static final Logger LOG = LogManager.getLogger(AMIForestPlot.class);
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
