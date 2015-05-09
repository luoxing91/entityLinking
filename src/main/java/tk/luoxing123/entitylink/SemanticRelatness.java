package tk.luoxing123.entitylink;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import tk.luoxing123.corpus.Mention;
import tk.luoxing123.graph.Node;
import tk.luoxing123.utils.TopN;

//id content;
public class SemanticRelatness {
	public SemanticRelatness(String entityIndex){
		
	}
	public double coref(Node node,Node other){
		return toDouble(node.getMention(),other.getMention());
	}
	
	public double loc(Node node,Node other){
		double i=0.0;
		return i;
	}
	
    static public Double toDouble(Mention mention,Mention other){
        if(mention.getArticleId().equals(other.getArticleId())){
            return 1.0;
		 }
        return 0.0;
    }
	
	
	static Map<String,Integer>
		getTermFrequency(IndexReader reader,int docId) throws IOException{
		Terms vector = reader.getTermVector(docId,"wiki_text");
		TermsEnum termsEnum =null;
		termsEnum = vector.iterator(termsEnum);
		Map<String,Integer> fre = new HashMap<>();
		BytesRef text =null;
		while(true){
			text = termsEnum.next();
			if(text==null) break;
			String term = text.utf8ToString();
			int freq = (int) termsEnum.totalTermFreq();
			fre.put(term,freq);
		}
		return fre;
	}
	
	
	static public Double cosine(Node node, Node other){
        List<Double> feature = toFeatureList(node);
        List<Double> feature2 = toFeatureList(other);
        return IntStream.rangeClosed(0,feature.size()-1)
            .mapToDouble(i->feature.get(i)*feature2.get(i)).sum();
        
    }
    static public List<Double> toFeatureList(Node node){
        List<Double> lst = new ArrayList<>();
        lst.add(node.getPopularity());
        lst.add(node.getTitleTF());
        return lst;
    }
    static public List<Node>
        nearstNeighbor(Node node,  List<Node> nodes,int k ){
        TopN<Node,Double> top = new TopN<>(20);
        for(Node n: nodes){
            top.put(n,cosine(node,n));
        }
        return top.toList();
    }
	public double rel(Node node, Node other) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
