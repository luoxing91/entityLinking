
package tk.luoxing123.corpus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import tk.luoxing123.entitylink.MentionI;

public class Query implements Comparable<Query>,MentionI{
	public static List<Query> toQueryList(String filename){
        SAXParserFactory spf = SAXParserFactory.newInstance();
        QueryHandler handler = new QueryHandler();
        try {
            SAXParser saxParser = spf.newSAXParser();
            saxParser.parse(new File(filename),handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler.getQueryList();
    }
    public Query(String name,String docId,String id,int begin) {
        this.name=name;
        this.id = id;
        this.docID=docId;
        this.begin=begin;
    }
	@Deprecated
    public String getId(){
        return id;
    }
	public String getQueryId(){
		return id;
	}
    @Override
    public int compareTo(Query o) {
        return id.compareTo(o.id);
    }
	@Override 
    public int hashCode(){
        //return id.hashCode();
		return new HashCodeBuilder(17,31)
			.append(name)
			.append(id)
			.toHashCode();
    }
    @Override
		public boolean equals(Object other){
		if(!(other instanceof Query))
			return false;
		Query q = (Query)other;
		return _equals(q);
	}
    public boolean _equals(Query  other){
        return id.equals(other.id);
    }
    @Override
    public String toString() {
        return name+"\t"+begin+"\t"+docID;
    }
	@Override
    public int getStart(){
        return begin;
    }
	@Override
    public String getName(){
        return name;
    }
	@Override
	public String getArticleId(){
        return docID;
    }
	public String getNer(){
		return "O";
	}
	private String id;
    private String name;
    private String docID;
    private int begin;
    /*
    public File getDocument(){
        String path = Constant.queryDocDirectory;
        String[] str = docID.split("[_||\\.]");
        path += str[0].toLowerCase()+"_"+str[1].toLowerCase()+"\\";
        path += str[2]+"\\";
        return new File(path+docID+".sgm");
        return null;
    }
    
    */
    
}
class QueryHandler extends DefaultHandler{
	List<Query> queries = new ArrayList<Query>();
	String docid;
	String name="some thing wrong";
	String id="ok";
	String current;
	int  begin;
	public List<Query> getQueryList(){
		return queries;
	}
	@Override
	public void endElement(String arg0, String arg1, String qName) {
		// TODO: Stub
		if(qName.equals("query")){
                queries.add(new Query(name,docid,id,begin));
		}
		current=null;
	}
	@Override
	public void startElement(String arg0,
							 String arg1, String qName, Attributes attr) {
		if(qName.equals("query")) {
			id=attr.getValue("id");
		}
		current=qName;
	}
	@Override
	public void characters(char[] ch, int start, int length) {
            // TODO: Stub
		String content = new String(ch,start,length);
		
		if(current!=null&&current.equalsIgnoreCase("name")){
			name= new String(ch,start,length);
			//                System.out.println(name+""+name.length());
		}else if(current !=null &&current.equalsIgnoreCase("docid")){
                docid=content;
		}else if(current !=null &&current.equalsIgnoreCase("beg")){
			begin= Integer.parseInt(content);
			//begin = 
		}
	}

}

