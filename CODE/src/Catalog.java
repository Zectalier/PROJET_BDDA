import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Classe qui contient les informations de schéma pour l'ensemble de la base de données.<p>
 * Elle comporte une seule et unique instance.
 * @author Hu Tony, SILVA Andrio, CONSTANTINE Benjohnson
 *
 */
public enum Catalog {
	INSTANCE;

	private ArrayList<RelationInfo> tableau_rel_info;

	private Catalog() {
		tableau_rel_info = new ArrayList<RelationInfo>();
	}

	/**
	 * Méthode qui permet d'obtenir la liste des relations d'une base de données
	 */
	@SuppressWarnings("unchecked") // I added this line cause I got a warning on with FileInputStream -ben
	public void Init() {
		try {
			File file = new File(DBParams.DBPath + "Catalog.def");
			file.createNewFile();
			FileInputStream tab = new FileInputStream(file);
			if(file.length()==0) {
				tab.close();
				return;
			}
			ObjectInputStream object = new ObjectInputStream(tab);
			tableau_rel_info= (ArrayList<RelationInfo>) (object.readObject());
			object.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			
		}
	}

	/**
	 * Méthode qui permet de sauvegarder les relations de la base de données
	 */
	public void Finish() {

		try {
			FileOutputStream tab = new FileOutputStream(new File(DBParams.DBPath + "Catalog.def"));
			ObjectOutputStream object = new ObjectOutputStream(tab);
			object.writeObject(tableau_rel_info);
			object.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Ajoute une Relation dans la liste des relations
	 * @param liste
	 */
	public void AddRelation(RelationInfo relinfo) {
		tableau_rel_info.add(relinfo);
	}
	
	/**
	 * Remet à zéro la liste des relations et supprime le fichier contenant les relations
	 */
	public void reset() {
		tableau_rel_info = new ArrayList<RelationInfo>();
		File catalogFile = new File(DBParams.DBPath + "Catalog.def");
		catalogFile.delete();
	}
	
	/**
	 * Retourne la liste des relations
	 * @return ArrayList<RelationInfo>
	 */
	public ArrayList<RelationInfo> getTableauRelInfo(){
		return tableau_rel_info;
	}
	
	/**
	 * Retourne la relationInfo qui est est dans la liste de relation si celle ci est présente
	 * @param nomRelation - le nom de la relation
	 * @return RelationInfo
	 */
	public RelationInfo findRelation(String nomRelation) {
		for (int i = 0; i < tableau_rel_info.size(); i++) {
			if(tableau_rel_info.get(i).getNom().equals(nomRelation))
				return tableau_rel_info.get(i);
		}
		return null;
	}
}