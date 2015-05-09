package tk.luoxing123.corpus;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;

import tk.luoxing123.entitylink.ArticleI;
import tk.luoxing123.utils.StringsUtils;
import tk.luoxing123.utils.TopN;
import tk.luoxing123.utils.UnigramStatistics;
import tk.luoxing123.utils.IterableUtils;

import org.w3c.dom.NamedNodeMap;


public  class Article implements ArticleI{

	public String getArticleId(){
		return id.getId();
	}
	public List<String> getTitleList(){
		return null;
	}
    public Article(Node node){
        this.text = node.getTextContent();
        this.id = new IdObject(node);
    }
	private String text;
	private IdObject id ;
	public String toString(){
		return getArticleId();
	}
    public String getTextContent(){
        return text;
    }
    public List<String> getWords(){
        return StringsUtils.toList(text);
    }
    public Set<String> toTopWordSet(int k){
        return TopN.of(UnigramStatistics
                       .toMap(getTextContent()),k)
            .toSet();

    }
    public Map<String,Integer> toTopWordMap(int k){
        return TopN.of(UnigramStatistics
                       .toMap(getTextContent()),k)
            .toMap();
    }
	@Deprecated
	public Map<String,Integer> toWordFrequencyMap(){
		return StringsUtils.toMap(getWords());
	}
	@Deprecated
	public List<String> contentToList(){
		return getWords();
	}
	//for webwires
	public String getIdi(Node node){
        for(Node node1 : IterableUtils.make(node.getChildNodes())){
            if(node1.getNodeName().equals("DOCID"))
                return node1.getTextContent();    
        }
		return null;
    }

}
class IdObject{
	final static String NOID="noknown";
	private   String id=null;
	public IdObject(Node node){
		NamedNodeMap map = node.getAttributes();
		for(Node n: IterableUtils.make(map)){
			if(n.getNodeName().equals("id") ){
					id=n.getNodeValue();
			}
		}
		if(id==null) id = NOID;
	}
	public String getId(){return id;}
}
