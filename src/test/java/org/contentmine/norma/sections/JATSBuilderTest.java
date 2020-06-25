package org.contentmine.norma.sections;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.AbstractMetadata.HtmlMetadataScheme;
import org.contentmine.cproject.metadata.html.HtmlMD;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlMeta;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** builds JATSElements from components or legacy
 * 
 * @author pm286
 *
 */
public class JATSBuilderTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(JATSBuilderTest.class);
private static File TESTSEARCH4 = new File(AbstractAMITest.SRC_TEST_DOWNLOAD, "testsearch4");
	private static File TESTSEARCH50 = new File(AbstractAMITest.SRC_TEST_DOWNLOAD, "testsearch50");
	private static File T_903427 = new File(TESTSEARCH4, "10_1101_2020_01_12_903427v1");
	
	@Test
	public void testExtractMetas() {
		File file = new File(T_903427, "landingPage.html");
		HtmlElement htmlElement = HtmlElement.create(file);
		List<HtmlMeta> metaList = HtmlMeta.extractMetas(htmlElement, HtmlMeta.HEAD_META_XPATH);
		Assert.assertEquals("meta", 111, metaList.size());
		long dc =  
				metaList.stream()
					.filter(m -> m.getName() != null && m.getName().startsWith("DC."))
					.peek(m -> System.out.println(m.getName()+" = "+m.getContent()))
					.count();
			Assert.assertEquals("dc", 11, dc);
		long hw =  
				metaList.stream()
					.filter(m -> m.getName() != null && m.getName().startsWith("citation_"))
					.peek(m -> System.out.println(m.getName()+" = "+m.getContent()))
					.count();
			Assert.assertEquals("hw", 78, hw);
		long other =  
				metaList.stream()
					.filter(m -> m.getName() != null &&
							!m.getName().startsWith("citation_") &&
							!m.getName().startsWith("DC"))
					.peek(m -> System.out.println(m.getName()+" = "+m.getContent()))
					.count();
			Assert.assertEquals("other", 21, other);
		long empty =  
				metaList.stream()
					.filter(m -> m.getName() == null)
					.peek(m -> System.out.println(m.getName()+" = "+m.getContent()))
					.count();
			Assert.assertEquals("empty", 1, empty);
	}

	@Test
	public void testExtractMetaLists() {
		List<HtmlMeta> metaList = HtmlMeta.createMetaList(new File(T_903427, "landingPage.html"));
		HtmlMetaJATSBuilder jatsBuilder = (HtmlMetaJATSBuilder) JATSBuilderFactory.createJATSBuilder(JATSBuilder.BuilderType.HTML);
		Map<HtmlMetadataScheme, List<HtmlMeta>> map = jatsBuilder.readMetaListsByMetadataScheme(metaList);
		Assert.assertEquals(80, map.get(HtmlMetadataScheme.HW).size());
		Assert.assertEquals(11, map.get(HtmlMetadataScheme.DC).size());

	}

	@Test
	// broken in refactoring
	@Ignore
	public void testProcessMeta() {
		List<HtmlMeta> metaList = HtmlMeta.createMetaList(new File(T_903427, "landingPage.html"));
		HtmlMetaJATSBuilder jatsBuilder = (HtmlMetaJATSBuilder) JATSBuilderFactory.createJATSBuilder(JATSBuilder.BuilderType.HTML);
		Map<HtmlMetadataScheme, List<HtmlMeta>> map = jatsBuilder.readMetaListsByMetadataScheme(metaList);
//		JATSArticleElement article = jatsBuilder.processHWList();
//		Assert.assertEquals("descendants", 99, XMLUtil.getQueryElements(article, "//*"));
//		XMLUtil.debug(article);

	}

	@Test
	public void testProcessMetaLists() {
		HtmlMetaJATSBuilder jatsBuilder = (HtmlMetaJATSBuilder) JATSBuilderFactory.createJATSBuilder(JATSBuilder.BuilderType.HTML);
		jatsBuilder.setCProject(new CProject(TESTSEARCH4));
		jatsBuilder.setOutputLandingMetadata(true);
		jatsBuilder.extractMetadataFromCProject(new HtmlMD(), HtmlMetadataScheme.HW);

	}

	@Test
	public void testProcessMetaListsLarge() {
		HtmlMetaJATSBuilder jatsBuilder = (HtmlMetaJATSBuilder) JATSBuilderFactory.createJATSBuilder(JATSBuilder.BuilderType.HTML);
		jatsBuilder.setCProject(new CProject(TESTSEARCH50));
		jatsBuilder.extractMetadataFromCProject(new HtmlMD());

	}


}
