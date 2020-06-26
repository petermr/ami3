package org.contentmine.ami.tools;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */

@Command(
name = "dummy",
description = "Minimal AMI Tool for editing into more powerful classes."
)
public class AMIDummyTool extends AbstractAMITool {

	private static final Logger LOG = LogManager.getLogger(AMIDummyTool.class);
interface ToolMethod {
		public void runMe();
	}
	
	public enum Tool implements ToolMethod{
		foo,
		date,
		hello
		;
		
		private Tool() {
		}
		
		@Override
		public void runMe() {
			switch (this) {
	        case date:
	        	LOG.warn("Date: "+LocalDate.now());
	        	break;				
	        case hello:
				LOG.warn("Hello AMI");
	        	break;				
	        default:
				LOG.warn("Missed "+this);
			}
		}
	}

    @Option(names = {"--tools"},
    		arity = "1..*",
            description = "run a list of Tools (omitted lists tools)")
    private List<Tool> toolList = new ArrayList<>();

	
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIDummyTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIDummyTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIDummyTool().runCommands(new String[] {});
    	new AMIDummyTool().runCommands(new String[] {"--tools", "date", "hello", "foo"});
    }


    @Override
	protected boolean parseGenerics() {
		LOG.info("project         " + cProject);
		return true;
	}

    @Override
	protected void parseSpecifics() {
		LOG.info("tools         " + toolList);
	}
    
    @Override
    protected void runSpecifics() {
    	runTools();
    }

	private void runTools() {
		if (toolList.size() == 0) {
			LOG.warn("enum "+Arrays.asList(Tool.values()));
		} else {
			toolList.forEach(t -> t.runMe());
		}
	}

}
