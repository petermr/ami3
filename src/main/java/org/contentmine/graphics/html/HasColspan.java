package org.contentmine.graphics.html;

/** col and colgroup can return total colspan
 * 
 * @author pm286
 *
 */
public interface HasColspan {
	public final static String COLSPAN = "colspan";

	int getColspan();
}
