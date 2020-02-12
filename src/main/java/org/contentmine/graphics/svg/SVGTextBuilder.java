package org.contentmine.graphics.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;


/**
 * builds text components (including TextBlocks, HTML) from SVGText.
 * Not finished
 * 
 * @author pm286
 *
 */
public class SVGTextBuilder {
	private static final Logger LOG = Logger.getLogger(SVGTextBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGText> textList;
	private HtmlElement htmlElement;
	private Multiset<Double> deltaYMultiset; // will be formatted to 1 decimal
	private Multiset<Integer> leftXMultiset;
	private Multiset<Integer> rightXMultiset;
	private int deltaYPlaces = 1;

	public SVGTextBuilder() {
		
	}

	public void readTextList(List<SVGText> textList) {
		this.textList = new ArrayList<>(textList);
		generateStatistics();
	}

	public HtmlElement getOrCreateHtml() {
		if (htmlElement == null && textList != null) {
			htmlElement = HtmlHtml.createUTF8Html();
			generateStatistics();
			SVGText lastText = null;
			for (SVGText text : textList) {
//				if (couldBeNextLine(lastText, ) 
			}
		}
		return htmlElement;
	}

	private void generateStatistics() {
		if (deltaYMultiset == null) {
			deltaYMultiset = HashMultiset.create();
			leftXMultiset = HashMultiset.create();
			rightXMultiset = HashMultiset.create();			
			SVGText lastText = null;
			for (SVGText text : textList) {
				Double y = text.getY();
				IntRange xRange = text.getBoundingBox().getXRange().getIntRange();
				if (lastText == null) {
					
				} else {
					double deltaY = Util.format(text.getY() - lastText.getY(), deltaYPlaces);
					deltaYMultiset.add(deltaY);
				}
				lastText = text;
				leftXMultiset.add(xRange.getMin());
				rightXMultiset.add(xRange.getMax());
			}
		}
		LOG.debug("left: "+leftXMultiset);
		LOG.debug("right: "+rightXMultiset);
		LOG.debug("delta: "+deltaYMultiset);
	}


	
}
