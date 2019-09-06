package org.contentmine.graphics.svg.misc;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.contentmine.graphics.svg.path.QuadPrimitive;

public class Penrose {
	private static final Logger LOG = Logger.getLogger(Penrose.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static Double SCALE = 10.0;
	public final static Double TWOPI5 = 2 * Math.PI / 5.0;
	public final static Double PHI = (1.0 + Math.sqrt(5.0)) / 2.0;
	
	public final static Double PI4 = Math.PI / 4.0;
	public final static Double PI5 = Math.PI / 5.0;
	public final static Double JIG = PI5;
	
	public final static Real2 OFFSET = new Real2(200.0, 200.0);
	public final static Double L1 = 4.0 * SCALE;
	public final static Real2 VECT1 = new Real2(L1, 0.0);
	public final static Double L2 = 2.0 * SCALE;
	public final static Double L2Y = 0.7 * L2;
	public final static Real2 VECT2 = new Real2(L2, 0.0);
	public final static Real2 VECT2A = new Real2(-1.0 * L2, 1.0 * L2Y);
	public final static Real2 VECT2B = new Real2(-1.5 * L2, -1.0 * L2Y);
	
	public final static Double L3 = 7.0 * SCALE;
	public final static Real2 VECT3 = new Real2(L3, 0.0);
	public final static Double L4 = 14.0 * SCALE;
	public final static Real2 VECT4 = new Real2(L4, 0.0);
	private Real2 currentPoint;
	
	
	public static void main(String[] args) {
		Penrose penrose = new Penrose();
		SVGG g;
		
		Angle angle = new Angle(0.0);
		g = penrose.drawKite(OFFSET, angle);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/penrose/kite.svg"));
		g = penrose.drawKite(OFFSET, new Angle(Math.PI / 2.0));
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/penrose/kite2.svg"));
		g = penrose.drawKite5(OFFSET);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/penrose/kite5.svg"));
		
		g = penrose.drawDart(OFFSET, angle);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/penrose/dart.svg"));
		g = penrose.drawDart(OFFSET, new Angle(Math.PI / 2.0));
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/penrose/dart2.svg"));
		g = penrose.drawDart5(OFFSET);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/penrose/dart5.svg"));
	}

	public Penrose() {
		
	}
	
	public SVGG drawKite(Real2 offset, Angle axisAngle) {
		Angle a1 = new Angle(1.0 * PI5);
		Angle a3 = new Angle(3.0 * PI5);
		int direction = -1;
		SVGG gg = new SVGG();
		
		// edges
		gg.appendChild(drawEdge(offset, 1.0 , axisAngle.plus(a1), direction));
		gg.appendChild(drawEdge(offset, 1.0 , axisAngle.subtract(a1), direction));
		Real2 axisVector = createAxisVector(axisAngle);
		offset = offset.plus(axisVector.multiplyBy(PHI));
		gg.appendChild(new SVGCircle(offset, 4.0));
		// small edges
		gg.appendChild(drawEdge(offset, 1.0 / PHI, axisAngle.plus(a3), direction));
		gg.appendChild(drawEdge(offset, 1.0 / PHI, axisAngle.subtract(a3), direction));
		
		return gg;
	}

	public SVGG drawDart(Real2 offset, Angle axisAngle) {
		Angle a3 = new Angle(3.0 * PI5);
		Angle a4 = new Angle(4.0 * PI5);
		int direction = 1;
		SVGG gg = new SVGG();
		
	
		// small edges
		gg.appendChild(drawEdge(offset, 1.0 / PHI, axisAngle.plus(a3), direction));
		gg.appendChild(drawEdge(offset, 1.0 / PHI, axisAngle.subtract(a3), direction));
		Real2 axisVector = createAxisVector(axisAngle);
		offset = offset.plus(axisVector);
		// large edges
		gg.appendChild(drawEdge(offset, 1.0, axisAngle.plus(a4), direction));
		gg.appendChild(drawEdge(offset, 1.0, axisAngle.subtract(a4), direction));
		
		return gg;
	}

	public SVGG drawKite5(Real2 offset) {
		SVGG g = new SVGG();
		for (int i = 0; i < 5; i++) {
			Angle angle = new Angle(i * 2.0 * PI5 / 100.);
			g.appendChild(drawKite(offset, angle));
		}
		return g;
	}
	
	public SVGG drawDart5(Real2 offset) {
		SVGG g = new SVGG();
		for (int i = 0; i < 5; i++) {
			Angle angle = new Angle(i * 2.0 * PI5 / 100.);
			g.appendChild(drawDart(offset, angle));
		}
		return g;
	}
	
	private Real2 createAxisVector(Angle axisAngle) {
		Transform2 rot = new Transform2(axisAngle);
		Real2Array horizontalEdge  = createHorizontalEdge();
		Real2 axisVector = horizontalEdge.get(11);
		axisVector.transformBy(rot);		
		axisVector = axisVector.multiplyBy(1.0 / PHI);
		return axisVector;
	}

	private AbstractCMElement drawEdge(Real2 offset, double scale, Angle angle, int direction) {
		AbstractCMElement g;
		Real2Array horizontalEdge  = createHorizontalEdge();
		if (direction == -1) {
			horizontalEdge = horizontalEdge.getRotatedAboutMidPoint();
		}
		horizontalEdge.multiplyBy(scale);
		horizontalEdge = horizontalEdge.plusEquals(offset);
		horizontalEdge.transformBy(Transform2.getRotationAboutPoint(angle, offset));
		g = drawEdge(horizontalEdge);
		SVGCircle c = new SVGCircle(offset, 3.);
		c.setFill("red");
		g.appendChild(c);
		return g;
	}
	
		
	private Real2Array createHorizontalEdge() {
		Real2 xy1 = new Real2(0.0, 0.0);
		Real2 xy2 = xy1.plus(VECT1);
		Real2 xy3 = xy2.plus(VECT2);
		Real2 xy4 = xy3.plus(VECT2A);
		Real2 xy5 = xy4.plus(VECT2A);
		Real2 xy6 = xy5.plus(VECT2);
		Real2 xy7 = xy6.plus(VECT3);
		Real2 xy8 = xy7.plus(VECT2);
		Real2 xy9 = xy8.plus(VECT2B);
		Real2 xy10 = xy9.plus(VECT2B);
		Real2 xy11 = xy10.plus(VECT2);
		Real2 xy12 = xy11.plus(VECT4);
		return Real2Array.createReal2Array(xy1, xy2, xy3, xy4, xy5, xy6, xy7, xy8, xy9, xy10, xy11, xy12);
	}
	
	private AbstractCMElement drawEdge(Real2Array ra) {
		AbstractCMElement g = new SVGG();
		SVGElement line = createLine(ra.get(0), ra.get(1));
		g.appendChild(line);
		SVGPath path = createQuadPath(ra.get(1), ra.get(2), ra.get(3));
		g.appendChild(path);
		path = createQuadPath(ra.get(3), ra.get(4), ra.get(5));
		path.setStroke("blue");
		g.appendChild(path);
		line = createLine(ra.get(5), ra.get(6));
		g.appendChild(line);
		path = createQuadPath(ra.get(6), ra.get(7), ra.get(8));
		g.appendChild(path);
		path = createQuadPath(ra.get(8), ra.get(9), ra.get(10));
		path.setStroke("blue");
		g.appendChild(path);
		line = createLine(ra.get(10), ra.get(11));
		g.appendChild(line);
		return g;
	}

	private SVGPath createQuadPath(Real2 xy0, Real2 xy1, Real2 xy2) {
		PathPrimitiveList ppl = new PathPrimitiveList();
		Real2Array ra = Real2Array.createReal2Array(xy1, xy2);
		MovePrimitive move = new MovePrimitive(xy0);
		ppl.add(move);
		QuadPrimitive quad = new QuadPrimitive(ra);
		ppl.add(quad);
		SVGPath path = new SVGPath(ppl);
		path.setStroke("red");
		path.setStrokeWidth(1.0);
		path.setFill("none");
		return path;
	}

	private SVGElement createLine(Real2 xy1, Real2 xy2) {
		SVGLine line = new SVGLine(xy1, xy2);
		line.setWidth(0.5);
		line.setStroke("black");
		return line;
	}
}
