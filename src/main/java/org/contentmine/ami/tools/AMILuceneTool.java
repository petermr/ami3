package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.contentmine.ami.tools.lucene.LuceneToolsOld;
import org.contentmine.ami.tools.lucene.LuceneUtils;
import org.contentmine.cproject.files.CProject;

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
 * * https://lucene.apache.org/core/8_5_1/core/org/apache/lucene/analysis/package-summary.html#package.description

 *https://cwiki.apache.org/confluence/display/LUCENE/LuceneFAQ  // VERY OUT-of-DATE
 */

@Command(
name = "lucene",
description = {
		 "Runs Lucene (words and search) Experimental"
})
public class AMILuceneTool extends AbstractAMITool {

	private static final Version LUCENE851 = Version.LUCENE_8_5_1;

	private static final Logger LOG = LogManager.getLogger(AMILuceneTool.class);
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
            description = "index directory, %n"
            		+ "if absolute name use that%n"
            		+ " if --project then relative to cProject%n "
            		+ "      default ($DEFAULT_VALUE) would use <cproject>/__index/lucene.idx.%n"
            		+ " Use as output for `index`, input for `query` and both for `update` "
            		)
	private String index = null;
    			
    @Option(names = {"--inputdir"},
	arity = "1",
    description = "input directory%n"
    		+ " if --project is null, then use absolute name%n"
    		+ " if --project is present, `input` is relative to <project>.%n"
    		+ " only relevant for `index`"
    		)
	private String input = null;

    @Option(names = {"--operations"},
    		arity = "1..*",
            description = "operations ")
	private List<LuceneOperation> operations = null;

    @Option(names = {"--query"},
    		arity = "2",
            description = "query to apply to index:%n"
            		+ " field , value (e.g. 'id', 'id1'; note replace by Map")
	private List<String> query;

    @Option(names = { "--skiptypes"},
    		arity = "1..*",
            description = "Skip files with extensions (no '.')")
	private List<String> skipExtensions = DEFAULT_SKIP_EXTENSIONS;

    @Option(names = {"--update"},
    		arity = "1",
            description = "update - not yet understood or supported%n")
	private boolean update = false;

    int count = 0;
    private Path luceneIndexPath = null; // will be computed from `index`
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
    	resolveIndex();
	}

	private void resolveIndex() {
		if (index != null) { 
    		Path indexPath = Paths.get(index);
    		luceneIndexPath = (Files.isReadable(indexPath)) ? indexPath :
    			new File(getCProjectDirectory(), index).toPath();
    	}
	}

    public String getSpecifics() {
    	return getOptionsValue();
    }

    @Override
    protected void runSpecifics() {
    	inputPath = cProject == null ? null : Paths.get(cProject.getDirectory().getAbsolutePath());
    	if (operations == null) {
    		LOG.error("No operations given");
    		return;
    	}
    	try {
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
    	} catch (RuntimeException e) {
    		LOG.debug("cannot run: " + e);
//    		throw e;
    	}
    }

	protected boolean processTree() {
		System.out.println("Not yet implemented; may not be rekevant");
		return true;
	}

	private void makeIndex() {
		if (!canRead("input", inputPath)) {
			throw new RuntimeException("Cannot read input: " + inputPath);
		}
        try {
            System.out.println("Indexing to directory '" + luceneIndexPath + "'...");
            createOrAppendIndex(update ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
    
        } catch (IOException e) {
        	throw new RuntimeException("cannot make index from: " + inputPath, e);
        }
	}

	private void updateIndex() {
		if (!canRead("index", luceneIndexPath)) {
			throw new RuntimeException("Cannot read index: " + luceneIndexPath);
		}
        try {
            System.out.println("Indexing to directory '" + luceneIndexPath + "'...");
            createOrAppendIndex(OpenMode.CREATE_OR_APPEND);

        } catch (IOException e) {
            throw new RuntimeException("cannot update index "+luceneIndexPath, e);
        }
	}

	private void runQuery() {
        IndexSearcher searcher = getIndexSearcher();
		try {
			List<Element> results = LuceneUtils.searchForDocument(searcher, query);
			for (Element result : results) {
				System.out.println("result: "+result.toXML());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    private void getFields() {
        IndexReader indexReader = getIndexReader();
	    for (int i = 0; i < indexReader.numDocs(); i++) {
	        Document doc = null;
			try {
				doc = indexReader.document(i);
			} catch (IOException e) {
				LOG.error("Cannot read doc: " + doc, e);
				continue;
			}
	        List<IndexableField> fields = doc.getFields();
	        for (IndexableField field : fields) {
	            // use these to get field-related data:
	            IndexableFieldType fieldType = field.fieldType();
				System.out.println("field: " + field.name()+" | "+fieldType.toString());
	        }
	    }
    }

      private void getFieldsOld() {
          IndexReader indexReader = getIndexReader();
          IndexSearcher searcher = getIndexSearcher();
    	  
 		  int numDocs = indexReader.numDocs();
    	  System.out.println("n "+numDocs);
    	  List<String> fields = Arrays.asList(new String[] {"id", "path", "contents"});
    	  for (String field : fields) {
	    	  try {
				summarizeFields(indexReader, field);
			} catch (IOException e) {
				LOG.error("Cannot summarize: " + e.getMessage());
			}
    	  }
    	  String line = "method";
    	  String field = "contents";
    	  Query query = LuceneUtils.createQuery(field, line);
    	  int numTotalHits = 25;
    	  Document doc1 = null;
    	  ScoreDoc[] hits = null;
    	  try {
	  		hits = searcher.search(query, numTotalHits).scoreDocs;
	  		doc1 = searcher.doc(hits[0].doc);
    	  } catch (Exception e) {
    		  throw new RuntimeException("cannot create hits", e);
    	  }
  		
  		System.out.println("fields "+doc1.getFields());
  		for (int i = 0; i < hits.length; i++) {
  	
  			Document doc;
			try {
				doc = searcher.doc(hits[i].doc);
			} catch (IOException e) {
				LOG.error("cannot search docs ", e);
				continue;
			}
  			String path = doc.get("path");
  			String title = doc.get("title");
  			String modified = doc.get("modified");
  			String contents = doc.get("contents");
  			System.out.println(path+" | " + title + " | " + contents + " | " + modified);
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
		System.out.println("LEAVES for " + field + " " + indexReader.leaves());
		System.out.println("freq "+indexReader.getSumDocFreq(field));
		   
	}
	
	private void createOrAppendIndex(OpenMode openMode) throws IOException {
		Directory indexDir = FSDirectory.open(luceneIndexPath);
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setIndexCreatedVersionMajor(LUCENE851.major); 
		iwc.setOpenMode(openMode);
		IndexWriter writer = new IndexWriter(indexDir, iwc);
		LuceneUtils.indexDocs(writer, inputPath);
		writer.close();
	}
	
	private boolean canRead(String type, Path path) {
		boolean readable = true;
		if (!Files.isReadable(path)) {
            System.out.println(type + " directory '" +path+ "' does"
          		+ " not exist or is not readable");
            readable = false;
        }
		return readable;
	}
    
// ============= may be useful ================


}






































