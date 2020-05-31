package org.contentmine.ami.tools.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

public class SearchStuff {

/**
 * 
 * @author W.P.Roshan
 * @email  sunone5 at gmail.com
 * 
 * The RAMDirector is deprecated instead you can use 
 * 
 * import org.apache.lucene.index.memory.MemoryIndex;
 *
 */

    static void writeIndex(RAMDirectory ramDir, Analyzer analyzer) {
        try {
            // IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE);

            // IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(ramDir, iwc);

            // Create some docs with name and content
            indexDoc(writer, "document-1", "hello world");
            indexDoc(writer, "document-2", "hello happy world");
            indexDoc(writer, "document-3", "hello happy world");
            indexDoc(writer, "document-4", "hello hello world");

            // don't forget to close the writer
            writer.close();
        } catch (IOException e) {
            // Any error goes here
            e.printStackTrace();
        }
    }

    static void indexDoc(IndexWriter writer, String name, String content) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("name", name, Store.YES));
        doc.add(new TextField("content", content, Store.YES));
        writer.addDocument(doc);
    }

    static void searchIndex(RAMDirectory ramDir, Analyzer analyzer) {
        IndexReader reader = null;
        try {
            // Create Reader
            reader = DirectoryReader.open(ramDir);

            // Create index searcher
            IndexSearcher searcher = new IndexSearcher(reader);

            // Build query
            QueryParser qp = new QueryParser("content", analyzer);
            Query query = qp.parse("happy");

            // Search the index
            TopDocs foundDocs = searcher.search(query, 10);

            // Total found documents
            System.out.println("Total Results :: " + foundDocs.totalHits);

            // Let's print found doc names and their content along with score
            for (ScoreDoc sd : foundDocs.scoreDocs) {
                Document d = searcher.doc(sd.doc);
                System.out.println("Document Number : " + sd.doc + " :: Document Name : " + d.get("name")
                        + "  :: Content : " + d.get("content") + "  :: Score : " + sd.score);
            }
            System.out.println("");

            // don't forget to close the reader
            reader.close();
        } catch (IOException e) {
            // Any error goes here
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static void readIndex_Get_Documents(RAMDirectory ramDir) {
        IndexReader reader = null;
        try {
            // Create Reader
            reader = DirectoryReader.open(ramDir);

            // Create index searcher
            IndexSearcher searcher = new IndexSearcher(reader);

            System.out.println("-----------------------Document List-----------------------");
            int maxDoc = reader.maxDoc();
            for (int i = 0; i < maxDoc; i++) {
                Document d = reader.document(i);

                /**
                 * There are three types of method to retrieve indexed document name list
                 */

                /**
                 * Method 1 for get document name list
                 */
                // System.out.println(""+d.getFields().iterator().next().stringValue());

                /**
                 * Method 2 for get document name list
                 */
                // System.out.println(""+d.iterator().next().stringValue());

                /**
                 * Method 3 for get document name list
                 */
                String[] vls = d.getValues("name");
                for (int j = 0; j < vls.length; j++) {
                    System.out.println("" + vls[j].toString());
                }
            }

            // don't forget to close the reader
            reader.close();
        } catch (IOException e) {
            // Any error goes here
            e.printStackTrace();
        }
    }

    static void readIndex_Get_Terms(RAMDirectory ramDir) {
        IndexReader reader = null;
        try {
            // Create Reader
            reader = DirectoryReader.open(ramDir);

            // Create index searcher
            IndexSearcher searcher = new IndexSearcher(reader);

            System.out.println("");
            System.out.println("--------------------------Term List------------------------");
            int maxDoc = reader.maxDoc();
            for (int i = 0; i < maxDoc; i++) {
                Document d = reader.document(i);

                /**
                 * There are three types of methods to retrieve indexed term list
                 */

                /**
                 * Method 1 for retrieve terms list
                 */
                // System.out.println(""+d.get("content").toString());

                /**
                 * Method 2 for retrieve terms list
                 */
                // System.out.println(""+d.getField("content").stringValue());

                /**
                 * Method 3 for retrieve terms list
                 */
                String[] vl = searcher.doc(i).getValues("content");
                for (int k = 0; k < vl.length; k++) {
                    System.out.println("" + vl[k].toString());
                }
            }
            // don't forget to close the reader
            reader.close();
        } catch (IOException e) {
            // Any error goes here
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create RAMDirectory instance
        RAMDirectory ramDir = new RAMDirectory();

        // Builds an analyzer with the default stop words
        Analyzer analyzer = new StandardAnalyzer();

        // Write some docs to RAMDirectory
        writeIndex(ramDir, analyzer);

        // Search indexed docs in RAMDirectory
        searchIndex(ramDir, analyzer);

        // read Index get indexed document list
        readIndex_Get_Documents(ramDir);

        // read Index get indexed terms list
        readIndex_Get_Terms(ramDir);
    }

}

