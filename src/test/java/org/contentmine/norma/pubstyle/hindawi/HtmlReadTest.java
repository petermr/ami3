package org.contentmine.norma.pubstyle.hindawi;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.norma.InputFormat;
import org.contentmine.norma.pubstyle.PubstyleReader;
import org.contentmine.norma.pubstyle.hindawi.HindawiReader;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HtmlReadTest {

	@Test
	@Ignore // remote read
	public void readRawHtmlTest() throws Exception {
		String urlString = "http://www.hindawi.com/journals/ija/2014/507405/";
		PubstyleReader hindawiReader = new HindawiReader(InputFormat.HTML);
		hindawiReader.readURL(urlString);
		HtmlElement rawHtml = hindawiReader.getOrCreateXHtmlFromRawHtml();
		Assert.assertNotNull("raw input", rawHtml);
		File file = new File("target/htmlsvg/507405.xml");
		FileUtils.touch(file);
		XMLUtil.debug(rawHtml, new FileOutputStream(file), 0);
		long size = FileUtils.sizeOf(file);
		Assert.assertTrue("size "+size, /*(207900 < size) && */(size < 207940));
	}
}
