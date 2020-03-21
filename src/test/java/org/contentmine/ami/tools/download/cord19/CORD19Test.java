package org.contentmine.ami.tools.download.cord19;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMITest;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
# JSON schema of full text documents


{
    "paper_id": <str>,                      # 40-character sha1 of the PDF
    "metadata": {
        "title": <str>,
        "authors": [                        # list of author dicts, in order
            {
                "first": <str>,
                "middle": <list of str>,
                "last": <str>,
                "suffix": <str>,
                "affiliation": <dict>,
                "email": <str>
            },
            ...
        ],
        "abstract": [                       # list of paragraphs in the abstract
            {
                "text": <str>,
                "cite_spans": [             # list of character indices of inline citations
                                            # e.g. citation "[7]" occurs at positions 151-154 in "text"
                                            #      linked to bibliography entry BIBREF3
                    {
                        "start": 151,
                        "end": 154,
                        "text": "[7]",
                        "ref_id": "BIBREF3"
                    },
                    ...
                ],
                "ref_spans": <list of dicts similar to cite_spans>,     # e.g. inline reference to "Table 1"
                "section": "Abstract"
            },
            ...
        ],
        "body_text": [                      # list of paragraphs in full body
                                            # paragraph dicts look the same as above
            {
                "text": <str>,
                "cite_spans": [],
                "ref_spans": [],
                "eq_spans": [],
                "section": "Introduction"
            },
            ...
            {
                ...,
                "section": "Conclusion"
            }
        ],
        "bib_entries": {
            "BIBREF0": {
                "ref_id": <str>,
                "title": <str>,
                "authors": <list of dict>       # same structure as earlier,
                                                # but without `affiliation` or `email`
                "year": <int>,
                "venue": <str>,
                "volume": <str>,
                "issn": <str>,
                "pages": <str>,
                "other_ids": {
                    "DOI": [
                        <str>
                    ]
                }
            },
            "BIBREF1": {},
            ...
            "BIBREF25": {}
        },
        "ref_entries":
            "FIGREF0": {
                "text": <str>,                  # figure caption text
                "type": "figure"
            },
            ...
            "TABREF13": {
                "text": <str>,                  # table caption text
                "type": "table"
            }
        },
        "back_matter": <list of dict>           # same structure as body_text
    }
}
 * @author pm286
 *
 */
public class CORD19Test extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(CORD19Test.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static File PROJECTS = new File("/Users/pm286/projects");
	public static File OPEN_VIRUS = new File(PROJECTS, "openVirus");
	public static File CORD19 = new File(OPEN_VIRUS, "cord19");
	public static File BIORXIV_MEDRXIV = new File(CORD19, "biorxiv_medrxiv");

	@Test
	public void testReadJSON() {
		 
		File jsonFile = new File(BIORXIV_MEDRXIV, "b801b7f92cff2155d98f0e3404229c67b60e2f9f.json");
		JsonObject oo = null;
		try {
			String resultsJsonString = IOUtils.toString(new FileInputStream(jsonFile), "UTF-8");
		    JsonParser parser = new JsonParser();
		    oo = (JsonObject) parser.parse(resultsJsonString);
		    
		} catch (Exception e) {
			throw new RuntimeException("Cannot read CORD19 file: "+jsonFile, e);
		}
		
//		JsonObject response = (JsonObject) hgncJson.get("response");
//		int numTerms = response.get("numFound").getAsInt();
//		JsonArray docs = response.get("docs").getAsJsonArray();
//		createIds(docs, numTerms);
		
	    
	    String paperId = oo.get("paper_id").getAsString();
	    System.out.println("id: "+paperId);
	    JsonElement metadata = oo.get("metadata");
	    JsonObject metadataObject = metadata.getAsJsonObject();
  	    String title = metadataObject.get("title").getAsString();
	    System.out.println("title: "+title);
  	    JsonArray authors = metadataObject.get("authors").getAsJsonArray();
	    System.out.println("authors: "+authors);
  	    JsonElement abstrakt = metadataObject.get("abstract");
	    System.out.println("abstract: "+abstrakt);
  	    JsonElement bodyText = metadataObject.get("body_text");
	    System.out.println("bodyText: "+bodyText);
  	    JsonElement boo = metadataObject.get("boo");
	    System.out.println("bodyText: "+boo);

	}
	 
}
