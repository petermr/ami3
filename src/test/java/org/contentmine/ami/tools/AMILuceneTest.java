package org.contentmine.ami.tools;

import java.io.File;


import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.DecimalDigitFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.UpperCaseFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountFilter;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.TruncateTokenFilter;
import org.apache.lucene.analysis.reverse.ReverseStringFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tr.ApostropheFilter;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.demo.SearchFiles;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.contentmine.ami.tools.lucene.LuceneUtils;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.jupiter.api.Test;



/** test Lucene.
 * 
 * https://lucene.apache.org/core/8_5_1/core/org/apache/lucene/analysis/package-summary.html#package.description
 * 
 * 
 * @author pm286
 *
 */
public class AMILuceneTest extends AbstractAMITest {
	
	private static final Logger LOG = LogManager.getLogger(AMILuceneTest.class);
	private static final File TARGET_DIR = new AMILuceneTest().createAbsoluteTargetDir();

	public final static String CRICK_WATSON = ""
			+ "It has not escaped our notice that the specific pairing we have postulated"
			+ " immediately suggests a possible copying mechanism for the genetic material.";

	/** apologies for converting apostrophe's to modern usage*/
	public final static String TOBE = ""
			+ "“To be, or not to be: that is the question:\n" + 
			"Whether 'tis nobler in the mind to suffer\n" + 
			"The slings and arrows of outrageous fortune,\n" + 
			"Or to take arms against a sea of troubles,\n" + 
			"And by opposing end them? To die: to sleep;\n" + 
			"No more; and by a sleep to say we end\n" + 
			"The heart-ache and the thousand natural shocks\n" + 
			"That flesh is heir to, 'tis a consummation\n" + 
			"Devoutly to be wish'd. To die, to sleep;";
	String excludeTypes = "png";

	private static final File ZIKA2INDEX = new File("target/lucene/zika2");
	private static final File ZIKA2INPUT = new File(NAConstants.TEST_AMI_DIR, "zika2");
	private static final File ZIKA10INDEX = new File("target/lucene/zika10");
	private static final File ZIKA10INPUT = new File(NAConstants.TEST_AMI_DIR, "zika10");
	File inputDir;

	@Test
	public void testHelp() {
		new AMILuceneTool().runCommands(new String[]{});
	}

	@Test
	public void testMakeIndex() {
		File inputDir = ZIKA10INPUT;
		File indexDir = ZIKA10INDEX;
		System.out.println(inputDir.listFiles().length);
		String cmd = " -vv"
				+ " lucene "
				+ " --inputdir " + inputDir
				+ " --skiptypes " + excludeTypes
				+ " --index " + indexDir  
				;
		AMI.execute(cmd);
//		AMILuceneTool amiLuceneTool = AMI.execute(AMILuceneTool.class, cmd);
		Assert.assertTrue(new File(indexDir, "write.lock").exists());
		
	}

	@Test
	public void testSearchIndex() {
		String query = "id id1000";
		File indexDir = ZIKA10INDEX;
		File[] files = ZIKA2INDEX.listFiles();
		for (File file : files) {
			System.out.println(">"+file);
			file.delete();
		}
		System.out.println(indexDir);
		String cmd = " -vv"
				+ " lucene "
				+ " --index " + indexDir  
				+ " --query "+query
				;
		AMI.execute(AMILuceneTool.class, cmd);
		files = ZIKA2INDEX.listFiles();
		System.out.println(">>"+files.length);
		for (File file : files) {
			System.out.println(">"+file);
		}
	}

	@Test
	public void testMakeAndSearchIndex() {
		File inputDir = ZIKA2INPUT;
		File indexDir = ZIKA2INDEX;
		String cmd = " -vv"
				+ " -p " + ZIKA2INPUT
				+ " lucene "
				+ " --operations index"
				+ " --skiptypes " + excludeTypes
//				+ " --inputdir " + inputDir  
				+ " --index " + indexDir.getAbsolutePath()  
				;
		AMI.execute(AMILuceneTool.class, cmd);
		
		String query1 = "id id1";
		String query2 = "id id2";
		cmd = " -vv"
				+ " lucene "
				+ " --operations query"
				+ " --index " + indexDir.getAbsolutePath()  
				+ " --query " + query1
				+ " --query " + query2
				;
		AMI.execute(AMILuceneTool.class, cmd);
		
		cmd = " -vv"
				+ " lucene "
				+ " --operations fields"
				+ " --index " + indexDir.getAbsolutePath()  
				;
		AMI.execute(AMILuceneTool.class, cmd);
	}

	/**

//	https://www.codeproject.com/Articles/5246976/Lucene-Full-Text-Search-A-Very-Basic-Tutorial
	public void testLuceneTutorial() {
	}
		public Document createIndexDocument(IndexableDocument docToAdd)
		{
		   Document retVal = new Document();

		   IndexableField docIdField = new StringField("DOCID",
		      docToAdd.getDocumentId(),
		      Field.Store.YES);
		   IndexableField titleField = new TextField("TITLE",
		      docToAdd.getTitle(),
		      Field.Store.YES);
		   IndexableField contentField = new TextField("CONTENT",
		      docToAdd.getContent(),
		      Field.Store.NO);
		   IndexableField keywordsField = new TextField("KEYWORDS",
		      docToAdd.getKeywords(),
		      Field.Store.YES);
		   IndexableField categoryField = new StringField("CATEGORY",
		      docToAdd.getCategory(),
		      Field.Store.YES);
		   IndexableField authorNameField = new TextField("AUTHOR",
		      docToAdd.getAuthorName(),
		      Field.Store.YES);
		   long createTime = docToAdd.getDocumentDate().getTime();
		   IndexableField documentTimeField = new StoredField("DOCTIME", createTime);
		   IndexableField emailField = new StringField("AUTHOREMAIL",
		      docToAdd.getAuthorEmail(),
		      Field.Store.YES);

		   retVal.add(docIdField);
		   retVal.add(titleField);
		   retVal.add(contentField);
		   retVal.add(keywordsField);
		   retVal.add(categoryField);
		   retVal.add(authorNameField);
		   retVal.add(documentTimeField);
		   retVal.add(emailField);

		   return retVal;
		}

		String indexDirectory;
public void indexDocument(Document docToAdd) throws Exception
{
   IndexWriter writer = null;
   try
   {
      Directory indexWriteToDir =
            FSDirectory.open(Paths.get(indexDirectory));

      writer = new IndexWriter(indexWriteToDir, new IndexWriterConfig());
      writer.addDocument(docToAdd);
      writer.flush();
      writer.commit();
   }
   finally
   {
      if (writer != null)
      {
         writer.close();
      }
   }
}
public List<FoundDocument> searchForDocument(String searchVal)
{
   List<FoundDocument> retVal = new ArrayList<FoundDocument>();

   try
   {
      Directory dirOfIndexes =
            FSDirectory.open(Paths.get(indexDirectory));

      IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dirOfIndexes));

      QueryBuilder bldr = new QueryBuilder(new StandardAnalyzer());
      Query q1 = bldr.createPhraseQuery("TITLE", searchVal);
      Query q2 = bldr.createPhraseQuery("KEYWORDS", searchVal);
      Query q3 = bldr.createPhraseQuery("CONTENT", searchVal);
      Query q4 = bldr.createBooleanQuery("CATEGORY", searchVal);
      Query q5 = bldr.createPhraseQuery("AUTHOR", searchVal);
      Query q6 = bldr.createBooleanQuery("AUTHOREMAIL", searchVal);

      BooleanQuery.Builder chainQryBldr = new BooleanQuery.Builder();
      chainQryBldr.add(q1, Occur.SHOULD);
      chainQryBldr.add(q2, Occur.SHOULD);
      chainQryBldr.add(q3, Occur.SHOULD);
      chainQryBldr.add(q4, Occur.SHOULD);
      chainQryBldr.add(q5, Occur.SHOULD);
      chainQryBldr.add(q6, Occur.SHOULD);

      BooleanQuery finalQry = chainQryBldr.build();

      TopDocs allFound = searcher.search(finalQry, 100);
      if (allFound.scoreDocs != null)
      {
         for (ScoreDoc doc : allFound.scoreDocs)
         {
            System.out.println("Score: " + doc.score);

            int docidx = doc.doc;
            Document docRetrieved = searcher.doc(docidx);
            if (docRetrieved != null)
            {
               FoundDocument docToAdd = new FoundDocument();

               IndexableField field = docRetrieved.getField("TITLE");
               if (field != null)
               {
                  docToAdd.setTitle(field.stringValue());
               }

               field = docRetrieved.getField("DOCID");
               if (field != null)
               {
                  docToAdd.setDocumentId(field.stringValue());
               }

               field = docRetrieved.getField("KEYWORDS");
               if (field != null)
               {
                  docToAdd.setKeywords(field.stringValue());
               }

               field = docRetrieved.getField("CATEGORY");
               if (field != null)
               {
                  docToAdd.setCategory(field.stringValue());
               }

               if (docToAdd.validate())
               {
                  retVal.add(docToAdd);
               }
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


public Document getDocumentById(String docId)
{
   Document retVal = null;
   try
   {
      Directory dirOfIndexes =
            FSDirectory.open(Paths.get(indexDirectory));

      StandardAnalyzer analyzer = new StandardAnalyzer();
      IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dirOfIndexes));
      QueryBuilder quryBldr = new QueryBuilder(analyzer);

      Query idQury = quryBldr.createPhraseQuery("DOCID", docId);
      TopDocs foundDocs = searcher.search(idQury, 1);
      if (foundDocs != null)
      {
         if (foundDocs.scoreDocs != null && foundDocs.scoreDocs.length > 0)
         {
            System.out.println("Score: " + foundDocs.scoreDocs[0].score);
            retVal = searcher.doc(foundDocs.scoreDocs[0].doc);
         }
      }
   }
   catch (Exception ex)
   {
      ex.printStackTrace();
   }

   return retVal;
   
}
public void deleteAllIndexes() throws Exception
{
   IndexWriter writer = null;
   try
   {
      Directory indexWriteToDir =
            FSDirectory.open(Paths.get(indexDirectory));

      writer = new IndexWriter(indexWriteToDir, new IndexWriterConfig());
      writer.deleteAll();
      writer.flush();
      writer.commit();
   }
   finally
   {
      if (writer != null)
      {
         writer.close();
      }
   }
}
public static IndexableDocument prepareDocForTesting(String docId)
{
   IndexableDocument doc = new IndexableDocument();

   Calendar cal = Calendar.getInstance();
   cal.set(2018, 8, 21, 13, 13, 13);

   doc.setDocumentId(docId);
   doc.setAuthorEmail("testuser@lucenetest.com");
   doc.setAuthorName("Lucene Test User");
   doc.setCategory("Index File Sample");
   doc.setContent("There are two main types of medical gloves: "
      + "examination and surgical. Surgical gloves have more "
      + "precise sizing with a better precision and sensitivity "
      + "and are made to a higher standard. Examination gloves "
      + "are available as either sterile or non-sterile, while "
      + "surgical gloves are generally sterile.");
   doc.setDocumentDate(cal.getTime());
   doc.setKeywords("Joseph, Brian, Clancy, Connery, Reynolds, Lindsay");
   doc.setTitle("Quick brown fox and the lazy dog");

   return doc;
}
public static void testFindDocument(String searchTerm)
{
   LuceneDocumentLocator locator = new LuceneDocumentLocator("c:/DevJunk/Lucene/indexes");
   List<FoundDocument> foundDocs = locator.searchForDocument(searchTerm);
   
   if (foundDocs != null)
   {
      for (FoundDocument doc : foundDocs)
      {
         System.out.println("------------------------------");
         System.out.println("Found document...");
         System.out.println("Document Id: " + doc.getDocumentId());
         System.out.println("Title: " + doc.getTitle());
         System.out.println("Category: " + doc.getCategory());
         System.out.println("Keywords: " + doc.getKeywords());
         System.out.println("------------------------------");
      }
   }
}
*/
	@Test
	public void testLuceneIndexDemo() {
		String docs = "src/test/resources/org/contentmine/ami/battery10";
		String index = "target/lucene/battery10";
		String cmd = "-index " + index +" -docs "+docs;
		/*org.contentmine.ami.tools.lucene.demo.*/IndexFiles.main(cmd.split("\\s+"));
	}


	/** Index all text files under a directory.
	 * Uses a CProject with 10 CTrees
	 * 
	 * creates an index in target
	 */
	@Test
	public void testLuceneIndex() {
		String inputDir = "src/test/resources/org/contentmine/ami/battery10";
		String indexPath = "target/lucene/battery10";
	    boolean create = true;
	    LuceneUtils.createDefaultLuceneIndex(inputDir, indexPath, create);
	}



	/** from the distrib - interactive 
	 * creates a simple index
	 * */
	@Test
	public void testLuceneSearchDemo() throws Exception {
		// this is legacy CLI from Lucene
	    //" [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]
		// \n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
		String index = "target/lucene/distrib";
		String cmd = "-index " + index ;
		SearchFiles.main(cmd.split("\\s+"));
	}

	@Test
	public void testLuceneSearch() throws Exception {
		String index = "target/lucene/battery10";
		
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
//	    reader.getDocCount(field);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    String field = "contents";
	    String line = "lithium";
	    int hitsPerPage = 20;
	    
	    Query query = LuceneUtils.createQuery(field, line);
	      
	    // Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		
		int numTotalHits = Math.toIntExact(results.totalHits.value);
		System.out.println(numTotalHits + " total matching documents");		
		
		hits = searcher.search(query, numTotalHits).scoreDocs;
		
		Document doc1 = searcher.doc(hits[0].doc);
		System.out.println("fields "+doc1.getFields());
		for (int i = 0; i < hits.length; i++) {
	
			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("path");
			String title = doc.get("title");
			String modified = doc.get("modified");
			String contents = doc.get("contents");
			System.out.println(path+" | " + title + " | " + contents + " | " + modified);
		}
	
	}

	@Test
	public void testPorterStemming() {
		List<String> tokens = LuceneUtils.applyStandardTokenizedLucenePorterStemming(CRICK_WATSON);
		Assert.assertEquals("stemmed", "["
				+ "It, ha, not, escap, our, notic, that, the, specif, pair, we, have, postul, "
				+ "immedi, suggest, a, possibl, copi, mechan, for, the, genet, materi]" + 
				"", tokens.toString());
		System.out.println(tokens);

	}
	
	@Test
	public void testChainedFilters() {
		// not used
		PorterStemFilter ff;
		ApostropheFilter a;
		DecimalDigitFilter d;
		ElisionFilter e;
		LimitTokenCountFilter l;
		RemoveDuplicatesTokenFilter rr;
		ShingleFilter sf;

		StringReader reader = new StringReader(TOBE);
		Analyzer analyzer = new StandardAnalyzer();
		TokenStream tokenStream = analyzer.tokenStream("lowercase", reader);
		
		analyzer.close();
		tokenStream = new LowerCaseFilter(tokenStream);
		// remove stopwords
		tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
		// convert to upper
		tokenStream = new UpperCaseFilter(tokenStream);
		// reverse
		tokenStream = new ReverseStringFilter(tokenStream);
		// reconvert to lower
		tokenStream = new LowerCaseFilter(tokenStream);
		// re-reverse, so back where started
		tokenStream = new ReverseStringFilter(tokenStream);
		// capitalize first character
		tokenStream = new CapitalizationFilter(tokenStream);
		// stem
		tokenStream = new SnowballFilter(tokenStream, "English");
		// trim tokens
		tokenStream = new TrimFilter(tokenStream);
		// truncate them
		tokenStream = new TruncateTokenFilter(tokenStream, 6);

		// can only call this once unless tokenStream is reset in
		// some way I and others can't work
		List<String> tokenList = LuceneUtils.extractStringList(tokenStream);
		analyzer.close();
		Assert.assertEquals("tokens", 
				"[Questi, Whethe, Tis, Nobler, Mind, Suffer, Sling, Arrow, Outrag, Fortun, Take, Arms, Agains, Sea, Troubl, Oppose, End, Them, Die, Sleep, More, Sleep, Say, We, End, Heart, Ache, Thousa, Natur, Shock, Flesh, Heir, Tis, Consum, Devout, Wish'd, Die, Sleep]" + 
				"", tokenList.toString());

	}
	
//	https://howtodoinjava.com/lucene/lucene-search-highlight-example/
		
//	public static List<String> createStringList(TokenStream tokens) {
//	        List<String> results = new ArrayList<>();
////	        try (TokenStream tokens = analyzer.tokenStream("", value)) {
//	            CharTermAttribute term = tokens.getAttribute(CharTermAttribute.class);
//	            tokens.reset();
//	            while (tokens.incrementToken()) {
////	            	tokens.
//	                String t = term.toString().trim();
//	                if (t.length() > 0) {
//	                    results.add(t);
//	                }
//	            }
////	        }
//	        return results.toArray(new String[results.size()]);
	
}
