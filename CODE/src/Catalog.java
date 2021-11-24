import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Catalog implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Catalog INSTANCE;
    private static ArrayList<RelationInfo> tableau_rel_info;
    private int comptRelations = 0;

    private Catalog() {
        tableau_rel_info = new ArrayList<RelationInfo>();
    }
    
	public static Catalog getInstance() {
		return getINSTANCE();
	}
/*
 * fait par nous mais ca ne marche pas, a la ligne 32, le "readObject" ne marche pas
    @SuppressWarnings("unchecked")
	public void Init() {
        try {
        	if ( getINSTANCE() == null) {
        		INSTANCE = new Catalog();
        	}
            FileInputStream tab = new FileInputStream(new File(DBParams.DBPath + "Catalog.txt"));
            ObjectInputStream object = new ObjectInputStream(tab);
            tableau_rel_info= (ArrayList<RelationInfo>) (object.readObject());
            object.close();

        } catch (IOException e) {
            System.out.println("Erreur, le fichier n'a pas pu Ãªtre trouvÃ©e");
            e.getMessage();
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur, classe inexistante");
            e.getMessage();
        }
    }
*/
	// fait par nous avec l'aide de steeven, ca marche pour 
	/** Instancie Catalog 
	 * @throws IOException 
	 * @throws ClassNotFoundException */
	@SuppressWarnings("unchecked")
	
	public static void Init() throws IOException, ClassNotFoundException{
		if(INSTANCE == null) {
			INSTANCE = new Catalog();
		}
		FileInputStream is = new FileInputStream(new File(DBParams.DBPath + "Catalog.def"));
	    ObjectInputStream ois = new ObjectInputStream(is);
	    tableau_rel_info = (ArrayList<RelationInfo>) ois.readObject();
		ois.close();
	}
	
    public void Finish() {

        try {
            FileOutputStream tab = new FileOutputStream(new File(DBParams.DBPath + "Catalog.def"));
            ObjectOutputStream object = new ObjectOutputStream(tab);
            object.writeObject(tableau_rel_info);
            object.close();
            if(getINSTANCE() != null) {
    			INSTANCE = null;
    		}
        } catch (IOException e) {
            System.out.println("Erreur, le fichier n'a pas pu Ãªtre trouvÃ©e");
            e.getMessage();
        }
    }

    public  ArrayList<RelationInfo> AddRelation(RelationInfo liste) {
        tableau_rel_info.add(liste);
        comptRelations = getComptRelations() + 1;
        return tableau_rel_info;
    }
    
	public ArrayList<RelationInfo> getRelationInfoList(){
		return tableau_rel_info;
	}

	public static Catalog getINSTANCE() {
		return INSTANCE;
	}

	public int getComptRelations() {
		return comptRelations;
	}

}
