package tk.luoxing123.app;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
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

public class MakeEntityIndex{
	/**
	 * @param args - Arguments passed from the command line
	 **/
	public static void main(String[] args) {
		File dir = new File("/home/luoxing/windows/test/data");
		try{
			EntityIndexWriter inf = 
				new EntityIndexWriter("/home/luoxing/inference1");
			for(File file : dir.listFiles()){
				if(file.getName().endsWith("xml")){
					inf.addWikiFile(file);
					System.out.println(file.getName());
				}
			}
			inf.close();
		}catch(IOException e){
			e.printStackTrace();
		}catch(SAXException e){
			e.printStackTrace();
		}catch(ParserConfigurationException e){
			e.printStackTrace();
		}
	}
		
		
}
class EntityIndexWriter {
	public EntityIndexWriter(String indexStr)
		throws IOException,ParserConfigurationException,SAXException{
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		IndexWriterConfig config =
                new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		config.setOpenMode(OpenMode.CREATE);
		SAXParserFactory spf = SAXParserFactory.newInstance();
		this.saxParser = spf.newSAXParser();
		Directory directory = FSDirectory.open(new File(indexStr));
		this.iwriter = new IndexWriter(directory,config);
	}
	SAXParser saxParser;
	IndexWriter iwriter ;
	public void addWikiFile(File wiki) throws IOException,SAXException{
		saxParser.parse(wiki,new Handler(iwriter));
	}
	public void close() throws IOException{
		iwriter.close();
	}
}

class Handler extends DefaultHandler {
	public Handler(IndexWriter writer){
		this.iwriter=writer;
	}
	private IndexWriter iwriter;
	@Override
	public void startElement(String uri, String localName,
							 String qName, Attributes attr)
		throws SAXException {
		current = qName;
		if(current.equals("entity")){
			fileId= attr.getValue("id");
			ner = attr.getValue("type");
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
		}
	}
	private String current=null;
	private StringBuffer textContent=null;
	private String fileId=null ;
	private String ner =null;
	private String titleName=null;
	private  void 	createIndex(){
		Document doc = new Document();
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
}
