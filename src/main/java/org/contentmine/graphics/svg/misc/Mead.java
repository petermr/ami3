package org.contentmine.graphics.svg.misc;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.GraphicsElement.FontWeight;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.contentmine.graphics.svg.path.QuadPrimitive;

public class Mead {
	private static final Logger LOG = Logger.getLogger(Mead.class);
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
	public final static Double LUG_START = 5.0 * SCALE;
	public final static Real2 VECT1 = new Real2(LUG_START, 0.0);
	public final static Double LUG_BEND = 1.0 * SCALE;
	public final static Double L2Y = 0.7 * LUG_BEND;
	public final static Real2 VECT2 = new Real2(LUG_BEND, 0.0);
	public final static Real2 VECT2A = new Real2(-1.0 * LUG_BEND, 1.0 * L2Y);
	public final static Real2 VECT2B = new Real2(-1.5 * LUG_BEND, -1.0 * L2Y);
	
	public final static Double LUG_LENGTH = 3.0 * SCALE;
	public final static Real2 VECT3 = new Real2(LUG_LENGTH, 0.0);
	public final static Double LUG_END = 3.0 * SCALE;
	public final static Real2 VECT4 = new Real2(LUG_END, 0.0);
	
	public final static String QUOTE = ""
			+ "Never doubt that a small group of thoughtful, committed citizens can change the world;"
			+ " indeed, it's the only thing that ever has. Margaret Mead.";
	public static final int NX = 12;
	public static final int OFFSETX = 50;
	public static final int DX = 100;
	public final static int NY = 6;
	public static final int OFFSETY = 50;
	public static final int DY = 100;
	
	private Real2 currentPoint;
	private SVGG box;
	public double fontSize = 20;
	private int currentChar;
	
	
	public static void main(String[] args) {
		Mead quote = new Mead();
		SVGG g;
	}

	public Mead() {
		drawRect();
		SVGSVG.wrapAndWriteAsSVG(box, new File("target/puzzle/mead.svg"));
	}
	
	private void drawRect() {
		currentChar = 0;
		box = new SVGG();
		SVGRect rect = SVGRect.createFromReal2Range(
				new Real2Range(
						new RealRange(OFFSETX, OFFSETX + NX * DX),
						new RealRange(OFFSETY, OFFSETY + NY * DY)
						)
			);
		rect.setFill("none");
		rect.setStrokeWidth(1.0);
		rect.setStroke("black");
		box.appendChild(rect); 
		
		for (int j = 0; j <NY; j++) {
			for (int i = 0; i < NX; i++) {
				// vertical
				if (i < NX && j > 0) {
					addLine(
							new Real2(OFFSETX + (i) * DX, OFFSETY + j * DY),
							new Real2(OFFSETX + (i) * DX, OFFSETY + (j + 1) * DY),
							new Angle(0.0),
							new Real2(0.0, DY * 0.0),
							"black"
							);
				}
				// horizontal
				if (i < NX - 1 && j  < NY) {
					addLine(
							new Real2(OFFSETX + (i) * DX, OFFSETY + (j + 1) * DY),
							new Real2(OFFSETX + (i + 1) * DX, OFFSETY + (j + 1) * DY),
							new Angle(Math.PI / 2.),
							new Real2(DX * 1.0, 0.0),
							"black"
							);
				}
				Real2 centre = new Real2(OFFSETX + DX * i + DX/2, OFFSETY + DY * j + DY/2);
				Real2 dxy = new Real2(DX/4., DY/4.);
				if (currentChar >= QUOTE.length()) return; 
				drawLine(centre, dxy, -1.);
				if (currentChar >= QUOTE.length()) return; 
				drawLine(centre, dxy, 1.);
			}
		}
	}

	private void drawLine(Real2 centre, Real2 dxy, double mult) {
		SVGText text = new SVGText(centre.plus(dxy.multiplyBy(mult)), String.valueOf(QUOTE.charAt(currentChar++)));
		text.setFontSize(fontSize);
		text.setFontFamily("helvetica");
		text.setFontWeight(FontWeight.BOLD);
		text.setFill("none");
//		text.setFill("red");
		text.setStrokeWidth(0.1);
		text.setStroke("blue");
		box.appendChild(text);
	}

	private void addLine(Real2 xy0, Real2 xy1, Angle angle, Real2 dxy, String col) {
		SVGLine line = new SVGLine(xy0, xy1);
		line.setWidth(1.0);
		line.setStroke("black");
//		box.appendChild(line);
		SVGElement lugs = (SVGElement) drawEdge(xy0.plus(dxy), 1.0, angle, 0);
		lugs.setStroke(col);
		box.appendChild(lugs);
		
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
//		c.setFill("red");
//		g.appendChild(c);
		return g;
	}
	

	private AbstractCMElement drawEdge(Real2Array ra) {
		AbstractCMElement g = new SVGG();
		SVGElement line = createLine(ra.get(0), ra.get(1));
		g.appendChild(line);
		SVGPath path = createQuadPath(ra.get(1), ra.get(2), ra.get(3));
		g.appendChild(path);
		path = createQuadPath(ra.get(3), ra.get(4), ra.get(5));
//		path.setStroke("blue");
		g.appendChild(path);
		line = createLine(ra.get(5), ra.get(6));
		g.appendChild(line);
		path = createQuadPath(ra.get(6), ra.get(7), ra.get(8));
		g.appendChild(path);
		path = createQuadPath(ra.get(8), ra.get(9), ra.get(10));
//		path.setStroke("blue");
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
		path.setStroke("black");
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
