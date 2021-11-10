import java.io.Serializable;
import java.util.ArrayList;

// the teacher told us to implements Serializable in RealtionInfi look up for Andrio for more information
public class RelationInfo implements Serializable {

	private String nom;
	private int nb_col;
	private ArrayList<ColInfo> liste;
	private PageID headerPageId;
	private int recordSize;
	private int slotCount;

	public RelationInfo(final String nom, final int nb_col, ArrayList<ColInfo> liste, PageID headerPageId) {
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

	public void setNom(final String nom) {
		this.nom = nom;
	}

	public int getNb_col() {
		return nb_col;
	}

	public void setNb_col(final int nb_col) {
		this.nb_col = nb_col;
	}

	public ArrayList<ColInfo> getListe() {
		return liste;
	}

	public PageID getHeaderPageId() {
		return headerPageId;
	}
}
