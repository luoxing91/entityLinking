package tk.luoxing123.utils;

import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.DirectoryReader;

public class LuceneDocIterator implements Iterator<Document> {
	public LuceneDocIterator(String index) throws IOException {
		Directory dir = FSDirectory.open(new File(index));
		this.reader = DirectoryReader.open(dir);
		this.pointer = 0;
		this.max = reader.numDocs();
	}

	public LuceneDocIterator(IndexReader reader) {
		this.reader = reader;
		this.pointer = 0;
		this.max = reader.numDocs();
	}

	@Override
	public void remove() {
	}

	public boolean hasNext() {
		return pointer < max;
	}

	@Override
	public Document next() {
		Document doc = null;
		try {
			doc = reader.document(pointer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pointer++;
		return doc;
	}

	private IndexReader reader;
	private int pointer;
	private int max;

}
