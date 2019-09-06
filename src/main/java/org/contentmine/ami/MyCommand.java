package org.contentmine.ami;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(name = "zip", description = "Example reuse with @Mixin annotation.")
public class MyCommand {

    // adds the options defined in ReusableOptions to this command
    @Mixin
    public ReusableOptions myMixin;
//    ...
}




