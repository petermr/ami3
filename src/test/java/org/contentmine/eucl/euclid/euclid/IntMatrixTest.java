/**
 *    Copyright 2011 Peter Murray-Rust
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

package org.contentmine.eucl.euclid.euclid;

import static org.contentmine.eucl.euclid.EuclidConstants.S_RBRAK;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.Int;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test IntMatrix
 * 
 * @author pmr
 * 
 */
public class IntMatrixTest {

	final static Logger LOG = Logger.getLogger(IntMatrixTest.class);

	IntMatrix m0;

	IntMatrix m1;

	IntMatrix m2;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		LOG.setLevel(Level.WARN);
		m0 = new IntMatrix();
		m1 = new IntMatrix(3, 4);
		m2 = new IntMatrix(3, 4, new int[] { 11, 12, 13, 14, 21, 22, 23, 24,
				31, 32, 33, 34, });
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
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
				expected);
		Assert.assertNotNull("expected should have columns (" + msg + S_RBRAK,
				expected.getCols());
		Assert.assertNotNull("expected should have rows (" + msg + S_RBRAK,
				expected.getRows());
		Assert.assertNotNull("test should have columns (" + msg + S_RBRAK, test
				.getCols());
		Assert.assertNotNull("test should have rows (" + msg + S_RBRAK, test
				.getRows());
		Assert.assertEquals("rows should be equal (" + msg + S_RBRAK, test
				.getRows(), expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + S_RBRAK, test
				.getCols(), expected.getCols());
		String s = Int.testEquals(test.getMatrixAsArray(), expected
						.getMatrixAsArray());
		if (s != null) {
			Assert.fail(msg + "; " + s);
		}
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
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + S_RBRAK,
				expected);
		Assert.assertEquals("rows should be equal (" + msg + S_RBRAK, rows,
				expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + S_RBRAK, cols,
				expected.getCols());
		String s = Int.testEquals(test, expected.getMatrixAsArray());
		if (s != null) {
			Assert.fail(msg + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.IntMatrix()'
	 */
	@Test
	public void testIntMatrix() {
		Assert.assertEquals("empty", "()", m0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.IntMatrix(int, int)'
	 */
	@Test
	public void testIntMatrixIntInt() {
		Assert.assertEquals("int int", "{3,4}" + "\n(0,0,0,0)" + "\n(0,0,0,0)"
				+ "\n(0,0,0,0)", m1.toString());
		Assert.assertEquals("int int rows", 3, m1.getRows());
		Assert.assertEquals("int int cols", 4, m1.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.IntMatrix(int, int, int[])'
	 */
	@Test
	public void testIntMatrixIntIntIntegerArray() {
		Assert.assertEquals("int int int[]", "{3,4}" + "\n(11,12,13,14)"
				+ "\n(21,22,23,24)" + "\n(31,32,33,34)", m2.toString());
		Assert.assertEquals("int int int[] rows", 3, m2.getRows());
		Assert.assertEquals("int int int[] cols", 4, m2.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.IntMatrix(int, int, int)'
	 */
	@Test
	public void testIntMatrixIntIntInteger() {
		IntMatrix m = new IntMatrix(3, 4, 10);
		Assert.assertEquals("int int int[]", "{3,4}" + "\n(10,10,10,10)"
				+ "\n(10,10,10,10)" + "\n(10,10,10,10)", m.toString());
		Assert.assertEquals("int int int[] rows", 3, m.getRows());
		Assert.assertEquals("int int int[] cols", 4, m.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.IntMatrix(IntMatrix, int,
	 * int, int, int)'
	 */
	@Test
	public void testIntMatrixIntMatrixIntIntIntInt() {
		IntMatrix m = new IntMatrix(m2, 1, 2, 1, 3);
		Assert.assertEquals("int int int[]", "{2,3}" + "\n(22,23,24)"
				+ "\n(32,33,34)", m.toString());
		Assert.assertEquals("int int int[] rows", 2, m.getRows());
		Assert.assertEquals("int int int[] cols", 3, m.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.IntMatrix(IntMatrix)'
	 */
	@Test
	public void testIntMatrixIntMatrix() {
		IntMatrix m = new IntMatrix(m2);
		Assert.assertEquals("int int int[]", "{3,4}" + "\n(11,12,13,14)"
				+ "\n(21,22,23,24)" + "\n(31,32,33,34)", m.toString());
		Assert.assertEquals("int int int[] rows", 3, m.getRows());
		Assert.assertEquals("int int int[] cols", 4, m.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.getIntMatrix()'
	 */
	@Test
	public void testGetIntMatrix() {
		IntMatrix mm2 = new IntMatrix(3, 4, new int[] { 11, 12, 13, 14, 21, 22,
				23, 24, 31, 32, 33, 34, });
		IntMatrix m = mm2.getIntMatrix();
		Assert.assertEquals("int int int[]", "{3,4}" + "\n(11,12,13,14)"
				+ "\n(21,22,23,24)" + "\n(31,32,33,34)", m.toString());
		Assert.assertEquals("int int int[] rows", 3, m.getRows());
		Assert.assertEquals("int int int[] cols", 4, m.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.IntMatrix(int[][])'
	 */
	@Test
	public void testIntMatrixIntegerArrayArray() {
		IntMatrix mm2 = new IntMatrix(new int[][] {
				new int[] { 11, 12, 13, 14 }, new int[] { 21, 22, 23, 24 },
				new int[] { 31, 32, 33, 34 } });
		IntMatrix m = mm2.getIntMatrix();
		Assert.assertEquals("int int int[]", "{3,4}" + "\n(11,12,13,14)"
				+ "\n(21,22,23,24)" + "\n(31,32,33,34)", m.toString());
		Assert.assertEquals("int int int[] rows", 3, m.getRows());
		Assert.assertEquals("int int int[] cols", 4, m.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.setFormat(DecimalFormat)'
	 */
	@Test
	public void testSetFormat() {
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.getFormat()'
	 */
	@Test
	public void testGetFormat() {
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.getRows()'
	 */
	@Test
	public void testGetRowsCols() {
		IntMatrix m = new IntMatrix(new int[][] { new int[] { 11, 12, 13, 14 },
				new int[] { 21, 22, 23, 24 }, new int[] { 31, 32, 33, 34 } });
		Assert.assertEquals("int int int[] rows", 3, m.getRows());
		Assert.assertEquals("int int int[] cols", 4, m.getCols());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.getMatrix()'
	 */
	@Test
	public void testGetMatrix() {
		int[][] matrix = m1.getMatrix();
		Assert.assertEquals("getMatrix", 3, matrix.length);
		Assert.assertEquals("getMatrix", 4, matrix[0].length);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.getMatrixAsArray()'
	 */
	@Test
	public void testGetMatrixAsArray() {
		int[] array = m2.getMatrixAsArray();
		String s = Int.testEquals((new int[] { 11, 12, 13, 14, 21,
						22, 23, 24, 31, 32, 33, 34 }), array);
		if (s != null) {
			Assert.fail("matrix as array" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.isEqualTo(IntMatrix)'
	 */
	@Test
	public void testIsEqualTo() {
		Assert.assertTrue("isEqualTo", m2.isEqualTo(m2));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.plus(IntMatrix)'
	 */
	@Test
	public void testPlus() {
		IntMatrix m = m2.plus(m2);
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 22, 24,
				26, 28, 42, 44, 46, 48, 62, 64, 66, 68 }, m);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.subtract(IntMatrix)'
	 */
	@Test
	public void testSubtract() {
		IntMatrix m = new IntMatrix(new int[][] { new int[] { 11, 12, 13, 14 },
				new int[] { 21, 22, 23, 24 }, new int[] { 31, 32, 33, 34 } });
		IntMatrix mm = m2.subtract(m);
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { -0, -0,
				-0, -0, -0, -0, -0, -0, -0, -0, -0, -0, }, mm);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.negative()'
	 */
	@Test
	public void testNegative() {
		m2.negative();
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { -11,
				-12, -13, -14, -21, -22, -23, -24, -31, -32, -33, -34 }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.multiply(IntMatrix)'
	 */
	@Test
	public void testMultiplyIntMatrix() {
		IntMatrix m = new IntMatrix(new int[][] { new int[] { 10, 20, 30 },
				new int[] { 40, 50, 60 }, });
		IntMatrix mm = m.multiply(m2);
		IntMatrixTest.assertEquals("matrix as array", 2, 4, new int[] { 1460,
				1520, 1580, 1640, 3350, 3500, 3650, 3800, }, mm);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.multiplyBy(int)'
	 */
	@Test
	public void testMultiplyBy() {
		m2.multiplyBy(10);
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 110,
				120, 130, 140, 210, 220, 230, 240, 310, 320, 330, 340, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.multiplyEquals(IntMatrix)'
	 */
	@Test
	public void testMultiplyEquals() {
		IntMatrix m = new IntMatrix(new int[][] { new int[] { 10, 20, 30 },
				new int[] { 40, 50, 60 }, });
		try {
			m2.multiplyEquals(m);
			Assert.fail("should always throw " + "non-conformable matrices");
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("multiplyEquals", "unequal matrices (4, 2)", e
					.getMessage());
		}
		m.multiplyEquals(m2);
		IntMatrixTest.assertEquals("matrix as array", 2, 4, new int[] { 1460,
				1520, 1580, 1640, 3350, 3500, 3650, 3800, }, m);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.multiply(IntArray)'
	 */
	@Test
	public void testMultiplyIntArray() {
		IntArray ra = new IntArray(new int[] { 1, 2, 3, 4 });
		IntArray raa = m2.multiply(ra);
		IntArrayTest.assertEquals("array", new int[] { 130, 230, 330 }, raa);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.columnwiseDivide(IntArray)'
	 */
	@Test
	public void testColumnwiseDivide() {
		IntArray ra = new IntArray(new int[] { 1, 2, 3, 4 });
		m2.columnwiseDivide(ra);
		IntMatrixTest.assertEquals("array", 3, 4, new int[] { 11, 6, 4, 3, 21,
				11, 7, 6, 31, 16, 11, 8, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.elementAt(int, int)'
	 */
	@Test
	public void testElementAtIntInt() {
		Assert.assertEquals("elementAt ", 32, m2.elementAt(2, 1));
		try {
			m2.elementAt(5, 5);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("elementAt", "Bad value of row: 5/3", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.elementAt(Int2)'
	 */
	@Test
	public void testElementAtInt2() {
		Assert.assertEquals("elementAt ", 32, m2.elementAt(new Int2(2, 1)));
		try {
			m2.elementAt(new Int2(5, 5));
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("elementAt", "Bad value of row: 5/3", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.setElementAt(int, int, int)'
	 */
	@Test
	public void testSetElementAt() {
		m2.setElementAt(1, 2, 15);
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 11, 12,
				13, 14, 21, 22, 15, 24, 31, 32, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.largestElement()'
	 */
	@Test
	public void testLargestElement() {
		int d = m2.largestElement();
		Assert.assertEquals("largestElement", 34, d);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.indexOfLargestElement()'
	 */
	@Test
	public void testIndexOfLargestElement() {
		Int2 ii = m2.indexOfLargestElement();
		Assert.assertEquals("indexOfLargestElement", 2, ii.getX());
		Assert.assertEquals("indexOfLargestElement", 3, ii.getY());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.largestElementInColumn(int)'
	 */
	@Test
	public void testLargestElementInColumn() {
		int d = m2.largestElementInColumn(1);
		Assert.assertEquals("largestElement", 32, d);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.IntMatrix.indexOfLargestElementInColumn(int)'
	 */
	@Test
	public void testIndexOfLargestElementInColumn() {
		int i = m2.indexOfLargestElementInColumn(1);
		Assert.assertEquals("largestElement", 2, i);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.largestElementInRow(int)'
	 */
	@Test
	public void testLargestElementInRow() {
		int d = m2.largestElementInRow(1);
		Assert.assertEquals("largestElement", 24, d);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.IntMatrix.indexOfLargestElementInRow(int)'
	 */
	@Test
	public void testIndexOfLargestElementInRow() {
		int i = m2.indexOfLargestElementInRow(1);
		Assert.assertEquals("largestElement", 3, i);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.smallestElement()'
	 */
	@Test
	public void testSmallestElement() {
		int d = m2.smallestElement();
		Assert.assertEquals("smallestElement", 11, d);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.indexOfSmallestElement()'
	 */
	@Test
	public void testIndexOfSmallestElement() {
		Int2 ii = m2.indexOfSmallestElement();
		Assert.assertEquals("indexOfSmallestElement", 0, ii.getX());
		Assert.assertEquals("indexOfSmallestElement", 0, ii.getY());
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.IntMatrix.smallestElementInColumn(int)'
	 */
	@Test
	public void testSmallestElementInColumn() {
		int d = m2.smallestElementInColumn(1);
		Assert.assertEquals("smallestElement", 12, d);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.IntMatrix.indexOfSmallestElementInColumn(int)'
	 */
	@Test
	public void testIndexOfSmallestElementInColumn() {
		int i = m2.indexOfSmallestElementInColumn(1);
		Assert.assertEquals("largestElement", 0, i);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.smallestElementInRow(int)'
	 */
	@Test
	public void testSmallestElementInRow() {
		int d = m2.smallestElementInRow(1);
		Assert.assertEquals("smallestElement", 21, d);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.IntMatrix.indexOfSmallestElementInRow(int)'
	 */
	@Test
	public void testIndexOfSmallestElementInRow() {
		int i = m2.indexOfSmallestElementInRow(1);
		Assert.assertEquals("largestElement", 0, i);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.extractColumnData(int)'
	 */
	@Test
	public void testExtractColumnData() {
		IntArray ra = m2.extractColumnData(1);
		IntArrayTest.assertEquals("euclidean column lengths", new int[] { 12,
				22, 32 }, ra);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.extractRowData(int)'
	 */
	@Test
	public void testExtractRowData() {
		IntArray ra = m2.extractRowData(1);
		IntArrayTest.assertEquals("euclidean column lengths", new int[] { 21,
				22, 23, 24 }, ra);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.clearMatrix()'
	 */
	@Test
	public void testClearMatrix() {
		m2.clearMatrix();
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.setAllElements(int)'
	 */
	@Test
	public void testSetAllElements() {
		m2.setAllElements(23);
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 23, 23,
				23, 23, 23, 23, 23, 23, 23, 23, 23, 23, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.getTranspose()'
	 */
	@Test
	public void testGetTranspose() {
		IntMatrix m = m2.getTranspose();
		IntMatrixTest.assertEquals("transpose", 4, 3, new int[] { 11, 21, 31,
				12, 22, 32, 13, 23, 33, 14, 24, 34, }, m);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.isSquare()'
	 */
	@Test
	public void testIsSquare() {
		Assert.assertFalse("isSquare", m2.isSquare());
		Assert.assertTrue("isSquare", new IntMatrix(2, 2, new int[] { 11, 12,
				21, 22 }).isSquare());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.deleteColumn(int)'
	 */
	@Test
	public void testDeleteColumn() {
		m2.deleteColumn(1);
		IntMatrixTest.assertEquals("matrix as array", 3, 3, new int[] { 11, 13,
				14, 21, 23, 24, 31, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.deleteColumns(int, int)'
	 */
	@Test
	public void testDeleteColumns() {
		m2.deleteColumns(1, 2);
		IntMatrixTest.assertEquals("matrix as array", 3, 2, new int[] { 11, 14,
				21, 24, 31, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.deleteRow(int)'
	 */
	@Test
	public void testDeleteRow() {
		m2.deleteRow(1);
		IntMatrixTest.assertEquals("matrix as array", 2, 4, new int[] { 11, 12,
				13, 14, 31, 32, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.deleteRows(int, int)'
	 */
	@Test
	public void testDeleteRows() {
		// FIXME does not work for high = nrows
		m2.deleteRows(1, 1);
		IntMatrixTest.assertEquals("matrix as array", 2, 4, new int[] { 11, 12,
				13, 14, 31, 32, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.replaceColumnData(int,
	 * IntArray)'
	 */
	@Test
	public void testReplaceColumnDataIntIntArray() {
		m2.replaceColumnData(1, new IntArray(new int[] { 19, 29, 39 }));
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 11, 19,
				13, 14, 21, 29, 23, 24, 31, 39, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.replaceColumnData(int,
	 * int[])'
	 */
	@Test
	public void testReplaceColumnDataIntIntegerArray() {
		m2.replaceColumnData(1, new int[] { 19, 29, 39 });
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 11, 19,
				13, 14, 21, 29, 23, 24, 31, 39, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.replaceColumnData(int,
	 * IntMatrix)'
	 */
	@Test
	public void testReplaceColumnDataIntIntMatrix() {
		IntMatrix expect = null;
		IntMatrix m = new IntMatrix(3, 2, new int[] { 72, 73, 82, 83, 92, 93 });
		m2.replaceColumnData(1, m);
		expect = new IntMatrix(3, 4, new int[] { 11, 72, 73, 14, 21, 82, 83,
				24, 31, 92, 93, 34, });
		IntMatrixTest.assertEquals("matrix as array", m2, expect);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.insertColumns(int, int)'
	 */
	@Test
	public void testInsertColumns() {
		// inserts 3 empty columns
		m2.makeSpaceForNewColumns(1, 3);
		IntMatrix expect = new IntMatrix(3, 7, new int[] { 11, 0, 0, 0, 12, 13,
				14, 21, 0, 0, 0, 22, 23, 24, 31, 0, 0, 0, 32, 33, 34, });
		IntMatrixTest.assertEquals("matrix as array", m2, expect);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.insertColumnData(int,
	 * IntArray)'
	 */
	@Test
	public void testInsertColumnDataIntIntArray() {
		// inserts a column
		m2.insertColumnData(1, new IntArray(new int[] { 91, 92, 93 }));
		IntMatrix expect = new IntMatrix(3, 5, new int[] { 11, 12, 91, 13, 14,
				21, 22, 92, 23, 24, 31, 32, 93, 33, 34, });
		IntMatrixTest.assertEquals("matrix as array", m2, expect);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.insertColumnData(int,
	 * IntMatrix)'
	 */
	@Test
	public void testInsertColumnDataIntIntMatrix() {
		LOG.info("+++insertColumnData>>>");
		IntMatrix insert = new IntMatrix(3, 2, new int[] { 72, 73, 82, 83, 92,
				93, });
		m2.insertColumnData(1, insert);
		IntMatrix expect = new IntMatrix(3, 6, new int[] { 11, 12, 72, 73, 13,
				14, 21, 22, 82, 83, 23, 24, 31, 32, 92, 93, 33, 34, });
		IntMatrixTest.assertEquals("matrix as array", m2, expect);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.insertRows(int, int)'
	 */
	@Test
	public void testInsertRows() {
		m2.insertRows(1, 2);
		int[] array = m2.getMatrixAsArray();
		String s = Int.testEquals((new int[] { 11, 12, 13, 14, 0,
						0, 0, 0, 0, 0, 0, 0, 21, 22, 23, 24, 31, 32, 33, 34, }), array);
		if (s != null) {
			Assert.fail("matrix as array" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.replaceRowData(int,
	 * IntArray)'
	 */
	@Test
	public void testReplaceRowDataIntIntArray() {
		m2.replaceRowData(1, new IntArray(new int[] { 71, 72, 73, 74 }));
		int[] array = m2.getMatrixAsArray();
		String s = Int.testEquals((new int[] { 11, 12, 13, 14, 71,
						72, 73, 74, 31, 32, 33, 34, }), array);
		if (s != null) {
			Assert.fail("matrix as array" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.replaceRowData(int, int[])'
	 */
	@Test
	public void testReplaceRowDataIntIntegerArray() {
		m2.replaceRowData(1, new int[] { 71, 72, 73, 74 });
		int[] array = m2.getMatrixAsArray();
		String s = Int.testEquals((new int[] { 11, 12, 13, 14, 71,
						72, 73, 74, 31, 32, 33, 34, }), array);
		if (s != null) {
			Assert.fail("matrix as array" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.replaceRowData(int,
	 * IntMatrix)'
	 */
	@Test
	public void testReplaceRowDataIntIntMatrix() {
		LOG.info("+++replaceRowData>>>");
		// FIXME
		IntMatrix insert = new IntMatrix(new IntMatrix(2, 4, new int[] { 71,
				72, 73, 74, 81, 82, 83, 84, }));
		m2.replaceRowData(0, insert);
		IntMatrix expect = new IntMatrix(3, 4, new int[] { 11, 12, 13, 14, 71,
				72, 73, 74, 81, 82, 83, 84, });
		// rows 2 and 3 are not filled
		IntMatrixTest.assertEquals("matrix as array", m2, expect);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.insertRowData(int,
	 * IntMatrix)'
	 */
	@Test
	public void testInsertRowDataIntIntMatrix() {
		// FIXME
		m2.insertRowData(1, new IntMatrix(2, 4, new int[] { 71, 72, 73, 74, 81,
				82, 83, 84, }));
		IntMatrix expect = new IntMatrix(5, 4, new int[] { 11, 12, 13, 14, 21,
				22, 23, 24, 71, 72, 73, 74, 81, 82, 83, 84, 31, 32, 33, 34, });
		String s = Int.testEquals(expect.getMatrixAsArray(), m2
						.getMatrixAsArray());
		if (s != null) {
			Assert.fail("matrix as array" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.insertRowData(int,
	 * IntArray)'
	 */
	@Test
	public void testInsertRowDataIntIntArray() {
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 11, 12,
				13, 14, 21, 22, 23, 24, 31, 32, 33, 34, }, m2);
		m2.insertRowData(1, new IntArray(new int[] { 71, 72, 73, 74, }));
		IntMatrixTest.assertEquals("matrix as array", 4, 4, new int[] { 11, 12,
				13, 14, 21, 22, 23, 24, 71, 72, 73, 74, 31, 32, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.appendColumnData(IntArray)'
	 */
	@Test
	public void testAppendColumnDataIntArray() {
		m2.appendColumnData(new IntArray(new int[] { 17, 27, 37, }));
		int[] array = m2.getMatrixAsArray();
		String s = Int.testEquals((new int[] { 11, 12, 13, 14, 17,
						21, 22, 23, 24, 27, 31, 32, 33, 34, 37 }), array);
		if (s != null) {
			Assert.fail("matrix as array" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.appendColumnData(IntMatrix)'
	 */
	@Test
	public void testAppendColumnDataIntMatrix() {
		// logger.info("+++appendColumnData>>>");
		IntMatrix rm = new IntMatrix(3, 2, new int[] { 17, 18, 27, 28, 37, 38 });
		m2.appendColumnData(rm);
		IntMatrix expect = new IntMatrix(3, 6, new int[] { 11, 12, 13, 14, 17,
				18, 21, 22, 23, 24, 27, 28, 31, 32, 33, 34, 37, 38 });
		IntMatrixTest.assertEquals("matrix as array", m2, expect);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.appendRowData(IntArray)'
	 */
	@Test
	public void testAppendRowDataIntArray() {
		IntArray ra = new IntArray(new int[] { 41, 42, 43, 44 });
		m2.appendRowData(ra);
		// fails to insert data
		IntMatrixTest.assertEquals("matrix as array", 4, 4, new int[] { 11, 12,
				13, 14, 21, 22, 23, 24, 31, 32, 33, 34, 41, 42, 43, 44 }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.appendRowData(IntMatrix)'
	 */
	@Test
	public void testAppendRowDataIntMatrix() {
		LOG.info("+++appendRowData>>>");
		// FIXME
		IntMatrix rm = new IntMatrix(2, 4, new int[] { 41, 42, 43, 44, 51, 52,
				53, 54 });
		m2.appendRowData(rm);
		IntMatrix expect = new IntMatrix(5, 4, new int[] { 11, 12, 13, 14, 21,
				22, 23, 24, 31, 32, 33, 34, 41, 42, 43, 44, 51, 52, 53, 54 });
		IntMatrixTest.assertEquals("matrix as array", m2, expect);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.replaceSubMatrixData(int,
	 * int, IntMatrix)'
	 */
	@Test
	public void testReplaceSubMatrixData() {
		IntMatrix rm = new IntMatrix(2, 2, new int[] { 71, 72, 81, 82 });
		m2.replaceSubMatrixData(1, 1, rm);
		// fails to insert data
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 71, 72,
				13, 14, 81, 82, 23, 24, 31, 32, 33, 34, }, m2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.reorderColumnsBy(IntSet)'
	 */
	@Test
	public void testReorderColumnsBy() {
		IntMatrix mm = m2
				.reorderColumnsBy(new IntSet(new int[] { 3, 1, 2, 0 }));
		// fails to insert data
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 14, 12,
				13, 11, 24, 22, 23, 21, 34, 32, 33, 31 }, mm);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.reorderRowsBy(IntSet)'
	 */
	@Test
	public void testReorderRowsBy() {
		IntMatrix mm = m2.reorderRowsBy(new IntSet(new int[] { 1, 2, 0 }));
		// fails to insert data
		IntMatrixTest.assertEquals("matrix as array", 3, 4, new int[] { 21, 22,
				23, 24, 31, 32, 33, 34, 11, 12, 13, 14, }, mm);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.extractSubMatrixData(int,
	 * int, int, int)'
	 */
	@Test
	public void testExtractSubMatrixData() {
		IntMatrix mm = m2.extractSubMatrixData(1, 2, 2, 3);
		IntMatrixTest.assertEquals("sub matrix", 2, 2, new int[] { 23, 24, 33,
				34 }, mm);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.elementsInRange(IntRange)'
	 */
	@Test
	public void testElementsInRange() {
		IntMatrix im = m2.elementsInRange(new IntRange(13, 31));
		IntMatrixTest.assertEquals("sub matrix", 3, 4, new int[] { 0, 0, 1, 1,
				1, 1, 1, 1, 1, 0, 0, 0 }, im);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntMatrix.writeXML(Writer)'
	 */
	@Test
	public void testWriteXML() {
		StringWriter w = new StringWriter();
		try {
			m2.writeXML(w);
			w.close();
		} catch (IOException e) {
			throw new EuclidRuntimeException("should never throw " + e);
		}
		Assert
				.assertEquals(
						"writeXML",
						"<matrix rows='3' columns='4'>11 12 13 14 21 22 23 24 31 32 33 34</matrix>",
						w.toString());
	}
	
	@Test
	public void testReadMatrix() {
		String matrixS = ""
		+ "{25,18}\n" 
        + "(23,8,8,11,6,4,2,5,1,1,2,3,3,2,2,0,2,1)\n"
        + "(21,6,6,8,6,4,4,7,3,2,3,3,2,3,0,0,2,1)\n"
        + "(6,8,2,3,0,5,0,1,0,2,0,0,1,1,1,0,1,0)\n"
        + "(15,5,6,4,5,3,2,3,0,2,2,0,1,1,1,0,1,1)\n"
        + "(13,7,5,7,1,4,0,4,1,2,1,1,2,2,1,0,2,1)\n"
        + "(8,8,5,5,0,3,0,2,0,2,0,0,1,1,2,1,1,0)\n"
        + "(35,5,6,9,10,4,3,7,2,2,1,3,2,2,0,0,2,1)\n"
        + "(13,5,5,5,4,3,1,6,1,2,1,1,1,3,2,0,1,0)\n"
        + "(6,7,5,3,0,5,0,2,0,1,1,0,1,1,0,0,1,0)\n"
        + "(4,2,2,3,1,2,0,1,1,0,0,0,1,1,0,0,1,0)\n"
        + "(9,1,4,2,4,1,2,8,5,1,2,0,0,1,0,0,0,0)\n"
        + "(13,4,2,9,4,2,1,3,1,1,1,2,1,1,1,0,1,0)\n"
        + "(3,3,1,0,0,2,0,1,1,2,0,0,0,0,0,0,0,1)\n"
        + "(5,1,2,1,3,1,2,3,1,1,1,0,0,0,0,0,0,1)\n"
        + "(10,6,11,3,1,3,0,3,0,0,0,2,2,2,2,0,1,1)\n"
        + "(4,1,2,1,0,1,0,3,1,1,0,0,0,0,1,0,0,0)\n"
        + "(8,2,2,1,2,1,1,5,1,1,1,1,0,2,1,1,0,1)\n"
        + "(0,5,6,0,1,3,0,0,0,0,0,0,1,0,0,1,0,1)\n"
        + "(8,2,2,2,1,2,1,4,2,1,0,0,0,2,1,0,1,0)\n"
        + "(1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)\n"
        + "(5,2,1,2,1,2,1,1,1,0,0,0,1,1,1,0,1,0)\n"
        + "(2,0,1,0,3,0,2,1,1,0,1,0,0,2,0,0,0,0)\n"
        + "(10,1,2,3,1,0,1,2,0,0,0,1,0,2,1,0,0,1)\n"
        + "(2,0,1,2,0,0,0,0,0,0,0,1,0,0,0,0,1,0)\n"
        + "(1,2,2,1,0,2,0,0,0,1,0,0,0,0,0,0,0,0)"
        ;
		IntMatrix intMatrix = IntMatrix.readMatrix(matrixS);
		Assert.assertEquals(matrixS,  intMatrix.toString());
		Assert.assertEquals(18,  intMatrix.getCols());
		Assert.assertEquals(25,  intMatrix.getRows());
	}
}
