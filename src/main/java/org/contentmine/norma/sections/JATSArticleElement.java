package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

/**

 * 
 * @author pm286
 *
 */
public class JATSArticleElement extends JATSElement implements IsBlock, HasDirectory {

	/**
		<article>
		  <front>
		  </front>
		  <body>
		  </body>
		  <back>
		  </back>
		  </article>
	 */

	static final Logger LOG = Logger.getLogger(JATSArticleElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	static final String TAG = "article";
	public static final String ARTICLE_TYPE = "article-type";
	
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
		JATSDivFactory.FRONT,
		JATSDivFactory.BODY,
		JATSDivFactory.BACK,
		JATSDivFactory.FLOATS_GROUP,
		JATSDivFactory.FLOATS_WRAP,
	});

	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}


	private JATSFrontElement front;
	private JATSBodyElement body;
	private JATSBackElement back;
	
	public JATSArticleElement() {
		super(TAG);
	}
	
	public JATSArticleElement(Element element) {
		super(element);
	}
	
	protected void applyNonXMLSemantics() {
		front = getOrCreateSingleFrontChild();
		body = (JATSBodyElement) getSingleChild(JATSBodyElement.TAG);
		back = (JATSBackElement) getSingleChild(JATSBackElement.TAG);
	}

	public JATSFrontElement getOrCreateSingleFrontChild() {
		front = (JATSFrontElement) getSingleChild(JATSFrontElement.TAG);
		if (front ==  null) {
			front = new JATSFrontElement();
			this.appendElement(front);
		}
		return front;
	}

	public JATSBodyElement getOrCreateSingleBodyChild() {
		body = (JATSBodyElement) getSingleChild(JATSBodyElement.TAG);
		if (body ==  null) {
			body = new JATSBodyElement();
			this.appendElement(body);
		}
		return body;
	}

	public JATSBackElement getOrCreateSingleBackChild() {
		back = (JATSBackElement) getSingleChild(JATSBackElement.TAG);
		if (back ==  null) {
			back = new JATSBackElement();
			this.appendElement(back);
		}
		return back;
	}

	public JATSRefListElement getReflistElement() {
		return back == null ? null : back.getRefList();
	}

	public JATSFrontElement getFront() {
		return front;
	}

	public JATSFrontElement addFront() {
		
		return front;
	}

	public JATSBodyElement getBody() {
		return body;
	}

	public JATSBackElement getBack() {
		return back;
	}

	public String getPMCID() {
		return front == null ? null : front.getPMCID();
	}

	public String directoryName() {
		return this.TAG;
	}

	public JATSElement setArticleType(String content) {
		this.addAttribute(new Attribute(ARTICLE_TYPE, content));
		return this;
	}

}
