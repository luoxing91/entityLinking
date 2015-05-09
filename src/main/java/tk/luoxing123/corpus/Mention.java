package tk.luoxing123.corpus;

import java.util.List;

import org.apache.lucene.document.Document;

import tk.luoxing123.entitylink.ArticleI;
import tk.luoxing123.entitylink.MentionI;
import tk.luoxing123.utils.StringsUtils;


public class Mention implements MentionI{
    private String fileId;
    private String word;
    private int start;
    private String ner;
	//    private ArticleInterface art;
	
    public Mention(ArticleI file,
				   String word,String ner, Integer start){
        //this.art=file;
		this.fileId = file.getArticleId();
        this.word = word;
        this.ner=ner;
        this.start =start;
    }
	public Mention(String fileId,String name ,String ner,int start){
		this(fileId,name,ner,new Integer(start));
	}
	public Mention(String fileId,String name ,String ner ,Integer start){
		//this.art =null;
		this.fileId = fileId;
		this.word = name;
		this.ner = ner;
		this.start = start;
	}
    public boolean equals(Mention other){
        return word.equals(other.word)&& ner.equals(other.ner);
    }
    public char getFirstChar(){ return word.charAt(0);    }
    public char getEndChar(){   return word.charAt(word.length()-1);    }
    public String getNer(){        return ner;    }
	public String getArticleId(){  return  fileId;	}
    public String getName(){       return word;    }
    public int getStart(){     return start;    }
	@Deprecated
    public String getWord(){        return  word;    }
	@Deprecated
    //public ArticleInterface toArticle(){        return art;    }

    @Override
    public java.lang.String toString() {
        // TODO: Stub
        return getArticleId()+"\t"+word+"\t"+ner+"\t"+start;
    }
	public List<String> getTitleList(){
        return StringsUtils.toList(this.getWord());
    }
    public List<String> toList(){
        return getTitleList();
    }
    public List<String> toTitleList(){
        return getTitleList();
    }
    public List<String> toWordList(){
        return getTitleList();
    }
	public static Mention ofDocument(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}
}
    //    public Double idf(){
    //    return Math.log(wikis.getTotal()/wikis.getTotal(this));
    //}
    // public static Mention queryToMention(Query query,ArticleInterface art){
    //     return new Mention
    //         (art,query.getWord(),"O",query.getStart());
    // }

