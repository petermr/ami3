package org.contentmine.pdf2svg.cmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.font.CodePointSet;

import nu.xom.Element;

public class TexCharacterSet {

	private static final String $$CARET = "^^$$CARET";
	private static final String $$CARET0 = "$$CARET";
	public final static String LATEX2UNICODE = "src/main/resources/org/contentmine/pdf2svg/codepoints/latex/unimathsymbols.txt";
	public final static String LATEX2UNICODE_XML = "src/main/resources/org/contentmine/pdf2svg/codepoints/latex/unimathsymbols.xml";
	private static final Logger LOG = Logger.getLogger(TexCharacterSet.class);
	public static TexCharacterSet MILDE_SET = null;
	/*
# Unicode characters and corresponding LaTeX math mode commands
# *************************************************************
#
# :Copyright: © 2011 Günter Milde
# :Date:      Last revised 2011-11-08
# :Licence:   This work may be distributed and/or modified under the
#             conditions of the `LaTeX Project Public License`_,
#             either version 1.3 of this license or (at your option)
#             any later version.
#
# .. _LaTeX Project Public License: http://www.latex-project.org/lppl.txt
#
# This is a mapping of mathematical Unicode characters to corresponding
# (La)TeX commands.
#
# While the contents of this file represent the best information
# available to the author as of the date referenced above, it
# contains omissions and maybe errors. It is likely that the
# information in this file will change from time to time.
#
# The character encoding of the file is UTF-8.
#
# Each data record consists of 8 fields. Fields are delimited by “^”.
# Spaces adjacent to the delimiter are not significant. The number and
# type of fields in this file may change in future versions.
#
# 1. code point (Unicode character number)
#
#    The code point field is unique.
#
# 2. literal character (UTF-8 encoded)
#
# 3. (La)TeX _`command`
#
#    Preferred representation of the character in TeX.
#    Alternative commands are listed in the comments_ field.
#
# 4. command used by the `unicode-math`_ package
#
#    .. _unicode-math:
#       http://mirror.ctan.org/help/Catalogue/entries/unicode-math.html
#
# 5. Unicode math character class (after MathClassEx_).
#
#    .. _MathClassEx:
#       http://www.unicode.org/Public/math/revision-11/MathClassEx-11.txt
#
#    The class can be one of:
#
#    :N: Normal- includes all digits and symbols requiring only one form
#    :A: Alphabetic
#    :B: Binary
#    :C: Closing – usually paired with opening delimiter
#    :D: Diacritic
#    :F: Fence - unpaired delimiter (often used as opening or closing)
#    :G: Glyph_Part- piece of large operator
#    :L: Large -n-ary or Large operator, often takes limits
#    :O: Opening – usually paired with closing delimiter
#    :P: Punctuation
#    :R: Relation- includes arrows
#    :S: Space
#    :U: Unary – operators that are only unary
#    :V: Vary – operators that can be unary or binary depending on context
#    :X: Special –characters not covered by other classes
#
#    C, O, and F operators are stretchy. In addition some binary
#    operators, such as 002F are stretchy as noted in the descriptive
#    comments. The classes are also useful in determining extra spacing
#    around the operators as discussed in UTR#25.
#
# 6. TeX math category (after unimath-symbols_)
#
#    .. _unimath-symbols:
#       http://mirror.ctan.org/macros/latex/contrib/unicode-math/unimath-symbols.pdf
#
# 7. requirements and conflicts
#
#    Space delimited list of LaTeX packages or features [1]_ providing
#    the LaTeX command_ or conflicting with it.
#
#    Packages/features preceded by a HYPHEN-MINUS (-) use the command
#    for a different symbol.
#
#    To save space, packages providing/modifying (almost) all commands
#    of a feature or another package are not listed here but in the
#    ``packages.txt`` file.
#
#    .. [1] A feature can be a set of commands common to several packages,
#    	    (e.g. ``mathbb`` or ``slantedGreek``) or a constraint (e.g.
#	    ``literal`` mapping plain characters to upright face).
#
# 8. descriptive _`comments`
#
#    The descriptive comments provide more information about the
#    character, or its specific appearance or use.
#
#    Some descriptions contain references to related commands,
#    marked by a character describing the relation
#
#    :=:  equals  (alias commands),
#    :#:  approx  (similar, different character with same glyph),
#    :x:  not     (false friends and name clashes),
#    :t:  text    (text mode command),
#
#    followed by requirements in parantheses, and
#    delimited by commas.
#
#    Comments in UPPERCASE are Unicode character names
#
# no.^chr^LaTeX^unicode-math^cls^category^requirements^comments
00021^!^!^\exclam^N^mathpunct^^EXCLAMATION MARK
00023^#^\#^\octothorpe^N^mathord^-oz^# \# (oz), NUMBER SIGN
00024^$^\$^\mathdollar^N^mathord^^= \mathdollar, DOLLAR SIGN
00025^%^\%^\percent^N^mathord^^PERCENT SIGN
00026^&^\&^\ampersand^N^mathord^^# \binampersand (stmaryrd)
00028^(^(^\lparen^O^mathopen^^LEFT PARENTHESIS
00029^)^)^\rparen^C^mathclose^^RIGHT PARENTHESIS
0002A^*^*^^N^mathord^^# \ast, (high) ASTERISK, star
0002B^+^+^\plus^V^mathbin^^PLUS SIGN
0002C^,^,^\comma^P^mathpunct^^COMMA
0002D^-^^^N^mathbin^^t -, HYPHEN-MINUS (deprecated for math)
	 */
	
	static {
		try {
			MILDE_SET = TexCharacterSet.readFile(new File(LATEX2UNICODE));
		} catch (Exception e) {
			throw new RuntimeException("Cannot read latex2Unicodefile: "+LATEX2UNICODE, e);
		}

	};

	private Map<String, TexCharacter> characterByLatexMap;
	private Map<String, TexCharacter> characterByUnicodeMathMap;
	private List<TexCharacter> characterList;
	
	public TexCharacterSet() {
		
	}
	
	public static TexCharacterSet readFile(File file) throws Exception {
		TexCharacterSet characterSet = new TexCharacterSet();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		int nchars = 0;
		while ((line = br.readLine()) != null) {
			// skip comments and blank lines
			if (line.startsWith("#") || line.trim().length() == 0) {
				continue;
			}
			characterSet.addCharacter(line);
			nchars++;
		}
		br.close();
		LOG.trace("characters "+nchars + " latex "+characterSet.characterByLatexMap.size()+ " unicodeMath "+characterSet.characterByUnicodeMathMap.size());
		return characterSet;
	}
	
	private void addCharacter(String line) {
		// deal with \^ HORRIBLE 
		boolean caret = false;
		if (line.indexOf("^^\\^") != -1) {
			line = line.replace("^^\\^", $$CARET);
			caret = true;
		}
		String[] tokens = line.split("\\^");
		if (caret) {
			restoreCaret(tokens);
		}
		if (tokens.length != 8) {
			throw new RuntimeException("Must have 8 tokens, found: "+tokens.length+" in: "+line);
		}
		TexCharacter texCharacter = new TexCharacter(tokens);
		addToLatexMap(texCharacter);
		addToUnicodeMathMap(texCharacter);
		ensureCharacterList();
		characterList.add(texCharacter);
	}

	private void ensureCharacterList() {
		if (characterList == null) {
			characterList = new ArrayList<TexCharacter>();
		}
	}

	private void restoreCaret(String[] tokens) {
		for (int i = 0; i < 8; i++) {
			if (tokens[i].equals($$CARET0)) {
				tokens[i] = "\\^";
			}
		}
	}

	private void addToLatexMap(TexCharacter texCharacter) {
		ensureCharacterByLatexMap();
		String latex = texCharacter.getLatex();
		if (!latex.equals("")) {
			if (characterByLatexMap.containsKey(latex)) {
				// this can happen with (say \Gamma). Most duplicates are (say) variant due to italic
				LOG.trace("DUPLICATE: ignored: "+latex + " / "+texCharacter.getUnicodeHex());
			} else {
				characterByLatexMap.put(latex, texCharacter);
			}
		}
	}

	private void ensureCharacterByLatexMap() {
		if (characterByLatexMap == null) {
			characterByLatexMap = new HashMap<String, TexCharacter>();
		}
	}

	private void addToUnicodeMathMap(TexCharacter texCharacter) {
		ensureCharacterByUnicodeMathMap();
		String unicodeMath = texCharacter.getUnicodeMath();
		if (!unicodeMath.equals("")) {
			characterByUnicodeMathMap.put(unicodeMath, texCharacter);
		}
	}

	private void ensureCharacterByUnicodeMathMap() {
		if (characterByUnicodeMathMap == null) {
			characterByUnicodeMathMap = new HashMap<String, TexCharacter>();
		}
	}

	private CodePointSet getCodePointSet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static TexCharacterSet getMildeSet() {
		return MILDE_SET;
	}
	
	public TexCharacter getUnicodeMath(String key) {
		return characterByUnicodeMathMap.get(key);
	}

	public TexCharacter getUnicodeMathOrLatex(String key) {
		TexCharacter texCharacter = getUnicodeMath(key);
		if (texCharacter == null) {
			texCharacter = getLatex(key);
		}
		return texCharacter;
	}

	public TexCharacter getLatex(String key) {
		return characterByLatexMap.get(key);
	}

	public Element createElement() {
		Element element = new Element("characterSet");
		int count = 0;
		for (TexCharacter texCharacter : characterList) {
			Element characterElement = texCharacter.createElement();
			element.appendChild(characterElement);
			count++;
		}
		LOG.debug("added "+count+" characters");
		return element;
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: TextCharacterSet <file>");
			System.exit(0);
		}
		TexCharacterSet mildeSet = getMildeSet();
//		System.out.println(mildeSet.getLatex("\\rightangle"));
//		System.out.println(mildeSet.getUnicodeMath("\\rightangle"));
//		System.out.println(mildeSet.getUnicodeMathOrLatex("\\leftharpoondown"));
////		System.out.println(mildeSet.getUnicodeMathOrLatex("\\Pisymbol{psy}{105}")); // FAILS
////		System.out.println(mildeSet.getUnicodeMathOrLatex("\\bfitGamma")); // FAILS
////		System.out.println(mildeSet.getUnicodeMathOrLatex("\\oldstyle{0}")); // FAILS
//		System.out.println(mildeSet.getUnicodeMathOrLatex("\\natural")); 
//		System.out.println(mildeSet.getUnicodeMathOrLatex("\\Gamma"));
//		System.out.println(mildeSet.getUnicodeMathOrLatex("\\Kappa"));
		XMLUtil.debug(mildeSet.createElement(), new FileOutputStream(LATEX2UNICODE_XML), 1);
	}

}
