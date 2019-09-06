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
package org.contentmine.pdf2svg;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

public class SVGSerializerTest {

	private final static Logger LOG = Logger.getLogger(SVGSerializerTest.class);
	
	private final static String SCRIPT_O = new String(Character.toChars(120030));
	@Test
	public void testSerializer() {
		Element element = new Element("myString");
		Document doc = new Document(element);
		String content = "char 945 is "+(char)945+" i.e. alpha ";
		element.appendChild(content);
		
		try {
			FileOutputStream os = new FileOutputStream("target/test.svg");
			SVGSerializer serializer = new SVGSerializer(os);
			serializer.write(doc);
		} catch (Exception e) {
			throw new RuntimeException("cannot serialize ", e);
		}
	}
	
	@Test
	public void testSurrogatePairDefault() throws IOException {
		int codePoint = 120030;
		StringBuffer sb = new StringBuffer();
		sb.append("a");
		sb.appendCodePoint(codePoint);
		sb.append("b");
		String s = sb.toString();
		Element e = new Element("t");
		e.appendChild(s);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Serializer serializer = new Serializer(baos);
		serializer.write(new Document(e));
		String sout = baos.toString();
		// no idea where the \r comes from
		String ss = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<t>a"+SCRIPT_O+"b</t>\n";
		// this behaves differently on different OS's Windows has \r\n
//		sout = sout.replaceAll("\\r", "");
//		Assert.assertEquals("xmlstring", 51, ss.length());
//		Assert.assertEquals(sout.length(), ss.length());
//		Assert.assertEquals('a', sout.charAt(42));
//		Assert.assertEquals('b', sout.charAt(45));
//		for (int i = 0; i < 43; i++) {
//			Assert.assertEquals("char "+i, sout.charAt(i), ss.charAt(i));
//		}
//		for (int i = 46; i < 51; i++) {
//			Assert.assertEquals("char "+i, sout.charAt(i), ss.charAt(i));
//		}
//		Assert.assertEquals("code", 120030, ss.codePointAt(43));
//		Assert.assertEquals("code", 120030, sout.codePointAt(43));
		
	}
	
	@Test
	public void testSurrogatePairSerializer() throws IOException {
		int codePoint = 120030;
		StringBuffer sb = new StringBuffer();
		sb.append("a");
		sb.appendCodePoint(codePoint);
		sb.append("b");
		String s = sb.toString();
		Element e = new Element("test");
		e.appendChild(s);
		Serializer serializer = new SVGSerializer(new FileOutputStream("target/surrogateSvg.xml"));
		serializer.setIndent(1);
		serializer.write(new Document(e));
	}
	
	@Test
	public void testSurrogates() throws IOException {
		// creating a string
		StringBuffer sb = new StringBuffer();
		sb.append("a");
		sb.appendCodePoint(120030);
		sb.append("b");
		String s = sb.toString();
		// iterating over string
		int codePointCount = s.codePointCount(0, s.length());
		Assert.assertEquals(3, codePointCount);
		int charIndex = 0;
		for (int i = 0; i < codePointCount; i++) {
			int codepoint = s.codePointAt(charIndex);
			int charCount = Character.charCount(codepoint);
			charIndex += charCount;
		}
	}
}
