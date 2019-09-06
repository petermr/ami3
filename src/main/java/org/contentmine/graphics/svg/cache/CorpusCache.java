package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.util.FilePathGlobber;

/** manages a complete corpus of several documents.
 * not suitable for large numbers
 * @author pm286
 *
 */
public class CorpusCache extends ComponentCache {
	
	private static final Logger LOG = Logger.getLogger(CorpusCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String DIR_REGEX = "(.*)/fulltext\\.(pdf|xml)";

	private CProject cProject;
	// documents and cTrees should be in sync
	private DocumentCacheList documentCacheList;
	private CTreeList cTreeList;
	private List<HtmlElement> htmlElementList;


	protected CorpusCache() {
		
	}
	
	public CorpusCache(CProject cproject) {
		this.cProject = cproject;
		cProject.setCorpusCache(this);
	}
	
	/** aggreates all HTML in single file
	 * I think
	 */
	public AbstractCMElement createSummaryElement() throws IOException {
		getOrCreateDocumentCacheList();
		convertedSVGElement = new SVGG();
		convertedSVGElement.setFontSize(10.);
		double x = 10.0;
		double y = 20.0;
		double deltaY = 10.;
		int count = 0;
		cProject.getOrCreateCTreeList();
		LOG.debug(cTreeList.size()+"; "+cTreeList);
		for (DocumentCache documentCache : documentCacheList) {
			HtmlElement htmlDiv = documentCache.getHtmlDiv();
			CTree cTree = documentCache.getCTree();
			File file = cTree.createFile("html/html.html");
			LOG.debug("WROTE: "+file);
			HtmlHtml.wrapAndWriteAsHtml(htmlDiv, file);
			convertedSVGElement.appendChild(new SVGText(new Real2(x, y),cTree.getName()));
			y += deltaY;
		}
		return convertedSVGElement;
	}

	private List<File> getChildDirectoryList(File cProjectDir) throws IOException {
		FilePathGlobber globber = new FilePathGlobber();
		globber.setRegex(CorpusCache.DIR_REGEX)
		    .setUseDirectories(true)
		    .setLocation(cProjectDir.toString());
		List<File> cTreeFiles = globber.listFiles();
		return cTreeFiles;
	}

	public DocumentCacheList ensureDocumentCacheList() {
		if (documentCacheList == null) {
			documentCacheList = new DocumentCacheList();
		}
		return documentCacheList;
	}

	/** use with care.
	 * 
	 * @param cProject
	 */
	public void setCProject(CProject cProject) {
		this.cProject = cProject;
	}

	/** concatenates the documents as one huge HTML
	 * 
	 * @return
	 */
	public HtmlElement getOrCreateConvertedHtmlElement() {
		if (this.convertedHtmlElement == null) {
			this.convertedHtmlElement = new HtmlHtml();
			HtmlBody bodyAll = ((HtmlHtml)convertedHtmlElement).getOrCreateBody();
			for (DocumentCache documentCache : documentCacheList) {
				LOG.debug("hack this later");
//				HtmlElement element = documentCache.getOrCreateConvertedHtmlElement();
				
			}
		}
		return convertedHtmlElement;
	}

	public List<HtmlElement> getOrCreateHtmlElementList() {
		if (this.htmlElementList == null) {
			this.htmlElementList = new ArrayList<HtmlElement>();
			for (DocumentCache documentCache : documentCacheList) {
				LOG.debug("********************Document: "+documentCache.getTitle()+"******************");
				HtmlElement element = documentCache.getOrCreateConvertedHtmlElement();
				if (element == null) {
					LOG.warn("Null html");
					throw new RuntimeException("Null element");
				} else {
					htmlElementList.add(element);
				}
			}
		}
		return htmlElementList;
	}

	/** creates a new CProject and CorpusCache.
	 * 
	 * @param corpusDir // normally the same as a CProject.directory
	 * requires the directory to exist
	 * 
	 * @return null if no directory
	 */
	public static CorpusCache createCorpusCache(File corpusDir) {
		CMFileUtil.assertExistingDirectory(corpusDir);
		CProject cProject = new CProject(corpusDir);
		return new CorpusCache(cProject); 
	}

	public CProject getCProject() {
		return cProject;
	}

	public DocumentCacheList getOrCreateDocumentCacheList() {
		if (documentCacheList == null) {
			ensureDocumentCacheList();
			getOrCreateCTreeList();
			for (CTree cTree : cTreeList) {
				DocumentCache documentCache = new DocumentCache(cTree);
				documentCacheList.add(documentCache);
			}
		}
		return documentCacheList;
	}

	private CTreeList getOrCreateCTreeList() {
		if (cTreeList == null && cProject != null) {
			cTreeList = cProject.getOrCreateCTreeList();
		}
		return cTreeList;
	}

	public List<File> getFulltextPDFFiles() {
		return this.getOrCreateCTreeList().getFulltextPDFFiles();
	}

	public List<File> getFulltextHTMLFiles() {
		return this.getOrCreateCTreeList().getFulltextHtmlFiles();
	}

	public void convertPDF2SVG() {
		if (cProject != null) {
			cProject.convertPDF2SVG();
		}
	}

	public void convertPDF2HTML() {
		if (cProject != null) {
			cProject.convertPDF2HTML();
		}
	}

	public DocumentCache getDocumentCache(String name) {
		if (name != null) {
			for (DocumentCache documentCache : documentCacheList) {
				if (!name.equals(documentCache.getName())) {
					return documentCache;
				}
			}
		}
		return null;
	}

}
