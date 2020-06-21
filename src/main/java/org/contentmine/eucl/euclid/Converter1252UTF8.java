package org.contentmine.eucl.euclid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter1252UTF8 {

	private static final String LEADING_CHARS = "âÆËÅÂÃ";
	private static Converter1252UTF8 BUILTIN_CONVERTER;
	private Map<String, Equivalence1252UTF8> equivalenceByMultibyte;
	private Map<String, Equivalence1252UTF8> equivalenceByUnicode;
	private Map<String, Equivalence1252UTF8> equivalenceByC1252;
	
	static {
		BUILTIN_CONVERTER = new Converter1252UTF8();
		BUILTIN_CONVERTER.add("U+20AC","0x80","€","â‚¬",new String[]{"%E2","%82","%AC"});
		BUILTIN_CONVERTER.add("U+201A","0x82","‚","â€š",new String[]{"%E2","%80","%9A"});
		BUILTIN_CONVERTER.add("U+0192","0x83","ƒ","Æ’",new String[]{"%C6","%92"});
		BUILTIN_CONVERTER.add("U+201E","0x84","„","â€ž",new String[]{"%E2","%80","%9E"});
		BUILTIN_CONVERTER.add("U+2026","0x85","…","â€¦",new String[]{"%E2","%80","%A6"});
		BUILTIN_CONVERTER.add("U+2020","0x86","†","â€",new String[]{"%E2","%80","%A0"});
		BUILTIN_CONVERTER.add("U+2021","0x87","‡","â€¡",new String[]{"%E2","%80","%A1"});
		BUILTIN_CONVERTER.add("U+02C6","0x88","ˆ","Ë†",new String[]{"%CB","%86"});
		BUILTIN_CONVERTER.add("U+2030","0x89","‰","â€°",new String[]{"%E2","%80","%B0"});
		BUILTIN_CONVERTER.add("U+0160","0x8A","Š","Å",new String[]{"%C5","%A0"});
		BUILTIN_CONVERTER.add("U+2039","0x8B","‹","â€¹",new String[]{"%E2","%80","%B9"});
		BUILTIN_CONVERTER.add("U+0152","0x8C","Œ","Å’",new String[]{"%C5","%92"});
		BUILTIN_CONVERTER.add("U+017D","0x8E","Ž","Å½",new String[]{"%C5","%BD"});
		BUILTIN_CONVERTER.add("U+2018","0x91","‘","â€˜",new String[]{"%E2","%80","%98"});
		BUILTIN_CONVERTER.add("U+2019","0x92","’","â€™",new String[]{"%E2","%80","%99"});
		BUILTIN_CONVERTER.add("U+201C","0x93","“","â€œ",new String[]{"%E2","%80","%9C"});
		BUILTIN_CONVERTER.add("U+201D","0x94","”","â€",new String[]{"%E2","%80","%9D"});
		BUILTIN_CONVERTER.add("U+2022","0x95","•","â€¢",new String[]{"%E2","%80","%A2"});
		BUILTIN_CONVERTER.add("U+2013","0x96","–","â€“",new String[]{"%E2","%80","%93"});
		BUILTIN_CONVERTER.add("U+2014","0x97","—","â€”",new String[]{"%E2","%80","%94"});
		BUILTIN_CONVERTER.add("U+02DC","0x98","˜","Ëœ",new String[]{"%CB","%9C"});
		BUILTIN_CONVERTER.add("U+2122","0x99","™","â„¢",new String[]{"%E2","%84","%A2"});
		BUILTIN_CONVERTER.add("U+0161","0x9A","š","Å¡",new String[]{"%C5","%A1"});
		BUILTIN_CONVERTER.add("U+203A","0x9B","›","â€º",new String[]{"%E2","%80","%BA"});
		BUILTIN_CONVERTER.add("U+0153","0x9C","œ","Å“",new String[]{"%C5","%93"});
		BUILTIN_CONVERTER.add("U+017E","0x9E","ž","Å¾",new String[]{"%C5","%BE"});
		BUILTIN_CONVERTER.add("U+0178","0x9F","Ÿ","Å¸",new String[]{"%C5","%B8"});
		BUILTIN_CONVERTER.add("U+00A0","0xA0",""+((char)160),"Â",new String[]{"%C2","%A0"});
		BUILTIN_CONVERTER.add("U+00A1","0xA1","¡","Â¡",new String[]{"%C2","%A1"});
		BUILTIN_CONVERTER.add("U+00A2","0xA2","¢","Â¢",new String[]{"%C2","%A2"});
		BUILTIN_CONVERTER.add("U+00A3","0xA3","£","Â£",new String[]{"%C2","%A3"});
		BUILTIN_CONVERTER.add("U+00A4","0xA4","¤","Â¤",new String[]{"%C2","%A4"});
		BUILTIN_CONVERTER.add("U+00A5","0xA5","¥","Â¥",new String[]{"%C2","%A5"});
		BUILTIN_CONVERTER.add("U+00A6","0xA6","¦","Â¦",new String[]{"%C2","%A6"});
		BUILTIN_CONVERTER.add("U+00A7","0xA7","§","Â§",new String[]{"%C2","%A7"});
		BUILTIN_CONVERTER.add("U+00A8","0xA8","¨","Â¨",new String[]{"%C2","%A8"});
		BUILTIN_CONVERTER.add("U+00A9","0xA9","©","Â©",new String[]{"%C2","%A9"});
		BUILTIN_CONVERTER.add("U+00AA","0xAA","ª","Âª",new String[]{"%C2","%AA"});
		BUILTIN_CONVERTER.add("U+00AB","0xAB","«","Â«",new String[]{"%C2","%AB"});
		BUILTIN_CONVERTER.add("U+00AC","0xAC","¬","Â¬",new String[]{"%C2","%AC"});
		BUILTIN_CONVERTER.add("U+00AD","0xAD","­","Â­",new String[]{"%C2","%AD"});
		BUILTIN_CONVERTER.add("U+00AE","0xAE","®","Â®",new String[]{"%C2","%AE"});
		BUILTIN_CONVERTER.add("U+00AF","0xAF","¯","Â¯",new String[]{"%C2","%AF"});
		BUILTIN_CONVERTER.add("U+00B0","0xB0","°","Â°",new String[]{"%C2","%B0"});
		BUILTIN_CONVERTER.add("U+00B1","0xB1","±","Â±",new String[]{"%C2","%B1"});
		BUILTIN_CONVERTER.add("U+00B2","0xB2","²","Â²",new String[]{"%C2","%B2"});
		BUILTIN_CONVERTER.add("U+00B3","0xB3","³","Â³",new String[]{"%C2","%B3"});
		BUILTIN_CONVERTER.add("U+00B4","0xB4","´","Â´",new String[]{"%C2","%B4"});
		BUILTIN_CONVERTER.add("U+00B5","0xB5","µ","Âµ",new String[]{"%C2","%B5"});
		BUILTIN_CONVERTER.add("U+00B6","0xB6","¶","Â¶",new String[]{"%C2","%B6"});
		BUILTIN_CONVERTER.add("U+00B7","0xB7","·","Â·",new String[]{"%C2","%B7"});
		BUILTIN_CONVERTER.add("U+00B8","0xB8","¸","Â¸",new String[]{"%C2","%B8"});
		BUILTIN_CONVERTER.add("U+00B9","0xB9","¹","Â¹",new String[]{"%C2","%B9"});
		BUILTIN_CONVERTER.add("U+00BA","0xBA","º","Âº",new String[]{"%C2","%BA"});
		BUILTIN_CONVERTER.add("U+00BB","0xBB","»","Â»",new String[]{"%C2","%BB"});
		BUILTIN_CONVERTER.add("U+00BC","0xBC","¼","Â¼",new String[]{"%C2","%BC"});
		BUILTIN_CONVERTER.add("U+00BD","0xBD","½","Â½",new String[]{"%C2","%BD"});
		BUILTIN_CONVERTER.add("U+00BE","0xBE","¾","Â¾",new String[]{"%C2","%BE"});
		BUILTIN_CONVERTER.add("U+00BF","0xBF","¿","Â¿",new String[]{"%C2","%BF"});
		BUILTIN_CONVERTER.add("U+00C0","0xC0","À","Ã€",new String[]{"%C3","%80"});
		BUILTIN_CONVERTER.add("U+00C1","0xC1","Á","Ã",new String[]{"%C3","%81"});
		BUILTIN_CONVERTER.add("U+00C2","0xC2","Â","Ã‚",new String[]{"%C3","%82"});
		BUILTIN_CONVERTER.add("U+00C3","0xC3","Ã","Ãƒ",new String[]{"%C3","%83"});
		BUILTIN_CONVERTER.add("U+00C4","0xC4","Ä","Ã„",new String[]{"%C3","%84"});
		BUILTIN_CONVERTER.add("U+00C5","0xC5","Å","Ã…",new String[]{"%C3","%85"});
		BUILTIN_CONVERTER.add("U+00C6","0xC6","Æ","Ã†",new String[]{"%C3","%86"});
		BUILTIN_CONVERTER.add("U+00C7","0xC7","Ç","Ã‡",new String[]{"%C3","%87"});
		BUILTIN_CONVERTER.add("U+00C8","0xC8","È","Ãˆ",new String[]{"%C3","%88"});
		BUILTIN_CONVERTER.add("U+00C9","0xC9","É","Ã‰",new String[]{"%C3","%89"});
		BUILTIN_CONVERTER.add("U+00CA","0xCA","Ê","ÃŠ",new String[]{"%C3","%8A"});
		BUILTIN_CONVERTER.add("U+00CB","0xCB","Ë","Ã‹",new String[]{"%C3","%8B"});
		BUILTIN_CONVERTER.add("U+00CC","0xCC","Ì","ÃŒ",new String[]{"%C3","%8C"});
		BUILTIN_CONVERTER.add("U+00CD","0xCD","Í","Ã",new String[]{"%C3","%8D"});
		BUILTIN_CONVERTER.add("U+00CE","0xCE","Î","ÃŽ",new String[]{"%C3","%8E"});
		BUILTIN_CONVERTER.add("U+00CF","0xCF","Ï","Ã",new String[]{"%C3","%8F"});
		BUILTIN_CONVERTER.add("U+00D0","0xD0","Ð","Ã",new String[]{"%C3","%90"});
		BUILTIN_CONVERTER.add("U+00D1","0xD1","Ñ","Ã‘",new String[]{"%C3","%91"});
		BUILTIN_CONVERTER.add("U+00D2","0xD2","Ò","Ã’",new String[]{"%C3","%92"});
		BUILTIN_CONVERTER.add("U+00D3","0xD3","Ó","Ã“",new String[]{"%C3","%93"});
		BUILTIN_CONVERTER.add("U+00D4","0xD4","Ô","Ã”",new String[]{"%C3","%94"});
		BUILTIN_CONVERTER.add("U+00D5","0xD5","Õ","Ã•",new String[]{"%C3","%95"});
		BUILTIN_CONVERTER.add("U+00D6","0xD6","Ö","Ã–",new String[]{"%C3","%96"});
		BUILTIN_CONVERTER.add("U+00D7","0xD7","×","Ã—",new String[]{"%C3","%97"});
		BUILTIN_CONVERTER.add("U+00D8","0xD8","Ø","Ã˜",new String[]{"%C3","%98"});
		BUILTIN_CONVERTER.add("U+00D9","0xD9","Ù","Ã™",new String[]{"%C3","%99"});
		BUILTIN_CONVERTER.add("U+00DA","0xDA","Ú","Ãš",new String[]{"%C3","%9A"});
		BUILTIN_CONVERTER.add("U+00DB","0xDB","Û","Ã›",new String[]{"%C3","%9B"});
		BUILTIN_CONVERTER.add("U+00DC","0xDC","Ü","Ãœ",new String[]{"%C3","%9C"});
		BUILTIN_CONVERTER.add("U+00DD","0xDD","Ý","Ã",new String[]{"%C3","%9D"});
		BUILTIN_CONVERTER.add("U+00DE","0xDE","Þ","Ãž",new String[]{"%C3","%9E"});
		BUILTIN_CONVERTER.add("U+00DF","0xDF","ß","ÃŸ",new String[]{"%C3","%9F"});
		BUILTIN_CONVERTER.add("U+00E0","0xE0","à","Ã",new String[]{"%C3","%A0"});
		BUILTIN_CONVERTER.add("U+00E1","0xE1","á","Ã¡",new String[]{"%C3","%A1"});
		BUILTIN_CONVERTER.add("U+00E2","0xE2","â","Ã¢",new String[]{"%C3","%A2"});
		BUILTIN_CONVERTER.add("U+00E3","0xE3","ã","Ã£",new String[]{"%C3","%A3"});
		BUILTIN_CONVERTER.add("U+00E4","0xE4","ä","Ã¤",new String[]{"%C3","%A4"});
		BUILTIN_CONVERTER.add("U+00E5","0xE5","å","Ã¥",new String[]{"%C3","%A5"});
		BUILTIN_CONVERTER.add("U+00E6","0xE6","æ","Ã¦",new String[]{"%C3","%A6"});
		BUILTIN_CONVERTER.add("U+00E7","0xE7","ç","Ã§",new String[]{"%C3","%A7"});
		BUILTIN_CONVERTER.add("U+00E8","0xE8","è","Ã¨",new String[]{"%C3","%A8"});
		BUILTIN_CONVERTER.add("U+00E9","0xE9","é","Ã©",new String[]{"%C3","%A9"});
		BUILTIN_CONVERTER.add("U+00EA","0xEA","ê","Ãª",new String[]{"%C3","%AA"});
		BUILTIN_CONVERTER.add("U+00EB","0xEB","ë","Ã«",new String[]{"%C3","%AB"});
		BUILTIN_CONVERTER.add("U+00EC","0xEC","ì","Ã¬",new String[]{"%C3","%AC"});
		BUILTIN_CONVERTER.add("U+00ED","0xED","í","Ã­",new String[]{"%C3","%AD"});
		BUILTIN_CONVERTER.add("U+00EE","0xEE","î","Ã®",new String[]{"%C3","%AE"});
		BUILTIN_CONVERTER.add("U+00EF","0xEF","ï","Ã¯",new String[]{"%C3","%AF"});
		BUILTIN_CONVERTER.add("U+00F0","0xF0","ð","Ã°",new String[]{"%C3","%B0"});
		BUILTIN_CONVERTER.add("U+00F1","0xF1","ñ","Ã±",new String[]{"%C3","%B1"});
		BUILTIN_CONVERTER.add("U+00F2","0xF2","ò","Ã²",new String[]{"%C3","%B2"});
		BUILTIN_CONVERTER.add("U+00F3","0xF3","ó","Ã³",new String[]{"%C3","%B3"});
		BUILTIN_CONVERTER.add("U+00F4","0xF4","ô","Ã´",new String[]{"%C3","%B4"});
		BUILTIN_CONVERTER.add("U+00F5","0xF5","õ","Ãµ",new String[]{"%C3","%B5"});
		BUILTIN_CONVERTER.add("U+00F6","0xF6","ö","Ã¶",new String[]{"%C3","%B6"});
		BUILTIN_CONVERTER.add("U+00F7","0xF7","÷","Ã·",new String[]{"%C3","%B7"});
		BUILTIN_CONVERTER.add("U+00F8","0xF8","ø","Ã¸",new String[]{"%C3","%B8"});
		BUILTIN_CONVERTER.add("U+00F9","0xF9","ù","Ã¹",new String[]{"%C3","%B9"});
		BUILTIN_CONVERTER.add("U+00FA","0xFA","ú","Ãº",new String[]{"%C3","%BA"});
		BUILTIN_CONVERTER.add("U+00FB","0xFB","û","Ã»",new String[]{"%C3","%BB"});
		BUILTIN_CONVERTER.add("U+00FC","0xFC","ü","Ã¼",new String[]{"%C3","%BC"});
		BUILTIN_CONVERTER.add("U+00FD","0xFD","ý","Ã½",new String[]{"%C3","%BD"});
		BUILTIN_CONVERTER.add("U+00FE","0xFE","þ","Ã¾",new String[]{"%C3","%BE"});
		BUILTIN_CONVERTER.add("U+00FF","0xFF","ÿ","Ã¿",new String[]{"%C3","%BF"});	
		// LRM non-printing character from Wikidata , convert to empty
		BUILTIN_CONVERTER.add("U+200E",String.valueOf((char)8206),"","â€Ž",new String[]{"%E2","%80","%17B"});	// dec 8206
	}
	
	
	public static Converter1252UTF8 getBuiltinConverter() {
		return 	BUILTIN_CONVERTER;
	}
	
	public static String getLeadingCharacters() {
		return LEADING_CHARS;
	}
	
	private void add(String unicode, String c1252, String utf8, String multibyte, String[] hexcodes) {
		Equivalence1252UTF8 equivalence = new Equivalence1252UTF8(unicode, c1252, utf8, multibyte, hexcodes);
		this.add(equivalence);
	}

	private void add(Equivalence1252UTF8 equivalence) {
		ensureMaps();
		equivalenceByMultibyte.put(equivalence.getMultibyte(), equivalence);
		equivalenceByUnicode.put(equivalence.getUnicode(), equivalence);
		equivalenceByC1252.put(equivalence.getC1252(), equivalence);
		
	}

	private void ensureMaps() {
		if (equivalenceByMultibyte == null) {
			equivalenceByMultibyte = new HashMap<>();
			equivalenceByUnicode = new HashMap<>();
			equivalenceByC1252 = new HashMap<>();
		}
	}

	/** mojibake
	 * 
	 * http://www.i18nqa.com/debug/utf8-debug.html
	 * converts 2- or 3-character substrings to UTF-8. 
	 * This is a last resort . The encoding has got screwed and should 
	 * be fixed rather than use this.
	 */
/*
Table for Debugging Common UTF-8 Character Encoding Problems.
Code Point	Characters	UTF-8 Bytes	 	Code Point	Characters	UTF-8 Bytes
Unicode	Windows
1252	Expected	Actual	Unicode	Windows
1252	Expected	Actual
U+20AC	0x80	€	â‚¬	%E2 %82 %AC	 	U+00C0	0xC0	À	Ã€	%C3 %80
0x81				 	U+00C1	0xC1	Á	Ã	%C3 %81
U+201A	0x82	‚	â€š	%E2 %80 %9A	 	U+00C2	0xC2	Â	Ã‚	%C3 %82
U+0192	0x83	ƒ	Æ’	%C6 %92	 	U+00C3	0xC3	Ã	Ãƒ	%C3 %83
U+201E	0x84	„	â€ž	%E2 %80 %9E	 	U+00C4	0xC4	Ä	Ã„	%C3 %84
U+2026	0x85	…	â€¦	%E2 %80 %A6	 	U+00C5	0xC5	Å	Ã…	%C3 %85
U+2020	0x86	†	â€	%E2 %80 %A0	 	U+00C6	0xC6	Æ	Ã†	%C3 %86
U+2021	0x87	‡	â€¡	%E2 %80 %A1	 	U+00C7	0xC7	Ç	Ã‡	%C3 %87
U+02C6	0x88	ˆ	Ë†	%CB %86	 	U+00C8	0xC8	È	Ãˆ	%C3 %88
U+2030	0x89	‰	â€°	%E2 %80 %B0	 	U+00C9	0xC9	É	Ã‰	%C3 %89
U+0160	0x8A	Š	Å	%C5 %A0	 	U+00CA	0xCA	Ê	ÃŠ	%C3 %8A
U+2039	0x8B	‹	â€¹	%E2 %80 %B9	 	U+00CB	0xCB	Ë	Ã‹	%C3 %8B
U+0152	0x8C	Œ	Å’	%C5 %92	 	U+00CC	0xCC	Ì	ÃŒ	%C3 %8C
0x8D				 	U+00CD	0xCD	Í	Ã	%C3 %8D
U+017D	0x8E	Ž	Å½	%C5 %BD	 	U+00CE	0xCE	Î	ÃŽ	%C3 %8E
0x8F				 	U+00CF	0xCF	Ï	Ã	%C3 %8F
0x90				 	U+00D0	0xD0	Ð	Ã	%C3 %90
U+2018	0x91	‘	â€˜	%E2 %80 %98	 	U+00D1	0xD1	Ñ	Ã‘	%C3 %91
U+2019	0x92	’	â€™	%E2 %80 %99	 	U+00D2	0xD2	Ò	Ã’	%C3 %92
U+201C	0x93	“	â€œ	%E2 %80 %9C	 	U+00D3	0xD3	Ó	Ã“	%C3 %93
U+201D	0x94	”	â€	%E2 %80 %9D	 	U+00D4	0xD4	Ô	Ã”	%C3 %94
U+2022	0x95	•	â€¢	%E2 %80 %A2	 	U+00D5	0xD5	Õ	Ã•	%C3 %95
U+2013	0x96	–	â€“	%E2 %80 %93	 	U+00D6	0xD6	Ö	Ã–	%C3 %96
U+2014	0x97	—	â€”	%E2 %80 %94	 	U+00D7	0xD7	×	Ã—	%C3 %97
U+02DC	0x98	˜	Ëœ	%CB %9C	 	U+00D8	0xD8	Ø	Ã˜	%C3 %98
U+2122	0x99	™	â„¢	%E2 %84 %A2	 	U+00D9	0xD9	Ù	Ã™	%C3 %99
U+0161	0x9A	š	Å¡	%C5 %A1	 	U+00DA	0xDA	Ú	Ãš	%C3 %9A
U+203A	0x9B	›	â€º	%E2 %80 %BA	 	U+00DB	0xDB	Û	Ã›	%C3 %9B
U+0153	0x9C	œ	Å“	%C5 %93	 	U+00DC	0xDC	Ü	Ãœ	%C3 %9C
0x9D				 	U+00DD	0xDD	Ý	Ã	%C3 %9D
U+017E	0x9E	ž	Å¾	%C5 %BE	 	U+00DE	0xDE	Þ	Ãž	%C3 %9E
U+0178	0x9F	Ÿ	Å¸	%C5 %B8	 	U+00DF	0xDF	ß	ÃŸ	%C3 %9F
U+00A0	0xA0		Â	%C2 %A0	 	U+00E0	0xE0	à	Ã	%C3 %A0
U+00A1	0xA1	¡	Â¡	%C2 %A1	 	U+00E1	0xE1	á	Ã¡	%C3 %A1
U+00A2	0xA2	¢	Â¢	%C2 %A2	 	U+00E2	0xE2	â	Ã¢	%C3 %A2
U+00A3	0xA3	£	Â£	%C2 %A3	 	U+00E3	0xE3	ã	Ã£	%C3 %A3
U+00A4	0xA4	¤	Â¤	%C2 %A4	 	U+00E4	0xE4	ä	Ã¤	%C3 %A4
U+00A5	0xA5	¥	Â¥	%C2 %A5	 	U+00E5	0xE5	å	Ã¥	%C3 %A5
U+00A6	0xA6	¦	Â¦	%C2 %A6	 	U+00E6	0xE6	æ	Ã¦	%C3 %A6
U+00A7	0xA7	§	Â§	%C2 %A7	 	U+00E7	0xE7	ç	Ã§	%C3 %A7
U+00A8	0xA8	¨	Â¨	%C2 %A8	 	U+00E8	0xE8	è	Ã¨	%C3 %A8
U+00A9	0xA9	©	Â©	%C2 %A9	 	U+00E9	0xE9	é	Ã©	%C3 %A9
U+00AA	0xAA	ª	Âª	%C2 %AA	 	U+00EA	0xEA	ê	Ãª	%C3 %AA
U+00AB	0xAB	«	Â«	%C2 %AB	 	U+00EB	0xEB	ë	Ã«	%C3 %AB
U+00AC	0xAC	¬	Â¬	%C2 %AC	 	U+00EC	0xEC	ì	Ã¬	%C3 %AC
U+00AD	0xAD	­	Â­	%C2 %AD	 	U+00ED	0xED	í	Ã­	%C3 %AD
U+00AE	0xAE	®	Â®	%C2 %AE	 	U+00EE	0xEE	î	Ã®	%C3 %AE
U+00AF	0xAF	¯	Â¯	%C2 %AF	 	U+00EF	0xEF	ï	Ã¯	%C3 %AF
U+00B0	0xB0	°	Â°	%C2 %B0	 	U+00F0	0xF0	ð	Ã°	%C3 %B0
U+00B1	0xB1	±	Â±	%C2 %B1	 	U+00F1	0xF1	ñ	Ã±	%C3 %B1
U+00B2	0xB2	²	Â²	%C2 %B2	 	U+00F2	0xF2	ò	Ã²	%C3 %B2
U+00B3	0xB3	³	Â³	%C2 %B3	 	U+00F3	0xF3	ó	Ã³	%C3 %B3
U+00B4	0xB4	´	Â´	%C2 %B4	 	U+00F4	0xF4	ô	Ã´	%C3 %B4
U+00B5	0xB5	µ	Âµ	%C2 %B5	 	U+00F5	0xF5	õ	Ãµ	%C3 %B5
U+00B6	0xB6	¶	Â¶	%C2 %B6	 	U+00F6	0xF6	ö	Ã¶	%C3 %B6
U+00B7	0xB7	·	Â·	%C2 %B7	 	U+00F7	0xF7	÷	Ã·	%C3 %B7
U+00B8	0xB8	¸	Â¸	%C2 %B8	 	U+00F8	0xF8	ø	Ã¸	%C3 %B8
U+00B9	0xB9	¹	Â¹	%C2 %B9	 	U+00F9	0xF9	ù	Ã¹	%C3 %B9
U+00BA	0xBA	º	Âº	%C2 %BA	 	U+00FA	0xFA	ú	Ãº	%C3 %BA
U+00BB	0xBB	»	Â»	%C2 %BB	 	U+00FB	0xFB	û	Ã»	%C3 %BB
U+00BC	0xBC	¼	Â¼	%C2 %BC	 	U+00FC	0xFC	ü	Ã¼	%C3 %BC
U+00BD	0xBD	½	Â½	%C2 %BD	 	U+00FD	0xFD	ý	Ã½	%C3 %BD
U+00BE	0xBE	¾	Â¾	%C2 %BE	 	U+00FE	0xFE	þ	Ã¾	%C3 %BE
U+00BF	0xBF	¿	Â¿	%C2 %BF	 	U+00FF	0xFF	ÿ	Ã¿	%C3 %BF
*/
	public static String convertMultibyteToUTF8(String multibyte) {

		Equivalence1252UTF8 equivalence = Converter1252UTF8.getBuiltinConverter().getEquivalenceByMultibyte(multibyte);
		return equivalence == null ? null : equivalence.getUTF8();
	}

	private Equivalence1252UTF8 getEquivalenceByMultibyte(String multibyte) {
		return equivalenceByMultibyte.get(multibyte);
	}
		
	private Equivalence1252UTF8 getEquivalenceByUnicode(String unicode) {
		return equivalenceByUnicode.get(unicode);
	}
		
	private Equivalence1252UTF8 getEquivalenceByC1252(String c1252) {
		return equivalenceByC1252.get(c1252);
	}
		
}
	
	

	/** holds conversion to/from UTF8 and CP1252
	 * 
	 * 
	 * @author pm286
	 *
	 */
class Equivalence1252UTF8 {
	public String getUnicode() {
			return unicode;
		}

		public String getC1252() {
			return c1252;
		}

		public String getUTF8() {
			return utf8;
		}

		public String getMultibyte() {
			return multibyte;
		}

		public List<String> getHexcodes() {
			return hexcodes;
		}

	private String unicode;
	private String c1252;
	private String utf8;
	private String multibyte;
	private List<String> hexcodes;

	public Equivalence1252UTF8(String unicode, String c1252, String single, String multibyte, String[] hexcodes) {
		this.unicode = unicode;
		this.c1252 = c1252;
		this.utf8 = single;
		this.multibyte = multibyte;
		this.hexcodes = new ArrayList<>(Arrays.asList(hexcodes));
	}


}
