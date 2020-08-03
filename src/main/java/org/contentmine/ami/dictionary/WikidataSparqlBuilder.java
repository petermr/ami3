package org.contentmine.ami.dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author pm286
 *
 */
public class WikidataSparqlBuilder {

	private List<WikidataTriple> tripleList;
	private List<String> variableList;
	private List<String> languageList;
	private int limit;
	private WikidataOptional optional;

	enum WikidataLabel {
		ALT_LABEL("AltLabel"),
		DESCRIPTION("Description"),
		LABEL("Label"),
		;
		private String label;
		private WikidataLabel(String label) {
			this.label = label; 
		}
		public String getLabel() {
			return label;
		}
	}

	public WikidataSparqlBuilder() {
		tripleList = new ArrayList<>();
		variableList = new ArrayList<>();
	}

	public void addSelect(String variable, WikidataLabel ... labels) {
		addVariable(variable);
		for (WikidataLabel label : labels) {
			addSelect(variable + label.getLabel());
		}
	}

	private void addVariable(String variable) {
		if (!variableList.contains(variable)) {
			variableList.add(variable);
		}
	}

	public void addTriple(String subject, String predicate, String object) {
		if (predicate.startsWith("P")) predicate = "wdt:" + predicate;
		if (object.startsWith("Q")) object = "wd:" + object;
		WikidataTriple triple = new WikidataTriple(subject, predicate, object);
		tripleList.add(triple);
	}

	/**
	 * create 
				"    OPTIONAL {\n" + 
				"      ?wikipedia schema:about ?wikidata .\n" + 
				"      ?wikipedia schema:inLanguage \"en\" .\n" + 
				"      ?wikipedia schema:isPartOf <https://en.wikipedia.org/> .\n
				"    } 
	 * @param container
	 * @param item
	 * @param lang

	 * 		
	OPTIONAL {
				?wikipedia schema:about ?molecule . 
				?wikipedia schema:inLanguage "en" .
				?wikipedia schema:isPartOf <https://en.wikipedia.org/> .
}
	 * 
	 */
	public void addWikidataLink(String container, String item, String lang) {
		optional = new WikidataOptional();
		optional.addTriple(new WikidataTriple(container, "schema:about", item));
		optional.addTriple(new WikidataTriple(container, "schema:inLanguage", "\""+lang+"\""));
		optional.addTriple(new WikidataTriple(container, "schema:isPartOf", "<https://en.wikipedia.org/>"));
		
	}

	/**
	  SERVICE wikibase:label {  + 
		bd:serviceParam wikibase:language "[AUTO_LANGUAGE],en,hi" + 
	}
	*/

	public void setLabelService(String... langs) {
		languageList = new ArrayList<>();
		for (String lang : langs) {
			if (lang.equalsIgnoreCase("AUTO")) {
				lang = "[AUTO_LANGUAGE]";
			}
			languageList.add(lang);
		}
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public String createQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append(selectHeader() + " \n");
		for (WikidataTriple triple : tripleList) {
			sb.append(triple.toString() + "\n");
		}
		sb.append(labelService() + "\n");
		if (optional != null) {
			sb.append(optional.createSparql());
		}
		sb.append("}\n"); // the header
		if (limit > 0) {
			sb.append("LIMIT "+limit + "\n");
		}
		return sb.toString();
	}

	/**
    SERVICE wikibase:label {
		bd:serviceParam wikibase:language "[AUTO_LANGUAGE],en,hi" 
	} 
	 * @return
	 */
	private String labelService() {
		String s = "SERVICE wikibase:label {\n"
				+ " bd:serviceParam wikibase:language \"";
		int i = 0;
		for (String lang : languageList) {
			if (i++ > 0) s += ",";
			s += lang;
		}
		s += "\"";
		s += "\n}";
		return s;
	}

	private String selectHeader() {
		String s = "SELECT";
		for (String variable : variableList) {
			s += " " + variable;
		}
		s += " " + "WHERE {";
		return s;
	}
}
class WikidataOptional {

	private List<WikidataTriple> tripleList;
	
	public WikidataOptional() {
		tripleList = new ArrayList<>();
	}
	
	public void addTriple(WikidataTriple wikidataTriple) {
		tripleList.add(wikidataTriple);
	}

	public String createSparql() {
		String s = "OPTIONAL {" + "\n";
		for (WikidataTriple triple : tripleList) {
			s += triple.toString() + "\n";
		}
		s += "}" + "\n";
		return s;
	}
}

class WikidataTriple {

	private String subject;
	private String predicate;
	private String object;

	public WikidataTriple(String subject, String predicate, String object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public String getObject() {
		return object;
	}
	
	public String toString() {
		String s = subject + " " + predicate + " " + object + " .";
		return s;
	}
}
