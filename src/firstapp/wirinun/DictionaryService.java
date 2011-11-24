package firstapp.wirinun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class DictionaryService extends Service {

//    private RAMDirectory ramDirectory;
    private NIOFSDirectory fsDirectory;
    private Analyzer analyzer; 
    String[] stopWords; 	

    private final IDictionaryService.Stub binder =
	new IDictionaryService.Stub() {

	public List<String> getLanguageBMeaning(String word)
	throws RemoteException {

	    return query(word);
	}

	public List<String> getLanguageAMeaning(String word)
	throws RemoteException {
	    // TODO Auto-generated method stub
	    return null;
	}

	public List<String> getLanguageBMeaningInContext(String word,
		String context) throws RemoteException {
	    // TODO Auto-generated method stub
	    return null;
	}

	public List<String> getLanguageAMeaningInContext(String word,
		String context) throws RemoteException {
	    // TODO Auto-generated method stub
	    return null;
	}
    };
    @Override
    public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return this.binder;
    }
    @Override
    public void onStart(Intent intent, int startId) {
	super.onStart(intent, startId);

	try {
	    System.out.println("filename : " + intent.getExtras().get("index_place"));
	    fsDirectory = new NIOFSDirectory(new File((String) intent.getExtras().get("index_place")));
	    
	    
//	    ramDirectory = new RAMDirectory(fsDir);
	    
	    IndexReader ir = IndexReader.open(fsDirectory);
	    System.out.println("size : " + ir.numDocs());
	    
	    

	    
	    stopWords = new String[]{ "a", "an", "and", "are", "as", "be",
		    "but", "by", "if", "is", "it", "no", "not", 
		    "or","such", "that", "the", "their", "then", "there", "these",
		    "they", "this", "was", "will", "we", "our", "i", "me", "you", "your",
		    "he", "she", "it"};


	    analyzer = new SnowballAnalyzer(org.apache.lucene.util.Version.LUCENE_30, "English");


	} catch (IOException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}



    }

    public List<String> query(String str) {
	List<String> v = new ArrayList<String>();

	if (str.trim().equalsIgnoreCase("")) {
	    return v;
	}

	try {
	    Query q = new QueryParser(Version.LUCENE_30, "english", analyzer).parse(str);
	    System.out.println("querystr : " + str + " analyzed : " + q.toString());

	    int hitsPerPage = 1000;
	    IndexSearcher searcher = new IndexSearcher(fsDirectory);
	    System.out.println("maxdoc : " + searcher.maxDoc());
	    
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    //          System.out.println("Found " + hits.length + " hits.");
	    for (int i = 0; i < hits.length; ++i) {
		int docId = hits[i].doc;
		Document d = searcher.doc(docId);
		v.add(d.get("english") + " -> " + d.get("hungarian"));
		System.out.println((i + 1) + ". " + d.get("english") + " -> " + d.get("hungarian"));
	    }
	    return v;
	} catch (CorruptIndexException ex) {
	} catch (IOException ex) {
	} catch (ParseException ex) {
	}

	return null;
    } 
    public List<String> sentenceQuery(String str, String sentence) {
	List<String> v = new ArrayList<String>();

	if (sentence.trim().equalsIgnoreCase("")) {
	    return v;
	}

	/*
	        TokenStream result = new StandardTokenizer(new StringReader(sentence));


	        result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS);

	        try {

	            Token token = new Token();


	            while ((token = result.next(token)) != null) {
	                System.out.println("tokenstream : " + token.term());

	            }


	        } catch (IOException ex) {
	            Logger.getLogger(DictionaryManager.class.getName()).log(Level.SEVERE, null, ex);
	        }
	 */






	try {
	    System.out.println("sentence : " + sentence);
	    Query q = new QueryParser(Version.LUCENE_30, "english", analyzer).parse(sentence);
	    System.out.println("sentence querystr : " + str + " analyzed : " + q.toString());




	    int hitsPerPage = 1000;
	    IndexSearcher searcher = new IndexSearcher(fsDirectory);
	    searcher.setSimilarity(new MySimilarity());
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    //          System.out.println("Found " + hits.length + " hits.");
	    for (int i = 0; i < hits.length; ++i) {
		int docId = hits[i].doc;
		Document d = searcher.doc(docId);
		v.add(d.get("english") + " -> " + d.get("hungarian"));
		//	                System.out.println((i + 1) + ". " + d.get("english") + " -> " + d.get("hungarian"));
	    }
	    return v;
	} catch (CorruptIndexException ex) {
	} catch (IOException ex) {
	} catch (ParseException ex) {
	}

	return null;




    }

    private class MySimilarity extends DefaultSimilarity {

	@Override
	public float coord(int overlap, int maxOverlap) {

	    if (overlap > 1) {
		return super.coord(overlap, maxOverlap);
	    } else {
		return 0f;
	    }
	}

    }

}
