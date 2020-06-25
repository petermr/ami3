package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.contentmine.ami.tools.AMIPDFTool;
import org.contentmine.ami.tools.AMIPDFTool.PDFTidySVG;
import org.contentmine.ami.tools.AMIPDFTool.ParserType;
import org.contentmine.ami.tools.AMISVGTool.TidySVG;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;

import nu.xom.IllegalCharacterDataException;

/** helper class extending PDFBox renderer
 * works with PageDrawer to capture the PDF input stream
 * 
 * @author pm286
 *
 */
public class DocumentParser extends PDFRenderer {
	private static final Logger LOG = LogManager.getLogger(DocumentParser.class);
private AbstractPageParser currentPageParser;
	private SVGG currentSVGG;
	private Map<PageSerial, SVGG> svgPageBySerial;
	private Map<PageSerial, BufferedImage> renderedImageBySerial;
	private int pageIndex;
	private int iPage;
	private Map<String, BufferedImage> rawImageByTitle;
	private PDFDocumentProcessor documentProcessor;
	private double yeps = 0.00001;


	DocumentParser(PDDocument document) {
        super(document);
        LOG.trace("created parserRenderer");
        clean();
    }
	
	public void clean() {
		currentPageParser = null;
		currentSVGG = null;
		svgPageBySerial = null;
		rawImageByTitle = null;
		renderedImageBySerial = null;
		pageIndex = -1;
		iPage = -1;
	}

	/** create pageParser, actually a subclassed PageDrawer.
	 * think of PageDrawer as a parser that routes graphics ot SVG and creation
	 * of bufferedImages. Never call this.
	 * 
	 */
    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
        currentPageParser = null;
        ParserType parserType = documentProcessor.getParserType();
        if (parserType == null) {
        	throw new RuntimeException("must set parserType");
        } else if (AMIPDFTool.ParserType.zero.equals(parserType)) {
			currentPageParser = new PageParserZero(parameters, iPage, (AMIDebugParameters) null);
//        } else if (AMIPDFTool.ParserType.one.equals(parserType)) {
//			currentPageParser = new PageParserOne(parameters, iPage, (AMIDebugParameters) null);
        } else if (AMIPDFTool.ParserType.two.equals(parserType)) {
        	currentPageParser = new PageParserTwo(parameters, iPage, AMIDebugParameters.getBriefParameters());
        } else {
        	throw new RuntimeException(" cannot create parser: "+parserType);
        }
        currentPageParser.setDocumentProcessor(documentProcessor);
        return currentPageParser;
    }
    
    /** process the page - main entry point
     * 
     *  calls super.renderImage() 
     * 
     * @param iPage
     * @return
     */
	public void processPage(int iPage) {
		PageSerial pageSerial = PageSerial.createFromZeroBasedPage(iPage);
		this.iPage = iPage;
		try {
			BufferedImage renderImage = super.renderImage(this.iPage);
			currentPageParser.setRenderedImage(renderImage);
			currentPageParser.setPageSerial(pageSerial);
		} catch (IOException e) {
			LOG.error("fails to parse page " + e);
		} catch (IndexOutOfBoundsException ioobe) {
			ioobe.printStackTrace();
			LOG.error("BUG! "+ioobe);
		}
	}
	
    /**
     * Returns the given page as an RGB image at 72 DPI
     * @param pageIndex the zero-based index of the page to be converted.
     * @return the rendered page image
     * @throws IOException if the PDF cannot be read
     */
	/** meant to catch page index, but doesn't yet work ...*/
	@Override
    public BufferedImage renderImage(int pageIndex) throws IOException
    {
    	this.pageIndex = pageIndex;
        return super.renderImage(pageIndex, 1);
    }

	public AbstractPageParser getPageParser() {
		return currentPageParser;
	}

	/** reads PDF and extracts images and creates SVG.
	 * 
	 * @param file
	 * @return
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public Map<PageSerial, BufferedImage> parseDocument(PDFDocumentProcessor documentProcessor, PDDocument currentDoc) throws IOException {
		this.documentProcessor = documentProcessor;
		renderedImageBySerial = new HashMap<PageSerial, BufferedImage>();
        svgPageBySerial = new HashMap<PageSerial, SVGG>();
        rawImageByTitle = new HashMap<>();
        int numberOfPages = currentDoc.getNumberOfPages();
    	PageIncluder pageIncluder = documentProcessor.getOrCreatePageIncluder();
        iPage = 0;
        for (; iPage < numberOfPages; iPage++) {
        	
			PageSerial pageSerial = PageSerial.createFromZeroBasedPage(iPage);
			if (pageIncluder.pageIsIncluded(pageSerial)) {
	        	parsePage(pageSerial);
        	}
        }
        return renderedImageBySerial;
	}

	private void parsePage(PageSerial pageSerial) {
		System.out.print("["+pageSerial.getOneBasedPage()+"]");
		this.processPage(iPage);
		BufferedImage renderedImage = currentPageParser.getRenderedImage();
		renderedImageBySerial.put(pageSerial, renderedImage);
		SVGG svgPage = extractSVGG();
		tidySVG(svgPage);
		svgPageBySerial.put(pageSerial, svgPage);
		// FIXME we should write the images to disk, not store them?
		Map<String, BufferedImage> subImageMap = currentPageParser.getOrCreateRawImageMap();
		List<String> sortedImageTitles = new ArrayList<>(subImageMap.keySet());
		Collections.sort(sortedImageTitles);
		for (String title : sortedImageTitles) {
			BufferedImage image = subImageMap.get(title);
			rawImageByTitle.put(title, image);
		}
	}
	
	/** reads PDF and extracts images and creates SVG.
	 * 
	 * @param file
	 * @return list of files (can be empty but not null)
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public List<SVGG> getOrCreateSVGPageList() {
		List<SVGG> pageList = null;
		if (svgPageBySerial != null) {
			pageList = new ArrayList<SVGG>(svgPageBySerial.values());
		} else {
			pageList = new ArrayList<SVGG>();
		}
        return pageList;
	}
	
	public Map<String, BufferedImage> getRawImageMap1() {
		return rawImageByTitle;
	}

	private void tidySVG(SVGG svgPage) {
		if (documentProcessor.getTidySVGList().contains(PDFTidySVG.concat)) {
			concatenateCharacters(svgPage);
		}
		if (documentProcessor.getTidySVGList().contains(PDFTidySVG.spaces)) {
			addSpaces(svgPage);
		}
		if (documentProcessor.getTidySVGList().contains(PDFTidySVG.styles)) {
			addEmpiricalStyles(svgPage);
		}
	}

	private void addEmpiricalStyles(SVGG svgPage) {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svgPage);
		for (SVGText text : texts) {
			text.addEmpiricalStylesFromFont();
		}
	}

	private void addSpaces(SVGG svgPage) {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svgPage);
		for (SVGText text : texts) {
			text.addSpaces();
		}
	}

	private void concatenateCharacters(SVGG svgPage) {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svgPage);
		SVGText.concatenate(texts, yeps);
	}

	private SVGG extractSVGG() {
        currentSVGG = currentPageParser.getSVGG();
        
		return currentSVGG;
	}

	public List<SVGG> getOrCreateSVGList() {
		return new ArrayList<SVGG>(svgPageBySerial.values());
	}

	public List<BufferedImage> getOrCreateRenderedImageList() {
		return new ArrayList<BufferedImage>(renderedImageBySerial.values());
	}

	public Map<PageSerial, SVGG> getOrCreateSvgPageBySerial() {
		if (svgPageBySerial == null) {
			svgPageBySerial = new HashMap<PageSerial, SVGG>();
		}
		return svgPageBySerial;
	}




}
