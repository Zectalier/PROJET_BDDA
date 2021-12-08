import java.io.Serializable;

public class ColInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4505354957773225295L;
	private String nom_col;
	private String type_col;

	public ColInfo(String nom_col, String type_col) {
		this.nom_col = nom_col;
		this.type_col = type_col;
	}

	public String getNom_col() {
		return nom_col;
	}

	public String getType_col() {
		return type_col;
	}

	// Restreindre les types a int, float et String à taille fixe
}
