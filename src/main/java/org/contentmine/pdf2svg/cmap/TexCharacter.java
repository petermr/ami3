package org.contentmine.pdf2svg.cmap;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * 
# no.^chr^LaTeX^unicode-math^cls^category^requirements^comments
00021^!^!^\exclam^N^mathpunct^^EXCLAMATION MARK
 * @author pm286
 *
 */
public class TexCharacter {

	private static final String COMMENTS = "comments";
	private static final String REQUIREMENTS = "requirements";
	private static final String CATEGORY = "category";
	private static final String CLASS = "class";
	private static final String UNICODE_MATH = "unicodeMath";
	private static final String LATEX = "latex";
	private static final String CHARACTER = "character";
	private static final String UNICODE = "unicode";
	private String unicodeString;
	private Integer unicodePoint;
	private String character;
	private String latex;
	private String unicodeMath;
	private String clazz;
	private String category;
	private String requirements;
	private String comments;

	public TexCharacter(String[] tokens) {
		readTokens(tokens);
	}

	public void readTokens(String[] tokens) {
		createUnicode(tokens[0]);
		createCharacter(tokens[1]);
		createLatex(tokens[2]);
		createUnicodeMath(tokens[3]);
		createClazz(tokens[4]);
		createCategory(tokens[5]);
		createRequirements(tokens[6]);
		createComments(tokens[7]);
		
	}

	private void createUnicode(String token) {
		unicodeString      = token.trim();
		if (unicodeString.equals("")) {
			System.out.println("NO unicode: "+ unicodeString);
		} else {
			try {
				unicodePoint = Integer.decode("0X"+unicodeString);
			} catch (Exception e) {
				throw new RuntimeException("Cannot parse as integer: "+unicodeString);
			}
		}
	}
	
	private void createCharacter(String token) {
		character      = token.trim();
		if (character.equals("") || character.equals("?")) {
			if (unicodePoint <= 0xFFFF) {
				character = String.valueOf((char)(int)unicodePoint);
			}
		}
	}
	
	private void createLatex(String token) {
		latex      = token.trim();
//		if (latex.equals("")) System.out.println("latex: "+ unicode);
	}
	
	private void createUnicodeMath(String token) {
		unicodeMath      = token.trim();
//		if (unicodeMath.equals("")) System.out.println("unicodeMath: "+ unicode);
	}
	
	private void createClazz(String token) {
		clazz      = token.trim();
		if ("NABCDEFGLOPRSUVX".indexOf(clazz) == -1 && !clazz.equals("R?") && !clazz.equals("")) System.out.println("Class: "+unicodeString+" / "+clazz);
	}
	
	private void createCategory(String token) {
		category      = token.trim();
//		if (!category.equals("")) System.out.println("category: "+ unicode+" / "+category);
	}
	
	private void createRequirements(String token) {
		requirements      = token.trim();
//		if (!requirements.equals("")) System.out.println("requirements: "+requirements+"; "+ unicode);
	}
	
	private void createComments(String token) {
		comments      = token.trim();
		if (comments.equals("")) System.out.println("comments: "+ unicodeString);
	}

	public String getLatex() {
		return latex;
	}
	
	public String getUnicodeMath() {
		return unicodeMath;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Un "+unicodeString);
		sb.append("; Ch "+character);
		sb.append("; Lx "+latex);
		sb.append("; Um "+unicodeMath);
		sb.append("; Cl "+clazz);
		sb.append("; Cat "+category);
		sb.append("; Rq "+requirements);
		sb.append("; Cm "+comments);
		return sb.toString();
	}

	public Integer getUnicodePoint() {
		return unicodePoint;
	}

	public String getUnicodeHex() {
		return unicodePoint == null ? null : Integer.toHexString(unicodePoint); 
	}
	
	public Element createElement() {
		Element element = new Element("character");
		addAttribute(element, UNICODE, unicodeString);
		addAttribute(element, CHARACTER, character);
		addAttribute(element, LATEX,latex);
		addAttribute(element, UNICODE_MATH,unicodeMath);
		addAttribute(element, CLASS,clazz);
		addAttribute(element, CATEGORY, category);
		addAttribute(element, REQUIREMENTS, requirements);
		addAttribute(element, COMMENTS, comments);
		return element;
	}

	private void addAttribute(Element element, String name, String value) {
		if (value != null && value.trim().length() > 0) {
			element.addAttribute(new Attribute(name, value));
		}
	}
}
