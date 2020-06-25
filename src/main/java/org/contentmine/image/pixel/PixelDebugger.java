package org.contentmine.image.pixel;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGSVG;
import org.joda.time.DateTime;

/** writes pixels to SVG file.
 * crude
 * might integrate with logger
 * 
 * @author pm286
 *
 */
public class PixelDebugger {
	private static final Logger LOG = LogManager.getLogger(PixelDebugger.class);
public static void debugToRandomFile(PixelList pixelList, File debugDir) {
		String s = new DateTime().toString().replaceAll("/", "_");
		debugDir.mkdirs();
		SVGSVG.wrapAndWriteAsSVG(pixelList.getOrCreateSVG(), new File(debugDir, s+".svg"));
	}
}
