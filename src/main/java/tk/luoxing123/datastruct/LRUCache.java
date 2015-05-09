package tk.luoxing123.datastruct;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LRUCache<K, V> implements Map<K, V> {
	private LoadingCache<K, V> cache;

	public LRUCache(int max) {
		cache = CacheBuilder.newBuilder().maximumSize(max)
				.build(new CacheLoader<K, V>() {
					@Override
					public V load(K k) throws Exception {
						return loadValue(k);
					}
				});
	}

	protected V loadValue(K k) throws Exception {
		return null;
	}

	@Override
	public void clear() {
		// TODO: Stub
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return cache.asMap().containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return cache.asMap().entrySet();
	}

	@Override
	public boolean equals(java.lang.Object arg0) {
		// TODO: Stub
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(java.lang.Object k) {
		try {
			return cache.get((K) k);
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public int hashCode() {
		// TODO: Stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return cache.size() == 0;
	}

	@Override
	public java.util.Set<K> keySet() {
		return cache.asMap().keySet();
	}

	@Override
	public V put(K key, V value) {
		cache.put(key, value);
		return value;
	}

	@Override
	public void putAll(java.util.Map<? extends K, ? extends V> m) {
		cache.putAll(m);
	}

	@Override
	public V remove(Object key) {
		return cache.asMap().remove(key);
	}

	@Override
	public int size() {
		return cache.asMap().size();
	}

	@Override
	public Collection<V> values() {
		return cache.asMap().values();
	}
}
