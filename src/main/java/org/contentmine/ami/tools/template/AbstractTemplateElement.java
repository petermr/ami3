package org.contentmine.ami.tools.template;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

/** AMIImage template element
 * 
 * @author pm286
 *
 */
public abstract class AbstractTemplateElement extends Element {


	private static final Logger LOG = Logger.getLogger(AbstractTemplateElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static String TAG = "template";
	
	protected File currentDir;

	
	protected AbstractTemplateElement(Element element) {
		super(element);
	}
	
	public AbstractTemplateElement(String tag) {
		super(tag);
	}

	public static AbstractTemplateElement createTemplateElement(Element element, File currentDir) {
		AbstractTemplateElement templateElement = null;
		if (element != null) {
			String tag = element.getLocalName();
			if (false) {
			} else if (TemplateElement.TAG.equals(tag)) {
				templateElement = new TemplateElement();
			} else if (ImageTemplateElement.TAG.equals(tag)) {
				templateElement = new ImageTemplateElement();
			} else if (MessageTemplateElement.TAG.equals(tag)) {
				templateElement = new MessageTemplateElement();
			} else {
				System.err.println("Unknown tag: "+tag);
			}
			if (templateElement != null) {
				templateElement.setCurrentDir(currentDir);
				XMLUtil.copyAttributes(element, templateElement);
				for (int i = 0; i < element.getChildCount(); i++) {
					Node child = element.getChild(i);
					Node newChild = (child instanceof Element) ? AbstractTemplateElement.createTemplateElement((Element)child, currentDir) :
						child.copy();
					if (newChild != null) {
						templateElement.appendChild(newChild);
					}
				}
			}

		}
		return templateElement;

	}

	public static AbstractTemplateElement readTemplateElement(File file, File currentDir) {
		System.out.println(">reading template> "+file.getParentFile().getName());
		Element element = XMLUtil.parseQuietlyToRootElement(file);
		return AbstractTemplateElement.createTemplateElement(element, currentDir);
	}

	protected static String getNonNullAttributeValue(Element element, String attname) {
		String attval = element == null || attname == null ? null : element.getAttributeValue(attname);
		if (attval == null) {
			LOG.debug(element.toXML());
			throw new RuntimeException("Must give "+attname+" attribute");
		}
		return attval;
	}

	public static AbstractTemplateElement readTemplateElement(File currentDir, String templateFilename) {
		AbstractTemplateElement templateElement = null;
		if (templateFilename != null) {
			File templateFile = new File(currentDir, templateFilename);
			if (!templateFile.exists()) {
				System.err.println("no template.xml: "+templateFile);
			} else {
				try {
					templateElement = readTemplateElement(templateFile, currentDir);
				} catch (RuntimeException e) {
					System.out.println("Cannot read template>: ("+templateFilename+") "+e.getClass()+" "+e.getMessage());
				}
				if (templateElement != null) {
					templateElement.currentDir = currentDir;
				}
			}
		}
		return templateElement;
	}
	
	public void process() {
		Elements childElements = this.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			if (childElement instanceof AbstractTemplateElement) {
				((AbstractTemplateElement) childElement).process();
			} else {
				LOG.debug("skipped non TemplateElement: "+childElement.getLocalName());
			}
		}
	}
	
	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File currentDir) {
		this.currentDir = currentDir;
	}


	
	

}
