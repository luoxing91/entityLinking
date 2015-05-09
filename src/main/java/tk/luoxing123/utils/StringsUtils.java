package tk.luoxing123.utils;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

public class StringsUtils {
	public static boolean toBoolean(String str){
		return str.equalsIgnoreCase("Y");
	}
    static public List<String> toList(String context){
        //        return Stream.of(context.split("\\s+"))
        //  .collect(Collectors.toList());
        return toList(context,"\\s+");
    }
    static public List<String> toList(String context,String str){
        return Stream.of(context.split(str))
            .collect(Collectors.toList());
    }
	static public Map<String,Integer> toFrequencyMap(List<String> lst){
		return new WordCounter(lst).toMap();
	}
	static public Map<String,Integer> toMap(List<String> lst){
		return new WordCounter(lst).toMap();
	}
    public static boolean isCapital(char c){
        return Character.isUpperCase(c);
    }
}
class WordCounter{
	public WordCounter(List<String> lst){
		this.wordFrequency = new HashMap<String,Integer>();
		this.ignoreWord = new HashSet<String>();
		countWord(lst);
	}
	Map<String,Integer> wordFrequency;
	Set<String> ignoreWord;
	public Map<String,Integer> toMap(){
		return this.wordFrequency;
	}
	public void ignore(Scanner ignore){
		ignore.useDelimiter("[^A-za-z]+");
		while(ignore.hasNext()){
			ignoreWord.add(ignore.next());
		}
	}
	public void countWord(List<String> lst){
		for(String word: lst){
			if(!ignoreWord.contains(word)){
				Integer count = wordFrequency.get(word);
				if(count==null){
					wordFrequency.put(word, 1);
				}else{
					wordFrequency.put(word, count+1);
				}
			}
		}
	}
}
