import java.io.Serializable;
import java.util.ArrayList;

public class RelationInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2196028207559318797L;
	private String nom;
	private int nb_col;
	private ArrayList<ColInfo> liste;
	private PageID headerPageId;
	private int recordSize;
	private int slotCount;

	public RelationInfo(String nom, int nb_col, ArrayList<ColInfo> liste, PageID headerPageId) {
		this.nom = nom;
		this.nb_col = nb_col;
		this.headerPageId = headerPageId;
		this.liste = liste;
		recordSize = 0;
		for (ColInfo icol : liste) {
			if (icol.getType_col().equals("int") || icol.getType_col().equals("float")) {
				recordSize += 4;
			}
			else {
				recordSize += 2*Integer.parseInt(icol.getType_col().substring(6));
			}
		}
		// -16 Pour la taille des page_id, 2 int (2*4) pour chaque page_id, +1 car chaque record a un octet qui donne son statut
		slotCount = (DBParams.PageSize-16)/(recordSize+1);
	}

	public String getNom() {
		return nom;
	}

	public void addCol(String nom, String type) {
		if (type.equals("int") || type.equals("float")) {
			recordSize += 4;
		}
		else {
			recordSize += 2*Integer.parseInt(type.substring(6));
		}
		slotCount = (DBParams.PageSize-16)/(recordSize+1);
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getNb_col() {
		return nb_col;
	}

	public void setNb_col(int nb_col) {
		this.nb_col = nb_col;
	}

	public ArrayList<ColInfo> getListe() {
		return liste;
	}

	public PageID getHeaderPageId() {
		return headerPageId;
	}
	
	public int getSlotCount() {
		return slotCount;
	}
	
	public int getRecordSize() {
		return recordSize;
	}
	
	// retourne un string avec les informations de la relationInfo
	public String toString() {
	StringBuffer sb = new StringBuffer("");
	sb.append("nom: "+nom+" ");
	sb.append("nb_col: "+nb_col+" ");
	for (int i = 0; i < liste.size(); i++) {
		sb.append("nom_col"+i+": "+liste.get(i).getNom_col());
		sb.append("type_col"+i+": "+liste.get(i).getType_col());
	}
	sb.append(" pageID: "+headerPageId.getPageId());
	sb.append(" FileID: "+headerPageId.getFileId());
	return sb.toString();
	}
}
