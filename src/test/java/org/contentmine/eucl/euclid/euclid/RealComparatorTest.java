package org.contentmine.eucl.euclid.euclid;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.contentmine.eucl.euclid.RealComparator;
import org.junit.Assert;
import org.junit.Test;

public class RealComparatorTest {

	public final static Double ZERO = 0.0;
	public final static Double ONE = 1.0;
	public final static Double TWO = 2.0;
	public final static Double THREE = 3.0;
	public final static Double SQR3 = Math.sqrt(THREE);
	
	@Test
	public void comparatorTest() {
		RealComparator comparator = new RealComparator(ZERO);
		Assert.assertEquals(0, comparator.compare(ONE, ONE));
		Assert.assertEquals(1, comparator.compare(ONE, ZERO));
		Assert.assertFalse(comparator.compare(THREE, SQR3*SQR3) == 0);
		Assert.assertEquals(-1, comparator.compare(ONE, TWO));
	}
	
	@Test
	public void comparatorTest1() {
		RealComparator comparator = new RealComparator(0.01);
		Assert.assertEquals(0, comparator.compare(ONE, new Double(1.001)));
		Assert.assertEquals(0, comparator.compare(ONE, new Double(0.999)));
		Assert.assertEquals(0, comparator.compare(THREE, SQR3*SQR3));
		Assert.assertEquals(1, comparator.compare(ONE, new Double(0.98)));
		Assert.assertEquals(-1, comparator.compare(ONE, new Double(1.02)));
	}
	
	/** HashSet only works with exactness
	 * 
	 */
	@Test
	public void testHashSet(){
		Set<Double> set = new HashSet<Double>();
		set.add(new Double(1.0));
		set.add(new Double(1.0));
		Assert.assertEquals(1, set.size());
	}
	
	/** HashSet only works with exactness
	 * 
	 */
	@Test
	public void testHashSet1(){
		Set<Double> set = new HashSet<Double>();
		set.add(new Double(1.0));
		set.add(new Double((Math.sqrt(3.0)*Math.sqrt(3.0))/3.0));
		Assert.assertEquals(2, set.size());
	}
	
	/** TreeSet works
	 * 
	 */
	@Test
	public void testTreeSet(){
		RealComparator comparator = new RealComparator(0.0);
		Set<Double> set = new TreeSet<Double>(comparator);
		set.add(ONE);
		set.add(ONE);
		set.add(THREE);
		Assert.assertEquals(2, set.size());
	}
	
	/** TreeSet works
	 * 
	 */
	@Test
	public void testTreeSet1(){
		RealComparator comparator = new RealComparator(0.0);
		Set<Double> set = new TreeSet<Double>(comparator);
		set.add(ONE);
		set.add(ONE-0.001);
		set.add(THREE);
		Assert.assertEquals(3, set.size());
	}
	
	/** TreeSet works
	 * 
	 */
	@Test
	public void testTreeSet2(){
		RealComparator comparator = new RealComparator(0.01);
		Set<Double> set = new TreeSet<Double>(comparator);
		set.add(ONE);
		set.add(ONE - 0.001);
		set.add(THREE);
		Assert.assertEquals(2, set.size());
	}
	
	/** TreeSet works
	 * 
	 */
	@Test
	public void testTreeSet3(){
		RealComparator comparator = new RealComparator(0.01);
		Set<Double> set = new TreeSet<Double>(comparator);
		set.add(ONE - 0.001);
		set.add(ONE);
		set.add(THREE);
		Assert.assertEquals(2, set.size());
	}
	
	/** TreeSet works
	 * 
	 */
	@Test
	public void testTreeSetContains(){
		RealComparator comparator = new RealComparator(0.01);
		Set<Double> set = new TreeSet<Double>(comparator);
		set.add(ONE - 0.001);
		set.add(ONE);
		set.add(THREE);
		Assert.assertEquals(2, set.size());
		Assert.assertTrue(set.contains(1.0));
		Assert.assertTrue(set.contains(0.995));
	}
	
}
