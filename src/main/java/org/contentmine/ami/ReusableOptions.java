package org.contentmine.ami;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(synopsisHeading      = "%nUsage:%n%n",
         descriptionHeading   = "%nDescription:%n%n",
         parameterListHeading = "%nParameters:%n%n",
         optionListHeading    = "%nOptions:%n%n",
         commandListHeading   = "%nCommands:%n%n")
public class ReusableOptions {

    @Option(names = { "-v", "--verbose" }, description = {
            "Specify multiple -v options to increase verbosity.",
            "For example, `-v -v -v` or `-vvv`" })
        protected boolean[] verbosityx = new boolean[0];
    
    @Option(
    		names = {"--wombat" }, 
    		arity="1",
    		description = {
            "a wombat" }
    )
        protected int vombatus;
}
