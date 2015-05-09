package tk.luoxing123.utils;

public class Constant {
	public final static String queryXmlFile = "/home/luoxing/windows/test/training_queries.xml";
	public final static String entityFile = "/home/luoxing/windows/test/training_queries.xml";

	public static class Counter {
		Integer i = 0;

		public String toString() {
			i++;
			return i.toString();
		}
	}

}
