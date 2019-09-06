package org.contentmine.graphics.svg.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.SVGImage;

/** utility methods to help processing images.
 * <p>
 * Examples are very large images in href/data attributes and images which might be
 * stitched together
 * 
 * @author pm286
 *
 */
public class ImageConverter {

	private static final String HREF = "href=\"";
	private static final String DATA = "data:";
	private String data;
	private String svgString;
	private List<IntRange> imageStringBoundaries;
	private List<String> imageStrings;
	private List<String> hrefStrings;
	private List<String> imageFilenames;
	private File imageDirectory;
	private String fileroot = "image";
	private String mimeType = SVGImage.IMAGE_PNG;
	private List<IntRange> hrefIntRangeArray;

	public ImageConverter() {
		
	}

	/** reads a string corresponding to value of xlink:href data.
	 * 
	 * This is normally a base64 encoding of the image data.
	 * 
	 * @param data
	 */
	public void readHrefValue(String data) {
		this.data = data;
	}

	/** checks string including imageType and returns substring after image.
	 * 
	 * @return
	 */
	public String getStrippedHref() {
//		xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUh...AAASUVORK5CYII="
		if (svgString != null && data.startsWith(DATA)) {
			data = data.substring(DATA.length());
			int idx = data.indexOf(";");
			String imageType = data.substring(0,  idx);
			if (SVGImage.IMAGE_BMP.equals(imageType) ||
					SVGImage.IMAGE_JPG.equals(imageType) ||
					SVGImage.IMAGE_PNG.equals(imageType)) {
//				data = data.substring(idx+1);
			} else { 
				throw new RuntimeException("Unknown imageType: "+imageType);
			}
		}
		return data;
	}

	/** extracts all image data strings.
	 * 
	 * <p>Very crude. Designed to parse very long strings on assumption they come from 
	 * XOM tools. detects <image ... />. If it works may be refined. Range limits point to
	 * "!<" and "/>!". Note- will fail on prefixed XML.
	 * </p>
	 * @return empty list if none
	 */
	public List<IntRange> extractImageStringBoundaries() {
		if (imageStringBoundaries == null) {
			imageStringBoundaries = new ArrayList<IntRange>();
			if (svgString != null) {
				StringBuilder sb = new StringBuilder(svgString);
				int from = 0;
				while (true) {
					int idx = sb.indexOf("<image", from);
					if (idx == -1) {
						break;
					}
					int idxEnd = sb.indexOf("/>", idx);
					if (idxEnd == -1) {
						throw new RuntimeException("unbalanced <image />");
					} else {
						from = idxEnd;
						IntRange iRange = new IntRange(idx, idxEnd+2);
						imageStringBoundaries.add(iRange);
					}
				}
			}
		}
		return imageStringBoundaries;
	}

	public void readSVGString(String svgString) {
		this.svgString = svgString;
	}

	public List<String> extractImageStrings() {
		if (imageStrings == null) {
			extractImageStringBoundaries();
			imageStrings = new ArrayList<String>(imageStringBoundaries.size());
			for (IntRange intRange : imageStringBoundaries){
				String imageString = svgString.substring(intRange.getMin(), intRange.getMax());
				imageStrings.add(imageString);
			}
		}
		return imageStrings;
	}

	public List<String> extractHrefStrings() {
		if (hrefStrings == null) {
			extractImageStrings();
			hrefIntRangeArray = new ArrayList<IntRange>(); 
			hrefStrings = new ArrayList<String>(imageStrings.size());
			for (String imageString : imageStrings) {
				String href = null;
				int idx = imageString.indexOf(HREF);
				if (idx != -1) {
					idx += HREF.length();
					int hrefEnd = imageString.indexOf("\"", idx);
					if (hrefEnd != -1) {
						href = imageString.substring(idx, hrefEnd);
						hrefStrings.add(href);
						hrefIntRangeArray.add(new IntRange(idx, hrefEnd));
					}
				}
			}
		}
		return hrefStrings;
	}
	
	/** creates a list of filenames and fills them with contents of Hrefs.
	 * 
	 * <p>filenames are of form target/images/foo.2.png
	 * @param imageDir directory for files
	 * @param fileRoot root of files (e.g. "foo")
	 * @param mimeType e.g.image/png
	 * @return list of filenames (with relative syntax (/rather than \\)
	 * @throws IOException
	 */
	public List<String> createImageFiles() throws IOException {
		if (imageFilenames == null) {
			if (imageDirectory == null) {
				throw new RuntimeException("No image directory");
			}
			extractHrefStrings();
			imageFilenames = new ArrayList<String>(hrefStrings.size());
			int index = 1;
			String suffix = SVGImage.getFormatFromMimeType(mimeType).toLowerCase();
			for (String hrefString : hrefStrings) {
				BufferedImage bufferedImage = SVGImage.readSrcDataToBufferedImage(hrefString);
				String filename = fileroot+"."+index+"."+suffix;
				File file = new File(imageDirectory, filename);
				// because this will be relative URL
				filename = file.toString().replaceAll("\\\\", "/");
				imageFilenames.add(filename);
				SVGImage.writeBufferedImage(bufferedImage, mimeType, file);
				index++;
			}
		}
		return imageFilenames;
	}

	public void replaceHrefDataWithFileRef(String filePrefix) throws IOException {
		createImageFiles();
		StringBuilder sb = new StringBuilder(svgString);
		for (int i = imageStringBoundaries.size() - 1; i  >= 0; i--) {
			IntRange intRange = imageStringBoundaries.get(i);
			String imageString = new String(imageStrings.get(i));
			String fileRef = filePrefix + imageFilenames.get(i);
			StringBuilder imageSb = new StringBuilder(imageString);
			IntRange hrefRange = hrefIntRangeArray.get(i);
			imageSb.replace(hrefRange.getMin(), hrefRange.getMax(), fileRef);
			String newHref = imageSb.toString();
			sb.replace(intRange.getMin(), intRange.getMax(), newHref);
		}
		svgString = sb.toString();
	}

	/** sets the directory for images to be output.
	 * 
	 * <p>creates directory if not existing</p>
	 * 
	 * @param file
	 */
	public void setImageDirectory(File file) {
		this.imageDirectory = file;
		if (imageDirectory == null) {
			throw new RuntimeException("null image dirctory");
		} else if (imageDirectory.exists()) {
			if (!imageDirectory.isDirectory()) {
				throw new RuntimeException("must be a directory: "+imageDirectory);
			}
		} else {
			imageDirectory.mkdirs();
		}
	}

	public void setFileroot(String fileroot) {
		this.fileroot  = fileroot;
	}

	public void setMimeType(String mimeType) {
		if (SVGImage.getFormatFromMimeType(mimeType) == null) {
			throw new RuntimeException("Unknown mimeType: "+mimeType);
		}
		this.mimeType  = mimeType;
	}

	public String getSVGString() {
		return svgString;
	}

	public void readSVGFile(File svgFile) throws IOException {
		svgString = FileUtils.readFileToString(svgFile, "UTF-8");
	}
}
