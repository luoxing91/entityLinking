package tk.luoxing123.corpus;

import java.util.List;
import java.util.ArrayList;


public class EntityCollection{
   
    public Integer getTotal(){
        return  arts.size();
    }
    public Integer getTotal(Mention mention){
        return 0;
    }
    public EntityCollection(ArticleCollection arts){
        this.arts = arts;
    }/*
    public EntityCollection(String  str){
		try{
			this(new ArticleCollection(str));
		}catch(SAXException e){
			e.printStackTrace();
		}
		}*/
    public List<Entity> toList(){
        return new ArrayList<Entity>();
    }
    private ArticleCollection arts;
}
