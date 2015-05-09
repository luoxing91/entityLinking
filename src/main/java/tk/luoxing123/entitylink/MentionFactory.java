package tk.luoxing123.entitylink;

import tk.luoxing123.utils.LuceneDocIterator;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Properties;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;

import tk.luoxing123.corpus.ArticleCollection;
import tk.luoxing123.corpus.Mention;
import tk.luoxing123.entitylink.MentionI;
import tk.luoxing123.utils.IndexWriterHelp;

class IndexMaker {
	public IndexMaker(String str) {
		try {
			this.help = new IndexWriterHelp(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void open() {
		try {
			this.help.startWrite();
		} catch (IOException e) {

		}
	}

	private IndexWriterHelp help;

	public void addMention(MentionI m) throws IOException {
		org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
		document.add(new TextField("name", m.getName(), Field.Store.YES));
		document.add(new LongField("start", m.getStart(), Field.Store.YES));
		document.add(new TextField("ner", m.getNer(), Field.Store.YES));
		document.add(new TextField("fileId", m.getArticleId(), Field.Store.YES));
		help.addDocument(document);
		i++;
	}

	private int i = 0;

	public void close() throws IOException {
		System.out.println("has" + i + "mentions");
		help.endWrite();
	}
}

//
class IndexReaderIterator implements Iterator<tk.luoxing123.corpus.Mention> {
	public IndexReaderIterator(String str) {
		try {

			this.iter = new Iterator<tk.luoxing123.corpus.Mention>() {
				Iterator<Document> index = new LuceneDocIterator(str);

				@Override
				public boolean hasNext() {
					// TODO Auto-generated method stub
					return index.hasNext();
				}

				@Override
				public Mention next() {
					return tk.luoxing123.corpus.Mention
							.ofDocument(index.next());
				}

			};

		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
	}// NER:

	public boolean hasNext() {
		return iter.hasNext();
	}

	public tk.luoxing123.corpus.Mention next() {
		tk.luoxing123.corpus.Mention m = iter.next();
		return m;
	}

	public void remove() {
		iter.remove();
	}

	private Iterator<tk.luoxing123.corpus.Mention> iter;
}

public class MentionFactory {
	public static Iterator<tk.luoxing123.corpus.Mention> makeMentionIterator(
			String index) {
		try {
			return new IndexReaderIterator(index);
		} catch (Exception e) {
			throw new NullPointerException();
		}
	}

	// --------------public static --------------------------------------------
	public static MentionFactory newInstance() {
		return new MentionFactory();
	}

	public MentionFactory() {
		Properties props = new Properties();
		props.put("annotators", "tokenize,ssplit,pos,lemma ner");
		props.put("ner.model",
				"/home/luoxing/models/ner/english.conll.4class.distsim.crf.ser.gz");
		this.pipeline = new StanfordCoreNLP(props);
	}

	// -------------public -----------------------------------------------------
	public Iterator<Mention> toMentionIterator(ArticleCollection arts) {
		return new MentionIterator(this, arts);
	}

	public Iterator<Mention> mentionIterator(String content, String fileId) {
		return toMentionList(content, fileId).iterator();
	}

	public List<Mention> toMentionList(ArticleI art) {
		if (art == null)
			return new ArrayList<Mention>();
		return toMentionList(art.getTextContent(), art);
	}

	// --------------private ---------------------------------------------------
	private static class MentionIterator implements Iterator<Mention> {
		public MentionIterator(MentionFactory f, ArticleCollection arts) {
			this.mentions = new ArrayDeque<Mention>();
			this.articleIt = arts.iterator();
			this.f = f;
		}

		private ArrayDeque<Mention> mentions;
		private Iterator<ArticleI> articleIt;
		private MentionFactory f;
		private int count = 0;

		public boolean hasNext() {
			if (mentions.size() > 0 || articleIt.hasNext()) {
				return true;
			}
			return false;
		}

		public void remove() {
		}/*
		 * public boolean hasNext(){ if(this.mention==null) this.mention =
		 * _next(); return this.mention!=null; }
		 */

		/*
		 * public Mention next(){ if(this.mention==null) this.mention =_next();
		 * return this.mention;
		 * 
		 * }
		 */
		public Mention next() {
			if (this.mentions.isEmpty()) {
				ArticleI art = this.articleIt.next();
				if (art == null)
					return null;
				System.out.println("" + count + "" + art.toString());
				this.mentions = new ArrayDeque<>(this.f.toMentionList(art));
				count++;
				return this.mentions.poll();
			}
			return this.mentions.poll();
		}
	}

	private List<Mention> toMentionList(String str, ArticleI art) {
		Annotation document = new Annotation(str);
		this.pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<Mention> lst = new ArrayList<Mention>();
		for (CoreMap sentence : sentences) {
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			Iterator<CoreLabel> it = tokens.iterator();
			CoreLabel token;
			while (true) {
				if (!it.hasNext())
					break;
				while (true) {
					token = it.next();
					if (!getNer(token).equals("O"))
						break;
					if (!it.hasNext())
						break;
				}
				if (!it.hasNext())
					break;
				String ner = getNer(token);
				Integer start = getStart(token);
				StringBuffer word = new StringBuffer(10);
				word.append(getWord(token));
				while (true) {
					if (!it.hasNext())
						break;
					token = it.next();
					if (getNer(token).equals("O"))
						break;
					if (!getNer(token).equals(ner))
						break;
					word.append(" ");
					word.append(getWord(token));
				}
				lst.add(new Mention(art, word.toString(), ner, start));
			}
		}
		return lst;
	}

	private List<Mention> toMentionList(String str, String artfile) {
		Annotation document = new Annotation(str);
		this.pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<Mention> lst = new ArrayList<Mention>();
		for (CoreMap sentence : sentences) {
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			Iterator<CoreLabel> it = tokens.iterator();
			CoreLabel token;
			while (true) {
				if (!it.hasNext())
					break;
				while (true) {
					token = it.next();
					if (!getNer(token).equals("O"))
						break;
					if (!it.hasNext())
						break;
				}
				if (!it.hasNext())
					break;
				String ner = getNer(token);
				Integer start = getStart(token);
				StringBuffer word = new StringBuffer(10);
				word.append(getWord(token));
				while (true) {
					if (!it.hasNext())
						break;
					token = it.next();
					if (getNer(token).equals("O"))
						break;
					if (!getNer(token).equals(ner))
						break;
					word.append(" ");
					word.append(getWord(token));
				}
				lst.add(new Mention(artfile, word.toString(), ner, start));
			}
		}
		return lst;
	}

	private String getNer(CoreLabel token) {
		return token.get(NamedEntityTagAnnotation.class);
	}

	private Integer getStart(CoreLabel token) {
		return token.get(CharacterOffsetBeginAnnotation.class);
	}

	private String getWord(CoreLabel token) {
		return token.get(TextAnnotation.class);
	}

	// ----------------------------------------------------------------------
	private StanfordCoreNLP pipeline;

}
