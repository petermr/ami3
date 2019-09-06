package org.contentmine.font;

import org.junit.Assert;
import org.junit.Test;

public class UnicodePointTest {

	@Test
	public void dummy() {
		
	}
	
	@Test
	public void testCreateSurrogatePair() {
		int codePoint = 0x1D538;
		Assert.assertEquals("integer", 120120, codePoint);
		int[] surrogatePair = UnicodePoint.makeSurrogatePair(codePoint);
		Assert.assertTrue("valid0", UnicodePoint.isValidSurrogate(0, surrogatePair[0]));
		Assert.assertTrue("valid1", UnicodePoint.isValidSurrogate(1, surrogatePair[1]));
		Assert.assertEquals("high", 0XD835, surrogatePair[0]);
		Assert.assertEquals("low", 0XDD38, surrogatePair[1]);
	}
	
	@Test
	public void testCreateCodePoint() {
		int[] surrogatePair = {0XD835, 0XDD38};
		Integer codePoint = UnicodePoint.createCodePointFromSurrogates(surrogatePair);
		Assert.assertEquals("codePoint", 120120, (int)codePoint);
	}
	
}
