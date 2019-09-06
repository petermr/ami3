package org.contentmine.graphics.svg.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGMarker;

public class ArrowFactory {

	private static final Logger LOG = Logger.getLogger(ArrowFactory.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Double delta = 2.0;
	private List<SVGLine> lineList;
	private List<SVGTriangle> triangleList;
	private List<SVGLine> usedLineList;
	private List<SVGTriangle> usedTriangleList;
	private List<SVGArrow> arrowList;
	private SVGMarker markerEnd;
	private String fill;
	private String stroke;

	public ArrowFactory() {
		
	}
	
	public Double getDelta() {
		return delta;
	}

	public void setEps(Double delta) {
		this.delta = delta;
	}

	public List<SVGLine> getUsedLineList() {
		return usedLineList;
	}

	public List<SVGTriangle> getUsedTriangleList() {
		return usedTriangleList;
	}

	public void readLinesTriangles(List<SVGLine> lineList, List<SVGTriangle> triangleList) {
		this.lineList = new ArrayList<SVGLine>(lineList);
		this.triangleList = new ArrayList<SVGTriangle>(triangleList);
	}
	
	public List<SVGArrow> createFirstComeArrows() {
		usedLineList = new ArrayList<SVGLine>();
		usedTriangleList = new ArrayList<SVGTriangle>();
		arrowList = new ArrayList<SVGArrow>();
		for (int iLine = 0; iLine < lineList.size(); iLine++) {
			SVGLine line = lineList.get(iLine);
			if (usedLineList.contains(line)) continue;
			for (int jTriangle = 0; jTriangle < triangleList.size(); jTriangle++) {
				SVGTriangle triangle = triangleList.get(jTriangle);
				if (usedTriangleList.contains(triangle)) continue;
				LOG.trace("potential arrow: "+line.toXML()+" / "+triangle.toXML());
				SVGArrow arrow = SVGArrow.createArrow(line, triangle, delta);
				if (arrow != null) {
					LOG.trace("made arrow from "+line.toXML()+" / "+triangle.toXML());
					arrowList.add(arrow);
					usedLineList.add(line);
					usedTriangleList.add(triangle);
				}
			}
		}
		return arrowList;
	}

	public List<SVGArrow> getArrowList() {
		return arrowList;
	}

	public void setMarkerEnd(SVGMarker svgMarker) {
		this.markerEnd = svgMarker;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public void detach(List<? extends SVGElement> list) {
		for (AbstractCMElement element : list) {
			element.detach();
		}
	}

	public void replaceLinesAndTrianglesByArrows(AbstractCMElement g) {
		List<SVGTriangle> triangleList = SVGTriangle.extractSelfAndDescendantTriangles(g);
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(g);
		readLinesTriangles(lineList, triangleList);
		createFirstComeArrows();
		List<SVGArrow> arrowList = getArrowList();
		for (int i = 0; i < arrowList.size(); i++) {
			SVGArrow arrow = arrowList.get(i);
			g.appendChild(arrow);
			if (markerEnd != null) {
				arrow.setMarkerEndRef(markerEnd);
			}
			if (fill != null) {
				arrow.setFill(fill);
			}
			if (stroke != null) {
				arrow.setStroke(stroke);
			}
		}
		List<SVGLine> usedLineList = getUsedLineList(); 
		List<SVGTriangle> usedTriangleList = getUsedTriangleList(); 
		detach(usedLineList);
		detach(usedTriangleList);
	}

	public void setStroke(String stroke) {
		this.stroke = stroke;
	}
	
}
