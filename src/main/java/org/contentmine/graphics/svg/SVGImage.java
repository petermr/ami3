/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.svg;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;
import org.apache.xerces.impl.dv.util.Base64;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.image.ImageUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/** supports defs
 * 
 * @author pm286
 *
 */
public class SVGImage extends SVGShape {

	private final static Logger LOG = Logger.getLogger(SVGImage.class);
	
	private static final String DATA = "data";
	private static final String BASE64 = "base64";
	public static final String IMAGE_PNG = "image/png";
	public static final String PNG = "PNG";
	public static final String IMAGE_BMP = "image/bmp";
	public static final String BMP = "BMP";
	public static final String IMAGE_JPG = "image/jpeg";
	public static final String JPG = "JPG";
	
	private static final String XLINK_PREF = "xlink";
	private static final String HREF = "href";
	private static final String SRC = "src";
	private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	public final static String TAG ="image";
	
	static Pattern IMG_SRC= Pattern.compile("data:(image/.*);base64,(.*)");
	public final static String ALL_IMAGE_XPATH = ".//svg:image";
	
	private static Map<String, String> mimeType2ImageTypeMap;
	static {
		mimeType2ImageTypeMap = new HashMap<String, String>();
		mimeType2ImageTypeMap.put(IMAGE_PNG, PNG);
	}
	
	/** constructor
	 */
	public SVGImage() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public SVGImage(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGImage(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGImage(this);
    }

	

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/**
	 * we have to apply transformations HERE as the actual display image is transformed
	 * by the viewer.  
	 * @return
	 */
	public Real2Range getBoundingBox() {
		if (boundingBoxNeedsUpdating()) {
			Real2 xy = this.getXY();
			Double width = getWidth();
			Double height = getHeight();
			if (xy != null && width != null && height != null) {
				boundingBox = new Real2Range(xy, xy.plus(new Real2(width, height)));
				LOG.trace("BB0 "+boundingBox);
				Transform2 t2 = this.getTransform2FromAttribute();
				LOG.trace("T "+t2);
				boundingBox = boundingBox.getTranformedRange(t2);
				LOG.trace("BB1 "+boundingBox);
				this.setBoundingBoxAttribute(3);
			}
		}
		return boundingBox;
	}
	
	public void setBoundingBoxAttribute(Integer decimalPlaces) {
		if (boundingBox != null) {
			if (decimalPlaces != null) {
				boundingBox.format(decimalPlaces);
			}
//			CMLElement.addCMLXAttribute(this, BOUNDING_BOX, boundingBox.toString());
			SVGUtil.setSVGXAttribute(this, BOUNDING_BOX, boundingBox.toString());
		}
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default none
	 */
	protected String getBBFill() {
		return "pink";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default blue
	 */
	protected String getBBStroke() {
		return "blue";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default 0.5
	 */
	protected double getBBStrokeWidth() {
		return 0.5;
	}

	/**
	 * 
	 * Does not transform the actual image. Maybe it should
	 * 
   <image x="0" y="0" 
     transform="matrix(0.3605,0,0,0.3592,505.824,65.944)" 
     width="158" xlink:href="data:image/png;base64,iVBORw0KGgbGgjc... ...kJggg=="
     style="clip-path:url(#clipPath18);" 
     height="199" 
     preserveAspectRatio="none" xmlns:xlink="http://www.w3.org/1999/xlink"/>
	 */
	public void applyTransformPreserveUprightText(Transform2 t2) {
		Real2 xy = getXY();
		xy.transformBy(t2);
		setXY(xy);
		Real2 wh = new Real2(getWidth(), getHeight());
		Transform2 rotScale = t2.removeTranslations();
		wh.transformBy(rotScale);
		this.setWidth(wh.getX());
		this.setHeight(wh.getY());
	}

	/**
	<image x="0" y="0" transform="matrix(0.144,0,0,0.1439,251.521,271.844)" 
			clip-path="url(#clipPath2)" width="1797" xlink:href="data:image/png;
			base64,iVBORw0KGgoAAAANSUhEUgAABwUAAAV4CAMAAAB2DvLsAAADAFBMVEX////+/v56 
			enpWVlZbW1taWlpZWVnHx8eRkZFVVVWMjIysrKxXV1dYWFhqamr5+fnMzMxeXl7c 
			3NyUlJR/f3+3t7cAAACGhob29vYpKSliYmJPT083Nzf8/PyBgYENDQ3s7OwwMDD1 
			    ...
			    RERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERE 
			RPQP/R8CiIK+y8Q6KQAAAABJRU5ErkJggg==" height="1400"
			 preserveAspectRatio="none" stroke-width="0" xmlns:xlink="http://www.w3.org/1999/xlink"/>
    */
	
	/** Convert image data to base64.
	 * 
	 * @param bufferedImage
	 * @param mimeType
	 * @return base64 value
	 * @throws RuntimeException (probably when the mime type is bad or ImageIO fails (no useful message)
	 */
	public String readImageDataIntoSrcValue(BufferedImage bufferedImage, String mimeType) throws RuntimeException {
		String imageType = mimeType2ImageTypeMap.get(mimeType);
		if (imageType == null) {
			throw new RuntimeException("Cannot convert mimeType: "+mimeType);
		}
		setXYWidthHeight(bufferedImage);
		
		String base64 = convertBufferedImageToBase64(bufferedImage, imageType);
		String attValue = createHrefAttributeValue(mimeType, base64);
		addXlinkHref(attValue);
		return attValue;
	}

	public void setXYWidthHeight(BufferedImage bufferedImage) {
		double x = bufferedImage.getMinX();
		double y = bufferedImage.getMinY();
		double height = bufferedImage.getHeight();
		double width = bufferedImage.getWidth();
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
	}

	private static String createHrefAttributeValue(String mimeType, String base64) {
		String attValue = DATA+":"+mimeType+";"+BASE64+","+base64;
		return attValue;
	}

	public static String convertBufferedImageToBase64(BufferedImage bufferedImage, String imageType) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String format = SVGImage.getFormatFromMimeType(imageType); 
		LOG.trace("format "+format);
		format = SVGImage.PNG;
		try {
			boolean ok = ImageIO.write(bufferedImage, format, baos);
			if (!ok) {
				throw new RuntimeException("ImageIO: Cannot convert bufferedImage to ByteArrayOutputStream for type: "+imageType+" / "+bufferedImage);
			}
			baos.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot read image", e);
		}
		LOG.trace("BufferedImage "+bufferedImage);
		byte[] byteArray = baos.toByteArray();
		String base64 = Base64.encode(byteArray);
		return base64;
	}
	
	public static BufferedImage readSrcDataToBufferedImage(String srcValue) {
//		src="data:image/png;base64,iVBORw0KGgoA..."
		Matcher matcher = IMG_SRC.matcher(srcValue);
		String mimeType = null;
		String srcBase64 = null;
		if (matcher.matches()) {
			mimeType = matcher.group(1);
			srcBase64 = matcher.group(2);
			LOG.trace("base64 "+srcBase64.length());
		} else {
			throw new RuntimeException("Cannot convert img/src");
		}

		byte[] byteArray = Base64.decode(srcBase64);
		LOG.trace("bytes "+byteArray.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(bais);
			LOG.trace(bufferedImage);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read base64 image", e);
		}
		return bufferedImage;
	}

	/** converts href/data attribute to BufferedImage.
	 * 
	 * @return null if no image possible.
	 */
	public BufferedImage getBufferedImage() {
		BufferedImage bufferedImage = null;
		String hrefData = this.getImageValue();
		if (hrefData != null) {
			bufferedImage = readSrcDataToBufferedImage(hrefData);
		}
		return bufferedImage;
	}
	/*
	 * <image transform="matrix(0.06781766590473381,-0.0,-0.0,0.0678967742330725,33.93199920654297,33.12698745727539)"
	 *  x="0.0" y="0.0" width="702.0" height="310.0" xlink:href="data:image/png;base64,iVBORw0KGgoAAAA..."
	 * xmlns:xlink="http://www.w3.org/1999/xlink"/>
	 */
	public String getImageValue() {
		String value = this.getAttributeValue(HREF, XLINK_NS);
		return value;
	}
	
	
	/** makes a new list composed of the images in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGImage> extractImages(List<SVGElement> elements) {
		List<SVGImage> imageList = new ArrayList<SVGImage>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGImage) {
				imageList.add((SVGImage) element);
			}
		}
		return imageList;
	}

	/** convenience method to extract list of svgPaths in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGImage> extractSelfAndDescendantImages(AbstractCMElement svgElement) {
		return SVGImage.extractImages(SVGUtil.getQuerySVGElements(svgElement, ALL_IMAGE_XPATH));
	}

	public boolean writeImage(String imageFilename, String mimeType) throws IOException {
		return writeImage(new File(imageFilename), mimeType);
	}

	public boolean writeImage(File imageFile, String mimeType) throws IOException {
		boolean wrote = false;
		Attribute xLinkAttribute = this.getAttribute(HREF, XLINK_NS);
		if (xLinkAttribute != null) {
			String xLinkValue = xLinkAttribute.getValue();
			BufferedImage bufferedImage = readSrcDataToBufferedImage(xLinkValue);
			SVGImage.writeBufferedImage(bufferedImage, mimeType, imageFile);
			wrote = true;
		}
		return wrote;
	}
	
	public static void writeBufferedImage(BufferedImage bufferedImage, String mimeType, File file) throws IOException {
		boolean ok = SVGImage.checkIsKnownImageIOWriterMimeType(mimeType);
		if (ok) {
			LOG.trace("Writing: "+file.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(file);
			writeByWriter(bufferedImage, mimeType, fos);
			fos.close();
		} else {
			printKnownMimeTypes();
			throw new RuntimeException("ImageIO unknown mimeType: "+mimeType);
		}
	}

	private static void writeByWriter(BufferedImage bufferedImage,
			String mimeType, FileOutputStream fos) throws IOException {
		if (bufferedImage != null) {
			ImageWriter imageWriter = getFirstKnownImageWriter(mimeType);
			if (imageWriter == null) {
				throw new RuntimeException("Cannot create ImageWriter for: "+mimeType);
			}
		    ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
		    imageWriter.setOutput(ios);
		    imageWriter.write(bufferedImage);
		} else {
			LOG.trace("NULL BufferedImage");
		}
	}

	public static ImageWriter getFirstKnownImageWriter(String mimeType) {
		ImageWriter imageWriter = null;
		Iterator<ImageWriter> iterator = ImageIO.getImageWritersByMIMEType(mimeType);
		while (iterator.hasNext()) {
			imageWriter = iterator.next();
			break;
		}
		return imageWriter;
	}

	public static ImageReader getFirstKnownImageReader(String mimeType) {
		ImageReader imageReader = null;
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByMIMEType(mimeType);
		while (iterator.hasNext()) {
			imageReader = iterator.next();
			break;
		}
		return imageReader;
	}

	private static void printKnownMimeTypes() {
		System.err.println("Known reader mimeTypes:");
		for (String knownMimeType : ImageIO.getReaderMIMETypes()) {
			System.err.println(knownMimeType);
		}
		System.err.println("Known writer mimeTypes:");
		for (String knownMimeType : ImageIO.getWriterMIMETypes()) {
			System.err.println(knownMimeType);
		}
	}

	private static boolean checkIsKnownImageIOWriterMimeType(String mimeType) {
		boolean ok = false;
		for (String knownMimeType : ImageIO.getWriterMIMETypes()) {
			if (knownMimeType.equals(mimeType)) {
				ok = true;
				break;
			}
		}
		return ok;
	}

	@Override
	public String getGeometricHash() {
		return getAttributeValue(SRC)+" "+getAttributeValue(HREF);
	}

	/** sets image data into href attribute.
	 * 
	 * @param imageData; if null unset attribute
	 */
	public void setImageData(String imageData) {
		if (imageData != null) {
			if (!imageData.startsWith(DATA)) {
				throw new RuntimeException("ImageData must start with "+DATA);
			}
			addXlinkHref(imageData);
		} else {
			Attribute hrefAttribute = this.getAttribute(HREF, XLINK_NS);
			if (hrefAttribute != null) {
				hrefAttribute.detach();
			}
		}
	}

/**	
	<svg width="4in" height="3in" version="1.1"
		     xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
		  <desc>This graphic links to an external image
		  </desc>
		  <image x="200" y="200" width="100px" height="100px"
		         xlink:href="myimage.png">
		    <title>My image</title>
		  </image>
  */
	public void setHref(String href) {
		if (href != null) {
			addXlinkHref(href);
		} else {
			Attribute hrefAttribute = this.getAttribute(HREF, XLINK_NS);
			if (hrefAttribute != null) {
				hrefAttribute.detach();
			}
		}
	}

	private void addXlinkHref(String href) {
		this.addAttribute(new Attribute(XLINK_PREF+":"+HREF, XLINK_NS, href));
	}
	
	/** creates an SVGImage from file.
	 * 
	 * <p>will not create a location (may need to set x, y or transform independently);
	 * </p>
	 * @param imageFile
	 * @param imageType
	 * @return image
	 * @throws RuntimeException FNF, etc
	 */
	public static SVGImage createSVGFromImage(File imageFile, String imageType) throws RuntimeException {
		if (imageFile == null || !imageFile.exists() || imageFile.isDirectory()) {
			throw new RuntimeException("Image file does not exist: "+imageFile);
		}
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageUtil.readImage(imageFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		SVGImage svgImage = createImage(imageType, bufferedImage);
		return svgImage;
	}

	public static SVGImage createImage(String imageType, BufferedImage bufferedImage) {
		SVGImage svgImage = null;
		String format = SVGImage.getFormatFromMimeType(imageType);
		if (format == null) {
			throw new RuntimeException("Unsupported mime type: "+imageType);
		}
		String base64 = SVGImage.convertBufferedImageToBase64(bufferedImage, format);
		String imageData = SVGImage.createHrefAttributeValue(imageType, base64);
		LOG.trace(imageData);
		if (imageData != null) {
		    svgImage = new SVGImage();
		    svgImage.setImageData(imageData);
		}
		return svgImage;
	}

	/** get informal format for ImageIO.write.
	 * 
	 * ImageWriter types seem messy.
	 * 
	 * @param imageType
	 * @return format
	 */
	public static String getFormatFromMimeType(String imageType) {
		if (imageType == null) {
			return null;
		} else if (SVGImage.IMAGE_PNG.equals(imageType)) {
			return SVGImage.PNG;
		} else if (SVGImage.IMAGE_JPG.equals(imageType)) {
			return SVGImage.JPG;
		} else if (SVGImage.IMAGE_BMP.equals(imageType)) {
			return SVGImage.BMP;
		}

		// TODO Auto-generated method stub
		return null;
	}
	
	/** if image is a bitmap apply transformation.
	 * <p>
	 * Some image are "wrong way up" with a transformation matrix. If so
	 * apply this and reset to zero rotation.
	 */
	public BufferedImage applyTransformToImage(BufferedImage bufferedImage, Transform2 transform2) {
		BufferedImage rotatedImage = null;
		if (transform2 != null && bufferedImage != null) {
			RealSquareMatrix rotmat = (RealSquareMatrix) transform2.getRotationMatrix().format(1);
			Double m00 = rotmat.elementAt(0, 0);
			Double m11 = rotmat.elementAt(1, 1);
			if (rotmat.isImproperRotation()) {
				if (Real.isEqual(m11, -1.0, 0.001)) {
					rotatedImage = flipBitmapHorizontally(bufferedImage);
				} else if (Real.isEqual(m00, -1.0, 0.001)) {
					rotatedImage = flipBitmapVertically(bufferedImage);
				}
			} else if (Real.isEqual(m00, -1.0, 0.001) && Real.isEqual(m11, -1.0, 0.001)) {
				rotatedImage = rotateBitmapByPI(bufferedImage);
			} else {
				rotatedImage = transform(bufferedImage, transform2.getAffineTransform());
			}
		}
		return rotatedImage;
	}
	
	public void applyTransformToImage(Transform2 transform) {
		BufferedImage bufferedImage = this.getBufferedImage();
		if (bufferedImage != null) {
			BufferedImage transformedImage = applyTransformToImage(bufferedImage, transform);
			String imageData = SVGImage.convertBufferedImageToBase64(transformedImage, SVGImage.IMAGE_PNG);
			imageData = SVGImage.createHrefAttributeValue(SVGImage.IMAGE_PNG, imageData);
			this.setImageData(imageData);
		}
	}

	private BufferedImage rotateBitmapByPI(BufferedImage bufferedImage) {
		return transform(bufferedImage, new AffineTransform(new double[]{
				-1.0, 0.0, 0.0, -1.0, bufferedImage.getWidth(), bufferedImage.getHeight()}));
	}

	public BufferedImage flipBitmapVertically(BufferedImage bufferedImage) {
		return transform(bufferedImage, new AffineTransform(new double[]{1.0, 0.0, 0.0, -1.0, 0.0, bufferedImage.getHeight()}));
	}

	public BufferedImage flipBitmapHorizontally(BufferedImage bufferedImage) {
		return transform(bufferedImage, new AffineTransform(new double[]{-1.0, 0.0, 0.0, 1.0, bufferedImage.getWidth(), 0.0}));
	}

	private BufferedImage transform(BufferedImage img, AffineTransform affineTransform) {
		int w = img.getWidth();
	    int h = img.getHeight();
	    BufferedImage rot = ImageUtil.createARGBBufferedImage(w, h);
		Graphics2D graphic = rot.createGraphics();
		graphic.drawImage(img, affineTransform, null);
		return rot;
	}

	/** applies any rotations in explicit transform attribute
	 * 
	 */
	public void applyExplicitTransformationAndUpdate() {
		/*
		Transform2 transform = this.getTransform();
		if (transform != null) {
			this.applyTransformToImage(transform);
			transform.setElementAt(0, 0, Math.abs(transform.elementAt(0, 0)));
			transform.setElementAt(1, 1, Math.abs(transform.elementAt(1, 1)));
		    this.setTransform(transform);
		}
		*/
//		this.debug("XXXXXXXXXXX");
	}

	@Override
	/** cannot easily implement this, return false;
	 * 
	 */
	protected boolean isGeometricallyEqualTo(SVGElement shape, double epsilon) {
		return false;
	}

	/** clips a subImage into an SBGImage
	 * 
	 * @param image
	 * @param boundingBox
	 * @return
	 */
	public static SVGImage createSVGSubImage(BufferedImage image, Int2Range boundingBox) {
		BufferedImage subImage = ImageUtil.clipSubImage(image, boundingBox);
		SVGImage svgImage = SVGImage.createImage(SVGImage.IMAGE_PNG, subImage);
		svgImage.setX(boundingBox.getXRange().getMin());
		svgImage.setY(boundingBox.getYRange().getMin());
		return svgImage;
	}



}
