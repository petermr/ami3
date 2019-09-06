package org.contentmine.eucl.euclid.euclid;


import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.ParsedSymop;
import org.contentmine.eucl.euclid.Transform3;
import org.junit.Assert;
import org.junit.Test;

public class ParsedSymopTest {

	private final static Logger LOG = Logger.getLogger(ParsedSymopTest.class);

	@Test
	public void parseXYZ() {
		ParsedSymop symop = ParsedSymop.createSymop("x");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		symop = ParsedSymop.createSymop("X");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		symop = ParsedSymop.createSymop("y");
		Assert.assertEquals("xyz", "y", symop.getXyz());
		symop = ParsedSymop.createSymop("+z");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		symop = ParsedSymop.createSymop("-z");
		Assert.assertEquals("xyz", "-z", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertNull("xyz", symop.getNumber());
		try {
			symop = ParsedSymop.createSymop("a");
			Assert.fail();
		} catch (Exception e) {
			
		}
	}
	
	@Test
	public void parseXYZXYZ() {
		ParsedSymop symop = ParsedSymop.createSymop("+x-y");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "-y", symop.getXyz1());
		symop = ParsedSymop.createSymop("-x-y");
		Assert.assertEquals("xyz", "-x", symop.getXyz());
		Assert.assertEquals("xyz", "-y", symop.getXyz1());
		symop = ParsedSymop.createSymop("-x+y");
		Assert.assertEquals("xyz", "-x", symop.getXyz());
		Assert.assertEquals("xyz", "y", symop.getXyz1());
		try {
			symop = ParsedSymop.createSymop("xy");
		} catch (Exception e) {
			LOG.warn("'xy' is not picked up as error, sorry");
		}
	}
	
	@Test
	public void parseNUMB_XYZ() {
		ParsedSymop symop = ParsedSymop.createSymop("0.5+x");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop(".25-y");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-0.75+z");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	
	@Test
	public void parseNUMB_XYZXYZ() {
		ParsedSymop symop = ParsedSymop.createSymop("0.5+x+y");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "y", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop(".25-y-x");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-0.75+z-x");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	@Test
	public void parseFRACT_XYZ() {
		ParsedSymop symop = ParsedSymop.createSymop("1/2+x");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("1/4-y");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-3/4+z");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	@Test
	public void parseFRACT_XYZXYZ() {
		ParsedSymop symop = ParsedSymop.createSymop("1/2+x+y");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "y", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("1/4-y-x");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-3/4+z-x");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	
	
	@Test
	public void parseXYZ_NUMB() {
		ParsedSymop symop = ParsedSymop.createSymop("x+0.5");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-y+.25");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("z-0.75");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	
	@Test
	public void parseXYZXYZ_NUMB() {
		ParsedSymop symop = ParsedSymop.createSymop("x+y+0.5");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "y", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-y-x+.25");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("z-x-0.75");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	@Test
	public void parseXYZ_FRACT() {
		ParsedSymop symop = ParsedSymop.createSymop("x+1/2");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-y+1/4");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("z-3/4");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	@Test
	public void parseXYZXYZ_FRACT() {
		ParsedSymop symop = ParsedSymop.createSymop("x+y+1/2");
		Assert.assertEquals("xyz", "x", symop.getXyz());
		Assert.assertEquals("xyz", "y", symop.getXyz1());
		Assert.assertEquals("xyz", 0.5, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("-y-x+1/4");
		Assert.assertEquals("xyz", "-y", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", 0.25, symop.getNumber(), 0.001);
		symop = ParsedSymop.createSymop("z-x-3/4");
		Assert.assertEquals("xyz", "z", symop.getXyz());
		Assert.assertEquals("xyz", "-x", symop.getXyz1());
		Assert.assertEquals("xyz", -0.75, symop.getNumber(), 0.001);
	}
	
	@Test
	public void createTransformTest() {
		Transform3 transform = ParsedSymop.createTransform(new String[]{"x+y+1/2", "-y-x+1/4", "z-x-3/4"});
		Assert.assertEquals("matrix", "{4,4}\n"+
"(1.0,1.0,0.0,0.5)\n"+
"(-1.0,-1.0,0.0,0.25)\n"+
"(-1.0,0.0,1.0,-0.75)\n"+
"(0.0,0.0,0.0,1.0)", transform.toString());
	}
	
	
}
