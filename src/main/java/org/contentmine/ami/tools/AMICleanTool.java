package org.contentmine.ami.tools;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.CMFileUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * cleans some of all of the project.
 *
 * @author pm286
 */
@Command(
		name = "clean",
		description = {
				"Cleans specific files or directories in project.",
				"Accepts explicit paths or regular expressions.",
				"${COMMAND-FULL-NAME} -p /Users/pm286/workspace/tigr2ess --dir results cooccurrence%n"
						+ "    deletes subdirectories results/ and cooccurrence/ in projcts tigr2ess",
				"${COMMAND-FULL-NAME} -p /Users/pm286/workspace/tigr2ess --file commonest.dataTables.html\\%n"
						+ "           count.dataTables.html entries.dataTables.html full.dataTables.html%n"
						+ "    deletes 4 files by name%n"
		})
public class AMICleanTool extends AbstractAMITool {
	private static final Logger LOG = LogManager.getLogger(AMICleanTool.class);

	public enum Cleaner {
		FULLTEXT_HTML("fulltext.html", "f", "remove fulltext.html (probably not recoverable without redownload)"),
		FULLTEXT_PDF("fulltext.pdf", "f", "remove fulltext.pdf (probably not recoverable without redownload)"),
		FULLTEXT_XML("fulltext.xml", "f", "remove fulltext.xml (probably not recoverable without redownload)"),
		PDFIMAGES("pdfimages", "d", "remove pdfimages/ directory and contents (created by parsing fulltext.pdf)"),
		RAWIMAGES("rawimages", "d", "remove rawimages/ directory and contents (probably directly downloaded)"),
		SCHOLARLY_HTML("scholarly.html", "f", "remove scholarly.html (created by parsing)"),
		SVGDIR("svg", "d", "remove svg/ directory and contents (created by parsing fulltext.pdf)"),
		;
		public String file;
		public String type;
		public String message;

		private Cleaner(String file, String type, String message) {
			this.file = file;
			this.type = type;
			this.message = message;
		}

		public void help() {
			LOG.debug(DebugPrint.MARKER, file + ": " + message);
		}

		public void clean(CTree cTree, String arg) {
		}

		public boolean matches(String arg) {
			return this.file.equals(arg);
		}
	}

	@Parameters(arity = "1", description = "Files to delete. Glob patterns are supported.")
	private String[] files;

	/**
	 * used by some non-picocli calls
	 * obsolete it
	 *
	 * @param cProject
	 */
	public AMICleanTool(CProject cProject) {
		this.cProject = cProject;
	}

	public AMICleanTool() {
	}

	public static void main(String[] args) throws Exception {
		AMICleanTool amiCleaner = new AMICleanTool();
		amiCleaner.runCommands(args);
	}

	@Override
	protected void parseSpecifics() {
		LOG.info("fileGlobs     {}", Util.toStringList(files));
	}

	@Override
	protected void runSpecifics() {
		runClean();
	}

	private void runClean() {
		if (files != null && getCProjectDirectory() != null) {
			for (String fileGlob : files) {
				List<File> globList = CMineGlobber.listGlobbedFilesQuietly(cProject.getDirectory(), fileGlob);
				LOG.trace("GLOB: {}({}) ==> {}", fileGlob, globList.size(), globList);
				CMFileUtil.forceDeleteQuietly(globList);
			}
		}
	}

	public void cleanFiles(List<String> filenames) {
		for (String file : filenames) {
			cleanReserved(file);
		}
	}

	public void cleanFileOrDirs(List<String> filenameList) {
		if (filenameList == null) {
			LOG.warn("No filenameList given");
			showstopperEncountered = true;
			return;
		}
		if (cProject != null) {
			for (String filename : filenameList) {
				cProject.cleanTrees(filename);
			}
//		} else if (cTree != null) {
//			if (dirs != null) {
//				for (String dir : dirs) {
//					cTree.cleanFileOrDirs(dir);
//				}
//			}
//			if (files != null) {
//				for (String file : files) {
//					cTree.cleanFileOrDirs(file);
//				}
//			}
		} else {
			LOG.error("must give cProject or cTree");
			showstopperEncountered = true;
		}
	}

	public boolean cleanReserved(String arg) {
		for (Cleaner cleaner : Cleaner.values()) {
			if (cleaner.matches(arg)) {
				cProject.cleanTrees(arg);
				return true;
			}
		}
		return false;
	}

	public void cleanTrees(String filename) {
		cProject.cleanTrees(filename);
	}

}
