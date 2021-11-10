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

	public void Init() {
		try {
			FileInputStream tab = new FileInputStream(new File(DBParams.DBPath + "Catalog.def"));
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
			System.out.println("Erreur, le fichier n'a pas pu être trouvée");
			e.getMessage();
		}
	}

	public void AddRelation(RelationInfo liste) {
		tableau_rel_info.add(liste);
	}

}
