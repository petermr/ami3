package org.contentmine.graphics.svg;


import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Range;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.CubicPrimitive;
import org.contentmine.graphics.svg.path.LinePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SVGPathPrimitiveTest {

	private final static Logger LOG = Logger.getLogger(SVGPathPrimitiveTest.class);

	private static final Angle ANGLE_EPS = new Angle(0.0001, Units.RADIANS);
	
	static String D1 = 
			"M110.7 262.44 " +
			"L110.82 261.839 " +
			"L111.0 260.459 " +
			"L111.06 259.98 " +
			"C111.12 259.62 111.42 259.38 111.78 259.44 " +
			"C112.14 259.5 112.38 259.8 112.32 260.16 " +
			"L112.26 260.64 " +
			"L112.02 262.019 " +
			"L111.96 262.62 " +
			"C111.9 262.98 111.6 263.22 111.24 263.16 " +
			"C110.88 263.1 110.64 262.8 110.7 262.43 " +
			"Z";
	
	static String D2 = 
			"m1 1 M 2 2";
	
	static String D3 = 
			"M1 1 M 2 2";
	
	static String D4 = 
			"M1 1 M 2 2";
	
	static String D5 = 
			"M1 1 m 1 1";
	
	static String D6 = 
			"M1 1 L 2 2";
	
	static String D7 = 
			"M1 1 l 1 1";
	

	
	@Test
	public void testRelativeMoveCommandAtStart() {
		 PathPrimitiveList primitiveList1 = PathPrimitiveList.createPrimitiveList(D2);
		 PathPrimitiveList primitiveList2 = PathPrimitiveList.createPrimitiveList(D3);
		 Assert.assertEquals(primitiveList1.toString(), primitiveList2.toString());
	}
	
	@Test
	public void testRelativeMoveCommandNotAtStart() {
		 PathPrimitiveList primitiveList1 = PathPrimitiveList.createPrimitiveList(D4);
		 PathPrimitiveList primitiveList2 = PathPrimitiveList.createPrimitiveList(D5);
		 Assert.assertEquals(primitiveList1.toString(), primitiveList2.toString());
	}
	
	@Test
	public void testRelativeLineCommand() {
		 PathPrimitiveList primitiveList1 = PathPrimitiveList.createPrimitiveList(D6);
		 PathPrimitiveList primitiveList2 = PathPrimitiveList.createPrimitiveList(D7);
		 Assert.assertEquals(primitiveList1.toString(), primitiveList2.toString());
	}

	@Test
	public void testString() {
		 PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(D1);
		 Assert.assertEquals("l", 12, primitiveList.size());
		 Assert.assertTrue("m", primitiveList.get(0) instanceof MovePrimitive);
		 Assert.assertTrue("l", primitiveList.get(1) instanceof LinePrimitive);
		 Assert.assertTrue("c", primitiveList.get(10) instanceof CubicPrimitive);
		 Assert.assertTrue("z", primitiveList.get(11) instanceof ClosePrimitive);
	}

	@Test
	public void testZerothCoord1() {
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(D1);
		Assert.assertTrue("m", new Real2(110.7, 262.43).isEqualTo(primitiveList.get(0).getZerothCoord(), 0.001));
		Assert.assertTrue("l", new Real2(110.7, 262.44).isEqualTo(primitiveList.get(1).getZerothCoord(), 0.001));
		Assert.assertTrue("c", new Real2(111.24, 263.16).isEqualTo(primitiveList.get(10).getZerothCoord(), 0.001));
		Assert.assertTrue("z", new Real2(110.7, 262.43).isEqualTo(primitiveList.get(11).getZerothCoord(), 0.001));
	}
	

	@Test
	public void testFirstCoord() {
		 PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(D1);
		 Assert.assertTrue("m", new Real2(110.7, 262.44).isEqualTo(primitiveList.get(0).getFirstCoord(), 0.001));
		 Assert.assertTrue("l", new Real2(110.82, 261.839).isEqualTo(primitiveList.get(1).getFirstCoord(), 0.001));
		 Assert.assertTrue("c", new Real2(110.88, 263.1).isEqualTo(primitiveList.get(10).getFirstCoord(), 0.001));
		 // not sure about this one
//		 Assert.assertTrue("z", new Real2(110.7, 262.43).isEqualTo(primitiveList.get(11).getLastCoord(), 0.001));
	}

	@Test
	public void testLastCoord() {
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(D1);
		 Assert.assertTrue("m", new Real2(110.7, 262.44).isEqualTo(primitiveList.get(0).getLastCoord(), 0.001));
		 Assert.assertTrue("l", new Real2(110.82, 261.839).isEqualTo(primitiveList.get(1).getLastCoord(), 0.001));
		 Assert.assertTrue("c", new Real2(110.7, 262.43).isEqualTo(primitiveList.get(10).getLastCoord(), 0.001));
		 // not sure about this one
//		 Assert.assertTrue("z", new Real2(110.7, 262.44).isEqualTo(primitiveList.get(11).getLastCoord(), 0.001));
	}
	
	static String RELATIVE0 = "m 1,2 3,4";
	static String RELATIVE1 = "m 28,516 304,0 0,-388 -304,0 z";
/**
 * 	  <path

	     d="m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,22.567 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,22.566 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,11.284 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,11.284 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,56.416 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,11.284 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z"
	     svgx:z="3907"
	     id="path159010"
	     style="fill:#ffff00;stroke:#000000;stroke-width:0" />
*/

	@Test
	public void testImplicitLine0() {
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(RELATIVE0);
		primitiveList.format(3);
		Assert.assertEquals(2, primitiveList.size());
		 Assert.assertTrue("m", new Real2(1, 2).isEqualTo(primitiveList.get(0).getLastCoord(), 0.001));
		 Assert.assertTrue("l", new Real2(4, 6).isEqualTo(primitiveList.get(1).getLastCoord(), 0.001));


	}
	
	@Test
	public void testImplicitLine1() {
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(RELATIVE1);
		Assert.assertEquals(5, primitiveList.size());
		 Assert.assertTrue("m", new Real2(28.0, 516.0).isEqualTo(primitiveList.get(0).getLastCoord(), 0.001));
		 Assert.assertTrue("l", new Real2(28.0, 516.0).isEqualTo(primitiveList.get(4).getLastCoord(), 0.001));


	}
	
	@Test
	public void testInternalRelativeMoveRect2() {
	    String d=""
	    		+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z "
	    		+ "m 99.317,0 69.449,0 0,11.284 -69.449,0 z";
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(d);
		Assert.assertEquals("rel move", "["
				+ "M34.264 165.875 , L133.58 165.875 , L133.58 177.159 , L34.264 177.159 , Z, "
				+ "M133.581 165.875 , L203.029 165.875 , L203.029 177.159 , L133.58 177.159 , Z"
				+ "]", primitiveList.toString());
		Assert.assertEquals(10, primitiveList.size());
		SVGPath path = new SVGPath(primitiveList.getDString());
		SVGSVG.wrapAndWriteAsSVG(path, new File("target/path/rect2.svg"));
		
	}
	
	@Test
	public void testInternalRelativeMoveRect3() {
	    String d=""
	    		+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z "
	    		+ "m 99.317,0 69.449,0 0,11.284 -69.449,0 z "
				+ "m 69.449,0 63.426,0 0,11.284 -63.426,0 z "
	    		;
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(d);
		Assert.assertEquals("rel move", "["
				+ "M34.264 165.875 , L133.58 165.875 , L133.58 177.159 , L34.264 177.159 , Z, M133.581 165.875 , L203.029 165.875 , L203.029 177.159 , L133.58 177.159 , Z, M203.029 165.875 , L266.455 165.875 , L266.455 177.159 , L203.029 177.159 , Z"
				+ "]", primitiveList.toString());
		Assert.assertEquals(15, primitiveList.size());
		SVGPath path = new SVGPath(primitiveList.getDString());
		SVGSVG.wrapAndWriteAsSVG(path, new File("target/path/rect3.svg"));
		
	}
	
	@Test
	@Ignore // passes eclipse but fails mvn
	public void testInternalRelativeMoveRect4() {
	    String d=""
	    		+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z "
	    		+ "m 99.317,0 69.449,0 0,11.284 -69.449,0 z "
				+ "m 69.449,0 63.426,0 0,11.284 -63.426,0 z "
				+ "m 63.426,0 62.112,0 0,11.284 -62.112,0 z "
	    		;
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(d);
		Assert.assertEquals("rel move", "["
				+ "M34.264 165.875 , L133.58 165.875 , L133.58 177.159 , L34.264 177.159 , Z, M133.581 165.875 , L203.029 165.875 , L203.029 177.159 , L133.58 177.159 , Z, M203.029 165.875 , L266.455 165.875 , L266.455 177.159 , L203.029 177.159 , Z, M266.456 165.875 , L328.568 165.875 , L328.568 177.159 , L266.456 177.159 , Z"
				+ "]", primitiveList.toString());
		Assert.assertEquals(20, primitiveList.size());
		SVGPath path = new SVGPath(primitiveList.getDString());
		SVGSVG.wrapAndWriteAsSVG(path, new File("target/path/rect4.svg"));
		
	}
	
	@Test
	@Ignore // decimal point
	public void testTwoRowsOfInternalMovesRect2x4() {
		String d = ""
				+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z "
				+ "m 99.317,0 69.449,0 0,11.284 -69.449,0 z "
				+ "m 69.449,0 63.426,0 0,11.284 -63.426,0 z "
				+ "m 63.426,0 62.112,0 0,11.284 -62.112,0 z "
				+ ""
				+ "m -232.192,22.567 99.316,0 0,11.283 -99.316,0 z "
				+ "m 99.317,0 69.449,0 0,11.283 -69.449,0 z "
				+ "m 69.449,0 63.426,0 0,11.283 -63.426,0 z "
				+ "m 63.426,0 62.112,0 0,11.283 -62.112,0 z ";
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(d);
		Assert.assertEquals(40, primitiveList.size());
		Assert.assertEquals("rel move", "["
				+ "M34.264 165.875 , L133.58 165.875 , L133.58 177.159 , L34.264 177.159 , Z, M133.581 165.875 , L203.029 165.875 , L203.029 177.159 , L133.58 177.159 , Z, M203.029 165.875 , L266.455 165.875 , L266.456 177.159 , L203.029 177.159 , Z, M266.456 165.875 , L328.568 165.875 , L328.568 177.159 , L266.455 177.159 , Z, M34.263 188.442 , L133.579 188.442 , L133.579 199.725 , L34.263 199.725 , Z, M133.58 188.442 , L203.029 188.442 , L203.029 199.725 , L133.58 199.725 , Z, M203.029 188.442 , L266.455 188.442 , L266.455 199.725 , L203.029 199.725 , Z, M266.455 188.442 , L328.568 188.442 , L328.568 199.725 , L266.455 199.725 , Z"
				+ "]", primitiveList.toString());
		SVGPath path = new SVGPath(primitiveList.getDString());
		SVGSVG.wrapAndWriteAsSVG(path, new File("target/path/rect2x4.svg"));

	}

	@Test
	@Ignore // decimal points in test
	public void testTwoRowsOfInternalMovesGrid() {
		String d=""
				+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,22.567 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,22.566 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,11.284 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,11.284 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,56.416 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z m -232.192,11.283 99.316,0 0,11.284 -99.316,0 z m 99.317,0 69.449,0 0,11.284 -69.449,0 z m 69.449,0 63.426,0 0,11.284 -63.426,0 z m 63.426,0 62.112,0 0,11.284 -62.112,0 z m -232.192,11.284 99.316,0 0,11.283 -99.316,0 z m 99.317,0 69.449,0 0,11.283 -69.449,0 z m 69.449,0 63.426,0 0,11.283 -63.426,0 z m 63.426,0 62.112,0 0,11.283 -62.112,0 z";

		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(d);
		Assert.assertEquals(4*22*5, primitiveList.size());
		Assert.assertEquals("rel move", "["
				+ "M34.264 165.875 , L133.58 165.875 , L133.58 177.159 , L34.264 177.159 , Z, "
				+ "M133.581 165.875 , L203.029 165.875 , L203.029 177.159 , L133.58 177.159 , Z, "
				+ "M203.029 165.875 , L266.455 165.875 , L266.455 177.159 , L203.029 177.159 , Z, "
				+ "M266.456 165.875 , L328.568 165.875 , L328.568 177.159 , L266.456 177.159 , Z, "
				+ "M34.263 188.442 , L133.579 188.442 , L133.579 199.725 , L34.263 199.725 , Z, "
				+ "M133.58 188.442 , L203.029 188.442 , L203.029 199.725 , L133.58 199.725 , Z, "
				+ "M203.029 188.442 , L266.455 188.442 , L266.455 199.725 , L203.029 199.725 , Z, "
				+ "M266.455 188.442 , L328.568 188.442 , L328.568 199.725 , L266.455 199.725 , Z, "
				+ "M34.263 211.008 , L133.579 211.008 , L133.579 222.291 , L34.263 222.291 , Z, "
				+ "M133.58 211.008 , L203.029 211.008 , L203.029 222.291 , L133.58 222.291 , Z, "
				+ "M203.029 211.008 , L266.455 211.008 , L266.455 222.291 , L203.029 222.291 , Z, "
				+ "M266.455 211.008 , L328.568 211.008 , L328.568 222.291 , L266.455 222.291 , Z, "
				+ "M34.263 222.291 , L133.579 222.291 , L133.579 233.573 , L34.263 233.573 , Z, "
				+ "M133.58 222.291 , L203.029 222.291 , L203.029 233.573 , L133.58 233.573 , Z, "
				+ "M203.029 222.291 , L266.455 222.291 , L266.455 233.573 , L203.029 233.573 , Z, "
				+ "M266.455 222.291 , L328.568 222.291 , L328.568 233.573 , L266.455 233.573 , Z, "
				+ "M34.263 233.573 , L133.579 233.573 , L133.579 244.857 , L34.263 244.857 , Z, "
				+ "M133.58 233.573 , L203.029 233.573 , L203.029 244.857 , L133.58 244.857 , Z, "
				+ "M203.029 233.573 , L266.455 233.573 , L266.455 244.857 , L203.029 244.857 , Z, "
				+ "M266.455 233.573 , L328.568 233.573 , L328.568 244.857 , L266.455 244.857 , Z, "
				+ "M34.263 244.857 , L133.579 244.857 , L133.579 256.14 , L34.263 256.14 , Z, "
				+ "M133.58 244.857 , L203.029 244.857 , L203.029 256.14 , L133.58 256.14 , Z, "
				+ "M203.029 244.857 , L266.455 244.857 , L266.455 256.14 , L203.029 256.14 , Z, "
				+ "M266.455 244.857 , L328.568 244.857 , L328.568 256.14 , L266.455 256.14 , Z, "
				+ "M34.263 256.14 , L133.579 256.14 , L133.579 267.424 , L34.263 267.424 , Z, "
				+ "M133.58 256.14 , L203.029 256.14 , L203.029 267.424 , L133.58 267.424 , Z, "
				+ "M203.029 256.14 , L266.455 256.14 , L266.455 267.424 , L203.029 267.424 , Z, "
				+ "M266.455 256.14 , L328.568 256.14 , L328.568 267.424 , L266.455 267.424 , Z, "
				+ "M34.263 267.424 , L133.579 267.424 , L133.579 278.707 , L34.263 278.707 , Z, "
				+ "M133.58 267.424 , L203.029 267.424 , L203.029 278.707 , L133.58 278.707 , Z, "
				+ "M203.029 267.424 , L266.455 267.424 , L266.455 278.707 , L203.029 278.707 , Z, "
				+ "M266.455 267.424 , L328.568 267.424 , L328.568 278.707 , L266.455 278.707 , Z, "
				+ "M34.263 278.707 , L133.579 278.707 , L133.579 289.99 , L34.263 289.99 , Z, "
				+ "M133.58 278.707 , L203.029 278.707 , L203.029 289.99 , L133.58 289.99 , Z, "
				+ "M203.029 278.707 , L266.455 278.707 , L266.455 289.99 , L203.029 289.99 , Z, "
				+ "M266.455 278.707 , L328.568 278.707 , L328.568 289.99 , L266.455 289.99 , Z, "
				+ "M34.263 289.99 , L133.579 289.99 , L133.579 301.273 , L34.263 301.273 , Z, "
				+ "M133.58 289.99 , L203.029 289.99 , L203.029 301.273 , L133.58 301.273 , Z, "
				+ "M203.029 289.99 , L266.455 289.99 , L266.455 301.273 , L203.029 301.273 , Z, "
				+ "M266.455 289.99 , L328.568 289.99 , L328.568 301.273 , L266.455 301.273 , Z, "
				+ "M34.263 301.273 , L133.579 301.273 , L133.579 312.556 , L34.263 312.556 , Z, "
				+ "M133.58 301.273 , L203.029 301.273 , L203.029 312.556 , L133.58 312.556 , Z, "
				+ "M203.029 301.273 , L266.455 301.273 , L266.455 312.556 , L203.029 312.556 , Z, "
				+ "M266.455 301.273 , L328.568 301.273 , L328.568 312.556 , L266.455 312.556 , Z, "
				+ "M34.263 312.556 , L133.579 312.556 , L133.579 323.84 , L34.263 323.84 , Z, "
				+ "M133.58 312.556 , L203.029 312.556 , L203.029 323.84 , L133.58 323.84 , Z, "
				+ "M203.029 312.556 , L266.455 312.556 , L266.455 323.84 , L203.029 323.84 , Z, "
				+ "M266.455 312.556 , L328.568 312.556 , L328.568 323.84 , L266.455 323.84 , Z, "
				+ "M34.263 323.84 , L133.579 323.84 , L133.579 335.123 , L34.263 335.123 , Z, "
				+ "M133.58 323.84 , L203.029 323.84 , L203.029 335.123 , L133.58 335.123 , Z, "
				+ "M203.029 323.84 , L266.455 323.84 , L266.455 335.123 , L203.029 335.123 , Z, "
				+ "M266.455 323.84 , L328.568 323.84 , L328.568 335.123 , L266.455 335.123 , Z, "
				+ "M34.263 335.123 , L133.579 335.123 , L133.579 346.406 , L34.263 346.406 , Z, "
				+ "M133.58 335.123 , L203.029 335.123 , L203.029 346.406 , L133.58 346.406 , Z, "
				+ "M203.029 335.123 , L266.455 335.123 , L266.455 346.406 , L203.029 346.406 , Z, "
				+ "M266.455 335.123 , L328.568 335.123 , L328.568 346.406 , L266.455 346.406 , Z, "
				+ "M34.263 346.406 , L133.579 346.406 , L133.579 357.689 , L34.263 357.689 , Z, "
				+ "M133.58 346.406 , L203.029 346.406 , L203.029 357.689 , L133.58 357.689 , Z, "
				+ "M203.029 346.406 , L266.455 346.406 , L266.455 357.689 , L203.029 357.689 , Z, "
				+ "M266.455 346.406 , L328.568 346.406 , L328.568 357.689 , L266.455 357.689 , Z, "
				+ "M34.263 357.689 , L133.579 357.689 , L133.579 368.972 , L34.263 368.972 , Z, "
				+ "M133.58 357.689 , L203.029 357.689 , L203.029 368.972 , L133.58 368.972 , Z, "
				+ "M203.029 357.689 , L266.455 357.689 , L266.455 368.972 , L203.029 368.972 , Z, "
				+ "M266.455 357.689 , L328.568 357.689 , L328.568 368.972 , L266.455 368.972 , Z, "
				+ "M34.263 414.105 , L133.579 414.105 , L133.579 425.388 , L34.263 425.388 , Z, "
				+ "M133.58 414.105 , L203.029 414.105 , L203.029 425.388 , L133.58 425.388 , Z, "
				+ "M203.029 414.105 , L266.455 414.105 , L266.455 425.388 , L203.029 425.388 , Z, "
				+ "M266.455 414.105 , L328.568 414.105 , L328.568 425.388 , L266.455 425.388 , Z, "
				+ "M34.263 425.388 , L133.579 425.388 , L133.579 436.671 , L34.263 436.671 , Z, "
				+ "M133.58 425.388 , L203.029 425.388 , L203.029 436.671 , L133.58 436.671 , Z, "
				+ "M203.029 425.388 , L266.455 425.388 , L266.455 436.671 , L203.029 436.671 , Z, "
				+ "M266.455 425.388 , L328.568 425.388 , L328.568 436.671 , L266.455 436.671 , Z, "
				+ "M34.263 436.671 , L133.579 436.671 , L133.579 447.954 , L34.263 447.954 , Z, "
				+ "M133.58 436.671 , L203.029 436.671 , L203.029 447.954 , L133.58 447.954 , Z, "
				+ "M203.029 436.671 , L266.455 436.671 , L266.455 447.954 , L203.029 447.954 , Z, "
				+ "M266.455 436.671 , L328.568 436.671 , L328.568 447.954 , L266.455 447.954 , Z, "
				+ "M34.263 447.954 , L133.579 447.954 , L133.579 459.237 , L34.263 459.237 , Z, "
				+ "M133.58 447.954 , L203.029 447.954 , L203.029 459.237 , L133.58 459.237 , Z, "
				+ "M203.029 447.954 , L266.455 447.954 , L266.455 459.237 , L203.029 459.237 , Z, "
				+ "M266.455 447.954 , L328.568 447.954 , L328.568 459.237 , L266.455 459.237 , Z, "
				+ "M34.263 459.237 , L133.579 459.237 , L133.579 470.521 , L34.263 470.521 , Z, "
				+ "M133.58 459.237 , L203.029 459.237 , L203.029 470.521 , L133.58 470.521 , Z, "
				+ "M203.029 459.237 , L266.455 459.237 , L266.455 470.521 , L203.029 470.521 , Z, "
				+ "M266.455 459.237 , L328.568 459.237 , L328.568 470.521 , L266.455 470.521 , Z, "
				+ "M34.263 470.521 , L133.579 470.521 , L133.579 481.804 , L34.263 481.804 , Z, "
				+ "M133.58 470.521 , L203.029 470.521 , L203.029 481.804 , L133.58 481.804 , Z, "
				+ "M203.029 470.521 , L266.455 470.521 , L266.455 481.804 , L203.029 481.804 , Z, "
				+ "M266.455 470.521 , L328.568 470.521 , L328.568 481.804 , L266.455 481.804 , Z"
				+ "]", primitiveList.toString());
		SVGPath path = new SVGPath(primitiveList.getDString());
		SVGSVG.wrapAndWriteAsSVG(path, new File("target/path/grid.svg"));

	}


	@Test
	public void testGetDistance() {
		 PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(D1);
		 Real2 vector = primitiveList.get(0).getTranslation();
		 Assert.assertTrue("m"+vector, new Real2(0.0, 0.01).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(1).getTranslation();
		 
		 Assert.assertTrue("l"+vector, new Real2(0.12, -0.6).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(2).getTranslation();
		 Assert.assertTrue("l"+vector, new Real2(0.18, -1.38).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(3).getTranslation();
		 Assert.assertTrue("l"+vector, new Real2(0.06, -0.479).isEqualTo(vector, 0.001));
		 
		 vector = primitiveList.get(4).getTranslation();
		 Assert.assertTrue("c"+vector, new Real2(0.72, -0.54).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(5).getTranslation();
		 Assert.assertTrue("c"+vector, new Real2(0.54, 0.72).isEqualTo(vector, 0.001));

		 vector = primitiveList.get(6).getTranslation();   // 6 == -3
		 Assert.assertTrue("l"+vector, new Real2(-0.06, 0.48).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(7).getTranslation();  // 7 ~~ -2
		 Assert.assertTrue("l"+vector, new Real2(-0.24, 1.38).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(8).getTranslation();  // 8 ~~ -1
		 Assert.assertTrue("l"+vector, new Real2(-0.06, 0.60).isEqualTo(vector, 0.001));
		 
		 vector = primitiveList.get(9).getTranslation();
		 Assert.assertTrue("c"+vector, new Real2(-0.72, 0.54).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(10).getTranslation();
		 Assert.assertTrue("c"+vector, new Real2(-0.54, -0.73).isEqualTo(vector, 0.001));
		 vector = primitiveList.get(9).getZerothCoord().subtract(primitiveList.get(10).getLastCoord());
		 Assert.assertTrue("9-10 "+vector, new Real2(1.26, .19).isEqualTo(vector, 0.001));
		 Assert.assertNull("z", primitiveList.get(11).getTranslation());
	}

	@Test
	public void testGetAngle() {
		Angle pi2 = new Angle(Math.PI/2.);
		pi2.setRange(Range.UNSIGNED);
		PathPrimitiveList primitiveList = PathPrimitiveList.createPrimitiveList(D1);
		 Angle angle = primitiveList.get(0).getAngle();
		 Assert.assertNull("m", angle);
		 angle = primitiveList.get(1).getAngle();
		 Assert.assertTrue("l"+angle, angle.isEqualTo(0.0, 0.001));
		 
		 angle = primitiveList.get(4).getAngle();
		 angle.setRange(Range.UNSIGNED);
		 Assert.assertTrue("c"+angle, angle.isEqualTo(Math.PI/2.0, 0.001));
		 angle = primitiveList.get(5).getAngle();
		 angle.setRange(Range.UNSIGNED);
		 Assert.assertTrue("c"+angle, angle.isEqualTo(Math.PI/2.0, 0.001));
		 
		 angle = primitiveList.get(9).getAngle();
		 angle.setRange(Range.UNSIGNED);
		 Assert.assertTrue("c"+angle, angle.isEqualTo(Math.PI/2.0, 0.001));
		 angle = primitiveList.get(10).getAngle();
		 angle.setRange(Range.UNSIGNED);
		 // this one is not quite PI/2 - about 0.3% out
		 Assert.assertTrue("c"+angle,angle.isEqualTo(Math.PI/2.0, 0.006));
		 Assert.assertNull("z", primitiveList.get(11).getAngle());
	}
	
	/*<svg xmlns="http://www.w3.org/2000/svg">
	 <g>
	  <path stroke="black" fill="#000000" stroke-width="0.0" 
	  d="M172.14 512.58 
	  L172.14 504.3 
	  C172.14 504.18 172.26 504.06 172.38 504.06 
	  C172.5 504.06 172.62 504.18 172.62 504.3 
	  L172.62 512.58 
	  C172.62 512.76 172.5 512.88 172.38 512.88 
	  C172.26 512.88 172.14 512.76 172.14 512.58 "
	  svgx:z="1737"/>
	 </g>
	</svg>*/
	@Test
	public void checkAngleForClosedCurve() {
		SVGPath ROUNDED_LINE_SVG = (SVGPath) SVGElement.readAndCreateSVG(SVGHTMLFixtures.ROUNDED_LINE_SVG_FILE)
				.getChildElements().get(0).getChildElements().get(0);
	    PathPrimitiveList primitiveList = ROUNDED_LINE_SVG.getOrCreatePathPrimitiveList();
		Assert.assertEquals("MLCCLCC", ROUNDED_LINE_SVG.getOrCreateSignatureAttributeValue());
		Assert.assertTrue("closed", primitiveList.isClosed());
		Assert.assertNull("angle0", primitiveList.getAngle(0)); //MOVE
		Assert.assertEquals("angle1", 0.0, primitiveList.getAngle(1).getRadian(), ANGLE_EPS.getRadian());
		Assert.assertEquals("angle2", Math.PI / 2., primitiveList.getAngle(2).getRadian(), ANGLE_EPS.getRadian());
		Assert.assertEquals("angle3", Math.PI / 2., primitiveList.getAngle(3).getRadian(), ANGLE_EPS.getRadian());
		Assert.assertEquals("angle4", 0.0, primitiveList.getAngle(4).getRadian(), ANGLE_EPS.getRadian());
		Assert.assertEquals("angle5", Math.PI / 2., primitiveList.getAngle(5).getRadian(), ANGLE_EPS.getRadian());
		Assert.assertEquals("angle6", Math.PI / 2., primitiveList.getAngle(6).getRadian(), ANGLE_EPS.getRadian());
	}

	@Test
	public void testQuadrantValue() {
		SVGPath ROUNDED_LINE_SVG = (SVGPath) SVGElement.readAndCreateSVG(SVGHTMLFixtures.ROUNDED_LINE_SVG_FILE)
				.getChildElements().get(0).getChildElements().get(0);
		PathPrimitiveList primitiveList = ROUNDED_LINE_SVG.getOrCreatePathPrimitiveList();
		Assert.assertEquals("MLCCLCC", ROUNDED_LINE_SVG.getOrCreateSignatureAttributeValue());
		Assert.assertEquals("q0", 0, primitiveList.quadrantValue(0, ANGLE_EPS));
		Assert.assertEquals("q1", 0, primitiveList.quadrantValue(1, ANGLE_EPS));
		Assert.assertEquals("q2", 1, primitiveList.quadrantValue(2, ANGLE_EPS));
		Assert.assertEquals("q3", 1, primitiveList.quadrantValue(3, ANGLE_EPS));
		Assert.assertEquals("q4", 0, primitiveList.quadrantValue(4, ANGLE_EPS));
		Assert.assertEquals("q5", 1, primitiveList.quadrantValue(5, ANGLE_EPS));
		Assert.assertEquals("q6", 1, primitiveList.quadrantValue(6, ANGLE_EPS));
	}

	@Test
	public void testTwoQuadrantList() {
		SVGPath ROUNDED_LINE_SVG = (SVGPath) SVGElement.readAndCreateSVG(SVGHTMLFixtures.ROUNDED_LINE_SVG_FILE)
				.getChildElements().get(0).getChildElements().get(0);
	PathPrimitiveList primitiveList = ROUNDED_LINE_SVG.getOrCreatePathPrimitiveList();
		List<Integer> quadStartList = primitiveList.getUTurnList(ANGLE_EPS);
		Assert.assertEquals("quads", 2, quadStartList.size());
		Assert.assertEquals("quads1", 2, (int) quadStartList.get(0));
		Assert.assertEquals("quads2", 5, (int) quadStartList.get(1));
	}
	
	@Test
	public void testFindSemiCircles() {
		SVGPath ROUNDED_LINE_SVG = (SVGPath) SVGElement.readAndCreateSVG(SVGHTMLFixtures.ROUNDED_LINE_SVG_FILE)
				.getChildElements().get(0).getChildElements().get(0);
		PathPrimitiveList primitiveList = ROUNDED_LINE_SVG.getOrCreatePathPrimitiveList();
		Assert.assertEquals("MLCCLCC", ROUNDED_LINE_SVG.getOrCreateSignatureAttributeValue());
		Assert.assertTrue(primitiveList.isUTurn(2, ANGLE_EPS));
	}
	
	@Test
	public void testTwoQuadrantListMolecule() {
		List<SVGPath> pathList = SVGPath.extractPaths(SVGElement.readAndCreateSVG(SVGHTMLFixtures.IMAGE_2_13_SVG));
		Assert.assertEquals("paths", 13, pathList.size());
		SVGPath path = pathList.get(3);
		SVGSVG.wrapAndWriteAsSVG(path, new File("target/badPath.svg"));
		LOG.trace(path.toXML());
		PathPrimitiveList primitiveList = path.getOrCreatePathPrimitiveList();
		List<Integer> quadStartList = primitiveList.getUTurnList(new Angle(0.1, Units.RADIANS));
		Assert.assertEquals("quads", 2, quadStartList.size());
		Assert.assertEquals("quads1", 2, (int) quadStartList.get(0));
		Assert.assertEquals("quads2", 5, (int) quadStartList.get(1));
	}

	// ==================================================================
	
	
}
