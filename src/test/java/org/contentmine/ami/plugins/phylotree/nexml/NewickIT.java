package org.contentmine.ami.plugins.phylotree.nexml;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.phylotree.nexml.NWKTree;
import org.contentmine.ami.plugins.phylotree.nexml.NewickFactory;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

@Ignore("long")
public class NewickIT {
	private static final Logger LOG = LogManager.getLogger(NewickIT.class);
@Test
	public void testReadLargeTree() throws IOException {
		NewickFactory factory = new NewickFactory();
		NWKTree tree = factory.readNewick(new File(AMIFixtures.TEST_PHYLO_DIR, "supertree-analysis/strict1.tre"));
		LOG.trace("newick: "+tree.createNewick());
		Element xml = tree.createXML();
		XMLUtil.debug(xml, new File("target/phylo/strict1.xml"), 1);
		SVGG g = tree.createSVG();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/phylo/strict1.svg"));
	}

}
