package org.contentmine.ami.plugins.phylotree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.diagramAnalyzer.PhyloTreePixelAnalyzer;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlElement;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNode;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTree;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.contentmine.graphics.svg.text.SVGPhrase;
import org.contentmine.norma.image.ocr.HOCRReaderOLD;
import org.contentmine.norma.image.ocr.HOCRConverter;
import org.contentmine.norma.util.CommandRunner;

/** the main data and logic of phylo.
 * 
 * @author pm286
 *
 */
public class PhyloCore {

	public static final Logger LOG = Logger.getLogger(PhyloCore.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	

	private PhyloTreeArgProcessor argProcessor;
	private String hocrHtmlFilename;
	private HOCRReaderOLD hocrReader;
	private String hocrSvgFilename;
	private String svgFilename;
	private File outputDir;
	private String outputRoot;
	private HOCRConverter imageToHOCRConverter;
	private Double joiningRadius = 40.0;
	NexmlProcessor nexmlProcessor;
	private SVGXTree svgxTree;
	
	private static final String PNG = ".png";
	private static final int DEFAULT_RETRIES_FOR_TESSERACT_EXIT = 60;
	private static final String HOCR_HTML_SUFFIX = ".pbm.png.hocr.html";
	private static final String HOCR_SUFFIX = ".pbm.png.hocr";
	private static final String HOCR_SVG_SUFFIX = ".pbm.png.hocr.svg";
	private static Real2Range DEFAULT_HOCR_WORD_JOINING_BOX = new Real2Range(new RealRange(0.0, 20.0), new RealRange(-5.0, 5.0));
	
	public PhyloCore(PhyloTreeArgProcessor argProcessor) {
		this.argProcessor = argProcessor;
		this.nexmlProcessor = new NexmlProcessor(argProcessor);
	}

	PhyloCore() {
		this(new PhyloTreeArgProcessor());
	}

	void outputResults() {
		LOG.trace("cTreeLog: "+argProcessor.TREE_LOG());
		
		File resultsDir = new File(argProcessor.getCurrentCTreeDirectory(), "results");
		File phyloTreeDir = new File(resultsDir, "phylotree");
		if (nexmlProcessor.getNexml() != null) {
			if (nexmlProcessor.getNexmlFilename() != null) {
				nexmlProcessor.outputNexml(phyloTreeDir);
			}
			if (nexmlProcessor.getNewickFilename() != null) {
				nexmlProcessor.outputNewick(phyloTreeDir);
			}
			if (hocrHtmlFilename != null) {
				outputHocrHtml(phyloTreeDir);
			}
			if (hocrSvgFilename != null) {
				outputHocrSvg(phyloTreeDir);
			}
			if (svgFilename != null) {
				outputSvg(phyloTreeDir);
			}
		}
	}

	private void outputHocrHtml(File phyloTreeDir) {
		File hocrHtmlFile = new File(phyloTreeDir, getImageSerial()+".hocr.html");
		HOCRReaderOLD hocrReader = this.getOrCreateHOCRReader();
		try {
			HtmlElement htmlBody = hocrReader.getOrCreateHtmlBody();
			if (htmlBody != null) {
				FileUtils.write(hocrHtmlFile, htmlBody.toXML());
			} else {
				argProcessor.TREE_LOG().error("null HOCR");
			}
		} catch (IOException e) {
			argProcessor.TREE_LOG().error("Cannot create hocrHtmlFile: "+hocrHtmlFile+": "+ e);
		}
	}

	private void outputHocrSvg(File phyloTreeDir) {
		File hocrSvgFile = new File(phyloTreeDir, getImageSerial()+".hocr.svg");
		HOCRReaderOLD hocrReader = this.getOrCreateHOCRReader();
		argProcessor.TREE_LOG().info("wrote HOCSVG: "+hocrSvgFile);
		SVGElement svg = hocrReader.getOrCreateSVG();
		if (svg == null) {
			argProcessor.TREE_LOG().error("null svg");
		} else {
			SVGSVG.wrapAndWriteAsSVG(svg, hocrSvgFile);
		}
	}

	

	// =============================
	
	public HOCRReaderOLD getOrCreateHOCRReader() {
		if (hocrReader == null) {
			hocrReader = new HOCRReaderOLD();
			hocrReader.setJoiningBox(DEFAULT_HOCR_WORD_JOINING_BOX);
		}
		return hocrReader;
	}

	String getImageSerial() {
		return "001"; // must change
	}

	// =============================
	
	private void outputSvg(File phyloTreeDir) {
		File svgFile = new File(phyloTreeDir, getImageSerial()+".svg");
		argProcessor.TREE_LOG().info("wrote HOCSVG: "+svgFile);
		SVGSVG.wrapAndWriteAsSVG(nexmlProcessor.getNexml().createSVG(), svgFile);
	}

	PhyloTreePixelAnalyzer createAndConfigurePixelAnalyzer(BufferedImage image) {
		PhyloTreePixelAnalyzer phyloTreePixelAnalyzer = new PhyloTreePixelAnalyzer();
		phyloTreePixelAnalyzer.setSelectedIslandIndex(0);
		phyloTreePixelAnalyzer.setComputeLengths(true);
		phyloTreePixelAnalyzer.setImage(image);
		return phyloTreePixelAnalyzer;
	}

	private File createHocrOutputFileDescriptor() {
		ensureOutputDirectory();
		return new File(outputDir, outputRoot+HOCR_SUFFIX);
	}

	private File createHocrOutputHtmlFileDescriptor() {
		ensureOutputDirectory();
		return new File(outputDir, outputRoot+HOCR_HTML_SUFFIX);
	}

	private HOCRReaderOLD createHOCRReaderAndProcess(File imageFile) throws IOException,
			InterruptedException, FileNotFoundException {
		ensureOutputDirectory();
		outputDir.mkdirs();
		File hocrOutfile = createHocrOutputFileDescriptor();
		getOrCreateImageToHOCRConverter();
		File htmlFile = imageToHOCRConverter.convertImageToHOCR(imageFile, hocrOutfile);
		if (htmlFile == null || !htmlFile.exists()) {
			LOG.error("cannot run tesseract");
			return null;
		} else {
			getOrCreateHOCRReader();
			hocrReader.readHOCR(new FileInputStream(htmlFile));
		}
		return hocrReader;
	}

	File createHocrSVGFileDescriptor() {
		ensureOutputDirectory();
		return new File(outputDir, outputRoot+HOCR_SVG_SUFFIX);
	}

	/** matches tips to labels.
	 * 
	 * creates lists of failed tips (failedTipList) and failed labels (failedLabels).
	 * 
	 * @param wordLineList
	 * @param nexmlTree
	 */
	public void matchPhrasesToNodes(List<SVGPhrase> unusedPhraseList, NexmlTree nexmlTree) {
		List<NexmlNode> tipNodeList = nexmlTree.getOrCreateTipNodeList();
		Real2Range joiningBox = hocrReader.getWordJoiningBox();
		joiningBox = new Real2Range(new RealRange(0, 50), new RealRange(-10, 10));
		nexmlProcessor.annotateMatchedNodesAndDecrementUnmatchedLists(tipNodeList, unusedPhraseList, joiningBox, null);
		List<NexmlNode> branchNodeList = nexmlTree.getOrCreateNonTipNodeList();
		nexmlProcessor.annotateMatchedNodesAndDecrementUnmatchedLists(branchNodeList, unusedPhraseList, null, getJoiningRadius());
		
	}

	private void ensureOutputDirectory() {
		if (outputDir == null) {
			outputDir = new File("target/junk/"+System.currentTimeMillis()+"/");
			argProcessor.TREE_LOG().info("PhyloTree output to: "+outputDir+"; suggest you setOutputDir()");
		}
	}

	public Double getJoiningRadius() {
		return joiningRadius;
	}

	public CommandRunner getOrCreateImageToHOCRConverter() {
		if (imageToHOCRConverter == null) {
			imageToHOCRConverter = new HOCRConverter();
			imageToHOCRConverter.setTryCount(DEFAULT_RETRIES_FOR_TESSERACT_EXIT);
		}
		return imageToHOCRConverter;
	}

	public boolean mergeOCRAndPixelTree(File imageFile) throws IOException, InterruptedException {
			hocrReader = createHOCRReaderAndProcess(imageFile);
			if (hocrReader == null) return false;
			LOG.trace("start tree");
			NexmlNEXML nexml = nexmlProcessor.createNexmlAndTreeFromPixels(imageFile);
			LOG.trace("created nexml");
			mergeOCRAndPixelTree(hocrReader, nexml);
			LOG.trace("mergedOCR and tree");
			Pattern speciesPattern = nexmlProcessor.getSpeciesPattern();
			if (speciesPattern != null) {
				LOG.warn("old species pattern: "+speciesPattern);
				nexmlProcessor.checkOTUsAgainstSpeciesPattern(nexml, speciesPattern);
			}
			nexmlProcessor.processNexml();
			return true;
		}

	/**
	 * 
	 * @param hocrReader
	 * @param nexml modified by the process
	 */
	public void mergeOCRAndPixelTree(HOCRReaderOLD hocrReader, NexmlNEXML nexml) {
		if (nexml == null) {
			argProcessor.TREE_LOG().error("Cannot create tree");
		} else {
			NexmlTree nexmlTree = nexml.getSingleTree();
			List<SVGPhrase> unusedPhraseList = new ArrayList<SVGPhrase>(hocrReader.getOrCreatePhraseList());
			this.matchPhrasesToNodes(unusedPhraseList, nexmlTree);
		}
	}

	public void setJoiningRadius(Double joiningRadius) {
		this.joiningRadius = joiningRadius;
	}

	

	

	// =============================
	
		void createTree(File inputFile) {
			String suffix = FilenameUtils.getExtension(inputFile.toString());
			svgxTree = null;
			try {
				if (CTree.isImageSuffix(suffix)) {
					if (this.mergeOCRAndPixelTree(inputFile)) {
						argProcessor.TREE_LOG().info("Analyzed pixels for tree successfully");
					} else {
						argProcessor.TREE_LOG().warn("failed to analyze pixels for tree successfully");
					}
	//				createNexmlAndTreeFromPixels(inputFile);
				} else if (CTree.isSVG(suffix)) {
					createNexmlAndTreeFromSVG(inputFile);
				} else {
					throw new RuntimeException("Cannot process as phylotree: "+inputFile);
				}
				if (argProcessor.getOutput() != null) {
					File outputFile = new File(argProcessor.getOutput());
					outputFile.getParentFile().mkdirs();
					XMLUtil.debug(svgxTree, new FileOutputStream(outputFile), 1);
				}
				PhyloResultsElement resultsElement = new PhyloResultsElement(getTitle());
			} catch (Exception e) {
				throw new RuntimeException("Cannot read/interpret tree: "+inputFile, e);
			}
		}

		public NexmlNEXML mergeFiles(File hocrFile, File nexmlFile) throws Exception {
			getOrCreateHOCRReader();
			hocrReader.createWordLineList(hocrFile);
			NexmlNEXML nexml = (NexmlNEXML) NexmlElement.readAndCreateNEXML(nexmlFile);
			this.mergeOCRAndPixelTree(hocrReader, nexml);
			return nexml;
		}

		

		//	public InputStream getSpeciesPatternInputStream() {
		//		return speciesPatternInputStream;
		//	}
		
			public static void convertPngToHTML_SVG_NEXML_NWK(File infile, File outdir) 
					throws IOException, InterruptedException, FileNotFoundException {
				if (infile == null || outdir == null) {
					throw new RuntimeException("files must not be null");
				}
				if (infile.isDirectory()) {
					List<File> pngFiles = new ArrayList<File>(FileUtils.listFiles(infile, new String[]{"png"}, false));
					for (File pngFile : pngFiles) {
						String basename = FilenameUtils.getBaseName(pngFile.getAbsolutePath());
						File outputSubDir = new File(outdir, basename);
						outputSubDir.mkdirs();
						convertPng(pngFile, outputSubDir);
					}
				} else {
					convertPng(infile, outdir);
				}
			}

		private static void convertPng(File pngfile, File outdir)
				throws IOException, InterruptedException, FileNotFoundException {
			String name = pngfile.getName();
			String root = name.substring(0, name.length() - PNG.length());
			org.apache.commons.io.FileUtils.copyFile(pngfile, new File(outdir, root+PNG));
			PhyloCore phyloCore = new PhyloCore();
			phyloCore.setOutputRoot(root);
			phyloCore.setOutputDir(outdir);
			if (phyloCore.mergeOCRAndPixelTree(pngfile)) {
				NexmlNEXML nexml = phyloCore.getNexmlProcessor().getNexml();
				outdir.mkdirs();
				XMLUtil.debug(nexml, new FileOutputStream(new File(outdir, root+".nexml.xml")), 1);
				FileUtils.write(new File(outdir, root+".nwk"), nexml.createNewick());
				XMLUtil.debug(nexml.createSVG(), new FileOutputStream(new File(outdir, root+".svg")), 1);
				HOCRReaderOLD hocrReader = phyloCore.getOrCreateHOCRReader();
				SVGSVG.wrapAndWriteAsSVG(hocrReader.getOrCreateSVG(), new File(outdir, root+".words.svg"));
			}
		}

		private NexmlProcessor getNexmlProcessor() {
			return nexmlProcessor;
		}

		public void setSvgFilename(String string) {
			this.svgFilename = string;
		}

		public void setHocrSvgFilename(String string) {
			this.hocrSvgFilename = string;
		}

//		public DiagramTree getDiagramTree() {
//			return diagramTree;
//		}

		public void setOutputDir(File outdir) {
			this.outputDir = outdir;
		}

		public void setOutputRoot(String root) {
			this.outputRoot = root;
		}

		public void setHocrHtmlFilename(String string) {
			this.hocrHtmlFilename = string;
		}

		// =============================
		
		private String getTitle() {
			return "dummyTitle";
		}

		public void createNexmlAndTreeFromSVG(File svgInputFile) {
			SVGElement svgElement = SVGElement.readAndCreateSVG(svgInputFile);
			svgxTree = SVGXTree.makeTree(svgElement, 1.0, MergeMethod.TOUCHING_LINES);
			argProcessor.LOG.trace("tree "+svgxTree.toXML());
		}


}
