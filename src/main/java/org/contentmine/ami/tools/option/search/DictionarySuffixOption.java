package org.contentmine.ami.tools.option.search;

import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		)

public class DictionarySuffixOption {
    @Option(names = {"--dictionarySuffix"},
    		arity = "1",
    		defaultValue = "xml",
            description = "suffix for search dictionary")
    private List<String> dictionarySuffixList;
    public List<String> getDictionarySuffixList() {return dictionarySuffixList;}

}
