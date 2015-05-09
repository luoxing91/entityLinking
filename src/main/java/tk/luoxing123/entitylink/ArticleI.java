package tk.luoxing123.entitylink;

import java.util.List;
import java.util.Map;

public interface ArticleI{
	String getArticleId();
	String getTextContent();
	List<String> getTitleList();
	Map<String,Integer> toWordFrequencyMap();

}
