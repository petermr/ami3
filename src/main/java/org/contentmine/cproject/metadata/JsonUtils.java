package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JsonUtils {

	private static final Logger LOG = Logger.getLogger(JsonUtils.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static Number getNumber(JsonPrimitive prim) {
		Number number = null;
		if (prim.isNumber()) {
			try {
				number = (Long) prim.getAsLong();
			} catch (Exception e) {
				try {
					number = (Double) prim.getAsDouble();
				} catch (Exception ee) {
					// null
				}
			}
		}
		return number;
	}

	public static String getString(JsonPrimitive prim) {
		String s = null;
		if (prim.isString()) {
			try {
				s = prim.getAsString();
			} catch (Exception e) {
				// null
			}
		}
		return s;
	}
	
	public static JsonArray getArrayFromSingleElement(JsonElement value) {
		JsonArray array1 = null;
		if (value != null && value.isJsonArray()) {
			JsonArray jsonArray = value.getAsJsonArray();
			if (jsonArray.size() == 1) {
				JsonElement jsonElement = jsonArray.get(0);
				if (jsonElement.isJsonArray()) {
					array1 = jsonElement.getAsJsonArray();
				}
			}
		}
		return array1;
	}

	public static List<Long> getLongs(JsonArray array) {
		List<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).isJsonPrimitive()) {
				try {
					long lng = array.get(i).getAsLong();
					list.add(lng);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return list;
	}

	public static String getString(JsonArray array) {
		String s = null;
		if (array != null) {
			if (array.size() == 1) {
				s = array.getAsString();
			} else if (array.size() > 1) {
				s = array.get(0).getAsString();
			}
		}
		return s;
	}

	public static String getFirstStringValue(JsonArray array) {
		String s = null;
		if (array != null && array.size() > 0) {
			s = array.get(0).getAsString();
		}
		return s;
	}

	public static List<String> getStringList(JsonArray array) {
		List<String> stringList = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			String s = array.get(i).toString();
			stringList.add(s);
		}
		return stringList;
	}

	public static List<JsonElement> getListFromFile(File file) throws IOException {
		String s = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
		JsonArray array = (JsonArray) new JsonParser().parse(s);
		List<JsonElement> elements = new ArrayList<JsonElement>();
		for (int i = 0; i < array.size(); i++) {
			elements.add(array.get(i));
		}
		return elements;
	}
	
	public static JsonElement parseJson(File file) throws IOException {
		return new JsonParser().parse(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
	}




}
