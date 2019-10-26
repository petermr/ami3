package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSCountsElement extends JATSElement implements IsBlock {

	/**
<counts>
  <fig-count count="1"/>
  <table-count count="7"/>
  <equation-count count="141"/>
  <ref-count count="163"/>
  <page-count count="151"/>
 </counts>	 */
	public static String TAG = "counts";

	public JATSCountsElement(Element element) {
		super(element);
	}
	
	
	

}
