package tk.luoxing123.entitylink;

import java.io.Serializable;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract  class TextSpan 
	implements Serializable{
    private static final long serialVersionUID = 5101286571585149457L;

	public String surfaceForm;
	public int charStart;
	public int charLength;
    // the text is passed for debugging purposes only
    public static String getPositionHashKey(Constituent c){
        return null;
    }
    public String getPositionHashKey(int start,int len){
        return null;
    }
    public String getPositionHashKey(){
        return null;
    }
	
}
