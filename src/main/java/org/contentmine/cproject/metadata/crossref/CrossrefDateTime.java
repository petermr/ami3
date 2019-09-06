package org.contentmine.cproject.metadata.crossref;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.JsonUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CrossrefDateTime {

	private static final Logger LOG = Logger.getLogger(CrossrefDateTime.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private JsonObject jsonObject;

	public CrossrefDateTime() {
		
	}
	
	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public static String createFrom(JsonElement value) {
		String crDateTime = null;
		if (value != null) {
			JsonArray array = JsonUtils.getArrayFromSingleElement(value);
			crDateTime = createDateTimeString(array);
		}
		return crDateTime;
	}

	private static String createDateTimeString(JsonArray array) {
		String dateTime = null;
		if (array != null) {
			List<Long> longs  = JsonUtils.getLongs(array);
			if (longs != null && longs.size() > 0 && longs.size() < 4) {
				dateTime = createDateString(longs);
			}
		}
		return dateTime;
	}

	private static String createDateString(List<Long> longs) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < longs.size(); i++) {
			if (i > 0) {
				sb.append("-");
			}
			sb.append(longs.get(i));
		}
		return sb.toString();
	}

}
