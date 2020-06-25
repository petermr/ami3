package org.contentmine.ami;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.plugins.regex.RegexPlugin;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.ami.wordutil.WordSetWrapper;
import org.contentmine.cproject.args.DefaultArgProcessor;

public class ClinicalTrialsDemo {

	private static final Logger LOG = LogManager.getLogger(ClinicalTrialsDemo.class);
public static void main(String[] args) throws IOException {
//		runManyFiles();
		runDirectoryWithCMDirs();
//		runRegex();
	}

	private static void runManyFiles() throws IOException {
//		FileUtils.copyDirectory(new File("trialsdata/"), new File("trialstemp/"));
		String args[] = {
			"-q", // output from quickscrape
    "trialstemp/http_www.trialsjournal.com_content_16_1_1/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_10/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_11/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_12/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_13/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_14/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_15/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_16/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_17/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_18/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_19/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_2/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_20/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_21/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_22/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_23/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_24/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_25/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_26/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_27/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_28/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_29/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_3/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_30/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_31/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_32/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_33/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_34/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_35/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_36/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_37/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_38/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_39/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_4/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_40/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_41/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_42/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_43/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_44/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_45/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_46/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_47/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_48/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_49/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_5/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_50/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_51/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_52/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_53/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_54/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_55/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_56/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_57/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_58/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_59/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_6/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_60/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_61/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_62/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_63/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_64/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_65/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_66/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_67/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_68/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_69/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_7/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_70/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_71/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_72/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_73/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_74/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_75/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_76/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_77/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_78/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_79/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_8/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_80/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_81/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_82/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_83/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_84/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_85/",
    "trialstemp/http_www.trialsjournal.com_content_16_1_9/",
	"--w.words", WordArgProcessor.WORD_FREQUENCIES,
	"--w.stopwords", WordSetWrapper.COMMON_ENGLISH_STOPWORDS_TXT,
	"--w.wordtypes", "acronym",
		};
		DefaultArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}
	
	private static void runDirectoryWithCMDirs() throws IOException {
		FileUtils.copyDirectory(new File("trialsdata/"), new File("trialstemp/"));
		String args[] = {
			"-q trialstemp/ --w.words " + WordArgProcessor.WORD_FREQUENCIES + " --w.stopwords " +WordSetWrapper.COMMON_ENGLISH_STOPWORDS_TXT,
		};
		DefaultArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}
	

	
	private static void runRegex() throws IOException {
//		FileUtils.copyDirectory(new File("trialsdata/"), new File("trialstemp/"));
		String[] args = {
				"-q", "trialstemp/", // contains 86 CTree files
				"-i", "scholarly.html",
				"-o", "results.xml",
				"--context", "40", "40",
				"--r.regex", 
				    "regex/consort0.xml",
		};
		AMIPlugin regexPlugin = new RegexPlugin(args);
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) regexPlugin.getArgProcessor();
		argProcessor.runAndOutput();
	}
}
