package org.contentmine.norma.sections;

import java.util.Arrays;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

/**
 *   <ref id='pntd.0001477-Olson1'>
<label>13</label>
<element-citation>
<person-group>
 <name>
  <surname>Olson</surname>
  <given-names>JG</given-names>
 </name>
 <name>
  <surname>Ksiazek</surname>
  <given-names>TG</given-names>
 </name>
 <name>
  <surname>Suhandiman</surname>
 </name>
 <name>
  <surname>Triwibowo</surname>
 </name>
</person-group>
<year>1981</year>
<article-title>Zika virus, a cause  ... </article-title>
<source>Trans R Soc Trop Med ... </source>
<volume>75</volume>
<fpage>389</fpage>
<lpage>393</lpage>
<pub-id>6275577</pub-id>
</element-citation>
</ref>
*/
public class JATSRefElement extends JATSElement implements IsBlock {

	static final Logger LOG = Logger.getLogger(JATSRefElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String TAG = "ref";
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
			JATSSpanFactory.LABEL,
			JATSDivFactory.ELEMENT_CITATION,
	});
	
	private JATSElementCitationElement elementCitation;
	private String label;

	public JATSRefElement(Element element) {
		super(element);
	}
	
	public String getPMID() {
		return elementCitation == null ? null : elementCitation.getPMID();
	}

	public String getPMCID() {
		return elementCitation == null ? null : elementCitation.getPMCID();
	}
	
	protected void applyNonXMLSemantics() {
		label = getSingleChildValue(JATSSpanFactory.LABEL);
		elementCitation = (JATSElementCitationElement) getSingleChild(JATSElementCitationElement.TAG);
	}

	/**
	 *   <ref id='pntd.0001477-Olson1'>
   <label>13</label>
   <element-citation>
    <person-group>
     <name>
      <surname>Olson</surname>
      <given-names>JG</given-names>
     </name>
     <name>
      <surname>Triwibowo</surname>
     </name>
    </person-group>
    <year>1981</year>
    <article-title>Zika virus, a cause  ... </article-title>
    <source>Trans R Soc Trop Med ... </source>
    <volume>75</volume>
    <fpage>389</fpage>
    <lpage>393</lpage>
    <pub-id>6275577</pub-id>
   </element-citation>
  </ref>

	 */
	@Override
	public String debugString(int level) {
		StringBuilder sb = new StringBuilder();
		addNonNullDebugString(sb, getLabel(), level);
		addNonNullDebugString(sb, getElementCitation(), level);
		return sb.toString();
	}

	private JATSElementCitationElement getElementCitation() {
		return (JATSElementCitationElement) getSingleChild(JATSElementCitationElement.TAG);
	}
	
	private JATSLabelElement getLabel() {
		return (JATSLabelElement) getSingleChild(JATSLabelElement.TAG);
	}
	
	

}
