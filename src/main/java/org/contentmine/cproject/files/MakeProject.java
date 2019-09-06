package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** runs the CProject "makeProject" command with default files and regex.
 * 
 * @author pm286
 *
 */
public class MakeProject {
	private static final Logger LOG = Logger.getLogger(MakeProject.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			help();
		} else if (args.length > 1) {
			LOG.debug("only one arg allowed");
			help();
		} else {
			// try absolute filename
			makeProject(new File(args[0]));
		}
	}

	/** makes project from file
	 * possible entry point
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void makeProject(File file) throws IOException {
		String filename = file.getAbsolutePath();
		String command = " --project "+ file + CProject.MAKE_PROJECT_PDF;
		LOG.debug(">> "+command);
		if (!checkDirectory(file)) {
			file = new File(".", filename);
		}
		if (checkDirectory(file)) {
			LOG.debug("dir "+file.getCanonicalPath());
			CProject cProject = new CProject(file);
			command = " --project "+ file + CProject.MAKE_PROJECT_PDF;
			LOG.debug(">> "+command);
			cProject.run(command);
		} else {
			LOG.error("cannot find absolute or relative file: "+filename);
		}
	}

	private static boolean checkDirectory(File dir) {
		boolean exists = true;
		if (!dir.exists() || !dir.isDirectory()) {
			LOG.debug("Cannot find directory: "+dir+ "( give full filename or relative to current Dir ");
			exists = false;
		}
		return exists;
	}

	private static void help() {
		System.err.println(" makeProject  makes project from a directory of PDFs or other files");
		System.err.println("    makeProject <dirname> ");
	}
}
