package org.contentmine.norma.sections;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlLabel;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTfoot;

import nu.xom.Element;

/**
 * 
 * @author pm286
 * 
 * <table-wrap id="pntd-0001477-t001" position="float">
<object-id pub-id-type="doi">10.1371/journal.pntd.0001477.t001</object-id>
<label>Table 1</label>
<caption>
<title>Probable distribution of Zika virus based on virus isolation and seroprevalence.</title>
</caption>
<alternatives>
<graphic id="pntd-0001477-t001-1" xlink:href="pntd.0001477.t001"/>
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
</alternatives>
<table-wrap-foot>
<fn id="nt101">
<label/>
<p>&#x0002a;Earliest report, indicates either the first virus isolation or the first report of seroprevalence.</p>
</fn>
<fn id="nt102">
<p>
<bold>&#x02020;:</bold> Seroprevalence was either determined by one or more of the following methods: Haemagglution inhibition, neutralization, complement-fixation, IgG and/or IgM ELISA. Of note, it is possible due to antigenic cross-reactivity among flaviviruses that seropostive individuals may have been previously exposed to one or more flaviviruses and not to Zika virus.</p>
</fn>
<fn id="nt103">
<p>
<bold>&#x02021;:</bold> Viral RNA sequenced from four patients (Lanciotti et al. 2008).</p>
</fn>
</table-wrap-foot>
</table-wrap>
 *
 */
public class JATSTableWrapElement extends JATSElement implements IsBlock, IsFloat, HasTitle {

	private static final Logger LOG = LogManager.getLogger(JATSTableWrapElement.class);
static String TAG = "table-wrap";
	private HtmlTable htmlTable;
	
	public JATSTableWrapElement(Element element) {
		super(element);
	}
	
	public String debugString(int level) {
		String caption = XMLUtil.getSingleValue(this, 
				"./*[local-name()='caption']/*[local-name()='title']");
		return caption == null ? "" : "[TABLE: "+caption+"]";
	}

	@Override
	public String directoryName() {
		return TAG;
	}

	@Override
	public String generateTitle() {
		return this.getSingleChildValue(JATSLabelElement.TAG);
	}

	/** creates a tfoot
	 * @return HtmlTFoot
	 */
	@Override
	public HtmlElement createHTML() {
		htmlTable = null;
		HtmlTfoot tfoot = null;

		List<Element> elementList = this.getChildElementList();
		List<JATSTableElement> tableList = createTableAndNonTableLists(elementList);
		if (tableList.size() == 0) {
			LOG.error("no tables found");
		} else if (tableList.size() > 1) {
			LOG.error("found "+tableList.size()+" tables");
		} else {
			JATSTableElement jatsTable = (JATSTableElement) tableList.get(0);
			createTableAndAddFieldsToCaption(elementList, jatsTable);			
		}
		return htmlTable;
	}

	private void createTableAndAddFieldsToCaption(List<Element> elementList, JATSTableElement jatsTable) {
		HtmlTfoot tfoot;
		htmlTable = (HtmlTable) jatsTable.createHTML();
		HtmlLabel label = null;
		HtmlCaption caption = null;
		HtmlElement objectId = null;
		HtmlDiv permissions = null;
		HtmlDiv altText = null;
		for (Element nonTableChild : elementList) {
			if (nonTableChild instanceof JATSTableWrapFootElement) {
				HtmlElement tableWrapFootChild = ((JATSTableWrapFootElement)nonTableChild).createHTML();
				tfoot = htmlTable.getOrCreateTfoot();
				XMLUtil.copyAttributes(tableWrapFootChild, tfoot);
				XMLUtil.transferChildren(tableWrapFootChild, tfoot);
			} else if (nonTableChild instanceof JATSCaptionElement) {
				caption = (HtmlCaption) ((JATSCaptionElement)nonTableChild).deepCopyAndTransform(new HtmlCaption());
			} else if (nonTableChild instanceof JATSLabelElement) {
				label = (HtmlLabel) ((JATSLabelElement)nonTableChild).deepCopyAndTransform(new HtmlLabel());
			} else if (nonTableChild instanceof JATSObjectIdElement) {
				objectId = (HtmlSpan) ((JATSObjectIdElement)nonTableChild).deepCopyAndTransform(new HtmlSpan());
			} else if (nonTableChild instanceof JATSPermissionsElement) {
				permissions = (HtmlDiv) ((JATSElement)nonTableChild).deepCopyAndTransform(new HtmlDiv());
			} else if (nonTableChild instanceof JATSAltTextElement) {
				altText = (HtmlDiv) ((JATSElement)nonTableChild).deepCopyAndTransform(new HtmlDiv());
			} else if (nonTableChild instanceof JATSAlternativesElement) {
				// already dealt with
			} else {
				LOG.debug("unrecognized element in jats table-wrap "+nonTableChild.getLocalName());
			}
		}
		if (caption == null) {
			caption = new HtmlCaption();
		}
		htmlTable.insertChild(caption, 0);
		if (label != null) {
			caption.insertChild(label, 0);
		}
	}

	private List<JATSTableElement> createTableAndNonTableLists(List<Element> elementList) {
		int size = elementList.size();
		List<JATSTableElement> tableList = new ArrayList<>();
		for (int i = size - 1; i >= 0; i--) {
			Element childElement = elementList.get(i);
			JATSTableElement tableElement = null;
			if (childElement instanceof JATSTableElement) {
				tableElement = (JATSTableElement) childElement;
			} else if (childElement instanceof JATSAlternativesElement) {
				tableElement = (JATSTableElement) XMLUtil.getSingleChild(childElement, JATSTableElement.TAG);
			}
			if (tableElement != null) {
				tableList.add(tableElement);
				elementList.remove(tableElement);
//				LOG.debug("found table: ");
			}
		}
		return tableList;
	}
}
