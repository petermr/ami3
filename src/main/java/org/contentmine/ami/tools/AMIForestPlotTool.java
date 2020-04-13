package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.template.AbstractTemplateElement;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.text.SVGPhrase;
import org.contentmine.graphics.svg.text.SVGTextLine;
import org.contentmine.graphics.svg.text.SVGTextLineList;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.norma.image.ocr.HOCRConverter;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */
@Command(
name = "forest",
description = {
		"Analyzes ForestPlot images.",
		"Uses template.xml to steer the operations."
})
public class AMIForestPlotTool extends AbstractAMITool implements HasImageDir {

	static final Logger LOG = Logger.getLogger(AMIForestPlotTool.class);
	private static final String TEMPLATE_XML = "template.xml";

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum ForestPlotType {
		spss,
		stata;
		public static ForestPlotType getPlotType(String s) {
			if (s == null) return null;
			if (s.toLowerCase().equals(spss.toString())) return spss;
			if (s.toLowerCase().equals(stata.toString())) return stata;
			return null;
		}
		
	}
	
	private enum Axis {
		x,
		y
	}
	
//	private enum Orientation {
//		horizontal,
//		vertical,
//	}

    @Option(names = {"--color"},
    		arity = "1",
            description = "colors of lines on plot as hex string")
    private Integer color = 0x0;

    @Option(names = {"--display"},
    		arity = "1..*",
            description = "display files in panel"
            )
    private List<String> displayList = null;

    @Option(names = {"--hocr"},
    		arity = "1",
            description = "use HOCR output from Tesseract"
//            defaultValue = "true"
            )
    private boolean useHocr;

    @Option(names = {"--minline"},
    		arity = "1",
            description = "minimum line length")
    private Integer minline = 300;

    @Option(names = {"--minnested"},
    		arity = "1",
            description = "minimum level for internal countours to be significant. "
            		+ "Above this level we new expect isolated pixel islands to appear."
            		+ "Arcane and experiemental.")
    private Integer minNestedRings = 2;
	
    @Option(names = {"--offset"},
    		arity = "1..*",
            description = "offsets from split position/s")
    private List<Integer> offsets;

//    @Option(names = {"--orientation"},
//    		arity = "0",
//            description = "display direction (horizontal or vertical)")
//    private Orientation orientation = Orientation.horizontal;

    @Option(names = {"--plottype"},
    		arity = "1",
            description = "type of SPSS plot")
    private ForestPlotType plotType;

    @Option(names = {"--radius"},
    		arity = "1",
            description = "radius for drawing circle round centroid")
    private Double radius = 4.0;

    @Option(names = {"--segment"},
    		arity = "0",
            description = "segment using template file; requires --template")
    private boolean segment = false;

//    @Option(names = {"--summary"},
//    		arity = "1",
//            description = "create summary listing of files of given name in higher level directory."
//            		+ "e.g. summarize all raw.png files in ../raw.html . exploratory. Can be used recursively."
//            		+ "Currently used with --display.")
//    private String summaryFilename = null;

    @Option(names = {"--table"},
    		arity = "1",
            description = "svgFile containing potential table; path relative to inputname; e.g. 'hocr/hocr.svg")
    private String table = null;

    @Option(names = {"--tableType"},
    		arity = "1..*",
            description = "string describing type of table (experimental) - e.g 'leftjust'"
            )
    List<String> tableTypeList = new ArrayList<>();

    @Option(names = {"--template"},
    		arity = "1",
            description = "template to give imagedir-specific operations "
            		+ "(often created automatically by ami-image),"
            		)
    private String templateFilename = null;

	private Real2Array localSummitCoordinates;
	private DiagramAnalyzer diagramAnalyzer;
	private SVGLineList horizontalLines;
	private String basename;

	private Map<String, String> lineTypeByAbbrev;

	private Multiset<String> abbrevSet;
	private List<List<String>> phraseListList;

	private HtmlElement hocrElement;
	private File imageDir;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIForestPlotTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIForestPlotTool() {
		init();
	}
	
	public void init() {
		createLineTypeByAbbrev();
		createPhraseSet();
		abbrevSet = HashMultiset.create();
	}

	private void createPhraseSet() {
		phraseListList = new ArrayList<>();
		
		List<String> phrases;
		phrases = Arrays.asList(new String[] {
				"Study or Subgroup", 
				"Mean", "SD", "Total",
				"Mean", "SD", "Total",
				"Weight"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"Study or Subgroup", 
				"Events", "Total",
				"Events", "Total",
				"Weight"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"Study",
				"or",
				"Subgroup", 
				"log",
				"\\[",
				"Odds",
				"Ratio",
				"\\]",
				"SE",
				"Weight"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"Total",
				"\\(",
				"95%",
				"C(I|l)",
				"\\)",
				"%I",
				"%I"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"Total",
				"events",
				"%I",
				"%I"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"Heterogeneity:", 
				"Tau.?",
				"=",
				"%F",
				";",
				"Chi.?",
				"=",
				"%F",
				",",
				"df",
				"=",
				"I",
				"\\(",
				"P",
				"=", 
				"%F",
				"\\)",
				";",
				"I.?",
				"=",
				"%F"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"Heterogeneity:", 
				"Chi.?",
				"=",
				"%F",
				",",
				"df",
				"=",
				"%I",
				"\\(",
				"P",
				"=",
				"%F",
				"\\)",
				";",
				"I.?",
				"=",
				"%%"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"Test",
				"for",
				"overall",
				"effect:", 
				"Z",
				"=",
				"%F",
				"\\(",
				"P",
				"<",
				"%F",
				"\\)",
				"Favours",
				"%A",
				
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"[A-a].*",
				"%D",
				"%I",
				"%I",
				"%I",
				"%I",
				"%%", 
				"%F", 
				"[\\[\\(]",
				"%F",
				"\\,",
				"%F",
				"[\\]\\)]",
				"%D"
				});
		phraseListList.add(phrases);
		phrases = Arrays.asList(new String[] {
				"\\-?\\d+",
				"\\-?\\d+",
				"\\-?\\d+",
				"\\-?\\d+",
				"\\-?\\d+"
				});
		phraseListList.add(phrases);
	}

	private void createLineTypeByAbbrev() {
		lineTypeByAbbrev = new HashMap<>();
		
		lineTypeByAbbrev.put("AIIIIIII%FBFPFC", "1");
		lineTypeByAbbrev.put("AIIIII%FBFPFCI", "2");
		lineTypeByAbbrev.put("AAII", "3");
		lineTypeByAbbrev.put("AB%ACII%FBFPFC", "4");
		lineTypeByAbbrev.put("SIIII%FBFPFC", "5");
		lineTypeByAbbrev.put("AAASAEFBAEFC", "6");
		lineTypeByAbbrev.put("AB%ACII%FBFPFC", "7");
		lineTypeByAbbrev.put("AIIIII%FBFPFC", "8");
		lineTypeByAbbrev.put("AABICIIII%FBFPFC", "9");
		lineTypeByAbbrev.put("AB%ACII%FBFPFC", "10");
		lineTypeByAbbrev.put("ASSEFPAEIBAEFCPSE%", "11");
		lineTypeByAbbrev.put("B%ACII%FBFPFCA", "12");
		lineTypeByAbbrev.put("SABICIIII%FBFPFC", "13");
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIForestPlotTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
		System.out.println("color of lines      " + color);
		System.out.println("files to display    " + displayList);
		System.out.println("min line length     " + minline);
		System.out.println("min nested rings    " + minNestedRings);
		System.out.println("radius of contours  " + radius);
		System.out.println("plot type           " + plotType);
		System.out.println("use Hocr            " + useHocr);
		System.out.println("offsets             " + offsets);
		System.out.println("scaledFilename      " + basename);
		System.out.println("segment             " + segment);
		System.out.println("table               " + table);
		System.out.println("tableType           " + tableTypeList);
		System.out.println("template            " + templateFilename);
		System.out.println();

		System.out.println();
	}

    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree ie imageFile");
	    }
    }

	protected boolean processTree() {
		ImageDirProcessor imageDirProcessor = new ImageDirProcessor(this, cTree);
		processedTree = imageDirProcessor.processImageDirs();
		return processedTree;
	}

	@Override
	public File getImageFile(File imageDir, String inputname) {
		File imageFile = inputname != null ? new File(imageDir, inputname + "." + CTree.PNG) :
			AbstractAMITool.getRawImageFile(imageDir);
		return imageFile;
	}

	@Override
	public void processImageDir(File imageFile) {
		processImageDirCallback(imageFile);
	}

	@Override
	public void processImageDir() {
		
		LOG.error("Must override processImageDir() in:"+this.getClass().getName());
		processImageDirCallback(null);
	}

	private void processImageDirCallback(File imageFile) {
		if (imageFile == null) {
			throw new RuntimeException("null imageFile ");
		}
		this.imageDir = imageFile.getParentFile();
		if (segment) {	
			AbstractTemplateElement templateElement = 
					AbstractTemplateElement.readTemplateElement(imageDir, templateFilename);
			if (templateElement != null) {
				templateElement.process();
			}
		}
		if (useHocr) {
			File textLineListFile = HOCRConverter.getTextLineListFilename(imageDir);
			createForestPlotFromImageText(textLineListFile);
		} else if (false) {
			/** maybe wrong to comment out
			File imageFile = getRawImageFile(imageDir);
			*/
			createForestPlotFromImage(imageFile);
		}
		if (table != null) {
			if (getInputBasename() == null) {
				throw new RuntimeException("Must give inputbase");
			}
			File svgFile = new File(new File(imageDir, getInputBasename()), table);
			if (!svgFile.exists()) {
				LOG.warn("no file>"+svgFile);
			} else {
				if (tableTypeList != null && tableTypeList.size() > 0) {
					TableExtractor tableExtractor = new TableExtractor();
					tableExtractor.setTableTypeList(tableTypeList);
					tableExtractor.extractTable(svgFile);
				} else {
					LOG.warn("No tableType");
				}
			}
		}
	}

	private void createForestPlotFromImageText(File textLineListFile) {
		SVGElement svgElement = null;
		try {
			if (!textLineListFile.exists()) {
				LOG.error("Cannot find: "+textLineListFile+"\n"
						+ "CHECK that 'hocr' subdirectory exists; you must have run 'ami-ocr' to generate this");
				return;
			}
			svgElement = SVGUtil.parseToSVGElement(new FileInputStream(textLineListFile));
		} catch (FileNotFoundException fnfe) {
			throw new RuntimeException("Cannot find file: "+textLineListFile, fnfe);
		}
		SVGTextLineList textLineList = SVGTextLineList.createSVGTextLineList(svgElement);
		textLineList.splitAtCharacters("[]{}(),<>");
		for (SVGTextLine textLine : textLineList) {
			String abb = textLine.getOrCreateTypeAnnotatedString();
			System.out.println("tl: "+abb+";"+textLine);
			List<SVGPhrase> phraseList = textLine.createPhraseList();
			textLine.annotateWith(phraseListList);
		}
		List<String> textLineAbbs = textLineList.getOrCreateTypeAnnotations();
//		System.out.println("typeLines");
		for (String tl : textLineAbbs) {
			System.out.print(tl);
			String lineType = lineTypeByAbbrev.get(tl);
			if (lineType != null) {
//				System.out.print(" "+lineType);
				
			} else {
				abbrevSet.add(tl);
			}
//			System.out.println();
		}
		return;
	}


	public void createForestPlotFromImage(File imageFile) {
		diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setInputFile(imageFile);
		localSummitCoordinates = diagramAnalyzer.extractLocalSummitCoordinates(minNestedRings, 1);
		horizontalLines = diagramAnalyzer.extractHorizontalLines();
		displayPointsAndLines();
	}

	private void displayPointsAndLines() {
		SVGG g = new SVGG();
		if (localSummitCoordinates != null && localSummitCoordinates.size() > 0) {
			for (Real2 xy : localSummitCoordinates) {
				SVGCircle circle = (SVGCircle) new SVGCircle(xy.format(2), radius).setFill("none").setStrokeWidth(0.7).setStroke("blue");
				g.appendChild(circle);
			}
		}
		if (horizontalLines != null) {
			g.appendChild(horizontalLines.createSVGElement());
		}
		File svgFile = new File("target/"+basename+".junk.svg");
		LOG.debug("svg "+svgFile);
		SVGSVG.wrapAndWriteAsSVG(g, svgFile);
	}
}
