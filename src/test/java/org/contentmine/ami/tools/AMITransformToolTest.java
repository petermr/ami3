package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.util.JsoupWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class AMITransformToolTest {
	private static final Logger LOG = Logger.getLogger(AMITransformToolTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** runs NormaTransformer
	 * 
	 */
	@Test
	public void testZikaScholarlyHtml() {
		File targetDir = new File("target/cooccurrence/zika10");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		String args = 
				"-p /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10/"
			;
		new AMITransformTool().runCommands(args);
	}
	
	@Test
	public void testTidy() {
		Document doc = null;
		try {
			doc = Jsoup.connect("https://en.wikipedia.org/").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.debug(doc.title());
		System.out.println("D"+doc.title());
		Elements newsHeadlines = doc.select("#mp-itn b a");
		for (Element headline : newsHeadlines) {
		  LOG.debug("%s\n\t%s"+ 
		    headline.attr("title")+ headline.absUrl("href"));
		}
	}
	
	@Test
	public void testTidyTool() {
		String command = ""
				+ "-p target"
				+ " --tidy jsoup"
				+ " --input https://en.wikipedia.org/"
				;
		new AMITransformTool().runCommands(command);
	}
	
	@Test
//	@Ignore // FILE NOT FOUND
	public void testTidyToolFile() throws IOException {
		String command = ""
				+ "-p target"
				+ " --tidy jsoup"
				+ " --input src/test/resoures/org/contentmine/ami/tools/download/scielo/resultSet1.html"
				;
		File file = new File("src/test/resoures/org/contentmine/ami/tools/download/scielo/resultSet1.html");
		Assert.assertTrue("file exists", file.exists());
		Document jdoc = Jsoup.parse(file, CMineUtil.UTF_8.toString());
//		System.out.println("DOC>"+JsoupWrapper.jSoupToXOM(jdoc).toXML());
		nu.xom.Document xDoc = JsoupWrapper.jSoupToXOM(jdoc);
		System.out.println("DOC>"+xDoc.toXML());
		XMLUtil.writeQuietly(xDoc.getRootElement(), new File("target/jsoup/test.xml"), 1);
//		new AMITransformTool().runCommands(command);
	}
	
}
