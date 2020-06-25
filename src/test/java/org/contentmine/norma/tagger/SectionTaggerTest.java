package org.contentmine.norma.tagger;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaArgProcessor;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.tagger.SectionTaggerX;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Test;

/** Taggers not yet written
 * 
 * @author pm286
 *
 */
public class SectionTaggerTest {

	private static final Logger LOG = LogManager.getLogger(SectionTaggerTest.class);
/** iterates over two taggers.
	 * FIXME don't know if this works
	 */
	@Test
	public void testSectionTagger() {
		Norma norma = new Norma();
		norma.run("-i "+NormaFixtures.F0113556_XML+" -o target/tagger/f0113556 --ctree ");
		
		String cTree = "target/tagger/f0113556/src_test_resources_org_contentmine_norma_pubstyle_plosone_journal_pone_0113556_fulltext_xml";
		String cmd = "-i fulltext.xml --ctree "+cTree+" -o scholarly.html --transform nlm2html --tag foo bar";
		norma = new Norma();
		norma.run(cmd);
		List<SectionTaggerX> taggers = ((NormaArgProcessor)norma.getArgProcessor()).getSectionTaggers();
		Assert.assertEquals("taggers", 2, taggers.size());
		LOG.debug(taggers.get(0));
		LOG.debug(taggers.get(1));
	}
}

