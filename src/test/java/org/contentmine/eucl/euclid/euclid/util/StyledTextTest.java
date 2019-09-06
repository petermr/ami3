package org.contentmine.eucl.euclid.euclid.util;

import org.contentmine.eucl.euclid.util.StyledText;
import org.contentmine.eucl.euclid.util.StyledText.SysoutStyle;
import org.junit.Test;

public class StyledTextTest {
	
	@Test
	public void testStyledText() {
		System.out.println("This is styled: "+StyledText.wrapBold("bold text")+"; followed by normal");
		System.out.println("This is styled: "+StyledText.wrap("underline", SysoutStyle.UNDERLINE)+"; followed by normal");
		System.out.println("This is styled: "+StyledText.wrap("blue", SysoutStyle.BLUE)+"; followed by normal");
//		System.out.println("This is styled: "+StyledText.wrap(StyledText.wrap("underline blue", SysoutStyle.BLUE), SysoutStyle.UNDERLINE)+"; followed by normal");
	}

}
