package org.contentmine.cproject.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import nu.xom.Builder;
import nu.xom.Document;

public class XMLUtils {

	public static Document parseWithoutDTD(InputStream is) {
		Document doc = null;
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Builder builder = new Builder(xmlReader);
			doc = builder.build(is);
		} catch (Exception  e) {
			throw new RuntimeException("Cannot parse XML", e);
		}
		return doc;
	}

	public static Document parseWithoutDTD(File xmlFile) {
		try {
			return XMLUtils.parseWithoutDTD(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot parse XML", e);
		}
	}
}
