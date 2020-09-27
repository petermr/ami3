package org.contentmine.ami.tools;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.StandardLevel;

import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;
import picocli.jansi.graalvm.AnsiConsole;

@Command(name = "ami", versionProvider = ManifestVersionProvider.class,
		defaultValueProvider = CommandLine.PropertiesDefaultProvider.class,
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
				AMIDisplayTool.class,
				AMIDownloadTool.class,
				AMIDummyTool.class,
				AMIFigureTool.class,
				AMIFilesTool.class,
				AMIFilterTool.class,
				AMIForestPlotTool.class,
				//AMIGetpapersTool.class, // https://github.com/petermr/ami3/issues/29
				AMIGraphicsTool.class,
				AMIGrobidTool.class,
				AMIImageTool.class,
				AMILuceneTool.class,
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
	private static final Logger LOG = LogManager.getLogger(AMI.class);

	public static final String VERSION = "2020-07-18"; // update this with each change

	@ArgGroup(exclusive = true, order = 9)
	ProjectOrTreeOptions projectOrTreeOptions = new ProjectOrTreeOptions();

	@ArgGroup(validate = false, heading = "General Options:%n", order = 30)
	GeneralOptions generalOptions = new GeneralOptions();

	@ArgGroup(validate = false, heading = "Logging Options:%n", order = 70)
	LoggingOptions loggingOptions = new LoggingOptions();

//	@ArgGroup(validate = false, heading = "Dictionary Options:%n", order = 75)
//	DictionaryOptions dictionaryOptions = new DictionaryOptions();

	@Spec
	CommandSpec spec;

	@Override
	public void run() {
		throw new ParameterException(spec.commandLine(), "Missing required subcommand");
	}

	public static void main(String args) {
		if (args != null) {
			main(args.trim().split("\\s+"));
		} else {
			LOG.error("null args - no action");
		}
	}

	public static void main(String... args) {
		int exitCode;
		try (AnsiConsole ansi = AnsiConsole.windowsInstall()) { // enable colors on Windows
			exitCode = createCommandLine().execute(logArgs(args));
		}
		if (System.getProperty("ami.no.exit") == null) {
			System.exit(exitCode);
		}
	}

	/**
	 * FOR TESTING PURPOSES.
	 * <p>
	 * Executes {@code ami} with the specified command line arguments, and returns the
	 * specified {@code ami} subcommand.
	 * </p>
	 *
	 * @param subcommandClass the class of the {@code @Command}-annotated subcommand object to return.
	 * @param args            the command line arguments: a single String containing whitespace-separated
	 *                        optional global options followed by a required subcommand name and
	 *                        optional subcommand options.
	 *                        This will be split into arguments with {@code args.split("\\s)}.
	 * @param <T>             the generic type of the object to return
	 * @return the invoked subcommand instance
	 */
	public static <T> T execute(Class<T> subcommandClass, String args) {
		return execute(subcommandClass, args.trim().split("\\s+"));
	}

	static <T> T execute(Class<T> subcommandClass, String[] args) {
		CommandLine cmd = createCommandLine();
		cmd.execute(logArgs(args));
		if (cmd.getParseResult() == null) {
			return null;
		}
		return cmd.getParseResult().hasSubcommand()
				? (T) cmd.getParseResult().subcommand().commandSpec().userObject()
				: (T) cmd.getParseResult().commandSpec().userObject();
	}

	/**
	 * FOR TESTING PURPOSES.
	 * <p>
	 * Executes {@code ami} with the specified command line arguments and returns the exit code.
	 * </p>
	 *
	 * @param args the command line arguments: a single String containing whitespace-separated
	 *             optional global options followed by a required subcommand name and
	 *             optional subcommand options.
	 *             This will be split into arguments with {@code args.trim().split(\\s+)}.
	 * @return the exit code
	 */
	public static int execute(String args) {
		return execute(args.trim().split("\\s+"));
	}

	static int execute(String[] args) {
		return createCommandLine().execute(logArgs(args));
	}

	private static CommandLine createCommandLine() {
		CommandLine cmd = new CommandLine(new AMI());
		cmd.setParameterExceptionHandler(new ShortErrorMessageHandler());
		cmd.setExecutionStrategy(AMI::enhancedLoggingExecutionStrategy);
		cmd.setUnmatchedOptionsAllowedAsOptionParameters(false);
		return cmd;
	}

	private static int enhancedLoggingExecutionStrategy(ParseResult parseResult) {
		AMI ami = parseResult.commandSpec().commandLine().getCommand();
		ami.loggingOptions.reconfigureLogging();

		StringWriter sw = new StringWriter();
		parseResult.commandSpec().commandLine().printVersionHelp(new PrintWriter(sw, true), CommandLine.Help.Ansi.OFF);
		LOG.debug("Version: {}", sw);

		return new CommandLine.RunLast().execute(parseResult); // now delegate to the default execution strategy
	}

	private static String[] logArgs(String[] args) {
		LOG.info("args: {}", Arrays.toString(args));
		return args;
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
		@Option(names = {"-p", "--cproject"}, defaultValue = "${AMIPROJECT:-${user.home}/amiprojects/myproject}", paramLabel = "DIR",
				description = {"The CProject (directory) to process."
						+ " This can be (a) a child directory of cwd (current working directory) (b) cwd itself (use `-p .`) or (c) an absolute filename."
						+ " The cProject name is the basename of the file.",
						" The default is: `${DEFAULT-VALUE}`.",
						" You can control the default by setting the `AMIPROJECT` environment variable."
		})
		protected String cProjectDirectory = null;

		protected static class TreeOptions {
			@Option(names = {"-r", "--includetree"}, paramLabel = "DIR", order = 12,
					arity = "1..*",
					split = ",",
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

		@ArgGroup(exclusive = true, multiplicity = "0..1", order = 11)
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

		@ArgGroup(exclusive = true, multiplicity = "0..1", order = 21)
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

		@Option(names = {"-o", "--output"},
				paramLabel = "output",
				description = "Output filename (no defaults)"
		)
		protected String output = null;

	}

	static class LoggingOptions {
		@Option(names = {"-v", "--verbose"},
				description = {
						"Specify multiple -v options to increase verbosity. " +
								"For example, `-v -v -v` or `-vvv`. "
								+ "We map ERROR or WARN -> 0 (i.e. always print), INFO -> 1 (-v), DEBUG -> 2 (-vv)"})
		protected boolean[] verbosity = new boolean[0];

		@Option(names = {"--log4j"}, paramLabel = "CLASS=LEVEL[,CLASS=LEVEL...]", split = ",", hideParamSyntax = true,
				description = {
						"Customize logging configuration. Format: <classname>=<level>; sets logging level of class;",
						"  e.g. org.contentmine.ami.lookups.WikipediaDictionary=INFO",
						"This option may be specified multiple times and accepts multiple values."}
		)
		protected Map<Class, StandardLevel> log4j = new HashMap<>();

		/**
		 * Updates the logging configuration with user-specified modifications:
		 * <ul>
		 *   <li>verbosity - may change how much output is printed by the CONSOLE appender</li>>
		 *   <li>log4j - modify the log level for specific classes (without changing the log4j2.xml config file)</li>>
		 * </ul>
		 */
		private void reconfigureLogging() {
			Map<String, Level> levelByClass = log4j.entrySet().stream().collect(Collectors.toMap(
					e -> e.getKey().getName(), // class name = logger name
					e -> Level.toLevel(e.getValue().name()))); // StandardLevel (enum) -> Level
			Configurator.setLevel(levelByClass); // apply the user-specified changes

			Level level = verbosityToLogLevel();
			LOG.debug("Specified verbosity={}, this translates to level={}", verbosity.length, level);

			// find the CONSOLE appender and set its log level to match the specified verbosity
			List<File> logFiles = new ArrayList<>();
			LoggerContext loggerContext = LoggerContext.getContext(false);
			LoggerConfig rootConfig = loggerContext.getConfiguration().getRootLogger();
			for (Appender appender : rootConfig.getAppenders().values()) {
				if (appender instanceof ConsoleAppender) {
					LOG.debug("Reconfiguring {} appender with {}", appender, level);
					rootConfig.removeAppender(appender.getName());
					rootConfig.addAppender(appender, level, null);
				}
				if (appender instanceof FileAppender) {
					logFiles.add(new File(((FileAppender) appender).getFileName()));
				} else if (appender instanceof RollingFileAppender) {
					logFiles.add(new File(((RollingFileAppender) appender).getFileName()));
				} else if (appender instanceof RandomAccessFileAppender) {
					logFiles.add(new File(((RandomAccessFileAppender) appender).getFileName()));
				} else if (appender instanceof RollingRandomAccessFileAppender) {
					logFiles.add(new File(((RollingRandomAccessFileAppender) appender).getFileName()));
				}
			}
			// we may need to change the ROOT logger if it is stricter than the user-specified verbosity
			if (rootConfig.getLevel().isMoreSpecificThan(level)) {
				rootConfig.setLevel(level);
			}
			loggerContext.updateLoggers(); // apply the changes
			LOG.info("(The console will show {} level messages)", level);
			for (File logFile : logFiles) {
				LOG.info("(Logs will also be printed to {})", logFile.getAbsolutePath());
			}
		}

		private Level verbosityToLogLevel() {
			switch (verbosity.length) {
				case 0:  return Level.WARN;  // WARN, ERROR and FATAL messages are always printed to the console
				case 1:  return Level.INFO;  // -v
				case 2:  return Level.DEBUG; // -vv
				default: return Level.TRACE; // -vvv (or more)
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
