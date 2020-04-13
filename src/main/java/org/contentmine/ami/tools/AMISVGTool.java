package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.LineCache;
import org.contentmine.graphics.svg.cache.PageCache;
import org.contentmine.graphics.svg.cache.PathCache;
import org.contentmine.graphics.svg.cache.RectCache;
import org.contentmine.graphics.svg.cache.TextCache;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Translates SVG from PDF into structured text and graphics
 * 
 * @author pm286
 *
 */
@Command(
name = "svg",
description = {
		"Takes raw SVG from PDF2SVG and converts into structured HTML and higher graphics primitives."
})
public class AMISVGTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMISVGTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
    public enum TidySVG {
	    emptypath,     // has no primitives
	    nomove,        // has no initial M command
	    nullmove,;     // M without further primitives

		public static TidySVG getTidySVG(String tidy) {
			for (TidySVG tidySvg : values()) {
				if (tidySvg.toString().equals(tidy)) {
					return tidySvg;
				}
			}
			return null;
		}
	};
	
    public enum CacheEnum {
//	    circle(CircleCache.class),
	    line(LineCache.class),
	    page(PageCache.class),
	    path(PathCache.class),
	    rect(RectCache.class),
	    text(TextCache.class),
	    ;
    	private Class<?> clazz;

		private CacheEnum(Class<?> clazz) {
    		this.clazz = clazz;
    	}
    	
		public static CacheEnum getCache(String cache) {
			for (CacheEnum cacheEnum: values()) {
				if (cacheEnum.toString().equals(cache)) {
					return cacheEnum;
				}
			}
			return null;
		}
	};
	
    @Option(names = {"--caches"},
    		arity = "1..*",
            description = "caches to process/create; values: ${COMPLETION-CANDIDATES}")
    private List<CacheEnum> cacheList = null;

    /** this should be a Mixin, with PDFTool
     * 
     */
    @Option(names = {"--pages"},
    		arity = "1..*",
            description = "pages to extract")
    private List<Integer> pageList = null;

    @Option(names = {"--regex"},
    		arity = "1..*",
            description = "regexes to search for in svg pages. format (integerWeight space regex)."
            		+ "If regex starts with uppercase (e.g. Hedge's) forces"
            		+ " case sensitivity , else case-insensitive")
    private List<String> regexList = null;

    @Option(names = {"--regexfile"},
    		arity = "1",
            description = "file to read (weight-regex) pairs from. May contain ${CM_ANCILLARY} variable")
    private String regexFilename = null;

    @Option(names = {"--tidysvg"},
    		arity = "1..*",
            description = "tidy SVG (Valid values: ${COMPLETION-CANDIDATES})")
    private List<TidySVG> tidyList;

    @Option(names = {"--vectorlog"},
    		arity = "1",
    		defaultValue = "vectors.log",
            description = "file to contain statistics on vectors (probably diagrams or tables)")
    private String vectorLog = null;

    @Option(names = {"--vectordir"},
    		arity = "1",
   	    	defaultValue = "vectors/",
    		description = "output pages with SVG vectors to <directory>")
    private String vectorDirname;

	@Option(names = {"--logfile"},
			description = "(A) log file for each tree/file/image analyzed. "
	)
	public String logfile;

    public static Pattern PAGE_EXTRACT = Pattern.compile(".*\\/fulltext\\-page\\.(\\d+)\\.svg");

	private List<Pattern> patternList;
	private String currentPageName;
	private Writer logWriter;
	private Writer vectorWriter;
	private SVGSVG currentSvg;
	private File svgDir;
	private File svgVectorDir;
    
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMISVGTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMISVGTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMISVGTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
		System.out.println("pages                " + pageList);
		System.out.println("regexes              " + regexList);
		System.out.println("regexfile            " + regexFilename);
		System.out.println("tidyList             " + tidyList);
		System.out.println("vectorLogfilename    " + vectorLog);
		System.out.println("vectorDir            " + vectorDirname);
		System.out.println("logfile             " + logfile);
		System.out.println();
	}

    @Override
    protected void runSpecifics() {
    	createPatterns();
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	private void createPatterns() {
		if (regexFilename != null) {
			createPatternsFromFile();
		} else if (regexList != null) {
			createPatternsFromRegexes();
		}
	}

	private void createPatternsFromFile() {
		File jsonRegexFile = AMIUtil.getFileWithExpandedVariables(regexFilename);
		if (!jsonRegexFile.exists()) {
			throw new RuntimeException("File does not exist "+jsonRegexFile);
		}
	}

	private void createPatternsFromRegexes() {
		patternList = new ArrayList<Pattern>();
		if (regexList != null) {
			int i = 0;
			String ws;
			String regex = null;
			while (i < regexList.size()) {
				ws = regexList.get(i++);
				if (i == regexList.size() || !Character.isDigit(ws.charAt(0))) {
					System.err.println("badly formatted regexList at "+ws+" | "+regex);
					break;
				}
				int weight = Integer.parseInt(ws);
				regex = regexList.get(i++);
				Pattern pattern = (Character.isUpperCase(regex.charAt(0))) ? 
					Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				WeightedPattern weightedPattern = new WeightedPattern(pattern, weight);
				patternList.add(pattern);
			}
		}
	}

	protected boolean processTree() {
		processedTree = true;
		System.out.println("cTree: "+cTree.getName());
		svgDir = cTree.getExistingSVGDir();
		if (svgDir == null || !svgDir.exists()) {
			LOG.warn("no svg/ dir");
			processedTree = false;
		} else {
			createLogfileWriter();
			createVectorLogfileWriter();
			processPages();
			closeWriters();
			LOG.trace("closed");
		}
		return processedTree;
	}

	private void processPages() {
		List<File> svgFiles = CMineGlobber.listSortedChildFiles(svgDir, CTree.SVG);
		// these are normally pages
		for (File svgFile : svgFiles) {
			currentPageName = FilenameUtils.getBaseName(svgFile.toString());
			Matcher pageMatcher = PAGE_EXTRACT.matcher(svgFile.toString());
			int page = pageMatcher.matches() ? Integer.parseInt(pageMatcher.group(1)): -1;
			if ((pageList == null || pageList.size() == 0) || pageList.contains(new Integer(page))) {
				if (pageList != null) System.err.print(" p"+page);
				try {
					runSVG(svgFile);
				} catch (IOException e) {
					LOG.error("***, cannot process "+svgFile+" "+e.getMessage());
				}
			}
		}
	}

	private void closeWriters() {
//		closeWriter(logWriter);  // don't close!!
		closeWriter(vectorWriter);
	}

	private void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void createLogfileWriter() {
		logWriter = createWriter(svgDir, logfile);
	}

	private void createVectorLogfileWriter() {
		vectorWriter = vectorLog == null ? null : createWriter(svgDir, vectorLog);
	}

	private Writer createWriter(File svgDir, String filename) {
		Writer writer = new PrintWriter(System.out);
		if (filename != null) {
			try {
				File file = filename == null ? null : new File(svgDir, filename);
				writer = new PrintWriter(file);
			} catch (IOException e1) {
				writer = null;
				LOG.error("cannot create printWriter: "+e1);
			}
		}
		return writer;
	}
	
	private void runSVG(File svgFile) throws IOException {
		String basename = FilenameUtils.getBaseName(svgFile.toString());
		if (!svgFile.exists()) {
			System.err.println("!not exist "+basename+"!");
		} else {
			ensureVectorDirectory();
			currentSvg = (SVGSVG) SVGUtil.parseToSVGElement(new FileInputStream(svgFile));
			if (cacheList != null) {
				createHorizontalLineAndTextCacheAndWrite();
			}
			
			extractTextWithRegexes();
			extractSVGPaths();
		}
	}

	private void createHorizontalLineAndTextCacheAndWrite() {
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(currentSvg);
		SVGG g = new SVGG(); 
		List<SVGText> svgTexts = cache.getOrCreateTextCache().getOrCreateOriginalTextList();
		g.appendChildCopies(svgTexts);
		List<SVGLine> horizontalLines = cache.getOrCreateLineCache().getOrCreateHorizontalLineList();
		g.appendChildCopies(horizontalLines);
		List<SVGRect> rects = cache.getOrCreateRectCache().getOrCreateRectList();
		g.appendChildCopies(rects);
		List<SVGPath> paths = cache.getOrCreatePathCache().getCurrentPathList();
		g.appendChildCopies(paths);
		if (horizontalLines.size() > 0) {
			LOG.debug("HO "+horizontalLines.size());
			SVGSVG.wrapAndWriteAsSVG(g, new File(svgVectorDir, "textLineCache.svg" ));
		}
	}

	/** maybe this should be in the cache?
	 * 
	 */
	private void extractTextWithRegexes() {
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(currentSvg);
		for (SVGText text : textList) {
			String s = text.getValue();
			matchPatterns(s);
		}
	}

	private void extractSVGPaths() {
		List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(currentSvg);
		pathList = tidy(pathList);
		if (vectorDirname != null) {
			ensureVectorDirectory();
		}
		if (vectorWriter != null) {
			String page = "p."+currentPageName.split("\\.")[1];
			int size = pathList.size();
			try {
				if (size > 0) {
					vectorWriter.write("\n" + page + ":: " + size);
					if (size > 0) {
						SVGSVG.wrapAndWriteAsSVG(pathList, new File(svgVectorDir, "paths.svg"));
					}
				}
			} catch (IOException e) {
				System.err.println("Cannot write to writer");
			}
		}
	}

	private List<SVGPath> tidy(List<SVGPath> pathList) {
		if (tidyList != null) {
			for (TidySVG tidy : tidyList) {
				switch (tidy) {
					case emptypath:
						pathList = SVGPath.createPathsWithNoEmptyD(pathList);
						break;
					case nomove:
						pathList = SVGPath.createPathsWithNoMissingMove(pathList);
						break;
					case nullmove:
						pathList = SVGPath.createPathsWithNoNullMove(pathList);
						break;
					default: 
						break;
				}
			}
		}
		return pathList;
	}

	private void ensureVectorDirectory() {
		svgVectorDir = new File(svgDir, currentPageName);
	}

	private void matchPatterns(String s) {
		if (patternList != null) {
			for (Pattern pattern : patternList) {
				Matcher matcher = pattern.matcher(s);
				if (matcher.find()) {
					if (logWriter != null) {
						String page = "p."+currentPageName.split("\\.")[1];
						try {
							logWriter.write("\n" + page + ":: " + pattern + ": " + s);
						} catch (IOException e) {
							System.err.println("Cannot write to writer");
						}
					}
				}
			}
		}
	}

}
class WeightedPattern {
	Pattern pattern;
	Integer weight;
	
	public WeightedPattern(Pattern pattern, Integer weight) {
		this.pattern = pattern;
		this.weight = weight;
	}
}
