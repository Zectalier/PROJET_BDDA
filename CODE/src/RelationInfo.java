import java.util.ArrayList;
// implementer serializable d'apres la prof pour  .... dans la class Catalog
public class RelationInfo {
	
	private  String nom;
	private int nb_col;
	private ArrayList<ColInfo>liste;
	
	public RelationInfo(String nom, int nb_col) {
		this.nom=nom;
		this.nb_col=nb_col;
		liste = new ArrayList<ColInfo>();
	}
	
	public String getNom() {
		return nom;
	}
	
	public int getNb_col() {
		return nb_col;
	}
	
	public ArrayList<ColInfo> getListe(){
		return liste;
	}
}
