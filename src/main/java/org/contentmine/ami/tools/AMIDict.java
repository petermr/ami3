package org.contentmine.ami.tools;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.CProjectTreeMixin;
import org.contentmine.ami.tools.AMI.CProjectOptions;
import org.contentmine.ami.tools.AMI.CTreeOptions;
import org.contentmine.ami.tools.AMI.GeneralOptions;
import org.contentmine.ami.tools.AMI.LoggingOptions;
import org.contentmine.ami.tools.AMI.ProjectOrTreeOptions;
import org.contentmine.ami.tools.AMI.ShortErrorMessageHandler;
import org.contentmine.ami.tools.AbstractAMIDictTool.DictionaryFileFormat;
import org.contentmine.ami.tools.AbstractAMIDictTool.InputFormat;
import org.contentmine.ami.tools.AbstractAMIDictTool.Operation;
import org.contentmine.ami.tools.AbstractAMIDictTool.WikiFormat;
import org.contentmine.ami.tools.AbstractAMIDictTool.WikiLink;
import org.contentmine.ami.tools.dictionary.DictionaryCreationTool;
import org.contentmine.ami.tools.dictionary.DictionaryDisplayTool;
import org.contentmine.ami.tools.dictionary.DictionarySearchTool;
import org.contentmine.ami.tools.dictionary.DictionaryTranslateTool;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
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
		})
public class AMIDict implements Runnable {
	private static final Logger LOG = Logger.getLogger(AMIDict.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

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
		int exitCode = createCommandLine().execute(args);
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
	 * @param subcommandClass the class of the {@code @Command}-annotated subcommand object to return.
	 * @param args the command line arguments: a single String containing whitespace-separated
	 *               optional global options followed by a required subcommand name and
	 *               optional subcommand options.
	 *               This will be split into arguments with {@code args.split("\\s)}.
	 * @param <T> the generic type of the object to return
	 * @return the invoked subcommand instance
	 */
	static <T> T execute(Class<T> subcommandClass, String args) {
		return execute(subcommandClass, args.trim().split("\\s+"));
	}
	static <T> T execute(Class<T> subcommandClass, String[] args) {
		CommandLine cmd = createCommandLine();
		cmd.execute(args);
		return (T) cmd.getParseResult().subcommand().commandSpec().userObject();
	}

	/**
	 * FOR TESTING PURPOSES.
	 * <p>
	 * Executes {@code ami} with the specified command line arguments and returns the exit code.
	 * </p>
	 * @param args the command line arguments: a single String containing whitespace-separated
	 *               optional global options followed by a required subcommand name and
	 *               optional subcommand options.
	 *               This will be split into arguments with {@code args.trim().split(\\s+)}.
	 * @return the exit code
	 */
	static int execute(String args) {
		return execute(args.trim().split("\\s+"));
	}
	static int execute(String[] args) {
		return createCommandLine().execute(args);
	}

	private static CommandLine createCommandLine() {
		BasicConfigurator.configure(); // TBD not needed?
		CommandLine cmd = new CommandLine(new AMIDict());
		cmd.setParameterExceptionHandler(new ShortErrorMessageHandler());
		return cmd;
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
