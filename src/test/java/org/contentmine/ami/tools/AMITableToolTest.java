package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMITableTool;
import org.contentmine.ami.tools.AMITableTool.TableFormat;
import org.contentmine.ami.tools.AMITableTool.TableType;
import org.junit.Assert;
import org.junit.Test;

import picocli.CommandLine.Option;

public class AMITableToolTest {
	private static final Logger LOG = Logger.getLogger(AMITableToolTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static File CEPHIS_TOP;
	static {
		try {
			CEPHIS_TOP = new File("../cephis").getCanonicalFile();
		} catch (IOException e) {
			LOG.debug("Cannot initialise Files "+e.getMessage());
			e.printStackTrace();
		}
	}
	public final static File CEPHIS_RESOURCES = new File(CEPHIS_TOP, "src/test/resources");
	public final static File CEPHIS_CONTENTMINE = new File(CEPHIS_RESOURCES, "org/contentmine");
	public final static File CEPHIS_SVG2XML = new File(CEPHIS_CONTENTMINE, "svg2xml");
	public final static File CEPHIS_TABLE = new File(CEPHIS_SVG2XML, "table");
	public final static File CMUCL0_PROJECT = new File(CEPHIS_TABLE, "cmucl0");
	public final static File CMUCL0_BMC_TREE = new File(CMUCL0_PROJECT, "BMC_Medicine");

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


	@Test
	public void testSyntax() {
		Assert.assertTrue("table exists: "+CEPHIS_TABLE, CEPHIS_TABLE.exists());
		Assert.assertTrue("project exists: "+CMUCL0_PROJECT, CMUCL0_PROJECT.exists());
		String args = ""
				+ "-p " + CMUCL0_PROJECT + ""
				+ " --type apa"
				+ " --format THBF"
				+ " --column_split"
				+ " --row_split"
				+ " --cell_regex fred foo[bar]?"
				+ " --tables 1 2 3"
			;
		new AMITableTool().runCommands(args);
	}
}
