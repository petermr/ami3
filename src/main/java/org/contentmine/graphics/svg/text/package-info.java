/**
 * This is complex and evolving.
 * TextChunkCache is a top-level Cache for TextChunks. TextChunks are created by a mixture of 
 * heuristics - geometry, whitespace, font styles, content, proximity to other
 * graphics objects (boxes, etc.). TextChunkCache may reallocate PhraseChunks between 
 * TextChunks.
 * 
 * Characters are assembled into Words (WordNew ATM) based on horizontal proximity 
 * and no intervening whitespace
 * ThisIsAWord
 * but
 * This is a Phrase
 * and
 * This is a     PhraseChunk    composed of 3 Phrases
 * --------------
 * These are three PhraseChunks
 * because of the vertical separation
 * They are a possible TextChunk
 * -----------------
 * These are
 * two possible
 * 
 * TextChunks
 * and might be 
 * held as a List<TextChunk>
 * 
 * 
 * Phrases developed in 2017 mainly for Tables
 * These have much overlap with SVGPhrase, SVGWordBlock etc. which were developed
 * for Phylogenetics diagram. It is likely that the two approaches will be harmonised
 * 
 */
/**
 * @author pm286
 *
 */
package org.contentmine.graphics.svg.text;