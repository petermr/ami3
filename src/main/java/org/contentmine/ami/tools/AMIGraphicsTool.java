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
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.contentmine.graphics.svg.cache.GlyphCache;
import org.contentmine.graphics.svg.cache.ImageCache;
import org.contentmine.graphics.svg.cache.LineBoxCache;
import org.contentmine.graphics.svg.cache.LineCache;
import org.contentmine.graphics.svg.cache.MathCache;
import org.contentmine.graphics.svg.cache.PageCache;
import org.contentmine.graphics.svg.cache.PathCache;
import org.contentmine.graphics.svg.cache.PolygonCache;
import org.contentmine.graphics.svg.cache.PolylineCache;
import org.contentmine.graphics.svg.cache.RectCache;
import org.contentmine.graphics.svg.cache.ShapeCache;
import org.contentmine.graphics.svg.cache.TextCache;

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

	public enum Cache {
		document(new DocumentCache()),
		glyph(new GlyphCache()),
		image(new ImageCache()),
		line(new LineCache()),
		linebox(new LineBoxCache()),
		math(new MathCache()),
		page(new PageCache()),
		// more page components could go here
		path(new PathCache()),
		polygon(new PolygonCache()),
		polyline(new PolylineCache()),
		rect(new RectCache()),
		shape(new ShapeCache()),
		text(new TextCache()),
		;
		private AbstractCache cache;
		private Cache(AbstractCache cache) {
			this.cache = cache; 
		}
		
		public AbstractCache getCache() {
			return cache;
		}
	}
	

    @Option(names = {"--cache"},
    		arity = "1..*",
            description = "caches to use")
	private List<Cache> cacheList = new ArrayList<>() ;


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
			System.out.println("caches              " + cacheList);
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
			LOG.debug(">>>"+svgDir);
			
			List<Path> filesWithName = Files.walk(Paths.get(svgDir.toString()))
		            .filter(s -> s.toString().endsWith("."+CTree.SVG))
		            .filter(s -> s.toString().matches(FULLTEXT_PAGE_SVG_REGEX))
		            .map(Path::getFileName).sorted().collect(Collectors.toList());

		    for (Path path : filesWithName) {
				SVGElement svgElement = SVGElement.readAndCreateSVG(new File(svgDir, path.toString()));
		    	displayCaches(svgDir, path, svgElement);
		        
		    }
		} catch (IOException e) {
			throw new RuntimeException("cannot list files", e);
		}
	}

	private void displayCaches(File svgDir, Path path, SVGElement svgElement) {
		cacheList.forEach(c -> c.getCache().display(svgDir, path, svgElement));
	}



}
