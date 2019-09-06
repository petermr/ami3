package org.contentmine.image;

import org.contentmine.eucl.euclid.Real2Range;

public class ImageParameters extends AbstractParameters {

	private double segmentTolerance;
	private String stroke;
	private double lineWidth;
	private String fill;
	private Real2Range minimumIslandSize;

	public ImageParameters() {
		setDefaults();
	}
	
	private void setDefaults() {
		segmentTolerance = 2.0;
		stroke = "green";
		lineWidth = 1.0;
		fill = "none";
//		minimumIslandSize = new Real2Range(new RealRange(0, 100), new RealRange(0, 100));
		minimumIslandSize = null;
	}
	
	public Real2Range getMinimumIslandSize() {
		return minimumIslandSize;
	}

	public void setMinimumIslandSize(Real2Range minimumIslandSize) {
		this.minimumIslandSize = minimumIslandSize;
	}

	public double getSegmentTolerance() {
		return segmentTolerance;
	}

	public void setSegmentTolerance(double tolerance) {
		this.segmentTolerance = tolerance;
	}

	public String getStroke() {
		return stroke;
	}

	public double getLineWidth() {
		return lineWidth;
	}

	public String getFill() {
		return fill;
	}

}
