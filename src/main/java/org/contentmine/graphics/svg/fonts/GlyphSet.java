package org.contentmine.graphics.svg.fonts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.contentmine.graphics.svg.plot.XPlotBox;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Attribute;

/** holds the glyphs for a font
 * 
 * @author pm286
 *
 */
public class GlyphSet {
	
	private static final Logger LOG = Logger.getLogger(GlyphSet.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String GLYPH_LIST = "glyphList";
	private static final String GLYPH_SET = "glyphSet";
	private static String CHARACTER = "character";
	public static final double CHARACTER_DY = -0.15;
	public static final double CHARACTER_DX = -0.1;
	
	private Multiset<String> signatureSet;
	private Multimap<String, SVGGlyph> glyphMapBySignature;
	private HashMap<String, String> characterBySignatureMap;
	private List<Multiset.Entry<String>> signatureListSortedByCount;
	
	private double fontSizeRatio = 1.0;
	
	public GlyphSet() {
		
	}
	
	public void addToSetsAndMaps(SVGGlyph glyph) {
		getOrCreateSignatureSet();
		signatureSet.add(glyph.getOrCreateSignatureAttributeValue());
		getOrCreateGlyphBySignatureMap();
		glyphMapBySignature.put(glyph.getOrCreateSignature(), glyph);
	}

	public Multiset<String> getOrCreateSignatureSet() {
		if (signatureSet == null) {
			signatureSet = HashMultiset.create();
		}
		return signatureSet;
	}

	public Multimap<String, SVGGlyph> getOrCreateGlyphBySignatureMap() {
		if (glyphMapBySignature == null) {
			glyphMapBySignature = ArrayListMultimap.create();
		}
		return glyphMapBySignature;
	}

	public HashMap<String, String> getOrCreateCharacterMapBySignature() {
		if (characterBySignatureMap == null) {
			characterBySignatureMap = new HashMap<String, String>();
		}
		return characterBySignatureMap;
	}

	public List<Multiset.Entry<String>> getOrCreateSignaturesSortedByCount() {
		signatureSet = getOrCreateSignatureSet();
		signatureListSortedByCount = MultisetUtil.createListSortedByCount(signatureSet);
		return signatureListSortedByCount;
	}

	public List<Multiset.Entry<String>> getGlyphsSortedBySignatureCount() {
		Multiset<String> signatureSet = getOrCreateSignatureSet();
		List<Multiset.Entry<String>> sigsByCount = MultisetUtil.createListSortedByCount(signatureSet);
		return sigsByCount;
	}

	public void createGlyphSetsAndAnalyze(String fileroot, File outputDir, File inputFile) {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		List<SVGPath> paths = componentCache.getOrCreatePathCache().getCurrentPathList();
		addPathsToSetsAndMaps(paths);
		writeGlyphsForEachSig(fileroot, outputDir);
	}

	private void writeGlyphsForEachSig(String fileroot, File outputDir) {
		signatureListSortedByCount = getOrCreateSignaturesSortedByCount();
		for (int i = 0; i < signatureListSortedByCount.size(); i++) {
			Entry<String> sigEntry = signatureListSortedByCount.get(i);
			String sig = sigEntry.getElement();
			List<SVGGlyph> glyphList = new ArrayList<SVGGlyph>(getOrCreateGlyphBySignatureMap().get(sig));
			AbstractCMElement g = this.createSVG(glyphList, i);
			SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, fileroot+"/"+"glyph."+i+".svg"), 300, 100);
		}
	}

	private void addPathsToSetsAndMaps(List<SVGPath> paths) {
		for (SVGPath path : paths) {
			PathPrimitiveList pathPrimitiveList = path.getOrCreatePathPrimitiveList();
			List<PathPrimitiveList> pathPrimitiveListList = pathPrimitiveList.splitBefore(MovePrimitive.class);
			for (PathPrimitiveList primitiveList : pathPrimitiveListList) {
				SVGGlyph outlineGlyph = SVGGlyph.createRelativeToBBoxOrigin(primitiveList);
				addToSetsAndMaps(outlineGlyph);
			}
		}
	}

	public AbstractCMElement createSVG(List<SVGGlyph> glyphList, int serial) {
		AbstractCMElement g = new SVGG();
		Transform2 t2 = Transform2.applyScale(5.0);
		g.appendChild(SVGText.createDefaultText(new Real2(10,10.), ""+serial+" "+glyphList.get(0).getOrCreateSignature()));
		for (SVGGlyph glyph : glyphList) {
			// have to add copy() or add to SVGElement
			SVGPath glyph1 = (SVGPath) glyph.copy();
			glyph1.setTransform(t2);
			g.appendChild(glyph1);
		}
		return g;
	}

	public List<SVGGlyph> getGlyphBySig(String sig) {
		Multimap<String, SVGGlyph> glyphMapBySig = getOrCreateGlyphBySignatureMap();
		return glyphMapBySig == null ? null : new ArrayList<SVGGlyph>(glyphMapBySignature.get(sig));
	}

	/** writes glyset as XML.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void writeGlyphSet(File file) throws IOException {
		AbstractCMElement glyphSetXml = new SVGElement(GLYPH_SET);
		for (String signature : glyphMapBySignature.keySet()) {
			SVGElement sigXml = new SVGElement(GLYPH_LIST);
			sigXml.addAttribute(new Attribute(SVGPath.SIGNATURE, signature));
			glyphSetXml.appendChild(sigXml);
			List<SVGGlyph> glyphs = new ArrayList<SVGGlyph>(glyphMapBySignature.get(signature));
			for (SVGGlyph glyph : glyphs) {
				sigXml.appendChild(glyph.copy());
			}
		}
		XMLUtil.debug(glyphSetXml, new FileOutputStream(file), 1);
	}

	public static GlyphSet readGlyphSet(File file) {
		GlyphSet glyphSet = new GlyphSet();
		AbstractCMElement glyphSetXml = SVGElement.readAndCreateSVG(file);
		// no local name yet
		List<SVGElement> glyphList = SVGUtil.getQuerySVGElements(glyphSetXml, "./*");
		for (SVGElement glyph : glyphList) {
			String signature = glyph.getAttributeValue(SVGPath.SIGNATURE);
			glyphSet.getOrCreateSignatureSet().add(signature);
			String character = glyph.getAttributeValue(GlyphSet.CHARACTER);
			glyphSet.getOrCreateCharacterMapBySignature().put(signature, character);
		}
		return glyphSet;
	}

	
	public String getCharacterBySignature(String signature) {
		getOrCreateCharacterMapBySignature();
		return characterBySignatureMap.get(signature);
	}

	/** convert paths in list to text where possible.
	 * 
	 * @param elementList list of SVGElements
	 * @return list of converted and unconverted elements in same order
	 */
	public List<SVGElement> createTextFromGlyph(List<SVGElement> elementList) {
		List<SVGElement> convertedElementList = new ArrayList<SVGElement>();
		for (SVGElement element : elementList) {
			SVGElement convertedElement = element;
			if (element instanceof SVGPath) {
				SVGPath svgPath = (SVGPath) element;
				String signature = svgPath.getOrCreateSignatureAttributeValue();
				if (signature.equals(SVGPath.MLLLL) ||
					signature.equals(SVGPath.MLLLLZ) ||
					signature.equals(SVGPath.MLLLZ) ||
					signature.equals(SVGPath.MLLL)) {
					LOG.trace("skipped "+signature);
					// skip lines or rects
				} else {
					List<SVGPath> pathList = splitPaths(svgPath);
					if (pathList.size() > 1) {
						LOG.trace("PL"+pathList);
					}
					LOG.trace("===================");
					for (SVGPath path : pathList) {
						convertedElement = createTextFromGlyph(path);
						convertedElementList.add(convertedElement);
						LOG.trace(">gs >"+convertedElement.getValue()+";" +convertedElement.toXML());
					}
				}
			}
//			convertedElementList.add(convertedElement);
		}
		return convertedElementList;
	}

	/** splits path at M.
	 * if no split returns list with original path
	 * @param path
	 * @return
	 */
	public List<SVGPath> splitPaths(SVGPath path) {
		List<SVGPath> pathList = new ArrayList<SVGPath>();
		PathPrimitiveList pathPrimitiveList = path.getOrCreatePathPrimitiveList();
		List<PathPrimitiveList> pathPrimitiveListList = pathPrimitiveList.splitBefore(MovePrimitive.class);
		for (PathPrimitiveList primitiveList : pathPrimitiveListList) {
			SVGPath path0 = primitiveList.getOrCreateSVGPath();
			pathList.add(path0);
		}
		return pathList;
	}

	public SVGText createTextFromGlyph(SVGPath path) {
		Real2Range bbox = path.getBoundingBox();
		double fontSize = 4.0;
		String signature = path.getOrCreateSignatureAttributeValue();
		String character = getCharacterBySignature(signature);
		if (character == null) {
			LOG.trace("cannot find character for: "+signature);
			character = "*";
		} else if (character.equals("")) {
			LOG.trace("uncertain character for: "+signature);
			character = "?";
		} else if (character.equals("l")) {
			LOG.trace("l character for: "+signature);
			character = "l";
		} else {
			LOG.trace(">"+character+"<");
		}
		Real2 delta = new Real2(0.0, 0.0);
		if (!character.equals("") && !character.equals("?")) {
			delta = new Real2(GlyphSet.CHARACTER_DX * fontSize, GlyphSet.CHARACTER_DY * fontSize);
		}
		SVGText text = new SVGText(path.getXY().plus(delta), character);
		text.setFontSize(bbox.getYRange().getRange() * fontSizeRatio);
		text.setSVGXFontWidth(bbox.getXRange().getRange() * fontSizeRatio);
		return text;
	}
	
	/** fudge factor to translate glyph boxSize to font size.
	 * 
	 * @return
	 */
	public double getFontSizeRatio() {
		return fontSizeRatio;
	}

	/** fudge factor to translate glyph boxSize to font size.
	 * 
	 * @param fontSizeRatio
	 */
	public void setFontSizeRatio(double fontSizeRatio) {
		this.fontSizeRatio = fontSizeRatio;
	}


	
}
