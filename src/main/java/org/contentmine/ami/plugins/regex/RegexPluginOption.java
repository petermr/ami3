package org.contentmine.ami.plugins.regex;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPluginOption;
import org.contentmine.cproject.files.ResourceLocation;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Document;

public class RegexPluginOption extends AMIPluginOption {

	private static final Logger LOG = LogManager.getLogger(RegexPluginOption.class);
public static final String TAG = "regex";
	private CompoundRegex compoundRegex;

	public RegexPluginOption() {
		super(TAG);
	}
	
	public RegexPluginOption(List<String> options) {
		super(TAG, options);
		if (options != null && options.size() != 0) {
			readRegexFile(options.get(0));
		}
	}
	
//	public RegexPluginOption(List<String> options, List<String> flags) {
//		super(TAG, options, flags);
//		if (options != null && options.size() != 0) {
//			readRegexFile(options.get(0));
//		}
//	}

	private void readRegexFile(String regexFilename) {
		ResourceLocation location = new ResourceLocation();
		InputStream is = location.getInputStreamHeuristically(regexFilename);
		Document doc = XMLUtil.parseQuietlyToDocument(is);
		RegexArgProcessor regexArgProcessor = new RegexArgProcessor();
		compoundRegex = new CompoundRegex(regexArgProcessor, doc.getRootElement());
		options = Arrays.asList(new String[]{compoundRegex.getTitle()});
		LOG.debug("OPT "+options);
	}

	public void run() {
		StringBuilder commandString = createCoreCommandStringBuilder();
		commandString.append(" --r.regex "+optionString);
//		String sw = getOptionFlagString("w.stopwords", " ");
//		commandString.append(sw);
//		searchDictionary = getOptionFlagString("sr.search", " ");
//		if (searchDictionary != null && !searchDictionary.equals("")) {
//			commandString.append(searchDictionary);
//			plugin = "search";
//			dictionary = getOption(null);
//		}
		LOG.debug(">>>>>>>"+commandString);
		new RegexArgProcessor(commandString.toString()).runAndOutput();
	}

	protected String getPlugin(String plugin) {
		return plugin;
	}

	protected String getOption(String option) {
		String opt = option;
//		if (searchDictionary != null && !searchDictionary.trim().equals("")) {
//			String[] ss = searchDictionary.split("/");
//			String sss = ss[ss.length-1];
//			sss = sss.split("\\.")[0];
//			opt = sss;
//		}
		return opt;
	}

//	protected String createFilterCommandString(String option) {
//		String cmd = "--project "+projectDir;
//		String xpathFlags = createXpathQualifier();
//		option = (xxx != null) ? xxx : option;
//		cmd += " --filter file(**/"+getPlugin(plugin)+"/"+option+"/results.xml)xpath("+resultXPathBase+xpathFlags+") ";
//		cmd += " -o "+createSnippetsFilename(option)+"  ";
//		LOG.debug("runFilterResultsXMLOptions: >>>> "+cmd);
//		return cmd;
//	}


//	protected void runMatchSummaryAndCount(String option) {
//		String cmd = "--project "+projectDir+" -i "+createSnippetsFilename("regex")+"  "
//				+ "--xpath //result/"+resultXPathAttribute+" --summaryfile "+createCountFilename(dictionary);
//		LOG.debug("runMatchSummaryAndCount: "+cmd);
//		new DefaultArgProcessor(cmd).runAndOutput();
//	}

}
