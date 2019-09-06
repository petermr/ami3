package org.contentmine.cproject.metadata.quickscrape;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;

import nu.xom.Attribute;
import nu.xom.Element;

/** URL record in log.
 * 
 * @author pm286
 *
 */
public class QSRecord {

	private static final String HTTP_DX_DOI_ORG = "http://dx.doi.org/";
	private static final Logger LOG = Logger.getLogger(QSRecord.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String url;
	private String end;
	private List<String> middle;
	private int captured;
	private int total;
	private int failed;
	private ArrayList<QSLine> qsLines;
	
	public QSRecord() {
		middle = new ArrayList<String>();
		this.qsLines = new ArrayList<QSLine>();
	}
	
	public void setUrl(String string) {
		this.url = string;
	}

	public void setEnd(String string) {
		this.end = string;
		Pattern CAPTURE = Pattern.compile("captured (\\d+)/(\\d+) elements \\((\\d+) captures failed\\)");
		Matcher matcher = CAPTURE.matcher(end);
		if (matcher.matches()) {
			this.captured = Integer.parseInt(matcher.group(1));
			this.total = Integer.parseInt(matcher.group(2));
			this.failed = Integer.parseInt(matcher.group(3));
		}
	}

	public void add(String line) {
		middle.add(line);
	}

	public String getUrl() {
		return url;
	}
	
	public String getEnd() {
		return end;
	}
	
	public boolean isNo200() {
		return QuickscrapeLog.ERROR_PAGE_DID_NOT_RETURN_A_200.equals(end);
	}
	
	public int getCaptured() {
		return captured;
	}
	
	public int getFailed() {
		return failed;
	}
	
	public int getTotal() {
		return total;
	}

	public String getDOIPrefix() {
		String prefix = null;
		if (url != null) {
			prefix = CMineUtil.getDOIPrefix(url);
		}
		return prefix;
	}

	public void addInner(String level, String module, String msg) {
		addQSLine(level, module, msg);
	}

	private void addQSLine(String level, String module, String msg) {
		QSLine qsLine = new QSLine(level, module, msg);
		qsLines.add(qsLine);
	}

	public void addOuter(String level, String msg) {
		addQSLine(level, null, msg);
	}
	
	public List<QSLine> getQSLines() {
		return qsLines;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(url+"\n");
		for (String s : middle) {
			sb.append(s+"\n");
		}
		for (QSLine qsLine : qsLines) {
			sb.append(qsLine.toString()+"\n");
		}
		if (total > 0) {
			sb.append(captured+"/"+total+"("+failed+")");
		} else {
			sb.append(end+"\n");
		}
		return sb.toString();
	}

	public Element getXMLElement() {
		Element record = new Element("record");
		record.addAttribute(new Attribute("url", url));
		record.addAttribute(new Attribute("doiPrefix", getDOIPrefix()));
		record.addAttribute(new Attribute("total", ""+total));
		record.addAttribute(new Attribute("failed", ""+failed));
		record.addAttribute(new Attribute("captured", ""+captured));
		if (isNo200()) {
			record.addAttribute(new Attribute("no200", "true"));
		}
		return record;
	}

}
