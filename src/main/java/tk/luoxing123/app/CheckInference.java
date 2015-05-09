package tk.luoxing123.app;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import tk.luoxing123.utils.LuceneDocIterator;

public class CheckInference{
	public static void main(String[] args) {
		try{
			File indexdir = new File("/home/luoxing/mentions");
		
			Directory directory = FSDirectory.open(indexdir);
			IndexReader index = DirectoryReader.open(directory);
			LuceneDocIterator it
				= new LuceneDocIterator(index);
			int time =0;
			while(it.hasNext()){
				it.next();
				time++;
				System.out.println(""+time);
			}
			System.out.println(""+time);
		}catch(IOException e){
			e.printStackTrace();
		}

	}

}
