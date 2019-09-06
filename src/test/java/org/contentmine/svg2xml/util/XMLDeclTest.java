package org.contentmine.svg2xml.util;

import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.junit.Test;

import nu.xom.Builder;
import nu.xom.Element;

public class XMLDeclTest {
	
	private static final Logger LOG = Logger.getLogger(XMLDeclTest.class);

	@Test
	public void testXMLDeclarationInCDATA() throws Exception {
		Element e = new Builder().build(new FileInputStream("src/test/resources/org/contentmine/svg2xml/declaration.xml")).getRootElement();
	}

}
