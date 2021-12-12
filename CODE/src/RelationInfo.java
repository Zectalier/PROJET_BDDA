import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe qui permet de garder les informations « de schéma » d’une relation. <p>
 * Elle est composé de:<br>
 * - nom de la relation,<br>
 * - nombre de colonnes,<br>
 * - liste de ColInfo avec les informations de chaque colonne,<br>
 * - pageId de la headerPage,<br>
 * - la taille d'un record<br>
 * - nombre de slot.
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class RelationInfo implements Serializable {

	private static final long serialVersionUID = -2196028207559318797L;
	private String nom;
	private int nb_col;
	private ArrayList<ColInfo> liste;
	private PageID headerPageId;
	private int recordSize;
	private int slotCount;

	/**
	 * Constructeur qui permet de construire une nouvelle relation. 
	 * Ce constructeur permet d'obtenir la taille d'un record et le nombre de slots automatiquement.
	 * @param nom
	 * @param nb_col
	 * @param liste
	 * @param headerPageId
	 */
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

	/**
	 * Retourne le nom de la relation
	 * @return String
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Ajoute une colonne dans la relation
	 * @param nom - nom de la relation
	 * @param type - type de la relation
	 */
	public void addCol(String nom, String type) {
		if (type.equals("int") || type.equals("float")) {
			recordSize += 4;
		}
		else {
			recordSize += 2*Integer.parseInt(type.substring(6));
		}
		slotCount = (DBParams.PageSize-16)/(recordSize+1);
		nb_col++;
		liste.add(new ColInfo(nom, type));
	}

	/**
	 * Modifie le nom de la relation par celui donné en argument
	 * @param nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * Retourne le nombre de colonne de la relation
	 * @return int
	 */
	public int getNb_col() {
		return nb_col;
	}

	/**
	 * Modifie le nombre de colonnes par celui donné en argument
	 * @param nb_col
	 */
	public void setNb_col(int nb_col) {
		this.nb_col = nb_col;
	}

	/**
	 * Retourne la liste des ColInfo de la relation
	 * @return ArrayList<ColInfo>
	 */
	public ArrayList<ColInfo> getListe() {
		return liste;
	}

	/**
	 * Retourne la PageID du headerPage de la relation
	 * @return PageID
	 */
	public PageID getHeaderPageId() {
		return headerPageId;
	}
	
	/**
	 * Retourne le nombre de slot dans la relation
	 * @return int
	 */
	public int getSlotCount() {
		return slotCount;
	}
	
	/**
	 * Retourne la taille du record dans la relation
	 * @return int
	 */
	public int getRecordSize() {
		return recordSize;
	}
	
	/**
	 * Retourne un string avec les informations de la relation
	 * @return String
	 */
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
