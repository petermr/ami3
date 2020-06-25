package org.contentmine.ami.tools.table;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
public class FooterMatcher extends AbstractTTElement implements HasQuery {
	private static final Logger LOG = LogManager.getLogger(FooterMatcher.class);
public static String TAG = "footer";
	private TQueryTool queryTool;
	
	public FooterMatcher(TTemplateList templateList) {
		super(TAG, templateList);
	}
	public TQueryTool getOrCreateQueryTool() {
		if (queryTool == null) {
			queryTool = new TQueryTool(this);
		}
		return queryTool;
	}

}
