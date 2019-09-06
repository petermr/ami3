package org.contentmine.norma.pubstyle.hindawi;

import org.contentmine.norma.InputFormat;
import org.contentmine.norma.RawInput;
import org.contentmine.norma.pubstyle.PubstyleReader;
import org.contentmine.norma.pubstyle.hindawi.HindawiReader;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class URLReaderTest {

	@Test
	@Ignore // uses URL
	public void testReadURL() throws Exception {
		String urlString = "http://www.hindawi.com/journals/ija/2014/507405/";
		PubstyleReader hindawiReader = new HindawiReader(InputFormat.HTML);
		hindawiReader.readURL(urlString);
		RawInput rawInput = hindawiReader.getRawInput();
		Assert.assertNotNull("raw input", rawInput);
		byte[] bytes = rawInput.getRawBytes();
		Assert.assertNotNull("read bytes", bytes);
		Assert.assertEquals("bytes read", 111681, bytes.length);
	}
	
}
