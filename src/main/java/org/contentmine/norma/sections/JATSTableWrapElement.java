package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

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

	private static final Logger LOG = Logger.getLogger(JATSFigElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	static String TAG = "table-wrap";
	
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

	
}
