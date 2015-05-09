package tk.luoxing123.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.File;
import org.apache.lucene.document.Document;
import java.io.IOException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;

public class IndexSearcherHelp{
	private Analyzer analyzer;
	private QueryParser parser;
	private IndexSearcher searcher;
	private final String  indexDir;
	public IndexSearcherHelp(String str) throws IOException{
		this.indexDir = str;
		this.analyzer= new StandardAnalyzer(Version.LUCENE_4_9);
		Directory directory = FSDirectory.open(new File(this.indexDir));
		IndexReader reader = DirectoryReader.open(directory);
		this.searcher = new IndexSearcher(reader);
	}
	
	public IndexSearcher getSearcher(){
		return this.searcher;
	}
	public QueryParser queryParser(String str){
		QueryParser res = new QueryParser(Version.LUCENE_4_9,str,analyzer);
		return res;
	}
	
	public void search(String line ) throws IOException,ParseException{
		Query query = parser.parse(line);
		int hitsPerPage=8000;
		TopDocs results = searcher.search(query,20);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);
		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			System.out.println(doc);
		}
	}
	public TopDocs getDocsBy(String name1) throws ParseException,IOException{
		Query query = this.parser.parse(name1);
		TopDocs result1 = this.searcher.search(query,8000);
		return result1;
	}
	public double  search(String name1,String name2)
		throws ParseException ,IOException{
		double result = 0.0;
		TopDocs res1 = getDocsBy(name1);
		TopDocs res2 = getDocsBy(name2);
		if(res1.totalHits>0 && res2.totalHits>0){
			for(int i= 0;i<res1.scoreDocs.length;i++){
				for(int j=0;j< res2.scoreDocs.length;j++){
					if(res1.scoreDocs[i].doc == res2.scoreDocs[j].doc)
						result += 1;
				}
			}
		}
		return result;
	}
	
}
