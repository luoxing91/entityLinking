package tk.luoxing123.corpus;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class EntityIndex{
	public EntityIndex(String indexStr){
		SAXParserFactory spf = SAXParserFactory.newInstance();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
        try {
            IndexWriterConfig config =
                new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
            config.setOpenMode(OpenMode.CREATE);
            Directory directory = FSDirectory.open(new File(indexStr));
			this.iwriter = new IndexWriter(directory,config);
             this.saxParser = spf.newSAXParser();
        }catch(Exception e){
			e.printStackTrace();
		}
	}
	private IndexWriter iwriter;
	private SAXParser saxParser;
	public void addWikiFile(File wiki){
		try{
			saxParser.parse(wiki,new Handler(iwriter));
		}catch(SAXException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void close(){
		try{
			iwriter.close();
		}catch(IOException e){
			e.printStackTrace();
		}

	}

}
@Deprecated
public class InferenceEngine {
    final static String  INDEXStr="/tmp/inference";
    final static String  wikiFile = "/home/luoxing/windows/test/wikiFile.xml";
    public InferenceEngine(String index){
		this.indexStr =index;
    }
	

	public void addWikiFile(File wiki) throws IOException{
		try{
			saxParser.parse(wiki,new Handler(iwriter));
		}catch(SAXException e){
			e.printStackTrace();
		}
	}

	public void startWrite(){
		SAXParserFactory spf = SAXParserFactory.newInstance();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
 
        try {
            IndexWriterConfig config =
                new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
            config.setOpenMode(OpenMode.CREATE);
            Directory directory = FSDirectory.open(new File(indexStr));
             this.iwriter = new IndexWriter(directory,config);
             this.saxParser = spf.newSAXParser();
        }catch(Exception e){
			e.printStackTrace();
		}
	}
	private IndexWriter iwriter;
	private SAXParser saxParser;
	private String indexStr;
	public void endWrite() throws IOException{
		iwriter.close();
	}
	@Deprecated
	public InferenceEngine(){
		this.indexStr = INDEXStr ;
	}
	
    
}	
class Handler extends DefaultHandler {
	public Handler(IndexWriter writer){
		this.iwriter=writer;
	}
	IndexWriter iwriter;
	@Override
	public void startElement(String uri, String localName,
							 String qName, Attributes attr)
		throws SAXException {
		current = qName;
		if(current.equals("entity")){
			fileId= attr.getValue("id");
			ner = attr.getValue("UKN");
			titleName = attr.getValue("name");
		}else if(current.equals("wiki_text")){
			textContent= new StringBuffer(100);
		}
            
	}
	public void endElement(String uri,String localName, String qName){
		if(qName.equals("wiki_text")){
			createIndex();
		}else{
            
		}
		current=null;
	}
	public void characters(char[] ch,int start , int length){
		if(current!=null&&current.equals("wiki_text") ){
			textContent.append(new String(ch,start,length));
			//System.out.println(textContent);
		}
	}
	private String current=null;
	private StringBuffer textContent=null;
	private String fileId=null ;
	private String ner =null;
	private String titleName=null;
	private  void 	createIndex(){
		Document doc = new Document();
		//System.out.println(textContent.substring(0,10));
		doc.add(new TextField("content",textContent.toString(),
							  Field.Store.NO));
		doc.add(new StringField("id",fileId,Field.Store.YES));
		doc.add(new StringField("ner",ner,Field.Store.YES));
		doc.add(new TextField("titleName",titleName,Field.Store.YES));
		try{
			iwriter.addDocument(doc);   
		}catch(IOException e){
			e.printStackTrace();
		}
		textContent=null;
		fileId=null;
		ner =null;
		titleName =null;
		//System.out.println(fileID);
	}
}//
/*

	private static void
		createIndex(IndexWriter iwriter, String fileID,String textContent){
		Document doc = new Document();
		//System.out.println(textContent.substring(0,10));
		doc.add(new TextField("content",textContent,Field.Store.NO));
		doc.add(new StringField("id",fileID,Field.Store.YES));
		try{
			iwriter.addDocument(doc);   
		}catch(IOException e){
			e.printStackTrace();
		}
		//System.out.println(fileID);
	}
    
}

*/
