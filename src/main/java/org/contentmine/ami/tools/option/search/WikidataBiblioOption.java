package org.contentmine.ami.tools.option.search;

import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command()
public class WikidataBiblioOption {
    @Option(names = {"--wikidataBiblio"},
    		arity = "0",
            description = " lookup wikidata biblographic object")
    private Boolean wikidataBiblio = false;
}
