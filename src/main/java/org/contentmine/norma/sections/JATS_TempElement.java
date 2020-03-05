package org.contentmine.norma.sections;

import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;

import nu.xom.Element;

/** temporary element
 * 
 * @author pm286
 *
 */
public class JATS_TempElement extends JATSElement {

	public static String TAG = "_temp";
	private JATSArticleElement article;

	public JATS_TempElement() {
		super(TAG);
	}
	
	public JATS_TempElement(String content) {
		this();
		this.appendText(content);
	}
	
	public JATS_TempElement(Element element) {
		super(element);
	}
	
	public JATSArticleElement getOrCreateSingleArticleChild() {
		article = (JATSArticleElement) getSingleChild(JATSArticleElement.TAG);
		if (article ==  null) {
			article = new JATSArticleElement();
			this.appendElement(article);
		}
		return article;
	}


}
