package org.contentmine.graphics.svg.misc;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;

public class Jigsaw {

	private static final Logger LOG = Logger.getLogger(Jigsaw.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private int rows = 10;
	private int cols = 10;
	private int width = 400;
	private int height = 400;
	private int tol = 3;
	private int colWidth;
	private int rowHeight;
	private Int2[][] xycoords;
	
	public Jigsaw() {
		
	}

	public static void main(String[] args) {
		Jigsaw jigsaw = new Jigsaw();
		test1(jigsaw);
	}

	private static void test1(Jigsaw jigsaw) {
		jigsaw.setRows(12);
		jigsaw.setCols(12);
		jigsaw.setWidth(1600);
		jigsaw.setHeight(1200);
		jigsaw.setTol(10);
		SVGG g = jigsaw.draw();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/jigsaw.svg"));
	}
	private SVGG draw() {
		SVGG svgg = new SVGG();
		colWidth = width / cols;
		int x;
		rowHeight = height / rows;
		int y;
		createCoords();
		SVGLine line = null;
		
		Real2 offset = new Real2(10., 10.);
		for (int irow = 0; irow <= rows; irow++) {
			for (int jcol = 0; jcol <= cols; jcol++) {
				Real2 xy00 = offset.plus(new Real2(xycoords[irow][jcol]));
				Real2 xy01 = (jcol == cols) ? null : 
					offset.plus(new Real2(xycoords[irow][jcol + 1]));
				Real2 xy10 = (irow == rows) ? null :
					offset.plus(new Real2(xycoords[irow + 1][jcol]));
				if (xy10 != null) {
					line = new SVGLine(xy00, xy10);
					line.setStroke("blue").setStrokeWidth(1.0);
					svgg.appendChild(line);
				}
				if (xy01 != null) {
					line = new SVGLine(xy00, xy01);
					line.setStroke("red").setStrokeWidth(3.0);
					svgg.appendChild(line);
				}
			}
		}
		return svgg;
	}

	private void createCoords() {
		xycoords = new Int2[rows + 1][];
		for (int irow = 0; irow <= rows; irow++) {
			xycoords[irow] = new Int2[cols + 1];
		}
		for (int irow = 0; irow <= rows; irow++) {
			int y = irow * colWidth;
			for (int jcol = 0; jcol <= cols; jcol++) {
				int dy = irow == 0 || irow == rows ? 0 : jitter();
				y += dy;
				int x = jcol * rowHeight;
				int dx = jcol == 0 || jcol == cols ? 0 : jitter();
				x += dx;
				xycoords[irow][jcol] = new Int2(x + dx, y + dy);
			}
		}
	}

	private int jitter() {
		double r = Math.random() - 0.5;
		return (int) (r * (double) tol);
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getTol() {
		return tol;
	}

	public void setTol(int tol) {
		this.tol = tol;
	}

}
