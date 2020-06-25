package org.contentmine.ami.plugins.species;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPluginOption;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.util.CellRenderer;

public class SpeciesPluginOption extends AMIPluginOption {

	public static final String TAG = "species";
	private static final Logger LOG = LogManager.getLogger(SpeciesPluginOption.class);
public SpeciesPluginOption() {
		super(TAG);
	}

	public SpeciesPluginOption(List<String> options) {
		super(TAG, options);
	}

//	public SpeciesPluginOption(List<String> options, List<String> flags) {
//		super(TAG, options, flags);
//	}
//
	public void run() {
		String cmd = "--project "+projectDir+" -i scholarly.html --sp.species --sp.type "+optionString;
		DefaultArgProcessor.CM_LOG.debug(cmd);
//		System.out.print("S: "+cmd);
		System.out.print("SP: "+projectDir);
		new SpeciesArgProcessor(cmd).runAndOutput();
	}

	protected void runMatchSummaryAndCount(String option) {
		resultXPathAttribute = "@match";
		super.runMatchSummaryAndCount(option);
	}

	@Override
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = super.getNewCellRenderer();
		cellRenderer.setHref0(AMIPluginOption.WIKIPEDIA_HREF0);
		cellRenderer.setHref1(AMIPluginOption.WIKIPEDIA_HREF1);
		cellRenderer.setUseHrefWords(2, "_");
		return cellRenderer;
	}

}
