package org.contentmine.ami.tools;

import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import java.util.concurrent.Callable;

@Command(name = "ami",
        description = {
                "",
                "${COMMAND-FULL-NAME} is a command suite for managing (scholarly) documents: " +
                    "download, aggregate, transform, search, filter, index, annotate, re-use and republish.",
                "It caters for a wide range of (awful) inputs, creates de facto semantics, and an ontology (based on Wikidata).",
                "${COMMAND-FULL-NAME} is the basis for high-level science/tech applications including chemistry (molecules, spectra, reaction), Forest plots (metaanalyses of trials), phylogenetic trees (useful for virus mutations), geographic maps, and basic plots (x/y, scatter, etc.).",
                "",
                "Parameters:%n===========" // this is a hack to show a header for [@<filename>...]
        },
        //parameterListHeading  = "Parameters%n=========%n", // not shown because there are no positional parameters
        showAtFileInUsageHelp    = true,
        abbreviateSynopsis       = true,
        optionListHeading        = "Options:%n========%n",
        mixinStandardHelpOptions = true,
        synopsisSubcommandLabel  = "COMMAND",
        commandListHeading       = "Commands:%n=========%n",
        subcommandsRepeatable    = true,
        subcommands = {
                AMIAssertTool.class,
                AMICleanTool.class,
                AMIDictionaryTool.class,
                AMIDisplayTool.class,
                AMIDownloadTool.class,
                AMIDummyTool.class,
                AMIFilterTool.class,
                AMIForestPlotTool.class,
                AMIGetpapersTool.class,
                AMIGraphicsTool.class,
                AMIGrobidTool.class,
                AMIImageFilterTool.class,
                AMIImageTool.class,
                AMIMakeProjectTool.class,
                AMIMetadataTool.class,
                AMIOCRTool.class,
                AMIPDFTool.class,
                AMIPixelTool.class,
                AMIRegexTool.class,
                AMISearchTool.class,
                AMISectionTool.class,
                AMISummaryTool.class,
                AMISVGTool.class,
                AMITableTool.class,
                AMITransformTool.class,
                AMIWordsTool.class,
                CommandLine.HelpCommand.class,
                AutoComplete.GenerateCompletion.class,
        })
public class AMI implements Callable<Void> {
    @Spec CommandSpec spec;

    @Override
    public Void call() {
        throw new ParameterException(spec.commandLine(), "Missing required subcommand");
    }
    public static void main(String... args) {
        System.exit(new CommandLine(new AMI()).execute(args));
    }
}
