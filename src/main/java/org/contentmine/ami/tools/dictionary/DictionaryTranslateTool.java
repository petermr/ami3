package org.contentmine.ami.tools.dictionary;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.CMJsonDictionary;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.tools.AMIDictionaryToolOLD;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import picocli.CommandLine.Command;

@Command(
		name = "translate",
		description = {
				"translates dictionaries between formats",
				"(NOT natural languages)"
				+ ""
		})
public class DictionaryTranslateTool extends AbstractAMIDictTool {
	private static final Logger LOG = Logger.getLogger(DictionaryTranslateTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private CMJsonDictionary cmJsonDictionary;
	private DefaultAMIDictionary xmlDictionary;
	
	public DictionaryTranslateTool() {
		super();
	}
	
	private void convertDictionaries(
			File infile, DictionaryFileFormat informat, File outfile, DictionaryFileFormat outformat) {
		if (DictionaryFileFormat.json.equals(informat)) {
			convertJsonDictionaryToXML(infile, outfile);
			
		} else if (DictionaryFileFormat.xml.equals(informat)) {
			xmlDictionary = DefaultAMIDictionary.createSortedDictionary(infile);
			cmJsonDictionary = CMJsonDictionary.convertXMLToJson(xmlDictionary);
		}
	}

	private void convertJsonDictionaryToXML(File infile, File outfile) {
		String inString = null;
		try {
			inString = FileUtils.readFileToString(infile, UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read Json: " + infile, e);
		}
		cmJsonDictionary = CMJsonDictionary.readJsonDictionary(inString);
		xmlDictionary = CMJsonDictionary.convertJsonToXML(cmJsonDictionary);
		if (xmlDictionary != null) {
			addWikiLinksToDictionary(xmlDictionary);
		}
		outputXMLDictionary(outfile);
	}

	private void outputXMLDictionary(File outfile) {
		if (xmlDictionary != null) {
			try {
				XMLUtil.debug(xmlDictionary.getDictionaryElement(), outfile, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write XML dictionary "+outfile, e);
			}
		}
	}

	private void addWikiLinksToDictionary(DefaultAMIDictionary xmlDictionary) {
		List<Element> entryList = xmlDictionary.getEntryList();
		for (Element entry : entryList) {
			addWikiLinks(entry);
		}
	}

	@Override
	public void runSub() {
		File directory = null;
		boolean useAbsoluteNames = false;
		if (dictionaryTopname != null) {
			directory = new File(dictionaryTopname);
		} else if (dictionaryList != null && dictionaryList.size() > 0){
			directory = new File(dictionaryList.get(0)).getParentFile();
			useAbsoluteNames = true;
		} else {
			addLoggingLevel(Level.ERROR, "Must give either 'directory' or existing absolute filenames of dictionaries");
			return;
		}
		for (String dictionaryS : dictionaryList) {
			String basename = FilenameUtils.getBaseName(dictionaryS);
			File dictionaryFile = (useAbsoluteNames) ? new File(dictionaryS) : new File(directory, dictionaryS);
			if (!dictionaryFile.exists()) {
				addLoggingLevel(Level.ERROR, "File does not exist: "+dictionaryFile);
				continue;
			}
			dictInformat = DictionaryFileFormat.getFormat(FilenameUtils.getExtension(dictionaryS));
			if (dictInformat.equals(dictOutformat)) {
				addLoggingLevel(Level.WARN, "dictionary input and output formats identical; no action");
				continue;
			}
			File dictOutfile = new File(dictionaryFile.getParentFile(), basename + "." + dictOutformat);
			convertDictionaries(dictionaryFile, dictInformat, dictOutfile, dictOutformat);
		}
		
	}

}
