package org.contentmine.graphics.svg;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Attribute;

public class AbstractSVGGradient extends SVGElement {
	private static final String PERCENT = "%";
	private static final String Y2 = "y2";
	private static final String Y1 = "y1";
	private static final String X2 = "x2";
	private static final String X1 = "x1";
	private static final Logger LOG = LogManager.getLogger(AbstractSVGGradient.class);
public AbstractSVGGradient(String tag) {
		super(tag);
	}

	public void setX1Percent(int pc) {
		setPercent(X1, pc);
	}

	public void setX2Percent(int pc) {
		setPercent(X2, pc);
	}

	public void setY1Percent(int pc) {
		setPercent(Y1, pc);
	}

	public void setY2Percent(int pc) {
		setPercent(Y2, pc);
	}

	private void setPercent(String xy, int pc) {
		this.addAttribute(new Attribute(xy, String.valueOf(pc)+PERCENT));
	}

	void appendStop(int percent, String cssStyle ) {
		SVGStop insideStop = new SVGStop();
		insideStop.setOffsetPercent(percent);
		insideStop.setCSSStyle(cssStyle);
		appendChild(insideStop);
	}


}
