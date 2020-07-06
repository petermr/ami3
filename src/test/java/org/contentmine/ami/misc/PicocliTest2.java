package org.contentmine.ami.misc;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
//import picocli.CommandLine.NoCompletionCandidates;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** simple example of picocli that runs.
 * 
 * @author pm286
 *
 */

@Command(
				//String name() default "<main class>";
		name = "calculator", 
				//String[] aliases() default {};
		aliases = "sums",
				//Class<?>[] subcommands() default {};
//		subcommands = Double.class,
				//boolean addMethodSubcommands() default true;
		addMethodSubcommands = false,
				//String separator() default "=";
		separator = "=",
				//String[] version() default {};
		version = "calc 0.111",
				//Class<? extends IVersionProvider> versionProvider() default NoVersionProvider.class;
//		versionProvider = Class<? extends IVersionProvider>.class,
				//boolean mixinStandardHelpOptions() default false;
		mixinStandardHelpOptions = true,
				//boolean helpCommand() default false;
		helpCommand = true,
				//String headerHeading() default "";
		headerHeading = "Header\n======\n",
				//String[] header() default {};
		header = "I am a header\n",
				//String synopsisHeading() default "Usage: ";
		synopsisHeading = "Synopis\n=======\n",
				//boolean abbreviateSynopsis() default false;
		abbreviateSynopsis = true,
				//String[] customSynopsis() default {};
		customSynopsis = "@|bg(red) text with red background.I am a synopsis|@.\n",
				//String descriptionHeading() default "";
		descriptionHeading = "Description\n===========\n",
				//String[] description() default {};
		description = "Custom @|bold,underline styles|@ and @|fg(red) colors|@. Calculates powers and writes to STDOUT.\n",
				//String parameterListHeading() default "";
		parameterListHeading  = "Parameters\n=========\n",
				//String optionListHeading() default "";
		optionListHeading  = "Options\n=======\n",
				//boolean sortOptions() default true;
		sortOptions = true,
				//char requiredOptionMarker() default ' ';
		requiredOptionMarker = 'X',
				//Class<? extends IDefaultValueProvider> defaultValueProvider() default NoDefaultProvider.class;
//		defaultValueProvider = new Class<? extends IDefaultValueProvider>(),
				//boolean showDefaultValues() default false;
		showDefaultValues = true,
				//String commandListHeading() default "Commands:%n";
		commandListHeading = "Commandzz:%n",
				//String footerHeading() default "";
		footerHeading = "Footer\n======\n",
				//String[] footer() default {};
		footer = "I am a footer",
				//boolean hidden() default false;
		hidden = false,
				//String resourceBundle() default "";
//		resourceBundle = "theResourceBundle",
				//int usageHelpWidth() default 80;
		usageHelpWidth = 60
		)


public class PicocliTest2 implements Callable<Void> {

//    @Parameters(index = "0", description = "The file whose checksum to calculate.")
//    private File file;

//  String index() default ""; // Parameters only
//  String[] description() default {};
//  String arity() default "";
//  String paramLabel() default "";
//  boolean hideParamSyntax() default false;
//  Class<?>[] type() default {};
//  Class<? extends ITypeConverter<?>>[] converter() default {};
//  String split() default "";
//  boolean hidden() default false;
//  String defaultValue() default "__no_default_value__";
//  Help.Visibility showDefaultValue() default Help.Visibility.ON_DEMAND;
//  Class<? extends Iterable<String>> completionCandidates() default NoCompletionCandidates.class;
//  boolean interactive() default false; // only arity 1
//  String descriptionKey() default "";
    @Parameters(index = "0", 
    		description = "An integer",
    		//arity="1", // picocli does not allow having an `arity="1"` attribute for interactive options
    		paramLabel="arg for algorithm",
    		hideParamSyntax=false,
    		type=Integer.class,
//    		converter=MyConverter.class, // doesn't yet exist
    		//split="#", // picocli does not allow having a `split` attribute on a single-value option
    		hidden=false,
    		defaultValue="-99",
    		showDefaultValue=Help.Visibility.ALWAYS,
//    		completionCandidates=NoCompletionCandidates.class,
    		interactive=true,
    		descriptionKey="bar"
    		
    		)
    private Integer intArg;

    
    @Option(names = {"-a", "--algorithm"}, description = "square, cube")
    private String algorithm = "square";


	private int result;
	private String[] args;

    public PicocliTest2(String[] args) {
    	this.args = args;
	}

//  boolean required() default false;    // Option only
//  boolean usageHelp() default false;   //  Option only
//  boolean versionHelp() default false; //  Option only
//  String[] description() default {};
//  String arity() default "";
//  String paramLabel() default "";
//  boolean hideParamSyntax() default false;
//  Class<?>[] type() default {};
//  Class<? extends ITypeConverter<?>>[] converter() default {};
//  String split() default "";
//  boolean hidden() default false;
//  String defaultValue() default "__no_default_value__";
//  Help.Visibility showDefaultValue() default Help.Visibility.ON_DEMAND;
//  Class<? extends Iterable<String>> completionCandidates() default NoCompletionCandidates.class;
//  boolean interactive() default false;
//  String descriptionKey() default "";


    
    public static void main(String[] args) throws Exception {
    	int count = 3;
    	while (count-- > 0) {
        	PicocliTest2 test2 = new PicocliTest2(args);
        	test2.runAlgorithm();
    	}
    }

    private void runAlgorithm() {
        CommandLine.call(this, args);
    }

//    @Override
    public Void call() throws Exception {
    	System.out.println("called on " + intArg + " with "+algorithm);
        result = Integer.MIN_VALUE;
        if ("square".equals(algorithm)) {
        	result = intArg * intArg;
        } else if ("cube".equals(algorithm)) {
        	result = intArg * intArg * intArg;
        } else {
        	throw new RuntimeException("bad algorithm");
        }
        System.err.println(algorithm + "("+intArg+") = " + result);
        return null;
    }
    
//    public @interface Option {
//        /**
//         * One or more option names. At least one option name is required.
//         * <p>
//         * Different environments have different conventions for naming options, but usually options have a prefix
//         * that sets them apart from parameters.
//         * Picocli supports all of the below styles. The default separator is {@code '='}, but this can be configured.
//         * </p><p>
//         * <b>*nix</b>
//         * </p><p>
//         * In Unix and Linux, options have a short (single-character) name, a long name or both.
//         * Short options
//         * (<a href="http://pubs.opengroup.org/onlinepubs/9699919799/basedefs/V1_chap12.html#tag_12_02">POSIX
//         * style</a> are single-character and are preceded by the {@code '-'} character, e.g., {@code `-v'}.
//         * <a href="https://www.gnu.org/software/tar/manual/html_node/Long-Options.html">GNU-style</a> long
//         * (or <em>mnemonic</em>) options start with two dashes in a row, e.g., {@code `--file'}.
//         * </p><p>Picocli supports the POSIX convention that short options can be grouped, with the last option
//         * optionally taking a parameter, which may be attached to the option name or separated by a space or
//         * a {@code '='} character. The below examples are all equivalent:
//         * </p><pre>
//         * -xvfFILE
//         * -xvf FILE
//         * -xvf=FILE
//         * -xv --file FILE
//         * -xv --file=FILE
//         * -x -v --file FILE
//         * -x -v --file=FILE
//         * </pre><p>
//         * <b>DOS</b>
//         * </p><p>
//         * DOS options mostly have upper case single-character names and start with a single slash {@code '/'} character.
//         * Option parameters are separated by a {@code ':'} character. Options cannot be grouped together but
//         * must be specified separately. For example:
//         * </p><pre>
//         * DIR /S /A:D /T:C
//         * </pre><p>
//         * <b>PowerShell</b>
//         * </p><p>
//         * Windows PowerShell options generally are a word preceded by a single {@code '-'} character, e.g., {@code `-Help'}.
//         * Option parameters are separated by a space or by a {@code ':'} character.
//         * </p>
//         * @return one or more option names
//         */
//        String[] names();
//
//        /**
//         * Indicates whether this option is required. By default this is false.
//         * If an option is required, but a user invokes the program without specifying the required option,
//         * a {@link MissingParameterException} is thrown from the {@link #parse(String...)} method.
//         * @return whether this option is required
//         */
//        boolean required() default false;
//
//        /**
//         * Set {@code help=true} if this option should disable validation of the remaining arguments:
//         * If the {@code help} option is specified, no error message is generated for missing required options.
//         * <p>
//         * This attribute is useful for special options like help ({@code -h} and {@code --help} on unix,
//         * {@code -?} and {@code -Help} on Windows) or version ({@code -V} and {@code --version} on unix,
//         * {@code -Version} on Windows).
//         * </p>
//         * <p>
//         * Note that the {@link #parse(String...)} method will not print help documentation. It will only set
//         * the value of the annotated field. It is the responsibility of the caller to inspect the annotated fields
//         * and take the appropriate action.
//         * </p>
//         * @return whether this option disables validation of the other arguments
//         * @deprecated Use {@link #usageHelp()} and {@link #versionHelp()} instead. See {@link #printHelpIfRequested(List, PrintStream, CommandLine.Help.Ansi)}
//         */
//        @Deprecated boolean help() default false;
//
//        /**
//         * Set {@code usageHelp=true} for the {@code --help} option that triggers display of the usage help message.
//         * The <a href="http://picocli.info/#_printing_help_automatically">convenience methods</a> {@code Commandline.call},
//         * {@code Commandline.run}, and {@code Commandline.parseWithHandler(s)} will automatically print usage help
//         * when an option with {@code usageHelp=true} was specified on the command line.
//         * <p>
//         * By default, <em>all</em> options and positional parameters are included in the usage help message
//         * <em>except when explicitly marked {@linkplain #hidden() hidden}.</em>
//         * </p><p>
//         * If this option is specified on the command line, picocli will not validate the remaining arguments (so no "missing required
//         * option" errors) and the {@link CommandLine#isUsageHelpRequested()} method will return {@code true}.
//         * </p><p>
//         * Alternatively, consider annotating your command with {@linkplain Command#mixinStandardHelpOptions() @Command(mixinStandardHelpOptions = true)}.
//         * </p>
//         * @return whether this option allows the user to request usage help
//         * @since 0.9.8
//         * @see #hidden()
//         * @see #run(Runnable, String...)
//         * @see #call(Callable, String...)
//         * @see #parseWithHandler(IParseResultHandler2, String[])
//         * @see #printHelpIfRequested(List, PrintStream, PrintStream, Help.Ansi)
//         */
//        boolean usageHelp() default false;
//
//        /**
//         * Set {@code versionHelp=true} for the {@code --version} option that triggers display of the version information.
//         * The <a href="http://picocli.info/#_printing_help_automatically">convenience methods</a> {@code Commandline.call},
//         * {@code Commandline.run}, and {@code Commandline.parseWithHandler(s)} will automatically print version information
//         * when an option with {@code versionHelp=true} was specified on the command line.
//         * <p>
//         * The version information string is obtained from the command's {@linkplain Command#version() version} annotation
//         * or from the {@linkplain Command#versionProvider() version provider}.
//         * </p><p>
//         * If this option is specified on the command line, picocli will not validate the remaining arguments (so no "missing required
//         * option" errors) and the {@link CommandLine#isUsageHelpRequested()} method will return {@code true}.
//         * </p><p>
//         * Alternatively, consider annotating your command with {@linkplain Command#mixinStandardHelpOptions() @Command(mixinStandardHelpOptions = true)}.
//         * </p>
//         * @return whether this option allows the user to request version information
//         * @since 0.9.8
//         * @see #hidden()
//         * @see #run(Runnable, String...)
//         * @see #call(Callable, String...)
//         * @see #parseWithHandler(IParseResultHandler2, String[])
//         * @see #printHelpIfRequested(List, PrintStream, PrintStream, Help.Ansi)
//         */
//        boolean versionHelp() default false;
//
//        /**
//         * Description of this option, used when generating the usage documentation.
//         * <p>
//         * From picocli 3.2, the usage string may contain variables that are rendered when help is requested.
//         * The string {@code ${DEFAULT-VALUE}} is replaced with the default value of the option. This is regardless of
//         * the command's {@link Command#showDefaultValues() showDefaultValues} setting or the option's {@link #showDefaultValue() showDefaultValue} setting.
//         * The string {@code ${COMPLETION-CANDIDATES}} is replaced with the completion candidates generated by
//         * {@link #completionCandidates()} in the description for this option.
//         * Also, embedded {@code %n} newline markers are converted to actual newlines.
//         * </p>
//         * @return the description of this option
//         */
//        String[] description() default {};
//
//        /**
//         * Specifies the minimum number of required parameters and the maximum number of accepted parameters.
//         * If an option declares a positive arity, and the user specifies an insufficient number of parameters on the
//         * command line, a {@link MissingParameterException} is thrown by the {@link #parse(String...)} method.
//         * <p>
//         * In many cases picocli can deduce the number of required parameters from the field's type.
//         * By default, flags (boolean options) have arity zero,
//         * and single-valued type fields (String, int, Integer, double, Double, File, Date, etc) have arity one.
//         * Generally, fields with types that cannot hold multiple values can omit the {@code arity} attribute.
//         * </p><p>
//         * Fields used to capture options with arity two or higher should have a type that can hold multiple values,
//         * like arrays or Collections. See {@link #type()} for strongly-typed Collection fields.
//         * </p><p>
//         * For example, if an option has 2 required parameters and any number of optional parameters,
//         * specify {@code @Option(names = "-example", arity = "2..*")}.
//         * </p>
//         * <b>A note on boolean options</b>
//         * <p>
//         * By default picocli does not expect boolean options (also called "flags" or "switches") to have a parameter.
//         * You can make a boolean option take a required parameter by annotating your field with {@code arity="1"}.
//         * For example: </p>
//         * <pre>&#064;Option(names = "-v", arity = "1") boolean verbose;</pre>
//         * <p>
//         * Because this boolean field is defined with arity 1, the user must specify either {@code <program> -v false}
//         * or {@code <program> -v true}
//         * on the command line, or a {@link MissingParameterException} is thrown by the {@link #parse(String...)}
//         * method.
//         * </p><p>
//         * To make the boolean parameter possible but optional, define the field with {@code arity = "0..1"}.
//         * For example: </p>
//         * <pre>&#064;Option(names="-v", arity="0..1") boolean verbose;</pre>
//         * <p>This will accept any of the below without throwing an exception:</p>
//         * <pre>
//         * -v
//         * -v true
//         * -v false
//         * </pre>
//         * @return how many arguments this option requires
//         */
//        String arity() default "";
//
//        /**
//         * Specify a {@code paramLabel} for the option parameter to be used in the usage help message. If omitted,
//         * picocli uses the field name in fish brackets ({@code '<'} and {@code '>'}) by default. Example:
//         * <pre>class Example {
//         *     &#064;Option(names = {"-o", "--output"}, paramLabel="FILE", description="path of the output file")
//         *     private File out;
//         *     &#064;Option(names = {"-j", "--jobs"}, arity="0..1", description="Allow N jobs at once; infinite jobs with no arg.")
//         *     private int maxJobs = -1;
//         * }</pre>
//         * <p>By default, the above gives a usage help message like the following:</p><pre>
//         * Usage: &lt;main class&gt; [OPTIONS]
//         * -o, --output FILE       path of the output file
//         * -j, --jobs [&lt;maxJobs&gt;]  Allow N jobs at once; infinite jobs with no arg.
//         * </pre>
//         * @return name of the option parameter used in the usage help message
//         */
//        String paramLabel() default "";
//
//        /** Returns whether usage syntax decorations around the {@linkplain #paramLabel() paramLabel} should be suppressed.
//         * The default is {@code false}: by default, the paramLabel is surrounded with {@code '['} and {@code ']'} characters
//         * if the value is optional and followed by ellipses ("...") when multiple values can be specified.
//         * @since 3.6.0 */
//        boolean hideParamSyntax() default false;
//
//        /** <p>
//         * Optionally specify a {@code type} to control exactly what Class the option parameter should be converted
//         * to. This may be useful when the field type is an interface or an abstract class. For example, a field can
//         * be declared to have type {@code java.lang.Number}, and annotating {@code @Option(type=Short.class)}
//         * ensures that the option parameter value is converted to a {@code Short} before setting the field value.
//         * </p><p>
//         * For array fields whose <em>component</em> type is an interface or abstract class, specify the concrete <em>component</em> type.
//         * For example, a field with type {@code Number[]} may be annotated with {@code @Option(type=Short.class)}
//         * to ensure that option parameter values are converted to {@code Short} before adding an element to the array.
//         * </p><p>
//         * Picocli will use the {@link ITypeConverter} that is
//         * {@linkplain #registerConverter(Class, ITypeConverter) registered} for the specified type to convert
//         * the raw String values before modifying the field value.
//         * </p><p>
//         * Prior to 2.0, the {@code type} attribute was necessary for {@code Collection} and {@code Map} fields,
//         * but starting from 2.0 picocli will infer the component type from the generic type's type arguments.
//         * For example, for a field of type {@code Map<TimeUnit, Long>} picocli will know the option parameter
//         * should be split up in key=value pairs, where the key should be converted to a {@code java.util.concurrent.TimeUnit}
//         * enum value, and the value should be converted to a {@code Long}. No {@code @Option(type=...)} type attribute
//         * is required for this. For generic types with wildcards, picocli will take the specified upper or lower bound
//         * as the Class to convert to, unless the {@code @Option} annotation specifies an explicit {@code type} attribute.
//         * </p><p>
//         * If the field type is a raw collection or a raw map, and you want it to contain other values than Strings,
//         * or if the generic type's type arguments are interfaces or abstract classes, you may
//         * specify a {@code type} attribute to control the Class that the option parameter should be converted to.
//         * @return the type(s) to convert the raw String values
//         */
//        Class<?>[] type() default {};
//
//        /**
//         * Optionally specify one or more {@link ITypeConverter} classes to use to convert the command line argument into
//         * a strongly typed value (or key-value pair for map fields). This is useful when a particular field should
//         * use a custom conversion that is different from the normal conversion for the field's type.
//         * <p>For example, for a specific field you may want to use a converter that maps the constant names defined
//         * in {@link java.sql.Types java.sql.Types} to the {@code int} value of these constants, but any other {@code int} fields should
//         * not be affected by this and should continue to use the standard int converter that parses numeric values.</p>
//         * @return the type converter(s) to use to convert String values to strongly typed values for this field
//         * @see CommandLine#registerConverter(Class, ITypeConverter)
//         */
//        Class<? extends ITypeConverter<?>>[] converter() default {};
//
//        /**
//         * Specify a regular expression to use to split option parameter values before applying them to the field.
//         * All elements resulting from the split are added to the array or Collection. Ignored for single-value fields.
//         * @return a regular expression to split option parameter values or {@code ""} if the value should not be split
//         * @see String#split(String)
//         */
//        String split() default "";
//
//        /**
//         * Set {@code hidden=true} if this option should not be included in the usage help message.
//         * @return whether this option should be excluded from the usage documentation
//         */
//        boolean hidden() default false;
//
//        /** Returns the default value of this option, before splitting and type conversion.
//         * @return a String that (after type conversion) will be used as the value for this option if no value was specified on the command line
//         * @since 3.2 */
//        String defaultValue() default "__no_default_value__";
//
//        /** Use this attribute to control for a specific option whether its default value should be shown in the usage
//         * help message. If not specified, the default value is only shown when the {@link Command#showDefaultValues()}
//         * is set {@code true} on the command. Use this attribute to specify whether the default value
//         * for this specific option should always be shown or never be shown, regardless of the command setting.
//         * <p>Note that picocli 3.2 allows {@linkplain #description() embedding default values} anywhere in the description that ignores this setting.</p>
//         * @return whether this option's default value should be shown in the usage help message
//         */
//        Help.Visibility showDefaultValue() default Help.Visibility.ON_DEMAND;
//
//        /** Use this attribute to specify an {@code Iterable<String>} class that generates completion candidates for this option.
//         * For map fields, completion candidates should be in {@code key=value} form.
//         * <p>
//         * Completion candidates are used in bash completion scripts generated by the {@code picocli.AutoComplete} class.
//         * Bash has special completion options to generate file names and host names, and the bash completion scripts
//         * generated by {@code AutoComplete} delegate to these bash built-ins for {@code @Options} whose {@code type} is
//         * {@code java.io.File}, {@code java.nio.file.Path} or {@code java.net.InetAddress}.
//         * </p><p>
//         * For {@code @Options} whose {@code type} is a Java {@code enum}, {@code AutoComplete} can generate completion
//         * candidates from the type. For other types, use this attribute to specify completion candidates.
//         * </p>
//         *
//         * @return a class whose instances can iterate over the completion candidates for this option
//         * @see picocli.CommandLine.IFactory
//         * @since 3.2 */
//        Class<? extends Iterable<String>> completionCandidates() default NoCompletionCandidates.class;
//
//        /**
//         * Set {@code interactive=true} if this option will prompt the end user for a value (like a password).
//         * Only supported for single-value options (not arrays, collections or maps).
//         * When running on Java 6 or greater, this will use the {@link Console#readPassword()} API to get a value without echoing input to the console.
//         * @return whether this option prompts the end user for a value to be entered on the command line
//         * @since 3.5
//         */
//        boolean interactive() default false;
//
//        /** ResourceBundle key for this option. If not specified, (and a ResourceBundle {@linkplain Command#resourceBundle() exists for this command}) an attempt
//         * is made to find the option description using any of the option names (without leading hyphens) as key.
//         * @see OptionSpec#description()
//         * @since 3.6
//         */
//        String descriptionKey() default "";
//    }

//    public @interface Parameters {
//        /** Specify an index ("0", or "1", etc.) to pick which of the command line arguments should be assigned to this
//         * field. For array or Collection fields, you can also specify an index range ("0..3", or "2..*", etc.) to assign
//         * a subset of the command line arguments to this field. The default is "*", meaning all command line arguments.
//         * @return an index or range specifying which of the command line arguments should be assigned to this field
//         */
//        String index() default "";
//
//        /** Description of the parameter(s), used when generating the usage documentation.
//         * <p>
//         * From picocli 3.2, the usage string may contain variables that are rendered when help is requested.
//         * The string {@code ${DEFAULT-VALUE}} is replaced with the default value of the positional parameter. This is regardless of
//         * the command's {@link Command#showDefaultValues() showDefaultValues} setting or the positional parameter's {@link #showDefaultValue() showDefaultValue} setting.
//         * The string {@code ${COMPLETION-CANDIDATES}} is replaced with the completion candidates generated by
//         * {@link #completionCandidates()} in the description for this positional parameter.
//         * Also, embedded {@code %n} newline markers are converted to actual newlines.
//         * </p>
//         * @return the description of the parameter(s)
//         */
//        String[] description() default {};
//
//        /**
//         * Specifies the minimum number of required parameters and the maximum number of accepted parameters. If a
//         * positive arity is declared, and the user specifies an insufficient number of parameters on the command line,
//         * {@link MissingParameterException} is thrown by the {@link #parse(String...)} method.
//         * <p>The default depends on the type of the parameter: booleans require no parameters, arrays and Collections
//         * accept zero to any number of parameters, and any other type accepts one parameter.</p>
//         * @return the range of minimum and maximum parameters accepted by this command
//         */
//        String arity() default "";
//
//        /**
//         * Specify a {@code paramLabel} for the parameter to be used in the usage help message. If omitted,
//         * picocli uses the field name in fish brackets ({@code '<'} and {@code '>'}) by default. Example:
//         * <pre>class Example {
//         *     &#064;Parameters(paramLabel="FILE", description="path of the input FILE(s)")
//         *     private File[] inputFiles;
//         * }</pre>
//         * <p>By default, the above gives a usage help message like the following:</p><pre>
//         * Usage: &lt;main class&gt; [FILE...]
//         * [FILE...]       path of the input FILE(s)
//         * </pre>
//         * @return name of the positional parameter used in the usage help message
//         */
//        String paramLabel() default "";
//
//        /** Returns whether usage syntax decorations around the {@linkplain #paramLabel() paramLabel} should be suppressed.
//         * The default is {@code false}: by default, the paramLabel is surrounded with {@code '['} and {@code ']'} characters
//         * if the value is optional and followed by ellipses ("...") when multiple values can be specified.
//         * @since 3.6.0 */
//        boolean hideParamSyntax() default false;
//
//        /**
//         * <p>
//         * Optionally specify a {@code type} to control exactly what Class the positional parameter should be converted
//         * to. This may be useful when the field type is an interface or an abstract class. For example, a field can
//         * be declared to have type {@code java.lang.Number}, and annotating {@code @Parameters(type=Short.class)}
//         * ensures that the positional parameter value is converted to a {@code Short} before setting the field value.
//         * </p><p>
//         * For array fields whose <em>component</em> type is an interface or abstract class, specify the concrete <em>component</em> type.
//         * For example, a field with type {@code Number[]} may be annotated with {@code @Parameters(type=Short.class)}
//         * to ensure that positional parameter values are converted to {@code Short} before adding an element to the array.
//         * </p><p>
//         * Picocli will use the {@link ITypeConverter} that is
//         * {@linkplain #registerConverter(Class, ITypeConverter) registered} for the specified type to convert
//         * the raw String values before modifying the field value.
//         * </p><p>
//         * Prior to 2.0, the {@code type} attribute was necessary for {@code Collection} and {@code Map} fields,
//         * but starting from 2.0 picocli will infer the component type from the generic type's type arguments.
//         * For example, for a field of type {@code Map<TimeUnit, Long>} picocli will know the positional parameter
//         * should be split up in key=value pairs, where the key should be converted to a {@code java.util.concurrent.TimeUnit}
//         * enum value, and the value should be converted to a {@code Long}. No {@code @Parameters(type=...)} type attribute
//         * is required for this. For generic types with wildcards, picocli will take the specified upper or lower bound
//         * as the Class to convert to, unless the {@code @Parameters} annotation specifies an explicit {@code type} attribute.
//         * </p><p>
//         * If the field type is a raw collection or a raw map, and you want it to contain other values than Strings,
//         * or if the generic type's type arguments are interfaces or abstract classes, you may
//         * specify a {@code type} attribute to control the Class that the positional parameter should be converted to.
//         * @return the type(s) to convert the raw String values
//         */
//        Class<?>[] type() default {};
//
//        /**
//         * Optionally specify one or more {@link ITypeConverter} classes to use to convert the command line argument into
//         * a strongly typed value (or key-value pair for map fields). This is useful when a particular field should
//         * use a custom conversion that is different from the normal conversion for the field's type.
//         * <p>For example, for a specific field you may want to use a converter that maps the constant names defined
//         * in {@link java.sql.Types java.sql.Types} to the {@code int} value of these constants, but any other {@code int} fields should
//         * not be affected by this and should continue to use the standard int converter that parses numeric values.</p>
//         * @return the type converter(s) to use to convert String values to strongly typed values for this field
//         * @see CommandLine#registerConverter(Class, ITypeConverter)
//         */
//        Class<? extends ITypeConverter<?>>[] converter() default {};
//
//        /**
//         * Specify a regular expression to use to split positional parameter values before applying them to the field.
//         * All elements resulting from the split are added to the array or Collection. Ignored for single-value fields.
//         * @return a regular expression to split operand values or {@code ""} if the value should not be split
//         * @see String#split(String)
//         */
//        String split() default "";
//
//        /**
//         * Set {@code hidden=true} if this parameter should not be included in the usage message.
//         * @return whether this parameter should be excluded from the usage message
//         */
//        boolean hidden() default false;
//
//        /** Returns the default value of this positional parameter, before splitting and type conversion.
//         * @return a String that (after type conversion) will be used as the value for this positional parameter if no value was specified on the command line
//         * @since 3.2 */
//        String defaultValue() default "__no_default_value__";
//
//        /** Use this attribute to control for a specific positional parameter whether its default value should be shown in the usage
//         * help message. If not specified, the default value is only shown when the {@link Command#showDefaultValues()}
//         * is set {@code true} on the command. Use this attribute to specify whether the default value
//         * for this specific positional parameter should always be shown or never be shown, regardless of the command setting.
//         * <p>Note that picocli 3.2 allows {@linkplain #description() embedding default values} anywhere in the description that ignores this setting.</p>
//         * @return whether this positional parameter's default value should be shown in the usage help message
//         */
//        Help.Visibility showDefaultValue() default Help.Visibility.ON_DEMAND;
//
//        /** Use this attribute to specify an {@code Iterable<String>} class that generates completion candidates for
//         * this positional parameter. For map fields, completion candidates should be in {@code key=value} form.
//         * <p>
//         * Completion candidates are used in bash completion scripts generated by the {@code picocli.AutoComplete} class.
//         * Unfortunately, {@code picocli.AutoComplete} is not very good yet at generating completions for positional parameters.
//         * </p>
//         *
//         * @return a class whose instances can iterate over the completion candidates for this positional parameter
//         * @see picocli.CommandLine.IFactory
//         * @since 3.2 */
//        Class<? extends Iterable<String>> completionCandidates() default NoCompletionCandidates.class;
//
//        /**
//         * Set {@code interactive=true} if this positional parameter will prompt the end user for a value (like a password).
//         * Only supported for single-value positional parameters (not arrays, collections or maps).
//         * When running on Java 6 or greater, this will use the {@link Console#readPassword()} API to get a value without echoing input to the console.
//         * @return whether this positional parameter prompts the end user for a value to be entered on the command line
//         * @since 3.5
//         */
//        boolean interactive() default false;
//
//        /** ResourceBundle key for this option. If not specified, (and a ResourceBundle {@linkplain Command#resourceBundle() exists for this command}) an attempt
//         * is made to find the positional parameter description using {@code paramLabel() + "[" + index() + "]"} as key.
//         *
//         * @see PositionalParamSpec#description()
//         * @since 3.6
//         */
//        String descriptionKey() default "";
//    }
//    public @interface Command {
//        /** Program name to show in the synopsis. If omitted, {@code "<main class>"} is used.
//         * For {@linkplain #subcommands() declaratively added} subcommands, this attribute is also used
//         * by the parser to recognize subcommands in the command line arguments.
//         * @return the program name to show in the synopsis
//         * @see CommandSpec#name()
//         * @see Help#commandName() */
//        String name() default "<main class>";
//
//        /** Alternative command names by which this subcommand is recognized on the command line.
//         * @return one or more alternative command names
//         * @since 3.1 */
//        String[] aliases() default {};
//
//        /** A list of classes to instantiate and register as subcommands. When registering subcommands declaratively
//         * like this, you don't need to call the {@link CommandLine#addSubcommand(String, Object)} method. For example, this:
//         * <pre>
//         * &#064;Command(subcommands = {
//         *         GitStatus.class,
//         *         GitCommit.class,
//         *         GitBranch.class })
//         * public class Git { ... }
//         *
//         * CommandLine commandLine = new CommandLine(new Git());
//         * </pre> is equivalent to this:
//         * <pre>
//         * // alternative: programmatically add subcommands.
//         * // NOTE: in this case there should be no `subcommands` attribute on the @Command annotation.
//         * &#064;Command public class Git { ... }
//         *
//         * CommandLine commandLine = new CommandLine(new Git())
//         *         .addSubcommand("status",   new GitStatus())
//         *         .addSubcommand("commit",   new GitCommit())
//         *         .addSubcommand("branch",   new GitBranch());
//         * </pre>
//         * @return the declaratively registered subcommands of this command, or an empty array if none
//         * @see CommandLine#addSubcommand(String, Object)
//         * @see HelpCommand
//         * @since 0.9.8
//         */
//        Class<?>[] subcommands() default {};
//
//        /** Specify whether methods annotated with {@code @Command} should be registered as subcommands of their
//         * enclosing {@code @Command} class.
//         * The default is {@code true}. For example:
//         * <pre>
//         * &#064;Command
//         * public class Git {
//         *     &#064;Command
//         *     void status() { ... }
//         * }
//         *
//         * CommandLine git = new CommandLine(new Git());
//         * </pre> is equivalent to this:
//         * <pre>
//         * // don't add command methods as subcommands automatically
//         * &#064;Command(addMethodSubcommands = false)
//         * public class Git {
//         *     &#064;Command
//         *     void status() { ... }
//         * }
//         *
//         * // add command methods as subcommands programmatically
//         * CommandLine git = new CommandLine(new Git());
//         * CommandLine status = new CommandLine(CommandLine.getCommandMethods(Git.class, "status").get(0));
//         * git.addSubcommand("status", status);
//         * </pre>
//         * @return whether methods annotated with {@code @Command} should be registered as subcommands
//         * @see CommandLine#addSubcommand(String, Object)
//         * @see CommandLine#getCommandMethods(Class, String)
//         * @see CommandSpec#addMethodSubcommands()
//         * @since 3.6.0 */
//        boolean addMethodSubcommands() default true;
//
//        /** String that separates options from option parameters. Default is {@code "="}. Spaces are also accepted.
//         * @return the string that separates options from option parameters, used both when parsing and when generating usage help
//         * @see CommandLine#setSeparator(String) */
//        String separator() default "=";
//
//        /** Version information for this command, to print to the console when the user specifies an
//         * {@linkplain Option#versionHelp() option} to request version help. This is not part of the usage help message.
//         *
//         * @return a string or an array of strings with version information about this command (each string in the array is displayed on a separate line).
//         * @since 0.9.8
//         * @see CommandLine#printVersionHelp(PrintStream)
//         */
//        String[] version() default {};
//
//        /** Class that can provide version information dynamically at runtime. An implementation may return version
//         * information obtained from the JAR manifest, a properties file or some other source.
//         * @return a Class that can provide version information dynamically at runtime
//         * @since 2.2 */
//        Class<? extends IVersionProvider> versionProvider() default NoVersionProvider.class;
//
//        /**
//         * Adds the standard {@code -h} and {@code --help} {@linkplain Option#usageHelp() usageHelp} options and {@code -V}
//         * and {@code --version} {@linkplain Option#versionHelp() versionHelp} options to the options of this command.
//         * <p>
//         * Note that if no {@link #version()} or {@link #versionProvider()} is specified, the {@code --version} option will not print anything.
//         * </p><p>
//         * For {@linkplain #resourceBundle() internationalization}: the help option has {@code descriptionKey = "mixinStandardHelpOptions.help"},
//         * and the version option has {@code descriptionKey = "mixinStandardHelpOptions.version"}.
//         * </p>
//         * @return whether the auto-help mixin should be added to this command
//         * @since 3.0 */
//        boolean mixinStandardHelpOptions() default false;
//
//        /** Set this attribute to {@code true} if this subcommand is a help command, and required options and positional
//         * parameters of the parent command should not be validated. If a subcommand marked as {@code helpCommand} is
//         * specified on the command line, picocli will not validate the parent arguments (so no "missing required
//         * option" errors) and the {@link CommandLine#printHelpIfRequested(List, PrintStream, PrintStream, Help.Ansi)} method will return {@code true}.
//         * @return {@code true} if this subcommand is a help command and picocli should not check for missing required
//         *      options and positional parameters on the parent command
//         * @since 3.0 */
//        boolean helpCommand() default false;
//
//        /** Set the heading preceding the header section. May contain embedded {@linkplain java.util.Formatter format specifiers}.
//         * @return the heading preceding the header section
//         * @see UsageMessageSpec#headerHeading()
//         * @see Help#headerHeading(Object...)  */
//        String headerHeading() default "";
//
//        /** Optional summary description of the command, shown before the synopsis.
//         * @return summary description of the command
//         * @see UsageMessageSpec#header()
//         * @see Help#header(Object...)  */
//        String[] header() default {};
//
//        /** Set the heading preceding the synopsis text. May contain embedded
//         * {@linkplain java.util.Formatter format specifiers}. The default heading is {@code "Usage: "} (without a line
//         * break between the heading and the synopsis text).
//         * @return the heading preceding the synopsis text
//         * @see Help#synopsisHeading(Object...)  */
//        String synopsisHeading() default "Usage: ";
//
//        /** Specify {@code true} to generate an abbreviated synopsis like {@code "<main> [OPTIONS] [PARAMETERS...]"}.
//         * By default, a detailed synopsis with individual option names and parameters is generated.
//         * @return whether the synopsis should be abbreviated
//         * @see Help#abbreviatedSynopsis()
//         * @see Help#detailedSynopsis(Comparator, boolean) */
//        boolean abbreviateSynopsis() default false;
//
//        /** Specify one or more custom synopsis lines to display instead of an auto-generated synopsis.
//         * @return custom synopsis text to replace the auto-generated synopsis
//         * @see Help#customSynopsis(Object...) */
//        String[] customSynopsis() default {};
//
//        /** Set the heading preceding the description section. May contain embedded {@linkplain java.util.Formatter format specifiers}.
//         * @return the heading preceding the description section
//         * @see Help#descriptionHeading(Object...)  */
//        String descriptionHeading() default "";
//
//        /** Optional text to display between the synopsis line(s) and the list of options.
//         * @return description of this command
//         * @see Help#description(Object...) */
//        String[] description() default {};
//
//        /** Set the heading preceding the parameters list. May contain embedded {@linkplain java.util.Formatter format specifiers}.
//         * @return the heading preceding the parameters list
//         * @see Help#parameterListHeading(Object...)  */
//        String parameterListHeading() default "";
//
//        /** Set the heading preceding the options list. May contain embedded {@linkplain java.util.Formatter format specifiers}.
//         * @return the heading preceding the options list
//         * @see Help#optionListHeading(Object...)  */
//        String optionListHeading() default "";
//
//        /** Specify {@code false} to show Options in declaration order. The default is to sort alphabetically.
//         * @return whether options should be shown in alphabetic order. */
//        boolean sortOptions() default true;
//
//        /** Prefix required options with this character in the options list. The default is no marker: the synopsis
//         * indicates which options and parameters are required.
//         * @return the character to show in the options list to mark required options */
//        char requiredOptionMarker() default ' ';
//
//        /** Class that can provide default values dynamically at runtime. An implementation may return default
//         * value obtained from a configuration file like a properties file or some other source.
//         * @return a Class that can provide default values dynamically at runtime
//         * @since 3.6 */
//        Class<? extends IDefaultValueProvider> defaultValueProvider() default NoDefaultProvider.class;
//
//        /** Specify {@code true} to show default values in the description column of the options list (except for
//         * boolean options). False by default.
//         * <p>Note that picocli 3.2 allows {@linkplain Option#description() embedding default values} anywhere in the
//         * option or positional parameter description that ignores this setting.</p>
//         * @return whether the default values for options and parameters should be shown in the description column */
//        boolean showDefaultValues() default false;
//
//        /** Set the heading preceding the subcommands list. May contain embedded {@linkplain java.util.Formatter format specifiers}.
//         * The default heading is {@code "Commands:%n"} (with a line break at the end).
//         * @return the heading preceding the subcommands list
//         * @see Help#commandListHeading(Object...)  */
//        String commandListHeading() default "Commands:%n";
//
//        /** Set the heading preceding the footer section. May contain embedded {@linkplain java.util.Formatter format specifiers}.
//         * @return the heading preceding the footer section
//         * @see Help#footerHeading(Object...)  */
//        String footerHeading() default "";
//
//        /** Optional text to display after the list of options.
//         * @return text to display after the list of options
//         * @see Help#footer(Object...) */
//        String[] footer() default {};
//
//        /**
//         * Set {@code hidden=true} if this command should not be included in the list of commands in the usage help of the parent command.
//         * @return whether this command should be excluded from the usage message
//         * @since 3.0
//         */
//        boolean hidden() default false;
//
//        /** Set the base name of the ResourceBundle to find option and positional parameters descriptions, as well as
//         * usage help message sections and section headings. <p>See {@link Messages} for more details and an example.</p>
//         * @return the base name of the ResourceBundle for usage help strings
//         * @see ArgSpec#messages()
//         * @see UsageMessageSpec#messages()
//         * @see CommandSpec#resourceBundle()
//         * @see CommandLine#setResourceBundle(ResourceBundle)
//         * @since 3.6
//         */
//        String resourceBundle() default "";
//
//        /** Set the {@link UsageMessageSpec#width(int) usage help message width}. The default is 80.
//         * @since 3.7
//         */
//        int usageHelpWidth() default 80;
//    }
}
