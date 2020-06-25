package org.contentmine.norma.json;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

public class JsonPathTest {

	
	private static final Logger LOG = LogManager.getLogger(JsonPathTest.class);
@Test
	public void testExample0() throws IOException {
		String json = FileUtils.readFileToString(new File(NormaFixtures.TEST_JSON_DIR, "jsonpathExample.json"), CMineUtil.UTF8_CHARSET);
		ReadContext ctx = JsonPath.parse(json);
		List<String> authorsOfBooksWithISBN = ctx.read("$.store.book[?(@.isbn)].author");
		for (String author : authorsOfBooksWithISBN) {
			LOG.trace("auth >"+author);
		}
		List<Map<String, Object>> expensiveBooks = (List<Map<String, Object>>) JsonPath
//		                            .using(configuration)
		                            .parse(json)
		                            .read("$.store.book[?(@.price > 10)]", List.class);
		for (Map<String, Object> book : expensiveBooks) {
			LOG.trace("book "+book);
		}
	}
}
