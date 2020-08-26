package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.Util;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * cleans some of all of the project.
 *
 * @author pm286
 */
@Command(
		name = "files",
		description = {
				"Carries out file operations (copy, delete, etc.) on CProject and CTrees.",
				"Experimental. May replace `clean`. Accepts explicit paths or regular expressions."
				+ "",
				"${COMMAND-FULL-NAME} -p /Users/pm286/workspace/tigr2ess --dir results cooccurrence%n"
						+ "    deletes subdirectories results/ and cooccurrence/ in projcts tigr2ess",
		})
public class AMIFilesTool extends AbstractAMITool {
	private static final Logger LOG = LogManager.getLogger(AMIFilesTool.class);


//	@Parameters(arity = "1", description = "Files to process. Glob patterns are supported.")
//	private String[] files;

	
	private static final String _DELETE = "_delete";

    // FILTER OPTIONS

    @Option(names = {"--copydir"},
            description = "copy directory to output directory. Requires --input and --output")
	private boolean copydir = false;

	/**
	 * used by some non-picocli calls
	 *
	 * @param cProject
	 */
	public AMIFilesTool(CProject cProject) {
		this.cProject = cProject;
	}

	public AMIFilesTool() {
	}

	public static void main(String[] args) throws Exception {
		AMIFilesTool amiFileTool = new AMIFilesTool();
		amiFileTool.runCommands(args);
	}

	@Override
	protected void parseSpecifics() {
		super.parseSpecifics();
//		LOG.info("fileGlobs     {}", Util.toStringList(files));
	}

	@Override
	protected void runSpecifics() {
		if (copydir) {
			copyDir();
		}
	}

	private void copyDir() {
		if (input() == null) {
			throw new RuntimeException("must give inpur dir to copy");
		}
		if (output() == null) {
			throw new RuntimeException("must give output directory for copy");
		}
		CMineTestFixtures.cleanAndCopyDir(new File(input()), new File(output()));
	}



}
