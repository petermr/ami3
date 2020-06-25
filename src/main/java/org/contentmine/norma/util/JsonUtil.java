package org.contentmine.norma.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtil {

	
	private static final Logger LOG = LogManager.getLogger(JsonUtil.class);
/** extract an array of strings from a Json object.
	 * 
	 * @param jsonObject
	 * @param name
	 * @return
	 */
	public static List<String> getAsStringArray(JsonObject jsonObject, String name ) {
		JsonElement element = jsonObject.get(name);
		List<String> stringList = new ArrayList<String>();
		if (element.isJsonArray()) {	
			JsonArray jsonArray = element.getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				stringList.add(jsonArray.get(i).getAsString());
			}
		} else {
			stringList.add(element.getAsString());
		}
		return stringList;
	}
	
	public static String getString(JsonObject jsonObject, String name) {
		JsonElement element = jsonObject.get(name);
		return element == null ? null : element.getAsString();
	}
	
	public static Integer getInteger(JsonObject jsonObject, String name) {
		JsonElement element = jsonObject.get(name);
		return element == null ? null : element.getAsInt();
	}
	
	public static JsonObject createInteger(String name, Integer integer) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(name, integer);
		return jsonObject;
	}

}
