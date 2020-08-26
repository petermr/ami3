package org.contentmine.ami.tools.dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;

/** validates AMI dictionaries
 * 
 * @author pm286
 *
 */
public class AMIDictValidator {
	
	public static final String PERSONAL = "_";
	public static final String Q = "q_";
	public static final String P = "p_";
	public static final Logger LOG = LogManager.getLogger(WikidataSparql.class);
	public static final String MUST = "must";
	public static final String SHOULD = "should";
	public static final String CAN = "can";
	public final static List<String> MUST_AMI = Arrays.asList(new String[] {DefaultAMIDictionary.TERM});
	public final static List<String> SHOULD_AMI = Arrays.asList(new String[] {
			DefaultAMIDictionary.DESCRIPTION,
			DefaultAMIDictionary.NAME,
			DefaultAMIDictionary.WIKIDATA_ID,
			DefaultAMIDictionary.WIKIDATA_URL,
			DefaultAMIDictionary.WIKIPEDIA_PAGE,
			DefaultAMIDictionary.WIKIPEDIA_URL,
		});
	public final static List<String> CAN_AMI = Arrays.asList(new String[] {
//			DefaultAMIDictionary.SYNONYM
			});
	public final static List<String> ALL_AMI = new ArrayList<String>();
	static {
		ALL_AMI.addAll(MUST_AMI);
		ALL_AMI.addAll(SHOULD_AMI);
		ALL_AMI.addAll(CAN_AMI);
	};
	public static Pattern WIKIDATA_PQ_PATTERN = Pattern.compile("_[PpQq](\\d+)_[A-Za-z][A-Za-z0-9]+"); 

	public AMIDictValidator() {
		
	}
	

	public void checkNameMappings(List<String> amiNames) {
		LOG.info("WS>"+amiNames);
		Collections.sort(amiNames);
		checkAmiNamesContain(amiNames, MUST_AMI, MUST);
		checkAmiNamesContain(amiNames, SHOULD_AMI, SHOULD);
		checkAmiNamesContain(amiNames, CAN_AMI, CAN);
		checkUnknownAmiNames(amiNames);
	}
	
	private void checkAmiNamesContain(List<String> amiNames, List<String> names, String message) {
		for (String name : names) {
			if (!amiNames.contains(name)) {
				if (AMIDictValidator.MUST.contentEquals(message)) {
					throw new RuntimeException("sparqlMap MUST contain key: "+name);
				} else if (SHOULD.contentEquals(message)) {
					LOG.warn("sparqlMap SHOULD contain key: "+name);
				} else if (CAN.contentEquals(message)) {
					LOG.info("sparqlMap does not contain key: "+name);
				}
			}
		}
	}

	private void checkUnknownAmiNames(List<String> amiNames) {
		for (String amiName : amiNames) {
			if (ALL_AMI.contains(amiName)) {
				// OK
			} else if (amiName.startsWith(P) || amiName.startsWith(Q)) {
				Matcher matcher = WIKIDATA_PQ_PATTERN.matcher(amiName);
				if (!matcher.matches()) {
					LOG.error("Bad syntax for wikidata name: " + amiName + "; should match: " + WIKIDATA_PQ_PATTERN);
				}
				LOG.debug("Wikidata Item: "+amiName);
			} else if (amiName.startsWith(PERSONAL)) {
				LOG.debug("Personal ami name: "+amiName);
			} else {
				LOG.warn("unknown ami name: " + amiName);
			}
		}
	}



	
}
