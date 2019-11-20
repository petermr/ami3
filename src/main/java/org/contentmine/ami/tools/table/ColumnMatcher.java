package org.contentmine.ami.tools.table;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

/**
		<column name="compound" case="insensitive" >
		    <title>
			    <query>
				    constituent OR
				    compound OR
				    component
				    NOT activity
			    </query>
		    </title>
			<cell>
	  		  <query>^@CHEMICAL@$</query>
	  		  <lookup>@COMPOUND_DICT@</lookup>
			</cell>
		</column>
 * @author pm286
 *
 */
public class ColumnMatcher extends AbstractTTElement {
	private static final Logger LOG = Logger.getLogger(ColumnMatcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String TAG = "column";
	
	private HasQuery titleMatcher;
	private CellMatcher cellMatcher;
	private FooterMatcher footerMatcher;
	
	public HasQuery getOrCreateTitleMatcher() {
		if (titleMatcher == null) {
			titleMatcher = (TitleMatcher) XMLUtil.getSingleChild(this, TitleMatcher.TAG);
		}
		return titleMatcher;
	}

	public CellMatcher getOrCreateCellMatcher() {
		if (cellMatcher == null) {
			cellMatcher = (CellMatcher) XMLUtil.getSingleChild(this, CellMatcher.TAG);
		}
		return cellMatcher;
	}

	public FooterMatcher getOrCreateFooterMatcher() {
		if (footerMatcher == null) {
			footerMatcher = (FooterMatcher) XMLUtil.getSingleChild(this, FooterMatcher.TAG);
		}
		return footerMatcher;
	}


	public void setFooterMatcher(FooterMatcher footerMatcher) {
		this.footerMatcher = footerMatcher;
	}


	public ColumnMatcher(TTemplateList templateList) {
		super(TAG, templateList);
	}

	public boolean matches(String colHeader) {
		LOG.debug("NYI");
		return false;
	}

}
