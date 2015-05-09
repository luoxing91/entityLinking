package tk.luoxing123.datastruct;

import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.io.ByteBufferInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.io.FileInputStream;
import java.nio.IntBuffer;
import java.util.concurrent.ExecutionException;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

/**
 * Simple fast implementation of a disk backed array with byte[] data type.
 * Index file is an array of n integers that represents data length. When
 * reading index, we construct the offset array of n+1 elements. Assumption:
 * byte array object size smaller than 2GB
 * 
 * @author xingfe123@gmail.com
 *
 */
public class DiskArray implements Iterable<byte[]> {

	private static final String INDEX_FILE = "idx";
	private static final String ARRAY_FILE = "arr";

	// ///////////////////////////////////////////////////////////
	public static enum Mode {
		MMAP, DEFAULT
	}

	@Override
	public Iterator<byte[]> iterator() {
		try {
			return new ByteArrayIterator(directory, starts);
		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	// ////////**
	/*
	 * Get the byte representation of the ith element
	 */
	public byte[] get(int idx) throws IOException, InterruptedException,
			ExecutionException {
		int nextStart = starts.getQuick(idx + 1);
		int start = starts.getQuick(idx);
		int len = (nextStart - start);
		// //////////
		byte[] ret = new byte[len];
		int reads = 0;
		if (mbuf == null) {
			ByteBuffer buffer = ByteBuffer.wrap(ret);
			while (reads < len) {
				Future<Integer> bytesRead = fc.read(buffer, start);
				reads += bytesRead.get();
			}
		} else {
			mbuf.position(start);
			mbuf.get(ret, (int) start, len);
		}
		return ret;
	}

	public static class ByteArrayIterator implements Iterator<byte[]> {
		private int pointer = 0;
		private TIntArrayList offsets;
		private DataInputStream in;
		private RandomAccessFile raf;

		ByteArrayIterator(File dir, TIntArrayList offsets) throws IOException {
			raf = new RandomAccessFile(new File(dir, ARRAY_FILE), "r");
			in = new DataInputStream(ByteBufferInputStream.map(
					raf.getChannel(), MapMode.READ_ONLY));
			this.offsets = offsets;
		}

		@Override
		public boolean hasNext() {
			return pointer < offsets.size() - 2;
		}

		protected void finalize() {
			if (raf != null)
				try {
					raf.close();
				} catch (IOException e) {
					System.out.println("Error " + e.getMessage());
					e.printStackTrace();
				}

		}

		@Override
		public byte[] next() {
			int len = offsets.get(pointer + 1) - offsets.get(pointer);
			byte[] ret = new byte[len];
			try {
				in.readFully(ret);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			pointer++;
			return ret;
		}

		@Override
		public void remove() {
		}
	}

	public DiskArray(final File dir, Mode mode) throws IOException {
		this(dir);
		switch (mode) {
		case DEFAULT:
			fc = AsynchronousFileChannel.open(
					Paths.get(dir.getPath(), ARRAY_FILE),
					StandardOpenOption.READ);
			break;
		case MMAP:
			RandomAccessFile raf = new RandomAccessFile(new File(dir,
					ARRAY_FILE), "r");
			FileChannel fChannel = raf.getChannel();
			long size = fChannel.size();
			if (size > Integer.MAX_VALUE)
				throw new UnsupportedAddressTypeException();
			mbuf = fChannel.map(MapMode.READ_ONLY, 0, size);
			break;

		}
	}

	public void loadToMemory() {
		if (mbuf != null && !mbuf.isLoaded())
			mbuf.load();
	}

	// //////////////////////////////////////////////////////////
	private DiskArray(final File dir) throws IOException {
		File index = new File(dir, INDEX_FILE);
		FileInputStream in = new FileInputStream(index);
		FileChannel fc = in.getChannel();
		IntBuffer ib = fc.map(MapMode.READ_ONLY, 0, fc.size()).asIntBuffer();
		int lastStart = 0;
		while (ib.hasRemaining()) {
			int length = ib.get();
			lastStart += length;
			starts.add(lastStart);
		}
		starts.trimToSize();
		in.close();
		directory = dir;
	}

	private AsynchronousFileChannel fc;
	private MappedByteBuffer mbuf;
	private File directory;
	private TIntArrayList starts = new TIntArrayList();

}
