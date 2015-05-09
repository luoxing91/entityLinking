package tk.luoxing123.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import org.la4j.vector.Vector;
import org.la4j.factory.CCSFactory;
public class MapUtils {
    static public < T  extends Number> Double
        dotProduct(Map<String,T> x ,Map<String,T> y){
        return x.entrySet().stream()
            .filter(word -> y.containsKey(word.getKey()))
            .mapToDouble(word ->
                         x.get(word).doubleValue()*y.get(word).doubleValue())
            .sum();
    }
	
	static public <T extends Number> Map<String,Double>
							 normalize(Map<String,T> x){
		double sum=0;
		for(String str:x.keySet()){
			sum += x.get(str).doubleValue();
		}
		Map<String,Double> y = new HashMap<>();
		for(String str: x.keySet()){
			y.put(str,x.get(str).doubleValue()/sum);
		}
		return y;
	}
    static public < T  extends Number> Double
        dotProductMin(Map<String,T>x,Map<String,T> y){
        if(x.size()>y.size()){
            return dotProduct(y,x);
        }
        return dotProduct(x,y);
    }
	static Vector MakeRealVector(Map<String,Integer> map ,Set<String> terms){
		org.la4j.factory.Factory factory = new CCSFactory();
		Vector v = factory.createVector(terms.size());
		int i=0;
		int value;
		for(String term:terms){
			value = 0;
			if(map.containsKey(term)){
				value = map.get(term);
			}
			v.set(i,value);
			i++;
		}
		return v;
	}
	
}
