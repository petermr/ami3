package org.contentmine.norma;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** manages convenience methods for (chained) Norma operations.
 * 
 * @author pm286
 *
 */
public class NormaRunner {
	private static final Logger LOG = Logger.getLogger(NormaRunner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public NormaRunner() {
		
	}
// OBSOLETE
	
//	/** converts a projectDirectory to a project and the PDFs to SVG
//	 * 
//	 * @param projectDir
//	 */
//	public void convertRawPDFToProjectToSVG(File projectDir) {
//		new Norma().run("--project "+projectDir+" --makeProject (\\1)/fulltext.pdf --fileFilter .*\\/(.*)\\.pdf");
//		new Norma().run("--project " + projectDir + " --input fulltext.pdf "+ " --outputDir " + projectDir + " --transform pdf2svg ");
//	}
//
//	/** converts a projectDirectory to a project and the PDFs to SVG
//	 * 
//	 * @param projectDir
//	 */
//	public void convertRawPDFToProjectToCompactSVG(File projectDir) {
//		convertRawPDFToProjectToSVG(projectDir);
//		new Norma().run("--project " + projectDir + " --fileFilter .*/svg/fulltext-page(\\d+)\\.svg"+ " --outputDir " + projectDir + " --transform compactsvg ");
//	}
//
//	/** converts a projectDirectory to a project and the PDFs to SVG
//	 * 
//	 * @param projectDir
//	 */
//	public void convertRawTEIXMLToProject(File projectDir) {
//		new Norma().run("--project "+projectDir+" --makeProject (\\1)/fulltext.xml --fileFilter .*\\/(.*)\\.xml");
//	//		new Norma().run("--project " + projectDir + " --input fulltext.tei.xml "+ " --outputDir " + projectDir + " --transform tei2html ");
//	}
}
