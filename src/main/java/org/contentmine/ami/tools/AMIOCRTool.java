package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIOCRTool.OcrType;
import org.contentmine.ami.tools.ocr.OcrMerger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntegerMultiset;
import org.contentmine.eucl.euclid.IntegerMultisetList;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.text.SVGTextLine;
import org.contentmine.graphics.svg.text.SVGTextLineList;
import org.contentmine.image.ImageUtil;
import org.contentmine.norma.image.ocr.GOCRConverter;
import org.contentmine.norma.image.ocr.HOCRConverter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import nu.xom.Attribute;
import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Optical Character Recognition (OCR) of text
 * 
 * @author pm286
 *
 */
@Command(
name = "ocr",
description = "Extracts text from OCR and (NYI) postprocesses HOCR output to create HTML."
)
public class AMIOCRTool extends AbstractAMITool implements HasImageDir {
	public static final String RAW = "raw";
	public static final String HOCR = "hocr";
	public static final String GOCR = "gocr";
	// fraction of font that descender goes below baseline , approximate
	private static final Double DESCENDER_FUDGE = 0.15;
	
//	private static final int DELTA_Y = 3;
	// amount of bin to consider for overlap
	private static final double EDGE_FRACT = 0.3;
	// minimum gap in bin to split into 2 bins
	private static final double SPLIT_FRACT = 0.4;



	public static final String IMAGE_DOT = "image.";
	
	private static final Logger LOG = LogManager.getLogger(AMIOCRTool.class);

	/** not yet used*/
	public enum LineDir {
		horiz,
		vert,
		both,
		none
	}
	public enum OcrType {
		gocr,
		hocr;

		public static OcrType getType(String typeS) {
			OcrType type = null;
			if (gocr.toString().equals(typeS)) {
				type = gocr;
			} else if (hocr.toString().equals(typeS)) {
				type = hocr;
			}
			return type;
		}
	}
	
	public static final String GOCR_DIR = OcrType.gocr.toString();
	public static final String HOCR_DIR = OcrType.hocr.toString();
@Option(names = {"--disambiguate"},
    		arity = "0..1",
            description = "try to diambiguate characters "
            )
	public boolean disambiguate = false; 

    @Option(names = {"--extractlines"},
    		arity = "1..*",
            description = "extracts textlines from gocr and/or hocr "
//            defaultValue = "none"
            )
	public List<OcrType> extractLines = new ArrayList<OcrType>(); 

    @Option(names = {"--filename"},
    		arity = "1",
            description = "name for transformed Imagefile. no default")
	public String basename = null;
    
    @Option(names = {"--glyphs"},
    		arity = "0",
            description = "add glyphs for characters (GOCR) or string (HOCR, NYI)")
	public boolean glyphs = false;
    
    @Option(names = {"--gocr"},
    		arity = "1",
            description = "path for running gocr"
//            defaultValue = "/usr/local/bin/gocr"
            )
    private String gocrPath = null;
    
    @Option(names = {"--gocrconfig"},
    		arity = "1",
            description = "XML file containing GOCR configs (whitelist, etc. experimental)"
            )
    private String gocrConfig = null;
    
    @Option(names = {"--html"},
    		arity = "0..1",
            description = "create structured html") 
    boolean outputHtml = true;

    @Option(names = {"--maxsize"},
    		arity = "1",
            description = "maximum size of small dimension after scaling")
    private Double maxsize = null;

    @Option(names = {"--merge"},
    		arity = "2..*",
            description = "merge files/XML/SVG into single file")
    private List<String> mergeNames = new ArrayList<>();

//    @Option(names = {"--mergeboxes"},
//    		arity = "2..*",
//            description = "merge boxes (requires *.boxes.svg output with --lines) ")
//    private List<String> mergeBoxes = new ArrayList<>();

    @Option(names = {"--replace"},
    		arity = "2..*",
            description = "characters to substitute misreadings (even number of args, replace a(2n+1) by a(2n+2); )."
            		+ " GOCR often reads numbers as alpha , so (font-dependent) '--replace o 0 q 4 e 2 s 5 S 5 '. ")
    private List<String> replaceList = new ArrayList<>();

    @Option(names = {"--scalefactor"},
    		arity = "1",
            description = "increase geometric scale - helps tesseract. Normally '--maxize' will autoscale, "
            		+ "but this alternative allows forcing scale")
	public Double scalefactor;

    @Option(names = {"--scale"},
    		arity = "0..1",
            description = "apply - helps tesseract to have larger images")
	public Boolean applyScale;

    @Option(names = {"--tesseract"},
    		arity = "1",
            description = "path for tesseract binary e.g. /usr/local/tesseract/"
//            defaultValue = "/usr/local/bin/tesseract"
            )
	public String tesseractPath = null;
    
    @Option(names = {"--hocrconfig"},
    		arity = "1",
            description = "XML file containing Tesseract configs (whitelist, etc. experimental)"
            )
    private String hocrConfig = null;
    


	public File outputHOCRFile;
	public HtmlBody htmlBody;
	private IntegerMultisetList yBinList;
	public HOCRConverter hocrConverter;
	private Multimap<Integer, SVGText> textByYMap;
	private SVGTextLineList textLineList;
	private String newbasename;
	public File imageFile;
	private File imageDir;


	/** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIOCRTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIOCRTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIOCRTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
		LOG.info("disambiguate        {}", disambiguate);
		LOG.info("extractlines        {}", extractLines);
		LOG.info("glyphs              {}", glyphs);
		LOG.info("gocr                {}", gocrPath);
		LOG.info("html                {}", outputHtml);
		LOG.info("maxsize             {}", maxsize);
		LOG.info("mergeNames          {}", mergeNames);
//		LOG.info("mergeBoxes          {}", mergeBoxes);
		LOG.info("replace             {}", replaceList);
		LOG.info("scale               {}", applyScale);
		LOG.info("scalefactor         {}", scalefactor);
		LOG.info("scaledFilename      {}", basename);
		LOG.info("tesseractPath       {}", tesseractPath);
	}


    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
    		LOG.error(DebugPrint.MARKER, "must give cProject or cTree");
	    }
    }

	protected boolean processTree() {
		ImageDirProcessor imageDirProcessor = new ImageDirProcessor(this, cTree);
		processedTree = imageDirProcessor.processImageDirs();
		return processedTree;
	}

	/** this is called from ImageDirProcessor ? move it
	 * 
	 * @param imageFile
	 */
	void runOCR(File imageFile) {
//		LOG.info("image file "+imageFile.getParentFile().getName()+"/"+imageFile.getName());
		
		this.imageFile = imageFile;
		basename = FilenameUtils.getBaseName(imageFile.toString());
		newbasename = FilenameUtils.getBaseName(imageFile.getParentFile().toString());
		if (!imageFile.exists()) { 
			LOG.warn("image file does not exist {}", newbasename);
			return;
		}
		LOG.warn(">{}/{}>", newbasename, basename);
		imageDir = imageFile.getParentFile();
		if (gocrPath != null) {
			GOCRConverter gocrConverter = new GOCRConverter(this);
			if (replaceList.size() > 0) {
				gocrConverter.setReplaceList(replaceList);
			}
			
			try {
				gocrConverter.setGocrPath(gocrPath);
				gocrConverter.setImageFile(imageFile);
				gocrConverter.runGOCR();
			} catch (Exception e) {
				LOG.error("Cannot run GOCR", e);
				return;
			}	
//			processGOCR(imageDir);
		} else if (tesseractPath != null) {
			HOCRConverter converter = new HOCRConverter(this);
			converter.setConfigName(hocrConfig);
			converter.runTesseract(this, imageFile, basename, newbasename);
			if (outputHtml) {
				converter.processTesseractOutput(imageFile);
			}
		}
		// these might be run independently of the OCR
		if (extractLines.contains(OcrType.gocr)) {
			processGOCR(imageDir);
		}
		if (extractLines.contains(OcrType.hocr)) {
			processHOCR(imageDir);
		}
		if (mergeNames.size() >= 2) {
			merge();
		}
//		if (mergeBoxes.size() >= 2) {
//			mergeBoxes();
//		}
	}

	public File processGOCR(File imageDir) {
		GOCRConverter converter = new GOCRConverter(this);
		converter.setConfigName(gocrConfig);
		converter.processGOCR(imageDir);
		return converter.getSVGFile();
	}
	
	private void merge() {
		OcrMerger ocrMerger = new OcrMerger();
		File imageSubDir = new File(imageDir, getInputBasename());
		ocrMerger.addFile(new File(imageSubDir, mergeNames.get(0)));
		ocrMerger.addFile(new File(imageSubDir, mergeNames.get(1)));
		ocrMerger.merge();
	}

//	private void mergeBoxes() {
//		OcrBoxMerger ocrBoxMerger = new OcrBoxMerger();
//		File imageSubDir = new File(imageDir, inputBasename);
//		ocrBoxMerger.addBoxFile(new File(imageSubDir, mergeNames.get(0)));
//		ocrBoxMerger.addBoxFile(new File(imageSubDir, mergeNames.get(1)));
//		ocrBoxMerger.mergeBoxes();
//	}

	public File scaleAndWriteFile(File imageFile, String basename) {
		File scaledFile = null;
		try {
			BufferedImage image = ImageUtil.readImage(imageFile);
			if (applyScale != null && applyScale) {
				double height = image.getHeight();
				double width = image.getWidth();
				double scalex = maxsize / width;
				double scaley = maxsize / width;
				scalefactor = Math.max(scalex,  scaley);
			}
			image = ImageUtil.scaleImageScalr(scalefactor, image);
			File parentFile = imageFile.getParentFile();
			File scaledDir  = new File(parentFile, basename);
			scaledDir.mkdirs();
			scaledFile = new File(scaledDir, basename +"."+CTree.PNG);
			ImageIO.write(image, CTree.PNG, scaledFile);
			imageFile =  scaledFile;
		} catch (IOException e) {
			throw new RuntimeException("Cannot write scaled file: "+scaledFile, e);
		}
		return imageFile;
	}
	
	public SVGTextLineList createTextLineList(File rawSvgFile) throws FileNotFoundException {
		SVGSVG svg = (SVGSVG) SVGUtil.parseToSVGElement(new FileInputStream(rawSvgFile));
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svg);
		createSortedYBinList(textList); 
		textLineList = new SVGTextLineList();
		for (IntegerMultiset yBin : yBinList) {
			/** not used, may have been a cheap table creator */
			for (Integer y : yBin.getSortedValues()) {
				List<SVGText> textListY = new ArrayList<SVGText>(textByYMap.get(y));
				List<String> texts = SVGText.getTextStrings(textListY);
			}
			List<SVGText> binTextList = createTextList(textByYMap, yBin);
			SVGTextLine textLine = new SVGTextLine();
			for (SVGText binText : binTextList) {
				binText.addBBoxAttributeFromSiblingRect();
				binText.formatFontSize(1);
				binText.formatOpacity(3);
				textLine.add(binText);
			}
			textLineList.add(textLine);
		}
		return textLineList;
	}

	private List<SVGText> createTextList(Multimap<Integer, SVGText> textByYMap, IntegerMultiset yBin) {
		List<SVGText> textList = new ArrayList<SVGText>();
		for (Integer i : yBin.elementSet()) {
			List<SVGText> textListY = new ArrayList<SVGText>(textByYMap.get(i));
			textList.addAll(textListY);
		}
		SVGText.sortByX(textList);
		return textList;
	}

	private IntegerMultisetList createSortedYBinList(List<SVGText> textList) {
		int deltaY = 10; // guess bin separation
		textByYMap = createNonEmptyTextMultimap(textList);
		IntArray yArray = createSortedYCoordinates(textByYMap);
		yBinList = new IntegerMultisetList();
		yBinList.createMultisets(yArray, deltaY);
		yBinList.splitMultisets((int) (deltaY * SPLIT_FRACT));
		yBinList.mergeNeighbouringMultisets((int) (deltaY * EDGE_FRACT));
		yBinList.removeEmptyMultisets();
		Collections.sort(yBinList);
		return yBinList;
	}

	private IntArray createSortedYCoordinates(Multimap<Integer, SVGText> textMap) {
		List<Integer> keySet = new ArrayList<Integer>(textMap.keySet());
		Collections.sort(keySet);
		IntArray yArray = new IntArray();
		for (Integer y : keySet) {
			List<SVGText> entryTextList = new ArrayList<SVGText>(textMap.get(y));
			for (int i = 0; i < entryTextList.size(); i++) {
				yArray.addElement(y);
			}
		}
		return yArray;
	}

	private Multimap<Integer, SVGText> createNonEmptyTextMultimap(List<SVGText> textList) {
		Multimap<Integer, SVGText> textMap = ArrayListMultimap.create();
		for (SVGText text : textList) {
			String clazz =  text.getAttributeValue("class");
			if (clazz != null) {
				String txt = text.getText();
				if (txt != null && !"".equals(txt.trim())) {
					Integer y = new Integer((int)(double)text.getY());
					textMap.put(y, text);
				}
			}
		}
		return textMap;
	}
	
    public SVGTextLineList getTextLineList() {
		return textLineList;
	}

    public void writeTextLineList(File file) {
    	if (textLineList != null) {
    		SVGSVG.wrapAndWriteAsSVG((SVGElement) textLineList, file);
    	}
    }

	public File processHOCR(File processedImageDir) {
		File hocrSVGFile = AMIOCRTool.makeOcrOutputFilename(processedImageDir, basename, AMIOCRTool.HOCR, CTree.SVG);
		SVGElement hocrSVG1 = SVGElement.readAndCreateSVG(hocrSVGFile);
		return hocrSVGFile;
	}

	public boolean getDisambiguate() {
		return disambiguate;
	}

	private static File makeOcrTextFilename(File processedImageDir, String basename2, String gocr2) {
		// TODO Auto-generated method stub
		return null;
	}

	public static File makeOcrOutputFilename(File imageDir, String basename, String hocrGocr, String suffix) {
		File hocrGocrDir = makeHocrOcrDir(imageDir, basename, hocrGocr);
		File hocrGocrSVG = new File(hocrGocrDir, hocrGocr + "."+suffix);
		return hocrGocrSVG;
	}

	private static File makeHocrOcrDir(File imageDir, String basename, String hocrGocr) {
		File hocrGocrDir1 =  new File(new File(imageDir, basename), hocrGocr);
		hocrGocrDir1.mkdirs();
		File hocrGocrDir = hocrGocrDir1;
		return hocrGocrDir;
	}
	
	@Override
	public void processImageDir(File imageFile) {
		runOCR(imageFile);
	}

	@Override
	public void processImageDir() {
		LOG.warn("must implement processImageDir");
	}

	@Override
	public File getImageFile(File imageDir, String inputname) {
		File imageFile = inputname != null ? new File(imageDir, inputname+".png") :
			AbstractAMITool.getRawImageFile(imageDir);
		return imageFile;
	}

	public boolean isGlyphs() {
		return glyphs;
	}

	public void setGlyphs(boolean glyphs) {
		this.glyphs = glyphs;
	}



}
