package org.contentmine.ami.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.gene.GenePluginOption;
import org.contentmine.ami.plugins.regex.RegexPluginOption;
import org.contentmine.ami.plugins.search.SearchPluginOption;
import org.contentmine.ami.plugins.sequence.SequencePluginOption;
import org.contentmine.ami.plugins.species.SpeciesPluginOption;
import org.contentmine.ami.plugins.word.WordPluginOption;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.OptionFlag;
import org.contentmine.cproject.files.PluginOption;
import org.contentmine.cproject.util.CellRenderer;

public abstract class AMIPluginOption extends PluginOption {

private static final String EXACT_XPATH_ATTRIBUTE = "@exact";

private static final String RESULT_XPATH_ROOT = "//result";

	//	private static final String WORD = "word";
//	private static final String SPECIES = "species";
//	private static final String SEQUENCE = "sequence";
	private static final Logger LOG = LogManager.getLogger(AMIPluginOption.class);
public static Pattern COMMAND = Pattern.compile("(.*)\\((.*)\\)(.*)");
	
	public final static List<String> COMMANDS = Arrays.asList( new String[] {
	GenePluginOption.TAG,
//	IdentifierPluginOption.TAG,
	RegexPluginOption.TAG,
	SequencePluginOption.TAG,
	SpeciesPluginOption.TAG,
	WordPluginOption.TAG,
	});

	public final static String WIKIPEDIA_HREF0 = "http://en.wikipedia.org/wiki/";
	public final static String WIKIPEDIA_HREF1 = "";
	
	protected AMIPluginOption(String tag) {
		this.plugin = tag;
	}
	
	public AMIPluginOption(String plugin, List<String> options) {
		this(plugin);
		this.options = options;
		this.optionString = String.join(" ", options);
		LOG.trace("optionString: "+optionString);
		this.resultXPathBase = RESULT_XPATH_ROOT;
		this.resultXPathAttribute = EXACT_XPATH_ATTRIBUTE;

	}

	/** this is where the subclassing is created.
	 * E.G. creates WordPluginOption, SequencePluginOption
	 * 
	 * */
	public static AMIPluginOption createPluginOption(String cmd) {
		Matcher matcher = COMMAND.matcher(cmd);
//		LOG.debug("cmd: "+cmd);
		if (cmd == null || cmd.trim().equals("")) {
			throw new RuntimeException("Null/empty command");
		} else if (!matcher.matches()) {
			throw new RuntimeException("Command found: "+cmd+" must fit: "+matcher+""
					+ "...  plugin(option1[,option2...])[_flag1[_flag2...]]");
		}
		String optionTag = matcher.group(1);
		List<String> options = Arrays.asList(matcher.group(2).split(","));
		String flagString = matcher.group(3);
		flagString = flagString.replaceAll("_",  " ");
		List<String>flags = Arrays.asList(flagString.split("~"));
		List<OptionFlag> optionFlags = OptionFlag.createOptionFlags(flags);
		
		AMIPluginOption pluginOption = createPluginOption(optionTag, options, optionFlags);
		return pluginOption;
	}

	public static AMIPluginOption createPluginOption(String optionTag, List<String> subOptions, List<OptionFlag> optionFlags) {
		LOG.trace("OPTION: "+optionTag+" ... " +" subOptions: "+subOptions+ "\n option flags: "+optionFlags);
		AMIPluginOption pluginOption = null;
		if (false) {
		} else if (optionTag.equals(GenePluginOption.TAG)) {
			pluginOption = new GenePluginOption(subOptions);
		} else if (optionTag.equals(RegexPluginOption.TAG)) {
			pluginOption = new RegexPluginOption(subOptions); 
		} else if (optionTag.equals(SearchPluginOption.TAG)) {
			pluginOption = new SearchPluginOption(subOptions); 
		} else if (optionTag.equals(SequencePluginOption.TAG)) {
			pluginOption = new SequencePluginOption(subOptions); 
		} else if (optionTag.equals(SpeciesPluginOption.TAG)) {
			pluginOption = new SpeciesPluginOption(subOptions);
		} else if (optionTag.equals(WordPluginOption.TAG)) {
			pluginOption = new WordPluginOption(subOptions);
		} else {
			LOG.error("unknown command: "+optionTag);
//			LOG.info("commands: "+COMMANDS);
		}
		if (pluginOption != null) {
			pluginOption.setOptionFlags(optionFlags);
		}
		return pluginOption;
	}

	private void setOptionFlags(List<OptionFlag> optionFlags) {
		this.optionFlags = optionFlags;
	}

	List<OptionFlag> getOptionFlags() {
		return this.optionFlags;
	}

	public void setProject(File projectDir) {
		this.projectDir = projectDir;
	}
	
	public abstract void run();

	// create optionSnippets
	public void runFilterResultsXMLOptions() {
		for (String option : options) {
			runFilterResultsXMLOptions(option);
		}
	}
	
	private void runFilterResultsXMLOptions(String option) {
		// typical string:
		// AMIPluginOption  - filter: --project /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10 
		//    --filter file(**/word/frequencies/results.xml)xpath(//result[@count>70])
		//    -o word.frequencies.snippets.xml  

		String filterCommandString = createFilterCommandString(option);
		LOG.trace("filter debug: "+filterCommandString);
		new DefaultArgProcessor(filterCommandString).runAndOutput();
		LOG.trace("end filter");
		return;
	}

	protected String createFilterCommandString(String option) {
		String cmd = "--project "+projectDir;
		String xpathFlags = createXpathQualifier();
		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+getOption(option)+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
		cmd += " -o "+createSnippetsFilename(option)+"  ";
		DefaultArgProcessor.CM_LOG.debug("runFilterResultsXMLOptions: "+cmd);
		LOG.trace(option);
		return cmd;
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

	public String getPlugin() {
		return plugin;
	}

	protected String createXpathQualifier() {
		String xpathFlags = getOptionFlagString("xpath", "");
		if (xpathFlags != null && !"".equals(xpathFlags)) {
			xpathFlags = "["+xpathFlags+"]";
		}
		return xpathFlags;
	}

	protected String getOptionFlagString(String key, String separator) {
		StringBuilder optionFlagString = new StringBuilder();
		List<OptionFlag> keyedOptionFlags = getKeyedOptionFlags(key);
		if (keyedOptionFlags.size() > 0) {
			if (!key.equals("xpath")) {
				optionFlagString.append(" --"+key);
			}
			for (int i = 0; i < keyedOptionFlags.size(); i++) {
				optionFlagString.append(separator);
				String ko = keyedOptionFlags.get(i).getValue();
				LOG.trace(">>>>>>>>>>>>>"+ko);
				optionFlagString.append(ko);
			}
		}
		return optionFlagString.toString();
	}

	private List<OptionFlag> getKeyedOptionFlags(String key) {
		List<OptionFlag> keyedOptionFlags = new ArrayList<OptionFlag>();
		for (OptionFlag optionFlag : optionFlags) {
			if (optionFlag.getKey().equals(key)) {
				LOG.trace("OF "+optionFlag+ " /// "+key);
				keyedOptionFlags.add(optionFlag);
			}
		}
		return keyedOptionFlags;
	}

	/**
	 * what it actually runs
	 * 
String cmd0 ="	--project /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10"
		+ " -i word.frequencies.snippets.xml"
		+ " --xpath //result/@exact"
		+ " --summaryfile word.frequencies.count.xml"
		+ " --dffile word.frequencies.documents.xml";
		*/
	protected void runMatchSummaryAndCount(String option) {
		String cmd = "--project "+projectDir
				+ " -i "+createSnippetsFilename(option)
				+ " --xpath //result/"+resultXPathAttribute
				+ " --summaryfile "+createCountFilename(option)
				+ " --dffile "+createDocumentCountFilename(option)
				;
		DefaultArgProcessor.CM_LOG.debug("runMatchSummaryAndCount: "+cmd);
//		System.out.print("C: "+option+"; ");
		new DefaultArgProcessor(cmd).runAndOutput();
//		LOG.debug("end summary "+option);
		return;
	}
	


	// analyze optionSnippets
	public void runSummaryAndCountOptions() {
		for (String option : options) {
			runMatchSummaryAndCount(option);
		}
	}

	public String toString() {
		return plugin+"("+options+")"+optionFlags;
	}

	protected String createSnippetsFilename(String option) {
		return plugin+"."+getOption(option)+".snippets.xml";
	}

	protected String createCountFilename(String option) {
		return plugin+"."+getOption(option)+".count.xml";
	}
	
	protected String createDocumentCountFilename(String option) {
		return plugin+"."+getOption(option)+".documents.xml";
	}
	
	protected String getOption(String option) {
		return option;
	}

	protected StringBuilder createCoreCommandStringBuilder() {
		StringBuilder commandStringBuilder = new StringBuilder("--project "+projectDir+" -i scholarly.html");
//		LOG.debug(" JUNK remove");
//		commandStringBuilder.append(" -o junk.xxx");
		commandStringBuilder.append(getOptionFlagString("context", " "));
		return commandStringBuilder;
	}
	
	public CellRenderer getNewCellRenderer() {
		CellRenderer cellRenderer = new CellRenderer(this);
		return cellRenderer;
	}

	protected boolean matches(String pluginOptionName) {
		String pluginOptionTag = pluginOptionName.split(":")[0];
		LOG.trace("TAG "+pluginOptionTag);
		return getPlugin().equals(pluginOptionTag);
	}

}
