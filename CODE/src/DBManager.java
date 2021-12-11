import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public enum DBManager {
	DBMANAGER;

	public void Finish() {
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
	}

	public void Init() {
		Catalog.INSTANCE.Init();
	}

	public void ProcessCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		switch (chaine[0]) {
			case "CREATE" :
				CreateRelationCommand create = new CreateRelationCommand(reponse);
				create.Execute();
				System.out.println("Relation Créée");
				break;
			case "DROPDB":
				DropDB();
				System.out.println("La Base de données à été supprimé");
				break;
			case "INSERT":
				Insert(reponse);
				System.out.println("Le Record a été inséré");
				break;
			case "BATCHINSERT":
				BatchInsert(reponse);
				System.out.println("Tout les tuples ont été inséré");
				break;
			case "SELECTMONO":
				SelectMono(reponse);
				break;
			case "DELETE":
				delete(reponse);
				break;
			default:
				System.err.println("Erreur : commande inconnue");
		}
	}
	
	public void Exit() {
		Finish();
		return;
	}
	
	public void DropDB() {
		BufferManager.INSTANCE.reset();
		Catalog.INSTANCE.reset();
		File dir = new File(DBParams.DBPath);
		for(File file: dir.listFiles()) {
		    if (!file.isDirectory()) { 
		        file.delete();
		    }
		}
	}

	public void Insert(String reponse) {
		reponse=reponse.replace("(","");
		reponse=reponse.replace(")","");
		String[] chaine = reponse.split(" ");
		
		try {
			RelationInfo relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
			PageID freePage = FileManager.INSTANCE.getFreeDataPage(relInfo);
			Record record = new Record(relInfo);
			String[] values= chaine[4].split(",");
			for (int i = 0; i < values.length; i++) {
				record.getValues().add(values[i]);
			}
			FileManager.INSTANCE.writeRecordToDataPage(relInfo, record, freePage);
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void BatchInsert(String reponse) {
		String[] chaine = reponse.split(" ");
		try {
			RelationInfo relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
			
			String filename = chaine[5];
			FileReader fr = new FileReader("../"+filename);
			BufferedReader br = new BufferedReader(fr);
			String line;
			Record record;
			while((line = br.readLine())!=null){
				String[] lotValues = line.split(",");
				record=new Record(relInfo);
				for (int i = 0; i < lotValues.length; i++) {
					record.getValues().add(lotValues[i]);
				}
				PageID freePage = FileManager.INSTANCE.getFreeDataPage(relInfo);
				FileManager.INSTANCE.writeRecordToDataPage(relInfo, record, freePage);
			}
			br.close();
			fr.close();
		}catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//on cherche l'indice de la colonne correspondant à la condition dans RelationInfo en vérifiant nomCom=condition[0]
	private int chercheIndiceColonne(RelationInfo relInfo, String string) {
		for (int i = 0; i < relInfo.getListe().size(); i++) {
			if(relInfo.getListe().get(i).getNom_col().equals(string)) {
				return i;
			}
		}
		return -1;
	}
	
	//on vérifie une condition du SELECTMONO
	private boolean VerifCondition(String chaine,Record record, RelationInfo relInfo) {
		//on split avec le séparateur <>, s'il n'y a pas de <> dans l'expression, alors il n'y aura qu'un seul élément dans condition, sinon, il y en aura 2 et donc, la condition à vérifier est le <>
		String[]condition=chaine.split("<>");
		int indice;
		if(condition.length==2) { //si 2 éléments, alors on a trouvé le bon opérateur
			indice=chercheIndiceColonne(relInfo,condition[0]);
			if(indice!=-1) {
				if(!record.getValues().get(indice).equals(condition[1])) //on vérifie si la chaine values.get(i)!=condition[1]
					return true;
			}
			//si indice = -1, alors on return false
			return false;
		}
		//on tente de split avec le séparateur <=
		condition=chaine.split("<=");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[0]);
			if(indice!=-1) { //si indice =-1, alors on n'a pas trouvé la colonne correspondante a ce qui a été donné par l'utilisateur
				String type = relInfo.getListe().get(indice).getType_col();
			//switch pour vérifier la vérifier la condition en fonction du type de la valeur
				switch(type) {
					case "int":
						if(Integer.parseInt(record.getValues().get(indice))<=(Integer.parseInt(condition[1]))) 
							return true;
						else
							return false;
					case "float":
						if(Float.parseFloat(record.getValues().get(indice))<=(Float.parseFloat(condition[1])))
							return true;
						else
							return false;
					default:
						if(record.getValues().get(indice).compareTo(condition[1])<=0)
							return true;
						else
							return false;
				}
			}
			return false;
		}
		//on split avec le séparateur >=
		condition=chaine.split(">=");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[0]);
			if(indice!=-1) {
				String type = relInfo.getListe().get(indice).getType_col();
				switch(type) {
					case "int":
						if(Integer.parseInt(record.getValues().get(indice))>=(Integer.parseInt(condition[1]))) 
							return true;
						else
							return false;
					case "float":
						if(Float.parseFloat(record.getValues().get(indice))>=(Float.parseFloat(condition[1])))
							return true;
						else
							return false;
					default:
						if(record.getValues().get(indice).compareTo(condition[1])>=0)
							return true;
						else
							return false;
				}
			}
			return false;
		}
		//on split avec le séparateur >
		condition=chaine.split(">");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[0]);
			if(indice!=-1) {
				String type = relInfo.getListe().get(indice).getType_col();
				switch(type) {
					case "int":
						if(Integer.parseInt(record.getValues().get(indice))>(Integer.parseInt(condition[1]))) 
							return true;
						else
							return false;
					case "float":
						if(Float.parseFloat(record.getValues().get(indice))>(Float.parseFloat(condition[1])))
							return true;
						else
							return false;
					default:
						if(record.getValues().get(indice).compareTo(condition[1])>0)
							return true;
						else
							return false;
				}
			}
			return false;
		}
		//on split avec le séparateur <
		condition=chaine.split("<");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[0]);
			if(indice!=-1) {
				String type = relInfo.getListe().get(indice).getType_col();
				switch(type) {
					case "int":
						if(Integer.parseInt(record.getValues().get(indice))<(Integer.parseInt(condition[1]))) 
							return true;
						else
							return false;
					case "float":
						if(Float.parseFloat(record.getValues().get(indice))<(Float.parseFloat(condition[1])))
							return true;
						else
							return false;
					default:
						if(record.getValues().get(indice).compareTo(condition[1])<0)
							return true;
						else
							return false;
				}
			}
			return false;
		}
		//il ne reste plus que le = comme opérateur possible
		condition=chaine.split("=");
		indice=chercheIndiceColonne(relInfo,condition[0]);
		if(indice!=-1) {
			if(record.getValues().get(indice).equals(condition[1])) //on vérifie si la chaine values.get(i)==condition[1]
				return true;
		}
		return false;
	}
	
	//on vérif toutes les conditions en faisant une boucle sur le nombre de conditions en utilisant la méthode précédente pour chaque condition
	public boolean VerifToutesConditions(ArrayList<String>conditions,Record record, RelationInfo relInfo) {
		for (int i = 0; i < conditions.size(); i++) {
			if(!VerifCondition(conditions.get(i),record,relInfo))
				return false;
			
		}
		return true;
	}
	
	public void SelectMono(String reponse) {
		String[] chaine = reponse.split(" ");
		try {
			RelationInfo relInfo = Catalog.INSTANCE.findRelation(chaine[3]);
			ArrayList<Record> record = FileManager.INSTANCE.getAllRecords(relInfo);
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
					conditions.add(chaine[i]);	//on ajoute toutes les condtions dans une ArrayList
				}
				for (int i = 0; i < record.size(); i++) {
					if(!record.get(i).getValues().isEmpty()) {
						if(VerifToutesConditions(conditions, record.get(i), relInfo)) { //on regarde si le record vérifie toutes les conditions
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
			System.out.println(e.getMessage());
		}
		
	}
	
	public void delete(String reponse) {
		String[] chaine = reponse.split(" ");
		int compteur = 0;
		try {
			RelationInfo relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
			ArrayList<String> conditions = new ArrayList<String>();
			for (int i = 4; i < chaine.length; i+=2) {
				conditions.add(chaine[i]);	//on ajoute toutes les condtions dans une ArrayList
			}
			compteur = FileManager.INSTANCE.deleteAllRecords(relInfo, conditions);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println(compteur+" tuple(s) a(ont) été(s) supprimé");
	}
}
