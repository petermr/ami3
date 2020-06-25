package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTable;

import nu.xom.Element;

/**
 * 
 * @author pm286
 * 
<table frame="hsides" rules="groups">
<colgroup span="1">
<col align="left" span="1"/>
<col align="center" span="1"/>
...
</colgroup>
<thead>
<tr>
<td align="left" rowspan="1" colspan="1">Country</td>
<td align="left" rowspan="1" colspan="1">Earliest Report<xref ref-type="table-fn" rid="nt101">&#x0002a;</xref></td>
...
</tr>
</thead>
<tbody>
<tr>
<td align="left" rowspan="1" colspan="1">Borneo</td>
<td align="left" rowspan="1" colspan="1">1951</td>
...
</tr>
<tr>
<td align="left" rowspan="1" colspan="1">Burkina Faso</td>
<td align="left" rowspan="1" colspan="1">1981</td>
...
</tr>

</tbody>
</table>
 *
 */
public class JATSTableElement extends AbstractJATSHtmlElement implements IsBlock, IsFloat {

	private static final Logger LOG = LogManager.getLogger(JATSTableElement.class);
static String TAG = "table";
	
	public JATSTableElement(Element element) {
		super(element);
	}

	@Override
	public String directoryName() {
		return TAG;
	}

	/** creates a table
	 * @return HtmlTable
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlTable table = new HtmlTable();
		for (Element child : this.getChildElementList()) {
//			LOG.debug("tableChild: "+child.toXML());
			HtmlElement childHtml = ((JATSElement)child).createHTML();
			table.appendChild(childHtml);
		}
		return table;
	}


}
