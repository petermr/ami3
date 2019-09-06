package org.xmlcml.args.sandpit;

import junit.framework.Assert;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.euclid.IntArray;

public class SandpitArgProcessorIntegerTest {

	
	private static final Logger LOG = Logger
			.getLogger(SandpitArgProcessorIntegerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testInteger() {
		String args[] = {"--integer", "4"};
		SandpitArgProcessor sandpitArgProcessor = new SandpitArgProcessor(args);
		Assert.assertEquals("int", 4, (int)sandpitArgProcessor.getInteger());
	}
	
	@Test
	public void testIntegerOutOfRange() {
		String args[] = new String[]{"--integer", "2"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Illegal value");
		} catch (RuntimeException e) {
			Assert.assertEquals("illegal value", 
					"cannot process argument: --integer (IllegalArgumentException: --integer; value: 2 incompatible with: (3,7))", 
					e.getMessage());
		}
		args = new String[]{"--integer", "12"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Illegal value");
		} catch (RuntimeException e) {
			Assert.assertEquals("illegal value", 
					"cannot process argument: --integer (IllegalArgumentException: --integer; value: 12 incompatible with: (3,7))", 
					e.getMessage());
		}
	}
	
	@Test
	public void testBadInteger() {
		String args[] = new String[]{"--integer", "foo"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Illegal value");
		} catch (RuntimeException e) {
			Assert.assertEquals("illegal value", 
					"cannot process argument: --integer (IllegalArgumentException: --integer; value: foo incompatible with: (3,7))", e.getMessage());
		}
	}

	@Test
	public void testTooManyIntegers() {
		String[] args = new String[]{"--integer", "4", "7"};  // will be out of range
		try {
			new SandpitArgProcessor(args);
			Assert.fail("Should throw RuntimeException; Too many values");
		} catch (RuntimeException e) {
			Assert.assertEquals("too many ints", 
					"cannot process argument: --integer (IllegalArgumentException: --integer; "
					+ "argument count (2) is not compatible with {1,1})", e.getMessage());
		}
	}
	
	@Test
	public void testIntegerArray() {
		String[] args = new String[]{"--integerarray", "4", "7", "5"};  
		try {
			SandpitArgProcessor sandpitArgProcessor = new SandpitArgProcessor(args);
			IntArray intArray = sandpitArgProcessor.getIntArray();
			Assert.assertTrue("array values", new IntArray(new int[]{4, 7, 5}).equals(intArray));
		} catch (RuntimeException e) {
			Assert.fail("should not throw "+e);
		}
	}

	@Test
	public void testBadIntegerArray() {
		String[] args = new String[]{"--integerarray", "4", "7", "foo", "-4"};  
		try {
			new SandpitArgProcessor(args);
			Assert.fail("should throw bad element exception");
		} catch (RuntimeException e) {
			Assert.assertEquals("bad element", 
					"cannot process argument: --integerarray (IllegalArgumentException: --integerarray; value: foo incompatible with: (3,7))",
					e.getMessage());
		}
	}

	@Test
	public void testIntegerArrayValueOUtOfRange() {
		String[] args = new String[]{"--integerarray", "4", "7", "-3", "6"};  
		try {
			new SandpitArgProcessor(args);
			Assert.fail("should throw bad element exception");
		} catch (RuntimeException e) {
			Assert.assertEquals("bad element", 
					"cannot process argument: --integerarray (IllegalArgumentException: --integerarray; value: -3 incompatible with: (3,7))",
					e.getMessage());
		}
	}

}
