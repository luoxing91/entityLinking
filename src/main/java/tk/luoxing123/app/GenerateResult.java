package tk.luoxing123.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import tk.luoxing123.corpus.goldCollection;
import tk.luoxing123.graph.Graph;
import tk.luoxing123.graph.Node;
import tk.luoxing123.graph.NodeFactory;
import tk.luoxing123.utils.IterableUtils;

public class GenerateResult{
	/**
	 *
	 * @param args - Arguments passed from the command line
	 **/
	public static void main(String[] args) {
		ResultIndex index = new ResultIndex("/home/luoxing/result");
		Iterator<Node> iter = new ResultIterator();
		int i=0;
		while(true){
			Node node = iter.next();		
			if(node==null) {
				System.out.println(i+"is null");
				break ;
			}
			index.add(node);
			i++;
		}
		index.close();
	}
}
class ResultIndex{
	public ResultIndex(String indexPath){
		try{
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
			IndexWriterConfig iwc
				= new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			Directory dir = FSDirectory.open(new File(indexPath));
			this.iwriter = new IndexWriter(dir, iwc);
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	public void add(Node node){
		Document doc = new Document();
		doc.add(new StringField("name",node.getName(),Field.Store.YES));
		doc.add(new StringField("enity",node.getEntityId(),Field.Store.YES));
		doc.add(new StringField("ner",node.getNer(),Field.Store.YES));
		doc.add(new StringField("fileId",node.getArticleId(),
								Field.Store.YES));
	}
	public void close(){
		try{
			iwriter.close();	
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	private IndexWriter iwriter;

}
class ResultIterable implements Iterable<Node>{
	public Iterator<Node> iterator(){
		return new ResultIterator();
	}
}
class ResultIterator implements Iterator<Node>{
	public ResultIterator(){
		this._hasNext =true;
		this.gold = IterableUtils
			.toList(goldCollection.createInstance()
					.getGoldNodeIterableNotNIL());
		try{
			this.train = new NodeFactory().getCandidatesIterator();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public boolean hasNext(){
		return _hasNext;
	}
	public Node next(){
		Node ret;
		if(result==null&&train.hasNext()){
			System.out.println("result=null");
			List<Node> tmp = toList(train,1000);
			this.result = new Graph(gold,tmp).toResultIterator();
			System.out.println("get result ends");
		}
		if(this.result!=null){
			ret =this.result.next();
		}else {
			ret =null;
		}
		if(ret==null) _hasNext=false;
		return ret;
	}
	public void remove(){}
	private List<Node> gold;
	private boolean _hasNext;
	private Iterator<Node> result;
	private Iterator<Node> train;
	private List<Node> toList(Iterator<Node> iter,int len){
		List<Node> res = new ArrayList<Node>();
		Node value;
		for(int i=0;i<len;i++){
			value = iter.next();
			if(value ==null) {
				System.out.println("has in "+i);
				break;
			}
			res.add(value);
		}
		System.out.println("end of to 1000 tmp Node");
		return res;
	}

}
