package org.contentmine.ami.tools.table;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * parses and runs table template queries
 * @author pm286
 *
 */
public class TTQueryTool {
	private static final String QUERY_LIST = "queryList";
	private static final String QUERY = "query";
	private static final String FIND = "find";
	private static final String MATCH = "match";
	private static final String MODE = "mode";
	private static final String OR = "OR";
	private static final Logger LOG = Logger.getLogger(TTQueryTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public Element parseQueries(HasQuery queryElement) {
		Element result = new Element(QUERY_LIST);
		String queryContent = ((Element) queryElement).getValue();
		List<String> chunks = new ArrayList<String>(Arrays.asList(queryContent.split(OR)));
		for (String chunk : chunks) {
			Element query = new Element(QUERY);
			query.appendChild(chunk);
			String mode = (isMatchFormat(chunk)) ? MATCH : FIND;
			query.addAttribute(new Attribute(MODE, mode));
		}
		return result;
		
	}
	
	private boolean isMatchFormat(String chunk) {
		return chunk != null && chunk.startsWith("^") && chunk.endsWith("$"); 
		
	}

	public boolean getQueryResult(HasQuery hasQuery) {
		Element queryListElement = parseQueries(hasQuery);

		return false;
	}

}
