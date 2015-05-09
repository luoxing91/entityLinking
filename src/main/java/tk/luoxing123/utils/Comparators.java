package tk.luoxing123.utils;

import java.util.Comparator;
import java.util.Map;

import tk.luoxing123.corpus.Mention;

public class Comparators {
   
    static public
        <K extends Object,V extends Number> 
                                    MapValueComparator
                                    mapValue(Map<K,V> map){
        return new MapValueComparator(map,false);
    }
    static public MentionComparator mention(){
        return new MentionComparator();
    }
    public static class MapValueComparator 
        implements Comparator<Object>{
        
        public MapValueComparator(Map<? extends Object,? extends Number>
                                  map, boolean reverse){
            this.map =map;
            this.reverse = reverse;
        }
        public MapValueComparator(Map<? extends Object,? extends Number>
                                  map){
            this(map,false);
        }

        private boolean reverse = false;
        private final Map<? extends Object,? extends Number> map;
        @Override
            public int compare(Object arg0,Object arg1) {
            int value = Double.compare(map.get(arg0).doubleValue(),
                                       map.get(arg1).doubleValue());
            if(reverse) value = -value;
            return value;
        }

        
    }

    public static  class MentionComparator
        implements Comparator<Mention>{
        @Override
        public int compare(Mention first,Mention second){
            return first.toList().size() - second.toList().size();
        }
    }
}
