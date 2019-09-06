package org.contentmine.eucl.stern_library.string;

import org.contentmine.eucl.sternLibrary.string.DamerauLevenshteinAlgorithm;
import org.junit.Assert;
import org.junit.Test;

public class StringsTest {

	@Test
	public void testLevenstein() {
		// no idea what are good values
		int deleteCost = 1;
		int insertCost = 1;
		int replaceCost = 1;
		int swapCost = 1;
		DamerauLevenshteinAlgorithm dl = new DamerauLevenshteinAlgorithm(deleteCost, insertCost, replaceCost, swapCost);
		// from rosettacode.org/wiki/Levenshtein_distance ; values work
		String source = "kitten";
		String target = "sitting";
		int distance = dl.execute(source, target);
		Assert.assertEquals("distance", 3, distance);
		distance = dl.execute("rosettacode", "raisethysword");
		Assert.assertEquals("rosettacode", 8, distance);
	}
	
}
