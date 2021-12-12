import java.io.Serializable;

/**
 * Classe qui permet de gérer les informations d'une colonne
 * @author Hu Tony
 *
 */
public class ColInfo implements Serializable{

	private static final long serialVersionUID = -4505354957773225295L;
	private String nom_col;
	private String type_col;

	/**
	 * Constructeur
	 * @param nom_col
	 * @param type_col
	 */
	public ColInfo(String nom_col, String type_col) {
		this.nom_col = nom_col;
		this.type_col = type_col;
	}

	/**
	 * Retourne le nom de la colonne
	 * @return String
	 */
	public String getNom_col() {
		return nom_col;
	}

	/**
	 * Retourne le type de la colonne
	 * @return String
	 */
	public String getType_col() {
		return type_col;
	}

	// Restreindre les types a int, float et String à taille fixe
}
