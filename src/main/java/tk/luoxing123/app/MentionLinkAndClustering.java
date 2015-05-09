package tk.luoxing123.app;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tk.luoxing123.corpus.goldCollection;
import tk.luoxing123.graph.Graph;
import tk.luoxing123.graph.Node;
import tk.luoxing123.graph.NodeFactory;
import tk.luoxing123.utils.IterableUtils;
import tk.luoxing123.utils.LuceneHelp;

import org.apache.lucene.index.IndexWriter;

class MentionLinkAndClustering{
    public static void main(String[] args) throws IOException{
		IndexWriter write = LuceneHelp.makeWriter("/home/luoxing/linkResult");

		List<Node> can =
				IterableUtils
			.toList(new NodeFactory()
					.getCandidatesIterator("/home/luoxing/eval"),10000);
		List<Node> train 
		   = IterableUtils
			.toList(goldCollection
					.createInstance()
					.getGoldNodeIterableNotNIL(),1000);
		int k=10;
		List<List<Node>> cans = partition(can,k);
		int i=0;
		while(i<k){
			for(Node node : makeNodeLinked(train,cans.get(i))){
				if(node==null) break;
				write.addDocument(Node.makeDocument(node));
				System.out.println(node);
				//System.out.println(node);
			}
			i++;
			System.out.println(i);
		}
		write.close();
			
									
    }
	public static List<List<Node>> partition(List<Node> can,int k){
		List<List<Node>> part = new ArrayList<List<Node>>();
		int max =1000;

		int index=0;
		while(index<k){
			List<Node> ns = new ArrayList<Node>();
			int i=0;
			while(i<max){
				ns.add(can.get(index*max+i));
				i++;
			}
			part.add(ns);
			index++;
		}
		return part;
	}
	public static Iterable<Node> makeNodeLinked(List<Node> train,
												List<Node> can){
		
		return new Graph(train,can).toResultIterable();
		//return can;
	
	}
	
}
