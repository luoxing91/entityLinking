package tk.luoxing123.utils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import java.io.File;

import java.io.IOException;

public class LuceneHelp{
	static public IndexWriter makeWriter(String directory) throws IOException{
		IndexWriterConfig 
			iwc= new IndexWriterConfig(Version.LUCENE_4_9,
									new StandardAnalyzer(Version.LUCENE_4_9));
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		return new IndexWriter(FSDirectory.open(new File(directory)),
							   iwc);
	}
}
