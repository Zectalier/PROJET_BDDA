import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Catalog {
	
	private ArrayList<RelationInfo> tableau_rel_info;

	
	public Catalog(ArrayList<RelationInfo>tab) {
		tableau_rel_info = tab;
	}
	
	public void Init() {
		try{
			FileInputStream tab = new FileInputStream(DBParams.DBPath+"Catalog.def");
			ObjectInputStream object =  new ObjectInputStream(tab) ;
			
			// cette ligne ne marche pas, a completer 
		    tableau_rel_info = (ArrayList<RelationInfo>) object.readObject();

		    object.close();
			
		}catch(IOException e) {
			System.out.println("Erreur, le fichier n'a pas pu être crée");
			e.getMessage();
		}catch(ClassNotFoundException e) {
			System.out.println("Erreur, classe inexistante");
			e.getMessage();
		}
	}

	public void Finish() {
		
		try{
			FileOutputStream tab = new FileOutputStream(DBParams.DBPath+"Catalog.def");
			ObjectOutputStream object =  new ObjectOutputStream(tab) ;
			
			object.writeObject(tableau_rel_info);
			object.close();
			
		}catch(IOException e) {
			System.out.println("Erreur, le fichier n'a pas pu être crée");
			e.getMessage();
		}
	}
	
	public void AddRelation(RelationInfo liste) {
		tableau_rel_info.add(liste);
	}
}