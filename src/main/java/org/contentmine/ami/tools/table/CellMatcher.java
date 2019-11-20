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
public class CellMatcher extends AbstractTTElement implements HasQuery {
	private static final Logger LOG = Logger.getLogger(CellMatcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String TAG = "cell";
	private TQueryTool queryTool;
	
	public CellMatcher(TTemplateList templateList) {
		super(TAG, templateList);
		LOG.debug("CELL CTOR");
	}
	public TQueryTool getOrCreateQueryTool() {
		if (queryTool == null) {
			queryTool = new TQueryTool(this);
		}
		LOG.debug("CELL QUERY");
		return queryTool;
	}


}
