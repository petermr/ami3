package org.contentmine.graphics.svg.text.build;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.TextCache;
import org.contentmine.graphics.svg.fonts.StyleRecord;
import org.contentmine.graphics.svg.fonts.StyledBoxRecordSet;
import org.junit.Test;

import junit.framework.Assert;


/** 
 * tests Phrases (isolated chunks of text, normally with the "same" y-coordinate)
 * 
 * Phrases can be combined into PhraseCuhunks and then into TextChunks. The exact use of these
 * is under exploration.
 * 
 * 
 * @author pm286
 *
 */

public class PhraseTest {
	private static final Logger LOG = LogManager.getLogger(PhraseTest.class);
}
