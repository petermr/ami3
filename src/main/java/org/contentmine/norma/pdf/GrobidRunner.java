package org.contentmine.norma.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.norma.image.ocr.HOCRConverter;
import org.contentmine.norma.util.CommandRunner;

public class GrobidRunner extends CommandRunner {

	public final static Logger LOG = Logger.getLogger(GrobidRunner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum ExeOption {
		CLOSE("close"),
				
		PROCESS_FULL_TEXT("processFullText"),
		PROCESS_HEADER("processHeader"),
		PROCESS_DATE("processDate"),
		PROCESS_AUTHORS_HEADER("processAuthorsHeader"),
		PROCESS_AUTHORS_CITATION("processAuthorsCitation"),
		PROCESS_AFFILIATION("processAffiliation"),
		PROCESS_RAW_REFERENCE("processRawReference"),
		PROCESS_REFERENCES("processReferences"),
				
		CREATE_TRAINING("createTraining"),
		CREATE_TRAINING_MONOGRAPH("createTrainingMonograph"),
		CREATE_TRAINING_BLANK("createTrainingBlank"),
		CREATE_TRAINING_CITATION_PATENT("createTrainingCitationPatent"),
				
		PROCESS_CITATION_PATENT_TEI("processCitationPatentTEI"),
		PROCESS_CITATION_PATENT_ST36("processCitationPatentST36"),
		PROCESS_CITATION_PATENT_TXT("processCitationPatentTXT"),
		PROCESS_CITATION_PATENT_PDF("processCitationPatentPDF"),
		PROCESS_PDF_ANNOTATION("processPDFAnnotation"),
		;
		private String option;
		private ExeOption(String option) {
			this.option = option;
		}
		public static ExeOption getOption(String option) {
			for (ExeOption exeOption : values()) {
				if (exeOption.option.equals(option)) {
					return exeOption;
				}
			}
			return null;
		}

	}
	/**
java
 -jar
 /Users/pm286/workspace/grobid/grobid-0.5.3/grobid-core/build/libs/grobid-core-0.5.3-onejar.jar
 -gH /Users/pm286/workspace/grobid/grobid-0.5.3/grobid-home
 -teiCoordinates
 -exe processFullText	 */
	
	public final static String USER_HOME = System.getProperty("user.home");
	public static final String GROBID_VERSION = USER_HOME + "/" + "workspace/grobid/grobid-0.5.3";
	public static final String GROBID_JAR = GROBID_VERSION  + "/" + "grobid-core/build/libs/grobid-core-0.5.3-onejar.jar";
	public static final String JAVA = "/usr/bin/java";
	public static final String TEST_PROG = "/Users/pm286/workspace/cmdev/normami/target/appassembler/bin/ami-pdf";
	public static final String JAR = "-jar";
//	public final static String JAVA_GROBID = JAVA_JAR + GROBID_JAR;
	public final static String G_H = "-gH";
	public final static String GROBID_HOME = GROBID_VERSION + "/" + "grobid-home";
	public final static String D_IN = "-dIn";
	public final static String D_OUT = "-dOut";
	public final static String TEI_COORDINATES = "-teiCoordinates";
	public final static String EXE = "-exe";
	
	/**
	 exe options
	 
	close, 
	processFullText, processHeader, processDate, processAuthorsHeader, 
    processAuthorsCitation, processAffiliation, processRawReference, processReferences, 
	createTraining, createTrainingMonograph, createTrainingBlank, createTrainingCitationPatent, 
	processCitationPatentTEI, processCitationPatentST36, processCitationPatentTXT, processCitationPatentPDF, processPDFAnnotation
	 */

	public final static String CLOSE = "close";
	
    public final static String PROCESS_FULL_TEXT = "processFullText";
	public final static String PROCESS_HEADER = "processHeader";
	public final static String PROCESS_DATE = "processDate";
	public final static String PROCESS_AUTHORS_HEADER = "processAuthorsHeader";
	public final static String PROCESS_AUTHORS_CITATION = "processAuthorsCitation";
	public final static String PROCESS_AFFILIATION = "processAffiliation";
	public final static String PROCESS_RAW_REFERENCE = "processRawReference";
	public final static String PROCESS_REFERENCES = "processReferences";
	
	public final static String CREATE_TRAINING = "createTraining";
	public final static String CREATE_TRAINING_MONOGRAPH = "createTrainingMonograph";
	public final static String CREATE_TRAINING_BLANK = "createTrainingBlank";
	public final static String CREATE_TRAINING_CITATION_PATENT = "createTrainingCitationPatent";
	
	public final static String PROCESS_CITATION_PATENT_TEI = "processCitationPatentTEI";
	public final static String PROCESS_CITATION_PATENT_ST36 = "processCitationPatentST36";
	public final static String PROCESS_CITATION_PATENT_TXT = "processCitationPatentTXT";
	public final static String PROCESS_CITATION_PATENT_PDF = "processCitationPatentPDF";
	public final static String PROCESS_PDF_ANNOTATION = "processPDFAnnotation";

	private String option;

	public GrobidRunner() {
		setDefaults();
	}
	
	public void setExeOption(ExeOption exeOption) {
		this.option = exeOption == null ? null :exeOption.option;
	}
	
    /** converts PDF to Html.
     * relies on Grobid.
     * 
     * @param inputDir
     * @return HOCR.HTML file created (null if failed to create)
     * @throws IOException // if Tesseract not present
     * @throws InterruptedException ??
     */
    public void convertPDFToTEI(File inputDir, File outputDir, String option) {

    	outputDir.mkdirs();
		builder = new ProcessBuilder(
				getProgram(), 
				JAR,
				GROBID_JAR,
				G_H, GROBID_HOME,
				D_IN, inputDir.toString(), 
				D_OUT, outputDir.toString(),
				TEI_COORDINATES, 
				EXE, option
				);
        try {
			runBuilderAndCleanUp();
		} catch (Exception e) {
			throw new RuntimeException("Cannot run Grobid: ", e);
		}
    }

	protected String getProgram() {
		return JAVA; 
	}
	
}

