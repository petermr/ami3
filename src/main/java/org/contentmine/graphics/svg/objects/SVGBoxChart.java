package org.contentmine.graphics.svg.objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.AbstractCMElement;

public class SVGBoxChart extends SVGDiagram {
	
	private static final Logger LOG = LogManager.getLogger(SVGBoxChart.class);
public static final String BOX_CHART = "boxChart";

	public SVGBoxChart() {
		super();
		this.setSVGClassName(BOX_CHART);
	}
	
	public SVGBoxChart(AbstractCMElement diagram) {
		this();
		this.rawDiagram = diagram;
	}

	public void createChart() {
		createPathsTextAndShapes();
		this.createTextBoxes();
		this.createArrows(eps);
		this.findConnectors(eps);
		for (SVGConnector link : connectorList) {
//			System.err.println("=================== \n"+link.toString()+"\n===================");
		}
	}
	
}
