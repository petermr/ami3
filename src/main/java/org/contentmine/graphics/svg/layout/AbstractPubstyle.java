package org.contentmine.graphics.svg.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.util.NamePattern;

/** a pubstyle and/or its components
 * 
 * @author pm286
 *
 */
public abstract class AbstractPubstyle extends SVGG {
	private static final Logger LOG = Logger.getLogger(AbstractPubstyle.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	static final String NEW_FILL = "newFill";

	private List<NamePattern> namePatternList;
	private List<SVGText> templateTexts;
	protected List<SVGText> extractedTexts;
	private List<SVGPath> templatePaths;
	private List<SVGPath> extractedPaths;
	protected List<SVGShape> extractedShapes;
	protected SVGPubstyle containingPubstyle;

	protected AbstractPubstyle() {
		super();
		this.setSVGClassName(getPubstyleClassName());
	}

	/** create from parsed element.
	 * 
	 * @param element
	 */
	public AbstractPubstyle(SVGElement element) {
		this();
		this.copyAttributesChildrenElements(element);
	}

	public List<SVGElement> extractElementsContainedInBox(SVGElement inputSVGElement) {
		List<SVGElement> elements = SVGElement.extractSelfAndDescendantElements(inputSVGElement);
		List<SVGElement> elementsInBox = SVGElement.extractElementsContainedInBox(elements, this.getBoundingBox());
		return elementsInBox;
	}

	public Map<String, String> matchTexts(List<SVGText> texts) {
		Map<String, String> keyValues = new HashMap<String, String>();
		List<NamePattern> namePatterns = getOrCreateNamePatternList();
		for (SVGText text : texts) {
			matchAgainstText(keyValues, text);
		}
		return keyValues;
	}

	public  Map<String, String> extractKeyValues(SVGElement inputSVGElement) {
		List<SVGElement> headerElements = this.extractElementsContainedInBox(inputSVGElement);
		List<SVGText> texts = SVGText.extractTexts(headerElements);
		Map<String, String> keyValues = matchTexts(texts);
		return keyValues;
	}
	
	public List<SVGText> extractTextsContainedInBox(SVGElement inputSVGElement) {
		List<SVGElement> extractedElements = extractElementsContainedInBox(inputSVGElement);
		extractedTexts = SVGText.extractTexts(extractedElements);
		return extractedTexts;
	}

	public List<SVGPath> extractPathsContainedInBox(SVGElement inputSVGElement) {
		List<SVGElement> extractedElements = extractElementsContainedInBox(inputSVGElement);
		extractedPaths = SVGPath.extractPaths(extractedElements);
		return extractedPaths;
	}

	public List<SVGShape> extractShapesContainedInBox(SVGElement inputSVGElement) {
		List<SVGElement> extractedElements = extractElementsContainedInBox(inputSVGElement);
		extractedShapes = SVGShape.extractShapes(extractedElements);
		return extractedShapes;
	}

	public List<DocumentChunk> matchDocumentChunks() {
		createTemplateTexts();
		createTemplatePaths();
		List<DocumentChunk> annotatedChunkList = new ArrayList<DocumentChunk>();
		annotatedChunkList.addAll(addAnnotatedPathChunks());
		annotatedChunkList.addAll(addAnnotatedTextChunks());
		return annotatedChunkList;
	}

	private List<DocumentChunk> addAnnotatedTextChunks() {
		List<DocumentChunk> annotatedTextChunkList = new ArrayList<DocumentChunk>();
		if (templateTexts != null) {
			for (SVGText templateText : templateTexts) {
				ElementSelector selector = new ElementSelector(templateText);
				if (extractedTexts != null) {
					for (SVGText extractedText : extractedTexts) {
						DocumentChunk annotatedChunk = selector.createAnnotatedSectionHead(templateText, extractedText);
						if (annotatedChunk != null) {
							annotatedTextChunkList.add(annotatedChunk);
						}
					}
				}
			}
		}
		return annotatedTextChunkList;
	}

	private List<DocumentChunk> addAnnotatedPathChunks() {
		List<DocumentChunk> annotatedPathChunkList = new ArrayList<DocumentChunk>();
		if (templatePaths != null) {
			for (SVGPath templatePath : templatePaths) {
				ElementSelector selector = new ElementSelector(templatePath);
				if (extractedPaths != null) {
					for (SVGPath extractedPath : extractedPaths) {
						DocumentChunk annotatedChunk = selector.createDocumentChunk(templatePath, extractedPath);
						if (annotatedChunk != null) {
							annotatedPathChunkList.add(annotatedChunk);
						}
					}
				}
			}
		}
		return annotatedPathChunkList;
	}

	

	// ===========================================

	/** the class for each sub-PubStyle (e.g. "abstract")
	 * 
	 * @return
	 */
	protected abstract String getPubstyleClassName();
	
	// ===========================================
	
	/** adds text matches to key-values pairs
	 * 
	 * @param keyValues
	 * @param text
	 */
	private void matchAgainstText(Map<String, String> keyValues, SVGText text) {
		String value = text.getText();
		for (NamePattern namePattern : namePatternList) {
			List<String> nameList = namePattern.getNameList();
			Matcher matcher = namePattern.getPattern().matcher(value);
			if (matcher.matches()) {
				addNamesToMatch(keyValues, nameList, matcher);
			}
		}
	}

	private void addNamesToMatch(Map<String, String> keyValues, List<String> nameList, Matcher matcher) {
		if (matcher.groupCount() != nameList.size()) {
			throw new RuntimeException("groups and names don't match: "+matcher.groupCount() +" != "+ nameList.size());
		}
		for (int i = 1; i <= matcher.groupCount(); i++) {
			String key = nameList.get(i - 1);
			String matchedValue = matcher.group(i);
			if (key == null) {
				throw new RuntimeException("Null key in match");
			} else if (matchedValue == null) {
				throw new RuntimeException("Null value in match");
			}
			keyValues.put(key, matchedValue);
		}
	}

	private List<NamePattern> getOrCreateNamePatternList() {
		return getOrCreateNamePatternList((String)null);
	}

	/** 
	 * 
	 * @param xpath if null or matches tempates, add to list
	 * @return
	 */
	private List<NamePattern> getOrCreateNamePatternList(String xpath) {
		if (namePatternList == null) {
			createTemplateTexts();
			namePatternList = new ArrayList<NamePattern>();
			for (SVGText templateText : templateTexts) {
				if (xpath == null || XMLUtil.getQueryNodes(templateText, xpath).size() > 0) {
					addTemplateToNamedPatternList(templateText);
				}
			}
		}
		return namePatternList;
	}

	private void addTemplateToNamedPatternList(SVGText templateText) {
		String textValue = templateText.getText().trim();
		// replace all whitespace
		textValue = textValue.replaceAll("[\\s\\n]", "");
		if (textValue.startsWith("(") && textValue.endsWith(")")) {
			String regex = textValue.substring(1,  textValue.length() - 1);
			Pattern pattern = Pattern.compile(regex);
			List<String> captureNameList = NamePattern.makeCaptureNameList(regex);
			NamePattern namePattern1 = new NamePattern(pattern, captureNameList);
			NamePattern namePattern = namePattern1;
			namePatternList.add(namePattern);
		}
	}

	private List<SVGText> createTemplateTexts() {
		templateTexts = SVGText.extractSelfAndDescendantTexts(this);
		return templateTexts;
	}
	
	private List<SVGPath> createTemplatePaths() {
		templatePaths = SVGPath.extractSelfAndDescendantPaths(this);
		if (templatePaths.size() > 0) {
//			LOG.debug("PATHS "+templatePaths.size());
		}
		return templatePaths;
	}
	
	public void setExtractedTexts(List<SVGText> texts) {
		this.extractedTexts = texts;
	}
	
	public void setExtractedPaths(List<SVGPath> paths) {
		this.extractedPaths = paths;
	}

	public void setExtractedShapes(List<SVGShape> shapes) {
		this.extractedShapes = shapes;
	}

	public void setContainingPubstyle(SVGPubstyle containingPubstyle) {
		this.containingPubstyle = containingPubstyle;
	}



}
