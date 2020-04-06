package org.contentmine.ami.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIGrobidTool;
import org.contentmine.ami.tools.AMIOCRTool;
import org.contentmine.norma.pdf.GrobidRunner;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * test OCR.
 *
 * @author pm286
 */
public class AMIGROBIDTest {
	private static final Logger LOG = Logger.getLogger(AMIGROBIDTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	/**
	 * convert single (good) file
	 */
	@Test
	public void testGROBID() throws Exception {
		String args =
				"-t /Users/pm286/workspace/uclforest/dev/bowmann"
						+ " --inputname tei/"
						+ " --exe processFullText";
		new AMIGrobidTool().runCommands(args);
	}

	/**
	 * convert single (missing) file
	 */
	@Test
	public void testGROBIDDietrichson() throws Exception {
		String args =
				"-t /Users/pm286/workspace/uclforest/dev/dietrichson"
						+ " --inputname tei/"
						+ " --exe processFullText";
		new AMIGrobidTool().runCommands(args);
	}

	/**
	 * convert whole project
	 */
	@Test
	public void testGROBIDProject() throws Exception {
		String args =
				"-p /Users/pm286/workspace/uclforest/dev/"
						+ " --inputname tei/"
						+ " --exe processFullText";
		new AMIGrobidTool().runCommands(args);
	}

	/**
	 * Test default options.
	 */
	@Ignore
	@Test
	public void testGrobidNoOptions() throws Exception {
		String args = "-t " + System.getProperty("user.home") +
				" --exe processFullText";
		new AMIGrobidTool().runCommands(args);
	}

	/**
	 * Test help.
	 */
	@Test
	public void testGrobidHelp() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream old = System.out;
		System.setOut(new PrintStream(baos, true));
		try {
			new AMIGrobidTool().runCommands(new String[0]);
			String expected = String.format("" +
							"Usage: grobid [OPTIONS]%n" +
							"Description%n" +
							"===========%n" +
							"Runs grobid.%n" +
							"Internally, this uses:%n" +
							"${GROVID-JAVA-EXE} -jar ${GROVID-INSTALL-DIR}%n" +
							"/grobid-core/build/libs/grobid-core-${GROVID-VERSION}-onejar.jar \\%n" +
							"  -gH ${GROVID-INSTALL-DIR}/grobid-home \\%n" +
							"  -teiCoordinates \\%n" +
							"  -exe ${GROVID-EXE-OPTION}%n" +
							"%n" +
							"This is very slow as grobid has to boot each time but it only has to be done%n" +
							"once. We can set up a server if it becomes useful.%n" +
							"Options%n" +
							"=======%n" +
							"      --dryrun=<dryrun>      (A) for testing runs a single phase without%n" +
							"                               output, deletion or transformation.(NYI).%n" +
							"      --excludebase=<excludeBase>...%n" +
							"                             (A) exclude child files of cTree (only works with%n" +
							"                               --ctree). Currently must be explicit or with%n" +
							"                               trailing percent for truncated glob.%n" +
							"      --excludetree=<excludeTrees>...%n" +
							"                             (A) exclude the CTrees in the list. (only works%n" +
							"                               with --cproject). Currently must be explicit but%n" +
							"                               we'll add globbing later.%n" +
							"      --exe, --grobid-exe-option=<exeOption>%n" +
							"                             The value to pass to the Grobid `-exe` option.%n" +
							"                               Valid values: close, processFullText,%n" +
							"                               processHeader, processDate,%n" +
							"                               processAuthorsHeader, processAuthorsCitation,%n" +
							"                               processAffiliation, processRawReference,%n" +
							"                               processReferences, createTraining,%n" +
							"                               createTrainingMonograph, createTrainingBlank,%n" +
							"                               createTrainingCitationPatent,%n" +
							"                               processCitationPatentTEI,%n" +
							"                               processCitationPatentST36,%n" +
							"                               processCitationPatentTXT,%n" +
							"                               processCitationPatentPDF, processPDFAnnotation.%n" +
							"                               Default: processFullText%n" +
							"      --forcemake            (A) force 'make' regardless of file existence and%n" +
							"                               dates.%n" +
							"      --grobid-home=<PATH>   Optionally, specify the location of the%n" +
							"                               `grobid-home` directory. If not specified, the%n" +
							"                               value is derived from the Grobid install%n" +
							"                               directory.%n" +
							"      --grobid-install-dir=<PATH>%n" +
							"                             Optionally, specify the location where grobid is%n" +
							"                               installed. If not specified, the value is%n" +
							"                               derived from the Grobid version as follows:%n" +
							"                             System.getProperty(\"user.home\") +%n" +
							"                               \"/workspace/grobid/grobid-\" + <grobid-version>%n" +
							"      --grobid-jar=<PATH>    Optionally, specify the location of the Grobid%n" +
							"                               jar. If not specified, the value is derived from%n" +
							"                               the Grobid version and Grobid install directory.%n" +
							"      --grobid-java-exe=<PATH>%n" +
							"                             Optionally, specify the location of the java%n" +
							"                               executable to use to invoke Grobid. If not%n" +
							"                               specified, the java executable that runs ami is%n" +
							"                               used.%n" +
							"                             NOTE: Grobid supported version is Java 8. More%n" +
							"                               recent JVM version (like JVM 11) might lead to%n" +
							"                               issues.%n" +
							"                               Default: %s/bin/java%n" +
							"      --grobid-version=<X.Y.Z>%n" +
							"                             The grobid version to use.%n" +
							"                               Default: %s%n" +
							"  -h, --help                 Show this help message and exit.%n" +
							"  -i, --input=input          (A) input filename (no defaults)%n" +
							"      --includebase=<includeBase>...%n" +
							"                             (A) include child files of cTree (only works with%n" +
							"                               --ctree). Currently must be explicit or with%n" +
							"                               trailing percent for truncated glob.%n" +
							"      --includetree=<includeTrees>...%n" +
							"                             (A) include only the CTrees in the list. (only%n" +
							"                               works with --cproject). Currently must be%n" +
							"                               explicit but we'll add globbing later.%n" +
							"      --inputname=<inputBasename>%n" +
							"                             (A) User's basename for inputfiles (e.g.%n" +
							"                               foo/bar/<basename>.png) or directories. By%n" +
							"                               default this is often computed by AMI. However%n" +
							"                               some files will have variable names (e.g. output%n" +
							"                               of AMIImage) or from foreign sources or%n" +
							"                               applications%n" +
							"      --inputnamelist=<inputBasenameList>...%n" +
							"                             (A) list of inputnames; will iterate over them,%n" +
							"                               essentially compressing multiple commands into%n" +
							"                               one. Experimental%n" +
							"      --log4j=<log4j> <log4j>...%n" +
							"                             (A) format: <classname> <level>; sets logging%n" +
							"                               level of class, e.g.%n" +
							"                              org.contentmine.ami.lookups.WikipediaDictionary%n" +
							"                               INFO%n" +
							"      --logfile=<logfile>    (A) log file for each tree/file/image analyzed.%n" +
							"      --maxTrees=<maxTreeCount>%n" +
							"                             (A) quit after given number of trees; null means%n" +
							"                               infinite%n" +
							"  -o, --output=output        (A) output filename (no defaults)%n" +
							"      --oldstyle             (A) use oldstyle style of processing (project%n" +
							"                               based) for unconverted tools; new style is per%n" +
							"                               tree%n" +
							"      --outputname=<outputBasename>%n" +
							"                             (A) User's basename for outputfiles (e.g.%n" +
							"                               foo/bar/<basename>.png or directories. By%n" +
							"                               default this is computed by AMI. This allows%n" +
							"                               users to create their own variants, but they%n" +
							"                               won't always be known by default to%n" +
							"                               subsequentapplications%n" +
							"  -p, --cproject=CProject    (A) CProject (directory) to process. This can be%n" +
							"                               (a) a child directory of cwd (current working%n" +
							"                               directory (b) cwd itself (use -p .) or (c) an%n" +
							"                               absolute filename. No defaults. The cProject%n" +
							"                               name is the basename of the file.%n" +
							"      --rawfiletypes=<rawFileFormats>[,<rawFileFormats>...]...%n" +
							"                             (A) suffixes of included files (html, pdf, xml):%n" +
							"                               can be concatenated with commas%n" +
							"                               Default: []%n" +
							"      --subdirectorytype=<subdirectoryType>%n" +
							"                             (A) use subdirectory of cTree%n" +
							"  -t, --ctree[=CTree]        (A) CTree (directory) to process. This can be (a)%n" +
							"                               a child directory of cwd (current working%n" +
							"                               directory, usually cProject) (b) cwd itself,%n" +
							"                               usually cTree (use -t .) or (c) an absolute%n" +
							"                               filename. No defaults. The cTree name is the%n" +
							"                               basename of the file.%n" +
							"      --testString=<testString>%n" +
							"                             (A) String input for debuggingsemantics depend on%n" +
							"                               task%n" +
							"  -v, --verbose              (A) Specify multiple -v options to increase%n" +
							"                               verbosity.%n" +
							"                             For example, `-v -v -v` or `-vvv`. We map ERROR or%n" +
							"                               WARN -> 0 (i.e. always print), INFO -> 1(-v),%n" +
							"                               DEBUG->2 (-vv)%n" +
							"                               Default: []%n" +
							"  -V, --version              Print version information and exit.%n",
					System.getProperty("java.home"), GrobidRunner.GrobidOptions.GROBID_DEFAULT_VERSION);
			assertEquals(expected, baos.toString());
		} finally {
			System.setOut(old);
		}
	}
}
