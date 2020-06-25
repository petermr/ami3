package org.contentmine.ami.tools.template;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.image.ImageUtil;

/** currently only a no-op
 * 
 * @author pm286
 *
 */
public class MessageTemplateElement extends AbstractTemplateElement {


	private static final Logger LOG = LogManager.getLogger(MessageTemplateElement.class);
public static final String TAG = "message";
	
//	private enum Direction {
//		horizontal, 
//		vertical
//	}
//	private static final String NULL = "null";
	
//	public static final String BORDERS    = "borders";
//	public static final String EXTENSION  = "extension";
//	public static final String SECTIONS   = "sections";
//	public static final String SOURCE     = "source";
//	public static final String SPLIT      = "split";
//
//	private IntArray borders;
//	private List<String> sections;
//	private String source;
//	private Direction splitDirection;
//	private File sourceFile;
//	private String extension;
	

	public MessageTemplateElement() {
		super(TAG);
	}
	
	@Override
	public void process() {
//		System.out.println(">> "+this.toXML());
		boolean ok = true;
		try {
//			parseAttributes();
		} catch (RuntimeException e) {
			ok = false;
			LOG.debug("Cannot create message: "+e.getClass()+"/"+e.getMessage());
		}
//		if(ok) {
//			if (splitDirection != null) {
//				splitImage();
//			}
//			super.process();
//		}
	}


}
