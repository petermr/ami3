package org.contentmine.cproject.metadata.quickscrape;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CProjectArgProcessor;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Attribute;
import nu.xom.Element;

public class QuickscrapeLog {

	
	private static final Logger LOG = Logger.getLogger(QuickscrapeLog.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String ERROR_PAGE_DID_NOT_RETURN_A_200 = "error: page did not return a 200 so moving on to next url in list";
	public static final String INFO_URL_PROCESSED = "info: URL processed: ";
	public static final String INFO_PROCESSING_URL = "info: processing URL: ";


	// info: URL processed
	private final static Pattern OUTER = Pattern.compile("(trace|debug|info|warn|error)\\:\\s(.*)");
	// [debug] [phantom] opening url: http://dx.doi.org/10.1093/mnras/stw468, HTTP GET
	private final static Pattern INNER1 = Pattern.compile("\\s*\\[(trace|debug|info|warn|error)\\]\\s+\\[(phantom)\\]\\s+(.*)");
//	private final static Pattern INNER2 = Pattern.compile("\\[(^\\])+\\]\\s+(.*)");
	
	private ArrayList<String> lines;
	private List<String> headerLines;
	private int currentLine;
	private QSRecord record;
	private List<QSRecord> records;

	public QuickscrapeLog() {
		
	}
	
	public final static QuickscrapeLog readLog(File file) throws IOException {
		QuickscrapeLog quickscrapeLog = null;
		if (file != null) {
			quickscrapeLog = new QuickscrapeLog();
			List<String> lines = FileUtils.readLines(file);
			quickscrapeLog.setLines(lines);
		}
		return quickscrapeLog;
	}

	public void setLines(List<String> lines) {
		this.lines = new ArrayList<String>(lines);
	}

	public List<String> getLines() {
		return lines;
	}

	public void analyze() {
		readHeader();
		readRecords();
	}
	
	private void readHeader() {
		currentLine = 0;
		headerLines = new ArrayList<String>();
		for (; currentLine < lines.size(); currentLine++) {
			String line = lines.get(currentLine);
			if (line.startsWith(INFO_PROCESSING_URL)) {
				break;
			}
			headerLines.add(line);
		}
	}

	private void readRecords() {
		records = new ArrayList<QSRecord>();
		for (; currentLine < lines.size(); currentLine++) {
			String line = lines.get(currentLine).trim();
			line = removeColors(line);
			// remove ESCAPES
			line = line.replaceAll(""+(char)27, " ");
			line = line.replaceAll("\\t", " ");
			line = line.replaceAll("\\s+", " ");
			if (line.startsWith(INFO_PROCESSING_URL)) {
				// this is special as it's the start of a new record
				startAndExtractUrl(line);
			} else if (line.equals(ERROR_PAGE_DID_NOT_RETURN_A_200)) {
				// this is special as it's the end of a record
				terminateRecordSinceNo200(line);
			} else if (line.startsWith(INFO_URL_PROCESSED)) {
				// this is special as it's the end of a record
				terminateRecordNormally(line);
			} else {
				matchLevelMessages(line);
			}
		}
	}

	private void terminateRecordNormally(String line) {
		if (record == null) {
			LOG.warn("record not opened");
		}
		record.setEnd(line.substring(INFO_URL_PROCESSED.length()));
		record = null;
	}

	private void terminateRecordSinceNo200(String line) {
		if (record == null) {
			LOG.warn("record not opened before no 200");
		}
		record.setEnd(line);
		record = null;
	}

	private void startAndExtractUrl(String line) {
		if (record != null) {
			LOG.trace("record already opened: "+record.toString());
		}
		record = new QSRecord();
		records.add(record);
		record.setUrl(line.substring(INFO_PROCESSING_URL.length()));
	}

	/**
[36m[info][0m [phantom] Running suite: 3 steps
[32;1m[debug][0m	 */
	private String removeColors(String line) {
		return line == null ? null : line.replaceAll("\\[36m|\\[32;1m|\\[0m", "");
	}

	private void matchLevelMessages(String line) {
		Matcher outerMatcher = OUTER.matcher(line);
		if (outerMatcher.matches()) {
			record.addOuter(outerMatcher.group(1), outerMatcher.group(2));
		} else {
			Matcher innerMatcher1 = INNER1.matcher(line);
			if (innerMatcher1.matches()) {
				String level = innerMatcher1.group(1);
				String module = innerMatcher1.group(2);
				String msg = innerMatcher1.group(3);
				record.addInner(level, module, msg);
			} else {
				LOG.debug("Cannot parse: "+line);
			}
		}
	}


	public List<QSRecord> getQSURLRecords() {
		if (records == null) {
			this.analyze();
		}
		return records;
	}

	public List<QSRecord> getNo200s() {
		List<QSRecord> no200List = new ArrayList<QSRecord>();
		getQSURLRecords();
		for (QSRecord record : records) {
			if (record.isNo200()) {
				no200List.add(record);
			}
		}
		return no200List;
	}

	public List<QSRecord> getNoCaptureRecords() {
		List<QSRecord> noCaptures = new ArrayList<QSRecord>();
		getQSURLRecords();
		for (QSRecord record : records) {
			if (record.getCaptured() == 0) {
				noCaptures.add(record);
			}
		}
		return noCaptures;
	}

	public Multimap<String, String> getUrlsByPrefix(List<QSRecord> records) {
		Multimap<String, String> urlByDOIPrefix = ArrayListMultimap.create();
		for (QSRecord record : records) {
			String prefix = record.getDOIPrefix();
			String url = record.getUrl();
			urlByDOIPrefix.put(prefix, url);
		}
		return urlByDOIPrefix;
	}

	public void writeNoCaptureRecords(CProjectArgProcessor cProjectArgProcessor) throws IOException {
		List<QSRecord> records = getNoCaptureRecords();
		File noCaptureFile = new File(cProjectArgProcessor.getCProject().getDirectory(), cProjectArgProcessor.quickscrapeCaptureFilename);
		writeRecords("noCaptures", records, noCaptureFile);
	}

	private void writeRecords(String name, List<QSRecord> records, File file) throws IOException {
		Element element = new Element(name);
		Element recordsElement = new Element("records");
		element.appendChild(recordsElement);
		Multiset<String> doiPrefixes = HashMultiset.create();
		for (QSRecord record : records) {
			recordsElement.appendChild(record.getXMLElement());
			doiPrefixes.add(record.getDOIPrefix());
		}
		Element prefixesElement = new Element("doiPrefixes");
		element.appendChild(prefixesElement);
		Iterable<Entry<String>> entries = CMineUtil.getEntriesSortedByCount(doiPrefixes);
		Iterator<Entry<String>> iterator = entries.iterator();
		while (iterator.hasNext()) {
			Entry<String> entry = iterator.next();
			Element entryElement = new Element("prefix");
			entryElement.addAttribute(new Attribute("prefix", entry.getElement()));
			entryElement.addAttribute(new Attribute("count", ""+entry.getCount()));
			prefixesElement.appendChild(entryElement);
		}
		XMLUtil.debug(element, file, 1);
	}

	public void writeNo200Records(CProjectArgProcessor cProjectArgProcessor) throws IOException {
		List<QSRecord> records = getNo200s();
		File no200File = new File(cProjectArgProcessor.getCProject().getDirectory(), cProjectArgProcessor.quickscrapeNo200Filename);
		writeRecords("no200", records, no200File);
	}
}
