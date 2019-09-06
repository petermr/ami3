package org.contentmine.cproject.files;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.args.log.AbstractLogElement;
import org.contentmine.cproject.args.log.CMineLog;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.metadata.MetadataReader;
import org.contentmine.cproject.metadata.quickscrape.QuickscrapeMD;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.XMLUtils;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.layout.SuperPixelArrayManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.contentmine.image.ImageUtil;
import org.contentmine.pdf2svg2.PDFDocumentProcessor;
import org.contentmine.pdf2svg2.PageIncluder;
import org.contentmine.svg2xml.pdf.SVGDocumentProcessor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import nu.xom.Document;
import nu.xom.Element;


/** collection of files within the ContentMine system.
 * 
 * The structure of scholarly articles often requires many constituent articles. For example an article may 
 * have a PDF, an HTML abstract, several GIFs for images, some tables in HTML, some DOCX files, CIFs for 
 * crystallography, etc.. These all need keeping together...

Note that the Catalog (from CottageLabs) primarily holds metadata. [It's possible to hold some of the HTML 
content, but it soon starts to degrade performance]. We therefore have metadata in the Catalog and 
contentFiles on disk. These files and Open and can, in principle, be used independently of the Catalog.

I am designing a "CTree" which passes the bundle down the pipeline. This should be independent of what 
language [Python , JS, Java...] is used to create or read them. We believe that a normal filing system 
is satisfactory (at least at present while we develop the technology).

A typical pass for one DOI (e.g. journal.pone.0115884 ) through the pipeline (mandatory files 
are marked *, optional ?) might look like:

DOI --> Quickscrape -->

create directory  contentmine/some/where/journal.pone.0115884/. It may contain

results.json * // a listing of scraped files

fulltext.xml ? // publishers XML
fulltext.pdf ? // publishers PDF
fulltext.html ? // raw HTML
fulltext.pdf.txt ? // raw text from pdf
provisional.pdf ? // provisional PDF (often disappears)

foo12345.docx ? // data files numbered by publisher/author
bar54321.docx ?
ah1234.cif ? // crystallographic data
pqr987.cml ? // chemistry file
mmm.csv ? // table
pic5656.png ? // images
pic5657.gif ? // image
suppdata.pdf ? // supplemental data

and more

only results.json is mandatory. However there will normally be at least one fulltext.* file and probably at least one *.html file 
(as the landing page must be in HTML). Since quickscrape can extract data without fulltext it might also be deployed against a site with data files.

There may be some redundancy - *.xml may be transformable into *.html and *.pdf into *.html. The PDF may also contain the same images as some exposed *.png.

==================

This container (directory) is then massed to Norma. Norma will normalize as much information as possible, and we can expect this to continue to develop. This includes:
* conversion to Unicode (XML, HTML, and most "text" files)
* normalization of characters (e.g. Angstrom -> Aring, smart quotes => "", superscript "o" to degrees, etc.)
* creating well-formed HTML (often very hard)
* converting PDF to SVG (very empirical and heuristic)
* converting SVG to running text.
* building primitives (circles, squares, from the raw graphics)
* building graphics objects (arrows, textboxes, flowcharts) from the primitives
* building text from SVG

etc...

This often creates a lot of temporary files, which may be usefully cached for a period. We may create a subdirectory ./svg with intermediate pages, or extracted SVGs. These will be recorded in results.json, which will act as metadata for the files and subdirectories.

Norma will create ./svg/*.svg from PDF (using PDFBox and PDF2SVG), then fulltext.pdf.xhtml (heuristically created XHTML).  Norma will also create wellformed fulltext.html.xhtml from raw fulltext.html or from fulltext.xml.xhtml from fulltext.xml.

In the future Norma will also convert MS-formats such as DOCX and PPT using Apach POI.

Norma will then structure any flat structures into structured XHTML using grouping rules such as in XSLT2.

At this stage we shall have structured XHTML files ("scholarly HTML") with linked images and tables and supplemental data.  We'll update results.json

========================

AMI can further index or transform the ScholarlyHTML and associated files. An AMI plugin (e.g. AMI-species) will produce species.results.xml - a file with the named species in textual context. Similar outputs come from sequence, or other tagging (geotagging).

The main community development will come from regexes. For example we have
regex.crystal.results.xml, regex.farm.results.xml, regex.clinical_trials.results.xml, etc.

The results file include the regexes used and other metadata (more needed!). Again we can update results.json. We may also wish to delete temporary files such as the *.svg in PDF2SVG....

 * 
 * @author pm286
 *
 */
public class CTree extends CContainer implements Comparable<CTree> {



	private static final String IMAGEDOT = "image.";

	enum TableFormat {
		ANNOT_PNG("table.annot.png"),
		ANNOT_SVG("table.annot.svg"),
		TABLE_ROW_HTML("tableRow.html"),
		TABLE_SVG("table.svg"),
		TABLE_SVG_CSV("table.svg.csv"),
		TABLE_SVG_HTML("table.svg.html"),
		TABLE_SVG__ANNOT_SVG("table.svg..annot.svg"), // note repeated _ and . this is a misprint I think
		TABLE_SVG_SVG_HTML("table.svg.svg.html"),
		TRIMSVG_PNG("trimsvg.png"),
		;
		private String filename;
		private String regex;
		private TableFormat(String filename) {
			this.filename = filename;
			this.regex = ".*/"+filename.replaceAll("\\.", "\\\\.");
		}
	};
	
	private static final Logger LOG = Logger.getLogger(CTree.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static Pattern DOI_PREFIX = Pattern.compile("(10\\.[0-9]{3,}([\\.][0-9]+)*).*");
//	public final static Pattern DOI_PREFIX = Pattern.compile("(10\\.[0-9]{3,}).*");
	public static final String DOT      = ".";
	public static final String MINUS    = "-";
	public static final String ESCAPED_MINUS    = "\\-";

	public static final String CIF      = "cif";
	public static final String CSV      = "csv";
	public static final String DOC      = "doc";
	public static final String DOCX     = "docx";
	public static final String EPUB     = "epub";
	public static final String GIF      = "gif";
	public static final String HOCR     = "hocr";
	public static final String HTML     = "html";
	public static final String JPG      = "jpg";
	public static final String JSON     = "json";
	public static final String PDF      = "pdf";
	public static final String PDF_TXT  = "pdf.txt";
	public static final String PNG      = "png";
	public static final String PPT      = "ppt";
	public static final String PPTX     = "pptx";
	public static final String SVG      = "svg";
//	public static final String DOT_SVG = ".svg";
	public static final String TEI      = "tei";
	public static final String TEX      = "tex";
	public static final String TIF      = "tif";
	public static final String TSV      = "tsv";
	public static final String TXT      = "txt";
	public static final String TXT_HTML = "txt.html";
	public static final String XHTML    = "xhtml";
	public static final String XLS      = "xls";
	public static final String XLSX     = "xlsx";
	public static final String XML      = "xml";

	public static final String XSL      = "xsl";

	public static final String ABSTRACT     = "abstract";
	public static final String BODY         = "body";
	public static final String BACK         = "back";
	public static final String CROSSREF     = "crossref";
	public static final String EMPTY        = "empty";
	public static final String EUPMC_RESULT = "eupmc_result";
	public static final String FRONT        = "front";
	public static final String FULLTEXT     = "fulltext";
	public static final String IMAGE        = "image";
	public static final String METHOD       = "method";
	public static final String LOG1         = "log";
	public static final String PAGE         = "page";
	public static final String PAGES        = "pages";
	public static final String RESULT       = "result";
	public static final String RESULTS      = "results";
	public static final String SCHOLARLY    = "scholarly";
	public static final String DERIVED      = "derived/";
	
//	public static final String ABSTRACT_HTML      = ABSTRACT+DOT+HTML;
	public static final String EMPTY_XML          = EMPTY+DOT+XML;
	public static final String EUPMC_RESULT_JSON  = EUPMC_RESULT+DOT+JSON;
	public static final String FULLTEXT_DOCX      = FULLTEXT+DOT+DOCX;
	public static final String FULLTEXT_HTML      = FULLTEXT+DOT+HTML;
	public static final String FULLTEXT_PAGE      = FULLTEXT+MINUS+PAGE;
	public static final String FULLTEXT_PAGE_REGEX = FULLTEXT+ESCAPED_MINUS+PAGE;
	public static final String FULLTEXT_PDF       = FULLTEXT+DOT+PDF;
	public static final String FULLTEXT_PDF_HTML  = FULLTEXT+DOT+PDF+DOT+HTML;
	public static final String FULLTEXT_PDF_PNG   = FULLTEXT+DOT+PDF+DOT+PNG;
	public static final String FULLTEXT_PDF_SVG   = FULLTEXT+DOT+PDF+DOT+SVG;
	public static final String FULLTEXT_PDF_TXT   = FULLTEXT+DOT+PDF+DOT+TXT;
	public static final String FULLTEXT_TEI_HTML  = FULLTEXT+DOT+TEI+DOT+HTML;
	public static final String FULLTEXT_TEI_XML   = FULLTEXT+DOT+TEI+DOT+XML;
	public static final String FULLTEXT_TEX       = FULLTEXT+DOT+TEX;
	public static final String FULLTEXT_TEX_HTML  = FULLTEXT+DOT+TEX+DOT+HTML;
	public static final String FULLTEXT_TXT       = FULLTEXT+DOT+TXT;
	public static final String FULLTEXT_TXT_HTML  = FULLTEXT+DOT+TXT+DOT+HTML;
	public static final String FULLTEXT_XHTML     = FULLTEXT+DOT+XHTML;
	public static final String FULLTEXT_XML       = FULLTEXT+DOT+XML;
	public static final String HOCR_SVG           = HOCR+DOT+SVG;
	public static final String LOGFILE            = LOG1+DOT+XML;
	public static final String PNG_HOCR_SVG       = PNG+DOT+HOCR+DOT+SVG;
	public static final String RESULTS_XML        = RESULTS+DOT+XML;
	public static final String RESULTS_HTML       = RESULTS+DOT+HTML;
	public static final String SCHOLARLY_HTML     = SCHOLARLY+DOT+HTML;
	
	public final static List<String> RESERVED_FILE_NAMES;
	static {
			RESERVED_FILE_NAMES = Arrays.asList(new String[] {
//					ABSTRACT_HTML,
					
					AbstractMetadata.Type.CROSSREF.getCTreeMDFilename(),
					AbstractMetadata.Type.EPMC.getCTreeMDFilename(),
					AbstractMetadata.Type.QUICKSCRAPE.getCTreeMDFilename(),
					QuickscrapeMD.CTREE_RESULT_JSON_OLD, // kludge 
					
					FULLTEXT_DOCX,
					FULLTEXT_HTML,
					FULLTEXT_PDF,
					FULLTEXT_PDF_TXT,
					FULLTEXT_TEX,
					FULLTEXT_TXT,
					FULLTEXT_XHTML,
					FULLTEXT_XML,
					LOGFILE,
					RESULTS_XML,
					SCHOLARLY_HTML
			});
	}
	/** directories must end with slash.
	 * 
	 */
	
	public static final String ABSTRACT_DIR      = "abstract/";
	public static final String BACK_DIR          = "back/";
	public static final String BODY_DIR          = "body/";
	// this is confusing. there are (a) explicit images and (b) images extracted from PDFs
//	public static final String IMAGE_DIR         = "image/";
	public static final String FRONT_DIR         = "front/";
	public static final String PDF_DIR           = "pdf/";
	public static final String PDF_IMAGES_DIR    = "pdfimages/";
	public static final String RAW_IMAGES_DIR    = "rawimages/";
	public static final String RESULTS_DIR       = "results/";
	public static final String SECTIONS_DIR      = "sections/";
	public static final String SVG_IMAGES_DIR    = "svgimages/"; // not sure where these come from
	public static final String SUPPLEMENTAL_DIR  = "supplement/";
	public static final String SVG_DIR           = "svg/";
	public static final String TABLE_DIR         = "table/";
	public static final String TABLES_DIR        = "tables/";

	public final static List<String> RESERVED_DIR_NAMES;
	static {
			RESERVED_DIR_NAMES = Arrays.asList(new String[] {
					ABSTRACT_DIR,
					BACK_DIR,
					BODY_DIR,
					FRONT_DIR,
					PDF_IMAGES_DIR,
					RAW_IMAGES_DIR,
					SECTIONS_DIR,
					SVG_IMAGES_DIR,
					PDF_DIR,
					RESULTS_DIR,
					SUPPLEMENTAL_DIR,
					SVG_DIR,
					TABLE_DIR,  // probably Deprecated
					TABLES_DIR, // maybe duplicate
			});
	}
	
	
	public final static boolean isImageSuffix(String suffix) {
		return (
            GIF.equals(suffix) ||
            JPG.equals(suffix) ||
            PNG.equals(suffix) ||
            TIF.equals(suffix)
				);
	}
	
	public final static boolean isSupplementalSuffix(String suffix) {
		return (
            CIF.equals(suffix) ||
            CSV.equals(suffix) ||
            DOC.equals(suffix) ||
            DOCX.equals(suffix) ||
            PPT.equals(suffix) ||
            PPTX.equals(suffix) ||
            TEX.equals(suffix) ||
            XLS.equals(suffix) ||
            XLSX.equals(suffix)
				);
	}
	
	public final static boolean isSVG(String suffix) {
		return (
            SVG.equals(suffix)
				);
	}

	public final static Map<String, String> RESERVED_FILES_BY_EXTENSION = new HashMap<String, String>();
	private static final String RESULTS_DIRECTORY_NAME = "results";
	static {
		RESERVED_FILES_BY_EXTENSION.put(DOCX, FULLTEXT_DOCX);
		RESERVED_FILES_BY_EXTENSION.put(HTML, FULLTEXT_HTML);
		RESERVED_FILES_BY_EXTENSION.put(PDF, FULLTEXT_PDF);
		RESERVED_FILES_BY_EXTENSION.put(PDF_TXT, FULLTEXT_PDF_TXT);
		RESERVED_FILES_BY_EXTENSION.put(XML, FULLTEXT_XML);
	}
	
	public final static String EXPECTED = "expected";
	public final static String IMAGES = "images";
	public final static String SUPP_DATA = "suppData";
	public final static String TABLES = "tables";
	
	public final static Pattern FULLTEXT_STAR = Pattern.compile("fulltext.*");
	// messy
	public final static Pattern STAR_RESULT_JSON = Pattern.compile(".*results?\\.json");
	
	protected static final String[] ALLOWED_FILE_NAMES = new String[] {
		LOG_XML,
		MANIFEST_XML,
		SCHOLARLY_HTML,
		AbstractMetadata.Type.CROSSREF.getCTreeMDFilename(),
		AbstractMetadata.Type.EPMC.getCTreeMDFilename(),
		AbstractMetadata.Type.QUICKSCRAPE.getCTreeMDFilename(),
	};
	
	protected static final Pattern[] ALLOWED_FILE_PATTERNS = new Pattern[] {
		FULLTEXT_STAR,
		STAR_RESULT_JSON,
	};
	
	protected static final String[] ALLOWED_DIR_NAMES = new String[] {
		EXPECTED,
		IMAGES,
		RESULTS,
		SUPP_DATA,
		SVG,
		TABLES,
	};
	
	
	
	protected static final Pattern[] ALLOWED_DIR_PATTERNS = new Pattern[] {
	};
	
	
	public static boolean isReservedFilename(String name) {
		return RESERVED_FILE_NAMES.contains(name);
	}

	/** traps names such as "image/foo1.png".
	 * 
	 * @param name
	 * @return true if one "/" and first compenet is reserved directory
	 */
	public static boolean hasReservedParentDirectory(String name) {
		String[] fileStrings = name.split("/");
		return fileStrings.length == 2 && RESERVED_DIR_NAMES.contains(fileStrings[0]+"/");
	}
	
	public static boolean isReservedDirectory(String name) {
		if (!name.endsWith("/")) name += "/";
		return RESERVED_DIR_NAMES.contains(name);
	}
	
	private static final String TABLE_DIR_PREFIX = "table";
	private static final String TABLE_SERIAL = TABLE_DIR_PREFIX + "\\d+";
	public static final String TABLE_D = ".*/" + TABLE_SERIAL;

	
	private List<File> reservedFileList;
	private List<File> nonReservedFileList;
	private List<File> reservedDirList;
	private List<File> nonReservedDirList;
	private DefaultArgProcessor argProcessor;
	private ContentProcessor contentProcessor;
	private HtmlElement htmlElement;
	private List<Element> sectionElementList;
	private CProject cProject;
	private XMLSnippets snippets;
	private SnippetsTree snippetsTree;
	private CTreeFiles cTreeFiles;
	private ProjectFilesTree filesTree;
	private String title;
	private HtmlElement fulltextXHtml;
	private File fulltextHtmlFile;
	private File fulltextXHtmlFile;
	private boolean writeXHtml = true;
	private SuperPixelArrayManager spaManager;
	private DocumentCache documentCache;
//	private int deltaPages;
	private HtmlTagger htmlTagger;
	private File abstractDir;
	private File frontDir;
	private PDFDocumentProcessor pdfDocumentProcessor;
	private boolean forceMake = false;
	private TreeImageManager pdfImageManager;

	public CTree() {
		super();
		init();
	}
	
	protected void init() {
//		deltaPages = 200;
	}
	
	/** creates CTree object but does not alter filestore.
	 * 
	 * @param directory
	 */
	public CTree(File directory) {
		this();
		this.directory = directory;
	}

	public DocumentCache getOrCreateDocumentCache() {
		if (documentCache == null) {
			documentCache = new DocumentCache(this);
			documentCache.setCTree(this);
		}
		return documentCache;
	}
	
	/** ensures filestore matches a CTree structure.
	 * 
	 * @param directory
	 * @param delete
	 */
	public CTree(File directory, boolean delete) {
		this(directory);
		this.createDirectory(directory, delete);
	}
	
	public CTree(String filename) {
		this(new File(filename), false); 
	}

	public CTree(File directory, DocumentCache documentCache) {
		this(directory);
		this.documentCache = documentCache;
	}

	public void ensureReservedFilenames() {
		if (reservedFileList == null) {
			reservedFileList = new ArrayList<File>();
			nonReservedFileList = new ArrayList<File>();
			reservedDirList = new ArrayList<File>();
			nonReservedDirList = new ArrayList<File>();
			List<File> files = new ArrayList<File>(FileUtils.listFiles(directory, null, false));
			for (File file : files) {
				if (file.isDirectory()) {
					if (isReservedDirectory(FilenameUtils.getName(file.getAbsolutePath()))) {
						reservedDirList.add(file);
					} else {
						nonReservedDirList.add(file);
					}
				} else {
					if (isReservedFilename(FilenameUtils.getName(file.getAbsolutePath()))) {
						reservedFileList.add(file);
					} else {
						nonReservedFileList.add(file);
					}
				}
			}
		}
	}
	
	public List<File> getReservedDirectoryList() {
		ensureReservedFilenames();
		return reservedDirList;
	}
	
	public List<File> getReservedFileList() {
		ensureReservedFilenames();
		return reservedFileList;
	}
	
	public List<File> getNonReservedDirectoryList() {
		ensureReservedFilenames();
		return nonReservedDirList;
	}
	
	public List<File> getNonReservedFileList() {
		ensureReservedFilenames();
		return nonReservedFileList;
	}
	
	public static boolean containsNoReservedFilenames(File dir) {
		if (dir != null && dir.isDirectory()) {
			List<File> files = new ArrayList<File>(FileUtils.listFiles(dir, null, false));
			for (File file : files) {
				if (!file.isHidden()) {
					String name = FilenameUtils.getName(file.getAbsolutePath());
					if (isReservedFilename(name)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean containsNoReservedDirectories(File dir) {

		if (dir == null || !dir.isDirectory()) return false;
		File[] files = dir.listFiles();
		if (files == null) return true; // no files at all
		for (File file : files) {
			if (file.isDirectory()) {
				if (!file.isHidden()) {
					String name = FilenameUtils.getName(file.getAbsolutePath());
					if (isReservedDirectory(name)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean containsNoReservedFilenames() {
		return CTree.containsNoReservedFilenames(directory);
	}
	
	public boolean containsNoReservedDirectories() {
		return CTree.containsNoReservedDirectories(directory);
	}
	
	public void createDirectory(File dir, boolean delete) {
		this.directory = dir;
		CMFileUtil.createDirectory(dir, delete);
	}

	public void readDirectory(File dir) {
		this.directory = dir;
		Multimap<String, File> map = HashMultimap.create();
		
		requireDirectoryExists(dir);
		checkRequiredCMFiles();
	}

	/** checks that this CTree object is an existing directory.
	 * 
	 * @return true if getDirectory() refers to an existing directory
	 */
	public boolean hasExistingDirectory() {
		return isExistingDirectory(this.directory); 

	}

	private void checkRequiredCMFiles() {
		requireExistingNonEmptyFile(new File(directory, Type.QUICKSCRAPE.getCTreeMDFilename()));
	}

	public static boolean isExistingFile(File file) {
		boolean isExisting = false;
		if (file != null) {
			boolean exists = file.exists();
			boolean isFile = !file.isDirectory();
			isExisting = exists && isFile;
		}
		return isExisting;
	}

	public static boolean isExistingDirectory(File file) {
		return (file == null) ? false : file.exists() && file.isDirectory();
	}

	private void requireDirectoryExists(File dir) {
		if (dir == null) {
			throw new RuntimeException("Null directory");
		}
		if (!dir.exists()) {
			throw new RuntimeException("Directory: "+dir+" does not exist");
		}
		if (!dir.isDirectory()) {
			throw new RuntimeException("File: "+dir+" is not a directory");
		}
	}
	
	private void requireExistingNonEmptyFile(File file) {
		if (file == null) {
			throw new RuntimeException("Null file");
		}
		if (!file.exists()) {
			throw new RuntimeException("File: "+file+" does not exist");
		}
		if (file.isDirectory()) {
			throw new RuntimeException("File: "+file+" must not be a directory");
		}
		if (FileUtils.sizeOf(file) == 0) {
			throw new RuntimeException("File: "+file+" must not be empty");
		}
	}

	public boolean isFileOfExistingCTree(String fileType) {
		return directory != null && isExistingFile(new File(directory, fileType));
	}
	
	// FULLTEXT_XML =========================
	/** checks that this 
	 * 
	 * @return
	 */
	public boolean hasExistingFulltextXML() {
		return getExistingFulltextXML() != null;
	}

	/**
	 * checks that CTree exists and has child fulltext.xml
	 * 
	 * @param ctree
	 * @return true if ctree exists and has child fulltext.xml
	 */
	public static File getExistingFulltextXML(CTree ctree) {
		return (ctree == null) ? null : ctree.getExistingFulltextXML();
	}

	public static File getExistingFulltextXML(File ctreeFile) {
		return new CTree(ctreeFile).getExistingFulltextXML();
	}

	public File getExistingFulltextXML() {
		return getExistingReservedFile(FULLTEXT_XML);
	}
	
	// FULLTEXT_HTML =========================

	public boolean hasFulltextHTML() {
		return hasExistingDirectory() && isExistingFile(getExistingFulltextHTML());
	}
	
	public static File getExistingFulltextHTML(File ctreeFile) {
		return new CTree(ctreeFile).getExistingFulltextHTML();
	}

	public File getExistingFulltextHTML() {
		return getExistingReservedFile(FULLTEXT_HTML);
	}

	// FULLTEXT_XHTML =========================

	public boolean hasFulltextXHTML() {
		return hasExistingDirectory() && isExistingFile(getExistingFulltextXHTML());
	}
	
	/**
	 * checks that CTree exists and has child fulltext.html
	 * 
	 * @param ctree
	 * @return true if ctree exists and has child fulltext.html
	 */
	public static File getExistingFulltextXHTML(CTree ctree) {
		return (ctree == null) ? null : ctree.getExistingFulltextXHTML();
	}

	public static File getExistingFulltextXHTML(File ctreeFile) {
		return new CTree(ctreeFile).getExistingFulltextXHTML();
	}

	public File getExistingFulltextXHTML() {
		return getExistingReservedFile(FULLTEXT_XHTML);
	}

	// QUICKSCRAPE_MD =========================
	public boolean hasQuickscrapeMD() {
		return isExistingFile(new File(directory,  Type.QUICKSCRAPE.getCTreeMDFilename()));
	}
	
	/**
	 * checks that CTree exists and has child fulltext.xml
	 * 
	 * @param ctree
	 * @return true if ctree exists and has child metadata
	 */
	public static File getExistingQuickscrapeMD(CTree ctree) {
		return (ctree == null) ? null : ctree.getExistingQuickscrapeMD();
	}
	
	public static File getExistingQuickscrapeMD(File ctreeFile) {
		return new CTree(ctreeFile).getExistingQuickscrapeMD();
	}

	public File getExistingQuickscrapeMD() {
		return getExistingReservedFile( Type.QUICKSCRAPE.getCTreeMDFilename());
	}

	// SCHOLARLY_HTML =========================
	public boolean hasScholarlyHTML() {
		return getExistingScholarlyHTML() != null;
	}
	
	/**
	 * checks that CTree exists and has child scholarly.html
	 * 
	 * @param ctree
	 * @return true if ctree exists and has child scholarly.html
	 */
	public static File getExistingScholarlyHTML(CTree ctree) {
		return (ctree == null) ? null : ctree.getExistingScholarlyHTML();
	}
	
	public static File getExistingScholarlyHTML(File ctreeFile) {
		return new CTree(ctreeFile).getExistingScholarlyHTML();
	}

	public File getExistingScholarlyHTML() {
		return getExistingReservedFile(SCHOLARLY_HTML);
	}
	
	// FULLTEXT_PDF =========================
	public boolean hasFulltextPDF() {
		return getExistingFulltextPDF() != null;
	}
	
	public static File getExistingFulltextPDF(File ctreeFile) {
		return new CTree(ctreeFile).getExistingFulltextPDF();
	}

	public File getExistingFulltextPDF() {
		return getExistingReservedFile(FULLTEXT_PDF);
	}

	// FULLTEXT_PDFTXT =========================
	public boolean hasFulltextPDFTXT() {
		return getExistingFulltextPDFTXT() != null;
	}

	public static File getExistingFulltextPDFTXT(File ctreeFile) {
		return new CTree(ctreeFile).getExistingFulltextPDFTXT();
	}

	public File getExistingFulltextPDFTXT() {
		return getExistingReservedFile(FULLTEXT_PDF_TXT);
	}

	// FULLTEXT_DOCX =========================
	
	public boolean hasFulltextDOCX() {
		return getExistingFulltextDOCX() != null;
	}
	
	public static File getExistingFulltextDOCX(File ctreeFile) {
		return new CTree(ctreeFile).getExistingFulltextDOCX();
	}

	public File getExistingFulltextDOCX() {
		return getExistingReservedFile(FULLTEXT_DOCX);
	}

	// RESULTS =========================
	public boolean hasResultsDir() {
		return getExistingResultsDir() != null;
	}
	
	/**
	 */
	public static File getExistingResultsDir(CTree ctree) {
		return (ctree == null) ? null : ctree.getExistingResultsDir();
	}
	
	public static File getExistingResultsDir(File ctreeFile) {
		return new CTree(ctreeFile).getExistingResultsDir();
	}

	public File getExistingResultsDir() {
		return getExistingReservedFile(RESULTS_DIR);
	}

	// IMAGES =========================
	public boolean hasImageDir() {
		return getExistingImageDir() != null;
	}
	
	/**
	 */
	public static File getExistingRawImagesDir(CTree ctree) {
		return (ctree == null) ? null : ctree.getExistingImageDir();
	}
	
	public static File getExistingRawImagesDir(File ctreeFile) {
		return new CTree(ctreeFile).getExistingImageDir();
	}

	/** uses rawimage/ child directory*/
	public File getExistingImageDir() {
		return getExistingReservedDirectory(RAW_IMAGES_DIR, false);
	}

	// PDF_IMAGES =========================

	public File getExistingPDFImagesDir() {
		return getExistingReservedDirectory(PDF_IMAGES_DIR, false);
	}

	public File getOrCreateExistingImageDir() {
		return getExistingReservedDirectory(RAW_IMAGES_DIR, true);
	}

	public File getExistingImageFile(String filename) {
		File imageFile = null;
		File imageDir = getExistingImageDir();
		if (imageDir != null) {
			imageFile = new File(imageDir, filename);
		}
		return isExistingFile(imageFile) ? imageFile : null;
	}

	// SECTIONS =========================

	public File getExistingSectionsDir() {
		return getExistingReservedDirectory(SECTIONS_DIR, false);
	}

	public File getOrCreateExistingSectionsDir() {
		return getExistingReservedDirectory(SECTIONS_DIR, true);
	}

	public File getExistingSectionsFile(String filename) {
		File imageFile = null;
		File imageDir = getExistingImageDir();
		if (imageDir != null) {
			imageFile = new File(imageDir, filename);
		}
		return isExistingFile(imageFile) ? imageFile : null;
	}

	// SVG ===========================
	
	public File getExistingSVGDir() {
		return getExistingReservedDirectory(SVG_DIR, false);
	}

	public File getOrCreateExistingSVGDir() {
		return getExistingReservedDirectory(SVG_DIR, true);
	}

	/** currently in <cTree>/svg/fulltext-page.?123.svg
	 */
	public List<File> getExistingSVGFileList() {
		File svgDir = getExistingSVGDir();
		List<File> files = new ArrayList<File>();
		if (svgDir != null) {
			List<File> svgFiles0 = Arrays.asList(svgDir.listFiles());
			CMineGlobber globber = new CMineGlobber().setRegex(".*/" + CTree.FULLTEXT_PAGE + "\\.?" + "\\d+.svg").setLocation(svgDir);
			files = globber.listFiles();
		}
		return files;
	}

	// SVG IMAGES===========================
	// this is a child directory of svg (svg/images)
	public File getExistingSVGImagesDir() {
		File svgDir = getExistingReservedDirectory(SVG_DIR, false);
		DebugPrint.debugPrint(Level.DEBUG, "svgDir exists");
		File svgImageDir = null;
		if (svgDir != null) {
			svgImageDir = new File(svgDir, SVG_IMAGES_DIR);
			if (!svgImageDir.exists() || !svgImageDir.isDirectory()) {
				svgImageDir = null;
			}
		}
		return svgImageDir;
	}

	public List<File> getExistingSVGImagesFileList() {
		File svgImagesDir = getExistingSVGImagesDir();
		List<File> files = new ArrayList<File>();
		if (svgImagesDir != null) {
			CMineGlobber globber = new CMineGlobber().setRegex(".*/fulltext\\.p\\d+\\.i\\d+\\.(png|jpg|tiff?)").setLocation(svgImagesDir);
			files = globber.listFiles();
		}
		return files;
	}

	// SVG TABLES===========================
	// this is a child directory of svg (svg/images)
	public File getExistingSVGTablesDir() {
		File tablesDir = getExistingReservedDirectory(TABLES, false);
		if (tablesDir != null) {
		}
		return tablesDir;
	}

	/** returns sorted list of SVGTables.
	 * 
	 * @return
	 */
	public List<File> getExistingSortedSVGTablesDirList() {
		File svgTablesDir = getExistingSVGTablesDir();
		List<File> files = new ArrayList<File>();
		if (svgTablesDir != null) {
			CMineGlobber globber = new CMineGlobber().setRegex(TABLE_D).setLocation(svgTablesDir).setUseDirectories(true).setUseFiles(false);
			globber.setDebug(false);
			files = globber.listFiles();
			removeNonDirectories(files);
		}
		return  CMFileUtil.sortUniqueFilesByEmbeddedIntegers(files);
	}

	/**
	 * gets table file of form table12
	 * 
	 * @param serial serial from 1
	 * @return null if not found
	 */
	public File getExistingTableDirSerial(int serial) {
		List<File> files = getExistingSortedSVGTablesDirList();
		for (File file : files) {
			if (file.getName().equals(TABLE_DIR_PREFIX+serial)) {
				return file;
			}
		}
		return null;
	}
	
	public List<File> getExistingTableFiles(int serial, TableFormat format) {
		List<File> tableFiles = new ArrayList<File>();
		File tablesDir = this.getExistingTableDirSerial(serial);
		if (tablesDir != null) {
			CMineGlobber globber = new CMineGlobber();
			globber.setRegex(format.regex).setLocation(tablesDir);
			globber.setDebug(false);
			tableFiles = globber.listFiles();
		}
		return tableFiles;
	}
	
	public File getExistingTableFile(int serial, TableFormat format) {
		List<File> tableFiles = getExistingTableFiles(serial, format);
		return tableFiles.size() == 1 ? tableFiles.get(0) : null;
	}
	

	private static void removeNonDirectories(List<File> files) {
		for (int i = files.size() - 1; i >= 0; i--) {
			if (!files.get(i).isDirectory()) {
				files.remove(i);
			}
		}
	}


// ---
	/** checks that this 
	 * 
	 * @return
	 */
	public boolean hasExistingLogfile() {
		return getExistingLogfile() != null;
	}
	
	/**
	 * checks that CTree exists and has child fulltext.xml
	 * 
	 * @param ctree
	 * @return true if ctree exists and has child fulltext.xml
	 */
	public static File getExistingLogfile(CTree ctree) {
		return (ctree == null) ? null : ctree.getExistingLogfile();
	}
	
	public static File getExistingLogfile(File ctreeFile) {
		return new CTree(ctreeFile).getExistingLogfile();
	}
	
	public File getExistingLogfile() {
		return getExistingReservedFile(LOGFILE);
	}


	// ---
	public File getReservedFile(String reservedName) {
		File file = (!isReservedFilename(reservedName) || directory == null) ? null : new File(directory, reservedName);
		return file;
	}

	public File getReservedDirectory(String reservedName) {
		File file = (!isReservedDirectory(reservedName) || directory == null) ? null : new File(directory, reservedName);
		return file;
	}

	public File getExistingReservedFile(String reservedName) {
		File file = getReservedFile(reservedName);
		file = file == null || !isExistingFile(file) ? null : file;
		return file;
	}

	public File getExistingReservedDirectory(String reservedName, boolean forceCreate) {
		File file = getReservedDirectory(reservedName);
		if (file != null) {
			boolean exists = isExistingDirectory(file);
			if (!exists) {
				if (forceCreate) {
					file.mkdirs();
				} else {
					file = null;
				}
			}
		}
		return file;
	}
	
	public File getExistingFileWithReservedParentDirectory(String inputName) {
		File file = null;
		if (CTree.hasReservedParentDirectory(inputName)) {
			file = new File(directory, inputName);
		}
		return file;
	}
	

	@Override
	public String toString() {
		ensureReservedFilenames();
		StringBuilder sb = new StringBuilder();
		sb.append("dir: "+directory+"\n");
		for (File file : getReservedFileList()) {
			sb.append(file.toString()+"\n");
		}
		return sb.toString();
	}

	public void writeFile(String content, String filename) {
		if (filename == null) {
			LOG.error("Null output file");
			return;
		}
		File file = new File(directory, filename);
		if (file.exists()) {
			// this is allowable
			LOG.trace("file already exists (overwritten) "+file);
		}
		if (content != null) {
			try {
				LOG.trace("writing file: "+file);
				FileUtils.write(file, content, Charset.forName("UTF-8"));
			} catch (IOException e) {
				throw new RuntimeException("Cannot write file: ", e);
			}
		} else {
			LOG.trace("Null content");
		}
	}

	public File getDirectory() {
		return directory;
	}

	/** gets name of directory.
	 * 
	 * @return null if no directory
	 */
	public String getName() {
		return directory == null ? null : directory.getName();
	}

	public List<File> listFiles(boolean recursive) {
		List<File> files = new ArrayList<File>(FileUtils.listFiles(directory, null, recursive));
		return files;
	}

	public static String getCTreeReservedFilenameForExtension(String name) {
		String filename = null;
		String extension = FilenameUtils.getExtension(name);
		if (extension.equals("")) {
			// no type
		} else if (PDF.equals(extension)) {
			filename = FULLTEXT_PDF;
		} else if (isImageSuffix(extension)) {
			filename = RAW_IMAGES_DIR;
		} else if (isSupplementalSuffix(extension)) {
			filename = SUPPLEMENTAL_DIR;
		} else if (SVG.equals(extension)) {
			filename = SVG_DIR;
		} else if (XML.equals(extension)) {
			filename = FULLTEXT_XML;
		} else if (HTML.equals(extension)) {
			filename = FULLTEXT_HTML;
		} else if (XHTML.equals(extension)) {
			filename = FULLTEXT_XHTML;
		}
		return filename;
	}

	
	public Element getMetadataElement() {
		Element metadata = new Element("cTree");
		metadata.appendChild(this.toString());
		return metadata;
	}

	public static boolean isNonEmptyNonReservedInputList(List<String> inputList) {
		if (inputList == null || inputList.size() != 1) return false;
		if (CTree.hasReservedParentDirectory(inputList.get(0))) return false;
		if (CTree.isReservedFilename(inputList.get(0))) return false;
		return true;
	}

	public void writeReservedFile(File originalFile, String reservedFilename, boolean delete) throws Exception {
		if (reservedFilename == null) {
			throw new RuntimeException("reservedFilename is null: for "+originalFile);
		}
		File reservedFile = this.getReservedFile(reservedFilename);
		if (reservedFile == null) {
			throw new RuntimeException("Cannot create/find CTree reserved file: "+reservedFilename);
		} else if (reservedFile.exists()) {
			if (delete) {
				FileUtils.forceDelete(reservedFile);
			} else {
				LOG.error("File exists ("+reservedFile.getAbsolutePath()+"), not overwritten");
				return;
			}
		}
		FileUtils.copyFile(originalFile, reservedFile);
	}
	
	public void copyTo(File destDir, boolean overwrite) throws IOException {
		if (destDir == null) {
			throw new RuntimeException("Null destination file in copyTo()");
		} else {
			boolean canWrite = true;
			if (destDir.exists()) {
				if (overwrite) {
					try {
						FileUtils.forceDelete(destDir);
					} catch (IOException e) {
						LOG.error("cannot delete: "+destDir);
						canWrite = false;
					}
				} else {
					LOG.error("Cannot overwrite :"+destDir);
					canWrite = false;
				}
			}
			if (canWrite) {
				FileUtils.copyDirectory(this.directory, destDir);
				if (!destDir.exists() || !destDir.isDirectory()) {
					throw new RuntimeException("failed to create directory: "+destDir);
				}
			}
		}
	}


	File getResultsDirectory() {
		return new File(getDirectory(), RESULTS_DIRECTORY_NAME);
	}

	File getRawImagesDirectory() {
		return new File(getDirectory(), RAW_IMAGES_DIR);
	}

	File getSectionsDirectory() {
		File directory = getDirectory();
		return new File(directory, SECTIONS_DIR);
	}


	public ResultsElement getResultsElement(String pluginName, String methodName) {
		File resultsDir = getExistingResultsDir();
		ResultsElement resultsElement = null;
		if (CTree.isExistingDirectory(resultsDir)) {
			File pluginDir = new File(resultsDir, pluginName);
			if (CTree.isExistingDirectory(pluginDir)) {
				File methodDir = new File(pluginDir, methodName);
				if (CTree.isExistingDirectory(methodDir)) {
					File resultsXML = new File(methodDir, CTree.RESULTS_XML);
					if (CTree.isExistingFile(resultsXML)) {
						Document resultsDoc = XMLUtil.parseQuietlyToDocument(resultsXML);
						resultsElement = ResultsElement.createResultsElement(resultsDoc.getRootElement());
					}
				}
			}
		}
		return resultsElement;
	}

	public void ensureContentProcessor(DefaultArgProcessor argProcessor) {
		if (this.contentProcessor == null) {
			this.ensureArgProcessor(argProcessor);
			contentProcessor = new ContentProcessor(this);
		}
	}

	private void ensureArgProcessor(DefaultArgProcessor argProcessor) {
		if (this.argProcessor == null) {
			this.argProcessor = argProcessor;
		}
	}

	public List<String> extractWordsFromScholarlyHtml() {
		ensureScholarlyHtmlElement();
		String value = htmlElement == null ? null : htmlElement.getValue();
		return value == null ? new ArrayList<String>() :  new ArrayList<String>(Arrays.asList(value.split("\\s+")));
	}

	public List<Element> extractSectionsFromScholarlyHtml(String xpath) {
		ensureScholarlyHtmlElement();
		sectionElementList = XMLUtil.getQueryElements(getHtmlElement(), xpath);
		return sectionElementList;
	}

	public HtmlElement ensureScholarlyHtmlElement() {
		if (htmlElement == null) {
			htmlElement = DefaultArgProcessor.getScholarlyHtmlElement(this);
			// <h2 class="citation-title">
			if (title == null) {
				extractTitle();
			}
		}
		return htmlElement;
	}

	private void extractTitle() {
		List<Element> titleList = XMLUtil.getQueryElements(htmlElement, ".//*[local-name()='h2' and @class='citation-title']");
		if (titleList.size() != 0) {
			title = titleList.get(0).getValue();
			LOG.trace("title: "+title);
		} else {
			LOG.trace("NO NO NO");
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public List<String> extractWordsFromPDFTXT() {
		String value = this.readFileQuietly(this.getExistingFulltextPDFTXT());
		return value == null ? new ArrayList<String>() :  new ArrayList<String>(Arrays.asList(value.trim().split("\\s+")));
	}

	private String readFileQuietly(File file) {
		try {
			return file == null ? null : FileUtils.readFileToString(file, Charset.forName("UTF-8"));
		} catch (IOException e) {
//			throw new RuntimeException("Cannot read file: "+pdfTxt, e);
			return null;
		}
	}
	
	public String readFulltextTex() {
		return readFileQuietly(getReservedFile(FULLTEXT_TEX));
	}
	
	// ======= delegates to ContentProcessor ========
	public void putInContentProcessor(String name, ResultsElement resultsElement) {
		ensureContentProcessor(argProcessor);
		contentProcessor.put(name, resultsElement);
	}

	public void clearResultsElementList() {
		ensureContentProcessor(argProcessor);
		contentProcessor.clearResultsElementList();
	}

	public void add(ResultsElement resultsElement) {
		ensureContentProcessor(argProcessor);
		contentProcessor.addResultsElement(resultsElement);
	}

	public ContentProcessor getOrCreateContentProcessor() {
		if (contentProcessor == null) {
			contentProcessor = new ContentProcessor(this);
		}
		return contentProcessor;
	}

	public HtmlElement getHtmlElement() {
		return htmlElement;
	}

	public void readFulltextPDF(File file) {
		
		try {
			FileUtils.copyFile(file, this.getReservedFile(FULLTEXT_PDF));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read PDF", e);
		}
	}

	public File createLogfile() {
		return null;
	}

	public AbstractLogElement getOrCreateCTreeLog(DefaultArgProcessor argProcessor, String logfileName) {
		AbstractLogElement cTreeLog = null;
		if (CTree.LOGFILE.equals(logfileName)) {
			File file = CTree.getExistingLogfile(this);
			if (file == null) {
				file = this.getReservedFile(CTree.LOGFILE);
			}
			LOG.trace("file "+file);
			cTreeLog = new CMineLog(file);
		}
		return cTreeLog;
	}

	@Override
	protected CManifest createManifest() {
		manifest = new CTreeManifest(this);
		return manifest;
	}
	
	public void setProject(CProject cProject) {
		this.cProject = cProject;
	}
	
	@Override
	public void updateManifest() {
		this.getOrCreateManifest();
	}

	@Override
	protected void calculateFileAndCTreeLists() {
		for (File directory : allChildDirectoryList) {
			if (false) {
			} else if (
				isAllowedFile(directory, ALLOWED_DIR_PATTERNS) ||
				isAllowedFileName(directory, ALLOWED_DIR_NAMES)) {
				allowedChildDirectoryList.add(directory);
			} else {
				unknownChildDirectoryList.add(directory);
			}
		}
	}

	@Override
	protected void getAllowedAndUnknownFiles() {
		for (File file : allChildFileList) {
			if (false) {
			} else if (
				isAllowedFile(file, ALLOWED_FILE_PATTERNS) ||
				isAllowedFileName(file, ALLOWED_FILE_NAMES)) {
				allowedChildFileList.add(file);
			} else {
				unknownChildFileList.add(file);
				LOG.trace("Unknown file in project");
			}
		}
	}

	public List<File> getResultsXMLFileList() {
		List<File> resultsXMLFileList = new ArrayList<File>();
		File resultsDir = getResultsDirectory();
		if (resultsDir != null && resultsDir.isDirectory()) {
			Collection<File> collection = FileUtils.listFiles(resultsDir, new String[]{XML}, true);
			Iterator<File> fileIterator = collection.iterator();
			while (fileIterator.hasNext()) {
				File f = fileIterator.next();
				if (f.getName().equals(RESULTS_XML)) {
					resultsXMLFileList.add(f);
				}
			}
		}
		return resultsXMLFileList;
	}

	public CTreeFiles extractCTreeFiles(String glob) {
		List<File> files = CMineGlobber.listGlobbedFilesQuietly(this.getDirectory(), glob);
		this.cTreeFiles = new CTreeFiles(this, files);
		return cTreeFiles;
	}

	
	public SnippetsTree extractXPathSnippetsTree(String glob, String xpath) {
		snippetsTree = new SnippetsTree();
		extractCTreeFiles(glob);
		if (xpath != null) {
			for (File file : cTreeFiles) {
				XMLSnippets snippets = extractXMLSnippets(xpath, file);
				if (snippets.size() > 0) {
					snippets.addFile(file);
					snippetsTree.add(snippets);
				}
			}
		}
		return snippetsTree;
	}

	public XMLSnippets extractXMLSnippets(String xpath, File file) {
		if (xpath == null) {
			throw new RuntimeException("Null xpath");
		}
		Document doc = XMLUtils.parseWithoutDTD(file);
		List<Element> elementList = XMLUtil.getQueryElements(doc, xpath);
		snippets = new XMLSnippets(elementList, file);
		extractTitle();
		if (title != null) {
			snippets.setTitle(title);
		} else {
			LOG.trace("NO TITLE!!!!!");
		}
		return snippets;
	}
	
	public void setCTreeFiles(CTreeFiles cTreeFiles) {
		this.cTreeFiles = cTreeFiles;
	}
	
	public CTreeFiles getCTreeFiles() {
		return cTreeFiles;
	}

	public SnippetsTree getSnippetsTree() {
		return snippetsTree;
	}

	public void setSnippetsTree(SnippetsTree snippetsTree) {
		this.snippetsTree = snippetsTree;
	}

	public boolean matches(CTreeExplorer explorer) {
		String filename = explorer.getFilename();
		if (this.getExistingReservedFile(filename) != null) {
			return true;
		}
		return false;
	}

	/** renames directory using CMineUtil.normalizeDOIBasedFilename(name).
	 * 
	 * This 
	 * 
	 */
	public void normalizeDOIBasedDirectory() {
		if (directory != null) {
			File newDirectory = CMineUtil.normalizeDOIBasedFile(directory);
			if (!newDirectory.equals(directory)) {
				boolean renamed = directory.renameTo(newDirectory);
				if (!renamed) {
					boolean error = false;
					if (newDirectory.exists()) {
						File[] files = directory.listFiles();
						for (File file : files) {
							try {
								FileUtils.copyFile(file, new File(newDirectory, file.getName()));
							} catch (IOException e) {
								LOG.error("Couldn't copy: file to "+newDirectory, e);
								error = true;
							}
						}
					}
					if (error) {
						LOG.warn("Cannot rename file: "+directory);
					} else {
						try {
							FileUtils.deleteDirectory(directory);
//							LOG.info("Deleted moved directory: "+directory.getAbsolutePath());
						} catch (IOException e) {
							throw new RuntimeException("Cannot delete moved directory: "+directory.getAbsolutePath());
						}
					}
				} else {
					directory = newDirectory;
				}
			}
		}
	}

	public AbstractMetadata getOrCreateMetadata(AbstractMetadata.Type type) {
		AbstractMetadata metadata = null;
		if (type != null) {
			metadata = AbstractMetadata.getCTreeMetadata(this, type);
		}
		return metadata;
	}

	// HTML stuff - maybe create helper class?
	public void convertHtmlFileToXHtml() {
		this.fulltextHtmlFile = getOrCreateFulltextHtmlFile();
		if (this.fulltextHtmlFile != null) {
			long size = FileUtils.sizeOf(this.fulltextHtmlFile);
			if (size == 0) {
				fulltextXHtml = CMineUtil.createEmptyHTMLWthComment("zero bytes");
			} else {
				try {
					this.fulltextXHtml = new HtmlFactory().parse(this.fulltextHtmlFile);
				} catch (nu.xom.IllegalCharacterDataException icde) {
					fulltextXHtml = CMineUtil.createEmptyHTMLWthComment("Illegal content: "+icde.getMessage());
				} catch (Exception e) {
					fulltextXHtml = CMineUtil.createEmptyHTMLWthComment("Cannot tidy: "+e.getMessage());
				}
			}
			if (fulltextXHtml == null) {
				fulltextXHtml = new HtmlHtml();
			}
			if (this.fulltextXHtml != null && this.writeXHtml) {
				this.fulltextXHtmlFile = new File(this.fulltextHtmlFile.getParentFile(), CTree.FULLTEXT_XHTML);
				try {
					XMLUtil.debug(this.fulltextXHtml, this.fulltextXHtmlFile, 1);
					LOG.trace("wrote: "+this.fulltextXHtmlFile);
				} catch (IOException e) {
					throw new RuntimeException("Cannot write fulltextXHtml: "+this.fulltextXHtmlFile, e);
				}
			}
		}
	}

	public void parseXHtmlFile() {
		try {
			this.fulltextXHtml = new HtmlFactory().parse(this.fulltextXHtmlFile);
		} catch (Exception e) {
			LOG.error("Cannot parse XHtml file: "+this.fulltextXHtmlFile);
		}
	}

	public HtmlElement getOrCreateFulltextXHtml() {
		if (this.fulltextXHtml == null) {
			this.fulltextXHtmlFile = getOrCreateFulltextXHtmlFile();
			if (this.fulltextXHtmlFile != null) {
				parseXHtmlFile();
			} else {
				convertHtmlFileToXHtml();
			}
		}
		return this.fulltextXHtml;
	}

	public File getOrCreateFulltextHtmlFile() {
		if (this.fulltextHtmlFile == null) {
			this.fulltextHtmlFile = getExistingFulltextHTML();
		}
		return this.fulltextHtmlFile;
	}

	public File getOrCreateFulltextXHtmlFile() {
		if (this.fulltextXHtmlFile == null) {
			this.fulltextXHtmlFile = getExistingFulltextXHTML();
		}
		return this.fulltextXHtmlFile;
	}

	// '\b(10[.][0-9]{3,}(?:[.][0-9]+)*/.*\b';

	public String extractDOIPrefix() {
		String prefix = null;
		String dir = directory.getName();
		Matcher matcher = DOI_PREFIX.matcher(dir);
		if (matcher.matches()) {
			prefix = matcher.group(1);
		}
		return prefix;
	}

	public int compareTo(CTree cTree) {
		if (cTree == null || !(cTree instanceof CTree)) {
			return 0;
		}
		String thisDir = this.directory == null ? null : this.directory.getAbsolutePath();
		String cTreeDir = cTree.directory == null ? null : cTree.directory.getAbsolutePath();
		if (thisDir == null || cTreeDir == null) {
			return 0;
		}
		return thisDir.compareTo(cTreeDir);
	}

	/** actually creates file within CTree
	 * uses touch()
	 * 
	 * @param filename
	 */
	public File createFile(String filename) {
		File file = new File(directory, filename);
		try {
			FileUtils.touch(file);
		} catch (IOException e) {
			throw new RuntimeException("Cannot touch "+file, e);
		}
		return file;
	}

	/** writes CTree to "directory" using names of contained files.
	 * 
	 * @throws IOException
	 */
	public void write(File projectDirectory) throws IOException {
		if (projectDirectory != null) {
			File newDirectory = new File(projectDirectory, this.directory.getName());
			newDirectory.mkdirs();
			File[] files = this.directory.listFiles();
			if (files != null) {
				for (File file : files) {
					File newFile = new File(newDirectory, file.getName());
					FileUtils.copyFile(file, newFile);
				}
			}
		}
	}

	public void convertPDF2SVG() throws IOException {
		File pdfFile = getExistingFulltextPDF();
		if (pdfFile != null) {
			List<File> svgFiles = getExistingSVGFileList();
			getOrCreatePDFDocumentProcessor();
			// don't create if exist already
			if (svgFiles.size() == 0) {
			    pdfDocumentProcessor.readAndProcess(pdfFile);
				directory.mkdirs();
				if (cProject.getOrCreateProjectIO().isWriteSVGPages()) {
					pdfDocumentProcessor.writeSVGPages(directory);
				}
				// this is expensive
				if (cProject.getOrCreateProjectIO().isWriteRawImages()) {
					pdfDocumentProcessor.writePDFImages(directory);
				}
			}
		}
	}

	/** slightly messy. Use Caches?
	 * 
	 * @throws IOException
	 */
	public void convertPDF2HTML() throws IOException {
		File htmlFile = getExistingFulltextHTML();
		if (htmlFile == null) {
			documentCache.convertSVG2PageCacheList(); // ensures SVG files
			HtmlHtml fulltextHtml = documentCache.getConcatenatedHtml();
			//this is an interim kludge		
			htmlFile = new File(this.getDirectory(), FULLTEXT_HTML);
			LOG.trace("HT "+htmlFile);
			HtmlHtml.wrapAndWriteAsHtml(fulltextHtml, htmlFile);

		}
	}


	/** creates numbered page basenames.
	 * Examples "fulltext-page99
	 * @param page
	 * @return
	 */
	public static String createNumberedFullTextPageBasename(int page) {
		return CTree.FULLTEXT_PAGE+page;
	}
	
	
	public SuperPixelArrayManager createSuperPixelArrayManager() {
		spaManager = null;
		List<File> svgFiles = this.getExistingSVGFileList();
		if (svgFiles.size() > 0) {
			SVGElement page0 = SVGElement.readAndCreateSVG(svgFiles.get(0));
			Int2Range int2Range = new Int2Range(page0.getBoundingBox());
			spaManager = new SuperPixelArrayManager(int2Range);
			spaManager.setLeftPage(false);
			spaManager.setRightPage(true);
		}
			
		for (File svgPage : this.getExistingSVGFileList()) {
			spaManager.aggregatePixelArrays(svgPage);
		}
		return spaManager;
	}

	public void setDocumentCache(DocumentCache documentCache) {
		this.documentCache = documentCache;
	}

	public DocumentCache getDocumentCache() {
		return documentCache;
	}

	public List<File> getExistingSortedSVGFileList() {
		return  CMFileUtil.sortUniqueFilesByEmbeddedIntegers(this.getExistingSVGFileList());
	}

	public void processPDFTree() {
		int start = 0;
		File existingFulltextPDF = getExistingFulltextPDF();
		if (existingFulltextPDF == null) {
			DebugPrint.warnPrintln(debugLevel, "null PDF for: "+this.getName());
			return;
		}
		File svgDir = this.getExistingSVGDir();
		if (svgDir != null && !CMFileUtil.shouldMake(forceMake, svgDir, existingFulltextPDF)) {
//			DebugPrint.infoPrintln(debugLevel, "make is skipped: "+existingFulltextPDF);
			System.out.print("make skipped ");
			return;
		}
		PDDocument document = null;
		try {
			document = PDDocument.load(existingFulltextPDF);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load PDF "+existingFulltextPDF, e);
		}
		int pageCount = document.getNumberOfPages();
		int deltaPages = pdfDocumentProcessor.getMaxPages();
		System.out.print(" max pages: "+deltaPages);
		while (start < pageCount) {
			System.out.println(" " + start + " ");
			getOrCreatePDFDocumentProcessor();
		    try {
		    	PageIncluder pageIncluder = pdfDocumentProcessor.getOrCreatePageIncluder();
				pageIncluder.setZeroNumberedIncludePages(new IntRange(start, start + deltaPages - 1));
				System.out.print("pages "+pageIncluder.toString());
		    	pdfDocumentProcessor.readAndProcess(document);
		    	pdfDocumentProcessor.writeSVGPages(directory);
		    	pdfDocumentProcessor.writePDFImages(directory);
		    } catch (IOException ioe) {
		    	LOG.error("cannot read/process: " + this + "; "+ioe);
		    }
		    start += deltaPages;
		}
	}

	public void tidyImages() {
		ImageManager imageManager = new ImageManager(this);
		imageManager.tidyImages();
	}

	/** not fully integrated yet
	 * 
	 * 
	 * @throws IOException
	 */
	public void extractSVGAndPDFImages() throws IOException {
		PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
		documentProcessor.setCTree(this);
		documentProcessor.setMinimumImageBox(100, 100);
		File existingFulltextPDF = getExistingFulltextPDF();
		if (CMFileUtil.shouldMake(forceMake, documentProcessor.getOutputSVGDirectory(getDirectory()), existingFulltextPDF) ||
		    CMFileUtil.shouldMake(forceMake, documentProcessor.getOutputImagesDirectory(getDirectory()), existingFulltextPDF)) { 
			documentProcessor.readAndProcess(existingFulltextPDF);
			documentProcessor.writeSVGPages();
			documentProcessor.writePDFImages();
		} else {
			LOG.trace("skipped making SVG/Images for "+this.getName());
			System.err.print(" skip ");
		}
	}

	public File getImageFile(Int2 pageImage) {
		File imageDir = this.getExistingImageDir();
		return imageDir == null ? null : new File(imageDir, getImageName(pageImage));
 	}

	private String getImageName(Int2 pageImage) {
		return "page" + "." + pageImage.getX() + "." + pageImage.getY() + "." + CTree.PNG;
	}

	public File getPDFImageFile(Int2 pageImage) {
		File imageDir = this.getExistingPDFImagesDir();
		return imageDir == null ? null : new File(imageDir, getImageName(pageImage));
 	}

	public BufferedImage getPDFImage(Int2 pageImage) {
		File imageFile = this.getImageFile(pageImage);
		BufferedImage image = imageFile == null ? null : ImageUtil.readImage(imageFile);
		return image;
 	}

	/** creates top level sections/ directory 
	 * */
	public File makeSectionsDir(boolean delete) {
		File sectionsDirectory = getSectionsDirectory();
//		LOG.debug("sd: "+sectionsDirectory);
		CMFileUtil.createDirectory(sectionsDirectory, delete);
		return sectionsDirectory;
 	}

	/** returns sections/<sectionName> directory if it exists 
	 * */
	public File getExistingSectionDirectory(String sectionName) {
		File sectionDir = null;
		File existingSectionsDir = getExistingSectionsDir();
		if (sectionName != null) {
			if (existingSectionsDir != null) {
				File sectionFile = new File(existingSectionsDir, sectionName);
				sectionDir = sectionFile.exists() ? sectionFile : null;
			}
		}
		return sectionDir;
 	}

	/** creates  sections/sectionName directory 
	 * */
	public File makeSectionDir(String sectionName, boolean delete) {
		boolean delete0 = false;
		File sectionsDir = makeSectionsDir(delete0);
		File sectionDir = new File(sectionsDir, sectionName);
		CMFileUtil.createDirectory(sectionDir, delete);
		return sectionDir;
 	}


	public String createSVGFilename(Int2 pageImage) {
		return "page" + pageImage.getX() + "." + pageImage.getY() + "." + CTree.SVG;
	}

	void createAndWriteScholorlyHtml() {
		List<File> svgFiles = getExistingSVGFileList();
		SVGDocumentProcessor svgDocumentProcessor = new SVGDocumentProcessor();
		svgDocumentProcessor.readSVGFilesIntoSortedPageList(svgFiles);
		File htmlFile = new File(getDirectory(), CTree.SCHOLARLY_HTML);
		if (CMFileUtil.shouldMake(forceMake, htmlFile, getExistingSVGDir())) {
			LOG.debug("converting "+htmlFile);
			HtmlHtml html = svgDocumentProcessor.readAndConvertToHtml(svgFiles);
			try {
				XMLUtil.debug(html, htmlFile, 1);
			} catch (IOException e) {
				LOG.error("Cannot write html: " + htmlFile);
			}
		} else {
			LOG.trace("skipped "+htmlFile);
		}
	}

	/** this cleans a given file or directory.
	 * 
	 * @param filename relative to CTree directory
	 * @return
	 */
	public boolean cleanFileOrDirs(String filename) {
		File file = new File(this.getDirectory(), filename);
		boolean status = false;
		if (filename.endsWith("/") || file.isDirectory()) {
			status = FileUtils.deleteQuietly(file);
			if (status) DebugPrint.debugPrintln("deleted directory: "+file.getAbsolutePath());
		} else {
			status = FileUtils.deleteQuietly(file);
			if (status) DebugPrint.debugPrintln("deleted file: "+file.getAbsolutePath());
		}
		return status;
	}

	/** this cleans given files or directories by regex.
	 * 
	 * @param filename regex relative to CTree directory
	 * @return
	 */
	public boolean cleanRegex(String filename) {
		File file = new File(this.getDirectory(), filename);
		CMineGlobber globber = new CMineGlobber().setRegex(filename).setRecurse(true).setLocation(file);
		boolean status = true;
		List<File> files = globber.listFiles();
		for (File filex : files) {
			try {
				if (filex.isDirectory()) {
					FileUtils.deleteDirectory(filex);
				} else {
					FileUtils.forceDelete(filex);
				}
			} catch (IOException ioe) {
				status = false;
				DebugPrint.debugPrint("cannot delete: "+filex);
			}
		}
		return status;
	}

	public void writeFulltextHtmlToChildDirectory(HtmlDiv div, String directoryName) {
		File directory = getExistingReservedDirectory(directoryName, true);
		File file = new File(directory, FULLTEXT_HTML);
		try {
			XMLUtil.debug(div, file, 1);
		} catch (IOException e) {
			throw new RuntimeException("cannot write file "+file, e);
		}
	}
	
	public void writeFulltextHtmlToBodyChildDirectory(HtmlDiv div, String subDirectoryName) {
		File directory = getExistingReservedDirectory(BODY, true);
		File subDirectory = getExistingReservedDirectory(subDirectoryName, true);
		File file = new File(directory, FULLTEXT_HTML);
		try {
			XMLUtil.debug(div, file, 1);
		} catch (IOException e) {
			throw new RuntimeException("cannot write file "+file, e);
		}
	}
	
	public File getAbstractFile() {
		abstractDir = getExistingAbstractDir(false);
		return (abstractDir == null) ? null : new File(abstractDir, FULLTEXT_HTML);
	}

	public File getExistingAbstractDir(boolean forceCreate) {
		return getExistingReservedDirectory(ABSTRACT_DIR, forceCreate);
	}

	public File getBackDir() {
		frontDir = getExistingBackDir(false);
		return frontDir;
	}

	public File getExistingBackDir(boolean forceCreate) {
		return getExistingReservedDirectory(BACK_DIR, forceCreate);
	}

	public File getBodyDir() {
		frontDir = getExistingBodyDir(false);
		return frontDir;
	}

	public File getExistingBodyDir(boolean forceCreate) {
		return getExistingReservedDirectory(BODY_DIR, forceCreate);
	}

	public File getFrontDir() {
		frontDir = getExistingFrontDir(false);
		return frontDir;
	}

	public File getExistingFrontDir(boolean forceCreate) {
		return getExistingReservedDirectory(FRONT_DIR, forceCreate);
	}

	public void setPDFDocumentProcessor(PDFDocumentProcessor pdfDocumentProcessor) {
		this.pdfDocumentProcessor = pdfDocumentProcessor;
	}
	
	public PDFDocumentProcessor getOrCreatePDFDocumentProcessor() {
		if (pdfDocumentProcessor == null) {
			pdfDocumentProcessor = new PDFDocumentProcessor();
		}
		return pdfDocumentProcessor;
	}

	public boolean isForceMake() {
		return forceMake;
	}

	public void setForceMake(boolean forceMake) {
		this.forceMake = forceMake;
	}

	public File getOrCreateScholarlyHtmlFile() {
		return getDirectory() == null ? null : new File(getDirectory(), CTree.SCHOLARLY_HTML);
	}

	/** assumes a set of child directories, each with an image
	 * 
	 * @return
	 */
	public List<File> getPDFImagesImageDirectories() {
		List<File> imageDirectories = new ArrayList<File>();
		File dir = getExistingPDFImagesDir();
		if (dir != null) {
			List<File> images = CMineGlobber.listSortedChildFiles(dir, CTree.PNG);
			List<File> subDirectories = CMineGlobber.listSortedChildDirectories(dir);
			if (images.size() > 0 && subDirectories.size() == 0) {
				subDirectories = convertImagesToSubDirectories(images);
			}
			for (File subDir : subDirectories) {
				String name = subDir.getName();
				if (name.startsWith(IMAGEDOT)) {
					imageDirectories.add(subDir);
				}
			}
		}
		return imageDirectories;
	}


	private List<File> convertImagesToSubDirectories(List<File> imageFiles) {
		LOG.debug("converting to subdirectories: "+imageFiles.size());
		List<File> subDirectories = new ArrayList<File>();
		for (File imageFile : imageFiles) {
			String root = FilenameUtils.getBaseName(imageFile.toString());
			LOG.debug("converting: "+imageFile+" to "+root+"/");
			File subDir = new File(imageFile.getParentFile(), root);
			subDir.mkdirs();
			File imageFile1 = new File(subDir, "raw.png");
			try {
				FileUtils.moveFile(imageFile, imageFile1);
				LOG.debug("made "+imageFile1.getAbsolutePath());
				subDirectories.add(subDir);
			} catch (IOException e) {
				LOG.error("Cannot rename: "+imageFile+" to "+imageFile1+" "+ e);
			}
		}
		return subDirectories;
	}

	public TreeImageManager getOrCreatePDFImageManager() {
		if (pdfImageManager == null) {
			pdfImageManager = TreeImageManager.createTreeImageManager(this, this.getExistingPDFImagesDir());
		}
		return pdfImageManager;
	}

	
	public AbstractMetadata readMetadata(MetadataReader metadataReader, File metadataFile) {
		AbstractMetadata metadataEntry = null;
		if (metadataFile != null) {
			try {
				
				metadataEntry = metadataReader.readEntry(metadataFile);
			} catch (IOException e) {
				throw new RuntimeException("cannot parse metadata :"+metadataFile, e);
			}
		}
		return metadataEntry;
	}

	/** create a project for the tree. 
	 * if null, locates parent directory and create project from that
	 * no current check as to whether more than one project, so use with care.
	 * (ideally should check all siblings)
	 * i.e. if re-used carelessly might create different Project with different resources
	 * 
	 * will set this project
	 * @return
	 */
	public CProject getOrCreateProject() {
		if (this.cProject == null) {
			File file = getDirectory();
			if (file != null) {
				File parentFile = file.getParentFile();
				if (parentFile != null) {
					this.cProject = new CProject(parentFile);
				}
			}
		}
		return cProject;
	}

	public HtmlTagger getHtmlTagger() {
		return htmlTagger;
	}

	public void setHtmlTagger(HtmlTagger htmlTagger) {
		this.htmlTagger = htmlTagger;
		htmlTagger.setCTree(this);
	}

	public List<String> extractWords() {
		List<String> rawWords = null;
		if (this == null) {
			LOG.warn("null tree");
		} else if (hasScholarlyHTML()) {
			rawWords = extractWordsFromScholarlyHtml();
		} else if (hasFulltextPDFTXT()) {
			rawWords = extractWordsFromPDFTXT();
		} else {
			String msg = "No scholarlyHtml or PDFTXT: "+getDirectory();
			LOG.trace(msg);
		}
		LOG.trace("raw words " + (rawWords != null ? rawWords.size() : null));
		return rawWords;
	}
}
