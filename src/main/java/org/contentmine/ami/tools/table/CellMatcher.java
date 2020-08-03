package org.contentmine.ami.tools.table;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

//	<tableTemplate name="composition">
//		<title find="
//		     composition OR
//			 oil OR
//			 EO OR
//			 "
//			 >
//		</title>
//	</tableTemplate>
/** @author pm286
 *
 */
public class CellMatcher extends AbstractTTElement implements HasQuery {
	private static final Logger LOG = LogManager.getLogger(CellMatcher.class);
public static String TAG = "cell";
	private TQueryTool queryTool;
	
	public CellMatcher(TTemplateList templateList) {
		super(TAG, templateList);
	}
	public TQueryTool getOrCreateQueryTool() {
		if (queryTool == null) {
			queryTool = new TQueryTool(this);
		}
		return queryTool;
	}
	
}
