package org.contentmine.graphics.svg.cache;

/** a Cache(Processor) is a collection of SVG objects.
 * Caches ingest colections of objects and release them on demand.
 * Generally they also include Processing which adds value by tidying,
 * normalization, or creating higher objects. 
 * Caches are usually selfcontained but may have references to sibling
 * caches, either their more primitive predecessor or siblings they 
 * wish to use to create highr level objects.
 * 
 * For example TextCache ingests raw characters and normalizes them
 * TextChunkCache ingests TextCache(s) and assembles characters into TextChunks
 * 
 * The hierarchy is currently:
 * characters -> TextCache -> TextChunkCache
 * paths -> PathCache -> ShapeCache -> RectCache and LineCache
 * RectCache + other caches espTextChunkCache -> ContentBoxCache
 * 
 * ComponentCaches are normally created for a given problem (e.g. SVG page or part of page)
 * and contain exactly one of all the other concreate caches, even if they have no significant
 * content. 
 * 
 * In some cases content is contained in more than one cache. It may be referenced but may also 
 * sometimes be copied. 
 * 
*/

