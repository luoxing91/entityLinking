package tk.luoxing123.utils;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.bind.JAXBContext;

/*Accepts JAXB compatible model classes and performs the IO/parsing.
 * All operations are thread-safe. You can choose either use the
 * static method to load or extend this class to utilize the protected
 * parsing methods.
 */
public abstract class XmlModel {
	protected abstract Class<?> modelClass();

	static ConcurrentMap<Class<?>, JAXBContext> contextCache = new ConcurrentHashMap<>();
	private static ThreadLocal<Map<Class<?>, Unmarshaller>> unmarshallers = new ThreadLocal<Map<Class<?>, Unmarshaller>>() {
		@Override
		public Map<Class<?>, Unmarshaller> initialValue() {
			return new HashMap<Class<?>, Unmarshaller>();
		}
	};

	private static JAXBContext getContext(Class<?> clazz) throws JAXBException {
		if (!contextCache.containsKey(clazz)) {
			contextCache.put(clazz, JAXBContext.newInstance(clazz));
		}
		return contextCache.get(clazz);
	}

	private static Unmarshaller getUnmarshaller(Class<?> clazz)
			throws JAXBException {
		Map<Class<?>, Unmarshaller> localPool = unmarshallers.get();
		if (!localPool.containsKey(clazz)) {
			localPool.put(clazz, getContext(clazz).createUnmarshaller());
		}
		return localPool.get(clazz);
	}

	// ////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static <T> T load(Class<T> clazz, String filename) {
		try {

			return (T) getUnmarshaller(clazz).unmarshal(
					new InputStreamReader(new FileInputStream(filename),
							"UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
