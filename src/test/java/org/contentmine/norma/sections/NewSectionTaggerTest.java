package org.contentmine.norma.sections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.sections.JATSSectionTagger.SectionTag;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

public class NewSectionTaggerTest {

	public static final Logger LOG = Logger.getLogger(NewSectionTaggerTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	

	@Test
	public void testReadSectionTags() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		String defaultSectionTaggerResourceName = JATSSectionTagger.DEFAULT_SECTION_TAGGER_RESOURCE;
		Assert.assertEquals("/org/contentmine/norma/pubstyle/sectionTagger.xml",  defaultSectionTaggerResourceName);
		Element element = tagger.readSectionTags(defaultSectionTaggerResourceName);
		Assert.assertNotNull(element);
		Assert.assertEquals("child", 26, XMLUtil.getQueryElements(element, "./*[local-name()='tag']").size());
		
	}
	
	/** does the same as above? */
	@Test
	public void testReadDefaultSectionTags() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		Element element = tagger.readSectionTags();
		Assert.assertNotNull(element);
		List<Element> tags = XMLUtil.getQueryElements(element, "./tag");
		LOG.debug(element.toXML());
		Assert.assertTrue("tags "+tags.size(), tags.size() > 22);
	}

	@Test
	public void testMakeTagMap() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		Map<SectionTag, TagElement> tagElementByTag = tagger.getOrCreateMap();
		Assert.assertTrue("tag set", tagElementByTag.keySet().contains(SectionTag.ABSTRACT));
		TagElement tagElement = tagElementByTag.get(SectionTag.ABSTRACT);
		Assert.assertNotNull(tagElement);
		Assert.assertEquals("id", "cm:ABSTRACT", tagElement.getId());
		tagElement = tagger.get(SectionTag.ABSTRACT);
		List<JATSSectionTagger.SectionTag> tags = tagger.getSortedTags();
		Assert.assertTrue("tags "+tags.size(), tags.size() > 22);
	}
	
	@Test
	public void testSearchTagMap() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		TagElement tagElement = tagger.getTagElement(SectionTag.ABSTRACT);
		Assert.assertNotNull(tagElement);
		Assert.assertEquals("id", "cm:ABSTRACT", tagElement.getId());
	}
	
	@Test
	public void testGetRegexListXpath() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		TagElement tagElement = tagger.getTagElement(SectionTag.ABSTRACT);
		List<String> regexList = tagElement.getRegexList();
//		Assert.assertEquals("[(abstract)]", regexList.toString());
		String xpath = tagElement.getXpath();
		Assert.assertEquals("//*[@class='abstract' and not(*[@abstract-type='summary'])]", xpath);
	}

	@Test
	public void testGetMajorSections() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		TagElement tagElement = tagger.getTagElement(SectionTag.ABSTRACT);
		String xpath = tagElement.getXpath();
		Assert.assertEquals("//*[@class='abstract' and not(*[@abstract-type='summary'])]", xpath);
	}
	

}
