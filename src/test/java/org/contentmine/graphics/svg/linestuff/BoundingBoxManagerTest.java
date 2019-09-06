package org.contentmine.graphics.svg.linestuff;

import java.util.List;

import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager.BoxEdge;
import org.junit.Assert;
import org.junit.Test;

/** tests BoundingBoxManager which manages a list of Bounding boxes
 * 
 * @author pm286
 *
 */
public class BoundingBoxManagerTest {
	
	@Test
	public void testConstructorAndDefaults() {
		BoundingBoxManager boundingBoxManager = new BoundingBoxManager();
		List<Real2Range> bboxList = boundingBoxManager.getBBoxList();
		Assert.assertNotNull(bboxList);
		Assert.assertEquals(0, bboxList.size());
		List<Real2Range> emptyBoxList = boundingBoxManager.createEmptyBoxList(BoxEdge.XMIN);
		Assert.assertNotNull(emptyBoxList);
		Assert.assertEquals(0, emptyBoxList.size());
		Real2Range totalBox = boundingBoxManager.getTotalBox();
		Assert.assertNull(totalBox);
	}

	@Test
	public void testRanges() {
		BoundingBoxManager boundingBoxManager = new BoundingBoxManager();
		Real2Range r2r = new Real2Range(new RealRange(0.,100.), new RealRange(0.,100.));
		boundingBoxManager.add(r2r);
		r2r = new Real2Range(new RealRange(200.,300.), new RealRange(200.,400.));
		boundingBoxManager.add(r2r);
		List<Real2Range> bboxList = boundingBoxManager.getBBoxList();
		Assert.assertNotNull(bboxList);
		Assert.assertEquals(2, bboxList.size());
		Real2Range totalBox = boundingBoxManager.getTotalBox();
		Assert.assertNotNull(totalBox);
		totalBox.isEqualTo(new Real2Range(new RealRange(0.,300.), new RealRange(0.,400.)), 0.001);
	}
	
	@Test
	public void testEmptyBoxListX() {
		BoundingBoxManager boundingBoxManager = new BoundingBoxManager();
		boundingBoxManager.add(new Real2Range(new RealRange(0.,100.), new RealRange(0.,100.)));
		boundingBoxManager.add(new Real2Range(new RealRange(200.,300.), new RealRange(250.,350.)));
		boundingBoxManager.add(new Real2Range(new RealRange(350.,450.), new RealRange(500.,600.)));
		List<Real2Range> emptyBoxList = boundingBoxManager.createEmptyBoxList(BoxEdge.XMIN);
		Assert.assertNotNull(emptyBoxList);
		checkReal2RangeList(
				new Real2Range[] {
						new Real2Range(new RealRange(100., 200.), new RealRange(0., 600)),
						new Real2Range(new RealRange(300., 350.), new RealRange(0., 600)),
				},
				emptyBoxList, 0.001);
	}

	@Test
	public void testEmptyBoxListY() {
		BoundingBoxManager boundingBoxManager = new BoundingBoxManager();
		boundingBoxManager.add(new Real2Range(new RealRange(0.,100.), new RealRange(0.,100.)));
		boundingBoxManager.add(new Real2Range(new RealRange(200.,300.), new RealRange(250.,350.)));
		boundingBoxManager.add(new Real2Range(new RealRange(350.,450.), new RealRange(500.,600.)));
		List<Real2Range> emptyBoxList = boundingBoxManager.createEmptyBoxList(BoxEdge.YMIN);
		Assert.assertNotNull(emptyBoxList);
		checkReal2RangeList(
				new Real2Range[] {
						new Real2Range(new RealRange(0., 450), new RealRange(100., 250.)),
						new Real2Range(new RealRange(0., 450), new RealRange(350., 500.)),
				},
				emptyBoxList, 0.001);
	}

	/** boxes overlap in X direction (but not Y)
	 * 
	 */
	@Test
	public void testEmptyBoxListXNone() {
		BoundingBoxManager boundingBoxManager = new BoundingBoxManager();
		boundingBoxManager.add(new Real2Range(new RealRange(0.,100.), new RealRange(0.,100.)));
		boundingBoxManager.add(new Real2Range(new RealRange(70.,200.), new RealRange(200., 300.)));
		boundingBoxManager.add(new Real2Range(new RealRange(150.,300.), new RealRange(500.,600.)));
		List<Real2Range> emptyBoxXList = boundingBoxManager.createEmptyBoxList(BoxEdge.XMIN);
		Assert.assertNotNull(emptyBoxXList);
		checkReal2RangeList(
				new Real2Range[] {
				},
				emptyBoxXList, 0.001);
		
		List<Real2Range> emptyBoxYList = boundingBoxManager.createEmptyBoxList(BoxEdge.YMIN);
		Assert.assertNotNull(emptyBoxYList);
		BoundingBoxManagerTest.checkReal2RangeList(
				new Real2Range[] {
						new Real2Range(new RealRange(0., 300), new RealRange(100., 200.)),
						new Real2Range(new RealRange(0., 300), new RealRange(300., 500.)),
				},
				emptyBoxYList, 0.001);
	}

	public static void checkReal2RangeList(Real2Range[] refs, List<Real2Range> testList, double eps) {
		Assert.assertNotNull("refs should not be null", refs);
		Assert.assertNotNull("testList should not be null", testList);
		Assert.assertEquals("equal lengths", refs.length, testList.size());
		for (int i = 0; i < refs.length; i++) {
			Assert.assertTrue("range "+i, refs[i].isEqualTo(testList.get(i), eps));
		}
	}

}
