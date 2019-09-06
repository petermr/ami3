package org.contentmine.svg2xml.page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;

/** extracts parts of page through user-supplied crop boxes
 * 
 * will gradually accrete unit conversion, offsets, etc.
 * 
 * The user informs PageCropper of their cropping media box,
 * then creates a cropBox in their own units,
 * 
 * @author pm286
 *
 */
public class PageCropper {
	private static final int MIN_X = 0;
	private static final int MIN_Y = 0;
	private static final int MAX_X = 600;
	private static final int MAX_Y = 800;
	private static final Logger LOG = Logger.getLogger(PageCropper.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum Units {
		INCH("in", PageCropper.DPI),
		MM("mm", PageCropper.DPI * PageCropper.MM2INCH),
		PX("px", 1.0);
		
		private double user2px;
		private String abbrev;

		private Units(String abbrev, double user2px) {
			this.user2px = user2px;
			this.abbrev = abbrev;
		}
		/** get conversion units to px
		 * 
		 * @return
		 */
		public double getUser2Px() {
			return user2px;
		}
		
		public String getAbbrev() {
			return abbrev;
		}
		
		public static Units getUnitsFromAbbrev(String abbrev) {
			for (Units units : values()) {
				if (units.getAbbrev().equals(abbrev)) {
					return units;
				}
			}
			return null;
		}
	}

	// currently hardcode dpi
	public final static double DPI = 72;
	public final static double INCH2MM = 25.4;
	public final static double MM2INCH = 1./INCH2MM;

	private Real2Range localCropBox;
	private Real2Range localMediaBox;
	private Transform2 crop2LocalTransform;
	private SVGElement svgElement;
	private Real2 userCropxy0;
	private Real2 userCropxy1;
	private Units units;
	private BoxProcessor cropBoxProcessor;
	private BoxProcessor mediaBoxProcessor;

	public PageCropper() {
		setDefaults();
	}
	
	public PageCropper(Units units) {
		this();
		this.setUnits(units);
	}
	
	public void setUnits(Units units) {
		this.units = units;
	}

	/**
	 * 
	 * @param args filename x0, y0, x1, y1
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 5) {
			usage();
			return;
		}
		PageCropper pageCropper = new PageCropper();
		pageCropper.readSVG(args[0]);
		pageCropper.setTLBRUserMediaBox(new Real2(0,850), new Real2(600,0));
		pageCropper.setTLBRUserCropBox(args[1],args[2],args[3],args[4]);
	}

	private static void usage() {
		System.out.println("pageCropper <infile> x0 y0 x1 y1)");
		System.out.println("    x0 ... y1 is user crop box");
		System.out.println("user media box defaults to (0,800)(600,0)");
	}

	private void setDefaults() {
		this.localMediaBox = new Real2Range(new Real2(0,0), new Real2(600, 800));
		this.units = Units.PX;
	}

	/** coordinates used in native user coordinates.
	 * 
	 * @param mediaBox
	 */
	public void setLocalMediaBox(Real2Range mediaBox) {
		this.localMediaBox = mediaBox;
	}

	/** the transformation used by the cropper.
	 * this allows for transformation between units, y-direction, offsets, etc.
	 * 
	 * Note that Real2Range cannot hold negative ranges so we don't have a traditional box
	 * e.g. Inkscape has Y coordinates UP the page from bottom = 0 to top = 800
	 * so Inkscape cropMediaBox = Real2(0, 800), Real2(600, 0)
	 * 
	 * @param xy0 one corner
	 * @param xy1 opposite corner
	 */
	public void setTLBRUserMediaBox(Real2 xy0, Real2 xy1) {
		if (this.localMediaBox == null) {
			throw new RuntimeException("Must have local mediaBox");
		}
		double screen2px = units.getUser2Px();
		double X0 = this.localMediaBox.getXMin() * screen2px;
		double X1 = this.localMediaBox.getXMax() * screen2px;
		double xScale = (xy1.x - xy0.x) / (X1 - X0);
		double xConstant = -X0 * xScale + xy0.x;
		
		double Y0 = this.localMediaBox.getYMin() * screen2px;
		double Y1 = this.localMediaBox.getYMax() * screen2px;
		double yScale = (xy1.y - xy0.y) / (Y1 - Y0);
		double yConstant = -Y0 * yScale + xy0.y;
		
		crop2LocalTransform = Transform2.createScaleTransform(xScale, yScale);
		crop2LocalTransform.setTranslation(new Real2(xConstant, yConstant));
		
	}
	
	/** box as 4 numbers
	 * x0,y0,x1,y1
	 * 
	 * @param xy0xy1
	 */
	private void setTLBRUserCropBox(String x0, String y0, String x1, String y1) {
		Real2 xy0 = new Real2(x0, y0);
		Real2 xy1 = new Real2(x1, y1);
		this.setTLBRUserCropBox(xy0, xy1);
	}


	/** crop box in cropping coordinates.
	 * 
	 * @param cropBoxProcessor
	 */
	public void setTLBRUserCropBox(Real2 xy0, Real2 xy1, Units units) {
		this.setUnits(units);
		setTLBRUserScaledCropBox(xy0, xy1, units);
	}

	/** crop box in cropping coordinates.
	 * 
	 * @param cropBoxProcessor
	 */
	public void setTLBRUserCropBox(Real2 xy0, Real2 xy1) {
		this.userCropxy0 = new Real2(xy0);
		this.userCropxy0.transformBy(crop2LocalTransform);
		this.userCropxy1 = new Real2(xy1);
		this.userCropxy1.transformBy(crop2LocalTransform);
		
		this.localCropBox = new Real2Range(userCropxy0, userCropxy1);
	}

	/** crop box in cropping coordinates.
	 * 
	 * @param cropBoxProcessor
	 */
	public void setTLBRUserScaledCropBox(Real2 xy0, Real2 xy1, Units units) {
		this.userCropxy0 = new Real2(xy0).multiplyBy(units.user2px);
		this.userCropxy0.transformBy(crop2LocalTransform);
		this.userCropxy1 = new Real2(xy1).multiplyBy(units.user2px);
		this.userCropxy1.transformBy(crop2LocalTransform);
		
		this.localCropBox = new Real2Range(userCropxy0, userCropxy1);
	}

	public Real2Range getLocalCropBox() {
		return localCropBox;
	}

	public Transform2 getCropToLocalTransformation() {
		return crop2LocalTransform;
	}

	public List<SVGElement> extractContainedElements(List<SVGElement> descendants) {
		List<SVGElement> contained = SVGElement.extractElementsContainedInBox(descendants, localCropBox);
		return contained;
	}

	public List<SVGElement> extractDescendants(SVGElement svgElement) {
		List<SVGElement> descendants = SVGElement.extractSelfAndDescendantElements(svgElement);
		return descendants;
	}

	public void detachElementsOutsideBox() {
		svgElement.detachDescendantElementsOutsideBox(localCropBox);
	}

	public void readSVG(String filename) throws FileNotFoundException {
		File file = new File(filename);
		readSVG(file);
	}

	public void readSVG(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}
		svgElement = SVGElement.readAndCreateSVG(file);
		if (svgElement == null) {
			throw new RuntimeException("null svg element");
		}
	}

	public SVGElement getSVGElement() {
		return svgElement;
	}

	public void displayCropBox(File svgFile) {
		SVGRect box = SVGRect.createFromReal2Range(getLocalCropBox());
		box.setCSSStyle("stroke:blue;stroke-width:1.0;fill:none;");
		getSVGElement().appendChild(box);
		SVGSVG.wrapAndWriteAsSVG(getSVGElement(), svgFile);
		box.detach();
	}

	/** coordinates are cropping coordinates
	 * 
	 * @param fileroot
	 * @param inputFile
	 * @param tl
	 * @param br
	 * @return
	 * @throws FileNotFoundException
	 */
	public SVGElement cropFile(String fileroot, File inputFile, Real2 tl, Real2 br)
			throws FileNotFoundException {
		readSVG(inputFile);
		setTLBRUserMediaBox(new Real2(MIN_X, MAX_Y), new Real2(MAX_X, MIN_Y));
		setTLBRUserCropBox(tl, br);
		/** remove for production
		// just for display
		displayCropBox(new File(new File("target/crop/"), fileroot + ".raw.box.svg"));
		*/
		detachElementsOutsideBox();
		return svgElement;
	}
	
	/** coordinates have been created by cropBoxProcessor
	 * 
	 */
	public SVGElement cropFile(File inputFile) throws IOException {
		if (!cropBoxProcessor.isValid()) {
			throw new RuntimeException("no cropBoxProcessor");
		}
		readSVG(inputFile);
		setTLBRUserMediaBox(new Real2(MIN_X, MAX_Y), new Real2(MAX_X, MIN_Y));
		Real2 topLeft = cropBoxProcessor.getTopLeft();
		Real2 bottomRight = cropBoxProcessor.getBottomRight();
		LOG.trace("TL BR"+topLeft+" || "+bottomRight);
		setTLBRUserCropBox(topLeft, bottomRight);
		detachElementsOutsideBox();
		return svgElement;
	}
	
	/** coordinates are cropping coordinates
	 * 
	 * @param tl
	 * @param br
	 * @param userUnits
	 * @return transformed element
	 * @throws FileNotFoundException
	 */
	public SVGElement cropElementTLBR(Real2 tl, Real2 br, Units userUnits) {
		if (svgElement != null) {
			// default
			setTLBRUserMediaBox(new Real2(MIN_X, MAX_Y), new Real2(MAX_X, MIN_Y));
			setTLBRUserCropBox(tl.multiplyBy(userUnits.getUser2Px()), br.multiplyBy(userUnits.getUser2Px()));
			detachElementsOutsideBox();
		} else {
			throw new RuntimeException("null svgElement");
		}
		return svgElement;
	}

	/** make copy of svgElement.
	 * 
	 * @param svgElement
	 */
	public void setSVGElementCopy(AbstractCMElement svgElement) {
		this.svgElement = (SVGElement) svgElement.copy();
		LOG.debug(this.svgElement);
	}

	public SVGElement cropElementTLBR(Real2 tl, double width, double height, Units userUnits) {
		Real2 br= new Real2(tl.getX() + width, tl.getY() + height);
		return cropElementTLBR(tl, br, userUnits);
	}

	public void processCropBoxArgs(List<String> stringValues) {
		getOrCreateCropBoxProcessor();
		this.cropBoxProcessor.parseArguments(stringValues);
	}
	
	public BoxProcessor getOrCreateCropBoxProcessor() {
		if (this.cropBoxProcessor == null) {
			cropBoxProcessor = new BoxProcessor();
		}
		return cropBoxProcessor;
	}

	public void processMediaBoxArgs(List<String> stringValues) {
		getOrCreateMediaBoxProcessor();
		this.mediaBoxProcessor.parseArguments(stringValues);
	}

	public BoxProcessor getOrCreateMediaBoxProcessor() {
		if (this.mediaBoxProcessor == null) {
			mediaBoxProcessor = new BoxProcessor();
		}
		return mediaBoxProcessor;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("\n");
		sb.append("CropBox: "+String.valueOf(cropBoxProcessor)+"\n");
		sb.append("MediaBox: "+String.valueOf(mediaBoxProcessor)+"\n");
		return sb.toString();
		
	}

	public void setTLBRUserCropBox(Real2Range bbox) {
		Real2 tl = new Real2(bbox.getXMin(), bbox.getYMin());
		Real2 br = new Real2(bbox.getXMax(), bbox.getYMax());
		setTLBRUserCropBox(tl, br);
	}
	

}
