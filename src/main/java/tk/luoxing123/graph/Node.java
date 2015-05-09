package tk.luoxing123.graph;
import org.apache.lucene.document.Document;

import tk.luoxing123.corpus.Mention;
import tk.luoxing123.entitylink.NodeI;
import tk.luoxing123.utils.LuceneHelp;

import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field;

import java.util.Iterator;

import org.apache.lucene.document.DoubleField;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;

public class Node implements NodeI{
	static public Document makeDocument(Node node ){
		Document doc= new Document();
		doc.add( new StringField("name", node.getName(), Field.Store.YES));
		doc.add( new StringField( "ner" , node.getNer(), Field.Store.YES));
		doc.add (new LongField("start", getNodeStart(node),Field.Store.YES));
		doc.add( new StringField( "fileId",node.getArticleId(),
								  Field.Store.YES));
		doc.add(new  StringField( "enitityId", node.getEntityId(),
								  Field.Store.YES));
		doc.add(new DoubleField("pro", node.getPopularity(),
								Field.Store.YES));

		return doc;
	}
	static private Long getNodeStart(Node node){
		return (new Integer(node.getMention().getStart())).longValue();
	}
	static public void writeNodeIndex(Iterator<Node> lst, String directory)
	throws IOException{
		IndexWriter write= LuceneHelp.makeWriter(directory);
		Node node;
		while(lst.hasNext()){
			node = lst.next();
			if(node==null) break;
			write.addDocument(makeDocument(node));
		}
		write.close();
	}
	/////////////////////////////////////////////////////////////
	public String getNer(){
		String m =mention.getNer();
		if(m==null) return "NLN";
		return  m;
	}
    public Mention getMention(){
        return mention;
    }
	public String getName(){
		return mention.getName();
	}
	public String getEntityId(){
		return entityId;
	}
	public String getArticleId(){
		return mention.getArticleId();
	}
	public Double getPopularity(){
        return p;
    }

	@Override 
    public String toString(){
        StringBuffer buffer = new StringBuffer(100);
        buffer.append(getName())
			.append("\t")
			.append(getArticleId())
			.append("\t")
			.append(getNer())
            .append("\t")
            .append(getEntityId())
            .append("\t")
            .append(getPopularity());
        return buffer.toString();
    }
    public String getMentionName(){
        return mention.getName();
    }
    public Node(Mention mention,String  entity,Double p){
        this.mention =mention;
		this.entityId = entity;
		this.p= p;
        // using 1 ,or prior popularity 1/|wikis|;
    }
    public boolean isSameMention(Node other){
        return mention.equals(other.mention);
    }
    public boolean isLessThan(Node other){
        return p<other.p;
    }
    public boolean isMoreThan(Node other){
        if(other==this) return false;
        if(other==null )return true;
        return !isLessThan(other);
    }
    public Double getTitleTF(){
        //List<String> lst1 =entity.getTitleList();
        //List<String> lst = mention.getTitleList();
        return 0.0;//ListUtils.jaccardIndex(lst,lst1);
    }
    private Mention mention;
	private String entityId;
    private Double p;
    public boolean visited;
}
