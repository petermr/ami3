package org.contentmine.ami.plugins.phylotree.nexml;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.phylotree.nexml.NWKTree;
import org.contentmine.ami.plugins.phylotree.nexml.NewickFactory;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Test;

import nu.xom.Element;


public class NewickTest {

	private static final Logger LOG = Logger.getLogger(NewickTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	public final static String NEWICK = ""
			+ "((Pyramidobacter_piscolens:293,Thermotoga_maritime:349):11,(Synechocoocus_elongatus:350,((Chloroflexus_aurantiacus:363,"
			+ "(((((Pseudomonas_aeruginosa:238,(Escherichia_coli:178,Haemophilus_influenzae:214):53):37,(Neisseria_gonorrhoeae:247,"
			+ "Bordetella_pertussis:247):28):53,(Ehrlichia_chaffeensis:324,(Caulobacter_crescentus:259,Ochrobactrum_anthropi:246):58):40):16,"
			+ "(Treponema_denticola:393,(Opitutus_terrae:343,Chlamydia_trachomatis:440):54):13):8,((Chlorobium_tepidum:317,"
			+ "(Porphyromonas_gingivalis:182,Bacteroides_fragilis:186):176):52,Fusobacterium_nucleatum:315):10):2,"
			+ "((Bacillus_subtilis:261,(Lactobacillus_salivarius:167,Streptococcus_gordonii:211):89):52,Finegoldia_magna:303):44):12,"
			+ "Rhodopirellula_baltica:409):18):7);";
	
	public final static String NEWICK00 = ""
			+ "(A:19,B:25)C:53;";
			
	public final static String NEWICK0 = ""
			+ "((A:12,(B:34,C:56):53)D:11);";
	
	public final static String NEWICK1 = ""
		+ "((A:100,(B:178,C:214):53):11);";
	
	public final static String NEWICK2 = ""
	+ "((A,(B,C),(D,E))F);";
			
	@Test
	public void testReadNewick00() {
		NewickFactory factory = new NewickFactory();
		NWKTree tree = factory.readNewick(NEWICK00);
		LOG.trace("newick: "+tree.createNewick());
	}
	
	@Test
	public void testReadNewick0() {
		NewickFactory factory = new NewickFactory();
		NWKTree tree = factory.readNewick(NEWICK0);
		LOG.trace("newick: "+tree.createNewick());
	}
	
	@Test
	public void testReadNewick2() {
		NewickFactory factory = new NewickFactory();
		NWKTree tree = factory.readNewick(NEWICK2);
		LOG.trace("newick: "+tree.createNewick());
	}
	
	@Test
	public void testReadNewick() throws IOException {
		NewickFactory factory = new NewickFactory();
		NWKTree tree = factory.readNewick(NEWICK);
		LOG.trace("newick: "+tree.toString());
		LOG.trace("newick: "+tree.createNewick());
		Element xml = tree.createXML();
		XMLUtil.debug(xml, new File("target/phylo/testxml.xml"), 1);
		SVGG g = tree.createSVG();
		LOG.trace("newick: "+g.toXML());
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/phylo/testsvg.svg"));
	}
}
