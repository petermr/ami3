package org.contentmine.pdf2svg.codepoints.defacto;

import org.contentmine.CHESConstants;
import org.contentmine.font.CodePointSet;
import org.junit.Assert;
import org.junit.Test;

/** tests contents of codePointSet
 * 
 * @author pm286
 *
 */
public class MathPi1Test {

	private static final String MATHPI1_XML = "mathpi1.xml";

	@Test
	public void testReadCodePointSet() {
		CodePointSet codePointSet = CodePointSet.readCodePointSet(CHESConstants.ORG_CM_PDF2SVG + "/codepoints/defacto/mathpi1.xml");
		Assert.assertNotNull(MATHPI1_XML, codePointSet);
		Assert.assertTrue(MATHPI1_XML+" "+codePointSet.size(), codePointSet.size() > 10 && codePointSet.size() < 300);
	}
	
}
