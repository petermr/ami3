package org.contentmine.ami.plugins.search;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMIPluginOption;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.ResourceLocation;
import org.contentmine.cproject.util.CellRenderer;

public class SearchPluginOption extends AMIPluginOption {

	private static final Logger LOG = LogManager.getLogger(SearchPluginOption.class);
public static final String TAG = "search";
	private String searchDictionary;
	private String dictionary;

	public SearchPluginOption() {
		super(TAG);
	}

	public SearchPluginOption(List<String> options) {
		super(TAG, options);
	}

	public void run() {
		StringBuilder commandStringBuilder = createCoreCommandStringBuilder();
		searchDictionary = optionString;
		if (searchDictionary == null) {
			LOG.warn("no dictionary given); no search");
			return;
		}
		commandStringBuilder.append(" --sr.search");		
		commandStringBuilder.append(" "+createSearchDictionaryResourceString(searchDictionary));
		plugin = "search";
		LOG.debug("SEARCH "+commandStringBuilder);
		String commandString = commandStringBuilder.toString();
		SearchArgProcessor searchArgProcessor = new SearchArgProcessor(commandString);
		searchArgProcessor.runAndOutput();
	}
	
	/**
	 * 		StringBuilder commandStringBuilder = createCoreCommandStringBuilder();
		commandStringBuilder.append(" --w.words "+optionString);
		String sw = getOptionFlagString("w.stopwords", " ");
		commandStringBuilder.append(sw);
		LOG.debug("WORD "+commandStringBuilder);
		//LOG.warn("WS: "+projectDir+"  ");
		String commandString = commandStringBuilder.toString();
		WordArgProcessor argProcessor = new WordArgProcessor(commandString);
		argProcessor.setDebug(true);
		argProcessor.runAndOutput();
//		LOG.warn("running command second time? "+commandString);
//		new WordArgProcessor(commandString).runAndOutput();
		return;

	 */

	/** just letters and numbers? expand to resourceString
	 * org/contentmine/ami/plugins/dictionary/country.xml
	 */
	public static String createSearchDictionaryResourceString(String dictionary) {
		if (dictionary != null && dictionary.toLowerCase().replaceAll("[a-z0-9]", "").length() == 0) {
			dictionary = AMIArgProcessor.DICTIONARY_RESOURCE+"/"+dictionary+".xml";
		}
		return dictionary;
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

	/** create option from search dictionary.
	 * split at "/" , take last field and first name before "."
	 * Maybe FilenameUtils would do this?
	 */
	@Override
	protected String getOption(String option) {
		if (searchDictionary != null && !searchDictionary.trim().equals("")) {
			String[] ss = searchDictionary.split("/");
			String sss = ss[ss.length-1];
			option = sss.split("\\.")[0];
		}
		return option;
	}

	protected void runMatchSummaryAndCount(String option) {
		if (dictionary == null) {
			resultXPathAttribute = "@word";
			super.runMatchSummaryAndCount(option);
		} else {
			String cmd = "--project "+projectDir+" -i "+createSnippetsFilename(dictionary)+"  "
					+ "--xpath //result/"+resultXPathAttribute+" --summaryfile "+createCountFilename(dictionary);
			DefaultArgProcessor.CM_LOG.debug("runMatchSummaryAndCount: "+cmd);
			new DefaultArgProcessor(cmd).runAndOutput();
		}
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
