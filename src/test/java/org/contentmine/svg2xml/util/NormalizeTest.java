package org.contentmine.svg2xml.util;

import java.text.Normalizer;

import org.junit.Assert;
import org.junit.Test;

public class NormalizeTest {

	@Test
	public void testLigatures() {
		String s = "a"+(char)0xFB03+"x";
		Assert.assertEquals((char)'a', s.charAt(0));
		Assert.assertEquals(0xFB03, s.charAt(1));
		Assert.assertEquals((char)'x', s.charAt(2));
		Assert.assertFalse("non normalized", Normalizer.isNormalized(s, Normalizer.Form.NFKC));
		Assert.assertEquals(3, s.length());
		s = Normalizer.normalize(s, Normalizer.Form.NFKC);
		Assert.assertTrue("normalized", Normalizer.isNormalized(s, Normalizer.Form.NFKC));
		Assert.assertEquals(5, s.length());
		Assert.assertEquals((char)'a', s.charAt(0));
		Assert.assertEquals((char)'f', s.charAt(1));
		Assert.assertEquals((char)'f', s.charAt(2));
		Assert.assertEquals((char)'i', s.charAt(3));
		Assert.assertEquals((char)'x', s.charAt(4));
	}
	

	@Test
	public void testDiacritics() {
		String s = "a"+"e"+(char)0x300+"x";
		Assert.assertEquals('a', s.charAt(0));
		Assert.assertEquals((char)'e', s.charAt(1));
		Assert.assertEquals(0x0300, s.charAt(2));
		Assert.assertEquals((char)'x', s.charAt(3));
		Assert.assertFalse("non normalized", Normalizer.isNormalized(s, Normalizer.Form.NFKC));
		Assert.assertEquals(4, s.length());
		s = Normalizer.normalize(s, Normalizer.Form.NFKC);
		Assert.assertTrue("normalized", Normalizer.isNormalized(s, Normalizer.Form.NFKC));
		Assert.assertEquals(3, s.length());
		Assert.assertEquals((char)'a', s.charAt(0));
		Assert.assertEquals((char)232, (int)s.charAt(1));
		Assert.assertEquals((char)'x', s.charAt(2));
		s = "a"+"e"+(char)0x300+"e"+(char)0x301+"e"+(char)0x302+"e"+(char)0x303+"e"+(char)0x304+"e"+(char)0x305+
		        "e"+(char)0x306+"e"+(char)0x307+"e"+(char)0x308+"e"+(char)0x309+"e"+(char)0x30A+"e"+(char)0x30B+
		        "E"+(char)0x300+"E"+(char)0x301+"E"+(char)0x302+"E"+(char)0x303+"E"+(char)0x304+"E"+(char)0x305+
		        "E"+(char)0x306+"E"+(char)0x307+"E"+(char)0x308+"E"+(char)0x309+"E"+(char)0x30A+"E"+(char)0x30B+"x";
		Assert.assertEquals(50, s.length());
		s = Normalizer.normalize(s, Normalizer.Form.NFKC);
		Assert.assertEquals(32, s.length());
		// note that 0x0305, 0x030A and 0x030B do not combine to a normalized form for 'e' or 'E' 
		int[] chars ={'a',
				232,233,234,7869,275,'e',0x0305,
				277,279,235,7867,'e',0x030A,'e',0x030B,
				200,201,202,7868,274,'E',0x0305,
				276,278,203,7866,'E',0x030A,'E',0x030B,
				'x'};
		for (int i = 0; i < chars.length; i++) {
			Assert.assertEquals("char "+i, chars[i], s.charAt(i));
		}
	}
	
	@Test
	public void testAngstrom() {
		String aring = String.valueOf((char)0x00C5);
		Assert.assertTrue(Normalizer.isNormalized(aring, Normalizer.Form.NFKC));
		String angst = String.valueOf((char)0x212B);
		Assert.assertFalse(Normalizer.isNormalized(angst, Normalizer.Form.NFKC));
		Assert.assertFalse(aring.equals(angst));
		String angstNorm = Normalizer.normalize(angst, Normalizer.Form.NFKC);
		Assert.assertTrue(aring.equals(angstNorm));
	}
}
