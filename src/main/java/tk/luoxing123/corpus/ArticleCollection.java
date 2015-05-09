package tk.luoxing123.corpus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tk.luoxing123.entitylink.ArticleI;


public  class ArticleCollection {
    public java.util.Iterator<ArticleI> toArticleIterator(){
        return new ArticleIterator(doc);
    }
	public java.util.Iterator<ArticleI> iterator(){
		return new ArticleIterator(doc);
	}
	@Deprecated
    public List<String> getWords(){
        return new ArrayList<String>();
    }
	static final String newFolder = "/home/luoxing/windows/test/newswire";
	public static void main(String[] args){
		//File folder = new File(newFolder);
		//List<InputStream> streams = InputStreamOfFolder(folder);
		
		//for(InputStream stream: streams){
			//System.out.println(stream);
			//Iterator<ArticleInterface> it = iterator();
		//}
		try{
		String str = "/home/luoxing/windows/test/newswireTest/new_1.txt";
		ArticleCollection arts = new ArticleCollection(str);
		Iterator<ArticleI> it = arts.toArticleIterator();
		ArticleI srt = it.next();
		System.out.println(srt.getTextContent());
		}catch(SAXException io){
			
		}
	}
	
	static final String begining ="<docs>";
    static final String end="</docs>";
	static final InputStream beginingStream
		=new ByteArrayInputStream(begining.getBytes());
	static final InputStream endStream
		=new ByteArrayInputStream(end.getBytes());
	public static List<InputStream> InputStreamOfFolder(){
		return InputStreamOfFolder( new File(newFolder));
	}
	public static List<InputStream> InputStreamOfFolder(File folder){
		List<InputStream> streams = new ArrayList<InputStream>();
		if(!folder.exists()){
			System.out.println("no dir");
			return streams;
		} 
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles ==null) {
			System.out.println("no file");
			return streams;
		}
		for(File file : listOfFiles){
			try{
				streams.add(new FileInputStream(file));
				//System.out.println("cant't find file "+file.getName());
			}catch(FileNotFoundException e){
				System.err.println("cant't find file "+file.getName());
			}
		}
		return streams;
	}

	/*
	 *String --- --->InputStream---   -->SequeueInputStream --> Document
	 */
	@Deprecated
	public static Optional<Document> makeXmlDocument(String name)
        throws FileNotFoundException,SAXException,
		ParserConfigurationException,IOException{
		InputStream in = makeInputStream(name);
        SequenceInputStream sis =makeSequenceInputStream(in);
		return makeXmlDocument(sis);
    }
	@Deprecated
	public static   InputStream makeInputStream(String name)
        throws FileNotFoundException{
             FileInputStream  file = new FileInputStream(name);
			 return makeSequenceInputStream(file);
    }
	@Deprecated
	public  static SequenceInputStream
		makeSequenceInputStream(InputStream input){
		List<InputStream> streams;
		if(input!=null) streams	=
							Arrays.asList(beginingStream,input,endStream);
		else
			streams=Arrays.asList(beginingStream,endStream);
		return new SequenceInputStream(Collections.enumeration(streams));

	}
    @Deprecated
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
    private List<ArticleI> articles
		= new ArrayList<ArticleI>();
    protected Document doc;
	@Deprecated
    public ArticleCollection(String name) throws SAXException {
        try {
            Optional<Document> doc = makeXmlDocument(name);
            if(doc.isPresent()){
                this.doc=doc.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	@Deprecated
	public ArticleCollection(SequenceInputStream in) throws SAXException {
		try{
			Optional<Document> doc = makeXmlDocument(in);
			if(doc.isPresent()){
				this.doc=doc.get();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public ArticleCollection(Document doc){
		this.doc=doc;
	}
    public List<ArticleI> toList(){
		List<ArticleI> lst = new ArrayList<>();
		Iterator<ArticleI> it =  toArticleIterator();
		while(it.hasNext()){
			lst.add(it.next());
		}
		return lst;
    }
    public List<ArticleI> toArticleList(){ 
        return toList();
    }
    public int size(){ return articles.size();   }
    public List<Node> toNodeList(){
        java.util.Iterator<Node> it 
            = toNodeIterator();
        List<Node> lst = new ArrayList<>();
        while(it.hasNext()){
            lst.add(it.next());
        }
        return lst;
    }
    public Stream<Article> toArticleStream(){
        return toNodeStream().map(Article::new);
    }
    public Stream<Node> toNodeStream(){
		Iterator<Node> it = toNodeIterator();
        int characteristics = Spliterator.DISTINCT;
        Spliterator<Node> split =
            Spliterators.spliteratorUnknownSize(it,characteristics);
        return StreamSupport.stream(split,false);
    }
    public java.util.Iterator<Node> toNodeIterator(){
		return new NodeIterator(doc);
    }
	@Deprecated
	static public void println(String str){
		System.out.println(str);
	}
	@Deprecated
	static public void println(List<String> strs){
		for(String str:strs){
			println(str);
		}
	}
}
class NodeIterator implements java.util.Iterator<Node> {
	public NodeIterator(Document doc){
		nodes = doc.getElementsByTagName("DOC");
		i=0;
	}
	private NodeList nodes;
	private int i;
	public boolean hasNext(){
		return i<nodes.getLength();
	}
	public Node next(){
		Node node =nodes.item(i);
		i++;
		return node;
	}
	public void remove(){}
}
class ArticleIterator implements java.util.Iterator<ArticleI>{
	public ArticleIterator(org.w3c.dom.Document doc){
		 this.iterator = new NodeIterator(doc);
	 }
	public boolean hasNext(){
		 return iterator.hasNext();
	 }
	public void remove(){
		 
	 }
	private NodeIterator iterator;
	public ArticleI next(){
		while(iterator.hasNext()){
			Node node = iterator.next();
			if(node !=null) 
				return new Article(node);
		}
		return null;
	}
 }
