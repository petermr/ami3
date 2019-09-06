package org.contentmine.norma;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.CHESRunner;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CProject;

public class Norma implements CHESRunner {

	private static final Logger LOG = Logger.getLogger(Norma.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String NORMA_OUTPUT_RESOURCE = NAConstants.NORMA_RESOURCE + "/" +"output";
	private DefaultArgProcessor argProcessor;
	public final static String MAKEPROJECT_PDF0 = "--project ";
	public final static String MAKEPROJECT_PDF1 = " --makeProject (\\1)/fulltext.pdf --fileFilter .*/(.*)\\.pdf";
	private CProject cProject;

	public static void main(String[] args) {
		Norma norma = new Norma();
		norma.run(args);
	}

	public void run(String[] args) {
		argProcessor = new NormaArgProcessor(args);
		argProcessor.runAndOutput();
	}

	public void run(String args) {
		args = args == null ? null : args.trim();
		argProcessor = new NormaArgProcessor(args.split("\\s+"));
		argProcessor.setRunner(this);
		argProcessor.runAndOutput();
	}

	public DefaultArgProcessor getArgProcessor() {
		return argProcessor;
	}
	
	public CProject getCProject() {
		return cProject;
	}
	public CProject makeProjectFromPDFs(File cProjectDir) {
		cProject = new CProject(cProjectDir);
		this.run(makeMakeProjectCommand(cProjectDir));
		cProject.getOrCreateCTreeList();
		return cProject;
	}

	private String makeMakeProjectCommand(File cProjectDir) {
		return MAKEPROJECT_PDF0 + cProjectDir + MAKEPROJECT_PDF1;
	}

	/** return current CProject.
	 * simple getter since cannot getOrCreate it since we need a directory associated with it.
	 * 
	 * @return
	 */
	public CProject getOrCreateCProject() {
		return cProject;
	}

	/** converts a projectDirectory to a project and the PDFs to SVG
	 * 
	 * @param projectDir
	 */
	public void convertRawPDFToProjectToSVG(File projectDir) {
		this.run("--project "+projectDir+" --makeProject (\\1)/fulltext.pdf --fileFilter .*\\/(.*)\\.pdf");
		this.run("--project " + projectDir + " --input fulltext.pdf "+ " --outputDir " + projectDir + " --transform pdf2svg ");
	}

	/** converts a projectDirectory to a project and the PDFs to SVG
	 * 
	 * @param projectDir
	 */
	public void convertRawPDFToProjectToCompactSVG(File projectDir) {
		convertRawPDFToProjectToSVG(projectDir);
		convertSVGToCompactSVG(projectDir);
	}

	/** converts single character PDF to compact PDF
	 * NOT YET TESTED
	 */
	
	public void convertSVGToCompactSVG(File projectDir) {
		this.run("--project " + projectDir + " --fileFilter .*/svg/fulltext-page(\\d+)\\.svg"+ 
		      " --outputDir " + "compact" + " --transform compactsvg ");
	}

	/** converts a projectDirectory to a project and the PDFs to SVG
	 * 
	 * @param projectDir
	 */
	public void convertRawTEIXMLToProject(File projectDir) {
		this.run("--project "+projectDir+" --makeProject (\\1)/fulltext.xml --fileFilter .*\\/(.*)\\.xml");
	//		new Norma().run("--project " + projectDir + " --input fulltext.tei.xml "+ " --outputDir " + projectDir + " --transform tei2html ");
	}

}
