package org.contentmine.cproject.args.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.XMLUtils;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

/** tool to log events and data from CTree.
 * 
 * @author pm286
 *
 */
/** why not use Log4j?
 * have asked
 * http://stackoverflow.com/questions/31903280/recording-data-and-events-using-log4j-over-many-classes
 * 
 * see also
 * 
 * http://stackoverflow.com/questions/2763740/log4j-log-output-of-a-specific-class-to-a-specific-appender?lq=1
 * 
 * @author pm286
 *
 */

public class CMineLog extends AbstractLogElement {

	private static final String FILE_NAME = "file";
	private static final String FILE_ATT = "file";
	private static final String DATE = "date";
	private static final String DATE_ATT = "date";
	private static final String TIP_DELETION_ERROR = "Tip deletion error";
	private static final String MILLIS2 = "//@millis";
	private static final String WROTE_FILES_TITLE = "wrote files";
	private static final String MILLIS = "//*/@millis";
	private static final String METHOD = "//*/@method";
	private static final String DATE_TIME = "dateTime";
	private static final String SYNTAX_ERROR = "Syntax error";
	
	private static final String INVALID_GENUS_XPATH = "*[starts-with(@message,'invalid genus')]";
	private static final String NULL_NEXML_XPATH = "warn[@message='null nexml']";
	private static final String PHYLO_TREE_OUTPUT_XPATH = "*[starts-with(@message,'PhyloTree output to:')]";
	private static final String ANALYZED_PIXELS_XPATH = "*[@message='Analyzed pixels for tree successfully']";
	private static final String DUPLICATE_EDGE_XPATH = "error[starts-with(@message,'duplicate edge')]";
	private static final String DUPLICATE_EDGE = "duplicate edge";
	private static final String EDGE_TARGET_XPATH = "error[starts-with(@message,'edge target')]";
	private static final String EDGE_TARGET_SOURCE = "target==source";
	private static final String BAD_SYNTAX_XPATH = "error[starts-with(@message,'ERR_BAD_SYNTAX')]";
	private static final String CANNOT_DELETE_TIP_XPATH = "error[starts-with(@message,'cannot delete tip')]";
	private static final String WROTE_FILES_XPATH = "info[starts-with(@message,'wrote')]";

	private static final String EMPTY_TIPS_TITLE = "empty tips";
	private static final String EMPTY_TIPS_XPATH = "error[@message='ERR_BAD_SYNTAX []']";
	
	private static final String DELETED_NODE_TITLE = "deleted nodes";
	private static final String DELETED_NODE_XPATH = "info[starts-with(@message,'deleted node')]";
	
	
	static final Logger LOG = Logger.getLogger(CMineLog.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private final static String TAG = "log";
	private static final String LOG_ELEMENT = "log";
	
	public static final String LOG_XML = "log.xml";
	public final static String CMINE_LOG = "cproject.log.xml";
	private List<CMineLogRecord> recordList;
	private List<String> csvHeaders;

	public CMineLog() {
		super(TAG);
	}
	
	public CMineLog(File file) {
		super(TAG, file);
	}

	public void removeNodes(String xpath) {
		List<Node> nodes = XMLUtil.getQueryNodes(this, xpath);
		for (Node node : nodes) {
			node.detach();
		}
	}

	public void mergeLogFile(File logXmlFile, File dir, String... xpathList) {
//		Element logXmlElement = XMLUtil.parseQuietlyToDocument(logXmlFile).getRootElement();
		Element logXmlElement = XMLUtils.parseWithoutDTD(file).getRootElement();
		AbstractLogElement logElement = new AbstractLogElement(LOG_ELEMENT);
		XMLUtil.copyAttributes(logXmlElement, logElement);
		logElement.addAttribute(new Attribute(FILE_NAME, dir.toString()));
		this.appendChild(logElement);
		for (String xpath : xpathList) {
			List<Node> nodes = XMLUtil.getQueryNodes(logXmlElement, xpath);
			for (Node node : nodes) {
				logElement.appendChild(node.copy());
			}
		}
	}

	public void getLogAttribute(String attName, String msg) {
		List<Element> logElements = XMLUtil.getQueryElements(this, LOG_ELEMENT);
		for (Element logElement : logElements) {
			String value = logElement.getAttributeValue(attName);
			Element info = new InfoElement(msg);
			info.appendChild(String.valueOf(value));
			logElement.appendChild(info);
		}
	}

	public void collectWithinLog(String xpath, String msg) {
		List<Element> logElements = XMLUtil.getQueryElements(this, LOG_ELEMENT);
		for (Element logElement : logElements) {
			List<Element> elements = XMLUtil.getQueryElements(logElement, xpath);
			int count = elements.size();
			for (Element element : elements) {
				element.detach();
			}
			if (count > 0) {
				Element info = new InfoElement(msg);
				info.appendChild(String.valueOf(count));
				logElement.appendChild(info);
			}
		}
	}

	public void deleteFromLog(String xpath) {
		List<Element> logElements = XMLUtil.getQueryElements(this, LOG_ELEMENT);
		for (Element logElement : logElements) {
			List<Element> elements = XMLUtil.getQueryElements(logElement, xpath);
			for (Element element : elements) {
				element.detach();
			}
		}
	}

	public void summarizeInLog(File[] files) {
		for (File dir : files) {
			if (!dir.isDirectory()) {
				// skip this log file
				if (CMINE_LOG.equals(dir.getName())) {
					continue;
				}
				throw new RuntimeException("forbidden file "+dir+"; "+dir.getName());
			}
			File logXml = new File(dir, LOG_XML);
			if (!logXml.exists()) {
				error("missingLogFile: "+dir);
			} else {
				mergeLogFile(logXml, dir, "//error", "//warn", "//info");
			}
		}
		removeNodes(METHOD);
		removeNodes(MILLIS);
		getLogAttribute(FILE_ATT, FILE_NAME);
		getLogAttribute(DATE_ATT, DATE);
		collectWithinLog(DELETED_NODE_XPATH, DELETED_NODE_TITLE);
		collectWithinLog(EMPTY_TIPS_XPATH, EMPTY_TIPS_TITLE);
		collectWithinLog(WROTE_FILES_XPATH, WROTE_FILES_TITLE);
		collectWithinLog(CANNOT_DELETE_TIP_XPATH, TIP_DELETION_ERROR);
		collectWithinLog(BAD_SYNTAX_XPATH, SYNTAX_ERROR);
		collectWithinLog(EDGE_TARGET_XPATH, EDGE_TARGET_SOURCE);
		collectWithinLog(DUPLICATE_EDGE_XPATH, DUPLICATE_EDGE);
		
		deleteFromLog(ANALYZED_PIXELS_XPATH);
		deleteFromLog(PHYLO_TREE_OUTPUT_XPATH);
		deleteFromLog(NULL_NEXML_XPATH);
		deleteFromLog(INVALID_GENUS_XPATH);
		removeNodes(MILLIS2);
		writeLog();
		createCSV(Arrays.asList(new String[]{
			FILE_NAME,
			DATE,
			DELETED_NODE_TITLE, 
			EMPTY_TIPS_TITLE,
			WROTE_FILES_TITLE,
			TIP_DELETION_ERROR,
			SYNTAX_ERROR,
			EDGE_TARGET_SOURCE,
			DUPLICATE_EDGE
			}
		));
}

	private void createCSV(List<String> messageTypes) {
		if (messageTypes == null || messageTypes.size() == 0) {
			throw new RuntimeException("CSV headers must be initialized");
		}
		this.csvHeaders = messageTypes;
		recordList = new ArrayList<CMineLogRecord>();
		Elements elements = this.getChildElements();
		LOG.trace("descendants "+XMLUtil.getQueryElements(this, "//*").size());
		for (int i = 0; i < elements.size(); i++) {
			Element child = elements.get(i);
			CMineLogRecord record = new CMineLogRecord(csvHeaders);
			AbstractLogElement log = AbstractLogElement.readAndCreateElement(child);
			for (String messageType : messageTypes) {
				String count = log.getMessageCount(messageType);
				record.add(messageType, count);
			}
			recordList.add(record);
		}
		LOG.debug("records "+recordList.size());
		writeCSV("target/junk.csv");
	}

	private void writeCSV(String fileName) {
		List<List<String>> valueListList = new ArrayList<List<String>>();
        for (CMineLogRecord record : recordList) {
        	List<String> valueList = new ArrayList<String>();
        	for (String s : record.getValues()) {
        		valueList.add(s);
        	}
        	valueListList.add(valueList);
        }

        CMineUtil.writeCSV(fileName, csvHeaders, valueListList);
    }

}
