package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMI.ShortErrorMessageHandler;
import org.contentmine.cproject.args.AbstractTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * Reusable Commands for picocli CommandLine
 * see Picocli manual
 *
 * @author pm286
 */
@Command(
		mixinStandardHelpOptions = true,
		abbreviateSynopsis = true, // because there are 21 common options defined in this class
		descriptionHeading = "Description%n===========%n",
		parameterListHeading = "Parameters%n=========%n",
		optionListHeading = "Options%n=======%n",
		commandListHeading = "Commands:%n=========%n",
		requiredOptionMarker = '*',
		showDefaultValues = true, // alternatively, we could switch this off and use ${DEFAULT-VALUE} in description text
		usageHelpWidth = 120,
		usageHelpAutoWidth = true,
		//addMethodSubcommands = false, // TODO confirm with Peter
		//separator = "=", // this is the default
		//helpCommand = true, // this is a normal command, not a help command
		//sortOptions = true, // this is the default
		//hidden = false, // this is the default

		// TODO I would like to automate this
		version = "${COMMAND-FULL-NAME} 20190228" // also edit ami-jars.sh
)
public abstract class AbstractAMITool implements Callable<Void>, AbstractTool {
	private static final String P = "-p";
	private static final String PROJECT = "--project";
	private static final String AMI = "AMI";
	private static final String TOOL = "Tool";

	private static final Logger LOG = Logger.getLogger(AbstractAMITool.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum IncExc {
		INCLUDE,
		EXCLUDE
	}

	/**
	 * maybe add subdirectory of tree later
	 *
	 * @author pm286
	 */
	public enum Scope {
		PROJECT(P),
		TREE("-t"),
		;
		private String abbrev;

		private Scope(String abbrev) {
			this.abbrev = abbrev;
		}

		public String getAbbrev() {
			return abbrev;
		}
	}

	/**
	 * subdirectories of CTree
	 *
	 * @author pm286
	 */
	public enum SubDirectoryType {
		pdfimages("pdfimaages"), // TODO check if this typo is intentional
		svg("svg"),
		;
		public final String subdirname;

		private SubDirectoryType(String subdir) {
			this.subdirname = subdir;
		}

		public String getSubdirectoryName() {
			return subdirname;
		}
	}

	public enum Verbosity {
		TRACE(3),
		DEBUG(2),
		INFO(3),
		;
		private int verbosity;

		private Verbosity(int v) {
			this.verbosity = v;
		}

		public int getVerbosity() {
			return verbosity;
		}
	}

	// injected by picocli
	@ParentCommand
	AMI parent;

	protected static final String NONE = "NONE";
	protected static final String RAW = "raw";

	static final String TRUNCATE = "%";

	public static final int TRACE = 3;
	public static final int DEBUG = 2;
	public static final int INFO = 1;
	protected static File HOME_DIR = new File(System.getProperty("user.home"));
	protected static String CONTENT_MINE_HOME = "ContentMine";
	protected static File DEFAULT_CONTENT_MINE_DIR = new File(HOME_DIR, CONTENT_MINE_HOME);

	public CProject cProject;
	protected CTree cTree;
	protected CTreeList cTreeList;
	// needed for testing I think
	protected File cProjectOutputDir;
	protected File cTreeOutputDir;

//	@Deprecated(/*forRemoval = true*/)
//	protected String[] args;

	private Level level;
	protected File contentMineDir = DEFAULT_CONTENT_MINE_DIR;

	protected CTreeList processedTreeList;
	// has processTree run OK? 
	protected boolean processedTree = true;
	protected boolean makeCProjectDirectory = false;
	private int maxInPrettyList = 5;

	public void init() {
		// log4j configuration
		BasicConfigurator.configure();
	}

	public void runCommands(String cmd) {
		String[] args = cmd == null ? new String[]{} : cmd.trim().split("\\s+");
		runCommands(args);
	}

	/**
	 * parse commands and pass to CommandLine
	 * calls CommandLine.call(this, args)
	 *
	 * @param args
	 */
	public void runCommands(String[] args) {
		init();
//		// add help
		args = args.length == 0 ? new String[]{"--help"} : args;
		new CommandLine(this).execute(args);
	}


	@Override
	public Void call() throws Exception {
		runCommands();
		return null;
	}

	/**
	 * assumes arguments have been preset (e.g. by set commands).
	 * Use at own risk
	 */
	public void runCommands() {
		printGenericHeader();
		parseGenerics();

		printSpecificHeader();
		parseSpecifics();

		if (level != null && !Level.WARN.isGreaterOrEqual(level)) {
			System.err.println("processing halted due to argument errors, level:" + level);
		} else {
			runPrevious();
			runGenerics();
			runSpecifics();
		}
	}

	protected void runPrevious() {
		// override if you want previous Tools run
	}

	protected abstract void parseSpecifics();

	protected abstract void runSpecifics();

	protected boolean parseGenerics() {
		validateCProject();
		validateCTree();
		validateRawFormats();
		parent.setLogging();
		printGenericValues();
		return true;
	}

	/**
	 * subclass this if you want to process CTree and CProject differently
	 */
	protected boolean runGenerics() {
		return true;
	}

	/**
	 * validates the infput formats.
	 * Currently NOOP
	 */
	protected void validateRawFormats() {
	}

	/**
	 * creates cProject from cProjectDirectory.
	 * checks it exists
	 */
	protected void validateCProject() {
		if (cProject != null) {
			return; // no need to create a CProject
		}
		if (getCProjectDirectory() != null) {
			if (makeCProjectDirectory) {
				new File(getCProjectDirectory()).mkdirs();
			}
			File cProjectDir = new File(getCProjectDirectory());
			setCProjectDirectory(checkDirExistenceAndGetAbsoluteName(cProjectDir, "cProject"));

			if (getCProjectDirectory() != null) {
				cProject = new CProject(cProjectDir);
				cTreeList = generateCTreeList();
			} else {
				System.err.println(""
						+ "************************\n"
						+ "WARNING: CProject directory does not exist\n"
						+ "************************\n");
			}
		}
	}

//	private String ensureExistingCProjectDirectory() {
//		return cProject.getOrCreateExistingCProjectDirectory();
//	}

	private void checkIncludeExclude(String[] exclude, String[] include) {
		if (
				(exclude != null /* && exclude.length > 0*/) &&
						(include != null /* && include.length > 0*/)
		) {
			throw new IllegalArgumentException("Cannot have both include and exclude arguments: ");
		}
	}

	/**
	 * this looks awful
	 *
	 * @param dir
	 * @param type
	 * @return
	 */
	private String checkDirExistenceAndGetAbsoluteNameOld(File dir, String type) {
		String cProjectDirectory = null;
		if (!dir.exists() || !dir.isDirectory()) {
			File parentFile = dir.getParentFile();
			if (parentFile != null && (parentFile.exists() || parentFile.isDirectory())) {
				cProjectDirectory = parentFile.getAbsolutePath();
				dir = parentFile;
			} else {
				LOG.info("** using parentFile as " + type + ": " + cProjectDirectory);
			}
			throw new RuntimeException(type + " must be existing directory: " + cProjectDirectory + "(" + dir.getAbsolutePath());
		}
		return cProjectDirectory;
	}

	private String checkDirExistenceAndGetAbsoluteName(File dir, String type) {
		String directory = null;
		if (dir == null) {
			throw new RuntimeException("null project");
		}
		directory = dir.getAbsolutePath();
		if (!dir.exists() || !dir.isDirectory()) {
			File parentFile = dir.getParentFile();
			if (parentFile != null && (parentFile.exists() || parentFile.isDirectory())) {
				dir = parentFile;
				LOG.info("** using parentFile as " + type + ": " + directory);
			} else {
				System.err.println("not found: " + type + " must be existing directory or have directory parent: " +
						getCProjectDirectory() + " (" + dir.getAbsolutePath());
				directory = null;
			}
		}
		return directory;
	}


	private CTreeList generateCTreeList() {
		cTreeList = new CTreeList();
		if (cProject != null) {
			checkIncludeExclude(excludeTrees(), includeTrees());
			List<String> includeTreeList = includeTrees() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(includeTrees()));
			List<String> excludeTreeList = excludeTrees() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(excludeTrees()));
			CTreeList pList = cProject == null ? new CTreeList() : cProject.getOrCreateCTreeList();
			for (CTree ct : pList) {
				String name = ct.getName();
				if (includeTreeList.size() > 0) {
					if (includeTreeList.contains(name)) {
						cTreeList.add(ct);
					}
				} else if (excludeTreeList.size() > 0) {
					if (!excludeTreeList.contains(name)) {
						cTreeList.add(ct);
					}
				} else {
					cTreeList.add(ct);
				}
			}
		} else if (cTree != null) {
			cTreeList.add(cTree);
		}
		return cTreeList;
	}

	/**
	 * creates cTree from cTreeDirectory.
	 * checks it exists
	 */
	protected void validateCTree() {
		checkIncludeExclude(excludeBase(), includeBase()); // check anyway
		String cTreeDirectory = cTreeDirectory();
		if (cTreeDirectory != null) {
			File cTreeDir = new File(cTreeDirectory());
			cTreeDirectory(checkDirExistenceAndGetAbsoluteName(cTreeDir, "cTree"));
			if (cTreeDirectory() == null) {
				System.err.println("***Cannot find ctree/parent: " + cTreeDir + " ***");
			} else {
				cTree = new CTree(cTreeDir);
				cTreeList = new CTreeList();
				cTreeList.add(cTree);
			}
		}
	}


	/**
	 * prints generic values from abstract superclass.
	 * at present cproject, ctree and filetypes
	 */
	private void printGenericValues() {
		if (verbosity().length > 0) {
			System.out.println("input basename      " + getInputBasename());
			System.out.println("input basename list " + getInputBasenameList());
			System.out.println("cproject            " + (cProject == null ? "" : cProject.getDirectory().getAbsolutePath()));
			System.out.println("ctree               " + (cTree == null ? "" : cTree.getDirectory().getAbsolutePath()));
			System.out.println("cTreeList           " + prettyPrint(cTreeList));
			System.out.println("excludeBase         " + excludeBase());
			System.out.println("excludeTrees        " + excludeTrees());
			System.out.println("forceMake           " + getForceMake());
			System.out.println("includeBase         " + includeBase());
			System.out.println("includeTrees        " + toString(includeTrees()));
			System.out.println("log4j               " + (log4j() == null ? "" : new ArrayList<String>(Arrays.asList(log4j()))));
			System.out.println("verbose             " + verbosity().length);
		} else {
			System.out.println("-v to see generic values");
		}
	}

	private String toString(String[] strings) {
		return strings == null ? "null" : prettyPrint(Arrays.asList(strings));
	}

	private String prettyPrint(List<String> strings) {
		if (strings == null) return "null";
		StringBuilder sb = new StringBuilder(String.valueOf(strings.size()));
		sb.append( " [");
		int count = 0;
		for (String s : strings) {
			sb.append(s);
			if (count++ > maxInPrettyList ) {
				sb.append(" ... ");
				break;
			}
			if (count < strings.size()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	private String prettyPrint(CTreeList cTreeList) {
		String s = String.valueOf(cTreeList);
		if (cTreeList == null) {
		} else if (cTreeList.size() <= 5) {
		} else {
			s = "" + cTreeList.size() + " trees " + s.substring(0, Math.min(50, s.length()));
		}
		return s;
	}

	protected String getCProjectDirectory() {
		return parent.projectOrTreeOptions.cProjectOptions.cProjectDirectory;
	}

	protected void setCProjectDirectory(String newValue) {
		parent.projectOrTreeOptions.cProjectOptions.cProjectDirectory = newValue;
	}

	protected String cTreeDirectory() {
		return parent.projectOrTreeOptions.cTreeOptions.cTreeDirectory;
	}

	protected void cTreeDirectory(String newValue) {
		parent.projectOrTreeOptions.cTreeOptions.cTreeDirectory = newValue;
	}

	protected String[] excludeTrees() {
		return parent.projectOrTreeOptions.cProjectOptions.treeOptions.excludeTrees;
	}

	protected String[] includeTrees() {
		return parent.projectOrTreeOptions.cProjectOptions.treeOptions.includeTrees;
	}

	protected String[] excludeBase() {
		return parent.projectOrTreeOptions.cTreeOptions.baseOptions.excludeBase;
	}

	protected String[] includeBase() {
		return parent.projectOrTreeOptions.cTreeOptions.baseOptions.includeBase;
	}

	protected String input() {
		return parent.generalOptions.input;
	}

	protected void input(String newValue) {
		parent.generalOptions.input = newValue;
	}

	protected boolean[] verbosity() {
		return parent.loggingOptions.verbosity;
	}

	protected String[] log4j() {
		return parent.loggingOptions.log4j;
	}

	public AbstractAMITool setCProject(CProject cProject) {
		this.cProject = cProject;
		return this;
	}

	public AbstractAMITool setCTree(CTree cTree) {
		this.cTree = cTree;
		return this;
	}

	public CTree getCTree() {
		return cTree;
	}

	public AbstractAMITool setCProjectOutputDir(File dir) {
		this.cProjectOutputDir = dir;
		return this;
	}

	public File getCProjectOutputDir() {
		return cProjectOutputDir;
	}

	public AbstractAMITool setCTreeOutputDir(File outputDir) {
		cTreeOutputDir = outputDir;
		return this;
	}

	public File getCTreeOutputDir() {
		return cTreeOutputDir;
	}

	public CProject getCProject() {
		return cProject;

	}

	protected void printGenericHeader() {
		System.out.println();
		System.out.println("Generic values (" + this.getClass().getSimpleName() + ")");
		System.out.println("================================");
	}

	protected void printSpecificHeader() {
		System.out.println();
		System.out.println("Specific values (" + this.getClass().getSimpleName() + ")");
		System.out.println("================================");
	}

	protected void addLoggingLevel(Level level, String message) {
		combineLevel(level);
		if (level.isGreaterOrEqual(Level.WARN)) {
			System.err.println(this.getClass().getSimpleName() + ": " + level + ": " + message);
		}
	}

	private void combineLevel(Level level) {
		if (level == null) {
			LOG.warn("null level");
		} else if (this.level == null) {
			this.level = level;
		} else if (level.isGreaterOrEqual(this.level)) {
			this.level = level;
		}
	}

	public int getVerbosityInt() {
		return verbosity().length;
	}

	public Level getVerbosity() {
		if (verbosity().length == 0) {
//			addLoggingLevel(Level.ERROR, "BUG?? in verbosity");
			return Level.WARN;
		} else if (verbosity().length == 1) {
			return verbosity()[0] ? Level.INFO : Level.WARN;
		} else if (verbosity().length == 2) {
			return Level.DEBUG;
		} else if (verbosity().length == 3) {
			return Level.TRACE;
		}
		return Level.ERROR;

	}

	/**
	 * creates toplevel ContentMine directory in which all dictionaries and other tools
	 * will be stored. By default this is "ContentMine" under the users home directory.
	 * It is probably not a good idea to store actual projects here, but we will eveolve the usage.
	 *
	 * @return null if cannot create directory
	 */
	protected File getOrCreateExistingContentMineDir() {
		if (contentMineDir == null) {
			// null means cannot be created
		} else if (contentMineDir.exists()) {
			if (!contentMineDir.isDirectory()) {
				LOG.error(contentMineDir + " must be a directory");
				contentMineDir = null;
			}
		} else {
			LOG.info("Creating " + CONTENT_MINE_HOME + " directory: " + contentMineDir);
			try {
				contentMineDir.mkdirs();
			} catch (Exception e) {
				LOG.error("Cannot create " + contentMineDir);
				contentMineDir = null;
			}
		}
		return contentMineDir;
	}

	protected boolean processTrees() {
		boolean processed = cTreeList != null && cTreeList.size() > 0;
		int treeCount = 0;
		if (cTreeList != null && cTreeList.size() > 0) {
			for (CTree cTree : cTreeList) {
				if (parent.generalOptions.maxTreeCount != null && getOrCreateProcessedTrees().size() >= parent.generalOptions.maxTreeCount) {
					System.out.println("CTree limit reached: " + (--treeCount));
					break;
				}
				this.cTree = cTree;
				outputCTreeName();
				if (processTree()) {
					getOrCreateProcessedTrees().add(cTree);
				}
				;
			}
		} else {
			System.err.println("no trees");
//			LOG.warn("No trees to process");
		}
		return processed;
	}


	protected CTreeList getOrCreateProcessedTrees() {
		if (processedTreeList == null) {
			processedTreeList = new CTreeList();
		}
		return processedTreeList;
	}

	protected boolean processTree() {
		LOG.warn("Override processTree()");
		return true;
	}

	protected boolean includeExclude(String basename) {
		boolean include = true;
		if (includeBase() != null) {
			include = incExclude(includeBase(), basename);
		} else if (excludeBase() != null) {
			include = !incExclude(excludeBase(), basename);
		} else {
			include = true;
		}
		return include;
	}

	private boolean incExclude(String[] incExcludeBaseList, String basename) {
		for (String incExcludeBase : incExcludeBaseList) {
			boolean truncate = false;
			String ref = incExcludeBase;
			int idx = ref.lastIndexOf(TRUNCATE);
			if (idx == ref.length() - 1) {
				ref = ref.substring(0, idx - 1);
				truncate = true;
			}
			if (incExcludeBase.equals(basename) || (truncate && basename.startsWith(ref))) {
				return true;
			}
		}
		return false;
	}

	public String getInputBasename() {
		return parent.generalOptions.inputBasename;
	}

	public void setInputBasename(String inputBasename) {
		parent.generalOptions.inputBasename = inputBasename;
	}

	public List<String> getInputBasenameList() {
		return parent.generalOptions.inputBasenameList;
	}

	/**
	 * this may not be the best place to define this.
	 *
	 * @param imageDir
	 * @return
	 */
	protected static File getRawImageFile(File imageDir) {
		return new File(imageDir, RAW + "." + CTree.PNG);
	}

	protected void outputCTreeName() {
		System.out.println(this.getClass().getSimpleName() + " cTree: " + cTree.getName());
	}

	public Boolean getForceMake() {
		return parent.generalOptions.forceMake;
	}

	public void setForceMake(Boolean forceMake) {
		parent.generalOptions.forceMake = forceMake;
	}

	protected InputStream openInputStream() {
		InputStream inputStream = null;
		if (input() != null) {
			try {
				if (parent.generalOptions.input.startsWith("http")) {
					inputStream = new URL(input()).openStream();
				} else {
					File inputFile = new File(input());
					if (!inputFile.exists()) {
						throw new RuntimeException("inputFile does not exist: " + inputFile.getAbsolutePath());
					}
					inputStream = new FileInputStream(inputFile);
				}
			} catch (IOException e) {
				addLoggingLevel(Level.ERROR, "cannot read/open stream: " + input());
			}
		}
		return inputStream;
	}

	/**
	 * gets filename relative to CProject.
	 * useful for ancillary files such a templates or dictionaries.
	 * attempts to fine File as absolute name , else files file relative to CProject directory
	 * if CProject is null or directory does not exist returns null
	 *
	 * @param filename
	 * @return null if file does not exist
	 */
	protected File getFileRelativeToProject(String filename) {
		File file1 = null;
		if (filename != null) {
			File file = new File(filename);
			if (file.exists()) {
				file1 = file;
			} else if (cProject != null) {
				File directory = cProject.getDirectory();
				if (directory != null && directory.exists()) {
					file1 = new File(directory, file.toString());
					file1 = file1.exists() ? file1 : null;
				}
			}
		}
		return file1;
	}

	public static boolean isTrace(AbstractAMITool amiTool) {
		return isLevel(amiTool, AbstractAMITool.Verbosity.TRACE);
	}

	public static boolean isDebug(AbstractAMITool amiTool) {
		return isLevel(amiTool, AbstractAMITool.Verbosity.DEBUG);
	}

	public static boolean isInfo(AbstractAMITool amiTool) {
		return isLevel(amiTool, AbstractAMITool.Verbosity.INFO);
	}

	private static boolean isLevel(AbstractAMITool amiTool, Verbosity verbosity) {
		return (amiTool == null) ? false : verbosity.getVerbosity() == amiTool.getVerbosityInt();
	}

	protected boolean reachesLevel(Verbosity verbosity) {
		return verbosity.getVerbosity() <= this.getVerbosityInt();
	}

	public void debugPrint(Verbosity verbosity, String message) {
		if (reachesLevel(verbosity)) {
			System.out.println("<" + verbosity + ">" + message);
		}
	}

	/** newstyle commands
	 * 
	 * @param args
	 */
	protected void runCommandsNew(String args) {
		runCommandsNew(args.split("\\s+"));
	}

	/**
	 * new style commands
	 * @param args
	 */
	protected void runCommandsNew(String[] args) {
		String clazz = this.getClass().getSimpleName();
		if (clazz.startsWith(AMI) && clazz.endsWith(TOOL)) {
			clazz = clazz.substring(0, clazz.length() - TOOL.length());
			clazz = clazz.substring(AMI.length());
			clazz = clazz.toLowerCase();
		} else {
			System.err.println("Cannot create command for " + clazz);
			return;
		}
		List<String> argList = new ArrayList<>(Arrays.asList(args));
		if (argList.size() >= 2) {
			String arg0 = argList.get(0);
			if (arg0.equals(P) || arg0.contentEquals(PROJECT)) {
				argList.add(2, clazz);
			}
		}
		CommandLine cmd = new CommandLine(new AMI())
			.setParameterExceptionHandler(new ShortErrorMessageHandler());
			cmd.execute(argList.toArray(new String[0]));
	}

}
