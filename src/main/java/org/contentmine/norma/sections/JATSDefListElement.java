package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSDefListElement extends JATSElement implements IsBlock  {

	/**
<def-list>
  <title>Abbreviations:</title>
  <def-item>
   <term>CCD</term>
   <def><p>Charge Coupled Device.</p></def>
  </def-item>
  <def-item>
   <term>CMOS</term>
   <def><p>Complementary Metal Oxide Semiconductor.</p></def>
  </def-item>
  <def-item>
   <term>LED</term>
   <def><p>Light Emitting Diode.</p></def>
  </def-item>
 </def-list>
 	 */
	public static String TAG = "def-list";

	public JATSDefListElement(Element element) {
		super(element);
	}
	
	
	

}
