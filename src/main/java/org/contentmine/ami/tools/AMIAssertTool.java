package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.util.CMStringUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.image.ImageUtil;

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

	public enum AssertType {
		dir,
		dirtree,
		file,
		help,
		img,
		str,
		xpath,
	}
    
	@Option(names = {"--count"},
			arity = "1..2",
			split=",",
	        description = "count to assert (single value or range), mainly used for type=dir")
	private List<Integer> counts = null;
	
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

    @Option(names = {"--height"},
    		arity = "1..2",
            description = "heights of images")
    private List<Integer> heights = new ArrayList<>();

    @Option(names = {"--message"},
    		arity = "1..*",
            description = "(short) message to output. individual tokens will be concatenated. Punctuation is dangerous.")
    private List<String> messageList = null;

    @Option(names = {"--params"},
    		arity = "1..*",
            description = "(short) message to output. individual tokens will be concatenated. Punctuation is dangerous.")
    private Map<String, String> params = null;

    @Option(names = {"--size"},
    		arity = "1..2",
    		split=",",
            description = "sizes of objects (type-dependent)")
    private List<Integer> sizes = new ArrayList<>();

    @Option(names = {"--type"},
    		required = true,
            description = "type of object to assert (${COMPLETION-CANDIDATES})")
    private AssertType assertType = null;

    @Option(names = {"--width"},
    		arity = "1..2",
            description = "widths of images")
    private List<Integer> widths = new ArrayList<>();

    @Option(names = {"--xpath"},
    		arity = "1",
            description = "xpath to evaluate and apply to file. Result is a node-set that is matched against"
            		+ " list of sizes or values."
            		+ "")
    private String xpath;

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
    	if (processTrees()) { 
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
			assertDir();
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
			List<Integer> ints = sizes;
			checkIntValue(messageString, size, ints);
		} else {
			LOG.warn("No --input for file");
		}
	}

	private void assertDir() {
		LOG.info("DIR");
		File[] files = cTree.getDirectory().listFiles();
		List<File> fileList = files == null ? new ArrayList<>() : new ArrayList<File>(Arrays.asList(files));
		int size = fileList.size();
		if (counts != null) {
			String message1 = assertValue(size, counts);
			if (message1.length() > 0) {
				LOG.warn("***** " + messageString + ": " +  message1 + " *****");
			}
		}
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
		BufferedImage image = ImageUtil.readImageQuietly(currentFile);
		
		String totalMessage = null;
		if (!currentFile.exists()) {
			return "non-existent "+currentFile;
		}
		if (sizes != null) {
			totalMessage = concatenate("file size", compareWithRange((int) FileUtils.sizeOf(currentFile), sizes), totalMessage);
		}
		if (heights != null) {
			totalMessage = concatenate("height", compareWithRange(image.getHeight(), heights), totalMessage);
		}
		if (widths != null) {
			totalMessage = concatenate("width", compareWithRange(image.getWidth(), widths), totalMessage);
		}
		return totalMessage;
	}

	private String compareWithRange(int value, List<Integer> sizes) {
		String errorMessage = null;
		if (sizes.size() == 1) {
			errorMessage = assertRange(value, sizes.get(0), sizes.get(0));
		} else if (sizes.size() == 2) {
			errorMessage = assertRange(value, sizes.get(0), sizes.get(1));
		}
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
		if (sizes == null || sizes.size() == 0) {
			throw new RuntimeException("must give --sizes with 1 or 2 args");
		}
		String errorMessage = null;
		if (sizes != null) {
			/** exact count of args? */
			if (sizes.size() == 1) {
				errorMessage = assertRange(nodes.size(), sizes.get(0), sizes.get(0));
			} else if (sizes.size() == 2) {
				errorMessage = assertRange(nodes.size(), sizes.get(0), sizes.get(1));
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
	
	private String assertRange(int value, double arg0, double arg1) {
		if (arg0 > arg1) {
			return assertInputError(arg0 + "is greater than "+arg1);
		}
		if (value < arg0 || value > arg1) {
			return assertCondition(value + " is outside range "+arg0 + " to "+arg1);
		}
		return null;
	}
	
	private static String assertValue(int value, List<Integer> intLimits) {
		String message = "";
		IntRange intRange = IntRange.getIntRange(intLimits);
		if (intRange == null) {
			throw new RuntimeException("bad limits "+intLimits);
		}
		if (!intRange.contains(value)) {
			message = value + " not in range "+intRange;
		}
		return message;
	}
	
	private static void checkIntValue(String messageString, int size, List<Integer> ints) {
		if (ints != null) {
			String message1 = assertValue(size, ints);
			if (message1.length() > 0) {
				LOG.warn("***** " + messageString + ": " +  message1 + " *****");
			}
		}
	}
	

	

}
