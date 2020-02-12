package org.contentmine.ami.plugins.phylotree.nexml;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.graphics.svg.SVGG;

public class NewickFactory {

	private static final Logger LOG = Logger.getLogger(NewickFactory.class);
	private NWKTree nwkTree;
	static {
		LOG.setLevel(Level.DEBUG);
	}
	/**
		 * from Wikpedia 
	   Tree --> Subtree ";" | Branch ";"
	   Subtree --> Leaf | Internal
	   Leaf --> Name  
	   Internal --> "(" BranchSet ")" Name
	   BranchSet --> Branch | Branch "," BranchSet
	   Branch --> Subtree Length
	   Name --> empty | string // PMR string does not contain ":" or "," or (" or ")"
	   
	   	 * @param nwk
		((Pyramidobacter_piscolens:293,Thermotoga_maritime:349):11,(Synechocoocus_elongatus:350,((Chloroflexus_aurantiacus:363,
		(((((Pseudomonas_aeruginosa:238,(Escherichia_coli:178,Haemophilus_influenzae:214):53):37,(Neisseria_gonorrhoeae:247,
		Bordetella_pertussis:247):28):53,(Ehrlichia_chaffeensis:324,(Caulobacter_crescentus:259,Ochrobactrum_anthropi:246):58):40):16,
		(Treponema_denticola:393,(Opitutus_terrae:343,Chlamydia_trachomatis:440):54):13):8,((Chlorobium_tepidum:317,
		(Porphyromonas_gingivalis:182,Bacteroides_fragilis:186):176):52,Fusobacterium_nucleatum:315):10):2,
		((Bacillus_subtilis:261,(Lactobacillus_salivarius:167,Streptococcus_gordonii:211):89):52,Finegoldia_magna:303):44):12,
		Rhodopirellula_baltica:409):18):7);
		*/
	/**
	 * 
	 * @param nwk
	 * @return
	 */
		public NWKTree readNewick(String nwk) {
			nwkTree = null;
			if (nwk != null) {
				nwk = nwk.trim();
				if (!nwk.endsWith(";")) {
					throw new RuntimeException("nwk must end with ;");
				} else {
					StringBuilder sb = new StringBuilder(nwk);
					nwkTree = NWKTree.createTree(sb);
				}
			}
			return nwkTree;
		}
		
		public NWKTree readNewick(File nwkFile) throws IOException {
			nwkTree  = null;
			if (nwkFile != null) {
				String nwkString = FileUtils.readFileToString(nwkFile, CMineUtil.UTF8_CHARSET);
				nwkTree = readNewick(nwkString);
			}
			return nwkTree;
		}
		
		public SVGG createSVG() {
			SVGG g = null;
			if (nwkTree != null) {
				nwkTree.createXML();
				g = nwkTree.createSVG();
			}
			return g;
		}

}
