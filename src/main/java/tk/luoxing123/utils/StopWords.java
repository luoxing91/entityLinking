package tk.luoxing123.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class StopWords{
    private class NullStopWord extends StopWords{
    }
    private static NullStopWord NULL;
    private Set<String> stopWords = new HashSet<String>();
    public static NullStopWord makeNULLObject(){
		return NULL;
    }
    private StopWords(){}
    public StopWords(String filename) {
		InFile in = new InFile(filename);
        List<String> words = in.readLineTokens();
        while (words != null) {
			for (String word : words) {
                stopWords.add(word.toLowerCase());
			}
            words = in.readLineTokens();
        }
    }
    
    public boolean isStopWord(String s) {
        return stopWords.contains(s);
    }
    
    public List<String> filterStopWords(Iterable<String> words) {
        if (words == null)
            return null;
        List<String> res = new ArrayList<String>();
        for (String word : words)
            if (!isStopWord(word.toLowerCase()))
                res.add(word);
        return res;
    }
}
