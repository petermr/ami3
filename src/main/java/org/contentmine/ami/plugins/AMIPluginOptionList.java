package org.contentmine.ami.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.gene.GenePluginOption;
import org.contentmine.ami.plugins.regex.RegexPluginOption;
import org.contentmine.ami.plugins.search.SearchPluginOption;
import org.contentmine.ami.plugins.sequence.SequencePluginOption;
import org.contentmine.ami.plugins.species.SpeciesPluginOption;
import org.contentmine.ami.plugins.word.WordPluginOption;

public class AMIPluginOptionList {

	private static final Logger LOG = LogManager.getLogger(AMIPluginOptionList.class);
public List<AMIPluginOption> pluginOptionList;
	public final static AMIPluginOption GENE = new GenePluginOption();
	public final static AMIPluginOption REGEX = new RegexPluginOption();
	public final static AMIPluginOption SEARCH = new SearchPluginOption();
	public final static AMIPluginOption SEQUENCE = new SequencePluginOption();
	public final static AMIPluginOption SPECIES = new SpeciesPluginOption();
	public final static AMIPluginOption WORD = new WordPluginOption();
		
	
	public AMIPluginOptionList() {
		init();
	}

	private void init() {
		pluginOptionList = new ArrayList<AMIPluginOption>();
		pluginOptionList.add(GENE);
		pluginOptionList.add(REGEX);
		pluginOptionList.add(SEARCH);
		pluginOptionList.add(SEQUENCE);
		pluginOptionList.add(SPECIES);
		pluginOptionList.add(WORD);
	}

	public AMIPluginOption getPluginOption(String pluginOptionName) {
		for (AMIPluginOption pluginOption : pluginOptionList) {
			// messy - we just want the first name; this should be a PluginOption, not its name
			if (pluginOption.matches(pluginOptionName)) {
				return pluginOption;
			}
		}
		return null;
	}
}
