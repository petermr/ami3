package org.xmlcml.args.sandpit;

import junit.framework.Assert;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.RealArray;

public class SandpitArgProcessorDoubleTest {

	
	private static final Logger LOG = Logger
			.getLogger(SandpitArgProcessorDoubleTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testDouble() {
		String args[] = {"--double", "4.5"};
		SandpitArgProcessor sandpitArgProcessor = new SandpitArgProcessor(args);
		Assert.assertEquals("double", 4.5, sandpitArgProcessor.getDouble(), 0.0001);
	}
	
	@Test
	public void testDoubleOutOfRange() {
		String args[] = new String[]{"--double", "2.0"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Illegal value");
		} catch (RuntimeException e) {
			Assert.assertEquals("illegal value", 
					"cannot process argument: --double (IllegalArgumentException: --double; value: 2.0 incompatible with: (3.1,7.2))", 
					e.getMessage());
		}
		args = new String[]{"--double", "12.0"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Illegal value");
		} catch (RuntimeException e) {
			Assert.assertEquals("illegal value", 
					"cannot process argument: --double (IllegalArgumentException: --double; value: 12.0 incompatible with: (3.1,7.2))", 
					e.getMessage());
		}
	}
	
	@Test
	public void testBadDouble() {
		String args[] = new String[]{"--double", "foo"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Illegal value");
		} catch (RuntimeException e) {
			Assert.assertEquals("illegal value", 
					"cannot process argument: --double (IllegalArgumentException: --double; value: foo incompatible with: (3.1,7.2))", e.getMessage());
		}
	}

	@Test
	public void testTooManyDoubles() {
		String[] args = new String[]{"--double", "4.0", "7.0"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Too many values");
		} catch (RuntimeException e) {
			Assert.assertEquals("too many doubles", 
					"cannot process argument: --double (IllegalArgumentException: --double; "
					+ "argument count (2) is not compatible with {1,1})", e.getMessage());
		}
	}
	
	@Test
	public void testDoubleArray() {
		String[] args = new String[]{"--doublearray", "4.0", "7.0", "5.0"};  
		try {
			SandpitArgProcessor sandpitArgProcessor = new SandpitArgProcessor(args);
			RealArray doubleArray = sandpitArgProcessor.getDoubleArray();
			Assert.assertTrue("array values", new RealArray(new double[]{4.0, 7.0, 5.0}).equals(doubleArray, 0.001));
		} catch (RuntimeException e) {
			Assert.fail("should not throw "+e);
		}
	}

	@Test
	public void testBadDoubleArray() {
		String[] args = new String[]{"--doublearray", "4.0", "7.0", "foo", "-4.0"};  
		try {
			SandpitArgProcessor sandpitArgProcessor = new SandpitArgProcessor(args);
			Assert.fail("should throw bad element exception");
		} catch (RuntimeException e) {
			Assert.assertEquals("bad element", 
					"cannot process argument: --doublearray (IllegalArgumentException: --doublearray; value: foo incompatible with: (3.1,7.2))",
					e.getMessage());
		}
	}

}
