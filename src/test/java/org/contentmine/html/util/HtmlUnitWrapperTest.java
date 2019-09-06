package org.contentmine.html.util;

import java.io.FileOutputStream;
import java.net.URL;

import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.util.HtmlUnitWrapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Nodes;

public class HtmlUnitWrapperTest {

	@Test
	@Ignore // uses web resources
	public void testHtmlUnitBMC() throws Exception {
		URL url = new URL("http://www.biomedcentral.com/1471-2229/14/106");
		HtmlUnitWrapper htmlUnitWrapper = new HtmlUnitWrapper();
		HtmlElement htmlElement = htmlUnitWrapper.readAndCreateElement(url);
		Assert.assertNotNull("read element", htmlElement);
		Nodes nodes = htmlElement.query( 
				"//*["
				+ "local-name()='script'"
				+ " or local-name()='link'"
				+ " or local-name()='object'"
				+ " or local-name()='iframe' "
				+ " or local-name()='fieldset' "
				+ " or local-name()='button' "
				+ " or local-name()='style' "
				+ " or @class='mobile-hidden' "
				+ " or @id='left-article-box' "
				+ " or @id='branding' "
				+ "] "
				+ "| //comment()"
				+ "");
		for (int i = nodes.size()-1; i >= 0; i--) {
			nodes.get(i).detach();
		}
		XMLUtil.debug(htmlElement, new FileOutputStream("target/htmlunit.html"), 1);
	}

	@Test
	/** try to use at headless browser on Elsevier - doesn't seem to work.
	 * 
	 * @throws Exception
	 */
	@Ignore
	public void testHtmlUnitElsevier() throws Exception {
		URL url = new URL("	http://www.sciencedirect.com/science/article/pii/S1055790314003923");
		HtmlUnitWrapper htmlUnitWrapper = new HtmlUnitWrapper();
		HtmlElement htmlElement = htmlUnitWrapper.readAndCreateElement(url);
		Assert.assertNotNull("read element", htmlElement);
		Nodes nodes = htmlElement.query( 
				"//*["
				+ "local-name()='script'"
//				+ " or local-name()='link'"
//				+ " or local-name()='object'"
//				+ " or local-name()='iframe' "
//				+ " or local-name()='fieldset' "
//				+ " or local-name()='button' "
//				+ " or local-name()='style' "
//				+ " or @class='mobile-hidden' "
//				+ " or @id='left-article-box' "
//				+ " or @id='branding' "
				+ "] "
				+ "| //comment()"
				+ "");
		for (int i = nodes.size()-1; i >= 0; i--) {
			nodes.get(i).detach();
		}
		XMLUtil.debug(htmlElement, new FileOutputStream("target/htmlunitEls.html"), 1);
	}
	
	@Test
	/** try to use at headless browser on Elsevier - doesn't seem to work.
	 * 
	 * @throws Exception
	 */
	@Ignore
	public void testHtmlUnitElsevierNY() throws Exception {
		URL url = new URL("	http://www.sciencedirect.com/science/article/pii/S1055790314003923?np=y");
		HtmlUnitWrapper htmlUnitWrapper = new HtmlUnitWrapper();
		HtmlElement htmlElement = htmlUnitWrapper.readAndCreateElement(url);
		Assert.assertNotNull("read element", htmlElement);
		Nodes nodes = htmlElement.query( 
				"//*["
				+ "local-name()='script'"
//				+ " or local-name()='link'"
//				+ " or local-name()='object'"
//				+ " or local-name()='iframe' "
//				+ " or local-name()='fieldset' "
//				+ " or local-name()='button' "
//				+ " or local-name()='style' "
//				+ " or @class='mobile-hidden' "
//				+ " or @id='left-article-box' "
//				+ " or @id='branding' "
				+ "] "
				+ "| //comment()"
				+ "");
		for (int i = nodes.size()-1; i >= 0; i--) {
			nodes.get(i).detach();
		}
		XMLUtil.debug(htmlElement, new FileOutputStream("target/htmlunitEls.html"), 1);
	}
}

