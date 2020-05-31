package org.contentmine.ami.tools.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import nu.xom.Attribute;
import nu.xom.Element;

public class LuceneTools {

	/**
	   * This demonstrates a typical paging search scenario, where the search engine presents 
	   * pages of size n to the user. The user can then go to the next page if interested in
	   * the next hits.
	   * 
	   * When the query is executed for the first time, then only enough results are collected
	   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
	   * is executed another time and all hits are collected.
	   * 
	 */
	public static void doPagingSearch(IndexSearcher searcher, Query query, 
	                                   int hitsPerPage) throws IOException {
	
	  // Collect enough docs to show 5 pages
	  TopDocs results = searcher.search(query, 5 * hitsPerPage);
	  ScoreDoc[] hits = results.scoreDocs;
	  
	  int numTotalHits = Math.toIntExact(results.totalHits.value);
	  System.out.println(numTotalHits + " total matching documents");
	
	  int start = 0;
	  int end = Math.min(numTotalHits, hitsPerPage);
	  hits = searcher.search(query, numTotalHits).scoreDocs;
	    
		end = Math.min(hits.length, start + hitsPerPage);
		
		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			System.out.println((i+1) + ". " + doc.get("path") + " Title: " + doc.get("title"));
		}
	}
	
	/** reads a directory contents and creates index
	 * 
	 * @param inputDir
	 * @param indexPath
	 * @param create
	 */
	public static void createDefaultLuceneIndex(String inputDir, String indexPath, boolean create) {
		final Path docDir = Paths.get(inputDir);
	    if (!Files.isReadable(docDir)) {
	    	System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
	    } else {
	    
		    try {
				System.out.println("Indexing to directory '" + indexPath + "'...");
				
				Directory dir = FSDirectory.open(Paths.get(indexPath));
				Analyzer analyzer = new StandardAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				
				iwc.setOpenMode(create ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
				IndexWriter writer = new IndexWriter(dir, iwc);
				indexDocs(writer, docDir);
				
				writer.close();
	
		    } catch (IOException e) {
		    	System.out.println(" caught a " + e.getClass() +
		       "\n with message: " + e.getMessage());
		    }
	    }
	}
	
	/**
	 * 
	 * NOTE: This method indexes one document per input file.This is slow.For good
	 * throughput, put multiple documents into your input file(s).An example of this is
	 * in the benchmark module, which can create "line doc" files, one document per line,
	 * using the
	 * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 *
	 * @param writer Writer to the index where the given file/dir info will be stored
	 * @param path The file to index, or the directory to recurse into to find files to index
	 * @throws IOException If there is a low-level I/O error
	 */
	public static void indexDocs(final IndexWriter writer, Path path) throws IOException {
	    if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
						// don't index files that can't be read.
					}
					return FileVisitResult.CONTINUE;
				}
			});
	    } else {
	    	indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
	    }
	}
	
	  /** Indexes a single document */
	private static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			Document doc = makeDocAndAddBasicFields(file, lastModified, stream);
			
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been indexed) so 
				// we use updateDocument instead to replace the old one matching the exact 
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
	
	/** creates a document and adds fields: 
	 * "path" (StringField)
	 *  // Add the path of the file as a field named "path".    Use a
	    // field that is indexed (i.e. searchable), but don't tokenize 
	    // the field into separate words and don't index term frequency
	    // or positional information:
	
	 * "modified" (Date) (LongPoint)
	 *  // Add the last modified date of the file a field named "modified".
	    // Use a LongPoint that is indexed (i.e. efficiently filterable with
	    // PointRangeQuery).    This indexes to milli-second resolution, which
	    // is often too fine.    You could instead create a number based on
	    // year/month/day/hour/minutes/seconds, down the resolution you require.
	    // For example the long value 2011021714 would mean
	    // February 17, 2011, 2-3 PM.
	
	 * "contents" (TextField)
	    // Add the contents of the file to a field named "contents".    Specify a Reader,
	    // so that the text of the file is tokenized and indexed, but not stored.
	    // Note that FileReader expects the file to be in UTF-8 encoding.
	    // If that's not the case searching for special characters will fail.
	 * 
	 * @param path
	 * @param lastModified
	 * @param stream
	 * @return
	 */
	
	private static Document makeDocAndAddBasicFields(Path path, long lastModified, InputStream stream) {
	// make a new, empty document
	    Document doc = new Document();
	    
	    Field pathField = new StringField("path", path.toString(), Field.Store.YES);
	    doc.add(pathField);
	    
	    doc.add(new LongPoint("modified", lastModified));
	    
	    doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
		return doc;
	}

    
    public static List<Element> searchForDocument(IndexSearcher searcher, List<String> searchVal)
    {
       List<Element> retVal = new ArrayList<Element>();

       try
       {

          QueryBuilder bldr = new QueryBuilder(new StandardAnalyzer());
          Query q1 = bldr.createPhraseQuery(searchVal.get(0), searchVal.get(1));

          BooleanQuery.Builder chainQryBldr = new BooleanQuery.Builder();
          chainQryBldr.add(q1, Occur.SHOULD);

          BooleanQuery finalQry = chainQryBldr.build();
          System.out.println("FQ "+finalQry);
          System.out.println("REF "+searcher.getIndexReader().getRefCount());
          System.out.println("SS "+searcher.collectionStatistics("id"));
          TopDocs allFound = searcher.search(finalQry, 100);
          System.out.println("ALL "+allFound.totalHits);
          if (allFound.scoreDocs != null)
          {
             for (ScoreDoc doc : allFound.scoreDocs)
             {
                System.out.println("Score: " + doc.score);

                int docidx = doc.doc;
                Document docRetrieved = searcher.doc(docidx);
                if (docRetrieved != null)
                {
              	  List<IndexableField> fieldList = docRetrieved.getFields();
      			  for (IndexableField field : fieldList) {
      				  System.out.println("F "+field);
      			  }
                   Element resultsElement = new Element("results");

                   addNonNullField(docRetrieved, resultsElement, "id");
                   addNonNullField(docRetrieved, resultsElement, "path");
                }
             }
          }
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }

       return retVal;
    }

	private static void addNonNullField(Document docRetrieved, Element docToAdd, String name) {
		IndexableField field;
		field = docRetrieved.getField(name);
		 if (field != null) {
		    docToAdd.addAttribute(new Attribute(name, field.stringValue()));
		 }
	}
    


}
