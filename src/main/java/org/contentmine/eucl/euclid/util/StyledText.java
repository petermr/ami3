package org.contentmine.eucl.euclid.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** styles text (e.g. on sysout).
 * 
 * @author pm286
 *
 */
public class StyledText {
	private static final Logger LOG = Logger.getLogger(StyledText.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum SysoutStyle {
		RESET("0"),
		BOLD("1"),
		UNDERLINE("4"),
		SLOW_BLINK("5"),
		
		BLACK("30"),
		RED("31"),
		GREEN("32"),
		YELLOW("33"),
		BLUE("34"),
		PURPLE("35"),
		CYAN("36"),
		WHITE("37"),
		;
		public final String code;
		private SysoutStyle(String code) {
			this.code = code;
		}
	}
	public final static String ESC = "\033"; 
	public final static String BRAK = "[0;"; 
	public final static String BRAK0 = BRAK+"0;"; 
	public final static String M = "m"; 
	
	public static String wrapBold(String string) {
		return ESC + BRAK + SysoutStyle.BOLD.code + M + string + ESC + BRAK + SysoutStyle.RESET.code + M;
	}
	  
	public static String wrap(String string, SysoutStyle style) {
		return ESC + BRAK + style.code + M + string + ESC + BRAK + SysoutStyle.RESET.code + M;
		  
	}
}
