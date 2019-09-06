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

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.pdf2svg.PDF2SVGConverter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** this is a mess and needs refactoring
 * 
 * @author pm286
 *
 */
public class PDF2SVGConverterIT {

	public static final String PAGE6_PDF = "src/test/resources"+"/org/contentmine/pdf2svg/"+"page6.pdf";
	public final static Logger LOG = Logger.getLogger(PDF2SVGConverterIT.class);

	@Test
	@Ignore
	public void testUsage() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run();
	}

	@Test
	public void testBasenameOutdir() {
		File dir = new File("target", "page6");
		File file = new File("target/page6", "page6-page1.svg");

		dir.delete();
		file.delete();

		Assert.assertTrue("page6.pdf exists", new File(PAGE6_PDF).exists());
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target", "-mkdir", PAGE6_PDF);

		Assert.assertTrue(dir.exists() && dir.isDirectory());
		Assert.assertTrue(file.exists() && file.isFile());
	}

	@Test
	public void testSimpleRun() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/ajc", PAGE6_PDF);
	}


	@Test
	//@Ignore
	public void testPage6() {
		File page6File = new File("target/ajc/page6-page1.svg"); // yes, this serial number is what is is output as
		if (page6File.exists()) {
			page6File.delete();
		}
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/ajc", "-pages", "1", "-storesvg",
				PAGE6_PDF);
		// results have been written to target
//		Assert.assertTrue(page6File.exists());
		Assert.assertEquals("Page count", 1, converter.getPageList().size());
		SVGSVG svgPage = converter.getPageList().get(0);
		List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(
				svgPage, "//svg:text"));
		int nTexts = texts.size();
		Assert.assertTrue("count: (" + nTexts + ")", nTexts > 4090
				&& nTexts < 4100);
		List<SVGPath> paths = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(
				svgPage, "//svg:path"));
		int nPaths = paths.size();
		Assert.assertTrue("count: (" + nPaths + ")", nPaths > 195
				&& nPaths < 210);
	}

	@Test
	public void testWord() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/word/", "src/test/resources/word/test.pdf");
	}

	@Test
	public void testWordMath() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/word/", "src/test/resources/word/testmath.pdf");
	}

	@Test
	public void testWordMath1() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/word/", "src/test/resources/word/testmath1.pdf");
	}


	@Test
	@Ignore
	// FIXME - move to test/resources
	public void testPPT() {
		SemiTestIT.convertPDFsToSVG("../pdfs/ppt", "target/ppt");
	}

	@Test
	@Ignore
	public void testHelp() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run();
	}
	
	@Test
	@Ignore
	public void testImages() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/image/", "src/test/resources/org/contentmine/pdf2svg/misc/BMCBioinfGood2009.pdf");
	}

	@Test
	@Ignore
	public void testLarge() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/walnut/", "../pdfs/misc/walnutCreek.pdf");
	}

	@Test
	@Ignore // too slow
	public void testMultiStrokeGraphics() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/suspect/", "src/test/resources/graphicspaths/suspect.pdf");
	}

}
