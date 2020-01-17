package org.contentmine.ami.tools;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.AbstractCache;
import org.contentmine.graphics.svg.cache.AbstractCache.CacheType;
import org.contentmine.graphics.svg.cache.ComponentCache;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses vector graphics
 * 
 * @author pm286
 *
 */



@Command(
name = "ami-graphics", 
aliases = "graphics",
version = "ami-graphics 0.1",
description = "	transforms graphics contents (often from PDF/SVG)"
		+ "much is based on the Cache system"
		
)

public class AMIGraphicsTool extends AbstractAMITool {
	private static final String FULLTEXT_PAGE_SVG_REGEX = ".*/fulltext\\-page\\.\\d+\\.svg";

	private static final String IMAGE = "graphics";

	private static final Logger LOG = Logger.getLogger(AMIGraphicsTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


    @Option(names = {"--cache"},
    		arity = "1..*",
            description = "caches to use")
	private List<CacheType> cacheTypeList = new ArrayList<>() ;


    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIGraphicsTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIGraphicsTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIGraphicsTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
    	if (verbosity.length > 0) {
			System.out.println("caches              " + cacheTypeList);
    	}
		System.out.println();
	}


    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
//			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }


	protected void processTree() {
		if (getVerbosityInt() > 0) System.out.println("AMIGraphicsTool processTree");
		runGraphics();
	}

	private void runGraphics() {
		File svgDir = cTree.getExistingSVGDir();
		try {
			LOG.trace(">>>"+svgDir);
			if (svgDir != null) {
				List<Path> filesWithName = Files.walk(Paths.get(svgDir.toString()))
			            .filter(s -> s.toString().endsWith("."+CTree.SVG))
			            .filter(s -> s.toString().matches(FULLTEXT_PAGE_SVG_REGEX))
			            .map(Path::getFileName).sorted().collect(Collectors.toList());
	
			    for (Path path : filesWithName) {
					File file = new File(svgDir, path.toString());
					System.out.print(" "+path.toString().replaceAll("(fulltext\\-page|\\.svg)", ""));
			    	displayCaches(file);
			        
			    }
			}
		} catch (IOException e) {
			throw new RuntimeException("cannot list files", e);
		}
	}

	private void displayCaches(File svgFile) {
		Level level = LOG.getLevel();
//		LOG.setLevel(Level.TRACE);
		ComponentCache componentCache = ComponentCache.readAndCreateComponentCache(svgFile);
		List<AbstractCache> cacheList = componentCache.getCaches(cacheTypeList);
		for (AbstractCache cache : cacheList) {
			File outDir = new File(svgFile.toString().replace(".svg", "").replace("fulltext-",  ""));
			outDir.mkdirs();
			File outSvgFile = new File(outDir, cache.getOrCreateCacheType()+".svg");
			LOG.trace(cache.getOrCreateCacheType()+"/"+outDir);
			SVGElement svgElement = cache.getOrCreateConvertedSVGElement();
			if (svgElement.getChildElements().size() > 0) {
				SVGSVG.wrapAndWriteAsSVG(svgElement, outSvgFile);
			}
		}
		LOG.setLevel(level);
	}



}
