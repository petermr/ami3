package org.contentmine.cproject.metadata.bibjson;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * {
"title": "Open Bibliography for Science, Technology and Medicine",
"author":[
    {"name": "Richard Jones"},
    {"name": "Mark MacGillivray"},
    {"name": "Peter Murray-Rust"},
    {"name": "Jim Pitman"},
    {"name": "Peter Sefton"},
    {"name": "Ben O'Steen"},
    {"name": "William Waites"}
],
"type": "article",
"year": "2011",
"journal": {"name": "Journal of Cheminformatics"},
"link": [{"url":"http://www.jcheminf.com/content/3/1/47"}],
"identifier": [{"type":"doi","id":"10.1186/1758-2946-3-47"}]
}

 */

public class BibJSON extends AbstractBibJSON {

	private static final Logger LOG = Logger.getLogger(BibJSON.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String type;
	private List<BJAuthor> author;
	private List<BJLink> link;
	private BJJournal journal;
	private List<BJIdentifier> identifier;
	private String title;
	private String date;
	

	public BibJSON() {
		
	}
	
	
	public String getAbstract() {
		LOG.debug("NYI");
		return null;
	}

	
	public String getAbstractURL() {
		LOG.debug("NYI");
		return null;
	}

	
	public String getAuthorEmail() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getAuthorInstitution() {
        LOG.debug("NYI");
		return null;
	}

	
	public List<String> getAuthorList() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getCitations() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getCopyright() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getCreator() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getDate() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getDescription() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getDOI() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getFirstPage() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getLastPage() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getFulltextHTMLURL() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getFulltextPDFURL() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getFulltextPublicURL() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getFulltextXMLURL() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getISSN() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getIssue() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getKeywords() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getJournal() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getLicense() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getLanguage() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getPublicURL() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getPublisher() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getRights() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getTitle() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getURL() {
        LOG.debug("NYI");
		return null;
	}

	
	public String getVolume() {
        LOG.debug("NYI");
		return null;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addAuthor(String authorS) {
		if (author == null) {
			author = new ArrayList<BJAuthor>();
		}
		BJAuthor auth = new BJAuthor();
		auth.setName(authorS);
		author.add(auth);
		
	}

	public void setDOI(String doi) {
		if (identifier == null) {
			identifier = new ArrayList<BJIdentifier>();
		}
		BJIdentifier ident = new BJIdentifier("doi", doi);
		identifier.add(ident);
	}

	public void setUrl(String url) {
		if (this.link == null) {
			link = new ArrayList<BJLink>();
		}
		link.add(new BJLink(url));
		
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setJournal(String journal) {
        this.setJournal(new BJJournal(journal));
	}

	public void setJournal(BJJournal journal) {
		this.journal = journal;
	}

	/**
	 * this does not output JSON
	 * 
	private String type;
	private List<BJAuthor> authorList;
	private List<BJLink> link;
	private BJJournal journal;
	private List<BJIdentifier> identifier;
	private String title;
	private String date;
	 */
	public String toString() {
		return super.toString();
//		String s = gson.toJson(this);
//		String s = "";
//		s += type == null ? "" : "type: "+type+"\n";
//		s += title == null ? "" : "title: "+title+"\n";
//		s += date == null ? "" : "date: "+date+"\n";
//		s += journal == null ? "" : "journal: "+journal+"\n";
//		return s;
	}
}
