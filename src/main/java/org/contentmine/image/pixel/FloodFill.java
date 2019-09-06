package org.contentmine.image.pixel;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class FloodFill {
	private static final Logger LOG = Logger.getLogger(FloodFill.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	// informed by
	// http://stackoverflow.com/questions/2783204/flood-fill-using-a-stack

	protected boolean[][] painted;
	protected boolean diagonal = false;
	protected PixelIslandList islandList;
	protected int width;
	protected int height;
	private int maxIslands = 5000;

	protected FloodFill(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	protected abstract boolean isBlack(int posX, int posY);
	
	public boolean fillIslands() {
		painted = new boolean[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				addNextUnpaintedBlack(i, j);
				if (islandList.size() >= maxIslands ) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected void addNextUnpaintedBlack(int i, int j) {
		if (isBlack(j, i) && !painted[i][j]) {

			Queue<Point> queue = new LinkedList<Point>();
			queue.add(new Point(j, i));

			PixelList pixelList = new PixelList();
			while (!queue.isEmpty()) {
				Point p = queue.remove();
				if (isInsideImage(p)) {
					if (!painted[p.y][p.x] && isBlack(p.x, p.y)) {
						painted[p.y][p.x] = true;
						pixelList.add(getPixelFromPoint(p));
						addNewPoints(queue, p);
					}
				}
			}
			add(PixelIsland.createSeparateIslandWithClonedPixels(pixelList, true));
		}
	}

	protected Pixel getPixelFromPoint(Point p) {
		return new Pixel(p);
	}

	private void add(PixelIsland island) {
		ensureIslandList();
		islandList.add(island);
		LOG.trace("islandList "+islandList.size());
		return;
	}

	private void ensureIslandList() {
		if (islandList == null) {
			islandList = new PixelIslandList();
		}
	}
	
	public PixelIslandList getIslandList() {
		ensureIslandList();
		if (!fillIslands()) {
			System.err.println(">island overflow> "+maxIslands /* + " maybe requires imageprocessing?"*/);
		}
		for (PixelIsland island : islandList) {
			island.setDiagonal(diagonal);
			island.setIslandList(islandList); // each island knows who made it
		}
		LOG.trace("created islandList");
		islandList.setDiagonal(diagonal);
		return islandList;
	}

	private boolean isInsideImage(Point p) {
		return (p.x >= 0) && (p.x < width && (p.y >= 0) && (p.y < height));
	}

	private void addNewPoints(Queue<Point> queue, Point p) {
		queue.add(new Point(p.x + 1, p.y));
		queue.add(new Point(p.x - 1, p.y));
		queue.add(new Point(p.x, p.y + 1));
		queue.add(new Point(p.x, p.y - 1));
		if (diagonal) {
			queue.add(new Point(p.x + 1, p.y + 1));
			queue.add(new Point(p.x - 1, p.y + 1));
			queue.add(new Point(p.x - 1, p.y - 1));
			queue.add(new Point(p.x + 1, p.y - 1));
		}
	}

	public void setDiagonal(boolean b) {
		this.diagonal = b;
	}

	public int getMaxIslands() {
		return maxIslands;
	}

	public void setMaxIslands(int maxIslands) {
		this.maxIslands = maxIslands;
	}

}