package org.contentmine.ami.tools.table;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMITableTool;
import org.contentmine.cproject.util.RectTabColumn;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Util;
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
	private static final Logger LOG = LogManager.getLogger(ColumnMatcher.class);
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
			cellMatcher.getOrCreateQueryTool();
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

	public RectTabColumn validateAndAddColumn(String colHeader, RectTabColumn column) {
		String colName = getName();
		RectTabColumn rectangularCol = RectTabColumn.createColumn(column.getValues(), colName+"=("+colHeader+")");
		TQueryTool cellQueryTool = getOrCreateCellMatcher().getOrCreateQueryTool();
		IntArray matchCells = cellQueryTool.match(rectangularCol.getValues());
		double fract = Util.format((100. * matchCells.absSumAllElements()) / matchCells.size(), 1);
		System.out.println("      column: "+colName+" => "+colHeader + "; "+fract);
		LOG.debug("MATCHED "+cellQueryTool.getMatchedValues());
		LOG.debug("UNMATCHED "+cellQueryTool.getUnmatchedValues());
		return rectangularCol;
	}

	public TQueryTool findFooterQueryTool(String colHeader, RectTabColumn column) {
		FooterMatcher footerMatcher = getOrCreateFooterMatcher();
		TQueryTool footerQueryTool = null;
		if (footerMatcher != null) {
			footerQueryTool = footerMatcher.getOrCreateQueryTool();
			IntArray matchCells = footerQueryTool.match(column.getValues());
			LOG.debug("FOOTER matches "+matchCells);
		}
		return footerQueryTool;
	}

	/**
	 * 
	 * 	<column name="compound" case="insensitive" id="comp.col.comp">
		    <title id="comp.col.comp.tit">
			    <query id="comp.col.comp.tit.q">
				    constituent OR
				    compound OR
				    component
				    NOT class
			    </query>
		    </title>
			<cell id="comp.col.comp.cell">
	  		  <query id="comp.col.comp.cell.q1">^@CHEMICAL@$</query>
	  		  <query id="comp.col.comp.cell.q2" mode="lookup">@COMPOUND_DICT@</query>
			</cell>
		</column>
	
	 * @param subRectTable
	 * @param columnList
	 */
	public int createColumnsAndAddToSubTable(
			RectangularTable subRectTable, List<RectTabColumn> columnList, int footerStart) {
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			RectTabColumn column = columnList.get(jcol);
			if (column == null) {
				LOG.debug("null column");
			} else {
				String colHeader = column.getHeader();
				TQueryTool columnQueryTool = getOrCreateTitleMatcher().getOrCreateQueryTool();
				if (columnQueryTool.matches(colHeader)) {
					RectTabColumn rectangularCol = validateAndAddColumn(colHeader, column);
					subRectTable.addColumn(rectangularCol);
					footerStart = updateFooterStart(footerStart, column, colHeader);
				}
			}
		}
		return footerStart;
	}

	private int updateFooterStart(int footerStart, RectTabColumn column, String colHeader) {
		TQueryTool footerQueryTool = findFooterQueryTool(colHeader, column);
		if (footerQueryTool != null && footerStart != AMITableTool.INCONSISTENT_FOOTER) {
			int indexOfFirstMatch = footerQueryTool.getMatchIntArray().indexOfFirst(AMITableTool.FIRST_MATCH);
			if (indexOfFirstMatch == AMITableTool.NOT_FOUND) {
				// skip
			} else if (footerStart == AMITableTool.NO_FOOTER) {
				footerStart = indexOfFirstMatch;
			} else if (footerStart != indexOfFirstMatch) {
				LOG.error("inconsistent footer start: "+indexOfFirstMatch+" != "+footerStart);
				// this will skip the rest
				footerStart = AMITableTool.INCONSISTENT_FOOTER;
			}
		}
		return footerStart;
	}

}
