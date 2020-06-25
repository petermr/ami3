package org.contentmine.ami.tools.image;

import java.awt.image.BufferedImage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.image.ImageUtil;

/**
 * 
 * @author pm286
 *
 */
public class AnnotatedImage {
	private static final Logger LOG = LogManager.getLogger(AnnotatedImage.class);
private BufferedImage image;
	private int width;
	private int height;
	private int pixelCount;
	private int whiteThresh = 20;
	private int grayTol = 30;
	private HtmlDiv annotationElement;

	public AnnotatedImage() {
		
	}
	
	public static AnnotatedImage createAnnotatedImage(BufferedImage image) {
		AnnotatedImage annotatedImage = null;
		if (image != null) {
			annotatedImage = new AnnotatedImage();
			annotatedImage.image = image;
			annotatedImage.computeParameters();
		}
		return annotatedImage;
	}


	public BufferedImage getImage() {
		return image;
	}

	public AnnotatedImage setImage(BufferedImage image) {
		this.image = image;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getPixelCount() {
		return pixelCount;
	}

	public int getWhiteThresh() {
		return whiteThresh;
	}

	public AnnotatedImage setWhiteThresh(int whiteThresh) {
		this.whiteThresh = whiteThresh;
		return this;
	}

	public int getGrayTol() {
		return grayTol;
	}

	public AnnotatedImage setGrayTol(int grayTol) {
		this.grayTol = grayTol;
		return this;
	}

	private void computeParameters() {
		if (image == null) {
			throw new RuntimeException("Null image; check code");
		}
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.pixelCount = width * height;
		computeBinary();
		computeGray();
	}
	
	public void computeBinary() {
		long totalpix = image.getWidth() * image.getHeight();
		long whitepix = getWhiteCount(whiteThresh);
		long nonwhitepix = totalpix - whitepix;
		System.err.println("T: "+totalpix+"/W: "+whitepix+"/NW: "+nonwhitepix);
	}
	
	public void computeGray() {
		// is the image gray?
		int graypix = getGrayCount(grayTol);
	}

	/** gray defined as r,g,b all within graytol of each other
	 * 
	 * @param grayTol
	 * @return
	 */
	public int getGrayCount(Integer grayTol) {
		int count = 0;
		if (grayTol != null) {
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					int rgb = image.getRGB(x, y);
					int r = ImageUtil.getRed(rgb);
					int g = ImageUtil.getGreen(rgb);
					int b = ImageUtil.getBlue(rgb);
					if ((Math.abs(r - g) < grayTol) &&
					    (Math.abs(r - b) < grayTol) &&
					    (Math.abs(b - g) < grayTol)) {
						count++;
					}
				}
			}
		}
		return count;
	}

	/** 
	 * white defined as r,g,b all > thresh
	 * 
	 * @param whiteThresh
	 * @return
	 */
	public long getWhiteCount(Integer whiteThresh) {
		long count = 0;
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				int r = ImageUtil.getRed(rgb);
				int g = ImageUtil.getGreen(rgb);
				int b = ImageUtil.getBlue(rgb);
				if (whiteThresh == null || (r > whiteThresh && g > whiteThresh && b > whiteThresh)) count++;
			}
		}
		return count;
	}
	
	public HtmlElement createElement() {
		annotationElement = (HtmlDiv) new HtmlDiv().setTitle("annotation");
		return annotationElement;
	}
	
}
