package org.contentmine.ami.tools.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

/** reads Wikidata SPARQL and maps onto AMI names.
 * 
 * 
 * @author pm286
 *
 */
public class WikidataSparql {

	private static final String PERSONAL = "_";
	private static final String Q = "q_";
	private static final String P = "p_";
	private static final Logger LOG = LogManager.getLogger(WikidataSparql.class);
	private static final String MUST = "must";
	private static final String SHOULD = "should";
	private static final String CAN = "can";
	private final static List<String> MUST_AMI = Arrays.asList(new String[] {DefaultAMIDictionary.TERM});
	private final static List<String> SHOULD_AMI = Arrays.asList(new String[] {
			DefaultAMIDictionary.DESCRIPTION,
			DefaultAMIDictionary.NAME,
			DefaultAMIDictionary.WIKIDATA,
			DefaultAMIDictionary.WIKIPEDIA,
		});
	private final static List<String> CAN_AMI = Arrays.asList(new String[] {
//			DefaultAMIDictionary.SYNONYM
			});
	private final static List<String> ALL_AMI = new ArrayList<String>();
	static {
		ALL_AMI.addAll(MUST_AMI);
		ALL_AMI.addAll(SHOULD_AMI);
		ALL_AMI.addAll(CAN_AMI);
	};
	
	private static final String ITEM_LABEL = "itemLabel";
	
	private DictionaryCreationTool dictionaryCreationTool;
	private Element sparqlXml; 
	private List<String> sparqlVariables;
	private List<Element> sparqlResultList;
	private List<String> amiNames;
	private List<String> amiValues;
	private Element amiEntry;
	private Map<String, String> sparqlNameByAmiName;
	
	public WikidataSparql(DictionaryCreationTool dictionaryCreationTool) {
		this.dictionaryCreationTool = dictionaryCreationTool;
	}
	private void checkNameMappings() {
		amiNames = new ArrayList<String>(this.sparqlNameByAmiName.keySet());
		Collections.sort(amiNames);
		checkAmiNamesContain(MUST_AMI, MUST);
		checkAmiNamesContain(SHOULD_AMI, SHOULD);
		checkAmiNamesContain(CAN_AMI, CAN);
		checkUnknownAmiNames();
		checkWikidataVariables();
	}

	private void checkWikidataVariables() {
		amiValues = new ArrayList<String>(this.sparqlNameByAmiName.values());
		searchValuesInTarget(amiValues, "sparqlVariables", sparqlVariables);
		// __synonyms also create amiValues
		amiValues.addAll(dictionaryCreationTool.synonymList);
		searchValuesInTarget(sparqlVariables, "amiValues", amiValues);
	}

	private void searchValuesInTarget(List<String> searchValues, String target, List<String> targetValues) {
		for (String value : searchValues) {
			if (!targetValues.contains(value)) {
				LOG.warn(value + " not found in " + target);
			}
		}
	}

	private void checkUnknownAmiNames() {
		for (String amiName : amiNames) {
			if (ALL_AMI.contains(amiName)) {
				// OK
			} else if (amiName.startsWith(P) || amiName.startsWith(Q)) {
				// OK
			} else if (amiName.startsWith(PERSONAL)) {
				// OK
			} else {
				LOG.warn("unknown ami name: " + amiName);
			}
		}
	}

	private void checkAmiNamesContain(List<String> names, String message) {
		for (String name : names) {
			if (!amiNames.contains(name)) {
				if (MUST.contentEquals(message)) {
					throw new RuntimeException("sparqlMap MUST contain key: "+name);
				} else if (SHOULD.contentEquals(message)) {
					LOG.warn("sparqlMap SHOULD contain key: "+name);
				} else if (CAN.contentEquals(message)) {
					LOG.info("sparqlMap does not contain key: "+name);
				}
			}
		}
	}

	void readSparqlXml(InputStream inputStream) {
		/**
	<sparql xmlns='http://www.w3.org/2005/sparql-results#'>
	<head>
		<variable name='wikidata'/>
		<variable name='wikidataLabel'/>
		<variable name='wikipedia'/>
		<variable name='wikidataAltLabel'/>
		<variable name='synonyms'/>
	</head>
	<results>
		<result>
			<binding name='wikidata'>
				<uri>http://www.wikidata.org/entity/Q16</uri>
			</binding>
			<binding name='synonyms'>
				<literal>🇨🇦</literal>
			</binding>
			<binding name='wikipedia'>
				<uri>https://en.wikipedia.org/wiki/Canada</uri>
			</binding>
			<binding name='wikidataLabel'>
				<literal xml:lang='en'>Canada</literal>
			</binding>
			<binding name='wikidataAltLabel'>
				<literal xml:lang='en'>CA, ca, CDN, can, CAN, British North America, 🇨🇦, Dominion of Canada</literal>
			</binding>
		</result>		 
		...
		*/
		addDesc();
		
		sparqlXml = XMLUtil.parseQuietlyToRootElement(inputStream, CMineUtil.UTF8_CHARSET);
		sparqlVariables = XMLUtil.getQueryValues(sparqlXml, 
				"./*[local-name() = 'head']/*[local-name()='variable']/@name");
		if (this.sparqlNameByAmiName.size() > 0) {
			checkNameMappings();
			DictionaryCreationTool.LOG.info("sparql names " + sparqlVariables);
		} else {
			LOG.warn("no --wikidatasparqlmap: Relying on user names for mapping");
			createIdentityMapping();
		}
		sparqlResultList = XMLUtil.getQueryElements(sparqlXml, "./*[local-name()='results']/*[local-name()='result']");
		DictionaryCreationTool.LOG.info("results " + sparqlResultList.size());
		
		
		for (Element sparqlResult : sparqlResultList) {
			amiEntry = new Element(DefaultAMIDictionary.ENTRY);
			for (String amiName : amiNames) {
				if (amiName.contentEquals(DefaultAMIDictionary.SYNONYM)) {
					continue;
				}
				String sparqlName = this.sparqlNameByAmiName.get(amiName);
				if (sparqlName != null) {
					String sparqlValue = getValueByBindingName(sparqlResult, sparqlName);
					if (sparqlValue != null) {
						amiEntry.addAttribute(new Attribute(amiName, sparqlValue));
					}
				}
			}
			addSynonyms(sparqlResult);
			dictionaryCreationTool.simpleDictionary.getDictionaryElement().appendChild(amiEntry);
		}
	}

	/** creates map with keys identical to sparqlVariables.
	 * Useful when --wikidataSparqlmap is not given;
	 */
	private void createIdentityMapping() {
		sparqlNameByAmiName = new HashMap<>();
		amiNames = new ArrayList<>();
		for (String sparqlVariable : sparqlVariables) {
			sparqlNameByAmiName.put(sparqlVariable, sparqlVariable);
			amiNames.add(sparqlVariable);
		}
	}
	
	private void addDesc() {
		Element desc = new Element(DefaultAMIDictionary.DESC);
		desc.appendChild("Created from SPARQL query");
		dictionaryCreationTool.simpleDictionary.getDictionaryElement().appendChild(desc);
	}

	/**
		<result>
			<binding name='Disease'>
				<uri>http://www.wikidata.org/entity/Q12135</uri>
			</binding>
			<binding name='ICDcode'>
				<literal>F00-F99</literal>
			</binding>
			<binding name='DiseaseLabel'>
				<literal xml:lang='en'>mental disorder</literal>
			</binding>
			<binding name='instanceofLabel'>
				<literal xml:lang='en'>disease</literal>
			</binding>
			<binding name='DiseaseAltLabel'>
				<literal xml:lang='en'>disease of mental health, disorder of mental process, mental dysfunction, mental illness, mental or behavioural disorder, psychiatric condition, psychiatric disease, psychiatric disorder, mental disorders</literal>
			</binding>
		</result>
	 * @param sparqlResult
	 * @param bindingName
	 * @return
	 */
	private String getValueByBindingName(Element sparqlResult, String bindingName) {
		String value = null;
		String xpath = "./*[local-name()='" + DictionaryCreationTool.BINDING + "' and @name='" + bindingName + "']";
		Element bindingElement = XMLUtil.getFirstElement(sparqlResult, xpath);
		if (bindingElement == null) {
			DictionaryCreationTool.LOG.error(DictionaryCreationTool.CL + " cannot find binding: " + bindingName);
			return value;
		}
		Element child = XMLUtil.getFirstElement(bindingElement, "./*");
		if (child == null) {
			DictionaryCreationTool.LOG.error(DictionaryCreationTool.CL + " null value of child: " + sparqlResult.toXML());
		} else {
			value = child.getValue();
			String childName = child.getLocalName();
			if (childName.contentEquals(DictionaryCreationTool.URI)) {
				// last field in URI
				value = value.substring(value.lastIndexOf("/") + 1);
			} else if (childName.contentEquals(DictionaryCreationTool.LITERAL)) {
				// copy direct
			} else {
				DictionaryCreationTool.LOG.error(DictionaryCreationTool.CL + " unknown child: " + child.toXML());
			}
		}
		return value;
	}

//	private boolean addNonNullAttribute(DictionaryCreationTool dictionaryCreationTool, Element amiEntry, Element sparqlResult, String amiName) {
//		String value = null;
//		String bindingName = dictionaryCreationTool.sparqlNameByAmiName.get(amiName);
//		if (bindingName == null) {
//			DictionaryCreationTool.LOG.error(DictionaryCreationTool.CL + "cannot resolve amiName to sparqlName " + amiName);
//		} else {
//			value = getValueByBindingName(sparqlResult, bindingName);
//			if (value != null) {
//				amiEntry.addAttribute(new Attribute(bindingName, value));
//			}
//		}
//		return value != null;
//	}

	private void addSynonyms(Element result) {
		/**
		<binding name='synonyms'>
			<literal>🇨🇦</literal>
		</binding>
		<binding name='wikidataAltLabel'>
		  <literal xml:lang='en'>CA, ca, CDN, can, CAN, British North America, 🇨🇦, Dominion of Canada</literal>
		</binding>
		*/
		if (dictionaryCreationTool.synonymList != null) {
			for (String synonymRef : dictionaryCreationTool.synonymList) {
				splitAndAdd(result, synonymRef);
			}
		}
	}

	private void splitAndAdd(Element result, String bindingName) {
		String literal = getValueByBindingName(result, bindingName);
		if (literal != null) {
			List<String> synonyms = Arrays.asList(literal.split("\\s*,\\s*"));
			for (String synonym : synonyms) {
				addSynonym(synonym);
			}
		}
	}

	private void addSynonym(String synonym) {
		if (acceptableSynonym(synonym)) {
			Element synonymElement = new Element(DefaultAMIDictionary.SYNONYM);
			synonymElement.appendChild(synonym);
			amiEntry.appendChild(synonymElement);
		}
	}

	private boolean acceptableSynonym(String synonym) {
		if (Pattern.matches(DictionaryCreationTool.FLAG_A_Z, synonym)) {
			System.out.println("IGNORED FLAG"+synonym);
			return false;
		}
		return true;
	}


	void readSparqlCsv(InputStream inputStream) {
		/**
item,itemLabel,chebiId
http://www.wikidata.org/entity/Q4015903,Voacamine,10014
http://www.wikidata.org/entity/Q27176808,LSM-12060,100686
		...
		*/
		RectangularTable rectangularTable = null;
		boolean useHeader = true;
		try {
			rectangularTable = RectangularTable.readCSVTable(inputStream, useHeader);
		} catch (IOException e) {
			throw new RuntimeException("cannot read table", e);
		}
		dictionaryCreationTool.termCol = ITEM_LABEL;
		if (dictionaryCreationTool.termCol == null) {
			throw new RuntimeException("must give termCol");
		}
		dictionaryCreationTool.termList = rectangularTable.getColumn(dictionaryCreationTool.termCol).getValues();
		if (dictionaryCreationTool.termList == null) {
			throw new RuntimeException("Cannot find term column");
		}
		String wikidataIDCol = "item";
		List<String> wikidataIDList = rectangularTable.getColumn(wikidataIDCol).getValues();

		addDesc();
		
		
		for (int i = 0; i < rectangularTable.size(); i++) {
			Element entry = new Element(DefaultAMIDictionary.ENTRY);
			String wikidataId = wikidataIDList.get(i);
			wikidataId = wikidataId.substring(wikidataId.lastIndexOf("/") + 1);
			entry.addAttribute(new Attribute(DictionaryCreationTool.WIKIDATA, wikidataId));
			entry.addAttribute(new Attribute(DictionaryCreationTool.TERM, dictionaryCreationTool.termList.get(i)));
			LOG.trace(entry.toXML());
			dictionaryCreationTool.simpleDictionary.getDictionaryElement().appendChild(entry);
		}
	}
	public void copy(Map<String, String> sparqlNameByAmiName) {
		this.sparqlNameByAmiName = new HashMap<>();
		for (String key : sparqlNameByAmiName.keySet()) {
			this.sparqlNameByAmiName.put(key, sparqlNameByAmiName.get(key));
		}
		// TODO Auto-generated method stub
		
	}
	
	/** queries Wikidata/Sparql 
	 * 
	 * @param sparqQuery as entered in GUI
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String queryWikidata(String sparqQuery) throws MalformedURLException, IOException {
		String urlS = DictionaryCreationTool.WIKIDATA_SPARQL_ENDPOINT + Util.URLEncode(sparqQuery);
		return IOUtils.toString(new URL(urlS).openStream(), Charset.forName("UTF-8"));
	}
	

}
