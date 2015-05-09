package tk.luoxing123.nlpcc;

public class EntityItem {

	private String text;
	private String type;

	public EntityItem(String str, String type) {
		this.text = str;
		this.type = type;
	}
	public String getText() {
		return this.text;
	}
	public String getType() {
		return this.type;
	}

}
