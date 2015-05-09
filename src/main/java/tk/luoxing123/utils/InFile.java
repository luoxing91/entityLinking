package tk.luoxing123.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.io.IOException;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;

public class InFile {
	public static boolean convertToLowerCaseByDefault = false;
	public static boolean normalize = false;
	public static boolean pruneStopSymbols = false;
	public BufferedReader in = null;
	public static String stopSymbols = "@";

	public InFile(String filename) {
		try {
			in = new BufferedReader(new FileReader(filename));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public String readLine() {
		try {
			String s = in.readLine();
			if (s == null)
				return null;
			if (convertToLowerCaseByDefault)
				return s.toLowerCase().trim();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	public List<String> readLineTokens() {
		return tokenize(readLine());
	}

	public Iterable<List<String>> readLineIterator() {
		return new Iterable<List<String>>() {
			public Iterator<List<String>> iterator() {
				return new MyIterator();
			}
		};
	}

	public class MyIterator implements java.util.Iterator<List<String>> {
		public MyIterator() {
			_next();
		}

		private void _next() {
			it = readLineTokens();
		}

		private List<String> it = null;

		public List<String> next() {
			_next();
			return it;
		}

		public boolean hasNext() {
			return it != null;
		}

		public void remove() {

		}

	}

	public static List<String> tokenize(String s) {
		if (s == null)
			return null;
		List<String> res = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(s, " \n\t\r");
		while (st.hasMoreTokens())
			res.add(st.nextToken());
		return res;
	}

	public static List<String> tokenize(String s, String delims) {
		if (s == null)
			return null;
		List<String> res = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(s, delims);
		while (st.hasMoreTokens())
			res.add(st.nextToken());
		return res;
	}

	public static List<String> aggressiveTokenize(String s) {
		if (s == null)
			return null;
		return Arrays.asList(StringUtils.split(s,
				" \n\t,./<>?;':\"[]{}\\|`~!@#$%^&*()_+-="));
	}

	public void close() {
		try {
			this.in.close();
		} catch (Exception E) {
		}
	}

	public static String readFileText(String file) throws IOException {
		File f = new File(file);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
		System.out.println("character encoding = " + isr.getEncoding());
		int c;
		StringBuffer res = new StringBuffer();
		while ((c = isr.read()) != -1) {
			res.append((char) c);
		}
		isr.close();
		return res.toString().replace('�', '\'');
	}

	public static String readFileText(String file, String encoding)
			throws IOException {
		File f = new File(file);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f),
				encoding);
		System.out.println("character encoding = " + isr.getEncoding());
		int c;
		StringBuffer res = new StringBuffer();
		while (true) {
			c = isr.read();
			if (c == -1)
				break;
			res.append((char) c);
		}
		isr.close();
		return res.toString();
	}

	public static String vec2str(List<String> surfaceFormsAttribs) {
		return StringUtils.join(surfaceFormsAttribs, ',');
	}
}
