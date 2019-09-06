package org.contentmine.eucl.euclid.euclid;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.junit.Assert;
import org.junit.Test;

public class RealRangeArrayTest {
	private static final Logger LOG = Logger.getLogger(RealRangeArrayTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private RealRange rr10_25 = new RealRange(10.1, 25.1);
	private RealRange rr10_20 = new RealRange(10.1, 20.1);
	private RealRange rr30_40 = new RealRange(30.1, 40.1);
	private RealRange rr15_25 = new RealRange(15.1, 25.1);
	private RealRange rr15_17 = new RealRange(15.1, 17.1);
	private RealRange rr50_60 = new RealRange(50.1, 60.1);
	
	@Test
	public void testAdd() {
		RealRangeArray array = new RealRangeArray();
		Assert.assertEquals(0, array.size());
	}
	
	@Test
	public void testAdd1() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		Assert.assertEquals(1, array.size());
	}
	
	@Test
	public void testAdd2() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		array.add(rr15_25);
		Assert.assertEquals(2, array.size());
	}
	
	@Test
	public void testEquals() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		array.add(rr15_25);
		RealRangeArray array2 = new RealRangeArray();
		array2.add(rr10_20);
		array2.add(rr15_25);
		Assert.assertTrue(array.equals(array2));
	}
	
	@Test
	/**
	 * order of addition matters
	 */
	public void testNotEquals() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		array.add(rr15_25);
		RealRangeArray array2 = new RealRangeArray();
		array2.add(rr15_25);
		array2.add(rr10_20);
		Assert.assertFalse("order matters", array.equals(array2));
	}
	
	@Test
	/**
	 * order of addition matters
	 */
	public void testSort() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		array.add(rr15_17);
		array.add(rr15_25);
		array.add(rr30_40);
		array.add(rr50_60);
		RealRangeArray array2 = new RealRangeArray();
		array2.add(rr50_60);
		array2.add(rr15_25);
		array2.add(rr30_40);
		array2.add(rr15_17);
		array2.add(rr10_20);
		Assert.assertFalse("order matters", array.equals(array2));
		array2.sort();
		Assert.assertTrue("after sorting", array.equals(array2));
	}
	
	@Test
	/**
	 * sort and overlap
	 */
	public void testSortAndRemoveOverlapping() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_25);
		array.add(rr30_40);
		array.add(rr50_60);
		RealRangeArray array2 = new RealRangeArray();
		array2.add(rr50_60);
		array2.add(rr15_25);
		array2.add(rr30_40);
		array2.add(rr15_17);
		array2.add(rr10_20);
		Assert.assertFalse("order matters", array.equals(array2));
		array2.sortAndRemoveOverlapping();
		Assert.assertEquals("after sorting", array.toString(), array2.toString());
	}
	
	@Test
	/**
	 */
	public void testPlus() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		array.add(rr30_40);
		array.add(rr50_60);
		RealRangeArray array2 = new RealRangeArray();
		array2.add(rr15_25);
		array2.add(rr15_17);
		RealRangeArray plus = array2.plus(array);
		RealRangeArray ref = new RealRangeArray();
		ref.add(rr10_25);
		ref.add(rr30_40);
		ref.add(rr50_60);
		Assert.assertEquals("after sorting", ref.toString(), plus.toString());
	}
	
	@Test
	/**
	 */
	public void testInverse() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		array.add(rr30_40);
		array.add(rr50_60);
		RealRangeArray inverse = array.inverse();
		RealRangeArray ref = new RealRangeArray();
		ref.add(new RealRange(20.1, 30.1));
		ref.add(new RealRange(40.1, 50.1));
		Assert.assertEquals("after sorting", ref.toString(), inverse.toString());
	}
	
	@Test
	/**
	 */
	public void testInverse0() {
		RealRangeArray array = new RealRangeArray();
		RealRangeArray inverse = array.inverse();
		Assert.assertNull(inverse);
	}
	
	@Test
	/**
	 */
	public void testInverse1() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		RealRangeArray inverse = array.inverse();
		Assert.assertEquals(0, inverse.size());
	}
	@Test
	/**
	 */
	public void testInverse1a() {
		RealRangeArray array = new RealRangeArray();
		array.add(rr10_20);
		array.add(rr15_25);
		RealRangeArray inverse = array.inverse();
		Assert.assertEquals("inverse without caps", 0, inverse.size());
	}
	
	@Test
	/**
	 */
	public void testInverseWithCaps() {
		RealRangeArray array = new RealRangeArray();
		array.add(new RealRange(0.1,0.1));
		array.add(new RealRange(100.1,100.1));
		RealRangeArray inverse = array.inverse();
		Assert.assertEquals(1, inverse.size());
		RealRangeArray ref = new RealRangeArray();
		ref.add(new RealRange(0.1,100.1));
		Assert.assertEquals("inverse", ref.toString(), inverse.toString());
	}
	
	@Test
	/**
	 */
	public void testExtendRanges100() {
		List<RealRange> rangeList = createRealRangeArray();
		RealRangeArray mask = new RealRangeArray(rangeList);
		Assert.assertEquals("raw mask", "Direction: null; size: 14\n"+
"((124.838,132.808)(136.802,144.772)(148.765,156.735)(160.672,168.642)(172.636,180.606)\n"+
"(184.599,192.569)(196.562,204.532)(208.525,216.495)(220.489,228.459)(232.452,240.422)\n"+
"(244.415,252.385)(256.323,264.293)(268.286,276.256)(280.249,288.219))", mask.toString());
		mask.extendRangesBy(100.);
		mask.format(3);
		Assert.assertEquals("raw mask", "Direction: null; size: 14\n"+
"((24.838,134.805)(134.805,146.769)(146.769,158.704)(158.704,170.639)(170.639,182.603)\n"+
"(182.603,194.566)(194.566,206.529)(206.529,218.492)(218.492,230.456)(230.456,242.419)\n"+
"(242.419,254.354)(254.354,266.29)(266.29,278.253)(278.253,388.219))", mask.toString());
	}

	
	@Test
	/**
	 */
	public void testExtendRanges1() {
		List<RealRange> rangeList = createRealRangeArray();
		RealRangeArray mask = new RealRangeArray(rangeList);
		Assert.assertEquals("raw mask", "Direction: null; size: 14\n"+
		"((124.838,132.808)(136.802,144.772)(148.765,156.735)(160.672,168.642)(172.636,180.606)\n"+
		"(184.599,192.569)(196.562,204.532)(208.525,216.495)(220.489,228.459)(232.452,240.422)\n"+
		"(244.415,252.385)(256.323,264.293)(268.286,276.256)(280.249,288.219))", mask.toString());
		mask.extendRangesBy(1.);
		mask.format(3);
		Assert.assertEquals("raw mask", "Direction: null; size: 14\n"+
		"((123.838,133.808)(135.802,145.772)(147.765,157.735)(159.672,169.642)(171.636,181.606)\n"+
		"(183.599,193.569)(195.562,205.532)(207.525,217.495)(219.489,229.459)(231.452,241.422)\n"+
		"(243.415,253.385)(255.323,265.293)(267.286,277.256)(279.249,289.219))", mask.toString());
	}
		
	private static List<RealRange> createRealRangeArray() {
		List<RealRange> rangeList = new ArrayList<RealRange>();
		rangeList.add(new RealRange(124.838,132.808));
		rangeList.add(new RealRange(136.802,144.772));
		rangeList.add(new RealRange(148.765,156.735));
		rangeList.add(new RealRange(160.672,168.642));
		rangeList.add(new RealRange(172.636,180.606));
		rangeList.add(new RealRange(184.599,192.569));
		rangeList.add(new RealRange(196.562,204.532));
		rangeList.add(new RealRange(208.525,216.495));
		rangeList.add(new RealRange(220.489,228.459));
		rangeList.add(new RealRange(232.452,240.422));
		rangeList.add(new RealRange(244.415,252.385));
		rangeList.add(new RealRange(256.323,264.293));
		rangeList.add(new RealRange(268.286,276.256));
		rangeList.add(new RealRange(280.249,288.219));
		return rangeList;
	}
}
	
