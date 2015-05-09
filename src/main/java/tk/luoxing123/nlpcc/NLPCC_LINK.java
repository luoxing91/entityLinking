package tk.luoxing123.nlpcc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class NLPCC_LINK {

    public static void main(String[] args) throws IOException{
    	String file = "D:/nlpcc_annotation_100.tag";
    	BufferedReader reader=new BufferedReader( new InputStreamReader(new FileInputStream(file),"utf-8"));
    	String line; // "我是中国人"
    	EntityExtractor extract = new EntityExtractor();
    	List<Term> parse = ToAnalysis.parse("让战士们过一个欢乐祥和的新春佳节。");
        System.out.println(parse);
    			
    	}
    }
    
