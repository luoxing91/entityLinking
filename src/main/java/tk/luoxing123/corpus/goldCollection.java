package tk.luoxing123.corpus;

import java.util.Scanner;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair; 
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.ScoreDoc;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.document.LongField;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import tk.luoxing123.corpus.Mention;
import tk.luoxing123.graph.Node;
import tk.luoxing123.corpus.Query;
import tk.luoxing123.utils.LuceneDocIterator;
import tk.luoxing123.utils.StringsUtils;
public class goldCollection{
    final static String LinkTabFile
		="/home/luoxing/windows/TrainingData/data/links.tab";
	final static String IndexDirectory
		= "/home/luoxing/goldCollection";
	final static String QueryXmlFile
		="/home/luoxing/windows/TrainingData/data/queries.xml";
	@Deprecated
	public static Searcher getGoldSearcher() throws IOException{
		Directory directory = FSDirectory.open(new File(IndexDirectory));
		IndexReader reader = DirectoryReader.open(directory);
		return new Searcher(reader,
							new StandardAnalyzer(Version.LUCENE_4_9));
	}
	public static goldCollection createInstance() {
		try{
			return new goldCollection(IndexDirectory);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	public goldCollection() throws IOException{
		this(IndexDirectory);
	}
	public goldCollection(String dir) throws IOException {
		Directory directory = FSDirectory.open(new File(dir));
		this.reader = DirectoryReader.open(directory);
	}
	public Iterator<Node> getGoldNode(){
		Iterator<Node> nodes = new IndexReaderIterator(this.reader);
		return nodes;
	}
	public Iterable<Node> getGoldNodeIterable(){
		return tk.luoxing123.utils.IterableUtils.make(getGoldNode());
	}
	public Iterable<Node> getGoldNodeIterableNotNIL(){
		return Iterables
			.filter(getGoldNodeIterable(),
					new Predicate<Node>(){
						public boolean apply(Node node){
							return !node.getArticleId().startsWith("NIL");
						}
						public boolean equals(Object object){
							return object.equals(object);
						}
						public int hashCode(){
							return 0;
						}
					});
	}
	public static void makeIndex(String index) throws Exception{
		IndexMaker inf = new IndexMaker(index);
		inf.startWrite();
		for(Pair<Query,Link>  pair: _toMatchList() ){
			inf.add(pair.getLeft(),pair.getRight());
		}
		inf.endWrite();
	}

	private static List<Pair<Query,Link>> _toMatchList() throws IOException{
		List<Pair<Query,Link>> lst = new ArrayList<>();
		for(Query query :
			Query.toQueryList(QueryXmlFile) ){
			for(Link link :_toLinkList()){
				if(link.getQueryId().equals(query.getQueryId())){
					lst.add(Pair.of(query,link));
				}
			}
		}
		return lst;
	}
	public static List<Link> _toLinkList() throws IOException{
		LineIterator in =
			new LineIterator(new Scanner(new File(LinkTabFile)));
		List<Link> links = new ArrayList<Link>();
		for(Link link : new LinkIterable(in)){
			links.add(link);
		}
		return links;

	}
	private IndexReader reader;

}
//////////////////////////////////////////////////////////////////////////////
class LineIterator implements Iterator<String>{
	public LineIterator(Scanner s){	this.scanner =s;	}
	public String next(){return this.scanner.nextLine();}
	public boolean hasNext() {return this.scanner.hasNextLine();}
	public void remove(){}
	private Scanner scanner;
}

class LinkIterable implements Iterable<Link>{
	public LinkIterable(Iterator<String> reader){	this.reader = reader;}
	public Iterator<Link> iterator(){ return new LinkIterator(reader);}
	private Iterator<String> reader;
}

class LinkIterator implements Iterator<Link>{
	public LinkIterator(Iterator<String> reader){this.reader = reader;}
	public boolean hasNext(){return reader.hasNext();	}
	public Link next() {return new Link(reader.next());	}
	public void remove(){}
	private Iterator<String> reader;
}
class Link{
	public Link(String line){
		String[] tmp = line.split("\\s+");
		if(tmp.length !=6)
			this.entity = tmp[1];
		this.query = tmp[0];
		//System.out.println(line+tmp.length);
		this.ner = tmp[2];
		StringsUtils.toBoolean(tmp[3]);
		StringsUtils.toBoolean(tmp[4]);
		StringsUtils.toBoolean(tmp[5]);
	}
	public String getQueryId(){		return query;	}
	public String getEntityId(){	return entity;	}
	public String toString(){		return query+entity;	}
	public String getNer(){		    return ner;	}
	private String entity,query,ner;
}
/////////////////////////////////////////////////////////////////////
class IndexReaderIterator implements Iterator<Node>{
	public IndexReaderIterator(IndexReader indexReader){
		this.index = new LuceneDocIterator(indexReader);
	}
	public boolean hasNext(){return index.hasNext();		}
	public Node next(){
		Document doc = index.next();
		String ner =	  doc.get("ner"); 
		Mention mention
			= new Mention(doc.get("fileID"),
						  doc.get("name"),
						  ner,
						  Integer.parseInt(doc.get("start")));
	return new Node(mention,doc.get("entity"),1.0);		}
	public void remove(){}
	private LuceneDocIterator index;
}

class IndexMaker{
	public IndexMaker(String str) throws IOException{
		this.analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		this.indexDir = str;
	}
	Directory  directory;
	String  indexDir;
	Analyzer analyzer;
	public void  startWrite() throws IOException{
		IndexWriterConfig config =
			new IndexWriterConfig(Version.LUCENE_4_9, this.analyzer);
		this.directory = FSDirectory.open(new File(this.indexDir));
		config.setOpenMode(OpenMode.CREATE);
		this.iwriter = new IndexWriter(directory,config);
	}
	private IndexWriter iwriter;
	public void add(Query q,Link l) throws IOException{
		Document document = new Document();
		document.add(new TextField("name",q.getName(),Field.Store.YES));
		document.add(new TextField("fileID",q.getArticleId(),Field.Store.YES));
		document.add(new LongField("start",q.getStart(),Field.Store.YES));
		document.add(new TextField("query",l.getQueryId(),Field.Store.YES));
		document.add(new TextField("entity",l.getEntityId(),Field.Store.YES));
		iwriter.addDocument(document);
	}
	public void endWrite() throws IOException{
		if(iwriter !=null )this.iwriter.close();
	}
}

class Searcher{
	public Searcher(IndexReader reader,Analyzer analyzer){
		this.searcher =new IndexSearcher(reader);
		this.analyzer = analyzer;
	}
	private IndexSearcher searcher;
	private Analyzer analyzer;
	public List<Integer>
		getStartbyArticleAndName(String articleId,String name){
		QueryParser parser =
				new QueryParser(Version.LUCENE_4_9,"name",this.analyzer);
			List<Integer> lst = new ArrayList<Integer>();
			try{
				org.apache.lucene.search.Query
					query = parser.parse(name);
				TopDocs result= searcher.search(query,10);
				ScoreDoc[] hits = result.scoreDocs;
				for(int i=0;i<result.totalHits && i< 10;i++){
					Document doc = this.searcher.doc(hits[i].doc);
					if(doc.get("fileID").equals(articleId)){
						lst.add(Integer.parseInt(doc.get("start")));
					}
				}
			}catch(IOException e){
						
			}catch(ParseException e){
						
			}
			return lst;
		}
		public List<String> getQueryIdListbyName(String name) {
			try{
				return getIdListbyName(name);
			}catch(IOException e){
				System.out.println("IOException");
				return new  ArrayList<String>();
			}catch(ParseException e){
				System.out.println("ParseException");
				return new  ArrayList<String>();
			}
		}
		public String getQueryIdByName(String str){
			return getIdbyName(str);
		}
		public String getIdbyName(String name){
			List<String> lst = getQueryIdListbyName(name);
			if(lst.size()>0)
				return lst.get(0);
			return null;
		}
		public List<String> getIdListbyName(String name)
			throws IOException ,ParseException{
			QueryParser parser =
				new QueryParser(Version.LUCENE_4_9,"name",this.analyzer);
			org.apache.lucene.search.Query
				query = parser.parse(name);
			TopDocs result= searcher.search(query,10);
			ScoreDoc[] hits = result.scoreDocs;
			List<String> lst = new ArrayList<String>();
			
			for(int i=0;i<result.totalHits && i< 10;i++){
				Document doc = this.searcher.doc(hits[i].doc);
				if(doc.get("name").equals(name)){
					lst.add(doc.get("query"));
				}
			}
			return lst;
		}
		
}
//name fileID ,start, entity ,query 
