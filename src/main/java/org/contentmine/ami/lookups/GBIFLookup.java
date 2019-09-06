package org.contentmine.ami.lookups;

import java.io.IOException;

import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.lookup.AbstractLookup;

/**
 * Lookup common names (etc.) in GBIF.
 * 
 * API call e.g. http://api.gbif.org/v1/species/search?q=Puma&rank=GENUS
 * API call e.g. http://api.gbif.org/v1/species/search?q=Blue+whale
 *  
 *  JSON output:
 *  	    /**
"offset": 0,
"limit": 20,
"endOfRecords": false,
"count": 101,
"results": 
[

{

    "key": 116841966,
    "datasetKey": "cbb6498e-8927-405a-916b-576d00a6289b",
    "nubKey": 2440735,
    "parentKey": 143047347,
    "parent": "Balaenoptera",
    "kingdom": "Animalia",
    "phylum": "Chordata",
    "family": "Balaenopteridae",
    "genus": "Balaenoptera",
    "species": "Balaenoptera musculus",
    "kingdomKey": 116630539,
    "phylumKey": 143035196,
    "classKey": 143045262,
    "familyKey": 143047346,
    "genusKey": 143047347,
    "speciesKey": 116841966,
    "scientificName": "Balaenoptera musculus (Linnaeus, 1758)",
    "canonicalName": "Balaenoptera musculus",
    "authorship": " (Linnaeus, 1758)",
    "nameType": "WELLFORMED",
    "rank": "SPECIES",
    "numDescendants": 0,
    "numOccurrences": 0,
    "taxonID": "4925",
    "habitats": [ ],
    "nomenclaturalStatus": [ ],
    "threatStatuses": [ ],	    
"descriptions": [

{

    "description": "The blue whale (Balaenoptera musculus) is a marine mammal belonging to the baleen whales 
    (Mysticeti). At 30 m in length and 190 tonne or more in weight, it is the largest existing animal and the 
    heaviest that has ever existed.Long and slender, the blue whale's body can be various shades of bluish-grey 
    dorsally and somewhat lighter underneath. There are at least three distinct subspecies: B. m. musculus of the 
    North Atlantic and North Pacific, B. m. intermedia of the Southern Ocean and B. m. brevicauda (also known as 
    ...
    As of 2014, the Californian blue whale population has rebounded to nearly its pre-hunting population."

},
[... omitted]

],
"vernacularNames": 
[

    {
        "vernacularName": "Blue whale",
        "language": "eng"
    }

],
"synonym": false,
"higherClassificationMap": 

    {
        "116630539": "Animalia",
        "143035196": "Chordata",
        "143045262": "Mammalia",
        "143047346": "Balaenopteridae",
        "143047347": "Balaenoptera"
    },
    "class": "Mammalia"

},

 * see also
 * http://iphylo.blogspot.com.es/2012/06/linking-ncbi-taxonomy-to-gbif.html
 * 
 * @author pm286
 *
 */
public class GBIFLookup extends AbstractLookup {
	
	private static final Logger LOG = Logger.getLogger(GBIFLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public GBIFLookup() {
	}

	/*
http://api.gbif.org/v1/species/search?q=Puma&rank=GENUS
*/	
	public String lookup(String commonName) throws IOException {
		urlString = "http://api.gbif.org/v1/species/search?q="+commonName;
		return getResponse(url);
	}

}
