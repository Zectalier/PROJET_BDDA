import java.io.Serializable;
import java.util.ArrayList;

// the teacher told us to implements Serializable in RealtionInfi look up for Andrio for more information
public class RelationInfo implements Serializable {
	
	private  String nom;
	private int nb_col;
	private ArrayList<ColInfo>liste;
	
	public RelationInfo(final String nom, final int nb_col) {
		this.nom=nom;
		this.nb_col=nb_col;
		liste = new ArrayList<ColInfo>();
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(final String nom) {
		this.nom=nom;
	}
	
	public int getNb_col() {
		return nb_col;
	}
	
	public void setNb_col(final int nb_col) {
		this.nb_col=nb_col;
	}
	
	public ArrayList<ColInfo> getListe(){
		return liste;
	}
}
