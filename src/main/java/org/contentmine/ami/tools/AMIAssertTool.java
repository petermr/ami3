package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
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
)
public class AMIAssertTool extends AbstractAMITool {

	private static final Logger LOG = Logger.getLogger(AMIAssertTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String FAIL = "fail";

	public enum AssertType {
		file,
		help,
		image,
		xpath,
	}
	    
    @Option(names = {"--directoryname"},
    		arity = "1",
            description = "current directory")
    private String currentDirname = null;

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
    private List<String> message = null;

    @Option(names = {"--size"},
    		arity = "1..2",
            description = "sizes of objects (type-dependent)")
    private List<Integer> sizes = new ArrayList<>();

    @Option(names = {"--type"},
    		required = true,
            description = "type of object to assert")
    private AssertType assertType = null;

    @Option(names = {"--width"},
    		arity = "1..2",
            description = "widths of images")
    private List<Integer> widths = new ArrayList<>();

    @Option(names = {"--xpath"},
    		arity = "1",
            description = "xpath to evaluate and apply to file. Result is a node-set that is matched against list of sizes or values."
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
    	if (assertType == null) {
    		throw new RuntimeException("Must give type");
    	}
    	messageString = String.join(" ", message);
    	xpath = unescape(xpath);
		System.out.println("currentDirname      " + currentDirname);
		System.out.println("subdirectoryType    " + subdirectoryType);
		System.out.println("fail                " + fail);
		System.out.println("currentFilename     " + currentFilename);
		System.out.println("heights             " + heights);
		System.out.println("messageString       " + messageString);
		System.out.println("sizes               " + sizes);
		System.out.println("assertType          " + assertType);
		System.out.println("widths              " + widths);
		System.out.println("xpath               " + xpath);
		System.out.println();

	}


    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	protected boolean processTree() {
		boolean processed = true;
		System.out.println("cTree>> "+cTree.getName());
		if (SubDirectoryType.pdfimages.equals(subdirectoryType)) {
			iterateOverPDFImageDirs();
		} else if (SubDirectoryType.svg.equals(subdirectoryType)) {
			iterateOverSVGFiles();
		} else {
			currentDir = cTree.getDirectory();
			this.currentFile = new File(currentDir, getInputBasename());
			this.runAssert();
			
		}
		return processed;
	}

	private void iterateOverPDFImageDirs() {
		List<File> imageDirs = cTree.getPDFImagesImageDirectories();
		Collections.sort(imageDirs);
		for (int i = 0; i < imageDirs.size(); i++) {
			currentDir = imageDirs.get(i);
			System.out.println("======>" + currentDir.getName() + "/" + getInputBasename());
			if (getInputBasename() == null) {
				System.err.println("No input basename");
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
			System.out.println("======>" + currentDir.getName() + "/" + getInputBasename());
			if (getInputBasename() == null) {
				System.err.println("No input basename");
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
		if (AssertType.file.equals(assertType)) {
			assertMessage = assertFile();
		} else if (AssertType.image.equals(assertType)) {
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
				System.err.println("**WARN**: "+assertMessage);
			}
		}
	}

	private String assertFile() {
		return null;
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
	
	public static String unescape(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '%') {
				if (i < s.length() - 3) {
					String ss = extractChar(s, i);
					if (ss != null) {
						i += 2;
						sb.append(ss);
					} else {
						sb.append('%');
					}
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String extractChar(String s, int i) {
		String ss = null;
		char c1 = s.charAt(i+1);
		char c2 = s.charAt(i+2);
		if (isHexCharacter(c1) && isHexCharacter(c2)) {
			try {
				int ii = (int) Long.parseLong(s.substring(i+1, i+3), 16);
				ss = String.valueOf((char) ii);
			} catch (Exception e) {
				throw new RuntimeException("BUG", e);
			}
		}
		return ss;
	}

	private static boolean isHexCharacter(char c2) {
		if (c2 >= '0' && c2 <= '9') return true;
		if (c2 >= 'A' && c2 <= 'F') return true;
		if (c2 >= 'a' && c2 <= 'f') return true;
		return false;
	}

}
