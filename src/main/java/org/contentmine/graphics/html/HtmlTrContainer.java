package org.contentmine.graphics.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/** class abstracting Thead and Tbody (and TFooter) functionality
 * supports rowspans and colspans
 * @author pm286
 *
 */
public abstract class HtmlTrContainer extends HtmlElement {
	private static final Logger LOG = Logger.getLogger(HtmlTrContainer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	private static final String COLOR = {
//			"fdd", ""
//	}
	private static final String ID_SEP = "-";
	
	private RectangularTable idTable;
	private List<HtmlTr> childTrList;
	private IntArray totalColspans;

	private Map<String, HtmlTCell> tcellByIdStart;
	private HtmlTable headerTable;

	protected HtmlTrContainer(String tag) {
		super(tag);
	}

	public List<HtmlTr> getChildTrs() {
	    List<HtmlTr> rowList = new ArrayList<HtmlTr>();
	    List<HtmlElement> rows = getChildElements(this, HtmlTr.TAG);
	    for (HtmlElement el : rows) {
	        rowList.add((HtmlTr) el);
	    }
	    return rowList;
	}

	public HtmlTable denormalizeSpans() {
		createIdTable();
		headerTable = createHeaderTable();
		return headerTable;
	}

	private void createIdTable() {
		childTrList = this.getChildTrs();
		totalColspans = getColspanArray(childTrList);
		if (totalColspans.size() > 0) {
			idTable = RectangularTable.createNullFilledTable(childTrList.size(), totalColspans.getMax());
			for (int irow = 0; irow < childTrList.size(); irow++) {
				addRow(irow);
			}
		} else {
			LOG.warn("No COLSPANS");
		}
	}

	private void addRow(int irow) {
		HtmlTr htmlTr = childTrList.get(irow);
		List<HtmlTCell> tcellList = htmlTr.getTCellChildren();
		int jcol = 0;
		int icell = 0;
		while (icell < tcellList.size()) {
			HtmlTCell cell = tcellList.get(icell);
			int colspan = cell.getColspan();
			int rowspan = cell.getRowspan();
			String cellij = idTable.getCellValue(irow, jcol);
			String id = null;
			int jcol0 = jcol;
			if (cellij != null) {
				// skip filled cell (from previous rowspan)
				jcol++;
			} else if (rowspan > 1) {
				// foreach colspan ...
				for (int ic = 0; ic < colspan; ic++) {
					// fill cells downwards
					for (int ir = 0; ir < rowspan; ir++) {
						id = createColumnId(irow, jcol0, ir, ic);
						idTable.setCellValue(irow + ir, jcol, id);
					}
					jcol++;
				}
				icell++;
			} else {
				// fill cells rightwards, includes colspan=1
				for (int ic = 0; ic < colspan; ic++) {
					id = createRowId(irow, jcol, colspan, jcol0, ic);
					idTable.setCellValue(irow, jcol, id);
					jcol++;
				}
				icell++;
			}
			if (id != null) {
				cell.setId(id);
			}
		}
	}

	private String createRowId(int irow, int jcol, int colspan, int jcol0, int ic) {
		return irow+ID_SEP+(colspan == 1 ? ""+jcol+ID_SEP+"." : jcol0+ID_SEP+(char) ('a' + ic));
	}

	private String createColumnId(int irow, int jcol, int ir, int ic) {
		return irow+ID_SEP+jcol+ID_SEP+(char) ('A' + ir)+ID_SEP+(char) ('a' + ic);
	}

	private HtmlTable createHeaderTable() {
		createTcellByIdStart();
		createTcellColourMap();
		createHeaderTable0();
		return headerTable;
	}

	private void createTcellColourMap() {
		Set<String> idStarts = tcellByIdStart.keySet();
		int ncolor = idStarts.size();
		
	}

	private void createHeaderTable0() {
		headerTable = new HtmlTable();
		if (idTable != null) {
			for (List<String> row : idTable.getRows()) {
				HtmlTr tr = new HtmlTr();
				headerTable.appendChild(tr);
				addIdsToRow(row, tr);
			}
		}
	}

	private void addIdsToRow(List<String> row, HtmlTr tr) {
		for (String id : row) {
			HtmlTCell tcell = splitAndGetTcell(id);
			if (tcell == null) {
				LOG.error("null cell: "+id);
				tr.appendChild(new HtmlTd("null "+id));
			} else {
				String id1 = tcell.getId();
				String val = tcell.getValue();
				HtmlTh th = createStyledTh(id1, val);
				tr.appendChild(th);
				createStyledThId(id1, th);
				th.appendChild(new HtmlBr());
				HtmlSpan valSpan = createStyledThVal(val);
				th.appendChild(valSpan);
			}
		}
	}

	private HtmlSpan createStyledThVal(String val) {
		HtmlSpan span2 = new HtmlSpan(val.substring(0, Math.min(15, val.length())));
		span2.setStyle("font-weight:bold;");
		return span2;
	}

	private void createStyledThId(String id1, HtmlTh th) {
		HtmlSpan span = new HtmlSpan(id1);
		span.setStyle("font-size:50%");
		th.appendChild(span);
	}

	private HtmlTh createStyledTh(String id1, String val) {
		HtmlTh th = new HtmlTh();
		th.setTitle(id1+"; "+val);
		th.setStyle("border-width:1px;border-style:dotted;background-color:pink;");
		return th;
	}

	private HtmlTCell splitAndGetTcell(String id) {
		HtmlTCell tcell = tcellByIdStart.get(getIdStart(id));
		if (tcell == null) {
			LOG.error("null tcell "+getIdStart(id)+": "+tcellByIdStart);
		}
		return tcell;
	}

	private String getIdStart(String id) {
		String[] ids = id.split(ID_SEP);
		return ids[0] + ID_SEP + ids[1];
	}

	private void createTcellByIdStart() {
		tcellByIdStart = new HashMap<>();
		List<HtmlTCell> tcellList = new ArrayList<>();
		List<Element> elemList = XMLUtil.getQueryElements(this,
				".//*[local-name()='"+HtmlTh.TAG+"' or local-name()='"+HtmlTd.TAG+"']");
		for (Element elem : elemList) {
			tcellList.add((HtmlTCell)elem);
		}
		for (HtmlTCell tcell : tcellList) {
			String idStart = getIdStart(tcell.getId());
			tcellByIdStart.put(idStart, tcell);
		}
	}

	private IntArray getColspanArray(List<HtmlTr> trList) {
		IntArray colspanArray = new IntArray();
		for (int irow = 0; irow < trList.size(); irow++) {
			HtmlTr row = trList.get(irow);
			row.addDefaultRowColspans();
			int colspan = row.getTotalColspan();
			colspanArray.addElement(colspan);
		}
		return colspanArray;
	}

	public HtmlTable getDenormalizedTable() {
		return headerTable;
	}

}
