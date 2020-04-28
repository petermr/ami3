package org.contentmine.ami.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.euclid.Util;

import picocli.CommandLine;
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
				+ "Assuming a directory foo/ with files%n%n"
				+ "  a.pdf%n"
				+ "  b.pdf%n"
				+ "  a.html%n"
				+ "  b.xml%n"
				+ "  c.pdf%n"
				+ "%n"
				+ "makeproject -p foo -f pdf,html,xml%n"
				+ "will create:%n"
				+ "foo/%n"
				+ "  a/%n"
				+ "    fulltext.pdf%n"
				+ "    fulltext.html%n"
				+ "  b/%n"
				+ "    fulltext.pdf%n"
				+ "    fulltext.xml%n"
				+ "  c/%n"
				+ "    fulltext.pdf%n"
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
})
public class AMIMakeProjectTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMIMakeProjectTool.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
			description = "Suffixes of included files (${COMPLETION-CANDIDATES}): "
					+ "can be concatenated with commas ")
	protected List<AMIDictionaryTool.RawFileFormat> rawFileFormats = new ArrayList<>();

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
    	addLoggingLevel(Level.INFO, "compress            "+compress);
    	addLoggingLevel(Level.INFO, "omit                "+omitRegexList);
    	System.out.println("compress            "+compress);
    	System.out.println("omit                "+omitRegexList);
		System.out.println("file types          " + rawFileFormats);
		System.out.println("logfile             " + logfile);
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
			addLoggingLevel(Level.WARN, "must not have --ctree: " + cTreeDirectory() + "; IGNORED");
    	}
	}
	
    @Override
    protected void validateRawFormats() {
		if (rawFileFormats == null || rawFileFormats.size() == 0) {
			addLoggingLevel(Level.ERROR, "must give at least one filetype (e.g. html); NO ACTION");
		}
    }

}
