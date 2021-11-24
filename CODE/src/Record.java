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
			if (relInfo.getListe().get(i).getType_col().equals("int")) {
				buff.putInt(Integer.parseInt(values.get(i)));
			} else if (relInfo.getListe().get(i).getType_col().equals("float")) {
				buff.putFloat(Float.parseFloat(values.get(i)));
			} else if (relInfo.getListe().get(i).getType_col().substring(0,6).equals("string")) {
				for (int k = 0; k < values.get(i).length(); k++) {
					buff.putChar(values.get(i).charAt(k));
				}
			}
		}
	}

	void readFromBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		int string_length;
		for (int i = 0; i < relInfo.getNb_col(); i++) {
			if (relInfo.getListe().get(i).getType_col().equals("int")) {
				values.add(Integer.toString(buff.getInt()));
			} else if (relInfo.getListe().get(i).getType_col().equals("float")) {
				values.add(Float.toString(buff.getFloat()));
			} else if (relInfo.getListe().get(i).getType_col().substring(0,6).equals("string")){
				string_length = Integer.parseInt(relInfo.getListe().get(i).getType_col().substring(6));
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < string_length; j++) {
					sb.append(buff.getChar());
				}
				values.add(sb.toString());
			}
		}
	}

	public RelationInfo getRelationInfo() {
		return relInfo;
	}
	
}
