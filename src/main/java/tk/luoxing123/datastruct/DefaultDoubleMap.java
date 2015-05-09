package tk.luoxing123.datastruct;

import java.util.Map;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.BiFunction;

public class  DefaultDoubleMap<K> 
	implements Map<K,Double>{
	public DefaultDoubleMap(){
		this.map = new TObjectDoubleHashMap<K>();
	}
	private TObjectDoubleHashMap<K> map;
	public int size() {	return map.size();}
	public void clear() { map.clear();	}
	public int hashCode() {	return map.hashCode();	}
	public Set<K> keySet() { return map.keySet();	}
	public boolean isEmpty() { return map.isEmpty();	}

	@SuppressWarnings("unchecked")
	public Double get(Object arg0) {
		K key = (K)arg0;
		return map.get(key);	
	}
	public Double remove(Object arg0) {return map.remove(arg0);	}
	public Double put(K arg0, Double arg1) {return map.put(arg0,arg1);	}
	public boolean containsKey(Object arg0) {return map.containsKey(arg0);}
	public Double replace(K arg0, Double arg1) {return map.put(arg0,arg1);	}
	public boolean containsValue(Object arg0) {
		return map.containsValue((double)arg0);
	}


	@Override
		public Double compute(K key, BiFunction<? super K, ? super Double, ? extends Double> biFunction) {
		double res = biFunction.apply(key,map.get(key));
		return map.put(key,res);
	}
	@Override
		public Double computeIfAbsent(K arg0, Function<? super K, ? extends Double> arg1) {
		// TODO: Stub
		return null;
	}

	@Override
		public Double computeIfPresent(K arg0, BiFunction<? super K, ? super Double, ? extends Double> arg1) {
		// TODO: Stub
		return null;
	}
	@Override
		public Set<Map.Entry<K, Double>> entrySet() {
		// TODO: Stub
		return null;
	}

	////////////////////////////

	
	static class Entry<K> implements Map.Entry<K,Double>{
		public Entry(K key,Double d){
			this.key =key;
			this.d = d;
		}
		private K key;
		private Double d;
		public K getKey(){
			return key;
		}
		public Double getValue(){
			return d;
		}
		public Double setValue(Double d ){
			Double old =this.d;
			this.d=d;
			return old;
		}
	}

	@Override
		public boolean equals(Object arg0) {
		if(arg0 instanceof DefaultDoubleMap){
			return map.equals(map);
		}else{
			return false;
		}

	}

	@Override
		public void forEach(BiConsumer<? super K, ? super Double> arg0) {
		// TODO: Stub
	}

	

	@Override
		public Double getOrDefault(Object arg0, Double arg1) {
		Double value = map.get(arg0);
		if(value ==null )
			return arg1;
		return value;
	}
	
	
	@Override
		public Double merge(K arg0, Double arg1, BiFunction<? super Double, ? super Double, ? extends Double> arg2) {
		// TODO: Stub
		return null;
	}


	@Override
		public void putAll(Map<? extends K, ? extends Double> arg0) {
			// TODO: Stub
	}
	@Override
		public Double putIfAbsent(K arg0, Double arg1) {
		return map.putIfAbsent(arg0,arg1);
	}


	@Override
		public boolean remove(Object arg0, Object arg1) {
		// TODO: Stub
		return false;
	}

	@Override

		public boolean replace(K arg0, Double arg1, Double arg2) {
		// TODO: Stub
		return false;
	}

	@Override
		public void replaceAll(BiFunction<? super K, ? super Double, ? extends Double> arg0) {
		// TODO: Stub
	}


	@Override
		public Collection<Double> values() {
		// TODO: Stub
		return null;
	}
}
