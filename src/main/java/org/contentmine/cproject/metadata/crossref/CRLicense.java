package org.contentmine.cproject.metadata.crossref;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *       {
        "content-version": "tdm",
        "delay-in-days": 0,
        "start": {
          "date-parts": [
            [
              2016,
              6,
              1
            ]
          ],
          "date-time": "2016-06-01T00:00:00Z",
          "timestamp": 1464739200000
        },
        "URL": "http://doi.wiley.com/10.1002/tdm_license_1"
      },
      {
        "content-version": "vor",
        "delay-in-days": 0,
        "start": {
          "date-parts": [
            [
              2016,
              6,
              1
            ]
          ],
          "date-time": "2016-06-01T00:00:00Z",
          "timestamp": 1464739200000
        },
        "URL": "http://onlinelibrary.wiley.com/termsAndConditions"
      }

      
 * @author pm286
 *
 */
public class CRLicense {

	private static final Logger LOG = Logger.getLogger(CRLicense.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String url;
	public CRLicense() {
		
	}

	public static CRLicense createFrom(JsonObject jsonAuthor) {
		CRLicense license = new CRLicense();
		for (Map.Entry<String, JsonElement> entry : jsonAuthor.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if ("URL".equals(key)) {
				license.url = value.getAsString();
			} else if (
					"content-version".equals(key) ||
					"delay-in-days".equals(key) ||
					"start".equals(key))
					{
				// ignore all other stuff
			} else {
				throw new RuntimeException("unknown license field: "+key);
			}
		}
		return license;
	}

	public String toString() {
		String s = "";
		if (url != null) {
			s += url;
		}
		return s;
	}
	

}
