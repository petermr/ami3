package org.contentmine.ami.tools.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
	<tableTemplate name="composition">
		<title find="
		     composition OR
			 oil OR
			 EO OR
			 "
			 >
		</title>
	</tableTemplate>
 * @author pm286
 *
 */
public class TitleMatcher extends AbstractTTElement implements HasQuery {
	private static final Logger LOG = Logger.getLogger(TitleMatcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String TAG = "title";
	private TQueryTool queryTool;
	
	public TitleMatcher(TTemplateList templateList) {
		super(TAG, templateList);
//		LOG.debug("TITLE CTOR");
	}
	
	public TQueryTool getOrCreateQueryTool() {
		if (queryTool == null) {
			queryTool = new TQueryTool(this);
		}
//		LOG.debug("TITLE QUERY");
		return queryTool;
	}
	
}
