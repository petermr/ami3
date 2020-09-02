package org.contentmine.ami.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMIDictTool.RawFileFormat;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** makes a project.
 * 
 * @author pm286
 *
 */
@Command(
name = "makeproject",
description = {
		"Processes a directory (CProject) containing files (e.g.*.pdf, *.html, *.xml) to be made into CTrees.",
		"Assuming a directory foo/ with files%n%n"
				+ "  a.pdf%n"
				+ "  b.pdf%n"
				+ "  c.html%n"
				+ "  d.xml%n"
				+ "%n"
				+ "makeproject -p foo -f pdf,html,xml%n"
				+ "will create:%n"
				+ "foo/%n"
				+ "  a/%n"
				+ "    fulltext.pdf%n"
				+ "  b/%n"
				+ "    fulltext.pdf%n"
				+ "  c/%n"
				+ "    fulltext.html%n"
				+ "  d/%n"
				+ "    fulltext.xml%n"
				+ "%n"
				+ "The directories can contain multiple filetypes%n"
				+ "%n"
				+ " raw filename changes occur in CProject.makeProject()"
				+ "Files with uppercase characters, spaces, punctuation, long names, etc. may give problems. By default they %n"
				+ "(a) are lowercased, %n"
				+ "(b) have punctuation set to '_' %n"
				+ "(c) are truncated to --length characters.%n"
				+ " If any of these creates ambiguity, then numeric suffixes are added. "
				+ ""
				+ "By default a logfile of the conversions is created in make_project.json. "
				+ "The name can be changed "
				+ "The filenames are changed and the files moved."
				
})
public class AMIMakeProjectTool extends AbstractAMITool {
	private static final Logger LOG = LogManager.getLogger(AMIMakeProjectTool.class);

@Option(names = {"--compress"},
    		arity="0..1",
    		description = "compress and lowercase names. "
    		)
    private int compress = 25;

    @Option(names = {"--omit"},
    		arity="1..*",
    		description = "omit filenames (list of regexes). e.g. template.xml (applies to names, not paths) (not yet tested)",
    		defaultValue = "template\\.xml, log\\.txt, summary\\.json"
    		)
    private List<String> omitRegexList;

	@Option(names = {"--rawfiletypes"},
			arity = "1..*",
			split = ",",
//			defaultValue = RawFileFormat.pdf,
			description = "Suffixes of included files (${COMPLETION-CANDIDATES}): "
					+ "can be concatenated with commas. Default PDF ")
	protected List<RawFileFormat> rawFileFormats = 
		new ArrayList<RawFileFormat>(Arrays.asList(new RawFileFormat[] {RawFileFormat.pdf}));

	@Option(names = {"--logfile"},
			description = "(A) log file for each tree/file/image analyzed. "
	)
	public String logfile;

	public AMIMakeProjectTool() {
	}
	
    public static void main(String args) throws Exception {
    	new AMIMakeProjectTool().runCommands(args);
    }

    public static void main(String[] args) throws Exception {
    	new AMIMakeProjectTool().runCommands(args);
    }

	protected void parseSpecifics() {
		LOG.info("compress            "+compress);
		LOG.info("omit                "+omitRegexList);
		LOG.info("directory           "+cProject.getDirectory().getAbsolutePath());
		LOG.info("compress            "+compress);
		LOG.info("omit                "+omitRegexList);
		LOG.info("directory           "+cProject.getDirectory().getAbsolutePath());
		LOG.info("file types          " + rawFileFormats);
		LOG.info("logfile             " + logfile);
	}

	protected void runSpecifics() {
		if (cProject != null) {
			cProject.setOmitRegexList(omitRegexList);
			cProject.makeProjectRaw(rawFileFormats, compress);
			addMakeProjectLogfile();
		}
	}

	private void addMakeProjectLogfile() {
		if (logfile == null) {
			cProject.getMakeProjectLogfile();
		} else if (NONE.equalsIgnoreCase(logfile.toString())) {
			LOG.warn("omitting logfile");
		} else {
			cProject.getMakeProjectLogfile(logfile);
		}
	}
    
	@Override
	protected void validateCTree() {
		if (cTreeDirectory() != null) {
			LOG.warn("must not have --ctree: " + cTreeDirectory() + "; IGNORED");
			showstopperEncountered = true;
		}
	}
	
	@Override
	protected void validateRawFormats() {
		if (rawFileFormats == null || rawFileFormats.size() == 0) {
			LOG.error("must give at least one filetype (e.g. html); NO ACTION");
			showstopperEncountered = true;
		}
	}
}
