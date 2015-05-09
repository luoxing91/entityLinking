package tk.luoxing123.nlpcc;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;



public class EntityExtractor {

	public static void main(String[] args){
		EntityExtractor e = new EntityExtractor();
		List<String> es = e.stem("我爱北京天安门。吴奇隆");
		for(String i :es){
			System.out.println(i);
		}
	}
	public EntityExtractor(){
	
	}
	public List<String> stem(String text){
		List<String> result =  new ArrayList<String>();
		
		return result;
	}
	public List<EntityItem> parse(String text) {
		// text = "我爱北京天安门。吴奇隆";
		try {
			text = URLEncoder.encode(text, "utf-8");
			URL url = new URL(
					"http://ltpapi.voicecloud.cn/analysis/?api_key=l3W573H83eAOIliTrkyxUcakFWtOQPkYCGpNQFKL&text="
							+ text + "&pattern=ner&format=plain&only_ner=true");
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader innet = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line;
			List<EntityItem> es = new ArrayList<EntityItem>();
			while ((line = innet.readLine()) != null) {
				String[] t = line.split(" ");
				es.add(new EntityItem(t[0],t[1]));
			}
			innet.close();
			return es;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
