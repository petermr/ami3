/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contentmine.font;

import org.contentmine.eucl.euclid.Util;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Builder;
import nu.xom.Element;

public class CodePointTest {

	@Test
	public void testGetCodePointAttributes() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						CodePointSet.UNICODE_POINT_SET_XML, this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByUnicodeValue("U+039F");
		Assert.assertNotNull(codePoint);
///		Assert.assertEquals((int)927, (int)codePoint.getDecimal());
		Assert.assertEquals("U+039F", codePoint.getUnicodePoint().getUnicodeValue());
		Assert.assertEquals("GREEK CAPITAL LETTER OMICRON", codePoint.getUnicodePoint().getUnicodeName());
	}
	
	@Test
	public void testDummy() {
		
	}
}
