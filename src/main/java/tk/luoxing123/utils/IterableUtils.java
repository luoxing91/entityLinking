package tk.luoxing123.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class IterableUtils {
	public static<E> Iterable<E> toIterable(Iterator<E> iter){
		return () -> iter;
		
	}
    public static List<FileInputStream> folderToFileList(File folder){
        return toList(toIterable(folderToFileIterator(folder)));
    }
	public static Iterator<FileInputStream> folderToFileIterator(File folder) {
		boolean hasfolder = folder.exists();
		if (!hasfolder) {
			return new Iterator<FileInputStream>() {

				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public FileInputStream next() {
					return null;
				}

			};
		} else {						
			return new Iterator<FileInputStream>() {
				private File[] listOfFiles = folder.listFiles();
				private int now;
				int total = listOfFiles.length;

				@Override
				public boolean hasNext() {
					return now < total;
				}

				@Override
				public FileInputStream next() {
					try {
						return new FileInputStream(listOfFiles[now++]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					return null;
	
				}
			};

		}
	}
	public static <E> List<E> toList(Iterable<E> iterable) {
		if (iterable instanceof List) {
			return (List<E>) iterable;
		}
		ArrayList<E> list = new ArrayList<E>();
		if (iterable != null) {
			for (E e : iterable) {
				list.add(e);
			}
		}
		return list;
	}

	public static <T> List<T> toList(Iterator<T> luceneDocIterator, int k) {
		ArrayList<T> list = new ArrayList<T>();
		if (luceneDocIterator == null)
			return list;
		int i = 0;
		Iterable<T> able = () -> luceneDocIterator;
		for (T t : able) {
			if (i >= k)
				break;
			list.add(t);
			i++;
		}
		return list;
	}
	public static <T> List<T> toList(Iterable<T> luceneDocIterator, int k) {
		ArrayList<T> list = new ArrayList<T>();
		if (luceneDocIterator == null)
			return list;
		int i = 0;
		for (T t : luceneDocIterator) {
			if (i >= k)
				break;
			list.add(t);
			i++;
		}
		return list;
	}

	static public Iterable<Node> make(NamedNodeMap nodes) {
		return new NamedNodeMapIterable(nodes);
	}

	static public Iterable<Node> make(NodeList nodes) {
		return new NodeListIterable(nodes);
	}

	static public <E> Iterable<E> make(final Iterator<E> iterator) {
		if (iterator == null) {
			throw new NullPointerException();
		}
		return new Iterable<E>() {
			public Iterator<E> iterator() {
				return iterator;
			}
		};
	}
}

class NamedNodeMapIterable implements java.lang.Iterable<Node> {
	private static NamedNodeMap nodes;

	public NamedNodeMapIterable(final NamedNodeMap nodes) {
		NamedNodeMapIterable.nodes = nodes;
	}

	public Iterator<Node> iterator() {
		return new Iterator<Node>() {
			public boolean hasNext() {
				return i < nodes.getLength();
			}

			int i = 0;

			public Node next() {
				Node node = nodes.item(i);
				i++;
				return node;
			}

			public void remove() {
			}
		};
	}
}

class NodeListIterable implements java.lang.Iterable<Node> {
	public static NodeListIterable makeInstance(NodeList nodes) {
		return new NodeListIterable(nodes);
	}

	public Iterator<Node> iterator() {
		return new Iterator<Node>() {
			public boolean hasNext() {
				return i < nodes.getLength();
			}

			public Node next() {
				Node node = nodes.item(i);
				i++;
				return node;

			}
			public void remove() {
			}
		};
	}

	public NodeListIterable(NodeList nodes) {
		this.nodes = nodes;
	}

	int i = 0;
	private final NodeList nodes;
}
