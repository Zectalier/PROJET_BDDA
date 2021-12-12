import java.util.ArrayList;

/**
 * Classe de tests pour le Catalog
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class CatalogTests {	
	
  public static void main(String[]args) {
  
		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;
		
		RelationInfo relInfo = new RelationInfo("test",0,new ArrayList<ColInfo>(),new PageID(0,0));
		// Initier le catalog
		Catalog.INSTANCE.Init();
		// Ajoute une relation dans le catalog
		Catalog.INSTANCE.AddRelation(relInfo);
		// Sauvegarde le catalog dans le fichier catalog.def
		Catalog.INSTANCE.Finish();
	}
  
}