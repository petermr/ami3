package org.contentmine.ami.tools;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.demo.SearchFiles;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.contentmine.ami.tools.lucene.LuceneTools;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Test;



/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMILuceneTest {
	

	private static final Logger LOG = Logger.getLogger(AMILuceneTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
		AMILuceneTool amiLuceneTool = AMI.execute(AMILuceneTool.class, cmd);
		Assert.assertTrue(new File(indexDir, "write.lock").exists());
		
	}

	@Test
	public void testSearchIndex() {
		String query = "id id1000";
		File indexDir = ZIKA10INDEX;
		System.out.println(indexDir);
		String cmd = " -vv"
				+ " lucene "
				+ " --index " + indexDir  
				+ " --query "+query
				;
		AMI.execute(AMILuceneTool.class, cmd);
	}

	@Test
	public void testMakeAndSearchIndex() {
		File inputDir = ZIKA2INPUT;
		File indexDir = ZIKA2INDEX;
		String cmd = " -vv"
				+ " lucene "
				+ " --skiptypes " + excludeTypes
				+ " --inputdir " + inputDir  
				+ " --index " + indexDir  
				;
		AMI.execute(AMILuceneTool.class, cmd);
		String query1 = "id id1";
		String query2 = "id id2";
		cmd = " -vv"
				+ " lucene "
				+ " --index " + indexDir  
				+ " --query " + query1
				+ " --query " + query2
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
		String index = "target/lucene/distrib";
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
		String indexPath = "target/lucene/distrib";
	    boolean create = true;
	    LuceneTools.createDefaultLuceneIndex(inputDir, indexPath, create);
	}



	/** from the distrib - interactive 
	 * creates a simple index
	 * */
	@Test
	public void testLuceneSearchDemo() throws Exception {
		String index = "target/lucene/distrib";
		String cmd = "-index " + index ;
	    //" [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";

		/*org.contentmine.ami.tools.lucene.demo.*/SearchFiles.main(cmd.split("\\s+"));
	}

	@Test
	public void testLuceneSearch() throws Exception {
		String index = "target/lucene/distrib";
		
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new StandardAnalyzer();
	
	    String field = "contents";
	    String line = "lithium";
	    int hitsPerPage = 20;
	    QueryParser parser = new QueryParser(field, analyzer);
	    Query query = parser.parse(line);
	      
	    // Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		
		int numTotalHits = Math.toIntExact(results.totalHits.value);
		System.out.println(numTotalHits + " total matching documents");		
		
		hits = searcher.search(query, numTotalHits).scoreDocs;
		
		for (int i = 0; i < hits.length; i++) {
	
			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("path");
			String title = doc.get("title");
			System.out.println(path+" | " + title);
		}
	
	}
	
}
