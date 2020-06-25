package org.contentmine.graphics.svg.util;

import java.awt.Color;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.util.ColorStore.ColorizerType;
import org.junit.Test;

public class ColorStoreTest {
	private static final Logger LOG = LogManager.getLogger(ColorStoreTest.class);
	
@Test
	public void testColorStore() {
		ColorStore colorStore = ColorStore.createColorizer(ColorizerType.CONTRAST);
		Iterator<String> colorIterator = colorStore.getColorIterator();
		int count = 0;
		while (count++ < 20 && colorIterator.hasNext()) {
			LOG.trace(">> "+colorIterator.next());;
		}
	}
	
	@Test
	public void testJavaColorStore() {
		ColorStore colorStore = ColorStore.createColorizer(ColorizerType.CONTRAST);
		Iterator<Color> colorIterator = colorStore.getJavaColorIterator();
		int count = 0;
		while (count++ < 20 && colorIterator.hasNext()) {
			LOG.trace(">> "+colorIterator.next());;
		}
	}
	@Test
	public void testJavaColorStore1() {
		Iterator<Color> colorIterator = ColorStore.getJavaColorIterator(ColorizerType.CONTRAST);
		int count = 0;
		while (count++ < 20 && colorIterator.hasNext()) {
			LOG.trace(">> "+colorIterator.next());;
		}
	}
}
