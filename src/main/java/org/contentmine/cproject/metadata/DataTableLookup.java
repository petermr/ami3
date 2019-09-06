package org.contentmine.cproject.metadata;

import java.io.IOException;

import nu.xom.Element;

/** allows DataTablesTool to look up stuff from the web.
 * 
 * @author pm286
 *
 */
public interface DataTableLookup {

	/**
	 * run sparql query (e.g. Wikipedia)
	 * 
	 * @param query
	 * @return
	 */
	Element createSparqlLookup(String query) throws IOException;

}
