package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIProcessor;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.search.SearchPluginOption;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.norma.NAConstants;

import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */
public class AMITableTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMITableTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum TableType {
		apa,
		grid,
	}
	
	public enum TableFormat {
		THBF,
		HBTF,
	}
	
    @Option(names = {"--type"},
    		arity = "1",
            description = "type of table ${TableType}")
    private TableType tableType;

    @Option(names = {"--format"},
    		arity = "1",
            description = "format of table {$TableFormat}")
    private TableFormat tableFormat;

    @Option(names = {"--column_split"},
    		arity = "0..*",
            description = "split columns?")
    private Boolean splitColumns = false;

    @Option(names = {"--row_split"},
    		arity = "0..*",
            description = "split rows?")
    private Boolean splitRows = false;

    @Option(names = {"--cell_regex"},
    		arity = "1..*",
            description = "split cells into columns based on regexes ?")
    private List<String> regexList;

    @Option(names = {"--tables"},
    		arity = "1..*",
            description = "titles of tables")
    private List<String> tableNumberList;

	public AMITableTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMITableTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMITableTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
    	List<Pattern> patternList = compileRegexList();
		System.out.println("tableType            " + tableType);
		System.out.println("tableFormat          " + tableFormat);
		System.out.println("splitCooumns         " + splitColumns);
		System.out.println("splitRows            " + splitRows);
		System.out.println("regexList            " + patternList);
		System.out.println();
	}

    private List<Pattern> compileRegexList() {
    	List<Pattern> patternList = new ArrayList<>();
    	if (regexList != null) {
	    	for (String regex : regexList) {
	    		try {
	    			Pattern pattern = Pattern.compile(regex);
	    			patternList.add(pattern);
	    		} catch (PatternSyntaxException e) {
	    			addLoggingLevel(Level.ERROR, "bad regex: " + e.getMessage());
	    		}
	    	}
    	}
    	return patternList;
	}

	@Override
    protected void runSpecifics() {
    	if (cProject == null) {
    		DebugPrint.errorPrintln(Level.ERROR, "requires cProject");
    	} else if (projectExists(cProject)) {
    		processProject();
    	}
    }

	private boolean projectExists(CProject cProject) {
		return cProject == null || cProject.getDirectory() == null ? false : cProject.getDirectory().isDirectory();
	}

	public void processProject() {
		System.out.println("cProject: "+cProject.getName());
		runTable();
	}

	private void runTable() {
		System.out.println("table NYI");
	}


}
