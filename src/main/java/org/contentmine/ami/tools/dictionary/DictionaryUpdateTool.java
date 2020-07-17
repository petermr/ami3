package org.contentmine.ami.tools.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
Peter Murray-Rust  3:51 PM
create update (--replace --delete)
In a similar way to database CRUD I'm introducing new subcommands and Options to amidict . PLEASE COMMENT! Good design prevents bugs later.
amidict create is now in wide usage. In favourable cases it's possible to create a dictionary that needs no editing, but this is rare. Normally a dictionary needs continuous editing and releases. create can use wordlists, Wikipedia, Wikidata, in various ways
update This is new and will ( I think) allow:
for a single dictionary:
changing values of attributes
adding/deleting attributes
adding/deleting entry elements
adding/deleting desc elements
adding/deleting synonym children to entrys
3:54
However we sometimes arrive at dictionaries by more than one means. This could be different sources, different ages, different creation tools. They need merging. The merge will be:
dictionary3 = merge(dictionary1, dictionary2)
3:56
If there are more than 2 dictionaries, check that you are in control, but in functional style we'll write:
dictionary4 = merge(dictionary3 , merge(dictionary1, dictionary2))
3:58
So for 2 dictionaries:
"replace" is simply
dictionary2 = rename (dictionary1)
(edited)






Peter Murray-Rust  4:04 PM
merge is complex. It can include:
for the dictionary
take elements from either (OR)
take elements that only occur in both
replace element in dictionary1 by corresponding element in dictionary2
for attributes on an element:
take attributes from either (OR)
only take attributes from both (AND)
replace attribute A in dictionary1 by attribute B in dictionary2
 (edited) 





4:06
I hope that much of this can be managed by xpath semantics.
 */
@Command(
		name = "update",
		description = {
				"updates or merges dictionaries",
				"TBD"
				+ ""
		})
public class DictionaryUpdateTool extends AbstractAMIDictTool {
	public static final Logger LOG = LogManager.getLogger(DictionaryUpdateTool.class);
	
	public enum UpdateControl {
		both,
		keep,
		replace,
	}
	
	@Option(names = {"--control"}, 
			arity="1",
		description = "update strategy"
			)
	private UpdateControl control = UpdateControl.replace;
	
	@Option(names = {"--delete"}, 
			arity="1..*",
		description = "delete nodes referenced by DSL"
			)
	private List<String> deleteNodes;

	@Option(names = {"--merge"}, 
			arity="0",
		description = "merge nodes referenced by xpath"
			)
	private String merge;

	@Option(names = {"--replace"}, 
			arity="1",
		description = "replace nodes referenced by xpath"
			)
	private String replace;

	private List<SimpleDictionary> dictionaryList;
	private SimpleDictionary dictionary0;
	private SimpleDictionary dictionary1;

	public DictionaryUpdateTool() {
		
	}

//	@Override
	protected void parseSpecifics() {
	}

	@Override
	public void runSub() {
		getOrCreateDictionaries();
		if (merge != null) {
			SimpleDictionary dictionary2 = mergeDictionaries();
			if (dictionary2 != null) {
				XMLUtil.writeQuietly(dictionary2.getDictionaryElement(), new File("target/simpleDictionary/merge2.xml"), 1);
			}
//			LOG.info("d2> "+dictionary2.toXML());
		} else if (deleteNodes != null) {
			deleteNodes();
		} else if (replace != null) {
			replaceNodes();
		} else {
			LOG.warn("no Option (merge, replace, delete) for --update");
		}
		
	}

	private void replaceNodes() {
		if (dictionary0 == null || dictionary1 != null) {
			LOG.error("--replace requires one dictionary");
			return;
		}
		LOG.info("> "+replace);
		
	}
	
	private void deleteNodes() {
		LOG.info("> deleteNodes> "+deleteNodes);
		if (dictionary0 == null || dictionary1 != null) {
			LOG.error("--delete requires one dictionary");
			return;
		}
		for (String deleteNode : deleteNodes) {
			PseudoXPath pseudoXPath = new PseudoXPath(deleteNode);
			List<Node> nodeList = pseudoXPath.createNodeList(dictionary0.getDictionaryElement());
			for (Node node : nodeList) {
				node.detach();
			}
		}
	}

	private SimpleDictionary mergeDictionaries() {
		if (dictionary0 == null || dictionary1 == null) {
			LOG.error("--merge requires 2 dictionaries");
			return null;
		}
		SimpleDictionary dictionary2 = new SimpleDictionary(dictionary0.getTitle());
		LOG.info("d0>"+dictionary0.getDictionaryElement().toXML());
		LOG.info("d1>"+dictionary1.getDictionaryElement().toXML());
		
		copyDescElements(dictionary0, dictionary2);
		copyDescElements(dictionary1, dictionary2);
		
		Map<String, Element> entryByTerm0 = dictionary0.getEntryByTerm();
		Map<String, Element> entryByTerm1 = dictionary1.getEntryByTerm();
		copyDict0AndMergeTermsCommonToDict1(entryByTerm0, entryByTerm1, dictionary2);
		copyDict1NotInDict0(entryByTerm0, entryByTerm1, dictionary2);
		return dictionary2;
	}

	private void copyDict0AndMergeTermsCommonToDict1(
			Map<String, Element> entryByTerm0, Map<String, Element> entryByTerm1,
			SimpleDictionary dictionary2) {
		Set<String> keys0 = entryByTerm0.keySet();
		for (String key0 : keys0) {
			LOG.info("k0>"+key0);
			Element mergedEntry = null;
			Element entry0 = entryByTerm0.get(key0);
			Element entry1 = entryByTerm1.get(key0);
			mergedEntry = getMergedEntry(entry0, entry1);
			dictionary2.getDictionaryElement().appendChild(mergedEntry);
		}
	}

	private void copyDict1NotInDict0(
			Map<String, Element> entryByTerm0, Map<String, Element> entryByTerm1,
			SimpleDictionary dictionary2) {
		List<String> keys1 = new ArrayList<>(entryByTerm1.keySet());
		for (String key1 : keys1) {
			Element entry1 = entryByTerm1.get(key1);
			Element entry0 = entryByTerm0.get(key1);
			if (entry0 == null) {
				dictionary2.addEntry(new Element(entry1));
			}
		}
	}


	private void copyDescElements(SimpleDictionary dictionary0, SimpleDictionary dictionary2) {
		List<Element> descElements0 = dictionary0.getDescElements();
		for (Element descElement0 : descElements0) {
			dictionary2.getDictionaryElement().appendChild(new Element(descElement0));
		}
	}

	/** if entry1 == null copy entry0 else merge attributes
	 * 
	 * @param entry0
	 * @param entry1
	 * @return
	 */
	private Element getMergedEntry(Element entry0, Element entry1) {
		Element element2 = null;
		if (entry1 == null) {
			element2 = new Element(entry0); 
		} else {
			element2 = new Element(SimpleDictionary.ENTRY);
			addAttributes(entry0, entry1, element2, control);
//			addAttributes(entry1, entry0, element2, control);
		}
		return element2;
	}

	private void addAttributes(Element entry0, Element entry1, Element element2, UpdateControl control) {
		for (int i0 = 0; i0 < entry0.getAttributeCount(); i0++) {
			Attribute att0 = entry0.getAttribute(i0);
			String attName = att0.getLocalName();
			Attribute att1 = entry1.getAttribute(attName);
			Attribute att2 = (att1 == null) ? new Attribute(att0) : mergeAttribute(att0, att1, control);
			element2.addAttribute(new Attribute(att2));
		}
	}

//	private void addAttributesFrom1(Element entry0, Element entry1, Element element2) {
//		for (int i1 = 0; i1 < entry1.getAttributeCount(); i1++) {
//			Attribute att1 = entry1.getAttribute(i1);
//			Attribute att0 = entry0.getAttribute(att1.getLocalName());
//			if (att0 == null) {
//				element2.addAttribute(new Attribute(att1));
//			}
//		}
//	}

	private Attribute mergeAttribute(Attribute att0, Attribute att1, UpdateControl control) {
		if (UpdateControl.replace.equals(control)) {
			return (att1 != null) ? new Attribute(att1) : new Attribute(att0);
		} else  if (UpdateControl.keep.equals(control)) {
			return att0 == null ? null : new Attribute(att0);
		} else if (UpdateControl.both.equals(control)) {
			if (att0 != null && att1 != null) {
				return att0.equals(att1) ? new Attribute(att0) : null;
			}
			return null;
		}
		return null;
	}

	/** creates dictionaryList and dictionary0 and dictionary1
	 * 
	 * @return
	 */
	private List<? extends DefaultAMIDictionary> getOrCreateDictionaries() {
		String inputFormat = getInputFormat();
		if (dictionaryList == null) {
			dictionaryList = new ArrayList<>();
			for (String dictionaryName : getDictionaryNameList()) {
				File file = new File(parent.getDirectory(), dictionaryName + "." + inputFormat);
				LOG.info(" dil: file "+file);
				SimpleDictionary dictionary = null;
				try {
					dictionary = (SimpleDictionary) SimpleDictionary.createDictionary(
							dictionaryName, new FileInputStream(file));
				} catch (FileNotFoundException e) {
					LOG.error("cannot read dictionary " + e);
				}
				dictionaryList.add(dictionary);
			}
			dictionary0 = dictionaryList.size() > 0 ? dictionaryList.get(0) : null;
			dictionary1 = dictionaryList.size() > 1 ? dictionaryList.get(1) : null;
		}
		return dictionaryList;
	}

	/** NOT FIXED */
	private String getInputFormat() {
		String inputFormat = null;
//		inputFormat = parent.GeneralOptions.inputFormat;
//		inputFormat = AMIDict.GeneralOptions.inputFormat;
		inputFormat = "xml";
		return inputFormat;
	}
	/**
	 * 
Manipulate XML files using XPath declarations
     
By Laurent Bovet
JavaWorld | 10 JULY 2007 8:00 GMT

Sometimes it seems you spend more time manipulating XML files than you do writing Java code, so it makes sense to have one or two XML
 wranglers in your toolbox. In this article, Laurent Bovet gets you started with XmlMerge, an open source tool that lets you
  use XPath declarations to merge and manipulate XML data from different sources.

As a Java developer you use XML every day in your build scripts, deployment descriptors, configuration files, 
object-relational mapping files and more. Creating all these XML files can be tedious, but it's not especially challenging. 
Manipulating or merging the data contained in such disparate files, however, can be difficult and time-consuming. 
You might prefer to use several files split into different modules, but find yourself limited to one large file 
because that is the only format the XML's intended consumer can understand. You might want to override particular 
elements in a large file, but find yourself replicating the file's entire contents instead. Maybe you just lack the 
time to create the XSL transformations (XSLT) that would make it easier to manipulate XML elements in your documents. 
Whatever the case, it seems nothing is ever as easy as it should be when it comes to merging the elements in your XML files.

In this article, I present an open source tool I created to resolve many of the common problems associated with 
merging and manipulating data from different XML documents. EL4J XmlMerge is a Java library under the LGPL license 
that makes it easier to merge elements from different XML sources. While XmlMerge is part of the EL4J framework, you can use it independently of EL4J. All you need to run the XmlMerge utility from your command line is JDK 1.5 or greater.

In the discussion that follows, you will learn how to use XmlMerge for a variety of common XML merging scenarios, including merging two XML files, merging XML file data from different sources to create a Spring Resource bean at runtime and combining XmlMerge and Ant to create an automated deployment descriptor at build time. I'll also show you how to use XPath declarations and built-in actions and matchers to specify the treatment of specific elements during an XML merge. I'll conclude with a look at XmlMerge's simple merging algorithm and suggest ways it could be extended for more specialized XML merging operations.

You can Download XmlMerge now if you want to follow along with the examples.

Merging XML files
In Listing 1 you see the very common (and greatly simplified) example of two XML files that need to be merged.

Listing 1. Two XML files that need to be merged

File1.xml	File2.xml
<root>
<a>
 <b/>
</a>
</root>
<root>
<a>
 <c/>
</a>
<d/>
</root>
Listing 2 shows the command-line input to merge these two files using the XmlMerge utility, followed by the resulting output.

Listing 2. The two XML files merged using XmlMerge

~ $ java -jar xmlmerge-full.jar file1.xml file2.xml

<?xml version="1.0" encoding="UTF-8"?>
<root>
<a>
 <b />
 <c />
</a>
<d />
</root>
~ $
This first example of merging is very simple, but you may have noticed that the order in which the files are merged is important. If you switch the order, you can get different results. (Later in the article you'll see an example of what happens when you switch the order of two files to be merged.) To keep files in order, XmlMerge uses the term original for the first document and patch for the second one. This is easy to remember because the patch document always is merged into the original.

Merging XML files from different sources
You can implement the XmlMerge utility anywhere in your Java code and use it to merge data from different sources into a new, useful document. In Listing 3, I've used it to merge a file from my application filesystem and the contents of a servlet request into a single document object model (DOM).

Listing 3. Merging client and server XML into a DOM

XmlMerge xmlMerge = new DefaultXmlMerge();
org.w3c.dom.Document doc = documentBuilder.parse(
                             xmlMerge.merge(
                                new FileInputStream("file1.xml"),
                                servletRequest.getInputStream()));
Creating Spring Framework resources at runtime
In some cases it is useful to combine XmlMerge and the Spring Framework. For example, the Spring Resource bean shown in Listing 4 was created at runtime by merging separate XML files into a single XML stream. You could then use the Resource bean to configure other resources for object-relational mapping, document generation and more.

Listing 4. A Spring Resource bean

<bean name="mergedResource"
      class="ch.elca.el4j.services.xmlmerge.springframework.XmlMergeResource">
  <property name="resources">
    <list>
      <bean class="org.springframework.core.io.ClassPathResource">
        <constructor-arg>
          <value>ch/elca/el4j/tests/xmlmerge/r1.xml</value>
        </constructor-arg>
      </bean>
      <bean class="org.springframework.core.io.ClassPathResource">
        <constructor-arg>
          <value>ch/elca/el4j/tests/xmlmerge/r2.xml</value>
        </constructor-arg>
      </bean>
    </list>            
  </property>
  <property name="properties">
    <map>
      <entry key="action.default" value="COMPLETE"/>
      <entry key="XPath.path1" value="/root/a"/>
      <entry key="action.path1" value="MERGE"/>
    </map>
  </property>
</bean>
Generating an automated deployment descriptor at build time
You've probably used Ant to automate your builds. How about combining it with XmlMerge to generate an XML deployment descriptor at build time? Listing 5 shows the XmlMergeTask at work.

Listing 5. XmlMergeTask generates a deployment descriptor

<target name="test-task">
  <taskdef name="xmlmerge"
           classname="ch.elca.el4j.services.xmlmerge.anttask.XmlMergeTask"
           classpath="xmlmerge-full.jar"/>

  <xmlmerge dest="out.xml" conf="test.properties">
     <fileset dir="test">
        <include name="source*.xml"/>
     </fileset>
  </xmlmerge>
</target>
Related: Open Source Software Development Build Automation Web Development
XML merging made easy
Manipulate XML files using XPath declarations
     
By Laurent Bovet
JavaWorld | 10 JULY 2007 8:00 GMT

 PREVIOUS 1 2 3 NEXT 
Using XPath declarations with XmlMerge
You've seen a few examples of applying XmlMerge to common Java enterprise development scenarios. l'll spend the remainder of this article explaining how the tool works. By default, you can use XPath declarations to specify how XmlMerge handles your XML sources. A sample configuration is shown in Listing 6.

Listing 6. xmlmerge.properties

action.default=COMPLETE   # By default, only add elements not
                          # already existing in first file

XPath.a=/root/a           # define a XPath named "a" and matching
                          # all <a> elements under <root>

action.a=MERGE            # configure to merge children of <a>
Listing 7 shows two more files that need to be merged, this time as specified by the above XPath declarations.

Listing 7. Two XML files waiting to be merged

Original	Patch
<root>
<a/>
<c/>
</root>
<root>
<a>
 <b/>
</a>
<c>
 <d/>
</c>
</root>
Listing 8 shows the files merged as specified by the XPath declarations.

Listing 8. Two files merged as specified

<root>
<a>
 <b/>   # merged the content of the element <a>
</a>
<c/>    # by default, do not modify existing elements
</root>
Using XPath declarations within the XmlMerge utility lets you specify how each element in your XML files will be handled during a merge. In the next section I'll explain the actions you may have noticed in Listing 5, as well as the use of matching functions in XmlMerge.

Actions and matching functions
XmlMerge provides many built-in actions, some of them extending its functions well beyond simple merging. Consider the following actions and the various ways you could use XPath declarations to apply them to elements in your XML documents.

Table 1. Built-in actions for XmlMerge

Action	Description	Result
MERGE	Traverses in parallel the original and patch elements, determines matching pairs between documents in the order of traversal, and merges children recursively. MERGE is the default action and is sufficient for most common uses where the original and patch documents present elements in the same order.	
<root>
<a>
 <b/>
 <c/>
</a>
<d/>
<e/>
</root>
REPLACE	Replaces original elements with patch elements. Can also be used to add new (patch) elements to a file.	
<root> 
<a> 
 <c/> 
</a> 
<d/> 
<e/> 
</root>
OVERRIDE	Replaces an original element with a patch element.	
<root> 
<a> 
 <c/> 
</a> 
<d/> 
</root>
COMPLETE	Selectively adds in patch elements that did not exist in the original, using patch elements to complete the original ones.	
<root> 
<a> 
 <b/> 
</a> 
<d/> 
<e/> 
</root>
DELETE	Copies the original element only if it does not exist in the patch. If it exists in the patch, then nothing is added to the result (the presence of patch elements actually deletes the matching elements from the original).	
<root> 
<d/> 
</root>
PRESERVE	Invariantly copies the original element regardless of the existence of the patch element (it drops the patch element).	
<root> 
<a> 
 <b/> 
</a> 
<d/> 
</root>
It is also possible to tell XmlMerge that elements from the original and the patch correspond to criteria other than the element name. For this you would use matching functions, or matchers.

Table 2. Built-in matchers for XmlMerge

Matcher	Description
TAG	This default matcher says the original and patch elements match if the tag name is the same.
ID	The original and patch elements match if the tag names and the id attribute values are the same.
Related: Open Source Software Development Build Automation Web Development Enterprise Java Development Tools Java
*/
/**
 * 6


I use XSLT to merge XML files. It allows me to adjust the merge operation to just slam the content together or to merge at an specific level. It is a little more work (and XSLT syntax is kind of special) but super flexible. A few things you need here

a) Include an additional file b) Copy the original file 1:1 c) Design your merge point with or without duplication avoidance

a) In the beginning I have

<xsl:param name="mDocName">yoursecondfile.xml</xsl:param>
<xsl:variable name="mDoc" select="document($mDocName)" />
this allows to point to the second file using $mDoc

b) The instructions to copy a source tree 1:1 are 2 templates:

<!-- Copy everything including attributes as default action -->
<xsl:template match="*">
    <xsl:element name="{name()}">
         <xsl:apply-templates select="@*" />
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="@*">
    <xsl:attribute name="{name()}"><xsl:value-of select="." /></xsl:attribute>
</xsl:template>
With nothing else you get a 1:1 copy of your first source file. Works with any type of XML. The merging part is file specific. Let's presume you have event elements with an event ID attribute. You do not want duplicate IDs. The template would look like this:

 <xsl:template match="events">
    <xsl:variable name="allEvents" select="descendant::*" />
    <events>
        <!-- copies all events from the first file -->
        <xsl:apply-templates />
        <!-- Merge the new events in. You need to adjust the select clause -->
        <xsl:for-each select="$mDoc/logbook/server/events/event">
            <xsl:variable name="curID" select="@id" />
            <xsl:if test="not ($allEvents[@id=$curID]/@id = $curID)">
                <xsl:element name="event">
                    <xsl:apply-templates select="@*" />
                    <xsl:apply-templates />
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </properties>
</xsl:template>
Of course you can compare other things like tag names etc. Also it is up to you how deep the merge happens. If you don't have a key to compare, the construct becomes easier e.g. for log:

 <xsl:template match="logs">
     <xsl:element name="logs">
          <xsl:apply-templates select="@*" />
          <xsl:apply-templates />
          <xsl:apply-templates select="$mDoc/logbook/server/logs/log" />
    </xsl:element>
To run XSLT in Java use this:

    Source xmlSource = new StreamSource(xmlFile);
    Source xsltSource = new StreamSource(xsltFile);
    Result xmlResult = new StreamResult(resultFile);
    TransformerFactory transFact = TransformerFactory.newInstance();
    Transformer trans = transFact.newTransformer(xsltSource);
    // Load Parameters if we have any
    if (ParameterMap != null) {
       for (Entry<String, String> curParam : ParameterMap.entrySet()) {
            trans.setParameter(curParam.getKey(), curParam.getValue());
       }
    }
    trans.transform(xmlSource, xmlResult);
or you download the Saxon SAX Parser and do it from the command line (Linux shell example):

#!/bin/bash
notify-send -t 500 -u low -i gtk-dialog-info "Transforming $1 with $2 into $3 ..."
# That's actually the only relevant line below
java -cp saxon9he.jar net.sf.saxon.Transform -t -s:$1 -xsl:$2 -o:$3
notify-send -t 1000 -u low -i gtk-dialog-info "Extraction into $3 done!"
YMMV
 */

	
}

