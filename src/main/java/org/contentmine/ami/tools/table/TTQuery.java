package org.contentmine.ami.tools.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
public class TTQuery extends AbstractTTElement {
	private static final Logger LOG = Logger.getLogger(TTQuery.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String TAG = "query";
	
	public static String AND = "and";
	public static String NOT = "not";
	public static String OR = "or";
	
	public static String CASE_SENSITIVE = "case-sensitive";
	
	public TTQuery(TTemplateList templateList) {
		super(TAG, templateList);		
	}
	

}
