import java.util.ArrayList;

/**
 * Classe qui g?re la commande DELETE
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class DeleteCommand {
	RelationInfo relInfo;
	ArrayList<String> conditions;
	
	/**
	 * Constructeur
	 * @param reponse, la commande donn?e par l'utilisateur
	 */
	public DeleteCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
		conditions = new ArrayList<String>();
		for (int i = 4; i < chaine.length; i+=2) {
			conditions.add(chaine[i]);	//on ajoute toutes les conditions dans une ArrayList
		}
	}
	
	/**
	 * Methode qui permet d'executer la methode DELETE
	 */
	public void Execute() {
		int compteur=0;
		try {
			compteur = FileManager.INSTANCE.deleteAllRecords(relInfo, conditions);
			BufferManager.INSTANCE.flushAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(compteur+" tuple(s) a(ont) ?t?(s) supprim?(s)");
	}
}