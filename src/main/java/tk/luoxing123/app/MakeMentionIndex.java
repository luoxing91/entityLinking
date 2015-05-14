package tk.luoxing123.app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tk.luoxing123.corpus.ArticleCollection;
import tk.luoxing123.corpus.Mention;
import tk.luoxing123.entitylink.MentionFactory;
import tk.luoxing123.utils.IndexWriterHelp;
import tk.luoxing123.utils.IterableUtils;
class IndexMaker{
	public IndexMaker(String str) throws IOException{
		this.help= new IndexWriterHelp(str);
		this.help.startWrite();
	}
	private IndexWriterHelp help;
	public void addMention(Mention m) throws IOException{
		org.apache.lucene.document.Document document
			= new org.apache.lucene.document.Document();
		document
			.add(new TextField("name",m.getName(),Field.Store.YES));
		document
			.add(new LongField("start",m.getStart(),Field.Store.YES));
		document
			.add(new TextField("ner",m.getNer(),Field.Store.YES));
		document
			.add(new TextField("fileId",m.getArticleId(),Field.Store.YES));
		help.addDocument(document);
	}
	public void close() throws IOException{
		help.endWrite();
	}
}
//---for write mention to 
//------------for solve out of memory Exception----------------------------

class ArticleCollectionIterator
	implements Iterator<ArticleCollection>{
	public ArticleCollectionIterator(File folder){
		this.iterator = IterableUtils.folderToFileIterator(folder);
	}
	private Iterator<FileInputStream> iterator;
	public boolean hasNext(){
		return iterator.hasNext();
	}
	public ArticleCollection next(){
		while(iterator.hasNext()){
            SequenceInputStream seq=makeSequenceInputStream(iterator.next());
			try{
				Optional<Document> odoc = makeXmlDocument(seq);
				ArticleCollection arts = new ArticleCollection(odoc.get());
				return arts;
            }catch(SAXException e){
				continue;
			}
        }
		return null;
	}
	public void remove(){}
	public static List<ArticleCollection>
		toArticleCollectionList(File folder){
        List<AritcleCollection> lst = new ArrayList<ArticleCollection>();
        Iterator<ArticleCollection> iter = new ArticleColection(folder);
        for(ArticleColection arts : () -> iter){
            lst.add(arts);
        }
		return lst;
	}
	//----------------------------------------------------------------------
	public  static SequenceInputStream
		makeSequenceInputStream(InputStream input){
		InputStream beginingStream
			=new ByteArrayInputStream(begining.getBytes());
		InputStream endStream
			=new ByteArrayInputStream(end.getBytes());
		List<InputStream> 
			streams	=Arrays.asList(beginingStream,input,endStream);
		return new SequenceInputStream(Collections.enumeration(streams));

	}
	//just for makeSequenceInputStream function
	static final String begining ="<docs>";
    static final String end="</docs>";
	
	
	//-----------------------------------------------------------------------
	public static Optional<Document> makeXmlDocument(SequenceInputStream sis)
		throws SAXException{
        Document doc=null;
        try{
			DocumentBuilder dBuilder 
				= DocumentBuilderFactory.newInstance().newDocumentBuilder();
             doc = dBuilder.parse(sis);   
        }catch(IOException e){
			e.printStackTrace();
            //System.out.println
			//	(e.getPublicId()+e.getSystemId()+e.getLineNumber());
        }catch(ParserConfigurationException e){
			e.printStackTrace();
		}
        return Optional.of(doc);
	}
}
public class MakeMentionIndex {
	//----------------------------------------------------------------------
	//just for free
	//1,cant't find function:println(String ) in class:App
	//2,can't fine variable:mention in class:APpp
	///3, can't find  class:InNeswire in class:App
	//----------------------------------------------------------------------
	static final String testFolder = "/home/luoxing/windows/test/newswireTest";
	static final String newFolder =  "/home/luoxing/windows/test/newswire";
    public static void main(String[] args) throws Exception{
		IndexMaker maker = new IndexMaker("/home/luoxing/mentions1");
		MentionFactory factory = MentionFactory.newInstance();
		
		Iterator<ArticleCollection>
			it	= new ArticleCollectionIterator
			(new  File("/home/luoxing/windows/test/newswire"));
		while(true){
			ArticleCollection arts = it.next();
			if(arts == null) break;
			Iterator<Mention> mit = factory.toMentionIterator(arts);
			while(true){
				Mention m = mit.next();
				if(m==null) break;
				maker.addMention(m);
			}
			System.out.println(arts);
		}
		maker.close();
						
    }//
	static void testArticleCollectionIterator(){
		//mentionIndexMaker();
		Iterator<ArticleCollection> it
			= new ArticleCollectionIterator(new File(newFolder));
		while(it.hasNext()){
			ArticleCollection arts = it.next();
			println(arts.toString());
			
		}
	}
	// -------------------pass----------------------------------
	static void  testToMentionList(){
		File folder = new File(newFolder);
		Iterator<ArticleCollection> it = new ArticleCollectionIterator(folder);
		int time=0;
		while(it.hasNext()&&time<1){
			ArticleCollection arts = it.next();
			println(arts.toString());
			time++;
		}
	}
	
	
	
	public static void testArticle() { 
		//for(ArticleInterface art :ArticleCollection.newInstance().toList()){
            //for(Mention mention : mentions.toMentionList()){
				//      System.out.println(mention);
            //}
		//}
	}
	
	static void println(String str){
		System.out.println(str);
	}
	static boolean isAnyOfStrings(String str){
		List<String> lst = new ArrayList<String>();
		for(String word : lst){
			if(str.equals(word))return true;
		}
		return false;
	}
	static<E> void println(List<E> lst){
		for(E e:lst){
			println(e.toString());
		}
		if(lst.isEmpty()){
			println("this is empty");
		}
	}
		@Deprecated
	static void mentionIndexMaker() throws Exception{
		Iterator<ArticleCollection> it
			= new ArticleCollectionIterator(new File(newFolder));
		int time=0;
		IndexMaker maker = new IndexMaker("/home/luoxing/mentions");
		MentionFactory mentions = MentionFactory.newInstance();
		while(it.hasNext()){
			long start = System.currentTimeMillis();
			ArticleCollection arts = it.next();
			if(arts !=null) {
				Iterator<Mention> mentionIt
					=  mentions.toMentionIterator(arts);
				while(mentionIt.hasNext()){
					time++;
					Mention mention =mentionIt.next(); 
					if(mention !=null ){
						//println(mention.getName()+
						//mention.getStart()+mention.getArticleId());
						maker.addMention(mention);
						}
					}
				println("used " +(System.currentTimeMillis()-start)/1000
						+ "s "+arts.toString());

			}
		
		}
		println("this hava "+time+" file ");
		maker.close();

	}


}


