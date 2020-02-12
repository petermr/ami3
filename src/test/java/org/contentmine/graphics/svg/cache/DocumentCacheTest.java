package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.junit.Test;

import junit.framework.Assert;

public class DocumentCacheTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(DocumentCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testMLDocumentCacheSingleColumn() {
		File treeDir = new File(PDF2SVG2, "test/Hariharan");
		if (!treeDir.exists()) {
			LOG.error("no corpus: "+treeDir);
		}
		CTree cTree = new CTree(treeDir);
		DocumentCache documentCache = new DocumentCache(cTree);
		List<File> svgFiles = documentCache.createTextBoxesAndWriteToSVGDir();
		Assert.assertEquals("svgfiles",  16, svgFiles.size());
		List<PageCache> pageCacheList = documentCache.getOrCreatePageCacheList();
		Assert.assertEquals("page cache",  16, pageCacheList.size());
		HtmlElement htmlElement = documentCache.getOrCreateConvertedHtmlElement();
		Assert.assertNotNull(htmlElement);
		HtmlHtml.wrapAndWriteAsHtml(htmlElement, new File(treeDir, "converted.html"));
//		svgFiles.forEach(f -> System.out::println);
//			SVGElement elem = SVGElement.readAndCreateSVG(f);
//			SVGUtil.getQuerySVGElements(elem, ".//*")
//			    .forEach(e -> filter(System.out.println(e));
//			Assert.assertTrue(elem.);
//		});

	}
	
	/** get boxes for different text styles
	 * 
	 */
	@Test
	public void testGetTextBoxes() {
		CTree cTree = new CTree(new File(PDF2SVG2, "test/lichtenburg19a"));
		LOG.debug(cTree.getDirectory());
		Assert.assertTrue("file "+cTree.getDirectory(), cTree.getDirectory().exists());
		DocumentCache documentCache = new DocumentCache(cTree);
		List<File> svgFiles = documentCache.createTextBoxesAndWriteToSVGDir();
		Assert.assertEquals("svgfiles",  10, svgFiles.size());
		svgFiles.stream()  
			.map(SVGElement::readAndCreateSVG)
			.filter(e -> 
				{ 
		        List<SVGText> selfAndDescendantTexts = SVGText.extractSelfAndDescendantTexts(e);
		        System.out.println("texts: " + selfAndDescendantTexts.size());
				return selfAndDescendantTexts.size() > 300;
				})
			.mapToInt(e -> {return e.toString().length();})
			.forEach(l -> System.out.println(l))
			;
	}



}
