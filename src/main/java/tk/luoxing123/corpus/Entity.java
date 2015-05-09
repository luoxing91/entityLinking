package tk.luoxing123.corpus;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import tk.luoxing123.entitylink.ArticleI;
import tk.luoxing123.utils.IterableUtils;
import tk.luoxing123.utils.StringsUtils;


public class Entity implements ArticleI{
   
	public Entity(Node node){
        this.article = new Article(node);
		this.node = node;
    }
    public Entity(String title){
        
        this.title = title;
    }
    public Map<String,Integer> toTopWordMap(int k){
        return this.article.toTopWordMap(k);
    }
	public String getArticleId(){
		return this.article.getArticleId();
	}
    private String  title;
	private Node node;
	private Article article;
    public String getTitle(){
		if(title!=null)return title;
		else{
			title = new TitleObject(node).getValue();
        }
        return title;
    }
    public List<String> getTitleList(){
        return StringsUtils.toList(getTitle(),"_");
    }
    public List<String> toTitleList(){
        return getTitleList();
    }

	public String getTextContent(){
		return this.article.getTextContent();
	}
	@Override
	public Map<String, Integer> toWordFrequencyMap() {
		// TODO Auto-generated method stub
		return null;
	}  
}
class TitleObject{
	TitleObject(Node nodes){
		for(Node node: IterableUtils.make(nodes.getAttributes())){
			if(node.getNodeName().equals("wiki_title")){
				this.title = node.getNodeValue();
			}
		}
	}
	String title;
	String getValue(){
		return title;
	}
}