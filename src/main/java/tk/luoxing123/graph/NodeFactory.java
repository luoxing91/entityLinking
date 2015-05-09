package tk.luoxing123.graph;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import tk.luoxing123.corpus.Mention;
import tk.luoxing123.entitylink.MentionFactory;
import java.util.Iterator;
import java.util.LinkedList;
import tk.luoxing123.graph.Node;


class EntitySearcher{
	public EntitySearcher(String indexStr){
		try{
			this.searcher = new IndexSearcher(DirectoryReader
                          .open(FSDirectory
								.open(new File(indexStr))));
		}catch(IOException e){
			System.out.println("notFile");
			e.printStackTrace();
		}
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		this.parser=
                new QueryParser(Version.LUCENE_4_9,"content",analyzer);
		
	}
	public LinkedList<Node>
		getNodeList(final tk.luoxing123.corpus.Mention mention){
		LinkedList<Node> res = new LinkedList<Node>();     
		if(mention ==null ) throw new NullPointerException();
		try{
			String name = mention.getName();
			Query query = this.parser.parse(name);
			TopDocs results = this.searcher.search(query,2);
			if(results.totalHits<1) return res;
			ScoreDoc[] hits = results.scoreDocs;
			Double sumScore =1.0;
			sumScore=_sumScore(hits);
			
			for(int i=0;i<results.totalHits && i<2;i++){
				Document doc = searcher.doc(hits[i].doc); 
				res.add
				(new Node(mention,
						  doc.get("id"),
						  Double.valueOf((double)hits[i].score/sumScore)));
			}
			return res;
		}catch(IOException e){
			
		}catch(ParseException e){
			
		}
		return res;
	}
	private double _sumScore(ScoreDoc[] hits){
		double sum =0;
		for(ScoreDoc sc: hits){
			sum += sc.score;
		}
		return sum;
	}
	private IndexSearcher searcher;
	private QueryParser parser; 
}//EntitySearcher;
class NodeIterable implements Iterable<Node>{
	public NodeIterable(String mention,String inference){
		this.iter = new NodeIterator_(mention,inference);
	}
	public Iterator<Node> iterator(){  return this.iter;	}
	private Iterator<Node> iter;
}
class NodeIterableNER implements Iterable<Node>{
	public NodeIterableNER(NodeIterable iterable){
		this.iterable= iterable;
	}
	public Iterator<Node> iterator(){
		return new NERIterator(iterable.iterator());
	}
	NodeIterable iterable;
}
class NERIterator implements Iterator<Node> {
	public NERIterator(Iterator<Node> it){
		this.it = it;
	}
	private Iterator<Node> it;
	public void remove(){}
	public Node next(){
		Node node = it.next();
		if(!(node.getNer().equals("LOCATION")|
			 node.getNer().equals("ORGANIZATION")|
			 node.getNer().equals("PER"))  ){
			return next();
		}else{
			return node;
		}
	}
	public boolean hasNext(){
		return it.hasNext();
	}
}
class  NodeIterator_ implements java.util.Iterator<Node> {
	public NodeIterator_(String str,String entityIndex){
		try{
			this._hasNext=true;
			this.index = MentionFactory.makeMentionIterator(str);
			this.factory = new EntitySearcher(entityIndex);
			this.nodes=
				this.factory.getNodeList(index.next());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public boolean hasNext(){
		return _hasNext;
	}
	public Node next(){
		Node res ;
		if(!nodes.isEmpty()){
			res = nodes.poll();
		}else if(index.hasNext()){
			Mention m = index.next();
			if(m.getNer().equals("TIME"))
				return this.next();
			this.nodes = this.factory.getNodeList(m);
			if(this.nodes!=null&&!this.nodes.isEmpty())
				res = this.nodes.poll();
			else
				return this.next();
		}else{
			res=null;
		}
		if(res ==null) _hasNext=false;
		return res;
	}
	public void remove(){	}
	private boolean _hasNext;
	private Iterator<Mention> index ;
	private LinkedList<Node> nodes;
	private EntitySearcher factory;
	
}

public class NodeFactory {
    public NodeFactory(String str ){
		this.indexStr = str;
    }
	public NodeFactory(){
		this("/home/luoxing/inference1");
	}
	private String indexStr;
	private boolean hasInit=false;
	//return the canditates Node of the mention;
	public Iterable<Node> getCandidatesIterable() throws IOException{
		return new NodeIterable("/home/luoxing/mentions",
								"/home/luoxing/inference1");
	}
	public Iterator<Node> getCandidatesIterator(String index){
		return new NodeIterator_("/home/luoxing/eval",
								 "/home/luoxing/inference1");
	}
	public Iterator<Node> getCandidatesIterator() throws IOException{
		return new NodeIterator_("/home/luoxing/mentions",
								"/home/luoxing/inference1");
	}
	public Iterable<Node> getNodeIterable() throws IOException{
		return new
			NodeIterableNER(new
							NodeIterable("/home/luoxing/mentions",
										 "/home/luoxing/inference1"));
	}
	public List<Node>
		getCandidates(final tk.luoxing123.corpus.Mention mention){
		return getNodeList(mention);
	}
    public List<Node> getNodeList(tk.luoxing123.corpus.Mention mention){
		if(!this.hasInit) {
			startSearch();
			this.hasInit=true;
		}
        List<Node> lst = new ArrayList<>();
		try{
			lst = search(mention);
		}catch(IOException e){
			e.printStackTrace();
		}catch(ParseException e){
			e.printStackTrace();
		}
		return lst;

    }
	@Deprecated
    public LinkedList<Node> search(final Mention mention)
		throws IOException,ParseException{
        String name = mention.getName();
		Query query = this.parser.parse(name);
        TopDocs results = this.searcher.search(query,10);
        ScoreDoc[] hits = results.scoreDocs;
		Double sumScore =1.0;
		sumScore=_sumScore(hits);
		
        LinkedList<Node> res = new LinkedList<Node>();     
        for(int i=0;i<results.totalHits && i<10;i++){
            Document doc = searcher.doc(hits[i].doc); 
            res.add
				(new Node(mention,
						  doc.get("id"),
						  Double.valueOf((double)hits[i].score/sumScore)));
        }
        return res;
    }

    static private double _sumScore(ScoreDoc[] hits){
        double sum =0;
        for(int i=0;i<hits.length&&i<10;i++){
            sum+= hits[i].score;
        }
        return sum ;
    }
    private IndexSearcher searcher;

	private QueryParser parser; 
	public void startSearch(){
		try{
			this.searcher = new IndexSearcher(DirectoryReader
                          .open(FSDirectory
								.open(new File(indexStr))));
		}catch(IOException e){
			System.out.println("notFile");
			e.printStackTrace();
		}
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		this.parser=
                new QueryParser(Version.LUCENE_4_9,"content",analyzer);

    }

}
