package org.contentmine.ami.tools.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.contentmine.ami.dictionary.TermPhrase;
import org.contentmine.ami.wordutil.PorterStemmer;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * static utils for Lucene
 * 
 * @author pm286
 *
 */
/**
 * tips
 * https://howtodoinjava.com/lucene/lucene-search-highlight-example/
 * 
 */
public class LuceneUtils {
	
	private static final Logger LOG = Logger.getLogger(LuceneUtils.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String MODIFIED = "modified";
	private static final String CONTENTS = "contents";
	private static final String PATH = "path";

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
	    ScoreDoc[] scoreDocs = results.scoreDocs;
	  
	    int numTotalHits = Math.toIntExact(results.totalHits.value);
	    System.out.println(numTotalHits + " total matching documents");
	
	    int start = 0;
	    int end = Math.min(numTotalHits, hitsPerPage);
	    scoreDocs = searcher.search(query, numTotalHits).scoreDocs;
	    
	    printScoreDocs(searcher, scoreDocs);
	    
        end = Math.min(scoreDocs.length, start + hitsPerPage);
		
		  for (int i = start; i < end; i++) {
		  	  Document doc = searcher.doc(scoreDocs[i].doc);
			  System.out.println((i+1) + ". " + doc.get(PATH) + " Title: " + doc.get("title"));
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
//				System.out.println("adding " + file);
				System.out.print(".");
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been indexed) so 
				// we use updateDocument instead to replace the old one matching the exact 
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term(PATH, file.toString()), doc);
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
	    
	    if (LuceneUtils.isTextFile(path)) {
		    
		    addPath(path, doc);
		    addDateMillis(lastModified, doc);
		    addTextField(CONTENTS, stream, doc);
		    
		    for (IndexableField field : doc.getFields()) {
		    	System.out.println("Field "+field);
		    }
		    IndexableField field = doc.getField(CONTENTS);
		    field = doc.getField(MODIFIED);
	    }
		return doc;
	}

	private static void addTextField(String field, InputStream stream, Document doc) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		try {
			doc.add(new TextField(field, IOUtils.toString(reader), Field.Store.YES));
		} catch (IOException e) {
			throw new RuntimeException("cannot read reader", e);
		}
	}

	private static void addDateMillis(long lastModified, Document doc) {
		doc.add(new LongPoint(MODIFIED, lastModified));
	}

	private static void addPath(Path path, Document doc) {
		addStringField(PATH, path.toString(), doc, Field.Store.YES);
			
	}
	
	private static void addStringField(String field, String value, Document doc, Field.Store fieldStore) {
		Field pathField = new StringField(field, value, fieldStore);
		doc.add(pathField);
		
	}

    
    public static List<Element> searchForDocument(IndexSearcher searcher, List<String> searchVal) {
        List<Element> retVal = new ArrayList<Element>();

        try {
            QueryBuilder bldr = new QueryBuilder(new StandardAnalyzer());
            Query q1 = bldr.createPhraseQuery(searchVal.get(0), searchVal.get(1));

            BooleanQuery.Builder chainQryBldr = new BooleanQuery.Builder();
            chainQryBldr.add(q1, Occur.SHOULD);

            BooleanQuery finalQry = chainQryBldr.build();
            System.out.println("FinalQuery "+finalQry);
            if (searcher != null) {
	            System.out.println("RefCount "+searcher.getIndexReader().getRefCount());
	            System.out.println("SearcherStats "+searcher.collectionStatistics("id"));
	            TopDocs allFound = searcher.search(finalQry, 100);
	            System.out.println("AllHits "+allFound.totalHits);
	            ScoreDoc[] scoreDocs = allFound.scoreDocs;
	            
				listScoreDocs(searcher, scoreDocs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retVal;
    }

	private static void listScoreDocs(IndexSearcher searcher, ScoreDoc[] scoreDocs) throws IOException {
		if (scoreDocs != null) {
		    for (ScoreDoc doc : scoreDocs) {
		        System.out.println("Score: " + doc.score);

		        int docidx = doc.doc;
		        Document docRetrieved = searcher.doc(docidx);
		        if (docRetrieved != null) { 
		    	    List<IndexableField> fieldList = docRetrieved.getFields();
				    for (IndexableField field : fieldList) {
					    System.out.println("F "+field);
				    }
		            Element resultsElement = new Element("results");

		            addNonNullField(docRetrieved, resultsElement, "id");
		            addNonNullField(docRetrieved, resultsElement, PATH);
		        }
		    }
		}
	}

	private static void addNonNullField(Document docRetrieved, Element docToAdd, String name) {
		IndexableField field;
		field = docRetrieved.getField(name);
		 if (field != null) {
		    docToAdd.addAttribute(new Attribute(name, field.stringValue()));
		 }
	}
    
	public static void printScoreDocs(IndexSearcher searcher, ScoreDoc[] scoreDocs) throws IOException {
	    for (ScoreDoc sd : scoreDocs) {
	        Document d = searcher.doc(sd.doc);
	        System.out.println("Document Number : " + sd.doc + " :: Document Name : " + d.get("name")
	                + "  :: Content : " + d.get("content") + "  :: Score : " + sd.score);
	    }
	}

	/**
	 * creates a QueryParser from a filed and standardAnalyzer
	 * 
	 * @param field
	 * @param line
	 * @return
	 * @throws ParseException
	 */
	public static Query createQuery(String field, String line) {
		Analyzer analyzer = new StandardAnalyzer();
	    QueryParser parser = new QueryParser(field, analyzer);
	    Query query = null;
	    try {
			query = parser.parse(line);
		} catch (ParseException e) {
			throw new RuntimeException("cannot parse: " + line, e);
		}
		return query;
	}

	/** convenience method for creating TokenStream from a String.
	 * (mainly to save me looking it up)
	 * calls tokenStream.reset() // seems to be necessary
	 * NEVER call this again (I think)
	 * 
	 * @param analyzer
	 * @param string
	 * @return
	 * @throws IOException
	 */
	public static TokenStream createTokenStreamQuietly(Analyzer analyzer, String string) {
		TokenStream tokenStream = null;
//		try {
			tokenStream = analyzer.tokenStream(null, new StringReader(string));
			LuceneUtils.resetTokenStreamQuietly(tokenStream);
//		} catch (IOException e) {
//			throw new RuntimeException("cannot create tokenStream", e);
//		}
		return tokenStream;
	}

	public static TokenStream createWhitespaceTokenStreamQuietly(String string) {
		Analyzer analyzer = new WhitespaceAnalyzer();
		TokenStream tokenStream = null;
		tokenStream = analyzer.tokenStream(null, new StringReader(string));
		LuceneUtils.resetTokenStreamQuietly(tokenStream);
		analyzer.close();
		return tokenStream;
	}

	/** convenience method to create list of unigram tokens 
	 * 
	 * uses Lucene WhitespaceAnalyzer
	 * 
	 * @param analyzer
	 * @param string
	 * @return
	 */
	public static List<String> whitespaceTokenize(String string) {
		WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
	    return tokenize(string, analyzer);
	}

	public static List<String> tokenize(String string, Analyzer analyzer) {
		List<String> result = new ArrayList<String>();
		try {
	      TokenStream stream = LuceneUtils.createTokenStreamQuietly(analyzer, string);
	      while (stream.incrementToken()) {
	        result.add(stream.getAttribute(CharTermAttribute.class).toString());
	      }
	    } catch (IOException e) {
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
		return result;
	}

	public static List<String> createShingleStream(String input, int min, int max, Analyzer analyzer) throws IOException {
		TokenStream tokenStream = LuceneUtils.createTokenStreamQuietly(analyzer, input);
		ShingleFilter shingleFilter = new ShingleFilter(tokenStream, min, max);
		CharTermAttribute charTermAttribute = shingleFilter.addAttribute(CharTermAttribute.class);
		shingleFilter.setOutputUnigrams(false); // no single words
		List<String> shingles = new ArrayList<String>();
		while (shingleFilter.incrementToken()) {
			shingles.add(charTermAttribute.toString());
		}
		shingleFilter.end();
		shingleFilter.close();
		tokenStream.close();
		return shingles;
	}

	/** tokenize and create Shingles
		 * uses Whitespace analyze
		 * @param input to tokenize
		 * @param min
		 * @param max
		 * @return
		 * @throws IOException
		 */
		public static List<String> createWhitespaceShingleStream(String input, int min, int max) throws IOException {
			WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
			return 	createShingleStream(input, min, max, analyzer);
	}

	/** have to reset() tokenStream after creating it - no idea why.
	 * this traps exception
	 * @param tokenStream
	 */
	public static void resetTokenStreamQuietly(TokenStream tokenStream) {
		try {
			tokenStream.reset();
		} catch (IOException e) {
			try {
				tokenStream.close();
			} catch (IOException e1) {
			}
			throw new RuntimeException("Cannot reset stream", e);
		}
	}

	/** extracts a list of Strings from  a tokenStream ??
	 * 
	 * @param tokenStream
	 * @return
	 */
	public static TermPhrase createPhraseFromTokenStream(TokenStream tokenStream) {
		return TermPhrase.createTermPhrase(createListFromTokenStream(tokenStream));
	}

	/** extracts a list of Strings from  a tokenStream
	 * 
	 * This does not close the stream ?? FAILS
	 * 
	 * @param tokenStream
	 * @return
	 * @deprecated // does not close stream
	 */
	public static List<String> createListFromTokenStream(TokenStream tokenStream) {
        CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class);
		List<String> transformedWords = new ArrayList<String>();
		try {
			while (tokenStream.incrementToken()) {
			    transformedWords.add(charTermAttr.toString());
			}
	        tokenStream.close();
		} catch (IOException e) {
			throw new RuntimeException("token stream failed", e);
		}
		return transformedWords;
	}
	/** extract Strings from TokenStream 
	 * NOTE: This can only be called once on a given stream. In principle
	 * it can be `reset()` but I haven't managed to do that.
	 * 
	 * @tokenStream to extract tokens from
	 * @return list of extracted tokens as Strings
	 * @exception if called more than once.
	 * */
	public static List<String> extractStringList(TokenStream tokenStream) {
		List<String> stringList = new ArrayList<>();
	    try {
	        CharTermAttribute charTermAtt = tokenStream.addAttribute(CharTermAttribute.class);
	        tokenStream.reset();
	        while (tokenStream.incrementToken()) {
	            stringList.add(charTermAtt.toString());
	        }
	        tokenStream.end();
	        tokenStream.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return stringList;
	}

//	/** concatenates words, uses StandardTokenizer, creates a TokenStream, and returns a list of stemmed words.
//	 * 
//	 * @param currentWords
//	 * @return
//	 */
//	public static TermPhrase applyWhitespaceTokenizedLucenePorterStemming(String words) {
//		TermPhrase stemmedTerm = null;
//		if (words != null) {
//			List<String> splitWords = Arrays.asList(words.split("\\s+"));
//			stemmedTerm = LuceneUtils.applyWhitespaceTokenizedLucenePorterStemming(splitWords);
//		}
//	    return stemmedTerm;
//	}

	/** iterates over individual strings and returns a list of stemmed words.
	 * 
	 * @param words
	 * @return
	 */
	public static List<String> applyPorterStemming(List<String> words) {
		PorterStemmer porterStemmer = new PorterStemmer();
		List<String> tokenList = new ArrayList<String>();
		for (String word : words) {
			tokenList.add(porterStemmer.stem(word));
		}
	    return tokenList;
	}
	
	/** splits a (short) phrase and creates a stemmed Phrase.
	 * 
	 * @param phrase
	 * @return
	 */

	public static TermPhrase applyPorterStemming(String phrase) {
		List<String> tokenList = Arrays.asList(phrase.split("\\s+"));
		tokenList = LuceneUtils.applyPorterStemming(tokenList);
		return TermPhrase.createTermPhrase(tokenList);
	}

	public static List<String> createWhitespaceList(String string) {
		TokenStream tokenStream = LuceneUtils.createTokenStreamQuietly(new WhitespaceAnalyzer(), string);
		List<String> tokenList = LuceneUtils.createListFromTokenStream(tokenStream);
		return tokenList;
	}

	/** uses suffix to determine type of file
	 * 
	 * 		List<String> nonTexts = Arrays.asList(new String[] {
				"png", "jpg", "pdf", "bin", "doc", "docx","zip", "gz", "ppt", "pptx"});
		List<String> texts = Arrays.asList(new String[] {
				"txt", "xml", "svg", "json", "csv"});

	 * @param path
	 * @return
	 */
	public static boolean isTextFile(Path path) {
//		System.out.println(path+" | "+ Util.isBinaryPath(path)); // don't think this works
		List<String> nonTexts = Arrays.asList(new String[] {
				"bin", "doc", "docx", "gz", "html", "jpg", "pdf", "png", "ppt", "pptx", "zip"});
		List<String> texts = Arrays.asList(new String[] {
				"csv", "json", "log", "svg", "txt", "xml"});
		String extension = FilenameUtils.getExtension(path.toString());
		if (nonTexts.contains(extension)) {
			return false;
		}
		if (texts.contains(extension)) {
			return true;
		}
		if (true)throw new RuntimeException("Unknown suffix: "+extension);
		// defaults to false
		return false;
	}
	
	

	/** concatenates words, uses StandardTokenizer, creates a TokenStream, and returns a list of stemmed words.
	 * 
	 * @param words
	 * @return
	 */
	public static List<String> applyStandardTokenizedLucenePorterStemming(List<String> words) {
		String input = StringUtils.join(words.iterator(), " ");
		List<String> tokenList = applyStandardTokenizedLucenePorterStemming(input);
	    return tokenList;
	}

	public static List<String> applyStandardTokenizedLucenePorterStemming(String input) {
		StringReader stringReader = new StringReader(input);
	    TokenStream tokenStream = new StandardTokenizer(); 
	    ((Tokenizer)tokenStream).setReader(stringReader);
		LuceneUtils.resetTokenStreamQuietly(tokenStream);
	    tokenStream = new PorterStemFilter(tokenStream);
	
	    List<String> tokenList = LuceneUtils.createListFromTokenStream(tokenStream);
		return tokenList;
	}

}
