package org.contentmine.cproject.metadata.bibjson;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** mainly common classes such as toString() for BibJSON objects.
 * 
 * @author pm286
 *
 */
public abstract class AbstractBibJSON {

	private static final Logger LOG = Logger.getLogger(AbstractBibJSON.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private transient Gson gson;
	
	public String toString() {
		if (gson == null) {
			gson = new GsonBuilder()
		        .disableHtmlEscaping()
//		        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
		        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
		        .setPrettyPrinting()
//		        .serializeNulls()
		        .create();
		}
		return gson.toJson(this);

	}
}
