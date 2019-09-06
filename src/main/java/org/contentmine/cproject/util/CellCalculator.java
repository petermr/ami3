package org.contentmine.cproject.util;

import java.util.List;

import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTr;

public interface CellCalculator {

	void addCellValues(List<CellRenderer> columnHeadingList, HtmlTr htmlTr, int iRow);

	HtmlElement createCellContents(int iRow, int iCol);

}
