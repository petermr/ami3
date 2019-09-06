package org.contentmine.ami;

import picocli.CommandLine.Option;

public class CProjectTreeMixin {
	
    @Option(
    		names = {"--wombatx"},
    		arity="1"
    		) int wombatx = 99;
    @Option(names = "--wombaty") public int wombaty;
    
    public int getWombatx() {
    	return wombatx;
    }
    
	
}
