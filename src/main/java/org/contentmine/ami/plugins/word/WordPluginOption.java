package org.contentmine.ami.plugins.word;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPluginOption;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.util.CellRenderer;

public class WordPluginOption extends AMIPluginOption {

	private static final Logger LOG = LogManager.getLogger(WordPluginOption.class);
public static final String TAG = "word";

	public WordPluginOption() {
		super(TAG);
	}

	public WordPluginOption(List<String> options) {
		super(TAG, options);
	}

	public void run() {
		StringBuilder commandStringBuilder = createCoreCommandStringBuilder();
		commandStringBuilder.append(" --w.words "+optionString);
		String sw = getOptionFlagString("w.stopwords", " ");
		commandStringBuilder.append(sw);
		LOG.trace("WORD "+commandStringBuilder);
		String commandString = commandStringBuilder.toString();
		WordArgProcessor argProcessor = new WordArgProcessor(commandString);
		argProcessor.setDebug(true);
		argProcessor.runAndOutput();
		return;
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

	
	@Override
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = super.getNewCellRenderer();
		cellRenderer.setHref0(AMIPluginOption.WIKIPEDIA_HREF0);
		cellRenderer.setHref1(AMIPluginOption.WIKIPEDIA_HREF1);
		cellRenderer.setUseHrefWords(1, "_");
		return cellRenderer;
	}


}
