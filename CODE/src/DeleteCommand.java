import java.util.ArrayList;

/**
 * Classe qui gère la commande DELETE
 * @author Hu Tony
 *
 */
public class DeleteCommand {
	RelationInfo relInfo;
	ArrayList<String> conditions;
	
	public DeleteCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
		conditions = new ArrayList<String>();
		for (int i = 4; i < chaine.length; i+=2) {
			conditions.add(chaine[i]);	//on ajoute toutes les conditions dans une ArrayList
		}
	}
	
	public void Execute() {
		int compteur=0;
		try {
			compteur = FileManager.INSTANCE.deleteAllRecords(relInfo, conditions);
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(compteur+" tuple(s) a(ont) été(s) supprimé(s)");
	}
}
