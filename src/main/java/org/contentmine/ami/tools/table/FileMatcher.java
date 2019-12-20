package org.contentmine.ami.tools.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/**
	<tableTemplate name="composition">
			 
		<file>
		    <query> 
		        .* /table_\\d+\\.xml"/>
		    </query>
	</tableTemplate>
 * @author pm286
 *
 */
public class FileMatcher extends AbstractTTElement implements HasQuery {
	private static final Logger LOG = Logger.getLogger(FileMatcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String TAG = "file";
	private TQueryTool queryTool;
	
	public FileMatcher(TTemplateList templateList) {
		super(TAG, templateList);
//		LOG.debug("FILE CTOR");
	}

	public TQueryTool getOrCreateQueryTool() {
		if (queryTool == null) {
			queryTool = new TQueryTool(this);
		}
//		LOG.debug("FILE QUERY");
		return queryTool;
	}
}
