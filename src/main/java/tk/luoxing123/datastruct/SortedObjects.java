package tk.luoxing123.datastruct;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.IdentityHashMap;
import java.util.Collections;
import tk.luoxing123.utils.Comparators;

public class SortedObjects<T> {
    public SortedObjects(int capacity) {
        maxSize = capacity;
    }
     int maxSize;
    public SortedObjects(Map<T,? extends Number> scores){
        for(Entry<T,?extends Number> e : scores.entrySet()){
            add(e.getKey(),e.getValue().doubleValue());
        }
    }
    private List<T> tops = new ArrayList<T>();
    private List<Double> topScores = new ArrayList<Double>();
    public  void add(T o, double score){
        tops.add(o);
        topScores.add(score);
        if(tops.size() > maxSize){
            int min =0;
            for(int i=1;i<topScores.size();i++){
                if(topScores.get(min) > topScores.get(i))
                    min = i;
            }
            topScores.remove(min);
            tops.remove(min);
            
        }
    }
    public void sort(){
        IdentityHashMap<T ,Double> value  = new IdentityHashMap<>();
        for(int i=0;i<tops.size();i++){
            value.put(tops.get(i),topScores.get(i));
        }
        Collections.sort(tops,new Comparators.MapValueComparator(value));
        topScores.clear();
        for(T y : tops){
            topScores.add(value.get(y));
        }
    }
    public static class SortedWords extends SortedObjects<String>{
        public SortedWords(int capacity){
            super(capacity);
        }
        public SortedWords(Map<String,? extends Number> scores){
            super(scores);
        }
    }
}
