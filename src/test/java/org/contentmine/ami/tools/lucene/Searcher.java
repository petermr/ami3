package org.contentmine.ami.tools.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
//import org.apache.lucene.queryparser.surround.parser.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
  
   IndexSearcher indexSearcher;
   QueryParser queryParser;
   Query query;
   
   public Searcher(String indexDirectoryPath) 
      throws IOException {
      Directory indexDirectory = 
         FSDirectory.open(Paths.get(indexDirectoryPath));
      IndexReader reader = DirectoryReader.open(indexDirectory);
      indexSearcher = new IndexSearcher(reader);
      queryParser = new QueryParser(LuceneConstants.CONTENTS,
         new StandardAnalyzer());
   }
   
   public TopDocs search( String searchQuery) 
      throws IOException, ParseException {
      query = queryParser.parse(searchQuery);
      return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
   }

   public Document getDocument(ScoreDoc scoreDoc) 
      throws CorruptIndexException, IOException {
      return indexSearcher.doc(scoreDoc.doc);  
   }

}