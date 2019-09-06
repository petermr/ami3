package org.contentmine.graphics.svg.glyphs;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.fonts.GlyphSet;
import org.contentmine.graphics.svg.fonts.SVGGlyph;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multimap;

@Ignore("This really should be in POM or CL")
public class GlyphIT {
	private static final Logger LOG = Logger.getLogger(GlyphIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	@Test
	public void testFigure2b() {
		String fileroot = "figure2b";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		GlyphSet glyphSet = new GlyphSet();
		glyphSet.createGlyphSetsAndAnalyze(fileroot, outputDir, inputFile);
		
	}
	@Test
	public void testCompareMergeGlyphSets() {
		String fileroot = "figure2b";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		GlyphSet glyphSet2b= new GlyphSet();
		glyphSet2b.createGlyphSetsAndAnalyze(fileroot, outputDir, inputFile);
		fileroot = "figure4b";
		dirRoot = "glyphs";
		outputDir = new File("target/", dirRoot);
		inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		inputFile = new File(inputDir, fileroot + ".svg");
		GlyphSet glyphSet4b = new GlyphSet();
		glyphSet4b.createGlyphSetsAndAnalyze(fileroot, outputDir, inputFile);
		Multimap<String, SVGGlyph> glyphsBySig2b = glyphSet2b.getOrCreateGlyphBySignatureMap();
		Iterator<String> iterator2b = glyphsBySig2b.keySet().iterator();
		while (iterator2b.hasNext()) {
			String sig = iterator2b.next();
			List<SVGGlyph> glyphList = glyphSet4b.getGlyphBySig(sig);
			LOG.debug("for "+sig+": => "+(glyphList == null ? "NULL" : glyphList));
		}
		
	}

}
