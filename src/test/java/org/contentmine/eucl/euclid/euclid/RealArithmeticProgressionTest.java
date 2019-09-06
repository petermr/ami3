package org.contentmine.eucl.euclid.euclid;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealArithmeticProgression;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.junit.Test;

import com.google.common.collect.Multiset;

import junit.framework.Assert;

/** tests RealArithmeticProgression
 * 
 * @author pm286
 *
 */
public class RealArithmeticProgressionTest {
	private static final Logger LOG = Logger.getLogger(RealArithmeticProgressionTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static double EPS = 0.000001;
	
	@Test
	public void testArithmeticProgressionNull() {
		RealArithmeticProgression arithmeticProgression = new RealArithmeticProgression();
		Assert.assertNull("start", arithmeticProgression.getStart());
		Assert.assertNull("size", arithmeticProgression.getSize());
		Assert.assertNull("delta", arithmeticProgression.getDelta());
	}
		
	@Test
	public void testArithmeticProgression() {
		RealArithmeticProgression arithmeticProgression = new RealArithmeticProgression(1.0, 0.1, 10);
		double start = arithmeticProgression.getStart();
		Assert.assertEquals("0", start, 1.0, EPS);
		double first = arithmeticProgression.getTerm(1);
		Assert.assertEquals("0", first, 1.1, EPS);
		double end = arithmeticProgression.getEnd();
		Assert.assertEquals("0", end, 1.9, EPS);
	}
		
	@Test
	public void testArithmeticProgressionNoSize() {
		RealArithmeticProgression arithmeticProgression = new RealArithmeticProgression(1.0, 0.1);
		double start = arithmeticProgression.getStart();
		Assert.assertEquals("0", start, 1.0, EPS);
		double first = arithmeticProgression.getTerm(1);
		Assert.assertEquals("1", first, 1.1, EPS);
		Double end = arithmeticProgression.getEnd();
		Assert.assertNull("end", arithmeticProgression.getEnd());
		
	}

	@Test
	public void testCreateFromRealArray() {
		RealArray realArray = new RealArray("1 2 3 4 5");
		double epsilon = 0.1;
		RealArithmeticProgression arithmeticProgression = RealArithmeticProgression.createAP(realArray, epsilon);
		Assert.assertEquals("ap", "1.0(1.0)*5", arithmeticProgression.toString());
		realArray = new RealArray("1.1 2.05 2.97 4.03 4.99");
		epsilon = 0.1;
		arithmeticProgression = RealArithmeticProgression.createAP(realArray, epsilon);
		Assert.assertEquals("ap", "1.1(0.9725)*5", arithmeticProgression.toString());
		Assert.assertEquals("0", 1.1, arithmeticProgression.getStart(), 0.001);
		Assert.assertEquals("0", 4.99, arithmeticProgression.getEnd(), 0.001);
		
		RealArray realArrayOut = arithmeticProgression.getRealArray();
		realArrayOut.format(3);
		Assert.assertEquals("realArray", "(1.1,2.073,3.045,4.018,4.99)", realArrayOut.toString());
		Assert.assertEquals("delta", 0.9725, arithmeticProgression.getDelta(), 0.0001);
	}

	@Test
	public void testCreateFromMultipleRealArrays0() {
		RealArray realArray = new RealArray("1 2 3 4 5  8 9 10  13 14 15 16");
		double epsilon = 0.1;
		RealArithmeticProgression arithmeticProgression = RealArithmeticProgression.createAP(realArray, epsilon);
		Assert.assertNull("cannot parse", arithmeticProgression);
		Multiset<Double> diffs = realArray.createDoubleDifferenceMultiset(1);
		List<Multiset.Entry<Double>> diffList = MultisetUtil.createListSortedByCount(diffs);
		Assert.assertEquals("sorted", "[1.0 x 9, 3.0 x 2]", diffList.toString());
		Multiset.Entry<Double> diff0 = diffList.get(0);
		Assert.assertEquals("commonest", "1.0 x 9", diff0.toString());
		Assert.assertEquals("commonest", 1.0, diff0.getElement(), 0.0001);
		Assert.assertEquals("commonest", 9, diff0.getCount());
		Multiset.Entry<Double> diff1 = diffList.get(1);
		Assert.assertEquals("next", "3.0 x 2", diff1.toString());
		Assert.assertEquals("next", 3.0, diff1.getElement(), 0.0001);
		Assert.assertEquals("next", 2, diff1.getCount());
	}


}
