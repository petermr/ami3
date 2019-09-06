package org.contentmine.eucl.euclid.euclid;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeList;
import org.junit.Assert;
import org.junit.Test;

/** tests RealRangeList.
 * 
 * @author pm286
 *
 */
public class RealRangeListTest {

	private final static Logger LOG = Logger.getLogger(RealRangeListTest.class);
	
	@Test
	public void testAddInitialRange() {
		RealRangeList realRangeList = new RealRangeList();
		Assert.assertEquals("initial size", 0, realRangeList.size());
		RealRange range = new RealRange(3., 5.);
		int result = realRangeList.addRange(range);
		Assert.assertEquals("initial", 0, result);
		Assert.assertEquals("new size", 1, realRangeList.size());
	}
		
	@Test
	public void testAddHighest() {
		RealRangeList realRangeList = new RealRangeList();
		RealRange range = new RealRange(3., 5.);
		int result = realRangeList.addRange(range);
		Assert.assertTrue("range", range.isEqualTo(realRangeList.get(0), 0.01));
		range = new RealRange(7., 10.);
		result = realRangeList.addRange(range);
		Assert.assertEquals("highest", 1, result);
		Assert.assertEquals("new size", 2, realRangeList.size());
		Assert.assertTrue("range", range.isEqualTo(realRangeList.get(1), 0.01));
	}

	@Test
	public void testAddLowest() {
		RealRangeList realRangeList = new RealRangeList();
		RealRange range = new RealRange(13., 15.);
		int result = realRangeList.addRange(range);
		Assert.assertTrue("range", range.isEqualTo(realRangeList.get(0), 0.01));
		range = new RealRange(7., 10.);
		result = realRangeList.addRange(range);
		Assert.assertEquals("lowest", 0, result);
		Assert.assertEquals("new size", 2, realRangeList.size());
		Assert.assertTrue("range0", range.isEqualTo(realRangeList.get(0), 0.01));
		Assert.assertTrue("range1", new RealRange(13., 15.).isEqualTo(realRangeList.get(1), 0.01));
	}

	@Test
	public void testToString() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(13., 15.));
		realRangeList.addRange(new RealRange(7., 10.));
		Assert.assertEquals("string", "[(7.0,10.0), (13.0,15.0)]", realRangeList.toString());
	}

	@Test
	public void testAddMiddleNoOverlap() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(3., 5.));
		realRangeList.addRange(new RealRange(8., 10.));
		realRangeList.addRange(new RealRange(13., 15.));
		realRangeList.addRange(new RealRange(18., 20.));
		Assert.assertEquals("string", "[(3.0,5.0), (8.0,10.0), (13.0,15.0), (18.0,20.0)]", realRangeList.toString());
		int result = realRangeList.addRange(new RealRange(6., 7.));
		Assert.assertEquals("insert", 1, result);
		Assert.assertEquals("string", "[(3.0,5.0), (6.0,7.0), (8.0,10.0), (13.0,15.0), (18.0,20.0)]", realRangeList.toString());
		result = realRangeList.addRange(new RealRange(11., 12.));
		Assert.assertEquals("insert", 3, result);
		Assert.assertEquals("string", "[(3.0,5.0), (6.0,7.0), (8.0,10.0), (11.0,12.0), (13.0,15.0), (18.0,20.0)]", realRangeList.toString());
	}

	@Test
	public void testAddRandomOrderNoOverlap() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(13., 15.));
		realRangeList.addRange(new RealRange(3., 5.));
		realRangeList.addRange(new RealRange(18., 20.));
		realRangeList.addRange(new RealRange(8., 10.));
		Assert.assertEquals("string", "[(3.0,5.0), (8.0,10.0), (13.0,15.0), (18.0,20.0)]", realRangeList.toString());
	}


	@Test
	public void testAddOverlapAbove() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(3., 5.));
		RealRange range = new RealRange(4., 6.);
		realRangeList.addRange(range);
		Assert.assertEquals("string", "[(3.0,6.0)]", realRangeList.toString());
	}

	@Test
	public void testAddOverlapBelow() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(4., 6.));
		RealRange range = new RealRange(3., 5.);
		realRangeList.addRange(range);
		Assert.assertEquals("string", "[(3.0,6.0)]", realRangeList.toString());
	}

	@Test
	public void testAddOverlapMiddle() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(1., 2.));
		realRangeList.addRange(new RealRange(4., 6.));
		RealRange range = new RealRange(3., 5.);
		realRangeList.addRange(range);
		Assert.assertEquals("string", "[(1.0,2.0), (3.0,6.0)]", realRangeList.toString());
	}

	@Test
	public void testAddOverlapMiddleDown() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(0., 2.));
		realRangeList.addRange(new RealRange(4., 6.));
		RealRange range = new RealRange(1., 3.);
		realRangeList.addRange(range);
		Assert.assertEquals("string", "[(0.0,3.0), (4.0,6.0)]", realRangeList.toString());
	}

	@Test
	public void testAddMultipleOverlap() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(0., 2.));
		realRangeList.addRange(new RealRange(4., 5.));
		realRangeList.addRange(new RealRange(6., 7.));
		realRangeList.addRange(new RealRange(8., 9.));
		//
		RealRange range = new RealRange(3.0, 6.5);
		realRangeList.addRange(range);
		// [(0.0,2.0), (3.0,7.0), (6.0,7.0), (8.0,9.0)]
		Assert.assertEquals("string", "[(0.0,2.0), (3.0,7.0), (8.0,9.0)]", realRangeList.toString());
	}

	@Test
	public void testAddMultipleOverlap1() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(0., 2.));
		realRangeList.addRange(new RealRange(4., 5.));
		realRangeList.addRange(new RealRange(6., 7.));
		realRangeList.addRange(new RealRange(8., 9.));
		realRangeList.addRange(new RealRange(10., 11.));
		//
		RealRange range = new RealRange(3.0, 8.5);
		realRangeList.addRange(range);
		//[(0.0,2.0), (3.0,9.0), (6.0,7.0), (8.0,9.0), (10.0,11.0)]
		Assert.assertEquals("string", "[(0.0,2.0), (3.0,9.0), (10.0,11.0)]", realRangeList.toString());
	}

	@Test
	public void testAddLowerEnd() {
		RealRangeList realRangeList = new RealRangeList();
		realRangeList.addRange(new RealRange(68.0,101.0));
		RealRange range = new RealRange(68.0,78.0);
		realRangeList.addRange(range);
		Assert.assertEquals("string", "[(68.0,101.0)]", realRangeList.toString());
	}

}
