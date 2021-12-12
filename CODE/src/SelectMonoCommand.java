import java.util.ArrayList;
import java.util.NoSuchElementException;

public class SelectMonoCommand {
	RelationInfo relInfo;
	ArrayList<Record> record;
	String[] chaine;
	
	public SelectMonoCommand(String reponse) {
		chaine = reponse.split(" ");
		relInfo = Catalog.INSTANCE.findRelation(chaine[3]);
		record = FileManager.INSTANCE.getAllRecords(relInfo);
	}
	
	public void Execute() {
		try {
			if(chaine.length==4) {	//cas où il n'y a pas de WHERE, donc pas de conditions
				if(chaine[1].equals("*")){ //on affiche tous les records de la relation
					int recordnonvide = 0;
					for (int i = 0; i < record.size(); i++) {
						if(!record.get(i).getValues().isEmpty()) {
							for (int j = 0; j < record.get(i).getValues().size(); j++) {
								System.out.print(record.get(i).getValues().get(j)+";");
							}
							recordnonvide++;
							System.out.println("");
						}
					}
					System.out.println("Total Record = " + recordnonvide);	
				}else {
					System.out.println("");
				}
			}else{ //il y a des conditions WHERE dans le SELECTMONO
				ArrayList<String> conditions = new ArrayList<String>();
				int compteur=0;
				for (int i = 5; i < chaine.length; i+=2) {
					conditions.add(chaine[i]);	//on ajoute toutes les conditions dans une ArrayList
				}
				for (int i = 0; i < record.size(); i++) {
					if(!record.get(i).getValues().isEmpty()) {
						if(DBManager.DBMANAGER.VerifToutesConditions(conditions, record.get(i), relInfo)) { //on regarde si le record vérifie toutes les conditions
							for (int j = 0; j < record.get(i).getValues().size(); j++) {
								System.out.print(record.get(i).getValues().get(j)+";");
							}
							compteur++;
							System.out.println("");
						}
					}
				}
				System.out.println("Total Record = " + compteur);
			}
		}catch(NoSuchElementException e) {
			e.printStackTrace();
		}
	}
}
