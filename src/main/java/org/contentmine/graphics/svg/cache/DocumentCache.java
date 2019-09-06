package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.euclid.util.CMStringUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.layout.PubstyleManager;
import org.contentmine.graphics.svg.layout.SVGPubstyle;
import org.contentmine.graphics.util.FilePathGlobber;

import nu.xom.Comment;

/** manages a complete document of several pages.
 * should be linked intimately with a CTree.
 * 
 * Documentcache should only be instantiated if there is a corresonding CTree,
 * hence no public constructor.
 * 
 * @author pm286
 *
 */
public class DocumentCache extends ComponentCache {
	
	private static final Logger LOG = Logger.getLogger(DocumentCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String BOX = ".box";
	// we will drop this very shortly, maybe even now
	private static final String OPTIONAL_SVG_COMPACT = "(\\.svg\\.compact)?";
	// note we have to have leading .* to match whole name
	public static final String FULLTEXT_SVG_REGEX = ".*/svg/" + CTree.FULLTEXT_PAGE_REGEX + "(\\d+)" + OPTIONAL_SVG_COMPACT + "\\.svg";

	private boolean createSummaryBoxes;
	private List<File> svgFiles;
	private PageCacheList pageCacheList;
	// this counts from 1 (normally the SVG files)
	private PageCacheList pageCacheListByFileNumber;

	private PageLayout frontPageLayout;
	private PageLayout middlePageLayout;
	private PageLayout backPageLayout;
	private PageLayout currentPageLayout;
	private int npages;
	private HtmlElement htmlDiv;
	private SVGPubstyle pubstyle;
	private PubstyleManager pubstyleManager;
	private CTree cTree;

	protected DocumentCache() {
		init();
	}

	private void init() {
		
	}

	public DocumentCache(CTree cTree) {
		this();
		this.setCTree(cTree);
	}
	
	/** create DocumentCache for this directory.
	 * creates a new CTree and sync's them
	 * 
	 * @param directory
	 * @return
	 */
	public static DocumentCache createDocumentCache(File directory) {
		CTree cTree = new CTree(directory);
		return new DocumentCache(cTree);
	}

	public AbstractCMElement processSVGFilesToSVGElementAndHTMLElement() {
		if (cTree != null) {
			FilePathGlobber globber = new FilePathGlobber();
			globber.setRegex(DocumentCache.FULLTEXT_SVG_REGEX).setUseDirectories(false).setLocation(cTree.getDirectory().toString());
			try {
				svgFiles = globber.listFiles();
				CMFileUtil.sortUniqueFilesByEmbeddedIntegers(svgFiles);
			} catch (IOException e) {
				throw new RuntimeException("Globber failed", e);
			}
			if (svgFiles.size() == 0) {
				LOG.warn("no files found in: "+cTree+" with "+DocumentCache.FULLTEXT_SVG_REGEX);
			}
			try {
				processSVGFilesToSVGElementAndHTMLElement(svgFiles);
			} catch (Exception e) {
				throw new RuntimeException("Failed in: "+this.cTree.toString(), e);
			}
		}
		return convertedSVGElement;
	}

	/**
	 * this can be called publicly as well as internally
	 * currently:
	 * 		getOrCreatePageCacheList();
		addPagesToConvertedSVGElement();
		createHtmlElementFromPages();
		summarizePages();

	 * @param svgFiles
	 */
	public void processSVGFilesToSVGElementAndHTMLElement(List<File> svgFiles) {
		this.svgFiles = CMFileUtil.sortUniqueFilesByEmbeddedIntegers(svgFiles);
		convertedSVGElement = new SVGG();
		pageCacheList = getOrCreatePageCacheList();
		LOG.trace("pageCacheList "+pageCacheList.size());
		addPagesToConvertedSVGElement();
		createHtmlElementFromPages();
		summarizePagesNYI();
	}

	private HtmlElement createHtmlElementFromPages() {
		htmlDiv = new HtmlDiv();
		for (int ipage = 0; ipage < pageCacheList.size(); ipage++) {
			PageCache pageCache = pageCacheList.get(ipage);
			TextCache textCache = pageCache.getOrCreateTextCache();
			htmlDiv.appendChild(new Comment("======page "+ipage+" L======="));
			RealRange yr = new RealRange(33, 698);
			HtmlElement htmlElementL = textCache.createHtmlFromBox(new RealRange(0, 260), yr);
			htmlDiv.appendChild(htmlElementL);
			htmlDiv.appendChild(new Comment("======page "+ipage+" R======="));
			HtmlElement htmlElementR = textCache.createHtmlFromBox(new RealRange(250, 550), yr);
			htmlDiv.appendChild(htmlElementR);
//			convertedSVGElement.appendChild(pageCache.getExtractedSVGElement().copy());
		}
		return htmlDiv;
	}

	
	private void addPagesToConvertedSVGElement() {
		for (int ipage = 0; ipage < pageCacheList.size(); ipage++) {
			PageCache pageCache = pageCacheList.get(ipage);
			convertedSVGElement.appendChild(pageCache.getExtractedSVGElement().copy());
		}
	}

	public void analyzePages(File pageDir, String pubstyle, int npages, String fileDir, File targetDir) {
		makePageLayouts(pubstyle);
		this.setPageCount(npages);
		this.getOrCreatePageCacheList();
		LOG.trace("pageCaches: "+pageCacheList.size());
		for (int ipage = 0; ipage < npages; ipage++) {
			LOG.trace("PAGE "+ipage);
			PageCache pageCache = new PageCache(this);
			SVGElement boxes = debugPage(pageDir, fileDir, ipage, pageCache);
			File outFileSVG = new File(targetDir, fileDir+"/fulltext-page" + ipage + CTree.DOT+CTree.SVG);
			LOG.trace("out "+outFileSVG);
			SVGSVG.wrapAndWriteAsSVG(boxes, outFileSVG);
		}
	}

	private void setPageCount(int npages) {
		this.npages = npages;
	}

	private SVGElement debugPage(File pageDir, String fileDir, int ipage, PageCache pageCache) {
		currentPageLayout = getCurrentPageLayout(ipage);
		pageCache.setPageLayout(currentPageLayout);
		File svgFile = new File(pageDir, fileDir + "/svg/" + CTree.FULLTEXT_PAGE + ipage + CTree.DOT+CTree.SVG);
		pageCache.readGraphicsComponentsAndMakeCaches(svgFile);
		pageCache.readPageLayoutAndMakeBBoxesAndMargins(currentPageLayout);
		AbstractCMElement boxg = pageCache.createSummaryBoxes(svgFile);
		SVGElement boxes = pageCache.createSVGElementFromComponents();
		boxes.appendChild(boxg.copy());
		return boxes;
	}

	private void summarizePagesNYI() {
		LOG.trace("SUMMARIZE PAGES NYI");
	}

	public PageCacheList getOrCreatePageCacheList() {
		if (pageCacheList == null) {
//			pageCacheList = new PageCacheList();
//			createPageCacheListFromSVGFiles();
			convertSVG2PageCacheList();
			for (int i = 0; i < pageCacheList.size(); i++) {
				PageCache pageCache = pageCacheList.get(i);
				if (pageCache == null) {
					continue;
				}
				Integer serialNumber = pageCache.getSerialNumber();
				if (serialNumber != null) {
					pageCacheList.set(serialNumber, pageCache);
				}
			}
		}
		return pageCacheList;
	}

	public PageCacheList getOrCreatePageCacheListByFileNumber() {
		if (pageCacheListByFileNumber == null) {
			pageCacheListByFileNumber = new PageCacheList();
			convertSVG2PageCacheList();
			for (PageCache pageCache : pageCacheList) {
				if (pageCache == null) {
					continue;
				}
				Integer serialNumber = pageCache.getSerialNumber();
				pageCacheListByFileNumber.set(serialNumber, pageCache);
			}
		}
		return pageCacheListByFileNumber;
	}

	private SVGPubstyle getOrCreatePubstyle() {
		if (this.pubstyle == null) {
			ensurePubstyleManager();
			getOrCreateSvgFiles();
			if (svgFiles.size() > 0) {
				pubstyle = pubstyleManager.guessPubstyleFromFirstPage(svgFiles.get(0));
			}
		}
		return pubstyle;
	}

	private void ensurePubstyleManager() {
		if (pubstyleManager == null) {
			pubstyleManager = new PubstyleManager();
		}
	}

//	private void createPageCacheListFromSVGFiles() {
//		svgFiles = cTree.getExistingSVGFileList();
//		svgFiles = CMFileUtil.sortUniqueFilesByEmbeddedIntegers(svgFiles);
//		pageCacheList = new PageCacheList();
//		for (File svgFile : svgFiles) {
//			PageCache pageCache = new PageCache(this);
//		}
//	}

	public CTree getCTree() {
		return cTree;
	}

	public void setCTree(File cTreeDir) {
		if (cTree != null) {
			throw new RuntimeException("Already has a CTree: "+cTreeDir);
		}
		if (cTreeDir != null) {
			cTree = new CTree(cTreeDir);
		}
	}

	public void setCTree(CTree cTree) {
		this.cTree = cTree;
	}

	public boolean isCreateSummaryBoxes() {
		return createSummaryBoxes;
	}

	/** create SVGRect bounding boxes for the components discovered.
	 * draws rects on totalSvgElement
	 * @param createSummaryBoxes
	 */
	public void setCreateSummaryDebugBoxes(boolean createSummaryBoxes) {
		this.createSummaryBoxes = createSummaryBoxes;
	}

	public List<File> getOrCreateSvgFiles() {
		if (svgFiles == null) {
			svgFiles = new ArrayList<File>();
		}
		return svgFiles;
	}

	public void setSvgFiles(List<File> svgFiles) {
		this.svgFiles = svgFiles;
	}

	private void makePageLayouts(String pubstyle) {
		InputStream frontInputStream = getClass().getResourceAsStream(pubstyle+PageLayout.FRONT+PageLayout.DOT_SVG);
		this.frontPageLayout = PageLayout.readPageLayoutFromStream(frontInputStream);
		InputStream middleInputStream = getClass().getResourceAsStream(pubstyle+PageLayout.MIDDLE+PageLayout.DOT_SVG);
		this.middlePageLayout = PageLayout.readPageLayoutFromStream(middleInputStream);
		InputStream backInputStream = getClass().getResourceAsStream(pubstyle+PageLayout.BACK+PageLayout.DOT_SVG);
		this.backPageLayout = PageLayout.readPageLayoutFromStream(backInputStream);
	}
	
	private PageLayout getCurrentPageLayout(int ipage) {
		PageLayout pageLayout = null;
		if (ipage == 1 && frontPageLayout != null) {
			pageLayout = frontPageLayout;
		} else if (ipage == npages && backPageLayout != null) {
			pageLayout = backPageLayout;
		} else {
			pageLayout = middlePageLayout;
		}
		if (pageLayout == null) {
			pageLayout = PageLayout.getDefaultPageLayout();
			LOG.warn("Couldn't find bespoke layout, using default");
		}
		return pageLayout;
	}

	public HtmlElement getOrCreateConvertedHtmlElement() {
		return htmlDiv;
	}

	public HtmlElement getHtmlDiv() {
		return htmlDiv;
	}

	public String getTitle() {
		return cTree == null ? null : cTree.getName();
	}
	
	public int getPageCount() {
		return this.getOrCreatePageCacheListByFileNumber().size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("tree: "+cTree+"; pages: "+pageCacheList.size()+"; xml: "+(htmlDiv == null ? "null" : htmlDiv.toXML().length()));
		return sb.toString();
	}
	
	public List<PageCache> convertSVG2PageCacheList() {
		if(pageCacheList == null) {
			List<File> svgFiles = cTree.getExistingSVGFileList();
			svgFiles = CMFileUtil.sortUniqueFilesByEmbeddedIntegers(svgFiles);
			int page = 0;
			ensurePageCacheList();
			for (File svgFile : svgFiles) {
				PageCache pageCache = new PageCache();
				Integer serial = CMStringUtil.getEmbeddedInteger(svgFile.getName());
				pageCache.setSerialNumber(serial);
				int npages = pageCacheList.size();
				if (npages == page) {
					pageCacheList.add(pageCache);
				} else if (npages < page) {
					throw new RuntimeException("page caches ("+npages+") inconsistent with page "+page);
				}
				pageCache.readGraphicsComponentsAndMakeCaches(svgFile);
				page++;
			}
		}
		return pageCacheList;
	}

	private void ensurePageCacheList() {
		if (pageCacheList == null) {
			pageCacheList = new PageCacheList();
		}
	}

	public HtmlHtml getConcatenatedHtml() {
		throw new RuntimeException("NYI");
	}

	public HtmlDiv convertSVGPages2HTML() {
		convertSVG2PageCacheList();
		HtmlDiv htmlDiv = new HtmlDiv();
		for (int i = 0; i < pageCacheList.size(); i++) {
			PageCache pageCache = pageCacheList.get(i);
			HtmlDiv pageHtml = pageCache.createHTMLFromTextList();
			htmlDiv.appendChild(pageHtml);
		}
		return htmlDiv;
	}
	
	public String getName() {
		return cTree == null ? null : cTree.getName();
	}

}
