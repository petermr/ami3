package org.contentmine.ami.tools;

import org.junit.jupiter.api.Test;

/** demos of AMI, often including several chained commands.
 * 
 * @author pm286
 *
 */
public class AMIDemoTest extends AbstractAMITest {

	@Test
	/** extract bitmaps from PDFs and analyse them as Octrees.
	 * 
	 * NYI
	 */
	public void testOctree() {
		
		// Battery1 or CTree from Battery10

		String cmd =""
				+ "ami pdfbox ..."
				+ "filter ... remove images"
				+ "image ?"
				+ "pixel ..."
				;
				
		AMI.execute(cmd);
	}
}
