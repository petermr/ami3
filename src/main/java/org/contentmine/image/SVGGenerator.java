package org.contentmine.image;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;


public class SVGGenerator {

	private static final String BBOX = "bbox";
	private static final double FONT_SCALE = 1.5;
	
	private SVGSVG svgsvg;

	public SVGGenerator() {
		
	}

	/**
<span class='ocrx_word' id='word_2' title="bbox 1173 17 1582 66">Passeriformes</span>
	 * @param htmlFile
	 */
	public void readHtml(File htmlFile) {
		HtmlElement element = null;
		try {
			HtmlFactory htmlFactory = new HtmlFactory();
			element = htmlFactory.parse(htmlFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (element != null) {
			svgsvg = new SVGSVG();
			svgsvg.setWidth(2500.);
			svgsvg.setHeight(2500.);
			List<HtmlSpan> spanList = HtmlSpan.extractSpans(HtmlUtil.getQueryHtmlElements(
					element, ".//h:span[not(h:span)]"));
			for (HtmlSpan span : spanList) {
				String title = span.getTitle();
				if (title == null) {
					throw new RuntimeException("null title");
				}
				List<String> tt = Arrays.asList(title.split("\\s+"));
				if (tt.size() != 5 || !(BBOX.equals(tt.get(0)))) {
					throw new RuntimeException("bad title: "+title);
				}
				Real2 xy0 = new Real2(tt.get(1)+" "+tt.get(2));
				Real2 xy1 = new Real2(tt.get(3)+" "+tt.get(4));
				Real2Range bbox =new Real2Range(xy0, xy1);
				Double xRange = bbox.getXRange().getRange();
				Double yRange = bbox.getYRange().getRange();
				boolean vertical = yRange > xRange;
				double fontSize =  (vertical) ? xRange * FONT_SCALE : yRange * FONT_SCALE ;
				SVGText text = new SVGText(xy0, span.getValue());
				text.setFontSize(fontSize);
				if (vertical) {
					SVGG g = new SVGG();
					svgsvg.appendChild(g);
					g.appendChild(text);
					Transform2 t2 = Transform2.getRotationAboutPoint(new Angle(Math.PI / 2.), xy0);
					g.setTransform(t2);
					
				} else {
					svgsvg.appendChild(text);
				}
			}
		}
	}
	
	public SVGSVG getSVG() {
		return svgsvg;
	}
}
