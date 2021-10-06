import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Record {
	private RelationInfo relInfo;
	private ArrayList<String> values;

	public Record(RelationInfo relInfo) {
		this.relInfo = relInfo;
		values = new ArrayList<String>();
	}

	void writeToBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		for (int i = 0; i < values.size(); i++) {
			if(relInfo.getListe().get(i).getType_col().equals("int")) {
				buff.putInt(Integer.parseInt(values.get(i)));
			}else if(relInfo.getListe().get(i).getType_col().equals("float")) {
				buff.putFloat(Float.parseFloat(values.get(i)));
			}else {
				for (int k = 0; k < values.get(i).length(); k++) {
					buff.putChar(values.get(i).charAt(k));
				}
			}
		}
	}
	
	void readFromBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		int dernier_element_string;
		for (int i = 0; i < relInfo.getNb_col(); i++) {
			if(relInfo.getListe().get(i).getType_col().equals("int")) {
				System.out.print(buff.getInt()+" ");
			}else if(relInfo.getListe().get(i).getType_col().equals("float")) {
				System.out.print(buff.getFloat()+" ");
			}else {
				dernier_element_string=relInfo.getListe().get(i).getType_col().length()-1;
				for (int j = 0; j < (int)relInfo.getListe().get(i).getType_col().charAt(dernier_element_string); j++) {
					System.out.println(buff.getChar());
				}
			}
		}
	}
	
	
}
