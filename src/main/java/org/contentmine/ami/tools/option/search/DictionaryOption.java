package org.contentmine.ami.tools.option.search;

import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		)

public class DictionaryOption {
	@Option(names = {"--dictionary"},
    		arity = "1..*",
            description = "symbolic names of dictionaries (likely to be obsoleted). Good values are (country, disease, funders)")
    private List<String> dictionaryList;
    public List<String> getDictionaryList() {return dictionaryList;}

}
