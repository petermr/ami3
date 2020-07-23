package org.contentmine.ami.tools;

import java.io.File;
import java.io.PrintWriter;
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
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.StandardLevel;
import org.contentmine.ami.tools.AbstractAMIDictTool.DictionaryFileFormat;
import org.contentmine.ami.tools.dictionary.DictionaryCreationTool;
import org.contentmine.ami.tools.dictionary.DictionaryDisplayTool;
import org.contentmine.ami.tools.dictionary.DictionarySearchTool;
import org.contentmine.ami.tools.dictionary.DictionaryTranslateTool;
import org.contentmine.ami.tools.dictionary.DictionaryUpdateTool;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(name = "amidict",
		description = {
				"",
				"`${COMMAND-FULL-NAME}` is a command suite for managing dictionary: " +
						"",
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
				DictionaryCreationTool.class,
				DictionaryDisplayTool.class,
				DictionarySearchTool.class,
				DictionaryTranslateTool.class,
				DictionaryUpdateTool.class,
		})
public class AMIDict implements Runnable {
	private static final String CONTENT_MINE_DICTIONARIES = "ContentMine/dictionaries";

	private static final Logger LOG = LogManager.getLogger(AMIDict.class);

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

	public static void main(String... args) {
		int exitCode = createCommandLine().execute(logArgs(args));
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
		CommandLine.ParseResult parseResult = cmd.getParseResult();
		if (parseResult == null) {
			return null;
		}
		if (parseResult.hasSubcommand()) {
			return (T) parseResult.subcommand().commandSpec().userObject();
		}
		return null;
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
		return args == null ? -1 : execute(args.trim().split("\\s+"));
	}

	static int execute(String[] args) {
		return createCommandLine().execute(logArgs(args));
	}

	private static CommandLine createCommandLine() {
		CommandLine cmd = new CommandLine(new AMIDict());
		cmd.setParameterExceptionHandler(new ShortErrorMessageHandler());
		cmd.setExecutionStrategy(AMIDict::enhancedLoggingExecutionStrategy);
		return cmd;
	}

	private static int enhancedLoggingExecutionStrategy(CommandLine.ParseResult parseResult) {
		AMIDict dict = parseResult.commandSpec().commandLine().getCommand();
		dict.loggingOptions.reconfigureLogging();
		return new CommandLine.RunLast().execute(parseResult); // now delegate to the default execution strategy
	}

	private static String[] logArgs(String[] args) {
		LOG.info("args: {}", Arrays.toString(args));
		return args;
	}

	public String getDirectoryTopname() {
		return directory == null ? null : directory.toString();
	}

	public void setDirectoryTopname(String directoryTopname) {
		this.directory = new File(directoryTopname);
	}

	public List<String> getDictionaryList() {
		return dictionaryList;
	}

	public void setDictionaryList(List<String> dictionaryList) {
		this.dictionaryList = dictionaryList;
	}

	/**
	 * Toplevel
	 */
	@Option(names = {"-d", "--dictionary"},
			scope = CommandLine.ScopeType.INHERIT, // this option can be used in all subcommands
			arity = "1..*",
			split=",",
			description = "input or output dictionary name/s. for 'create' must be singular; when 'display' or 'translate', any number. "
					+ "Names should be lowercase, unique. [a-z][a-z0-9._]. Dots can be used to structure dictionaries into"
					+ "directories. Dictionary names are relative to 'directory'. If <directory> is absent then "
					+ "dictionary names are absolute.")
	List<String> dictionaryList = new ArrayList<>();

	/**
	 * both create and translate
	 */
	@Option(names = {"--directory"},
			scope = CommandLine.ScopeType.INHERIT, // this option can be used in all subcommands
			arity = "1",
			description = "top directory containing dictionary/s. Subdirectories will use structured names (NYI). Thus "
					+ "dictionary 'animals' is found in '<directory>/animals.xml', while 'plants.parts' is found in "
					+ "<directory>/plants/parts.xml. Required for relative dictionary names.")
	File directory = null;


	static class GeneralOptions {
		@Option(names = {"-i", "--input"}, paramLabel = "FILE",
				description = "Input filename, containing input for dictionary. its basename becomes the inputname"
		)
		protected String input = null;

		@Option(names = {"-n", "--inputname"}, paramLabel = "PATH",
				description = "User's basename for inputfiles (e.g. foo/bar/<basename>.txt)."
						+ " The default name for the dictionary; "
						+ " but may be obsolete and superseded by `dictionary`"
		)
		protected String inputBasenameOld;

		@Option(names = {"-L", "--inputnamelist"}, paramLabel = "PATH",
				arity = "1..*",
				description = "List of inputnames; will iterate over them, essentially compressing multiple commands into one. Experimental."
		)
		protected List<String> inputBasenameList = null;

		@Option(names = {"-x", "--informat"}, paramLabel = "PATH",
				arity = "1",
				description = "extension for dictionary file , default: " + "xml"
		)
		protected DictionaryFileFormat inputFormat = DictionaryFileFormat.xml;

	}

	/**
	 * This class can be used as a mixin in subcommands of the {@code AMIDict} command.
	 */
	public static class GeneralOptionsMixin {
		@Spec(Spec.Target.MIXEE)
		CommandSpec mixeeSpec; // the CommandSpec of the command where this mixin is used

		private GeneralOptions parentGeneralOptions() {
			CommandSpec p = mixeeSpec.parent();
			while (p != null && !(p.userObject() instanceof AMIDict)) {
				p = p.parent();
			}
			if (p == null) {
				throw new IllegalStateException("This mixin must only be used in a command that is a subcommand (or sub-subcommand, etc.) of AMIDict");
			}
			AMIDict amiDict = (AMIDict) p.userObject();
			return amiDict.generalOptions;
		}

		@Option(names = {"-i", "--input"}, paramLabel = "FILE",
				description = "Input filename (no defaults)"
		)
		protected void setInput(String input) {
			parentGeneralOptions().input = input;
		}
//		protected void get(String input) {
//			parentGeneralOptions().input = input;
//		}

		;

		@Option(names = {"-L", "--inputnamelist"}, paramLabel = "PATH",
				arity = "1..*",
				description = "List of inputnames; will iterate over them, essentially compressing multiple commands into one. Experimental."
		)
		protected void setInputBasenameList(List<String> inputBasenameList) {
			parentGeneralOptions().inputBasenameList = inputBasenameList;
		}
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

			// find the CONSOLE appender and set its log level to match the specified verbosity
			LoggerContext loggerContext = LoggerContext.getContext(false);
			LoggerConfig rootConfig = loggerContext.getConfiguration().getRootLogger();
			for (Appender appender : rootConfig.getAppenders().values()) {
				if (appender instanceof ConsoleAppender) {
					rootConfig.removeAppender(appender.getName());
					rootConfig.addAppender(appender, level, null);
				}
			}
			// we may need to change the ROOT logger if it is stricter than the user-specified verbosity
			if (rootConfig.getLevel().isMoreSpecificThan(level)) {
				rootConfig.setLevel(level);
			}
			loggerContext.updateLoggers(); // apply the changes
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

	public static File getDictionaryDirectory() {
		File homeDir = new File(System.getProperty("user.home"));
		return new File(homeDir, CONTENT_MINE_DICTIONARIES);
	}

	public File getDirectory() {
		return directory;
	}


}
