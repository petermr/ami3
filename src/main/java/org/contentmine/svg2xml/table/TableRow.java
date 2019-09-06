package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.svg.SVGElement;

import nu.xom.Element;
import nu.xom.Nodes;

public class TableRow extends TableChunk {

	private final static Logger LOG = Logger.getLogger(TableRow.class);
	private List<TableCell> cellList;
	
	public TableRow(RealRangeArray horizontalMask, RealRangeArray verticalMask) {
		super(horizontalMask, null);
	}
	
	public TableRow() {
		super();
	}

	public List<TableCell> createCells() {
		setCellList(new ArrayList<TableCell>());
		return getCellList();
	}

	public void createAndAnalyzeCells(RealRangeArray horizontalMask) {
		createCells();
		for (RealRange range : horizontalMask) {
			TableCell cell = new TableCell();
			getCellList().add(cell);
			for (SVGElement element : elementList) {
				RealRange elemRange = element.getBoundingBox().getXRange();
				if (range.includes(elemRange)) {
					cell.add(element);
				}
			}
		}
	}

	public List<TableCell> getCellList() {
		return cellList;
	}

	public void setCellList(List<TableCell> cellList) {
		this.cellList = cellList;
	}

	public HtmlElement createHtmlElement() {
		HtmlTr tr = new HtmlTr();
		for (TableCell cell : cellList) {
			tr.appendChild(cell.createHtmlElement());
		}
		return tr;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("{");
		for (TableCell cell : cellList) {
			sb.append("{"+cell.toString()+"}");
		}
		sb.append("}");
		return sb.toString();
	}

	public static HtmlElement convertBodyHeader(AbstractCMElement bodyOneTr) {
		Nodes nodes = bodyOneTr.query(".//*[local-name()='td']");
		HtmlElement tr = new HtmlTr();
		for (int i = 0; i < nodes.size();i++) {
			HtmlTh th = new HtmlTh();
			tr.appendChild(th);
			XMLUtil.transferChildren((Element) nodes.get(i), th);
		}
		return tr;
	}
}
