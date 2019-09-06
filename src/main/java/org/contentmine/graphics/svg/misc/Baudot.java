package org.contentmine.graphics.svg.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;

public class Baudot {

	private static Code[] CODES = {
			new Code("Null", "00000"),
			new Code("CR",   "00010"),
			new Code("LF",   "01000"),
			new Code(" ",   "00100"),
			new Code("Q",    "11101"),
			new Code("W",    "11001"),
			new Code("E",    "10000"),
			new Code("R",    "01010"),
			new Code("T",    "00001"),
			new Code("Y",    "10101"),
			new Code("U",    "00111"),
			new Code("I",    "00110"),
			new Code("O",    "11000"),
			new Code("P",    "10110"),
			new Code("1",    "11101"),
			new Code("2",    "11001"),
			new Code("3",    "10000"),
			new Code("4",    "01010"),
			new Code("5",    "00001"),
			new Code("6",    "10101"),
			new Code("7",    "00111"),
			new Code("8",    "00110"),
			new Code("9",    "11000"),
			new Code("0",    "10110"),
			new Code("A",    "00011"),
			new Code("S",    "00101"),
			new Code("D",    "01001"),
			new Code("F",    "01101"),
			new Code("G",    "11010"),
			new Code("H",    "10100"),
			new Code("J",    "01011"),
			new Code("K",    "01111"),
			new Code("L",    "10010"),
			new Code("Z",    "10001"),
			new Code("X",    "10111"),
			new Code("C",    "01110"),
			new Code("V",    "11110"),
			new Code("B",    "11001"),
			new Code("A",    "11101"),
			new Code("N",    "01100"),
			new Code("M",    "11100"),
			new Code("SHIFT","11011"),
			new Code("ERASE","11111"),
	};
	
	public final static List<Code> CODE_LIST = Arrays.asList(CODES);

	double deltax = 30.0;
	double deltay = deltax;

	public Baudot() {
		
	}
	
	public Code getCode(String ch) {
		for (Code code : CODE_LIST) {
			if (code.character.equals(ch)) {
				return code;
			}
		}
		return null;
	}

	public SVGG getSVGElement(List<String> chars) {
		SVGG gg = new SVGG();
		double y = 0 + deltay*0.2;
		for (String ch : chars) {
			Code code = this.getCode(ch);
			gg.appendChild(code.getSVG(deltax, y));
			y += deltay;
		}
		// surrounding rect
		double holesWidth = 5*deltax;
		double namesWidth = 2.5*deltax;
		double tapeWidth = holesWidth + namesWidth;
		double tapeBottom = y+deltay;
		Real2 tapeBox = new Real2(tapeWidth, tapeBottom);
		SVGRect rect = new SVGRect(new Real2(0, 0), tapeBox);
		String rectStyle = "fill:none;stroke:black;stroke-width:1.0;";
		rect.setCSSStyle(rectStyle);
		gg.appendChild(rect);
		
		double standHeight = holesWidth + namesWidth + holesWidth;
		createStandAndSlot(gg, standHeight, tapeWidth, tapeBottom, rectStyle);
		return gg;
	}

	private void createStandAndSlot(AbstractCMElement gg, double standHeight, double tapeWidth, double tapeBottom,
			String rectStyle) {
		double standTopY = tapeBottom + deltay;
		double standWidth = tapeWidth;
		double standBottom = standTopY + standHeight;
		SVGRect stand = new SVGRect(new Real2(0, standTopY), new Real2(standWidth, standBottom));
		stand.setCSSStyle(rectStyle);
		gg.appendChild(stand);
		double slotWidth = 10.8; // this is the thickness of the acrylic; // trial and error
		double standSlotX = tapeWidth / 2. - slotWidth / 2.;
		double standSlotY = standTopY + standHeight / 2. - tapeWidth / 2.;
		SVGRect standSlot = new SVGRect(new Real2(standSlotX, standSlotY), new Real2(standSlotX + slotWidth, standSlotY + tapeWidth));
		standSlot.setCSSStyle(rectStyle);
		gg.appendChild(standSlot);
	}
	
	public static void main(String[] args) {
		example1();
		example2();
	}

	private static void example2() {
		AbstractCMElement g = new SVGG();
		Baudot baudot = new Baudot();
		
		baudot.createSVG("LAURA TOM BBB 2017", new File("target/baudot/lauratom1.svg"));
		baudot.createSVG("JUDITH PETER BBB 2017", new File("target/baudot/judithpeter.svg"));
		baudot.createSVG("ALAN BBB 2017", new File("target/baudot/alan.svg"));
		baudot.createSVG("SUE BBB 2017", new File("target/baudot/sue.svg"));
		baudot.createSVG("LIZ NEIL BBB 2017", new File("target/baudot/lizneil.svg"));
		baudot.createSVG("TERESA DAVE BBB 2017", new File("target/baudot/daveteresa.svg"));

		double x = 250.0;
		double delta = 250.0;
		SVGG gg = baudot.getSVGElement("LAURA TOM BBB 2017");
		g.appendChild(gg);
		gg = baudot.getSVGElement("JUDITH PETER BBB 2017");
		gg.setTransform(new Transform2(new Vector2(x, 0.0)));
		g.appendChild(gg);
		gg = baudot.getSVGElement("ALAN BBB 2017");
		x += delta;
		gg.setTransform(new Transform2(new Vector2(x, 0.0)));
		g.appendChild(gg);
		gg = baudot.getSVGElement("SUE BBB 2017");
		x += delta;
		gg.setTransform(new Transform2(new Vector2(x, 0.0)));
		g.appendChild(gg);
		gg = baudot.getSVGElement("LIZ NEIL BBB 2017");
		x += delta;
		gg.setTransform(new Transform2(new Vector2(x, 0.0)));
		g.appendChild(gg);
		gg = baudot.getSVGElement("TERESA DAVE BBB 2017");
		x += delta;
		gg.setTransform(new Transform2(new Vector2(x, 0.0)));
		g.appendChild(gg);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/baudot/edinburgh.svg"), x + delta, 1200.0);
	}

	private static Baudot example1() {
		Baudot baudot = new Baudot();
		System.out.println(baudot.getCode("C"));
		SVGG g = baudot.getCode("C").getSVG(10., 20.);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/baudot/c.svg"));
		g = baudot.getCode("ERASE").getSVG(15., 30.);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/baudot/erase.svg"));
		String[] ss = {"T", "I", "M", " ", "C", "A", "T", "H", "E", "R", "I", "N", "E", " ", "2", "0", "1", "7"};
		g = baudot.getSVGElement(Arrays.asList(ss));
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/baudot/cat.svg"));
		ss =  new String[]{"J", "U", "D", "I", "T", "H", " ", "P", "E", "T", "E", "R", " ", "2", "0", "1", "7"};
		g = baudot.getSVGElement(Arrays.asList(ss));
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/baudot/jp.svg"));
		ss =  new String[]{"C", "A", "R", "O", "L", " ", "7", "0", "T", "H", " ", "2", "0", "1", "7"};
		g = baudot.getSVGElement(Arrays.asList(ss));
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/baudot/carol2.svg"));
		String s =  "DAVE 40TH 2017";
		g = baudot.getSVGElement(s);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/baudot/david.svg"));
		return baudot;
	}

	private AbstractCMElement createSVG(String s, File outfile) {
		SVGG g = this.getSVGElement(s);
		SVGSVG.wrapAndWriteAsSVG(g, outfile);
		return g;
	}

	private SVGG getSVGElement(String s) {
		List<String> ss = new ArrayList<String>();
		for (int i = 0; i < s.length(); i++) {
			ss.add(String.valueOf(s.charAt(i)));
		}
		return getSVGElement(ss);
	}
	
	
}
class Code {
	String holes;
	String character;
	double x;
	double deltax;

	public Code(String character, String holes) {
		this.character = character;
		this.holes = holes;
	}
	
	public SVGG getSVG(double deltax, double y) {
		String FILL = "black";
		String STROKE = "none";
		this.deltax = deltax;
		SVGG g = new SVGG();
		System.out.println(">>"+holes);
		x = 0.0 + 0.2*deltax;
		for (int i = 0; i < 5; i++) {
			char ch = holes.charAt(i);
			if (ch == '1') {
				g.appendChild(drawCircle(y, deltax/3.));
			}
			x += deltax;
			if (i == 2) {
				x -= deltax/6.;
				g.appendChild(drawCircle(y, deltax/5.));
				x += 5.*deltax/6.;
			}
		}
		x += deltax*0.3;
		String font = "Arial";
		if (character.equals("I")) x += deltax*0.2;
		SVGText t = new SVGText(new Real2(x+(deltax*0.3), y+deltax*0.85), character);
		t.setCSSStyle("font-family:" + font + ";font-size:"+(deltax*0.85)+";fill:"+FILL+";stroke:"+STROKE+";font-weight:bold;");
		g.appendChild(t);
		return g;
	}
	
	public SVGCircle drawCircle(double y, double r) {
		SVGCircle circle = new SVGCircle(new Real2(x + deltax/2, y+deltax/2), r);
		circle.setCSSStyle("fill:none;stroke:black;stroke-width:1.5;");
		return circle;
	}
	
	public String toString() {
		return character+":"+holes;
	}
}
class Hole8 {
	// https://www.staff.ncl.ac.uk/roger.broughton/museum/iomedia/pt1.htm
	
	/**
	 * 
58  _ _   654. 2 	The first column is the numerical value of the holes punched.
46  - *   6 4.32 	The second column is NORMAL shift character printed.
60  ; ;   654.3  	The third column is the SHIFT case character printed.
15  / :     4.321
63  . ,   654.321   The first eight characters are special characters.
43  £ 10  6 4. 21   It looks as though the 5th hole is punched
125 ->   7654.3 1   to make an even number of holes.

48  0 ^   65 .      The next ten are the numeric characters 0 - 9.
33  1 [   6  .  1   
34  2 ]   6  . 2    There are no 8 or 7 holes.
51  3 <   65 . 21   There is a 6 hole.
36  4 >   6  .3     The last four positions is binary from 0 - 9.
53  5 =   65 .3 1   The 5th hole is punched to make even parity.
54  6 x   65 .32 
39  7 :-  6  .321
40  8 (   6 4.   
57  9 )   654.  1

65  A a  7   .  1   The next 26 are the characters A - Z.
66  B b  7   . 2    
83  C c  7 5 . 21   The first fifteen are the characters A - O.
68  D d  7   .3     The last four are binary 1 - 15.
85  E e  7 5 .3 1   Once again the 5th is punched to make even parity.
86  F f  7 5 .32 
71  G g  7   .321
72  H h  7  4.   
89  I i  7 54.  1
90  J j  7 54. 2 
75  K k  7  4. 21
92  L l  7 54.3  
77  M m  7  4.3 1
78  N n  7  4.32 
95  O o  7 54.321
96  P p  76  .      The last eleven are characters P - Z.
113 Q q  765 .  1   They have a 7 and 6 punch.
114 R r  765 . 2    The last 4 positions are binary 0 - 10.
99  S s  76  . 21   Once again the 5th is punched to make even parity.
116 T t  765 .3  
101 U u  76  .3 1
102 V v  76  .32 
119 W w  765 .321
120 X x  7654.   
105 Y y  76 4.  1
106 Z z  76 4. 2 
*/
	
	
	
}
