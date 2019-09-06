package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;

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
name = "ami-display", 
aliases = "display",
version = "ami-display 0.1",
description = "	Displays files in CTree. Uses HTML to aggregate several files from (say) same imageDir."
		+ "Also creates aggregated links in parent directory."
		
)

public class AMIDisplayTool extends AbstractAMITool {
	private static final String FAIL = "fail";

	private static final String IMAGE = "image";

	private static final Logger LOG = Logger.getLogger(AMIDisplayTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private enum Orientation {
		horizontal,
		overlap,
		vertical,
	}



    @Option(names = {"--aggregate"},
    		arity = "1",
            description = "create summary listing of files of given name in higher level directory."
            		+ "e.g. summarize all raw.png files in ../raw.html . exploratory. Can be used recursively."
            		+ "Currently used with --display.")
    private String summaryFilename = null;
    
    @Option(names = {"--assert"},
    		arity = "2..*",
            description = "assertion. current args ' message type [value ...] [fail]? '. ducktype processing."
            		+ " message is single mnemonic string "
            		+ "action depends on 'type' (xpath, image, file)  compares output with "
            		+ "0 or more values. if 'fail', then throws RuntimeException. "
            		+ "SPACES, quotes in args must be escaped by %20 %(uck! but necessary)"
            		+ "Example: "
            		+ " '--assert checkHasFooChild xpath /*[local-name()='foo%20and%20@class='bar') 1 3 fail' will:"
            		+ "   apply xpath to  look for 'foo' child element of root in current element and pass if 1 <= count(nodes) <= 3"
               		+ " '--assert imageBigEnough image height 20 width 30 fail' will:"
            		+ "   fail if current image has width<20 or height < 30"
            		+ "This will evolve. "
            		+ " '--assert help will try to give some ducktype help"
            		+ "FRAGILE :-)"
            		
            )
    private List<String> assertList = null;

    @Option(names = {"--display"},
    		arity = "1..*",
            description = "display files in panel"
            )
    private List<String> displayList = null;

    @Option(names = {"--orientation"},
    		arity = "0",
            description = "display direction (horizontal or vertical)")
    private Orientation orientation = Orientation.horizontal;

	private File imageDir;
	private List<File> summaryFileList;
	private String basename;


    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIDisplayTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIDisplayTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIDisplayTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
		System.out.println("aggregate           " + summaryFilename);
		System.out.println("assert              " + assertList);
		System.out.println("display             " + displayList);
		System.out.println("orientation         " + orientation);
		System.out.println();
	}


    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	protected void processTree() {
		System.out.println("cTree>> "+cTree.getName());
		summaryFileList = new ArrayList<>();

		List<File> imageDirs = cTree.getPDFImagesImageDirectories();
		Collections.sort(imageDirs);
		for (int i = 0; i < imageDirs.size(); i++) {
			imageDir = imageDirs.get(i);
			this.basename = FilenameUtils.getBaseName(imageDir.toString());
			System.out.println("======>"+imageDir.getName()+"/"+inputBasename);

			if (displayList != null && displayList.size() > 0) {
				displayFiles();
				addSummaryFile();
			}
		}
		summarizeFiles();
	}

	
	private void addSummaryFile() {
		if (summaryFilename != null) {
			File summaryFile = new File(imageDir, summaryFilename);
			if (summaryFile.exists()) {
				summaryFileList.add(summaryFile);
			}
		}
	}
	
	private void summarizeFiles() {
		if (summaryFileList != null) {
			HtmlHtml html = new HtmlHtml();
			HtmlBody body = html.getOrCreateBody();
			HtmlUl ul = new HtmlUl();
			body.appendChild(ul);
			for (File summarizedFile : summaryFileList) {
				createAndAddLinkToFile(ul, summarizedFile);
			}
			if (summaryFilename == null) {
//				System.out.println(">> null summary");
			} else {
				File summaryHtmlFile = new File(imageDir.getParent(), summaryFilename);
				XMLUtil.writeQuietly(html, summaryHtmlFile, 1);
			}
		}
	}

	private void createAndAddLinkToFile(HtmlUl ul, File summaryFile) {
		String imageDirname = summaryFile.getParentFile().getName();
		HtmlLi li = new HtmlLi();
		ul.appendChild(li);
		HtmlA a = new HtmlA();
		a.setHref(""+imageDirname+"/"+summaryFile.getName());
		a.setTarget("_blank");
		a.appendChild(imageDirname);
		li.appendChild(a);
	}

	private void displayFiles() {
		HtmlTable table = createDisplayTable();
		String filename = inputBasename != null ? inputBasename+"."+CTree.HTML : summaryFilename;
		File htmlFile = new File(imageDir, filename);
		try {
			XMLUtil.debug(table, htmlFile, 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write: "+htmlFile);
		}
	}

	private HtmlTable createDisplayTable() {
		HtmlTable table = new HtmlTable();
		createAndFillHead(table);
		createAndFillBody(table);
		return table;
	}

	private void createAndFillBody(HtmlTable table) {
		HtmlTbody body = table.getOrCreateTbody();
		if (Orientation.horizontal.equals(orientation)) {
			createHorizontalDisplay(body);
		} else {
			createVerticalDisplay(body);
		}
	}

	private void createHorizontalDisplay(HtmlTbody body) {
		HtmlTr tr = new HtmlTr();
		body.appendChild(tr);
		for (int i = 0; i < displayList.size(); i++) {
			HtmlTd td = createTd(displayList.get(i));
			tr.appendChild(td);
		}
	}

	private void createVerticalDisplay(HtmlTbody body) {
		for (int i = 0; i < displayList.size(); i++) {
			HtmlTr tr = new HtmlTr();
			body.appendChild(tr);
			HtmlTd td = createTd(displayList.get(i));
			tr.appendChild(td);
		}
	}

	private HtmlTd createTd(String filename) {
		/** create full file. relative to imageDir 
		 *    if (inputBasename == null) imageDir/filename
		 *    If filename is ".foo", creates imageDir/inputBasename.foo
		 *    else treat as subdirectory/ => imageDir/inputBasename/filename
		 */
		File displayFile = null;
		String displayFilename = imageDir+"/"+filename;
		if (inputBasename == null) {
			displayFile = new File(imageDir, filename);
		} else {
			File subdir = new File(imageDir, inputBasename);
		    if (filename.startsWith("../")) {
		    	filename = filename.substring(3);
		    	displayFile = new File(imageDir, filename);
				displayFilename = filename;
		    } else if (filename.startsWith(".")) {
			    	String imageFilename = inputBasename+filename;
					displayFile = new File(imageDir, imageFilename);
					displayFilename = imageFilename;
		    } else {
		    	displayFile = new File(subdir, filename);
		    	displayFilename = inputBasename + "/" + filename;
		    }
		}
		HtmlTd td = createCell(displayFilename, displayFile);
		return td;
	}

	/** doesn't work yet
	 * 
	 * @return
	 */
//	private Integer getFirstYOffset() {
//		Integer offset = 0;
//		AbstractTemplateElement templateElement = 
//				AbstractTemplateElement.readTemplateElement(imageDir, templateFilename);
//		if (templateElement != null) {
//			String borderString = XMLUtil.getSingleValue(templateElement, "/template/image/@borders");
//			String[] borders = borderString == null ? null : borderString.split("\\s+");
//			if (borders != null && borders.length == 2) {
//				String s = borders[0];
//				try {
//					offset = Integer.parseInt(s);
//				} catch (NumberFormatException nfe) {
//					System.out.println("Cannot parse "+Arrays.asList(borders));
//				}
//			}
//		}
//		return offset;
//	}

	private HtmlTd createCell(String name, File file) {
//		String name = displayFile.getName();
		HtmlTd td = new HtmlTd();
		if (name.endsWith("."+CTree.PNG)) {
			HtmlImg img = new HtmlImg();
			img.setSrc(name);
			td.appendChild(img);
		} else if (name.endsWith("."+CTree.SVG)) {

			Integer offset = 0; /*  getFirstYOffset();*/ // doesn't work

			/**<object type="image/svg+xml" data="image.svg">
			  Your browser does not support SVG
			</object>*/
			// this doesn't work
//			HtmlObject obj = new HtmlObject();
//			obj.setType(HtmlObject.SVGTYPE);
//			obj.setSrc(name);
			if (!file.exists()) {
				LOG.warn("no file>"+file);
				td.appendChild("no file: "+file);
			} else {
				Element svg = XMLUtil.parseQuietlyToRootElement(file);
				SVGSVG svgCopy = (SVGSVG) SVGElement.readAndCreateSVG(svg);
				SVGElement svgx = (SVGElement) svgCopy.getChildElements().get(0);
				SVGG g;
				if (svgx instanceof SVGG) {
					g = (SVGG) svgx;
				} else {
					g = new SVGG();
					g.appendChild(svgx.copy());
				}
				// BUG in offset
//				offset = null; 
				offset = 0; 
				Transform2 t2 = new Transform2(new Vector2(0, (offset == null) ? -30 : -1 * offset));
				g.setTransform(t2);
				td.appendChild(svgCopy);
			}
		}
		return td;
		
	}

	private void createAndFillHead(HtmlTable table) {
		HtmlThead thead = table.getOrCreateThead();
		HtmlTr headTr = thead.getOrCreateChildTr();
		for (int i = 0; i < displayList.size(); i++) {
			HtmlTh th = new HtmlTh();
			th.appendChild(displayList.get(i));
			headTr.appendChild(th);
		}
	}




}
