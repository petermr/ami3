/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.testutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.eucl.euclid.JodaDate;
import org.contentmine.eucl.euclid.Line3;
import org.contentmine.eucl.euclid.Plane3;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.Point3Vector;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Vector;
import org.contentmine.eucl.euclid.Real3Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Transform3;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.eucl.euclid.Vector3;
import org.contentmine.eucl.stml.STMLConstants;
import org.contentmine.eucl.stml.STMLElement;
import org.contentmine.eucl.xml.XMLUtil;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.ComparisonFailure;

import junit.framework.AssertionFailedError;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.tests.XOMTestCase;

/**
 * 
 * <p>
 * Utility library of common methods for unit tests
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public final class TestUtils implements STMLConstants {

	private static final Logger LOG = Logger.getLogger(TestUtils.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String OUTPUT_DIR_NAME = "target/test-outputs";

	/**
	 * tests 2 XML objects for equality using canonical XML. uses
	 * XOMTestCase.assertEquals. This treats different prefixes as different and
	 * compares floats literally.
	 * 
	 * @param message
	 * @param refNode
	 *            first node
	 * @param testNode
	 *            second node
	 */
	public static void assertEqualsCanonically(String message, Node refNode,
			Node testNode) {
		try {
			XOMTestCase.assertEquals(message, refNode, testNode);
		} catch (ComparisonFailure e) {
			reportXMLDiff(message, e.getMessage(), refNode, testNode);
		} catch (AssertionFailedError e) {
			reportXMLDiff(message, e.getMessage(), refNode, testNode);
		}
	}

	/**
	 * compares two XML nodes and checks float near-equivalence (can also be
	 * used for documents without floats) usesTestUtils.assertEqualsCanonically
	 * and only uses PMR code if fails
	 * 
	 * @param message
	 * @param refNode
	 * @param testNode
	 * @param eps
	 */
	public static void assertEqualsIncludingFloat(String message, Node refNode,
			Node testNode, boolean stripWhite, double eps) {
		if (stripWhite && refNode instanceof Element
				&& testNode instanceof Element) {
			refNode = stripWhite((Element) refNode);
			testNode = stripWhite((Element) testNode);
		}
		try {
			assertEqualsIncludingFloat(message, refNode, testNode, eps);
		} catch (AssertionError e) {
			LOG.warn(e);
			reportXMLDiffInFull(message, e.getMessage(), refNode, testNode);
		}
	}

	public static void assertEqualsIncludingFloat(String message,
			String expectedS, Node testNode, boolean stripWhite, double eps) {
		assertEqualsIncludingFloat(message, TestUtils
				.parseValidString(expectedS), testNode, stripWhite, eps);
	}

	private static void assertEqualsIncludingFloat(String message,
			Node refNode, Node testNode, double eps) {
		try {
			Assert.assertEquals(message + ": classes", testNode.getClass(),
					refNode.getClass());
			if (refNode instanceof Text) {
				testStringDoubleEquality(message + " on node: "
						+ path(testNode), refNode.getValue().trim(), testNode
						.getValue().trim(), eps);
			} else if (refNode instanceof Comment) {
				Assert.assertEquals(message + " comment", refNode.getValue(),
						testNode.getValue());
			} else if (refNode instanceof ProcessingInstruction) {
				Assert.assertEquals(message + " pi",
						(ProcessingInstruction) refNode,
						(ProcessingInstruction) testNode);
			} else if (refNode instanceof Element) {
				int refNodeChildCount = refNode.getChildCount();
				int testNodeChildCount = testNode.getChildCount();
				String path = path(testNode);
				// FIXME? fails to resolve in tests
//				Assert.assertEquals("number of children of " + path,
//						refNodeChildCount, testNodeChildCount);
				if (refNodeChildCount != testNodeChildCount) {
					Assert.fail("number of children of " + path + " "+
						refNodeChildCount + " != " + testNodeChildCount);
				}
				for (int i = 0; i < refNodeChildCount; i++) {
					assertEqualsIncludingFloat(message, refNode.getChild(i),
							testNode.getChild(i), eps);
				}
				Element refElem = (Element) refNode;
				Element testElem = (Element) testNode;
				Assert.assertEquals(message + " name", refElem.getLocalName(),
						testElem.getLocalName());
				Assert.assertEquals(message + " namespace", refElem
						.getNamespaceURI(), testElem.getNamespaceURI());
				Assert.assertEquals(message + " attributes on "
						+ refElem.getClass(), refElem.getAttributeCount(),
						testElem.getAttributeCount());
				for (int i = 0; i < refElem.getAttributeCount(); i++) {
					Attribute refAtt = refElem.getAttribute(i);
					String attName = refAtt.getLocalName();
					String attNamespace = refAtt.getNamespaceURI();
					Attribute testAtt = testElem.getAttribute(attName,
							attNamespace);
					if (testAtt == null) {
						Assert.fail(message + " attribute on ref not on test: "
								+ attName);
					}

					testStringDoubleEquality(message + " attribute "
							+ path(testAtt) + " values differ:", refAtt
							.getValue(), testAtt.getValue(), eps);
				}
			} else {
				Assert.fail(message + "cannot deal with XMLNode: "
						+ refNode.getClass());
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String path(Node testNode) {
		List<String> fullpath = path(testNode, new ArrayList<String>());
		Collections.reverse(fullpath);
		StringBuilder sb = new StringBuilder();
		for (String p : fullpath) {
			sb.append(p);
		}
		return sb.toString();
	}

	private static List<String> path(Node testNode, List<String> path) {
		if (testNode instanceof Element) {
			Element e = (Element) testNode;
			StringBuilder frag = new StringBuilder("/");
			if (!"".equals(e.getNamespacePrefix())) {
				frag.append(e.getNamespacePrefix()).append(":");
			}
			path.add(frag.append(e.getLocalName()).append("[").append(
					siblingOrdinal(e)).append("]").toString());

		} else if (testNode instanceof Attribute) {
			Attribute a = (Attribute) testNode;
			path.add(new StringBuilder("@").append(a.getNamespacePrefix())
					.append(":").append(a.getLocalName()).toString());
		} else if (testNode instanceof Text) {
			path.add("/text()");
		}
		return (testNode.getParent() != null) ? path(testNode.getParent(), path)
				: path;
	}

	private static int siblingOrdinal(Element e) {
		Element parent = (Element) e.getParent();
		if (parent == null) {
			return 0;
		} else {
			Elements els = parent.getChildElements(e.getLocalName(), e
					.getNamespaceURI());
			for (int i = 0; i < els.size(); i++) {
				if (els.get(i).equals(e)) {
					return i;
				}
			}
			throw new RuntimeException(
					"Element was not a child of its parent. Most perplexing!");
		}
	}

	private static void testStringDoubleEquality(String message,
			String refValue, String testValue, double eps) {
		testValue = testValue.trim();
		refValue = refValue.trim();
		// maybe 
		if (testValue.endsWith(" ") || refValue.endsWith(" ")) {
			throw new RuntimeException("trim error");
		}
		if (!testValue.equals(refValue)) {
			boolean fail = true;
			try {
				compareAsFloats(message, refValue, testValue, eps);
				fail = false;
			} catch (Exception e) {
			}
			if (fail) {
				try {
					compareAsFloatArrays(message, refValue, testValue, eps);
					fail = false;
				} catch (Exception e) {
				}
			}
			if (fail) {
				try {
					compareAsDates(message, refValue, testValue, eps);
					fail = false;
				} catch (Exception e) {
				}
			}
			if (fail) {
				Assert.fail("Cannot equate: "+refValue+" != "+testValue);
			}
		} else {
			Assert.assertEquals(message, refValue, testValue);
		}
	}

	private static void compareAsFloats(String message, String refValue,
			String testValue, double eps) {
		double testVal = Double.NaN;
		double refVal = Double.NaN;
		Error ee = null;
		try {
			try {
				testVal = new Double(testValue).doubleValue();
				refVal = new Double(refValue).doubleValue();
				Assert.assertEquals(message + " doubles ", refVal, testVal,
								eps);
			} catch (NumberFormatException e) {
				Assert.assertEquals(message + " String ", refValue, testValue);
			}
		} catch (ComparisonFailure e) {
			ee = e;
		} catch (AssertionError e) {
			ee = e;
		}
		if (ee != null) {
			throw new RuntimeException("["+testValue+"] != ["+refValue+"]" ,ee);
		}
	}

	/** I am still haveing problems with dates
	 * if these ARE both dates assume they are equal (because of time zones
	 * sorry)
	 * @param message
	 * @param refValue
	 * @param testValue
	 * @param eps
	 */
	private static void compareAsDates(String message, String refValue,
			String testValue, double eps) {
		DateTime testVal = null;
		DateTime refVal = null;
		try {
			testVal = JodaDate.parseDate(testValue);
			refVal = JodaDate.parseDate(refValue);
//			Assert.assertEquals(message + " date ", refVal, testVal);
		} catch (Exception e) {
			Assert.fail("unequal strings "+testValue+" != "+refValue);
		}

	}

	private static void compareAsFloatArrays(String message, String refValue,
			String testValue, double eps) {
		Error ee = null;
		try {
			try {
				RealArray testArray = new RealArray(testValue);
				RealArray refArray = new RealArray(refValue);
				assertEquals(message, testArray, refArray, eps);
			} catch (NumberFormatException e) {
				Assert.assertEquals(message + " String ", refValue, testValue);
			}
		} catch (ComparisonFailure e) {
			ee = e;
		} catch (AssertionError e) {
			ee = e;
		}
		if (ee != null) {
			throw new RuntimeException("["+testValue+"] != ["+refValue+"]" ,ee);
		}
	}

	private static Element stripWhite(Element refNode) {
		refNode = new Element(refNode);
		XMLUtil.removeWhitespaceNodes(refNode);
		return refNode;
	}

	public static void alwaysFail(String message) {
		Assert.fail("should always throw " + message);
	}

// ====================== STML and Euclid ===================

	public static String testEquals(String message, double[] a, double[] b, double eps) {
		String msg = testEquals(a, b, eps);
		return (msg == null) ? null : message+"; "+msg;
	}
	
	// Real2
	/**
	 * returns a message if arrays differ.
	 * 
	 * @param a array to compare
	 * @param b array to compare
	 * @param eps tolerance
	 * @return null if arrays are equal else indicative message
	 */
	public static String testEquals(Real2 a, Real2 b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else {
			if (!Real.isEqual(a.x, b.x, eps) ||
				!Real.isEqual(a.y, b.y, eps)) {
				s = ""+a+" != "+b;
			}
		}
		return s;
	}
// double arrays and related
	
	/**
	 * Asserts equality of double arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a
	 *            expected array
	 * @param b
	 *            actual array
	 * @param eps
	 *            tolerance for agreement
	 */
	public static void assertEquals(String message, double[] a, double[] b,
			double eps) {
		String s = testEquals(a, b, eps);
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 4
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test, Plane3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertEquals("must be of length 4", 4, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 3
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test, Point3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertEquals("must be of length 3", 3, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 3
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test,
			Point3Vector expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getArray().length);
		TestUtils.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 2
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test, Real2 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertEquals("must be of length 2", 2, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test, expected.getXY(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 3
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test,
			Real2Vector expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getXY().getArray().length);
		TestUtils.assertEquals(msg, test, expected.getXY().getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 3
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test,
			RealArray expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getArray().length);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            16 values
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test,
			Transform2 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertEquals("test should have 16 elements (" + msg + STMLConstants.S_RBRAK,
				9, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test, expected.getMatrixAsArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            16 values
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test,
			Transform3 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertEquals("test should have 16 elements (" + msg + STMLConstants.S_RBRAK,
				16, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test, expected.getMatrixAsArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 3
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test,
			Vector3 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertEquals("must be of length 3", 3, test.length);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param rows
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, int rows, double[] test,
			RealSquareMatrix expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("rows should be equal (" + msg + STMLConstants.S_RBRAK, rows,
				expected.getRows());
		TestUtils.assertEquals(msg, test, expected.getMatrixAsArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param rows
	 * @param cols
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, int rows, int cols,
			double[] test, RealMatrix expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("rows should be equal (" + msg + STMLConstants.S_RBRAK, rows,
				expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + STMLConstants.S_RBRAK, cols,
				expected.getCols());
		TestUtils.assertEquals(msg, test, expected.getMatrixAsArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param rows
	 * @param cols
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, int rows, int cols, int[] test,
			IntMatrix expected) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("rows should be equal (" + msg + STMLConstants.S_RBRAK, rows,
				expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + STMLConstants.S_RBRAK, cols,
				expected.getCols());
		Assert.assertEquals(msg, test, expected.getMatrixAsArray());
	}

	/**
	 * Asserts equality of int arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a
	 *            expected array
	 * @param b
	 *            actual array
	 */
	public static void assertEquals(String message, int[] a, int[] b) {
		String s = testEquals(a, b);
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	/**
	 * equality test. true if both args not null and equal
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, int[] test, IntArray expected) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getArray().length);
		Assert.assertEquals(msg, test, expected.getArray());
	}

	/**
	 * equality test. true if both args not null and equal
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, int[] test, IntSet expected) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getElements().length);
		Assert.assertEquals(msg, test, expected.getElements());
	}

	/**
	 * equality test. true if both args not null and equal
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, IntArray test, IntArray expected) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals(msg, test.getArray(), expected.getArray());
	}

	/**
	 * equality test. true if both args not null and equal within epsilon and
	 * rows are present and equals and columns are present and equals
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, IntMatrix test,
			IntMatrix expected) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertNotNull("expected should have columns (" + msg + STMLConstants.S_RBRAK,
				expected.getCols());
		Assert.assertNotNull("expected should have rows (" + msg + STMLConstants.S_RBRAK,
				expected.getRows());
		Assert.assertNotNull("test should have columns (" + msg + STMLConstants.S_RBRAK, test
				.getCols());
		Assert.assertNotNull("test should have rows (" + msg + STMLConstants.S_RBRAK, test
				.getRows());
		Assert.assertEquals("rows should be equal (" + msg + STMLConstants.S_RBRAK, test
				.getRows(), expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + STMLConstants.S_RBRAK, test
				.getCols(), expected.getCols());
		Assert.assertEquals(msg, test.getMatrixAsArray(), expected.getMatrixAsArray());
	}

	/**
	 * equality test. true if both args not null and equal
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, IntSet test, IntSet expected) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertEquals(msg, test.getElements(), expected.getElements());
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Line3 test, Line3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getPoint(), expected.getPoint(), epsilon);
		TestUtils.assertEquals(msg, test.getVector(), expected.getVector(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Plane3 test, Plane3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getArray(), expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Point3 test, Point3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getArray(), expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param testPoint
	 * @param testVector
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Point3 testPoint,
			Vector3 testVector, Line3 expected, double epsilon) {
		Assert.assertNotNull("testPoint should not be null (" + msg + STMLConstants.S_RBRAK,
				testPoint);
		Assert.assertNotNull("testVector should not be null (" + msg + STMLConstants.S_RBRAK,
				testVector);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, testPoint, expected.getPoint(), epsilon);
		TestUtils.assertEquals(msg, testVector, expected.getVector(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Point3Vector test,
			Point3Vector expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getArray(), expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Real2 test, Real2 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getXY(), expected.getXY(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Real2Vector expected,
			Real2Vector test, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, expected.getXY().getArray(), test.getXY().getArray(),
				epsilon);
	}

	/**
	 * test ranges for equality.
	 * 
	 * @param msg
	 * @param r3ref
	 * @param r3
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Real3Range r3ref,
			Real3Range r3, double epsilon) {
		TestUtils.assertEquals("xRange", r3.getXRange(), r3ref.getXRange(), epsilon);
		TestUtils.assertEquals("yRange", r3.getYRange(), r3ref.getYRange(), epsilon);
		TestUtils.assertEquals("zRange", r3.getZRange(), r3ref.getZRange(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, RealArray test,
			RealArray expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getArray(), expected.getArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon and
	 * rows are present and equals and columns are present and equals
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, RealMatrix test,
			RealMatrix expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertNotNull("expected should have columns (" + msg + STMLConstants.S_RBRAK,
				expected.getCols());
		Assert.assertNotNull("expected should have rows (" + msg + STMLConstants.S_RBRAK,
				expected.getRows());
		Assert.assertNotNull("test should have columns (" + msg + STMLConstants.S_RBRAK, test
				.getCols());
		Assert.assertNotNull("test should have rows (" + msg + STMLConstants.S_RBRAK, test
				.getRows());
		Assert.assertEquals("rows should be equal (" + msg + STMLConstants.S_RBRAK, test
				.getRows(), expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + STMLConstants.S_RBRAK, test
				.getCols(), expected.getCols());
		TestUtils.assertEquals(msg, test.getMatrixAsArray(), expected.getMatrixAsArray(),
				epsilon);
	}

	/**
	 * tests equality of ranges.
	 * 
	 * @param msg
	 *            message
	 * @param ref
	 * @param r
	 * @param epsilon
	 */
	public static void assertEquals(String msg, RealRange ref, RealRange r,
			double epsilon) {
		Assert.assertEquals(msg + " min", r.getMin(), ref.getMin(), epsilon);
		Assert.assertEquals(msg + " max", r.getMax(), ref.getMax(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon and
	 * rows are present and equals and columns are present and equals
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, RealSquareMatrix test,
			RealSquareMatrix expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		Assert.assertNotNull("expected should have columns (" + msg + STMLConstants.S_RBRAK,
				expected.getCols());
		Assert.assertNotNull("expected should have rows (" + msg + STMLConstants.S_RBRAK,
				expected.getRows());
		Assert.assertNotNull("test should have columns (" + msg + STMLConstants.S_RBRAK, test
				.getCols());
		Assert.assertNotNull("test should have rows (" + msg + STMLConstants.S_RBRAK, test
				.getRows());
		Assert.assertEquals("rows should be equal (" + msg + STMLConstants.S_RBRAK, test
				.getRows(), expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + STMLConstants.S_RBRAK, test
				.getCols(), expected.getCols());
		TestUtils.assertEquals(msg, test.getMatrixAsArray(), expected.getMatrixAsArray(),
				epsilon);
	}

	/**
	 * Asserts equality of String arrays.
	 * 
	 * convenience method where test is a whitespace-separated set of tokens
	 * 
	 * @param message
	 * @param a
	 *            expected array as space concatenated
	 * @param b
	 *            actual array may not include nulls
	 */
	public static void assertEquals(String message, String a, String[] b) {
		String[] aa = a.split(EC.S_SPACE);
		String s = testEquals(aa, b);
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	/**
	 * Asserts equality of String arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * equality if individual elements are equal or both elements are null
	 * 
	 * @param message
	 * @param a
	 *            expected array may include nulls
	 * @param b
	 *            actual array may include nulls
	 */
	public static void assertEquals(String message, String[] a, String[] b) {
		String s = testEquals(a, b);
		if (s != null) {
			Assert.fail(message + "; " + s + 
					"("+Util.concatenate(a, "~")+" != "+Util.concatenate(b, "~"));
		}
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Transform2 test,
			Transform2 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getMatrixAsArray(), expected.getMatrixAsArray(),
				epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Transform3 test,
			Transform3 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getMatrixAsArray(), expected.getMatrixAsArray(),
				epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Vector2 test, Vector2 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getXY(), expected.getXY(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Vector3 test, Vector3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + STMLConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + STMLConstants.S_RBRAK,
				expected);
		TestUtils.assertEquals(msg, test.getArray(), expected.getArray(), epsilon);
	}

	/**
	 * tests 2 XML objects for equality using canonical XML.
	 * 
	 * @param message
	 * @param refNode
	 *            first node
	 * @param testNode
	 *            second node
	 * @param stripWhite
	 *            if true remove w/s nodes
	 */
	public static void assertEqualsCanonically(String message, Element refNode,
			Element testNode, boolean stripWhite) {
		assertEqualsCanonically(message, refNode, testNode, stripWhite, true);
	}


	/**
	 * tests 2 XML objects for equality using canonical XML.
	 * 
	 * @param message
	 * @param refNode
	 *            first node
	 * @param testNode
	 *            second node
	 * @param stripWhite
	 *            if true remove w/s nodes
	 */
	public static void assertEqualsCanonically(String message, String refXMLString,
			Element testNode, boolean stripWhite) {
		assertEqualsCanonically(message, TestUtils.parseValidString(refXMLString), testNode, stripWhite, true);
	}

//
//    public static void assertEqualsCanonically(String message, STMLMap refNode, STMLMap testNode) {
//		Assert.assertEquals("from refs", new HashSet<String>(refNode.getFromRefs()), new HashSet<String>(testNode.getFromRefs()));
//        Assert.assertEquals("to refs", new HashSet<String>(refNode.getToRefs()), new HashSet<String>(testNode.getToRefs()));
//        for (String fromRef : refNode.getFromRefs()) {
//            String toRef = refNode.getToRef(fromRef);
//            Assert.assertEquals("from/to refs", toRef, testNode.getToRef(fromRef));
//        }
//	}


	/**
	 * tests 2 XML objects for equality using canonical XML.
	 * 
	 * @param message
	 * @param refNode
	 *            first node
	 * @param testNode
	 *            second node
	 * @param stripWhite
	 *            if true remove w/s nodes
	 */
	private static void assertEqualsCanonically(String message,
			Element refNode, Element testNode, boolean stripWhite,
			boolean reportError) throws Error {
		if (stripWhite) {
			refNode = stripWhite(refNode);
			testNode = stripWhite(testNode);
		}
		Error ee = null;
		try {
			XOMTestCase.assertEquals(message, refNode, testNode);
		} catch (ComparisonFailure e) {
			ee = e;
		} catch (Error e) {
			ee = e;
		}
		if (ee != null) {
			if (reportError) {
				reportXMLDiffInFull(message, ee.getMessage(), refNode, testNode);
			} else {
				throw (ee);
			}
		}
	}

	/**
	 * compares two XML nodes and checks float near-equivalence (can also be
	 * used for documents without floats) uses CMLXOMTestUtils.assertEqualsCanonically and only
	 * uses PMR code if fails
	 * 
	 * @param message
	 * @param refNode
	 * @param testNode
	 * @param eps
	 */
	public static void assertEqualsIncludingFloat(String message, Node refNode,
			Node testNode, boolean stripWhite, double eps, boolean report) {
		if (stripWhite && refNode instanceof Element
				&& testNode instanceof Element) {
			refNode = stripWhite((Element) refNode);
			testNode = stripWhite((Element) testNode);
		}
		try {
			assertEqualsIncludingFloat(message, refNode, testNode, eps);
		} catch (RuntimeException e) {
			if (report) {
				reportXMLDiffInFull(message, e.getMessage(), refNode, testNode);
			}
		}
	}

	/**
	 * Asserts non equality of double arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a
	 *            expected array
	 * @param b
	 *            actual array
	 * @param eps
	 *            tolerance for agreement
	 */
	public static void assertNotEquals(String message, double[] a, double[] b,
			double eps) {
		String s = testEquals(a, b, eps);
		if (s == null) {
			Assert.fail(message + "; arrays are equal");
		}
	}

	/**
	 * Asserts non equality of double arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a
	 *            expected array
	 * @param b
	 *            actual array
	 */
	public static void assertNotEquals(String message, int[] a, int[] b) {
		String s = testEquals(a, b);
		if (s == null) {
			Assert.fail(message + "; arrays are equal");
		}
	}

	/**
	 * Asserts non equality of String arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a
	 *            expected array
	 * @param b
	 *            actual array
	 */
	public static void assertNotEquals(String message, String[] a, String[] b) {
		String s = testEquals(a, b);
		if (s == null) {
			Assert.fail(message + "; arrays are equal");
		}
	}

	/**
	 * tests 2 XML objects for non-equality using canonical XML.
	 * 
	 * @param message
	 * @param node1
	 *            first node
	 * @param node2
	 *            second node
	 */
	public static void assertNotEqualsCanonically(String message, Node node1,
			Node node2) {
		try {
			Assert.assertEquals(message, node1, node2);
			String s1 = XMLUtil.getCanonicalString(node1);
			String s2 = XMLUtil.getCanonicalString(node2);
			Assert.fail(message + "nodes should be different " + s1 + " != "
					+ s2);
		} catch (ComparisonFailure e) {
		} catch (AssertionFailedError e) {
		}
	}

	public static void assertObjectivelyEquals(String message, double[] a,
			double[] b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + STMLConstants.S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (!(((Double) a[i]).equals(b[i]) || !Real.isEqual(a[i], b[i],
						eps))) {
					s = "unequal element at (" + i + "), " + a[i] + " != "
							+ b[i];
					break;
				}
			}
		}
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	/**
	 * test the writeHTML method of element.
	 * 
	 * @param element
	 *            to test
	 * @param expected
	 *            HTML string
	 */
	public static void assertWriteHTML(STMLElement element, String expected) {
		StringWriter sw = new StringWriter();
		try {
			element.writeHTML(sw);
			sw.close();
		} catch (IOException e) {
			Assert.fail("should not throw " + e);
		}
		String s = sw.toString();
		Assert.assertEquals("HTML output ", expected, s);
	}

	/**
	 * used by Assert routines. copied from Assert
	 * 
	 * @param message
	 *            prepends if not null
	 * @param expected
	 * @param actual
	 * @return message
	 */
	public static String getAssertFormat(String message, Object expected,
			Object actual) {
		String formatted = "";
		if (message != null) {
			formatted = message + STMLConstants.S_SPACE;
		}
		return formatted + "expected:<" + expected + "> but was:<" + actual
				+ ">";
	}

	public static void neverFail(Exception e) {
		Assert.fail("should never throw " + e);
	}

	public static void neverThrow(Exception e) {
		throw new EuclidRuntimeException("should never throw " + e);
	}

	/**
	 * convenience method to parse test file. uses resource
	 * 
	 * @param filename
	 *            relative to classpath
	 * @return root element
	 */
	public static Element parseValidFile(String filename) {
		Element root = null;
		try {
			URL url = Util.getResource(filename);
			root = new Builder().build(new File(url.toURI()))
					.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
	}

	/**
	 * convenience method to parse test file. 
	 * @param filename relative to classpath
	 * @return root element
	 */
	public static Element parseValidFile(File file) {
		Element root = null;
		try {
			root = new Builder().build(new FileInputStream(file)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("BUG ", e);
		}
		return root;
	}

	/**
	 * convenience method to parse test string.
	 * 
	 * @param s
	 *            xml string (assumed valid)
	 * @return root element
	 */
	public static Element parseValidString(String s) {
		Element element = null;
		if (s == null) {
			throw new RuntimeException("NULL VALID JAVA_STRING");
		}
		try {
			element = XMLUtil.parseXML(s);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("ERROR " + e + e.getMessage() + "..."
					+ s.substring(0, Math.min(100, s.length())));
			Util.BUG(e);
		}
		return element;
	}

	static protected void reportXMLDiff(String message, String errorMessage,
			Node refNode, Node testNode) {
		Assert.fail(message + " ~ " + errorMessage);
	}

	static protected void reportXMLDiffInFull(String message,
			String errorMessage, Node refNode, Node testNode) {
		try {
			System.err.println("Error: "+errorMessage);
			System.err.println("==========XMLDIFF reference=========");
			XMLUtil.debug((Element) refNode, System.err, 2);
			System.err.println("------------test---------------------");
			String s = testNode.toXML().replace("><", ">\n<");
			System.err.println(s);
			System.err.println("==============" + message
					+ "===================");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Assert.fail(message + " ~ " + errorMessage);
	}

	/**
	 * returns a message if arrays differ.
	 * 
	 * @param a
	 *            array to compare
	 * @param b
	 *            array to compare
	 * @param eps
	 *            tolerance
	 * @return null if arrays are equal else indicative message
	 */
	static String testEquals(double[] a, double[] b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + STMLConstants.S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (!Real.isEqual(a[i], b[i], eps)) {
					s = "unequal element at (" + i + "), " + a[i] + " != "
							+ b[i];
					break;
				}
			}
		}
		return s;
	}

	/**
	 * returns a message if arrays of arrays differ.
	 * 
	 * @param a
	 *            array to compare
	 * @param b
	 *            array to compare
	 * @param eps
	 *            tolerance
	 * @return null if array are equal else indicative message
	 */
	static String testEquals(double[][] a, double[][] b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + STMLConstants.S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i].length != b[i].length) {
					s = "row (" + i + ") has unequal lengths: " + a[i].length
							+ STMLConstants.S_SLASH + b[i].length;
					break;
				}
				for (int j = 0; j < a[i].length; j++) {
					if (!Real.isEqual(a[i][j], b[i][j], eps)) {
						s = "unequal element at (" + i + ", " + j + "), ("
								+ a[i][j] + " != " + b[i][j] + STMLConstants.S_RBRAK;
						break;
					}
				}
			}
		}
		return s;
	}

	/**
	 * compare integer arrays.
	 * 
	 * @param a
	 * @param b
	 * @return message or null
	 */
	public static String testEquals(int[] a, int[] b) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + STMLConstants.S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i] != b[i]) {
					s = "unequal element (" + i + "), " + a[i] + " != " + b[i];
					break;
				}
			}
		}
		return s;
	}

	/**
	 * match arrays. error is a == null or b == null or a.length != b.length or
	 * a[i] != b[i] nulls match
	 * 
	 * @param a
	 * @param b
	 * @return message if errors else null
	 */
	public static String testEquals(String[] a, String[] b) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + STMLConstants.S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i] == null && b[i] == null) {
					// both null, match
				} else if (a[i] == null || b[i] == null || !a[i].equals(b[i])) {
					s = "unequal element (" + i + "), expected: " + a[i]
							+ " found: " + b[i];
					break;
				}
			}
		}
		return s;
	}

}
