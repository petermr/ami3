package org.contentmine.ami.tools.option.search;

import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command()
public class IgnorePluginsOption {
    @Option(names = {"--ignorePlugins"},
    		arity = "1..*",
            description = " list of plugins to skip (mainly for debugging)")
    public List<String> ignorePluginList = new ArrayList<>();
    public List<String> getIgnorePluginList() {return ignorePluginList;}

}
