package org.contentmine.svg2xml.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class SVG2XMLFontIT {
	private static final Logger LOG = Logger.getLogger(SVG2XMLFontIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
		/**
		 * list a few common fonts and metrics.
		 * not sure I believe all of them.
		 */
		public void testGetFontMetricsAll() {
			String[] fontNames = {
		    Font.SERIF,
		    Font.SANS_SERIF,
		    Font.MONOSPACED,
		    "Arial",
		    "Arial Black",
		    "Arial Rounded MT Bold",
		    "Arial Unicode MS",
		    "Arial Narrow",
		    "Cambria Math",
		    "Courier New",
		    "Dialog", // equiv to SansSerif?
		    "Garamond",
		    "Gill", // equiv to SansSerif?
		    "Lucida", // equiv to SansSerif?
		    "Symbol",
		    "Times New Roman",
		    "Wingdings",
		    "Wingdings 2",
		    "Wingdings 3",
	
		    /* These were on my Windows machine
	 Agency FB, Aharoni, Algerian, Andalus, Angsana New, AngsanaUPC, Aparajita, Arabic Typesetting
	 Arial Black, Arial Rounded MT Bold, Arial Unicode MS, Baskerville Old Face
	 Batang, BatangChe, Bauhaus 93, , Bell MT, Berlin Sans FB, Berlin Sans FB Demi, Bernard MT Condensed, Blackadder ITC
	 Bodoni MT, Bodoni MT Black, Bodoni MT Condensed, Bodoni MT Poster Compressed
	 Book Antiqua, Bookman Old Style, Bookshelf Symbol 7, Bradley Hand ITC
	 Britannic Bold, Broadway, Browallia New, BrowalliaUPC, Brush Script MT, Calibri, Calibri Light, Californian FB
	 Calisto MT, Cambria, Cambria Math, Candara, Castellar, Centaur, Century, Century Gothic
	 Century Schoolbook, Chiller, Colonna MT, Comic Sans MS, Consolas, Constantia, Cooper Black, Copperplate Gothic Bold
	 Copperplate Gothic Light,, Corbel, Cordia New, CordiaUPC, Curlz MT, DaunPenh, David, DFKai-SB, Dialog, DialogInput, DilleniaUPC, DokChampa
	 Dotum, DotumChe, Ebrima, Edwardian Script ITC, Elephant, Engravers MT, Eras Bold ITC, Eras Demi ITC
	 Eras Light ITC, Eras Medium ITC, Estrangelo Edessa, EucrosiaUPC, Euphemia, FangSong, Felix Titling, Footlight MT Light
	 Forte, Franklin Gothic Book, Franklin Gothic Demi, Franklin Gothic Demi Cond
	 Franklin Gothic Heavy, Franklin Gothic Medium, Franklin Gothic Medium Cond, FrankRuehl
	 FreesiaUPC, Freestyle Script, French Script MT, Gabriola
	 Garamond, Gautami, Georgia, Gigi, Gill Sans MT, Gill Sans MT Condensed, Gill Sans MT Ext Condensed Bold, Gill Sans Ultra Bold
	 Gill Sans Ultra Bold Condensed, Gisha, Gloucester MT Extra Condensed, Goudy Old Style
	 Goudy Stout, Gulim, GulimChe, Gungsuh, GungsuhChe, Haettenschweiler, Harlow Solid Italic, Harrington
	 High Tower Text, Impact, Imprint MT Shadow, Informal Roman, IrisUPC, Iskoola Pota, JasmineUPC, Jokerman
	 Juice ITC, KaiTi, Kalinga, Kartika, Khmer UI, KodchiangUPC, Kokila, Kristen ITC
	 Kunstler Script, Lao UI, Latha, Leelawadee, Levenim MT, LilyUPC, Lucida Bright, Lucida Calligraphy
	 Lucida Console, Lucida Fax, Lucida Handwriting, Lucida Sans, Lucida Sans Typewriter, Lucida Sans Unicode, Magneto, Maiandra GD
	 Malgun Gothic, Mangal, Marlett, Matura MT Script Capitals, Meiryo, Meiryo UI, Microsoft Himalaya, Microsoft JhengHei
	 Microsoft New Tai Lue, Microsoft PhagsPa, Microsoft Sans Serif, Microsoft Tai Le
	 Microsoft Uighur, Microsoft YaHei, Microsoft Yi Baiti, MingLiU, MingLiU-ExtB, MingLiU_HKSCS, MingLiU_HKSCS-ExtB, Miriam
	 Miriam Fixed, Mistral, Modern No. 20, Mongolian Baiti, Monospaced, Monotype Corsiva, MoolBoran, MS Gothic
	 MS Mincho, MS Outlook, MS PGothic, MS PMincho, MS Reference Sans Serif, MS Reference Specialty, MS UI Gothic, MT Extra
	 MV Boli, Narkisim, Niagara Engraved, Niagara Solid, NSimSun, Nyala, OCR A Extended, Old English Text MT
	 Onyx, Palace Script MT, Palatino Linotype, Papyrus, Parchment, Perpetua, Perpetua Titling MT, Plantagenet Cherokee
	 Playbill, PMingLiU, PMingLiU-ExtB, Poor Richard, Pristina, Raavi, Rage Italic, Ravie
	 Rockwell, Rockwell Condensed, Rockwell Extra Bold, Rod, Sakkal Majalla, SansSerif, Script MT Bold, Segoe Print
	 Segoe Script, Segoe UI, Segoe UI Light, Segoe UI Semibold, Segoe UI Symbol, Serif, Shonar Bangla, Showcard Gothic
	 Shruti, SimHei, Simplified Arabic, Simplified Arabic Fixed, SimSun, SimSun-ExtB, Snap ITC, Stencil
	 SWGamekeys MT, Sylfaen, Symbol, Tahoma, Tempus Sans ITC, Times New Roman, Traditional Arabic, Trebuchet MS
	 Tunga, Tw Cen MT, Tw Cen MT Condensed, Tw Cen MT Condensed Extra Bold, Utsaah, Vani, Verdana, Vijaya
	 Viner Hand ITC, Vivaldi, Vladimir Script, Vrinda, Webdings, Wide Latin, Wingdings, Wingdings 2, Wingdings 3
	    	    */
			};
	
			for (int i = 0; i < fontNames.length; i++) {
				Font font = new Font(fontNames[i], Font.PLAIN, 1000);
				BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_BINARY);
				Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
				FontMetrics fontMetrics = g2d.getFontMetrics(font);
				int[] ww = fontMetrics.getWidths();
				LOG.trace("========="+fontNames[i]+"==============");
				Assert.assertEquals("nchars", 256, ww.length);
				for (int j = 70; j < 80; j++) {
					LOG.trace(j+": "+(char)j+" "+ww[j]);
				}
				LOG.trace("===================================");
			}
		}

	
	@Test
	/** lists system fonts.
	 * 
	 */
	public void testDisplayFonts() {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontFamilyNames = graphicsEnvironment.getAvailableFontFamilyNames();
		for (String fontFamilyName : fontFamilyNames) {
			LOG.trace(fontFamilyName);
		}
	}

}
