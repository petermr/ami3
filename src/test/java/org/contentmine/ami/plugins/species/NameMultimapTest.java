package org.contentmine.ami.plugins.species;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.species.NameMultimap;
import org.junit.Assert;
import org.junit.Test;

public class NameMultimapTest {

	public static final Logger LOG = Logger.getLogger(NameMultimapTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testNameMultimap() {
		new NameMultimap();
	}
	
	@Test
	public void testNameMultimapId() {
		NameMultimap nameMultimap = new NameMultimap();
		List<String> names = nameMultimap.searchByKey("species:ncbi:274");
		Assert.assertEquals("Thermus thermophilus", "["
				+ "Thermus aquaticus, "
				+ "Thermus themophilus, "
				+ "T. aquaticus, "
				+ "Flavobacterium thermophilum, "
				+ "Thermua flavus, "
				+ "Thermus flavus, "
				+ "F. thermophilum, "
				+ "T. thermophilus, "
				+ "Thermus thermophilus, "
				+ "T. flavus"
				+ "]",
				names.toString());
	}
	
	@Test
	public void testNameMultimapByName() {
		NameMultimap nameMultimap = new NameMultimap();
		List<String> ids = nameMultimap.searchByNameValue("T. thermophilus");
		Assert.assertEquals("T.therm", "["
				+ "species:ncbi:274]",
				ids.toString());
	}
	@Test
	public void testAmbiguousNames() {
		NameMultimap nameMultimap = new NameMultimap();
		List<String> names = nameMultimap.getNames();
		for (String name : names) {
			if (Pattern.compile("[A-Z][a-z]?\\..*").matcher(name).matches()) continue;
			List<String> idList = nameMultimap.searchByNameValue(name);
			if (idList.size() > 1) {
//				System.out.println("----------------"+name+": "+idList);
				for (String id : idList) {
					List<String> nameList = nameMultimap.searchByKey(id);
//					System.out.println(id+": "+nameList);
				}
			}
		}
	}

}
