package org.contentmine.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.junit.Assert;
import org.junit.Test;




public class GenericChunkTest {
	private static final Logger LOG = Logger.getLogger(GenericChunkTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testTDBlockBBox() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		Real2Range bbox = genericChunk.getBoundingBox();
		bbox.format(3);
		Assert.assertEquals("bbox", "((70.243,268.462),(124.838,288.219))", bbox.toString());
	}

	@Test
	public void testTDBlockHorizontalGaps() {
		if (1 == 1) {
			LOG.error("FIXME regression");
			return;
		}

		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		RealRangeArray horizontalMask = genericChunk.createHorizontalMask();
		RealArray gaps = horizontalMask.getGaps();
		gaps.format(3);
		Assert.assertEquals("horizontalMask", "(32.511,43.518,52.118)", gaps.toString());
	}

	@Test
	public void testTDBlockHorizontalInverseMask() {
		if (1 == 1) {
			LOG.error("FIXME test regression");
			return;
		}

		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		RealRangeArray horizontalInverseMask = genericChunk.createHorizontalInverseMask(TableFixtures.PAGE_BOX);
		horizontalInverseMask.format(3);
		Assert.assertEquals("horizontalInverseMask", "Direction: HORIZONTAL; size: 5\n"+
	    "((0.0,70.243)(102.02,134.531)(146.629,190.147)(203.568,255.686)(269.107,600.0)\n)", horizontalInverseMask.toString());
	}

	@Test
	public void testTDBlockHorizontalMask() {
		if (1 == 1) {
			LOG.error("FIXME regression");
			return;
		}
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		RealRangeArray horizontalMask = genericChunk.createHorizontalMask();
		horizontalMask.format(3);
		Assert.assertEquals("horizontalMask", "Direction: HORIZONTAL; size: 4\n"+
	    "((70.243,102.02)(134.531,146.629)(190.147,203.568)(255.686,269.107))", horizontalMask.toString());
	}

	@Test
	public void testTDBlockVerticalGaps() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		RealRangeArray verticalMask = genericChunk.createVerticalMask();
		RealArray gaps = verticalMask.getGaps();
		gaps.format(3);
		Assert.assertEquals("verticalMask", 
				"(3.994,3.993,3.937,3.994,3.993,3.993,3.993,3.994,3.993,3.993,3.938,3.993,3.993)", gaps.toString());
	}

	@Test
	public void testTDBlockVerticalInverseMask() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		RealRangeArray verticalInverseMask = genericChunk.createVerticalInverseMask(TableFixtures.PAGE_BOX);
		verticalInverseMask.format(3);
		Assert.assertEquals("verticalInverseMask", "Direction: VERTICAL; size: 15\n"+
	    "((0.0,124.838)(132.808,136.802)(144.772,148.765)(156.735,160.672)(168.642,172.636)\n"+
	    "(180.606,184.599)(192.569,196.562)(204.532,208.525)(216.495,220.489)(228.459,232.452)\n"+
	    "(240.422,244.415)(252.385,256.323)(264.293,268.286)(276.256,280.249)(288.219,800.0)\n)"
	    , verticalInverseMask.toString());
	}

	@Test
	public void testTDBlockVerticalMask() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		RealRangeArray verticalMask = genericChunk.createVerticalMask();
		verticalMask.format(3);
		Assert.assertEquals("horizontalMask", "Direction: VERTICAL; size: 14\n"+
	    "((124.838,132.808)(136.802,144.772)(148.765,156.735)(160.672,168.642)(172.636,180.606)\n"+
	    "(184.599,192.569)(196.562,204.532)(208.525,216.495)(220.489,228.459)(232.452,240.422)\n"+
	    "(244.415,252.385)(256.323,264.293)(268.286,276.256)(280.249,288.219))", verticalMask.toString());
	}

	
}
