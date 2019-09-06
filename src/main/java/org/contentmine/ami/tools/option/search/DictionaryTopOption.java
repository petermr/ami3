package org.contentmine.ami.tools.option.search;

import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		)

public class DictionaryTopOption {
    @Option(names = {"--dictionaryTop"},
    		arity = "1",
            description = " local dictionary home directory")
    private List<String> dictionaryTopList;
    public List<String> getDictionaryTopList() {return dictionaryTopList;}

}
