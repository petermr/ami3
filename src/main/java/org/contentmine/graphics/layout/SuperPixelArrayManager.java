package org.contentmine.graphics.layout;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.cache.PageCache;
import org.contentmine.graphics.svg.util.SuperPixelArray;

/** 
 * holds superPixelArrays aggregated frm the pages of a document
 * 
 * @author pm286
 *
 */
public class SuperPixelArrayManager {
	private static final Logger LOG = Logger.getLogger(SuperPixelArrayManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private SuperPixelArray leftPageSPA;
	private SuperPixelArray rightPageSPA;
	private SuperPixelArray titlePageSPA;
	private SuperPixelArray backPageSPA;
	private Int2Range int2Range;
	private boolean isRightPage = true;
	private boolean isLeftPage = true;
	private File outDir;
	private boolean draw = true;
	
	public SuperPixelArrayManager(Int2Range int2Range) {
		this.int2Range = int2Range;
	}
	
	public SuperPixelArray getOrCreateLeftPageSPA() {
		if (leftPageSPA == null) {
			leftPageSPA = new SuperPixelArray(int2Range);
		}
		return leftPageSPA;
	}
	public void setLeftPageSPA(SuperPixelArray leftPageSPA) {
		this.leftPageSPA = leftPageSPA;
	}
	public SuperPixelArray getOrCreateRightPageSPA() {
		if (rightPageSPA == null) {
			rightPageSPA = new SuperPixelArray(int2Range);
		}
		return rightPageSPA;
	}
	public void setRightPageSPA(SuperPixelArray rightPageSPA) {
		this.rightPageSPA = rightPageSPA;
	}
	public SuperPixelArray getOrCreateTitleSPA() {
		if (titlePageSPA == null) {
			titlePageSPA = new SuperPixelArray(int2Range);
		}
		return titlePageSPA;
	}
	public void setTitleSPA(SuperPixelArray titleSPA) {
		this.titlePageSPA = titleSPA;
	}
	public SuperPixelArray getBackSPA() {
		if (backPageSPA == null) {
			backPageSPA = new SuperPixelArray(int2Range);
		}
		return backPageSPA;
	}
	public void setBackSPA(SuperPixelArray backSPA) {
		this.backPageSPA = backSPA;
	}

	public void aggregatePixelArrays(File svgFile) {
		System.out.print(".");
		PageCache pageCache = new PageCache();
		pageCache.setSvgFile(svgFile);
		SuperPixelArray superPixelArray = pageCache.createSuperpixelArray();
		SVGG g = new SVGG();
		if (draw) {
			superPixelArray.draw(g, new File(outDir, pageCache.getBasename()+".superPixels.svg"));
		}
		if (isLeftPage) {
			leftPageSPA = superPixelArray.plus(leftPageSPA);
		}
		if (isRightPage) {
			rightPageSPA = superPixelArray.plus(rightPageSPA);
		}
	}

	public boolean isRightPage() {
		return isRightPage;
	}

	public void setRightPage(boolean isRightPage) {
		this.isRightPage = isRightPage;
	}

	public boolean isLeftPage() {
		return isLeftPage;
	}

	public void setLeftPage(boolean isLeftPage) {
		this.isLeftPage = isLeftPage;
	}

	public File getOutDir() {
		return outDir;
	}

	public void setOutDir(File outDir) {
		this.outDir = outDir;
	}

	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}

}
