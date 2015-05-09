package tk.luoxing123.utils;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

public class UnigramStatistics {
    static public Map<String,Integer> toMap(String context){
        return toMap(StringsUtils.toList(context));
    }
    static public Multiset<String> toBag(List<String> lst){
        return HashMultiset.create(lst);
    }
    static public Map<String,Integer> toMap(List<String> lst){
        return lst.stream()
            .collect(HashMap<String,Integer>::new,
                     (map,str) ->{
                         if(!map.containsKey(str)){
                             map.put(str,1);
                         }else{
                             map.put(str,map.get(str)+1);
                         }
                     },
                     HashMap<String,Integer>::putAll);
    }
    
                                           
    public static void main(String[] args) {
        String line = "Returns a Collector that accumulates elements into a Map whose keys and values are the result of applying the provided mapping functions to the input elements";
        toMap(line).entrySet().stream().forEach(System.out::println);
    }
    

    public Set<String> getWords(){
        return wordCounts.keySet();
    }
    public int  getWordcount(String str){
        return wordCounts.get(str);
    }

    private  Map<String,Integer> wordCounts;
}
