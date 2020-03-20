package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDictionaryTool.RawFileFormat;
import org.contentmine.ami.tools.download.CurlDownloader;
import org.contentmine.cproject.args.AbstractTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.euclid.Util;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Reusable Commands for picocli CommandLine
 * see Picocli manual
 * 
 * @author pm286
 *
 */
@Command(
	addMethodSubcommands = false,
			//String separator() default "=";
	separator = "=",
			//String[] version() default {};
	mixinStandardHelpOptions = true,
			//boolean helpCommand() default false;
	helpCommand = true,
			//String headerHeading() default "";
	abbreviateSynopsis = true,
			//String[] customSynopsis() default {};
	descriptionHeading = "Description\n===========\n",
			//String[] description() default {};
	parameterListHeading  = "Parameters\n=========\n",
			//String optionListHeading() default "";
	optionListHeading  = "Options\n=======\n",
			//boolean sortOptions() default true;
	sortOptions = true,
			//char requiredOptionMarker() default ' ';
	requiredOptionMarker = '*',
			//Class<? extends IDefaultValueProvider> defaultValueProvider() default NoDefaultProvider.class;
	showDefaultValues = true,
			//String commandListHeading() default "Commands:%n";
	commandListHeading = "Commands:%n=========%n",
			//String footerHeading() default "";
	hidden = false,
			//String resourceBundle() default "";
	usageHelpWidth = 80,
	
	version = "ami20190228" // also edit ami-jars-sh
	)

public abstract class AbstractAMITool implements Callable<Void> , AbstractTool {
	private static final Logger LOG = Logger.getLogger(AbstractAMITool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum IncExc {
		INCLUDE,
		EXCLUDE
	}
	
	/** maybe add subdirectory of tree later
	 * 
	 * @author pm286
	 *
	 */
	public enum Scope {
		PROJECT("-p"),
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

	/** subdirectories of CTree
	 * 
	 * @author pm286
	 *
	 */
	public enum SubDirectoryType {
		pdfimages("pdfimaages"),
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
	
	@Option(names = {"--outputname"}, 
    		arity="1",
    		description = "(A) User's basename for outputfiles (e.g. foo/bar/<basename>.png or directories. By default this is computed by AMI."
    				+ " This allows users to create their own variants, but they won't always be known by default to subsequent"
    				+ "applications"
    		)
	protected String outputBasename;

	@Option(names = {"--inputname"}, 
    		arity="1",
    		description = "(A) User's basename for inputfiles (e.g. foo/bar/<basename>.png) or directories. By default this is often computed by AMI."
    				+ " However some files will have variable names (e.g. output of AMIImage) or from foreign sources or applications"
    		)
	protected String inputBasename;

	@Option(names = {"--inputnamelist"}, 
    		arity="1..*",
    		description = "(A) list of inputnames; will iterate over them , eseentially compressing multiple commands into one. Experimental"
    		)
	protected List<String> inputBasenameList = null;

    @Option(names = {"-p", "--cproject"}, 
		arity = "1",
		paramLabel="CProject",
		description = "(A) CProject (directory) to process. This can be (a) a child directory of cwd (current working directory (b) cwd itself (use -p .) or (c) an absolute filename."
				+ " No defaults. The cProject name is the basename of the file."
				)
    protected String cProjectDirectory = null;

    @Option(names = {"-i", "--input"}, 
		arity = "1",
		paramLabel="input",
		description = "(A) input filename (no defaults)"
				)
    protected String input = null;

    @Option(names = {"-o", "--output"}, 
		arity = "1",
		paramLabel="output",
		description = "(A) output filename (no defaults)"
				)
    protected String output = null;

    @Option(names = {"-t", "--ctree"}, 
		arity = "0..1",
		paramLabel = "CTree",
		interactive = false,
		descriptionKey = "descriptionKey",
		description = "(A) CTree (directory) to process. This can be (a) a child directory of cwd (current working directory, usually cProject) (b) cwd itself, usually cTree (use -t .) or (c) an absolute filename."
				+ " No defaults. The cTree name is the basename of the file."
				)
    protected String cTreeDirectory = null;

    @Option(names = {"--dryrun"}, 
    		arity="1",
    		description = "(A) for testing runs a single phase without output, deletion or transformation.(NYI)."
    		)
	protected Boolean dryrun = false;

    @Option(names = {"--forcemake"}, 
    		arity="0",
    		description = "(A) force 'make' regardless of file existence and dates."
    		)
	protected Boolean forceMake = false;

    @Option(names = {"--excludebase"}, 
    		arity="1..*",
    		description = "(A) exclude child files of cTree (only works with --ctree). "
    				+ "Currently must be explicit or with trailing percent for truncated glob."
    		)
	public String[] excludeBase;

    @Option(names = {"--excludetree"}, 
    		arity="1..*",
    		description = "(A) exclude the CTrees in the list. (only works with --cproject). "
    				+ "Currently must be explicit but we'll add globbing later."
    		)
	public String[] excludeTrees;

    @Option(names = {"--includebase"}, 
    		arity="1..*",
    		description = "(A) include child files of cTree (only works with --ctree). "
    				+ "Currently must be explicit or with trailing percent for truncated glob."
    		)
	public String[] includeBase;

    @Option(names = {"--includetree"}, 
    		arity="1..*",
    		description = "(A) include only the CTrees in the list. (only works with --cproject). "
    				+ "Currently must be explicit but we'll add globbing later."
    		)
	public String[] includeTrees;

    @Option(names = {"--log4j"}, 
    		arity="2..*",
    		description = "(A) format: <classname> <level>; sets logging level of class, e.g. \n "
    				+ "org.contentmine.ami.lookups.WikipediaDictionary INFO"
    		)
	public String[] log4j;

    @Option(names = {"--logfile"}, 
    		arity="1",
    		description = "(A) log file for each tree/file/image analyzed. "
    		)
	public String logfile;

    @Option(names = {"--oldstyle"},
    		arity = "0",
            description = "(A) use oldstyle style of processing (project based) for unconverted tools; new style is per tree")
	protected boolean oldstyle = true;
    

	@Option(names = {"--rawfiletypes" }, 
			arity = "1..*", 
			split = ",", 
			description = "(A) suffixes of included files (${COMPLETION-CANDIDATES}): "
					+ "can be concatenated with commas ")
	protected List<RawFileFormat> rawFileFormats = new ArrayList<>();

    @Option(names = {"--subdirectorytype"},
    		arity = "1",
            description = "(A) use subdirectory of cTree")
    protected SubDirectoryType subdirectoryType;

    @Option(names = {"--maxTrees"},
    		arity = "1",
            description = "(A) quit after given number of trees; null means infinite")
    protected Integer maxTreeCount = null;


	@Option(names = { "--testString" }, 
    		description = {
        "(A) String input for debugging"
        + "semantics depend on task" })
    protected String testString = null;

	@Option(names = { "-v", "--verbose" }, 
    		description = {
        "(A) Specify multiple -v options to increase verbosity.",
        "For example, `-v -v -v` or `-vvv`"
        + "We map ERROR or WARN -> 0 (i.e. always print), INFO -> 1(-v), DEBUG->2 (-vv)" })
    protected boolean[] verbosity = new boolean[0];
    

	
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
	
	protected String[] args;
	private Level level;
	protected File contentMineDir = DEFAULT_CONTENT_MINE_DIR;

	protected CTreeList processedTreeList;
	// has processTree run OK? 
	protected boolean processedTree = true;
	protected boolean makeCProjectDirectory = false;

	public void init() {
		// log4j configuration
		BasicConfigurator.configure();
	}

	public void runCommands(String cmd) {
		String[] args = cmd == null ? new String[]{} : cmd.trim().split("\\s+");
		runCommands(args);
	}
	
	/** parse commands and pass to CommandLine
	 * calls CommandLine.call(this, args)
	 * 
	 * @param args
	 */
	public void runCommands(String[] args) {
		init();
		this.args = args;
		// add help
    	args = args.length == 0 ? new String[] {"--help"} : args;
		CommandLine.call(this, args);
		
    	runCommands(); 
	}

	/** assumes arguments have been preset (e.g. by set commands). 
	 * Use at own risk
	 */
	public void runCommands() {
		printGenericHeader();
		parseGenerics();
		
    	printSpecificHeader();
		parseSpecifics();
		
		if (level != null && !Level.WARN.isGreaterOrEqual(level)) {
			System.err.println("processing halted due to argument errors, level:"+level);
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
    	setLogging();
    	printGenericValues();
        return true;
	}

	private void setLogging() {
		if (log4j != null) {
			if (log4j.length % 2 != 0) {
				throw new RuntimeException ("log4j must have even number of arguments");
			}
			Map<Class<?>, Level> levelByClass = new HashMap<Class<?>, Level>();
			for (int i = 0; i < log4j.length; ) {
				String className = log4j[i++];
				Class<?> logClass = null;
				try {
					logClass = Class.forName(className);
				} catch (ClassNotFoundException e) {
					System.err.println("Cannot find logger Class: "+className);
					i++;
					continue;
				}
				String levelS = log4j[i++];
				Level level =  Level.toLevel(levelS);
				if (level == null) {
					LOG.error("cannot parse class/level: "+className+":"+levelS);
				} else {
					levelByClass.put(logClass, level);
					Logger.getLogger(logClass).setLevel(level);
				}
			}
		}
	}

	@Override
    public Void call() throws Exception {
        return null;
    }

    /** subclass this if you want to process CTree and CProject differently
     * 
     */
	protected boolean runGenerics() {
        return true;
	}

	/** validates the infput formats.
	 * Currently NOOP
	 * 
	 */
	protected void validateRawFormats() {
	}

	/** creates cProject from cProjectDirectory.
	 * checks it exists
	 * 
	 */
	protected void validateCProject() {
				
		if (cProjectDirectory != null) {
			if (makeCProjectDirectory) {
				new File(cProjectDirectory).mkdirs();
			}
			File cProjectDir = new File(cProjectDirectory);
			cProjectDirectory = checkDirExistenceAndGetAbsoluteName(cProjectDir, "cProject");
			
			if (cProjectDirectory != null) {
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

	/** this looks awful
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
				LOG.info("** using parentFile as " + type + ": "+cProjectDirectory);
			}
 			throw new RuntimeException(type + " must be existing directory: " + cProjectDirectory + "("+dir.getAbsolutePath());
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
				LOG.info("** using parentFile as " + type + ": "+directory);
			} else {
	 			System.err.println("not found: "+ type + " must be existing directory or have directory parent: " +
			        cProjectDirectory + " ("+dir.getAbsolutePath());
	 			directory = null;
			}
		}
		return directory;
	}



	private CTreeList generateCTreeList() {
		cTreeList = new CTreeList();
		if (cProject != null) {
			checkIncludeExclude(excludeTrees, includeTrees);
			List<String> includeTreeList = includeTrees == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(includeTrees));
			List<String> excludeTreeList = excludeTrees == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(excludeTrees));
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

	/** creates cTree from cTreeDirectory.
	 * checks it exists
	 * 
	 */
	protected void validateCTree() {
		checkIncludeExclude(excludeBase, includeBase); // check anyway
		if (cTreeDirectory != null) {
			File cTreeDir = new File(cTreeDirectory);
			cTreeDirectory = checkDirExistenceAndGetAbsoluteName(cTreeDir, "cTree");
			if (cTreeDirectory == null) {
				System.err.println("***Cannot find ctree/parent: " + cTreeDir + " ***");
			} else {
				cTree = new CTree(cTreeDir);
				cTreeList = new CTreeList();
				cTreeList.add(cTree);
			}
    	}
	}
	

	/** prints generic values from abstract superclass.
	 * at present cproject, ctree and filetypes
	 * 
	 */
	private void printGenericValues() {
		if (verbosity.length > 0) {
	        System.out.println("output basename     " + outputBasename);
	        System.out.println("input basename      " + inputBasename);
	        System.out.println("input basename list " + inputBasenameList);
	        System.out.println("cproject            " + (cProject == null ? "" : cProject.getDirectory().getAbsolutePath()));
	        System.out.println("ctree               " + (cTree == null ? "" : cTree.getDirectory().getAbsolutePath()));
	        System.out.println("cTreeList           " + prettyPrint(cTreeList));
	        System.out.println("dryrun              " + dryrun);
	        System.out.println("excludeBase         " + excludeBase);
	        System.out.println("excludeTrees        " + excludeTrees);
	        System.out.println("file types          " + rawFileFormats);
	        System.out.println("forceMake           " + forceMake);
	        System.out.println("includeBase         " + includeBase);
	        System.out.println("includeTrees        " + includeTrees);
	        System.out.println("log4j               " + (log4j == null ? "" : new ArrayList<String>(Arrays.asList(log4j))));
	        System.out.println("logfile             " + logfile);
	        System.out.println("subdirectoryType    " + subdirectoryType);
	        System.out.println("testString          " + testString);
	        System.out.println("verbose             " + verbosity.length);
        } else {
        	System.out.println("-v to see generic values");
        }
        System.out.println("oldstyle            " + oldstyle);
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
		System.out.println("Generic values ("+this.getClass().getSimpleName()+")");
		System.out.println("================================");
	}

	protected void printSpecificHeader() {
		System.out.println();
		System.out.println("Specific values ("+this.getClass().getSimpleName()+")");
		System.out.println("================================");
	}

	protected void addLoggingLevel(Level level, String message) {
		combineLevel(level);
		if (level.isGreaterOrEqual(Level.WARN)) {
			System.err.println(this.getClass().getSimpleName()+": "+level + ": "+message);
		}
	}

	private void combineLevel(Level level) {
		if (level == null) {
			LOG.warn("null level");
		} else if (this.level== null) {
			this.level = level;
		} else if (level.isGreaterOrEqual(this.level)) {
			this.level = level;
		}
	}
	
	public int getVerbosityInt() {
		return verbosity.length;
	}
		
	public Level getVerbosity() {
		if (verbosity.length == 0) {
//			addLoggingLevel(Level.ERROR, "BUG?? in verbosity");
			return Level.WARN;
		} else if (verbosity.length == 1) {
			 return verbosity[0] ? Level.INFO : Level.WARN; 
		} else if (verbosity.length == 2) {
			 return Level.DEBUG; 
		} else if (verbosity.length == 3) {
			 return Level.TRACE; 
		}
		return Level.ERROR;
		
	}
	
	/** creates toplevel ContentMine directory in which all dictionaries and other tools
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
			LOG.info("Creating "+CONTENT_MINE_HOME+" directory: "+contentMineDir);
			try {
				contentMineDir.mkdirs();
			} catch (Exception e) {
				LOG.error("Cannot create "+contentMineDir);
				contentMineDir = null;
			}
		}
		return contentMineDir;
	}

	protected boolean processTrees() {
		boolean processed = cTreeList != null && cTreeList.size() > 0;
		int treeCount = 0; 
		if (cTreeList != null) {
			for (CTree cTree : cTreeList) {
				if (maxTreeCount != null && getOrCreateProcessedTrees().size() >= maxTreeCount) {
					System.out.println("CTree limit reached: "+(--treeCount));
					break;
				}
				this.cTree = cTree;
				outputCTreeName();
				if (processTree()) {
					getOrCreateProcessedTrees().add(cTree);
				};
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
		if (includeBase != null) {
			include = incExclude(includeBase, basename);
		} else if (excludeBase != null) {
			include = !incExclude(excludeBase, basename);
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
		return inputBasename;
	}

	public void setInputBasename(String inputBasename) {
		this.inputBasename = inputBasename;
	}

	public List<String> getInputBasenameList() {
		return inputBasenameList;
	}

	/** this may not be the best place to define this.
	 * 
	 * @param imageDir
	 * @return
	 */
	protected static File getRawImageFile(File imageDir) {
		return new File(imageDir, RAW + "." + CTree.PNG);
	}

	protected void outputCTreeName() {
		System.out.println(this.getClass().getSimpleName()+" cTree: "+cTree.getName());
	}

	public Boolean getForceMake() {
		return forceMake;
	}

	public void setForceMake(Boolean forceMake) {
		this.forceMake = forceMake;
	}

	protected InputStream openInputStream() {
		InputStream inputStream = null;
		if (input != null) {
			try {
				if (input.startsWith("http")) {
					inputStream = new URL(input).openStream();
				} else {
					File inputFile = new File(input);
					if (!inputFile.exists()) {
						throw new RuntimeException("inputFile does not exist: "+inputFile);
					}
					inputStream = new FileInputStream(inputFile);
				}
			} catch (IOException e) {
				addLoggingLevel(Level.ERROR, "cannot read/open stream: "+input);
			}
		}
		return inputStream;
	}

	/** gets filename relative to CProject.
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
			System.out.println("<"+verbosity+">"+message);
		}
	}

}
