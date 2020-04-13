package org.contentmine.norma.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.norma.util.CommandRunner;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GrobidRunner extends CommandRunner {

	public final static Logger LOG = Logger.getLogger(GrobidRunner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/**
	 exe options

	 close,
	 processFullText, processHeader, processDate, processAuthorsHeader,
	 processAuthorsCitation, processAffiliation, processRawReference, processReferences,
	 createTraining, createTrainingMonograph, createTrainingBlank, createTrainingCitationPatent,
	 processCitationPatentTEI, processCitationPatentST36, processCitationPatentTXT, processCitationPatentPDF, processPDFAnnotation
	 */
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
			throw new IllegalArgumentException("Invalid option: '" + option + "'. Valid values are: " + Arrays.toString(values()));
		}

		@Override
		public String toString() {
			return option;
		}
	}

	public static class GrobidOptions {
		public static final String GROBID_DEFAULT_VERSION = "0.5.3";
		private static final String GROBID_VERSION = System.getProperty("grobid.version", GROBID_DEFAULT_VERSION);
		private final static String JAVA_DEFAULT_EXE = System.getProperty("java.home") + "/bin/java";

		@Option(names = {"-G", "--grobid-version"}, paramLabel = "<X.Y.Z>",
				description = "The grobid version to use.")
		String grobidVersion = GROBID_VERSION;

		@Option(names = {"-g", "--grobid-install-dir"}, paramLabel = "<PATH>",
				description = {
						"Optionally, specify the location where grobid is installed. " +
								"If not specified, the value is derived from the Grobid version as follows:",
						"System.getProperty(\"user.home\") + \"/workspace/grobid/grobid-\" + <grobid-version>"
				})
		String grobidInstallLocation;

		@Option(names = {"-X", "--exe", "--grobid-exe-option"},
				defaultValue = "processFullText",
				description = {
						"The value to pass to the Grobid `-exe` option. Valid values: ${COMPLETION-CANDIDATES}.",
				})
		ExeOption exeOption;

		@Option(names = {"--grobid-jar"}, paramLabel = "<PATH>",
				description = "Optionally, specify the location of the Grobid jar. " +
						"If not specified, the value is derived from the Grobid version and Grobid install directory.")
		String grobidJarLocation;

		@Option(names = {"--grobid-home"}, paramLabel = "<PATH>",
				description = "Optionally, specify the location of the `grobid-home` directory. " +
						"If not specified, the value is derived from the Grobid install directory.")
		String grobidHomeLocation;

		@Option(names = {"--grobid-java-exe"}, paramLabel = "<PATH>",
				description = {
						"Optionally, specify the location of the java executable to use to invoke Grobid. " +
								"If not specified, the java executable that runs ami is used.",
						"NOTE: Grobid supported version is Java 8. More recent JVM version (like JVM 11) might lead to issues."
				})
		String grobidJavaExeLocation = JAVA_DEFAULT_EXE;

		public String getJavaProgram() {
			return grobidJavaExeLocation;
		}

		public String getGrobidVersion() {
			return grobidVersion;
		}

		String getGrobidInstallLocation() {
			if (grobidInstallLocation != null) {
				return grobidInstallLocation;
			}
			return System.getProperty("user.home") + "/workspace/grobid/grobid-" + getGrobidVersion();
		}

		public String getGrobidJarPath() {
			if (grobidJarLocation != null) {
				return grobidJarLocation;
			}
			return String.format("%s/grobid-core/build/libs/grobid-core-%s-onejar.jar",
					getGrobidInstallLocation(), getGrobidVersion());
		}

		public String getGrobidHome() {
			if (grobidHomeLocation != null) {
				return grobidHomeLocation;
			}
			return getGrobidInstallLocation() + "/grobid-home";
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("grobid-version=").append(getGrobidVersion());
			if (grobidInstallLocation != null) {
				sb.append(", grobid-install-dir=").append(grobidInstallLocation);
			}
			if (grobidJarLocation != null) {
				sb.append(", grobid-jar=").append(grobidJarLocation);
			}
			if (grobidHomeLocation != null) {
				sb.append(", grobid-home=").append(grobidHomeLocation);
			}
			if (grobidJavaExeLocation != null) {
				sb.append(", grobid-java-exe=").append(grobidJavaExeLocation);
			}
			return sb.toString();
		}
	}

	private static final String DEFAULT_TEST_PROG = "/Users/pm286/workspace/cmdev/normami/target/appassembler/bin/ami-pdf";
	public static final String TEST_PROG = System.getProperty("ami.grobid.test", DEFAULT_TEST_PROG);

	private static final String OPTION_JAR = "-jar";
	private final static String OPTION_G_H = "-gH";
	private final static String OPTION_D_IN = "-dIn";
	private final static String OPTION_D_OUT = "-dOut";
	private final static String OPTION_TEI_COORDINATES = "-teiCoordinates";
	private final static String OPTION_EXE = "-exe";

	private final GrobidOptions grobidOptions;

	public GrobidRunner() {
		this(new GrobidOptions());
	}

	public GrobidRunner(GrobidOptions grobidOptions) {
		this.grobidOptions = Objects.requireNonNull(grobidOptions, "GrobidOptions");
		setDefaults();
	}

	public void setExeOption(ExeOption exeOption) {
		grobidOptions.exeOption = exeOption;
	}
	
    /** converts PDF to Html.
     * relies on Grobid.
     * 
     * @param inputDir
     * @return HOCR.HTML file created (null if failed to create)
     * @throws IOException // if Tesseract not present
     * @throws InterruptedException ??
     */
    public void convertPDFToTEI(File inputDir, File outputDir) {

    	outputDir.mkdirs();
		builder = new ProcessBuilder(
				getProgram(),
				OPTION_JAR, grobidOptions.getGrobidJarPath(),
				OPTION_G_H, grobidOptions.getGrobidHome(),
				OPTION_D_IN, inputDir.toString(),
				OPTION_D_OUT, outputDir.toString(),
				OPTION_TEI_COORDINATES,
				OPTION_EXE, grobidOptions.exeOption.option
				);
        try {
        	String dir = builder.directory() == null ? System.getProperty("user.dir") : builder.directory().getAbsolutePath();
        	LOG.debug(String.format("Running program %s in %s%n", builder.command(), dir));
			runBuilderAndCleanUp();
		} catch (Exception e) {
			throw new RuntimeException("Cannot run Grobid: ", e);
		}
    }

	@Override
	protected String getProgram() {
		return grobidOptions.getJavaProgram();
	}

}

