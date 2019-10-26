package org.contentmine.norma.sections;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Node;

public class JATSSecElement extends JATSElement implements IsBlock, HasDirectory, HasTitle {

	private static final String BOLD = "*[local-name()='"+JATSBoldElement.TAG+"']";
	private static final String SEC_TYPE = "sec-type";

	public static String TAG = "sec";

	public final static String CASES = "cases";
	public final static String CONCLUSIONS = "conclusions";
	public final static String DISCUSSION = "discussion";
	public final static String INTRO = "intro";
	public final static String MATERIALS = "materials";
	public final static String METHODS = "methods";
	public final static String RESULTS = "results";
	public final static String SUBJECTS = "subjects";
	public final static String SUPPLEMENTARY = "supplementary-material";

	public final static List<String> SEC_TYPE_VALUES = Arrays.asList(new String[] {
         CONCLUSIONS,
         DISCUSSION,
         INTRO,
         MATERIALS,
         METHODS,
         RESULTS,
         SUBJECTS,
         SUPPLEMENTARY,
	});
		
	public JATSSecElement(Element element) {
		super(element);
	}
	
	public JATSSecElement() {
		super(TAG);
	}

	@Override
	protected String getAttributeString() {
		return getAttributeString(SEC_TYPE);
	}
	
	public String debugString(int level) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(Util.spaces(level)+"<"+this.getLocalName());
		
//		sb.append(this.getAttributeString(SEC_TYPE));
		sb.append(this.getAttributeString());
		String title = getTitleValue();
		sb.append("[[" + title + "]]");
//		title.detach();
		sb.append(">\n");
		List<Element> childElements = this.getChildElementList();
		for (Element childElement : childElements) {
			if (childElement instanceof JATSElement) {
				sb.append(((JATSElement)childElement).debugString(level + 1));
			} else {
				sb.append(childElement.toXML());
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	protected JATSElement getTitleElement() {
		JATSElement singleChild = this.getSingleChild(JATSTitleElement.TAG);
//		System.err.println(">>> "+Util.truncateAndAddEllipsis(this.toXML(), 40));
		return singleChild;
	}

	protected void writeSections(JATSElement parent, File currentDir) {
		String titleValue = getTitleValue();
		titleValue = Util.makeLowercaseAndDespace(titleValue, 20);
		if (currentDir != null) {
			currentDir = new File(currentDir, titleValue);
		} else {
			currentDir = new File(cTree.getSectionsDirectory(), titleValue);
		}
		if (!currentDir.exists()) currentDir.mkdirs();
		super.writeSections(this, currentDir);

	}

	@Override
	public String directoryName() {
		return TAG;
	}

	@Override
	public String generateTitle() {
		String title = this.getSingleChildValue(JATSTitleElement.TAG);
		if (title == null) {
			title = this.getID();
		}
		return title;
	}

	/** converts <sec id='s2.3'><p><bold>some phrase</bold> some sentence</p> ...
	 * to <sec id='s2.3'><sec id='s2.3.7'><title>some phrase</title> <p>some sentence</p></sec></sec>
	 * adds serial numbers to IDs if present.
	 * 
	 */
	public void makeBoldSections() {
		// p[body]
		List<Element> paraList = XMLUtil.getQueryElements(
				this, "./*[local-name()='"+JATSPElement.TAG+"' and *[local-name()='"+JATSBoldElement.TAG+"']]");
		for (int i = 0; i < paraList.size(); i++) {
			JATSPElement para = (JATSPElement)paraList.get(i);
			Element firstBoldElement = getElementWithFirstChildBoldElement(para);
			if (firstBoldElement != null) {
				creatNewSecAndTransferContent(i, para, firstBoldElement);
			}
			
		}
	}

	private void creatNewSecAndTransferContent(int i, JATSPElement para, Element firstBoldElement) {
		String boldValue = firstBoldElement.getValue();
		JATSTitleElement titleElement = new JATSTitleElement(boldValue);
		JATSSecElement newSecElement = new JATSSecElement();
		this.replaceChild(para, newSecElement);
		String id = this.getID();
		if (id != null) {
			newSecElement.setID(String.valueOf(id + "." + (i + 1)));
		}
		newSecElement.appendChild(titleElement);
		newSecElement.appendChild(para);
	}

	private Element getElementWithFirstChildBoldElement(JATSPElement para) {
		List<Node> nodes = XMLUtil.getQueryNodes(para, "node()");
		Element element = nodes.size() > 0 && (nodes.get(0) instanceof JATSBoldElement) ? (Element) nodes.get(0) : null;
		return element;
	}


}
