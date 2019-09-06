package org.contentmine.svg2xml.page;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.text.structure.TextAnalyzer;
import org.contentmine.svg2xml.paths.Chunk;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class TextAnalyzerUtils {
	private static final Logger LOG = Logger.getLogger(TextAnalyzerUtils.class);

	public static Double getCommonLeftXCoordinate(List<SVGText> texts, double eps) {
		Double dubble = null;
		for (SVGText text : texts) {
			double d = text.getXY().getX();
			if (dubble == null) {
				dubble = d;
			} else if (!Real.isEqual(dubble, d, eps)) {
				dubble = null;
				break;
			}
		}
		return dubble;
	}

	public static Double getCommonRightXCoordinate(List<SVGText> texts, double eps) {
		Double dubble = null;
		for (SVGText text : texts) {
			double d = text.getBoundingBox().getXMax();
			if (dubble == null) {
				dubble = d;
			} else if (!Real.isEqual(dubble, d, eps)) {
				dubble = null;
				break;
			}
		}
		return dubble;
	}

	public static Double getCommonYCoordinate(List<SVGText> texts, double eps) {
		Double dubble = null;
		for (SVGText text : texts) {
			double d = text.getXY().getY();
			if (dubble == null) {
				dubble = d;
			} else if (!Real.isEqual(dubble, d, eps)) {
				dubble = null;
				break;
			}
		}
		return dubble;
	}

	/**
	<text font-size="10" font-family="Verdana">
	      <tspan x="10" y="10">Here is a paragraph that</tspan>
	      <tspan x="10" y="20">requires word wrap.</tspan>
	    </text>	 
	    @param hasGParaSvgTextChild <g><text/><text/>...<g name='para'><text/>
	    */
	public static void cleanAndWordWrapText(Chunk textChunk) {
		SVGText svgText = getConcatenatedText(textChunk);
		if (svgText != null) {
			LOG.trace("wrapped text "+textChunk.getId());
			double textWidthFactor = TextAnalyzer.DEFAULT_TEXTWIDTH_FACTOR;
			List<SVGElement> rawTextList = SVGUtil.getQuerySVGElements(textChunk, "./svg:text");
			Real2Range rawBoundingBox = SVGUtil.createBoundingBox(rawTextList);
			if (rawBoundingBox == null) {
				throw new RuntimeException("null BB "+textChunk.getId());
			}
			Real2Range scaledBox = TextAnalyzerUtils.scaleBoxX(textWidthFactor, rawBoundingBox);
			for (AbstractCMElement element : rawTextList) {
				element.detach();
			}
			String title = svgText.getText();
			svgText.createWordWrappedTSpans(textWidthFactor, scaledBox, svgText.getFontSize());
			svgText.setTitle(title);
		} else {
			LOG.trace("concatenated text not processed for "+textChunk.getId());
		}
	}

	public static SVGText getConcatenatedText(Chunk textChunk) {
		SVGText svgText = null;
		String id = textChunk.getId();
		LOG.trace("text "+id);
		List<SVGElement> gList = SVGUtil.getQuerySVGElements(textChunk, "./svg:g[@name='para' and svg:text]");
		if (gList.size() == 1) {
			svgText = findSingleSVGText(gList);
		} else if (gList.size() > 0) {
			svgText = findSingleSVGText(gList);
			svgText.setTitle("more than one sibling para, omitted >1 as possible subscript");
		}
		return svgText;
	}

	private static SVGText findSingleSVGText(List<SVGElement> gList) {
		SVGText svgText = null;
		AbstractCMElement gName = (AbstractCMElement) gList.get(0);
		List<SVGElement> texts = SVGUtil.getQuerySVGElements(gName, "./svg:text");
		if (texts.size() == 1) {
			svgText = (SVGText) texts.get(0);
		}
		return svgText;
	}

	private static Real2Range scaleBoxX(double scale,	Real2Range rawBoundingBox) {
		if (rawBoundingBox == null) {
			LOG.trace("RAWBB");
		}
		RealRange xRange = rawBoundingBox.getXRange();
		double r = xRange.getRange();
		Real2Range scaledBox = new Real2Range(
				new RealRange(xRange.getMin(), xRange.getMin() + scale * xRange.getRange()),
				rawBoundingBox.getYRange());
		return scaledBox;
	}
	
	/** replace by SVGPlusCoordinate
	 * @param text
	 * @return
	 */
	public static Integer getScaledYCoord(SVGText text) {
		Double y = text.getY();
		return (y == null ? null : getScaledYCoord(y));
	}
	
	public static Integer getScaledYCoord(Double d) {
		return (int) Math.round((d * Y_SCALE));
	}

	private static final double Y_SCALE = 10;

	public static Multimap<Integer, SVGText> createCharactersByY(List<SVGText> textCharacters) {
		Multimap<Integer, SVGText> charactersByY = ArrayListMultimap.create();
		for (SVGText text : textCharacters) {
			Integer yCoord = TextAnalyzerUtils.getScaledYCoord(text);
			LOG.trace("Y "+yCoord);
			charactersByY.put(yCoord, text);
		}
		return charactersByY;
	}

//
//	public static List<Chunk> castToChunks(List<SVGElement> chunkElements) {
//		List<Chunk> chunks = new ArrayList<Chunk>();
//		for (SVGElement chunkElement : chunkElements) {
//			Chunk chunk = null;
//			if (!(chunkElement instanceof Chunk)) {
//				chunk = Chunk.createAndReplace(chunkElement);
//			} else {
//				chunk = (Chunk) chunkElement;
//			}
//			chunks.add(chunk);
//		}
//		return chunks;
//	}


}
