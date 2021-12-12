import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Classe qui correspond à un enregistrement/tuple dans une relation
 * @author Hu Tony
 *
 */
public class Record {
	
	private RelationInfo relInfo;
	private ArrayList<String> values;

	/**
	 * Constructeur qui construit un record avec la relationInfo en argument
	 * @param relInfo - Une RelationInfo
	 */
	public Record(RelationInfo relInfo) {
		this.relInfo = relInfo;
		values = new ArrayList<String>();
	}
	
	/**
	 * Constructeur qui construit un record avec la relationInfo et des valeurs en argument
	 * @param relInfo - Une RelationInfo
	 * @param al - une ArrayList<String> qui contient les valeurs
	 */
	public Record(RelationInfo relInfo, ArrayList<String> al) {
		this.relInfo = relInfo;
		values = al;
	}

	/**
	 * Ecrit à partir de la position donnée dans le buffer entrée en argument les valeurs dans le Record
	 * @param buff - un ByteBuffer, le buffer dans lequel on souhaite écrire
	 * @param position - int, la position à partir duquel on veut écrire
	 */
	public void writeToBuffer(ByteBuffer buff, int position) {
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

	/**
	 * Récupère les valeurs contenues dans le buffer à partir de la position donnée
	 * @param buff - un ByteBuffer, le buffer dans lequel on souhaite lire
	 * @param position - int, la position à partir duquel on souhaite lire
	 */
	public void readFromBuffer(ByteBuffer buff, int position) {
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

	/**
	 * Retourne la RelationInfo du record
	 * @return RelationInfo
	 */
	public RelationInfo getRelationInfo() {
		return relInfo;
	}
	
	/**
	 * Retourne la liste des valeurs du record
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getValues(){
		return values;
	}
	
	/**
	 * Modifie la liste des valeurs avec celle donnée en argument
	 * @param al - un ArrayList<String>
	 */
	public void setValues(ArrayList<String> al) {
		values = al;
		return;
	}
	
	/**
	 * Modifie la valeur du record pour le nom de la colonne donnée
	 * @param nomCol - String, le nom de la colonne
	 * @param value - String, la valeur pour la colonne
	 */
	public void setValueFor(String nomCol, String value) {
		for(int i = 0; i < relInfo.getListe().size();i++) {
			if(nomCol.equals(relInfo.getListe().get(i).getNom_col())) {
				values.set(i, value);
			}
		}
		return;
	}
}