package org.contentmine.ami.tools;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "ami",
		description = {
				"",
				"`${COMMAND-FULL-NAME}` is a command suite for managing (scholarly) documents: " +
						"download, aggregate, transform, search, filter, index, annotate, re-use and republish.",
				"It caters for a wide range of inputs (including some awful ones), and creates de facto semantics and an ontology (based on Wikidata).",
				"`${COMMAND-FULL-NAME}` is the basis for high-level science/tech applications including chemistry (molecules, spectra, reaction), Forest plots (metaanalyses of trials), phylogenetic trees (useful for virus mutations), geographic maps, and basic plots (x/y, scatter, etc.).",
				"",
				"Parameters:%n===========" // this is a hack to show a header for [@<filename>...] until https://github.com/remkop/picocli/issues/984 is fixed
		},
		//parameterListHeading  = "Parameters%n=========%n", // not shown because there are no positional parameters
		showAtFileInUsageHelp = true,
		abbreviateSynopsis = true,
		optionListHeading = "Options:%n========%n",
		mixinStandardHelpOptions = true,
		synopsisSubcommandLabel = "COMMAND",
		commandListHeading = "Commands:%n=========%n",
		usageHelpWidth = 120,
		usageHelpAutoWidth = true,
		subcommandsRepeatable = true,
		sortOptions = false,
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
public class AMI implements Runnable {
	private static final Logger LOG = Logger.getLogger(AMI.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	@ArgGroup(exclusive = true, heading = "", order = 9)
	ProjectOrTreeOptions projectOrTreeOptions = new ProjectOrTreeOptions();

	@ArgGroup(validate = false, heading = "General Options:%n", order = 30)
	GeneralOptions generalOptions = new GeneralOptions();

	@ArgGroup(validate = false, heading = "Logging Options:%n", order = 70)
	LoggingOptions loggingOptions = new LoggingOptions();

	@Spec
	CommandSpec spec;

	@Override
	public void run() {
		throw new ParameterException(spec.commandLine(), "Missing required subcommand");
	}

	protected void setLogging() {
		loggingOptions.setLogging();
	}

	public static void main(String... args) {
		System.exit(createCommandLine().execute(args));
	}

	/**
	 * FOR TESTING PURPOSES.
	 * <p>
	 * Executes {@code ami} with the specified command line arguments, and returns the
	 * specified {@code ami} subcommand.
	 * </p>
	 * @param subcommandClass the class of the {@code @Command}-annotated subcommand object to return.
	 * @param args the command line arguments: optional global options followed by a required subcommand name and optional subcommand options
	 * @param <T> the generic type of the object to return
	 * @return the invoked subcommand instance
	 */
	static <T> T execute(Class<T> subcommandClass, String... args) {
		CommandLine cmd = createCommandLine();
		cmd.execute(args);
		return (T) cmd.getParseResult().subcommand().commandSpec().userObject();
	}

	private static CommandLine createCommandLine() {
		BasicConfigurator.configure(); // TBD not needed?
		CommandLine cmd = new CommandLine(new AMI());
		cmd.setParameterExceptionHandler(new ShortErrorMessageHandler());
		return cmd;
	}

	static class ProjectOrTreeOptions {
		@ArgGroup(exclusive = false, multiplicity = "0..1",
				heading = "CProject Options:%n", order = 10)
		CProjectOptions cProjectOptions = new CProjectOptions();

		@ArgGroup(exclusive = false, multiplicity = "0..1",
				heading = "CTree Options:%n", order = 20)
		CTreeOptions cTreeOptions = new CTreeOptions();
	}

	static class CProjectOptions {
		@Option(names = {"-p", "--cproject"}, paramLabel = "DIR",
				description = "The CProject (directory) to process. This can be (a) a child directory of cwd (current working directory (b) cwd itself (use -p .) or (c) an absolute filename."
						+ " No defaults. The cProject name is the basename of the file."
		)
		protected String cProjectDirectory = null;

		protected static class TreeOptions {
			@Option(names = {"-r", "--includetree"}, paramLabel = "DIR", order = 12,
					arity = "1..*",
					description = "Include only the CTrees in the list. (only works with --cproject). "
							+ "Currently must be explicit but we'll add globbing later."
			)
			protected String[] includeTrees;

			@Option(names = {"-R", "--excludetree"}, paramLabel = "DIR", order = 13,
					arity = "1..*",
					description = "Exclude the CTrees in the list. (only works with --cproject). "
							+ "Currently must be explicit but we'll add globbing later."
			)
			protected String[] excludeTrees;
		}

		@ArgGroup(exclusive = true, multiplicity = "0..1", order = 11, heading = "")
		TreeOptions treeOptions = new TreeOptions();
	}

	static class CTreeOptions {
		@Option(names = {"-t", "--ctree"}, paramLabel = "DIR",
				description = "The CTree (directory) to process. This can be (a) a child directory of cwd (current working directory, usually cProject) (b) cwd itself, usually cTree (use -t .) or (c) an absolute filename."
						+ " No defaults. The cTree name is the basename of the file."
		)
		protected String cTreeDirectory = null;

		protected static class BaseOptions {

			@Option(names = {"-b", "--includebase"}, paramLabel = "PATH", order = 22,
					arity = "1..*",
					description = "Include child files of cTree (only works with --ctree). "
							+ "Currently must be explicit or with trailing percent for truncated glob."
			)
			protected String[] includeBase;

			@Option(names = {"-B", "--excludebase"}, paramLabel = "PATH",
					order = 23,
					arity = "1..*",
					description = "Exclude child files of cTree (only works with --ctree). "
							+ "Currently must be explicit or with trailing percent for truncated glob."
			)
			protected String[] excludeBase;
		}

		@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "", order = 21)
		BaseOptions baseOptions = new BaseOptions();
	}

	static class GeneralOptions {
		@Option(names = {"-i", "--input"}, paramLabel = "FILE",
				description = "Input filename (no defaults)"
		)
		protected String input = null;

		@Option(names = {"-n", "--inputname"}, paramLabel = "PATH",
				description = "User's basename for inputfiles (e.g. foo/bar/<basename>.png) or directories. By default this is often computed by AMI."
						+ " However some files will have variable names (e.g. output of AMIImage) or from foreign sources or applications"
		)
		protected String inputBasename;

		@Option(names = {"-L", "--inputnamelist"}, paramLabel = "PATH",
				arity = "1..*",
				description = "List of inputnames; will iterate over them, essentially compressing multiple commands into one. Experimental."
		)
		protected List<String> inputBasenameList = null;

		@Option(names = {"-f", "--forcemake"},
				description = "Force 'make' regardless of file existence and dates."
		)
		protected Boolean forceMake = false;

		@Option(names = {"-N", "--maxTrees"}, paramLabel = "COUNT",
				description = "Quit after given number of trees; null means infinite.")
		protected Integer maxTreeCount = null;
	}

	static class LoggingOptions {
		@Option(names = {"-v", "--verbose"},
				description = {
						"Specify multiple -v options to increase verbosity. " +
								"For example, `-v -v -v` or `-vvv`. "
								+ "We map ERROR or WARN -> 0 (i.e. always print), INFO -> 1(-v), DEBUG->2 (-vv)"})
		protected boolean[] verbosity = new boolean[0];

		@Option(names = {"--log4j"}, paramLabel = "(CLASS LEVEL)...", hideParamSyntax = true,
				arity = "2..*",
				description = "Customize logging configuration. Format: <classname> <level>; sets logging level of class, e.g. \n "
						+ "org.contentmine.ami.lookups.WikipediaDictionary INFO"
		)
		protected String[] log4j;

		private void setLogging() {
			if (log4j != null) {
				if (log4j.length % 2 != 0) {
					throw new RuntimeException("log4j must have even number of arguments");
				}
				Map<Class<?>, Level> levelByClass = new HashMap<Class<?>, Level>();
				for (int i = 0; i < log4j.length; ) {
					String className = log4j[i++];
					Class<?> logClass = null;
					try {
						logClass = Class.forName(className);
					} catch (ClassNotFoundException e) {
						System.err.println("Cannot find logger Class: " + className);
						i++;
						continue;
					}
					String levelS = log4j[i++];
					Level level = Level.toLevel(levelS);
					if (level == null) {
						LOG.error("cannot parse class/level: " + className + ":" + levelS);
					} else {
						levelByClass.put(logClass, level);
						Logger.getLogger(logClass).setLevel(level);
					}
				}
			}
		}
	}

	static class ShortErrorMessageHandler implements IParameterExceptionHandler {
		@Override
		public int handleParseException(ParameterException ex, String[] args) {
			CommandLine cmd = ex.getCommandLine();
			PrintWriter writer = cmd.getErr();

			writer.println(ex.getMessage());
			CommandLine.UnmatchedArgumentException.printSuggestions(ex, writer);
			writer.print(cmd.getHelp().fullSynopsis()); // since 4.1

			CommandSpec spec = cmd.getCommandSpec();
			writer.printf("Try '%s --help' for more information.%n", spec.qualifiedName());

			return cmd.getExitCodeExceptionMapper() != null
					? cmd.getExitCodeExceptionMapper().getExitCode(ex)
					: spec.exitCodeOnInvalidInput();
		}
	}
}
