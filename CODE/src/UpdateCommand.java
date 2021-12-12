import java.util.ArrayList;
import java.util.Arrays;

public class UpdateCommand {
	RelationInfo relInfo;
	ArrayList<String> updateTo;
	ArrayList<String> listeCol;
	ArrayList<String> conditions;
	
	public UpdateCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		relInfo = Catalog.INSTANCE.findRelation(chaine[1]);
		updateTo = new ArrayList<String>(Arrays.asList(chaine[3].split(",")));
		listeCol = new ArrayList<String>();
		for(String str : updateTo) {
			if(!listeCol.contains(str.substring(0,str.indexOf("=")))) {
				listeCol.add(str.substring(0,str.indexOf("=")));
			}
			else {
				System.err.println("Attention une colonne de la relation apparaît plusieurs fois");
				return;
			}
			str = str.substring(str.indexOf("="));
		}
		conditions = new ArrayList<String>();
		for (int i = 5; i < chaine.length; i+=2) {
			conditions.add(chaine[i]);	//on ajoute toutes les conditions dans une ArrayList
		}
	}
	
	public void Execute() {
		int compteur = 0;
		try {
			compteur = FileManager.INSTANCE.updateAllRecords(relInfo, updateTo, conditions);
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(compteur+" tuple(s) a(ont) été(s) mis à jour(s)");
	}
}
