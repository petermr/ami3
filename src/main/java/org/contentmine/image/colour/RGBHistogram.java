package org.contentmine.image.colour;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** simple plotter for debugging colours
 * 
 * @author pm286
 *
 */
public class RGBHistogram {
	private static final Logger LOG = Logger.getLogger(RGBHistogram.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double x0 ;
	private double y0;
	private double ydelta;
	private double xscale = 50.;
	private double yscale = 0.9;
	private SVGG g;
	private double height;
	private double fontSize;
	private double strokeWidth;
	private double fontOffset;
	private String stroke;
	private Multiset<RGBColor> set;
	private List<Entry<RGBColor>> list;
	private List<Entry<RGBColor>> rgb;
	private double size;

	private RGBHistogram() {
	}
	
	public RGBHistogram(Multiset<RGBColor> set) {
		this.rgb = RGBColor.createRGBListSortedByCount(set);
		this.setDefaults();
	}

	public RGBHistogram(List<Entry<RGBColor>> list) {
		this.rgb = list;
		this.setDefaults();
	}

	public SVGG plot() {
		
		g = new SVGG();
		double x = x0;
		double y = y0;
		height = ydelta * yscale;
		fontSize = ydelta * 0.75;
		strokeWidth = 0.5;
		fontOffset = ydelta * 0.75;
		stroke = "black";
		size = 0.0;
		for (Entry<RGBColor> entry : rgb) {	
			size += entry.getCount();
		}
		
		for (Entry<RGBColor> entry : rgb) {			
			double percent = (100. * (double) entry.getCount() / size); 
			RGBColor color = entry.getElement();
			if (color.getRGBInteger() != RGBColor.HEX_WHITE) {
				plotRectangleAndText( x, y, percent, color.getHex());
				y += ydelta;
			}
		}
		return g;
	}
	
	private void plotRectangleAndText(double x, double y, double percent, String color) {
		double barWidth = percent * xscale;
		SVGRect rect = new SVGRect(x, y, barWidth, height);
		SVGText text = new SVGText(new Real2(x, y + fontOffset), color);
		text.setFontSize(fontSize);
		text.setFontWeight("bold");
		text.setFill("white");
		text.setStrokeWidth(strokeWidth);
		text.setStroke(stroke);
		text.setFontFamily("monospace");
		
		rect.setFill(color);
		rect.setStrokeWidth(strokeWidth);
		rect.setStroke(stroke);
		g.appendChild(rect);
		g.appendChild(text);
	}


	public void setX0(double d) {
		x0 = d;
	}

	public void setY0(double d) {
		y0 = d;
	}

	public void setYDelta(double d) {
		ydelta = d;
	}

	public void setXScale(double d) {
		xscale = d;
	}

	public void setYScale(double d) {
		yscale = d;
	}

	void setDefaults() {
		setX0(10.);
		setY0(10.);
		setYDelta(10.);
		setXScale(50.);
		setYScale(0.9);
	}
}
