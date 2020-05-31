package org.contentmine.ami.tools;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;
import org.contentmine.ami.tools.lucene.LuceneTools;
import org.contentmine.cproject.files.CProject;

import nu.xom.Attribute;
import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


/** indexing and searching using the OS tool Lucene
 * 
 * @author pm286
 *
 */

/**
 * 
 * @author pm286
 *
 *https://cwiki.apache.org/confluence/display/LUCENE/LuceneFAQ
 */

@Command(
name = "lucene",
description = {
		 "Runs Lucene (words and search) Experimental"
})
public class AMILuceneTool extends AbstractAMITool {

	private static final Version LUCENE851 = Version.LUCENE_8_5_1;

	private static final Logger LOG = Logger.getLogger(AMILuceneTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private enum LuceneOperation {
		fields("extract fields"),
		index("make index"),
		query("run query"),
		update("update index"),
		;
		private String title;

		private LuceneOperation(String title) {
			this.title = title;
		}
	}

	private static final List<String> DEFAULT_SKIP_EXTENSIONS = Arrays.asList(new String[] {"png", "pdf", "svg"});

    @Option(names = {"--fields"},
            description = "extract fields")
	private boolean fields = false;

    @Option(names = {"-x", "--index"},
    		arity = "1",
    		defaultValue = "__index/lucene.idx",
            description = "index directory, relative to cProject; default ($DEFAULT_VALUE) would use"
            		+ " <cproject>/__index/lucene.idx."
            		)
    			
	private String index = null;

    @Option(names = {"--operations"},
    		arity = "1..*",
            description = "operations ")
	private List<LuceneOperation> operations = null;;

    @Option(names = {"--query"},
    		arity = "2",
            description = "query to apply to index: field , value (e.g. 'id', 'id1'; note replace by Map")
	private List<String> query;

    @Option(names = { "--skiptypes"},
    		arity = "1..*",
            description = "Skip files with extensions (no '.')")
	private List<String> skipExtensions = DEFAULT_SKIP_EXTENSIONS;

    @Option(names = {"--update"},
    		arity = "1",
            description = "update - not yet understood or supported")
	private boolean update = false;

    int count = 0;
    private Path luceneIndexPath = null;
    private Path inputPath;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMILuceneTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMILuceneTool() {
	}
	
    @Override
	protected void parseSpecifics() {
    	super.parseSpecifics();
    	if (index != null) {
    		luceneIndexPath = new File(getCProjectDirectory(), index).toPath();
    	}
	}

    public String getSpecifics() {
    	return getOptionsValue();
    }

    @Override
    protected void runSpecifics() {
    	if (cProject == null) {
    		LOG.error("no CProject given");
    		return;
    	}
    	inputPath = Paths.get(cProject.getDirectory().getAbsolutePath());
    	if (luceneIndexPath == null ) {
    		LOG.error("no index given");
    		return;
    	}
		if (operations.contains(LuceneOperation.index)) {
			makeIndex();
	    } else if (operations.contains(LuceneOperation.update)) {
	    	updateIndex();
	    }
        if (operations.contains(LuceneOperation.fields)) {
        	getFields();
        }
        if (operations.contains(LuceneOperation.query)) {
        	runQuery();
        }
    }

	private void updateIndex() {
		LOG.warn("update NYI");
	}

	private void runQuery() {
        IndexSearcher searcher = getIndexSearcher();
		try {
			List<Element> results = LuceneTools.searchForDocument(searcher, query);
			for (Element result : results) {
				System.out.println("result: "+result.toXML());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean processTree() {
		System.out.println("Not yet implemented; may not be rekevant");
		return true;
	}

	private void makeIndex() {
		if (!Files.isReadable(inputPath)) {
          System.out.println("Document directory '" +inputPath+ "' does"
          		+ " not exist or is not readable, please check the path");
          System.exit(1);
        }
        IndexWriter writer = null;
        try {
          System.out.println("Indexing to directory '" + luceneIndexPath + "'...");
    
          Directory indexDir = FSDirectory.open(luceneIndexPath);
          Analyzer analyzer = new StandardAnalyzer();
          IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
          iwc.setIndexCreatedVersionMajor(LUCENE851.major); // may be useful
    
          iwc.setOpenMode(update ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
    
          writer = new IndexWriter(indexDir, iwc);
          LuceneTools.indexDocs(writer, inputPath);
          writer.close();
    
        } catch (IOException e) {
          System.out.println(" caught a " + e.getClass() +
           "\n with message: " + e.getMessage());
        }
	}
    
//      /**
//       * Indexes the given file using the given writer, or if a directory is given,
//       * recurses over files and directories found under the given directory.
//       * 
//       * NOTE: This method indexes one document per input file.  This is slow.  For good
//       * throughput, put multiple documents into your input file(s).  An example of this is
//       * in the benchmark module, which can create "line doc" files, one document per line,
//       * using the
//       * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
//       * >WriteLineDocTask</a>.
//       *  
//       * @param writer Writer to the index where the given file/dir info will be stored
//       * @param path The file to index, or the directory to recurse into to find files to index
//       * @throws IOException If there is a low-level I/O error
//       */
//      void indexDocs(final IndexWriter writer, Path path) throws IOException {
//        if (Files.isDirectory(path)) {
//          Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
//            @Override
//            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
////            	System.out.println("FP "+filePath);
//            	if (skipFileTypes(filePath.toFile())) {
//            		System.out.println("skipped "+path);
//            		return FileVisitResult.TERMINATE;
//            	}
//              try {
//                indexDoc(writer, filePath, attrs.lastModifiedTime().toMillis());
//                count++;
//              } catch (IOException ignore) {
//                // don't index files that can't be read.
//              }
//              return FileVisitResult.CONTINUE;
//            }
//
//          });
//        } else {
//          indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
//        }
//      }
//
//  	private boolean skipFileTypes(File file) {
//  		String ext = FilenameUtils.getExtension(file.toString());
//  		return skipExtensions.contains(ext);
//	}
//
//      /** Indexes a single document */
//      void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
//        try (InputStream stream = Files.newInputStream(file)) {
//          // make a new, empty document
//          Document doc = new Document();
//          
//          // Add the path of the file as a field named "path".  Use a
//          // field that is indexed (i.e. searchable), but don't tokenize 
//          // the field into separate words and don't index term frequency
//          // or positional information:
//          Field pathField = new StringField("path", file.toString(), Field.Store.YES);
//          doc.add(pathField);
//          
//          // Add the last modified date of the file a field named "modified".
//          // Use a LongPoint that is indexed (i.e. efficiently filterable with
//          // PointRangeQuery).  This indexes to milli-second resolution, which
//          // is often too fine.  You could instead create a number based on
//          // year/month/day/hour/minutes/seconds, down the resolution you require.
//          // For example the long value 2011021714 would mean
//          // February 17, 2011, 2-3 PM.
//          doc.add(new LongPoint("modified", lastModified));
//          
//          // Add the contents of the file to a field named "text".  Specify a Reader,
//          // so that the text of the file is tokenized and indexed, but not stored.
//          // Note that FileReader expects the file to be in UTF-8 encoding.
//          // If that's not the case searching for special characters will fail.
//          doc.add(new TextField("text", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
//          String id = "id"+count;
//		  doc.add(new StringField("id", id, Field.Store.YES));
//          //System.out.println("id: "+id);
//          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
//            // New index, so we just add the document (no old document can be there):
//            System.out.println("adding " + file);
//            writer.addDocument(doc);
//          } else {
//            // Existing index (an old copy of this document may have been indexed) so 
//            // we use updateDocument instead to replace the old one matching the exact 
//            // path, if present:
//            System.out.println("updating " + file);
//            writer.updateDocument(new Term("path", file.toString()), doc);
//          }
//        }
//      }

      private void getFields() {
          IndexReader indexReader = getIndexReader();
    	  
 		  int numDocs = indexReader.numDocs();
    	  System.out.println("n "+numDocs);
    	  List<String> fields = Arrays.asList(new String[] {"id", "path"});
    	  for (String field : fields) {
	    	  try {
				summarizeFields(indexReader, field);
			} catch (IOException e) {
				LOG.error("Cannot summarize: " + e.getMessage());
			}
    	  }
      }

  	private IndexSearcher getIndexSearcher() {
		IndexSearcher searcher = null;
		try {
			searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(luceneIndexPath)));
		} catch (IOException e) {
			LOG.error("cannot get IndexSearcher: " + e.getMessage());
		}
		return searcher;
	}

	private IndexReader getIndexReader() {
		IndexSearcher searcher = getIndexSearcher();
    	IndexReader indexReader = searcher.getIndexReader();
		return indexReader;
	}

	private void summarizeFields(IndexReader indexReader, String field) throws IOException {
		System.out.println("DOCS for " + field + " " + indexReader.getDocCount(field));
		System.out.println("freq "+indexReader.getSumDocFreq(field));
		
	}
}






































