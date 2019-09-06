package org.xmlcml.args;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.QuickscrapeNormaList;
import org.xmlcml.xml.XMLUtil;

@Deprecated

public class DefaultArgProcessor {

	
	private static final Logger LOG = Logger.getLogger(DefaultArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String MINUS = "-";
	public static final String[] DEFAULT_EXTENSIONS = {"html", "xml", "pdf"};
	public final static String H = "-h";
	public final static String HELP = "--help";
	private static Pattern INTEGER_RANGE = Pattern.compile("(.*)\\{(\\d+),(\\d+)\\}(.*)");

	private static String RESOURCE_NAME_TOP = "/org/xmlcml/args";
	private static String ARGS_RESOURCE = RESOURCE_NAME_TOP+"/"+"args.xml";
	
	private static final Pattern INTEGER_RANGE_PATTERN = Pattern.compile("(\\d+):(\\d+)");
	protected static final String ARGS_XML = "args.xml";
	public static Pattern GENERAL_PATTERN = Pattern.compile("\\{([^\\}]*)\\}");
	
	/** creates a list of tokens that are found in an allowed list.
	 * 
	 * @param allowed
	 * @param tokens
	 * @return list of allowed tokens
	 */
	protected static List<String> getChosenList(List<String> allowed, List<String> tokens) {
		List<String> chosenTokens = new ArrayList<String>();
		for (String method : tokens) {
			if (allowed.contains(method)) {
				chosenTokens.add(method);
			} else {
				LOG.error("Unknown token: "+method);
			}
		}
		return chosenTokens;
	}

	protected String output;
	protected List<String> extensionList = null;
	private boolean recursive = false;
	protected List<String> inputList;
	public List<ArgumentOption> argumentOptionList;
	public List<ArgumentOption> chosenArgumentOptionList;
	protected QuickscrapeNormaList quickscrapeNormaList;
	protected QuickscrapeNorma currentQuickscrapeNorma;
	protected String summaryFileName;
	protected Map<String, String> variableByNameMap;
	private VariableProcessor variableProcessor;
	
	
	
	protected List<ArgumentOption> getArgumentOptionList() {
		return argumentOptionList;
	}

	public DefaultArgProcessor() {
		readArgumentOptions(ARGS_RESOURCE);
	}
	
	public DefaultArgProcessor(String resourceName) {
		this();
		readArgumentOptions(resourceName);
	}
	
	public void readArgumentOptions(String resourceName) {
		ensureArgumentOptionList();
		try {
			InputStream is = this.getClass().getResourceAsStream(resourceName);
			if (is == null) {
				throw new RuntimeException("Cannot read/find input resource stream: "+resourceName);
			}
			Element argElement = new Builder().build(is).getRootElement();
			List<Element> elementList = XMLUtil.getQueryElements(argElement, "/*/*[local-name()='arg']");
			for (Element element : elementList) {
				ArgumentOption argOption = ArgumentOption.createOption(this.getClass(), element);
				LOG.trace("created ArgumentOption: "+argOption);
				argumentOptionList.add(argOption);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/process args file "+resourceName, e);
		}
	}
	
	private void ensureArgumentOptionList() {
		if (this.argumentOptionList == null) {
			this.argumentOptionList = new ArrayList<ArgumentOption>();
		}
	}

	public void expandWildcardsExhaustively() {
		while (expandWildcardsOnce());
	}
	
	public boolean expandWildcardsOnce() {
		boolean change = false;
		ensureInputList();
		List<String> newInputList = new ArrayList<String>();
		for (String input : inputList) {
			List<String> expanded = expandWildcardsOnce(input);
			newInputList.addAll(expanded);
			change |= (expanded.size() > 1 || !expanded.get(0).equals(input));
		}
		inputList = newInputList;
		return change;
	}


	/** expand expressions/wildcards in input.
	 * 
	 * @param input
	 * @return
	 */
	private List<String> expandWildcardsOnce(String input) {
		Matcher matcher = GENERAL_PATTERN.matcher(input);
		List<String> inputs = new ArrayList<String>(); 
		if (matcher.find()) {
			String content = matcher.group(1);
			String pre = input.substring(0, matcher.start());
			String post = input.substring(matcher.end());
			inputs = expandIntegerMatch(content, pre, post);
			if (inputs.size() == 0) {
				inputs = expandStrings(content, pre, post);
			} 
			if (inputs.size() == 0) {
				LOG.error("Cannot expand "+content);
			}
		} else {
			inputs.add(input);
		}
		return inputs;
	}

	private List<String> expandIntegerMatch(String content, String pre, String post) {
		List<String> stringList = new ArrayList<String>();
		Matcher matcher = INTEGER_RANGE_PATTERN.matcher(content);
		if (matcher.find()) {
			int start = Integer.parseInt(matcher.group(1));
			int end = Integer.parseInt(matcher.group(2));
			for (int i = start; i <= end; i++) {
				String s = pre + i + post;
				stringList.add(s);
			}
		}
		return stringList;
	}

	private List<String> expandStrings(String content, String pre, String post) {
		List<String> newStringList = new ArrayList<String>();
		List<String> vars = Arrays.asList(content.split("\\|"));
		for (String var : vars) {
			newStringList.add(pre + var + post);
		}
		
		return newStringList;
	}

	// ============ METHODS ===============

	public void parseExtensions(ArgumentOption option, ArgIterator argIterator) {
		List<String> extensions = argIterator.createTokenListUpToNextNonDigitMinus(option);
		setExtensions(extensions);
	}


	public void parseQuickscrapeNorma(ArgumentOption option, ArgIterator argIterator) {
		List<String> qDirectoryNames = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createQuickscrapeNormaList(qDirectoryNames);
	}

	public void printHelp(ArgumentOption option, ArgIterator argIterator) {
		printHelp();
	}

	public void parseInput(ArgumentOption option, ArgIterator argIterator) {
		List<String> inputs = argIterator.createTokenListUpToNextNonDigitMinus(option);
		inputList = expandAllWildcards(inputs);
	}


	public void parseOutput(ArgumentOption option, ArgIterator argIterator) {
		output = argIterator.getString(option);
	}

	public void parseRecursive(ArgumentOption option, ArgIterator argIterator) {
		recursive = argIterator.getBoolean(option);
	}

	public void parseSummaryFile(ArgumentOption option, ArgIterator argIterator) {
		summaryFileName = argIterator.getString(option);
	}

	public void outputMethod(ArgumentOption option) {
		LOG.error("outputMethod NYI");
	}

	// =====================================
	
	private void createQuickscrapeNormaList(List<String> qDirectoryNames) {
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		quickscrapeNormaList = new QuickscrapeNormaList();
		for (String qDirectoryName : qDirectoryNames) {
			File qDirectory = new File(qDirectoryName);
			if (!qDirectory.exists()) {
				LOG.error("File does not exist: "+qDirectory.getAbsolutePath());
				continue;
			}
			if (!qDirectory.isDirectory()) {
				LOG.error("Not a directory: "+qDirectory.getAbsolutePath());
				continue;
			}
			QuickscrapeNorma quickscrapeNorma = new QuickscrapeNorma(qDirectoryName);
			if (quickscrapeNorma.containsNoReservedFilenames()) {
				List<File> childFiles = new ArrayList<File>(Arrays.asList(qDirectory.listFiles(directoryFilter)));
				List<String> childFilenames = new ArrayList<String>();
				for (File childFile : childFiles) {
					if (childFile.isDirectory()) {
						childFilenames.add(childFile.toString());
					}
				}
				LOG.trace(childFilenames);
				// recurse (no mixed directory structures)
				createQuickscrapeNormaList(childFilenames);
			} else {
				quickscrapeNormaList.add(quickscrapeNorma);
			}
		}
	}


	private List<String> expandAllWildcards(List<String> inputs) {
		inputList = new ArrayList<String>();
		for (String input : inputs) {
			inputList.addAll(expandWildcards(input));
		}
		return inputList;
	}
	
	/** expand expressions/wildcards in input.
	 * 
	 * @param input
	 * @return
	 */
	private List<String> expandWildcards(String input) {
		Matcher matcher = INTEGER_RANGE.matcher(input);
		List<String> inputs = new ArrayList<String>();
		if (matcher.matches()) {
			int start = Integer.parseInt(matcher.group(2));
			int end = Integer.parseInt(matcher.group(3));
			if (start <= end) {
				for (int i = start; i <= end; i++) {
					String input0 = matcher.group(1)+i+matcher.group(4);
					inputs.add(input0);
				}
			}
		} else {
			inputs.add(input);
		}
		LOG.trace("inputs: "+inputs);
		return inputs;
	}

	// =====================================
	public void setExtensions(List<String> extensions) {
		this.extensionList = extensions;
	}


	public List<String> getInputList() {
		ensureInputList();
		return inputList;
	}

	public String getString() {
		ensureInputList();
		return (inputList.size() != 1) ? null : inputList.get(0);
	}
	private void ensureInputList() {
		if (inputList == null) {
			inputList = new ArrayList<String>();
		}
	}

	public String getOutput() {
		return output;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public String getSummaryFileName() {
		return summaryFileName;
	}

	public QuickscrapeNormaList getQuickscrapeNormaList() {
		ensureQuickscrapeNormaList();
		return quickscrapeNormaList;
	}

	protected void ensureQuickscrapeNormaList() {
		if (quickscrapeNormaList == null) {
			quickscrapeNormaList = new QuickscrapeNormaList();
		}
	}
	

	// --------------------------------
	
	public void parseArgs(String[] commandLineArgs) {
		if (commandLineArgs == null || commandLineArgs.length == 0) {
			printHelp();
		} else {
			String[] totalArgs = addDefaultsAndParsedArgs(commandLineArgs);
			ArgIterator argIterator = new ArgIterator(totalArgs);
			LOG.trace("args with defaults is: "+new ArrayList<String>(Arrays.asList(totalArgs)));
			while (argIterator.hasNext()) {
				String arg = argIterator.next();
				LOG.trace("arg> "+arg);
				try {
					addArgumentOptionsAndRunParseMethods(argIterator, arg);
				} catch (Exception e) {
					throw new RuntimeException("cannot process argument: "+arg+" ("+ExceptionUtils.getRootCauseMessage(e)+")", e);
				}
			}
			finalizeArgs();
		}
	}
	
	public void parseArgs(String args) {
		parseArgs(args.split("\\s+"));
	}

	private void finalizeArgs() {
		processArgumentDependencies();
		finalizeInputList();
	}

	private void processArgumentDependencies() {
		for (ArgumentOption argumentOption : chosenArgumentOptionList) {
			argumentOption.processDependencies(chosenArgumentOptionList);
		}
	}

	private void finalizeInputList() {
		List<String> inputList0 = new ArrayList<String>();
		ensureInputList();
		for (String input : inputList) {
			File file = new File(input);
			if (file.isDirectory()) {
				LOG.debug("DIR: "+file.getAbsolutePath()+"; "+file.isDirectory());
				addDirectoryFiles(inputList0, file);
			} else {
				inputList0.add(input);
			}
		}
		inputList = inputList0;
	}

	private void addDirectoryFiles(List<String> inputList0, File file) {
		String[] extensions = getExtensions().toArray(new String[0]);
		List<File> files = new ArrayList<File>(
				FileUtils.listFiles(file, extensions, recursive));
		for (File file0 : files) {
			inputList0.add(file0.toString());
		}
	}

	private String[] addDefaultsAndParsedArgs(String[] commandLineArgs) {
		String[] defaultArgs = createDefaultArgumentStrings();
		List<String> totalArgList = new ArrayList<String>(Arrays.asList(createDefaultArgumentStrings()));
		List<String> commandArgList = Arrays.asList(commandLineArgs);
		totalArgList.addAll(commandArgList);
		String[] totalArgs = totalArgList.toArray(new String[0]);
		return totalArgs;
	}

	private String[] createDefaultArgumentStrings() {
		StringBuilder sb = new StringBuilder();
		for (ArgumentOption option : argumentOptionList) {
			String defalt = String.valueOf(option.getDefault());
			if (defalt != null && defalt.toString().trim().length() > 0) {
				String command = getBriefOrVerboseCommand(option);
				sb.append(command+" "+option.getDefault()+" ");
			}
		}
		String s = sb.toString().trim();
		return s.length() == 0 ? new String[0] : s.split("\\s+");
	}

	private String getBriefOrVerboseCommand(ArgumentOption option) {
		String command = option.getBrief();
		if (command == null || command.trim().length() == 0) {
			command = option.getVerbose();
		}
		return command;
	}

	public List<String> getExtensions() {
		ensureExtensionList();
		return extensionList;
	}

	private void ensureExtensionList() {
		if (extensionList == null) {
			extensionList = new ArrayList<String>();
		}
	}
	
	public void runRunMethodsOnChosenArgOptions() {
		for (ArgumentOption option : chosenArgumentOptionList) {
			String runMethodName = option.getRunMethodName();
			LOG.trace("Method: "+runMethodName);
			if (runMethodName != null) {
				LOG.trace("Method " + runMethodName);
				try {
					runRunMethod(option);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("cannot process argument: "+option.getVerbose()+" ("+ExceptionUtils.getRootCauseMessage(e)+")", e);
				}
			}
		}
	}
	
	public void runOutputMethodsOnChosenArgOptions() {
		for (ArgumentOption option : chosenArgumentOptionList) {
			String outputMethodName = option.getOutputMethodName();
			LOG.trace("OUTPUT "+outputMethodName);
			if (outputMethodName != null) {
				try {
					runOutputMethod(option);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("cannot process argument: "+option.getVerbose()+" ("+ExceptionUtils.getRootCauseMessage(e)+")");
				}
			}
		}
	}

	public void runFinalMethodsOnChosenArgOptions() {
		ensureChosenArgumentList();
		for (ArgumentOption option : chosenArgumentOptionList) {
			String finalMethodName = option.getFinalMethodName();
			if (finalMethodName != null) {
				try {
					runFinalMethod(option);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("cannot process argument: "+option.getVerbose()+" ("+ExceptionUtils.getRootCauseMessage(e)+")");
				}
			}
		}
	}

	protected void addArgumentOptionsAndRunParseMethods(ArgIterator argIterator, String arg) throws Exception {
		ensureChosenArgumentList();
		boolean processed = false;
		if (!arg.startsWith(MINUS)) {
			LOG.error("Parsing failed at: ("+arg+"), expected \"-\" trying to recover");
		} else {
			for (ArgumentOption option : argumentOptionList) {
				if (option.matches(arg)) {
					LOG.trace("OPTION>> "+option);
					String initMethodName = option.getInitMethodName();
					if (initMethodName != null) {
						runInitMethod(option, initMethodName);
					}
					String parseMethodName = option.getParseMethodName();
					if (parseMethodName != null) {
						runParseMethod(argIterator, option, parseMethodName);
					}
					processed = true;
					chosenArgumentOptionList.add(option);
					break;
				}
			}
			if (!processed) {
				LOG.error("Unknown arg: ("+arg+"), trying to recover");
			}
		}
	}

	private void runInitMethod(ArgumentOption option, String initMethodName) {
		runMethod(null, option, initMethodName);
	}

	private void runParseMethod(ArgIterator argIterator, ArgumentOption option, String parseMethodName) {
		runMethod(argIterator, option, parseMethodName);
	}

	private void runMethod(ArgIterator argIterator, ArgumentOption option, String methodName) {
		Method method;
		try {
			if (argIterator == null) {
				method = this.getClass().getMethod(methodName, option.getClass());
			} else {
				method = this.getClass().getMethod(methodName, option.getClass(), argIterator.getClass());
			}
		} catch (NoSuchMethodException e) {
			debugMethods();
			throw new RuntimeException("Cannot find: "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";", e);
		}
		method.setAccessible(true);
		try {
			if (argIterator == null) {
					method.invoke(this, option);
			} else {
				method.invoke(this, option, argIterator);
			}
		} catch (Exception e) {
			LOG.trace("failed to run "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";"+e.getCause());
//			e.printStackTrace();
			throw new RuntimeException("Cannot run: "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";", e);
		}
	}

	private void debugMethods() {
		LOG.debug("methods for "+this.getClass());
		for (Method meth : this.getClass().getDeclaredMethods()) {
			LOG.debug(meth);
		}
	}

	protected void runRunMethod(ArgumentOption option) throws Exception {
		String runMethodName = option.getRunMethodName();
		if (runMethodName != null) {
			LOG.trace("running "+runMethodName);
			Method runMethod = null;
			try {
				runMethod = this.getClass().getMethod(runMethodName, option.getClass()); 
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException(runMethodName+"; "+this.getClass()+"; "+option.getClass()+"; \nContact Norma developers: ", nsme);
			}
			runMethod.setAccessible(true);
			runMethod.invoke(this, option);
		}
	}

	protected void runOutputMethod(ArgumentOption option) throws Exception {
		String outputMethodName = option.getOutputMethodName();
		if (outputMethodName != null) {
			Method outputMethod = null;
			try {
				outputMethod = this.getClass().getMethod(outputMethodName, option.getClass()); 
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException(outputMethodName+"; "+this.getClass()+"; "+option.getClass()+"; \nContact Norma developers: ", nsme);
			}
			outputMethod.setAccessible(true);
			outputMethod.invoke(this, option);
		}
	}

	protected void runFinalMethod(ArgumentOption option) throws Exception {
		String finalMethodName = option.getFinalMethodName();
		if (finalMethodName != null) {
			Method finalMethod = null;
			try {
				finalMethod = this.getClass().getMethod(finalMethodName, option.getClass()); 
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException(finalMethodName+"; "+this.getClass()+"; "+option.getClass()+"; \nContact Norma developers: ", nsme);
			}
			finalMethod.setAccessible(true);
			finalMethod.invoke(this, option);
		}
	}

	private void ensureChosenArgumentList() {
		if (chosenArgumentOptionList == null) {
			chosenArgumentOptionList = new ArrayList<ArgumentOption>();
		}
	}

	protected void printHelp() {
		for (ArgumentOption option : argumentOptionList) {
			System.err.println(option.getHelp());
		}
	}
	
	public List<ArgumentOption> getChosenArgumentList() {
		ensureChosenArgumentList();
		return chosenArgumentOptionList;
	}
	
	public String createDebugString() {
		StringBuilder sb = new StringBuilder();
		getChosenArgumentList();
		for (ArgumentOption argumentOption : chosenArgumentOptionList) {
			sb.append(argumentOption.toString()+"\n");
		}
		return sb.toString();
	}

	public void runAndOutput() {
		ensureQuickscrapeNormaList();
		if (quickscrapeNormaList.size() == 0) {
			LOG.warn("Could not find list of CMdirs; possible error");
		}
		for (int i = 0; i < quickscrapeNormaList.size(); i++) {
			currentQuickscrapeNorma = quickscrapeNormaList.get(i);
			runRunMethodsOnChosenArgOptions();
			runOutputMethodsOnChosenArgOptions();
		}
		runFinalMethodsOnChosenArgOptions();
	}

	protected void addVariableAndExpandReferences(String name, String value) {
		ensureVariableProcessor();
		try {
			variableProcessor.addVariableAndExpandReferences(name, value);
		} catch (Exception e) {
			LOG.error("add variable {"+name+", "+value+"} failed");
		}
	}

	public VariableProcessor ensureVariableProcessor() {
		if (variableProcessor == null) {
			variableProcessor = new VariableProcessor();
		}
		return variableProcessor;
	}

}
