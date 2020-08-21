package org.contentmine.ami.tools;

import org.junit.Test;
import picocli.CommandLine;

import java.util.Scanner;

import static org.junit.Assert.*;
import static picocli.CommandLine.Help.Ansi.OFF;

public class AMITest {

    @Test
    public void testHelp() {
        String usageMessage = new CommandLine(new AMI()).getUsageMessage(OFF);

        String expected = String.format("" +
                "Usage: ami [OPTIONS] COMMAND%n" +
                "%n" +
                "`ami` is a command suite for managing (scholarly) documents: download, aggregate, transform, search, filter, index,%n" +
                "annotate, re-use and republish.%n" +
                "It caters for a wide range of inputs (including some awful ones), and creates de facto semantics and an ontology (based%n" +
                "on Wikidata).%n" +
                "`ami` is the basis for high-level science/tech applications including chemistry (molecules, spectra, reaction), Forest%n" +
                "plots (metaanalyses of trials), phylogenetic trees (useful for virus mutations), geographic maps, and basic plots (x/y,%n" +
                "scatter, etc.).%n" +
                "%n" +
                "Parameters:%n" +
                "===========%n" +
                "      [@<filename>...]       One or more argument files containing options.%n" +
                "Options:%n" +
                "========%n" +
                "  -h, --help                 Show this help message and exit.%n" +
                "  -V, --version              Print version information and exit.%n" +
                "CProject Options:%n" +
                "  -p, --cproject=DIR         The CProject (directory) to process. This can be (a) a child directory of cwd (current%n" +
                "                               working directory (b) cwd itself (use `-p .`) or (c) an absolute filename. The cProject%n" +
                "                               name is the basename of the file.%n" +
                "                              The default is: `%s/amiprojects/myproject`.%n" +
                "                              You can control the default by setting the `AMIPROJECT` environment variable.%n",
                System.getProperty("user.home"));

        Scanner scanExpected = new Scanner(expected);
        Scanner scanActual = new Scanner(usageMessage);
        int lineNumber = 1;
        for (String line = scanExpected.nextLine(); scanExpected.hasNextLine(); line = scanExpected.nextLine()) {
            assertTrue(scanActual.hasNextLine());
            assertEquals("line " + lineNumber++, scanActual.nextLine(), line);
        }
    }

}