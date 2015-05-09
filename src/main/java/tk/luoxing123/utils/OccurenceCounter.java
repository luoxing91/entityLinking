package tk.luoxing123.utils;

import java.util.Map;
import tk.luoxing123.utils.StringMap;
import java.util.Iterator;
import java.util.List;

public class OccurenceCounter 
    implements java.lang.Iterable<String>{
    @Override
        public Iterator<String> iterator(){
        return counts.keySet().iterator();
    }
    public OccurenceCounter(Iterable<String> iter) {
        for(String str: iter){
        	addToken(str);
        }
    }
  
    public int getCount(String word){
        if(counts.containsKey(word))
            return counts.get(word).intValue();
        return 0;
    }
    public void addToken(String s,int weight ){
        int value =weight;
        if(counts.containsKey(s)){
            value += counts.get(s).intValue();
        }
        counts.put(s,value);
    }

    public void addToken(String s){
        addToken(s,1) ;
    }
    private Map<String,Integer> counts  = new StringMap<Integer>();
}
