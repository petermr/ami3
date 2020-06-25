package org.contentmine.ami.tools.table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
	<tableTemplate name="composition">
		<column name="compound" find="
		    [Cc]onstituent _OR
		    [Cc]ompound _OR
		    [Cc]omponent
		    ">
		    <find .../>
		    </column>
	</tableTemplate>
	
	NYI
	
 * @author pm286
 *
 */
public class QueryMatcher extends AbstractTTElement {

	private static final Logger LOG = LogManager.getLogger(QueryMatcher.class);
public static String TAG = "query";
	
	public static String AND = "and";
	public static String NOT = "not";
	public static String OR = "or";
	
	public static String CASE_SENSITIVE = "case-sensitive";
	
	private List<Pattern> andPatternList;
	private List<Pattern> notPatternList;
	private List<Pattern> orPatternList;
	private String mode;
	private String lookupTarget;
	
	public QueryMatcher(TTemplateList templateList) {
		super(TAG, templateList);
		init();
	}
	
	private void init() {
	}

	public List<Pattern> getOrCreateAndPatternList() {
		if (andPatternList == null) {
			andPatternList = new ArrayList<Pattern>();
		}
		return andPatternList;
	}
	
	public List<Pattern> getOrCreateNotPatternList() {
		if (notPatternList == null) {
			notPatternList = new ArrayList<Pattern>();
		}
		return notPatternList;
	}
	
	public List<Pattern> getOrCreateOrPatternList() {
		if (orPatternList == null) {
			orPatternList = new ArrayList<Pattern>();
		}
		return orPatternList;
	}
	
	

}
