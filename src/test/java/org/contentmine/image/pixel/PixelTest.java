package org.contentmine.image.pixel;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PixelTest {

	private PixelIsland island;
	private Pixel[] pixel;

	@Before
	public void setUp() {
		island = new PixelIsland();
		island.setDiagonal(true);
		pixel = new Pixel[6];
		pixel[0] = new Pixel(new Point(0,0));
		pixel[1] = new Pixel(new Point(0,1));
		pixel[2] = new Pixel(new Point(1,0));
		pixel[3] = new Pixel(new Point(1,1));
		pixel[4] = new Pixel(new Point(2,0));
		pixel[5] = new Pixel(new Point(2,1));
		island.addPixelAndComputeNeighbourNeighbours(pixel[0]);
		island.addPixelAndComputeNeighbourNeighbours(pixel[1]);
		island.addPixelAndComputeNeighbourNeighbours(pixel[2]);
		island.addPixelAndComputeNeighbourNeighbours(pixel[3]);
		island.addPixelAndComputeNeighbourNeighbours(pixel[4]);
		island.addPixelAndComputeNeighbourNeighbours(pixel[5]);
	}

	@Test
	public void testOrthogonalGrid() {
		Assert.assertTrue(pixel[0].isOrthogonalNeighbour(pixel[1]));
		Assert.assertTrue(pixel[0].isOrthogonalNeighbour(pixel[2]));
		Assert.assertFalse(pixel[1].isOrthogonalNeighbour(pixel[2]));
	}

	@Test
	public void testDiagonal() {
		Assert.assertFalse(pixel[0].isDiagonalNeighbour(pixel[1]));
		Assert.assertTrue(pixel[1].isDiagonalNeighbour(pixel[2]));
	}
	

	@Test
	public void testKnightsMove() {
		Assert.assertFalse(pixel[0].isKnightsMove(pixel[1], pixel[5]));
		Assert.assertTrue(pixel[0].isKnightsMove(pixel[2], pixel[5]));
		Assert.assertTrue(pixel[1].isKnightsMove(pixel[2], pixel[4]));
		Assert.assertTrue(pixel[1].isKnightsMove(pixel[3], pixel[4]));
	}
}
