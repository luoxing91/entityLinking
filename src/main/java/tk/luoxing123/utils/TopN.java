package tk.luoxing123.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;

public class TopN<T,V extends Number> {
    public static<T,V extends Number> TopN<T,V> of(Map<T,V> map,int k){
        TopN<T,V> top = new TopN<>(k);
        top.putAll(map);
        return top;
    }
    public TopN(int k){
        this.size=k;
    }
    public Map<T,V>  toMap(){
        return 
            lst.stream()
            .collect(Collectors.toMap(i -> i.key, i-> i.value));
    }
    public List<T> toList(){
        return lst.stream().map(item -> item.key)
            .collect(Collectors.toList());
    }
    public Set<T> toSet(){
        return lst.stream()
            .map(item -> item.key)
            .collect(Collectors.toSet());
    }
    
    public void putAll(Map< ? extends T,V> map){
        map.keySet().stream().forEach(k -> put(k,map.get(k)));
    }
    public void put(T key,V value){
        if(cur<size){
            cur++;
            lst.add(new Item(key,value));
        }else{
            replaceMin(key,value);
        }
    }
    private void replaceMin(T key,V value){
        int min=0;
        for(int i=1;i<size;i++){
            if(lst.get(min).toDouble()>lst.get(i).toDouble()){
                min=i;
            }
        }
    }
    public class Item{
        public Item(T key ,V value){
            this.key=key;
            this.value=value;
        }
        T key;

        V value;
        public Double toDouble(){
            return value.doubleValue();
        }
    }
    //private List<Item> lst = new ArrayList<>();
    private List<Item> lst = new ArrayList<Item>();
    private int size;
    private int cur=0;

}
