import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public enum Catalog {
	INSTANCE;

	private ArrayList<RelationInfo> tableau_rel_info;

	private Catalog() {
		tableau_rel_info = new ArrayList<RelationInfo>();
	}
	
	// i add line 19 cause i got a warning on line 24
	@SuppressWarnings("unchecked")
	public void Init() {
		try {
			File file = new File(DBParams.DBPath + "Catalog.def");
			FileInputStream tab = new FileInputStream(file);
			if(file.length()==0) {
				tab.close();
				return;
			}
			ObjectInputStream object = new ObjectInputStream(tab);
			tableau_rel_info= (ArrayList<RelationInfo>) (object.readObject());
			object.close();

		} catch (IOException e) {
			System.out.println("Erreur, le fichier n'a pas pu être trouvée");
			e.getMessage();
		} catch (ClassNotFoundException e) {
			System.out.println("Erreur, classe inexistante");
			e.getMessage();
		}
	}

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

	public void AddRelation(RelationInfo liste) {
		for (int i = 0; i <tableau_rel_info.size(); i++) {
			System.out.println(tableau_rel_info.get(i));
		}
		tableau_rel_info.add(liste);
	}
	
	public void reset() {
		tableau_rel_info = new ArrayList<RelationInfo>();
		File catalogFile = new File(DBParams.DBPath + "Catalog.def");
		catalogFile.delete();
	}
	
	public ArrayList<RelationInfo> getTableauRelInfo(){
		return tableau_rel_info;
	}
	
	public RelationInfo findRelation(String nomRelation) {
		for (int i = 0; i < tableau_rel_info.size(); i++) {
			if(tableau_rel_info.get(i).getNom().equals(nomRelation))
				return tableau_rel_info.get(i);
		}
		return null;
	}
}