package tk.luoxing123.utils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.apache.lucene.store.FSDirectory;
import java.io.File;
import org.apache.lucene.document.Document;
import java.io.IOException;


public class IndexWriterHelp{
	public IndexWriterHelp(String str) throws IOException{
		this.indexDir = str;
	}
	public void  startWrite() throws IOException{
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		IndexWriterConfig config =
			new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		Directory directory = FSDirectory.open(new File(this.indexDir));
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		this.iwriter = new IndexWriter(directory,config);
	}
	public void endWrite()throws IOException {
		if(iwriter !=null )this.iwriter.close();
	}
	public void addDocument(Document doc) throws IOException{
		iwriter.addDocument(doc );
	}
	private final String  indexDir;
	private IndexWriter iwriter;
}

