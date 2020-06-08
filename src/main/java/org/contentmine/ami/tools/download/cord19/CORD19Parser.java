package org.contentmine.ami.tools.download.cord19;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.html.HtmlBr;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlUl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CORD19Parser {
	private static final Logger LOG = Logger.getLogger(CORD19Parser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	int level = 0;
	private HtmlElement currentElement;
	
	public CORD19Parser() {
		currentElement = new HtmlDiv();
	}
	
	public HtmlDiv parse(JsonObject object) {
		level++;
		Set<Entry<String,JsonElement>> entrySet = object.entrySet();
		HtmlDiv divTop = new HtmlDiv();
//		div.setTitle(key);
		for (Entry<String, JsonElement> entry : entrySet) {
			String key = entry.getKey();
			HtmlDiv div = new HtmlDiv();
			div.setTitle(key);
			divTop.appendChild(div);
			JsonElement value = entry.getValue();
			div.appendChild(parse(value));
		}
		level--;
		return divTop;
	}

	private HtmlElement parse(JsonArray array) {
		int size = array.size();
		if (size == 0) {
			return new HtmlBr();
		} else if (size == 1) {
			HtmlDiv div = new HtmlDiv();
			div.setTitle("single");
			div.appendChild(parse(array.get(0)));
			return div;
		} else {
			HtmlUl ul = new HtmlUl();
			for (JsonElement element : array) {
				HtmlElement li = new HtmlLi();
				ul.appendChild(li);
				HtmlElement el = parse(element);
				li.appendChild(el);
			}
			return ul;
		}
	}

	private HtmlSpan parse(JsonPrimitive primitive) {
		/**
		if (primitive.isNumber()) {
			Number n = primitive.getAsNumber();
			if (n instanceof Double) {
				printLevel("D: "+n);
			} else {
				printLevel("I: "+n);				
			}
		} else if (primitive.isBoolean()) {
			printLevel("B: "+primitive.getAsBoolean());
		} else if (primitive.isString()) {
			printLevel("S: "+primitive.getAsString());
		} else {
			printLevel("U: "+primitive);
		}
		*/
		HtmlSpan span = new HtmlSpan();
		span.setValue(primitive.getAsString());
		return span;
	}
	
	private HtmlSpan parse(JsonNull jsonNull) {
		return new HtmlSpan();
	}

	private HtmlElement parse(JsonElement element) {
		if (element instanceof JsonArray) {
			return parse((JsonArray) element);
		} else if (element instanceof JsonObject) {
			return parse((JsonObject) element);
		} else if (element instanceof JsonPrimitive) {
			return parse((JsonPrimitive) element);
		} else if (element instanceof JsonNull) {
			return parse((JsonNull) element);
		} else {
			printLevel("unknown element "+element.getClass());
			HtmlDiv div = new HtmlDiv();
			div.setTitle("unknown");
			return div;
		}
	}
	
	private void printLevel(String string) {
		int l2 = level * 4;
		System.out.println(Util.spaces(l2) + string);
		
	}

}
