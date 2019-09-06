package org.contentmine.image.pixel;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.contentmine.image.ImageAnalysisFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JunctionTest {

	private final static Logger LOG = Logger.getLogger(JunctionTest.class);
	
	private final static File G002_DIR = new File(ImageAnalysisFixtures.COMPOUND_DIR, "journal.pone.0095565.g002");
	private PixelIsland island1;
	private Pixel p1_00;
	private Pixel p1_10;
	private Pixel p1_20;
	private Pixel p1_11;

	private PixelIsland createIsland1() {
		island1 = new PixelIsland();
		p1_00 = new Pixel(0,0);
		p1_10 = new Pixel(1,0);
		p1_20 = new Pixel(2,0);
		p1_11 = new Pixel(1,1);
		island1.addPixelAndComputeNeighbourNeighbours(p1_00);
		island1.addPixelAndComputeNeighbourNeighbours(p1_10);
		island1.addPixelAndComputeNeighbourNeighbours(p1_20);
		island1.addPixelAndComputeNeighbourNeighbours(p1_11);
		island1.setDiagonal(true);
		return island1;
	}

	
	@Test
	@Ignore // not sure why
	public void testcreateIsland() throws IOException {
		PixelIsland island = createIsland1();
//		island.setDiagonal(true);
		Assert.assertEquals("island", 4, island.size());
		PixelList n00 = p1_00.getOrCreateNeighbours(island1);
		Assert.assertEquals("n00", "{(1,0)(1,1)}", n00.toString());
		PixelList n10 = p1_10.getOrCreateNeighbours(island1);
		Assert.assertEquals("n10", "{(2,0)(0,0)(1,1)}", n10.toString());
		PixelList n20 = p1_20.getOrCreateNeighbours(island1);
		Assert.assertEquals("n20", "{(1,0)(1,1)}", n20.toString());
		PixelList n11 = p1_11.getOrCreateNeighbours(island1);
		Assert.assertEquals("n11", "{(1,0)(2,0)(0,0)}", n11.toString());
	}

}
