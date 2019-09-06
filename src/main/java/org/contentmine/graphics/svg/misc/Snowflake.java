package org.contentmine.graphics.svg.misc;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;

public class Snowflake {

	private static final Logger LOG = Logger.getLogger(Snowflake.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static Transform2 ROT300 = new Transform2(new Angle( 5. * Math.PI / 3.));

	private double scale = 400.0;
	private Real2 points[] = new Real2[] {
		new Real2(0.0, 0.0),
		new Real2(0.0, scale),
		new Real2(scale * Math.sqrt(3.0) * 0.5, scale * 0.5)
	};
	
	public Snowflake(double scale) {
		this.scale = scale;
	}
	
	public SVGG createFlake(int level) {
		SVGG g = new SVGG();
		g.setTransform(new Transform2(new Vector2(scale * 0.5, scale * 0.5)));
		for (int i = 0; i < 3; i++) {
			AbstractCMElement edge = createEdge(level, points[i], points[(i + 1) % 3]);
			g.appendChild(edge);
		}
		return g;
	}
	
	public AbstractCMElement createEdge(int level, Real2 start, Real2 end) {
		
		SVGElement element = null;
		if (level == 0) {
			start = start.format(2);
			end = end.format(4);
			element = new SVGLine(start, end);
			element.setStrokeWidth(2.0);
			element.setStroke("blue");
		} else {
			Real2 s1 = (start.plus(start).plus(end)).multiplyBy(1. / 3.);
			Real2 e1 = (start.plus(end).plus(end)).multiplyBy(1. / 3.);
			Real2 v1 = e1.subtract(s1);
			v1.transformBy(ROT300);
			Real2 m = s1.plus(v1);
			element = new SVGG();
			element.appendChild(createEdge(level - 1, start, s1));
			element.appendChild(createEdge(level - 1, s1, m));
			element.appendChild(createEdge(level - 1, m, e1));
			element.appendChild(createEdge(level - 1, e1, end));
			
		}
		return element;
	}
	
	public static void main(String[] args) {
		Snowflake snowflake = new Snowflake(500.);
		SVGG g = snowflake.createFlake(5);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/snowflake/s5.svg"));
		g = snowflake.createFlake(4);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/snowflake/s4.svg"));
		g = snowflake.createFlake(3);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/snowflake/s3.svg"));
		g = snowflake.createFlake(2);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/snowflake/s2.svg"));
	}
}
