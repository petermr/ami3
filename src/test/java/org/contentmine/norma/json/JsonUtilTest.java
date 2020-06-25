package org.contentmine.norma.json;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtilTest {
	
	private static final Logger LOG = LogManager.getLogger(JsonUtilTest.class);
@Test
	public void testCreateInteger() {
		JsonObject  jsonObject = JsonUtil.createInteger("testInteger", 3);
		JsonElement testInteger = jsonObject.get("testInteger");
		Integer testInt = (Integer) testInteger.getAsInt();
		Assert.assertEquals(3,  (int) testInt); 
	}
}
