package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.euclid.util.CMStringUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.image.ImageUtil;

import jline.internal.Log;
import nu.xom.Element;
import nu.xom.Node;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */



@Command(
	name = "assert",
	description = "Makes assertions about objects created by AMI. "
			+ "Currently requires a type (${COMPLETION-CANDIDATES}),"
			+ "and maybe a SubdirectoryType."
)
public class AMIAssertTool extends AbstractAMITool {

	private static final Logger LOG = LogManager.getLogger(AMIAssertTool.class);
	private static final String FAIL = "fail";
	
	private enum AssertType {
		dir("files in a directory"),
		dirtree("complete directory tree"),
		file("single file"),
		help("help!!"),
		img("image"),
		str("string"),
		xpath("selector within XML files"),
		;
		private String desc;
	
		private AssertType(String desc) {
			this.desc = desc;
		}
	}

	private enum ParamType {
		height("height of image"),
		width("width of image"),
		;
		private String desc;
	
		private ParamType(String desc) {
			this.desc = desc;
		}

		public String getDescription() {
			return desc;
		}
	}

	private enum RangeType {
		percent("allowed variation in either direction (positive real)"),
		intd("deviation in either direction (positive int)"),
		reald("deviation in either direction (positive real)"),
		;
		private String desc;
	
		private RangeType(String desc) {
			this.desc = desc;
		}

		public String getDescription() {
			return desc;
		}

		/**
		 * gets value for RangeType.intd and interprets as integer
		 * @return null if rangeMap is null or no RangeType.intd or not integer
		 */
		public static Integer getInteger(Map<RangeType, String> rangeMap) {
			String s = rangeMap == null ? null : rangeMap.get(RangeType.intd);
			try {
				return (s == null) ? null : Integer.parseInt(s);
			} catch (NumberFormatException nfe) {
				LOG.warn("unparsable integer: " + s);
				return null;
			}
		}
		/**
		 * gets value for RangeType.intd and interprets as integer
		 * @return null if rangeMap is null or no RangeType.intd or not integer
		 */
		public static Double getDouble(Map<RangeType, String> rangeMap) {
			String s = rangeMap == null ? null : rangeMap.get(RangeType.reald);
			try {
				return (s == null) ? null : Double.parseDouble(s);
			} catch (NumberFormatException nfe) {
				LOG.warn("unparsable double: " + s);
				return null;
			}
		}
	}

	@Option(names = {"--filecount"},
			arity = "1..*",
			split=",",
	        description = "file count(s) to assert , e.g. file counts per cTree /type=dir"
	        		+ "")
	private List<Integer> fileCounts = null;
	
	@Option(names = {"--directoryname"},
		arity = "1",
	    description = "current directory")
	private String currentDirname = null;

    @Option(names = {"--dirtree"},
    		arity = "1",
            description = "file containing non-hidden files in directory tree sorted lexically")
    private File dirtree = null;

    @Option(names = {"--fail"},
    		arity = "",
            description = "throw exception if assertion fails, else warning message")
    private boolean fail;

    @Option(names = {"--file"},
    		arity = "1",
            description = "current file relative to ctree")
    private String currentFilename = null;

    @Option(names = {"--glob"},
    		arity = "1..*",
            description = "list of globs (aggregated) to create fileList")
    private List<String> globList = null;

//    @Option(names = {"--height"},
//    		arity = "1..2",
//            description = "heights of images; maybe obsolete (replace by params?)")
//    private List<Integer> heights = new ArrayList<>();

    @Option(names = {"--message"},
    		arity = "1..*",
            description = "(short) message to output. individual tokens will be concatenated. Punctuation is dangerous.")
    private List<String> messageList = null;

    @Option(names = {"--params"},
    		arity = "1..*",
            description = "parameters")
    private Map<ParamType, String> params = null;

    @Option(names = {"--range"},
    		split=",",
            description = "range of expected values - applied equally to all values. ")
    private Map<RangeType, String> rangeMap = new HashMap<>();

    @Option(names = {"--filesize"},
    		arity = "1..*",
    		split=",",
            description = "sizes of files")
    private List<Integer> fileSizes = new ArrayList<>();

    @Option(names = {"--type"},
    		required = true,
            description = "type of object to assert (${COMPLETION-CANDIDATES})")
    private AssertType assertType = null;

//    @Option(names = {"--width"},
//    		arity = "1..2",
//            description = "widths of images ; maybe obsolete (replace by params?)")
//    private List<Integer> widths = new ArrayList<>();

    @Option(names = {"--xpath"},
    		arity = "1",
            description = "xpath to evaluate and apply to file. Result is a node-set that is matched against"
            		+ " list of sizes or values."
            		+ "")
    private String xpath;
    
    // https://www.xmlunit.org/api/java/2.7.0/overview-summary.html
    @Option(names = {"--xmlassert"},
//    		arity = "1",
            description = "NOT YET USED (placeholder to use XMLAssert where possible)."
            		+ "")
    private String xmlassert = null;

	private boolean assertFail;
	private String assertMessage;
	private File currentDir;
	private File currentFile;

	private String messageString;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIAssertTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIAssertTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIAssertTool().runCommands(args);
    }

	@Option(names = {"--subdirectorytype"},
			description = "Use subdirectory of cTree (${COMPLETION-CANDIDATES})")
	protected SubDirectoryType subdirectoryType;
	private File topDir;

    @Override
	protected void parseSpecifics() {
    	super.parseSpecifics();
    	if (assertType == null) {
    		throw new RuntimeException("Must give type");
    	}
    	messageString = messageList == null ? null : String.join(" ", messageList);
    	xpath = CMStringUtil.urlDecode(xpath);
	}


    @Override
    protected void runSpecifics() {
		topDir = getTopLevelDirectoryTreeOrProject();
    	if (cProject != null) {
    		for (CTree t : cProject.getOrCreateCTreeList()) {
    			cTree = t;
    			processTree();
    		}
    	} else if (cTree != null) { 
    		processTree();
    	} else {
			LOG.error(DebugPrint.MARKER, "must give cProject or cTree");
	    }
    }

	protected boolean processTree() {
		boolean processed = true;
		LOG.warn("cTree>> "+cTree.getName());
		if (SubDirectoryType.pdfimages.equals(subdirectoryType)) {
			iterateOverPDFImageDirs();
		} else if (SubDirectoryType.svg.equals(subdirectoryType)) {
			iterateOverSVGFiles();
		} else if (AssertType.dir.equals(assertType)) {
			assertSingleDir();
		} else if (AssertType.dirtree.equals(assertType)) {
			assertDirTree();
		} else if (AssertType.file.equals(assertType)) {
			assertFile();
		} else {
			System.err.println("Nothing to assert");
		}
		return processed;
	}

	private void assertFile() {
		LOG.info("FILE");
		
		if (input() != null) {
			currentDir = cTree.getDirectory();
			this.currentFile = new File(currentDir, /*getInputBasename()*/ input());
			if (!currentFile.exists()) {
				throw new RuntimeException("File does not exist: "+currentFile);
			}
			int size = (int)FileUtils.sizeOf(currentFile);
			List<Integer> ints = fileSizes;
			checkIntValue(messageString, size, ints.get(0));
		} else {
			LOG.warn("No --input for file");
		}
	}

	private void assertSingleDir() {
		LOG.info("DIR");
		List<File> fileList = createFileListFromCTreeOrCproject();
		if (fileList != null) {
			int actualFileCount = fileList.size();
			if (fileCounts != null && fileCounts.size() == 1/*fileList.size()*/) {
				String message1 = assertValue(actualFileCount, fileCounts.get(0)/*, rangeMap*/);
				if (message1.length() > 0) {
					LOG.warn("***** " + messageString + ": " +  message1 + " *****");
				}
			}
		}
	}

	private List<File> createFileListFromCTreeOrCproject() {
		List<File> fileList = new ArrayList<>();
		if (topDir == null) {
			LOG.error("no dir given for assert type=dir");
			return fileList;
		}
		if (globList != null && globList.size() > 0) {
			CMineGlobber cMineGlobber = new CMineGlobber();
			fileList = cMineGlobber.combineFileListsFromGlobs(topDir, globList);
		} else {
			fileList = CMFileUtil.listFiles(topDir);
		}
		return fileList;
	}

	private File getTopLevelDirectoryTreeOrProject() {
		File dir = cProject != null ? cProject.getDirectory() : (cTree != null ? cTree.getDirectory() : null);
		return dir;
	}

	private void assertDirTree() {
		LOG.info("DIR_TREE");
		Element tree = AMISummaryTool.createDirectoryTree(cTree.getDirectory());
		XMLUtil.assertEqualsCanonically(dirtree, tree);
	}



	private void iterateOverPDFImageDirs() {
		List<File> imageDirs = cTree.getPDFImagesImageDirectories();
		Collections.sort(imageDirs);
		for (int i = 0; i < imageDirs.size(); i++) {
			currentDir = imageDirs.get(i);
			LOG.warn("======>" + currentDir.getName() + "/" + getInputBasename());
			if (getInputBasename() == null) {
				LOG.warn("No input basename");
			} else {
				this.currentFile = new File(currentDir, getInputBasename());
				this.runAssert();
			}
		}
	}

	private void iterateOverSVGFiles() {
		List<File> imageDirs = cTree.getExistingSortedSVGFileList();
		Collections.sort(imageDirs);
		for (int i = 0; i < imageDirs.size(); i++) {
			currentDir = imageDirs.get(i);
			LOG.warn("======>" + currentDir.getName() + "/" + getInputBasename());
			if (getInputBasename() == null) {
				LOG.warn("No input basename");
			} else {
				this.currentFile = new File(currentDir, getInputBasename());
				this.runAssert();
			}
		}
	}

	public void setCurrentDirname(String dirname) {
		this.currentDirname = dirname;
	}
	
	public void setCurrentFilename(String filename) {
		this.currentFilename = filename;
	}
	
	public void runAssert() {
		if (false) {
		} else if (AssertType.file.equals(assertType)) {
//			assertMessage = assertFile();
		} else if (AssertType.img.equals(assertType)) {
			assertMessage = assertImage();
		} else if (AssertType.xpath.equals(assertType)) {
			assertMessage = assertXPath();
		}
		outputAssert();

	}

	private void outputAssert() {
		if (assertMessage != null) {
			assertMessage = "["+currentDir+"]"+assertMessage;
			if (assertFail) {
				throw new RuntimeException(assertMessage);
			} else {
				LOG.warn("**WARN**: "+assertMessage);
			}
		}
	}

	private String assertImage() {
		if (params == null) {
			throw new RuntimeException("must give --params for assert-image");
		}
		BufferedImage image = ImageUtil.readImageQuietly(currentFile);
		
		String totalMessage = null;
		if (!currentFile.exists()) {
			return "non-existent "+currentFile;
		}
		if (fileSizes != null) {
			// REFACTOR
			totalMessage = concatenate("file size", assertRange(
					(int) FileUtils.sizeOf(currentFile), new IntRange(fileSizes.get(0), fileSizes.get(0))), totalMessage);
		}
		if (params.get(ParamType.height) != null) {
			totalMessage = compareIntegersAgainstValuesAndRanges(ParamType.height, image.getHeight(), totalMessage);
		}
		if (params.get(ParamType.width) != null) {
			totalMessage = compareIntegersAgainstValuesAndRanges(ParamType.width, image.getWidth(), totalMessage);
		}
		return totalMessage;
	}

	private String compareIntegersAgainstValuesAndRanges(ParamType paramType, int actual, String totalMessage) {
		IntRange expectedValues = parseIntegers(params.get(paramType));
		totalMessage = concatenate(paramType.getDescription(), compareWithRange(actual, expectedValues), totalMessage);
		return totalMessage;
	}

	private IntRange parseIntegers(String string) {
		IntRange intRange = null;
		Pattern pattern = Pattern.compile("(-?\\d+)(_-?\\d+)?");
		Matcher matcher = pattern.matcher(string);
		if (matcher.matches()) {
			Integer min = new Integer(matcher.group(1));
			Integer max = (matcher.groupCount() == 2) ? new Integer(matcher.group(2).substring(1)) : min;
			intRange = new IntRange(min, max);
		}
		return intRange;
	}

	/** only supports normal doubles, not scientific notation
	 * 
	 * @param string
	 * @return
	 */
	private RealRange parseDoubles(String string) {
		RealRange realRange = null;
		Pattern pattern = Pattern.compile("(-?\\d*?\\.?\\d+)(_(-?\\d*?\\.?\\d+))?");
		Matcher matcher = pattern.matcher(string);
		if (matcher.matches()) {
			Double min = new Double(matcher.group(1));
			Double max = (matcher.groupCount() == 2) ? new Double(matcher.group(2).substring(1)) : min;
			realRange = new RealRange(min, max);
		}
		return realRange;
	}

	private String compareWithRange(int actualValue, IntRange expectedRange) {
		String errorMessage = assertRange(actualValue, expectedRange);
		return errorMessage;
	}

	private String concatenate(String param, String errorMessage, String totalMessage) {
		if (errorMessage != null) {
			errorMessage = " "+param+" > "+errorMessage;
			totalMessage = totalMessage == null ? errorMessage : totalMessage+" ; "+errorMessage;
		}
		return totalMessage;
	}

	private String assertXPath() {
		Element element = XMLUtil.parseQuietlyToRootElement(currentFile);
		List<Node> nodes = XMLUtil.getQueryNodes(element, xpath, null);
		
		boolean fail = false;
		if (fileSizes == null || fileSizes.size() == 0) {
			throw new RuntimeException("must give --sizes with 1 or 2 args");
		}
		String errorMessage = null;
		if (fileSizes != null) {
			/** exact count of args? */
			if (fileSizes.size() == 1) {
				errorMessage = assertIntegerRange(nodes.size(), fileSizes.get(0), fileSizes.get(0));
			} else if (fileSizes.size() == 2) {
				errorMessage = assertIntegerRange(nodes.size(), fileSizes.get(0), fileSizes.get(1));
			} else {
				errorMessage = assertInputError("too many args for xpath");
			}
		}
		return errorMessage;
	}

	// ======================= PRIVATE =======================
	
	private String assertCondition(String string) {
		return " "+string;
	}

	private String assertInputError(String msg) {
		return "ASSERT INPUT ERROR: "+msg; 
	}
	
	private String assertBug(String msg) {
		return "BUG (contact maintainers) : "+msg; 
	}
	
	private static String assertValue(int actual, int expected) {
		String msg = "";
		if (actual != expected) {
			msg =  actual + " is not equal to " + expected;
		}
		return msg;
	}
	
	private String assertRange(int value, IntRange intRange) {
		String msg = "";
		if (!intRange.contains(value)) {
			msg = (intRange.getRange() == 0) ? 
					value + " is not equal to " + intRange.getMin() :
					value + " is outside range " + intRange;
		}
		return msg;
	}
	
		private String assertRange(double value, RealRange realRange) {
			String msg = "";
			if (!realRange.contains(value)) {
				msg = (realRange.getMin() == realRange.getMax()) ? 
						value + " is not equal to " + realRange.getMin() :
						value + " is outside range " + realRange;
			}
			return msg;
		}
		
		private String assertIntegerRange(int value, double arg0, double arg1) {
			if (arg0 > arg1) {
				return assertInputError(arg0 + "is greater than "+arg1);
			}
			if (value < arg0 || value > arg1) {
				return assertCondition(value + " is outside range "+arg0 + " to "+arg1);
			}
			return null;
		}
		
//		private String assertRange(int value, int expected, Map<RangeType, String> rangeMap) {
//			int delta = RangeType.getInteger(rangeMap, RangeType.intd);
//			if (arg0 > arg1) {
//				return assertInputError(arg0 + "is greater than "+arg1);
//			}
//			if (value < arg0 || value > arg1) {
//				return assertCondition(value + " is outside range "+arg0 + " to "+arg1);
//			}
//			return null;
//		}
		
//	private static String assertValue(int value, List<Integer> intLimits) {
//		String message = "";
//		IntRange intRange = IntRange.getIntRange(intLimits);
//		if (intRange == null) {
//			throw new RuntimeException("bad limits "+intLimits);
//		}
//		if (intRange.getMin() == intRange.getMax() && value != intRange.getMin()) {
//			message = value + " not equal to expected "+intRange.getMin();
//		} else if (!intRange.contains(value)) {
//			message = value + " not in expected range "+intRange;
//		}
//		return message;
//	}
//	
	private static void checkIntValue(String messageString, int size, int expected) {
		String message1 = assertValue(size, expected);
		if (message1.length() > 0) {
			LOG.warn("***** " + messageString + ": " +  message1 + " *****");
		}
	}
	

	

}
