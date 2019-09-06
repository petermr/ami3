package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.lookup.AbstractLookup;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.Multiset;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

/** a container for ResultElement's.
 * 
 * @author pm286
 *
 */

public class ResultsElement extends Element implements Iterable<ResultElement> {
	
	private static final Logger LOG = Logger.getLogger(ResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String TAG = "results";
	
	public static final String DOCUMENTS = "documents";
	public static final String FREQUENCIES = "frequencies";
	public static final String TITLE = "title";
	
	protected List<ResultElement> resultElementList;
	public List<String> matchList;

	public ResultsElement() {
		super(TAG);
	}

	public ResultsElement(ResultsElement element) {
		this();
		copyAttributesAndAddChildren(element);
	}

	public ResultsElement(String title) {
		this();
		this.setTitle(title);
	}

	public void setTitle(String title) {
		if (title != null) {
			this.addAttribute(new Attribute(TITLE, title));
		}
	}

	public String getTitle() {
		return this.getAttributeValue(TITLE);
	}
	
	/** create ResultsElement from reading Element.
	 * 
	 * @param element
	 * @return
	 */
	public static ResultsElement createResultsElement(Element element) {
		return (ResultsElement) createResults0(element);
	}
	
	private static Element createResults0(Element element) {
		Element newElement = null;
		String tag = element.getLocalName();
		if (ResultsElement.TAG.equals(tag)) {	
			newElement = new ResultsElement();
		} else if (ResultElement.TAG.equals(tag)) {	
			newElement = new ResultElement();
		} else {
			LOG.error("Unknown element: "+tag);
		}
		XMLUtil.copyAttributes(element, newElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Text) {
				child = child.copy();
			} else {
				child = ResultsElement.createResults0((Element)child);
			}
			if (newElement != null && child != null) {	
				newElement.appendChild(child);
			}
		}
		LOG.trace("XML :"+newElement.toXML());
		return newElement;
	}

	/** transfers with detachment ResultElement's in one ResultsElement to another.
	 * 
	 * @param subResultsElement source of ResultElement's
	 */
	public void transferResultElements(ResultsElement subResultsElement) {
//		List<ResultElement> subResults = subResultsElement.getOrCreateResultElementList();
		for (ResultElement subResult : subResultsElement) {
			subResult.detach();
			this.appendChild(subResult);
		}
	}

	public List<ResultElement> getOrCreateResultElementList() {
		resultElementList = new ArrayList<ResultElement>();
		List<Element> resultChildren = XMLUtil.getQueryElements(this, "./*[local-name()='"+ResultElement.TAG+"']");
		for (Element resultElement : resultChildren) {
			resultElementList.add((ResultElement) resultElement);
		}
		return resultElementList;
	}

	public Iterator<ResultElement> iterator() {
		getOrCreateResultElementList();
		return resultElementList.iterator();
	}

	public int size() {
		getOrCreateResultElementList();
		return resultElementList == null ? 0 : resultElementList.size();
	}

	protected void copyAttributesAndAddChildren(ResultsElement resultsElement) {
		if (resultsElement == null) {
			throw new RuntimeException("Null ResultsElement");
		}
		XMLUtil.copyAttributesFromTo(resultsElement, this);
		for (ResultElement resultElement : resultsElement) {
			this.appendChild(resultElement);
		}
	}

	public void setAllResultElementNames(String name) {
		for (ResultElement resultElement : this) {
			resultElement.setName(name);
		}
	}

	public void setXPath(String xpath) {
		for (ResultElement resultElement : this) {
			resultElement.setXPath(xpath);
		}
	}

	public void addMatchAttributes(List<String> matchList) {
		if (this.size() != matchList.size()) {
			throw new RuntimeException("name list wrong length ("+matchList.size()+") rather than ("+this.size()+")");
		}
		int i = 0;
		for (ResultElement resultElement : this) {
			resultElement.setMatch(matchList.get(i));
			// cosmetic - keeps attributes in natural order
			resultElement.setPost(resultElement.getPost());
			i++;
		}
	}

	public List<String> getExactList() {
		if (matchList == null) {
			matchList = new ArrayList<String>();
			for (ResultElement resultElement : this) {
				String name = resultElement.getExact();
				matchList.add(name);
			}
		}
		return matchList;
	}

	public void lookup(Map<String, AbstractLookup> lookupInstanceByName, List<String> lookupNames) {
		if (lookupInstanceByName != null && lookupNames != null) {
			for (String lookupName : lookupNames) {
				AbstractLookup abstractLookup = lookupInstanceByName.get(lookupName);
				for (ResultElement element : resultElementList) {
					String exact = element.getExact();
					try {
						String lookupId = abstractLookup.lookup(exact);
						element.setId(lookupName, lookupId);
					} catch (IOException e) {
						LOG.debug("lookup failed", e);
					}
				}
			}
		}
	}

	public static boolean isEmpty(File xmlFile) {
		Document document = XMLUtil.parseQuietlyToDocument(xmlFile);
		if (document != null) {
			List<Element> results = XMLUtil.getQueryElements(document, "*/"+ResultElement.TAG);
			if (results.size() == 0) {
				return true;
			}
		}
		return false;
	}
	
	public ResultElement get(int i) {
		return resultElementList == null || resultElementList.size() <= i ? null : resultElementList.get(i); 
	}

	public void remove(int i) {
		if (resultElementList != null && resultElementList.size() > i) {
			resultElementList.remove(i); 
		}
	}

	public static ResultsElement getResultsElementSortedByCount(Multiset<String> matchSet) {
		Iterable<Multiset.Entry<String>> sortedEntries = CMineUtil.getEntriesSortedByCount(matchSet);
		Iterator<Multiset.Entry<String>> entries = sortedEntries.iterator();
		ResultsElement resultsElement = new ResultsElement(ResultsElement.FREQUENCIES);
		while (entries.hasNext()) {
			ResultElement resultElement = new ResultElement(ResultElement.FREQUENCY);
			Multiset.Entry<String> entry = entries.next();
			resultElement.appendChild(entry.getElement().toString());
			resultElement.setCount(entry.getCount());
			resultsElement.appendChild(resultElement);
		}
		return resultsElement;
	}

	public String toString() {
		getOrCreateResultElementList();
		return resultElementList.toString();
	}

}
