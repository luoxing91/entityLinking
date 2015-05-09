package tk.luoxing123.entitylink;

import tk.luoxing123.corpus.Article;
import tk.luoxing123.corpus.Entity;
import tk.luoxing123.corpus.Mention;

import java.util.Map;


public class ContextFeature {
    static public boolean isCapitalStart(Mention mention){
        return Character.isUpperCase(mention.getFirstChar());
    }
    static public boolean isCapitalEnd(Mention mention){
        return Character.isUpperCase(mention.getEndChar());
    }
    static public Map<String,Integer> toTop100Word(Article art){
        return art.toTopWordMap(100);
    }
    static public Map<String,Integer> toTop100Word(Entity art){
        return art.toTopWordMap(100);
    }

}
