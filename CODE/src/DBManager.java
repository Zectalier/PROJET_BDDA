import java.util.ArrayList;

/**
 * Classe qui gère la Base de Donnée<p>
 * Elle comporte une seule et unique instance.
 * @author Hu Tony
 *
 */
public enum DBManager {
	
	DBMANAGER;

	/**
	 * Méthode qui contient un appel à la méthode Finish du Catalog et un appel à la méthode FlushAll du BufferManager
	 */
	public void Finish() {
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
	}

	/**
	 * Méthode qui contient un appel à la méthode Init du Catalog
	 */
	public void Init() {
		Catalog.INSTANCE.Init();
	}
	
	/**
	 * Méthode qui prend en argument une chaîne de caractères qui correspond à une commande et execute la commande adaptée
	 * @param reponse - String
	 */
	public void ProcessCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		switch (chaine[0]) {
			case "CREATE" :
				CreateRelationCommand create = new CreateRelationCommand(reponse);
				create.Execute();
				System.out.println("Relation Créée");
				break;
			case "DROPDB":
				DropDBCommand dropDB= new DropDBCommand();
				dropDB.Execute();
				System.out.println("La Base de données à été remise à zéro");
				break;
			case "INSERT":
				InsertCommand insert = new InsertCommand(reponse);
				insert.Execute();
				System.out.println("Le Record a été inséré");
				break;
			case "BATCHINSERT":
				BatchInsertCommand batchInsert = new BatchInsertCommand(reponse);
				batchInsert.Execute();
				System.out.println("Tout les tuples ont été insérés");
				break;
			case "SELECTMONO":
				SelectMonoCommand selectMono = new SelectMonoCommand(reponse);
				selectMono.Execute();
				break;
			case "DELETE":
				DeleteCommand delete = new DeleteCommand(reponse);
				delete.Execute();
				break;
			case "UPDATE":
				UpdateCommand update = new UpdateCommand(reponse);
				update.Execute();
				break;
			default:
				System.err.println("Erreur : commande inconnue");
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
	/**
	 * Méthode qui vérifie si les valeurs du record respectent les condions données
	 * @param conditions - ArrayList&lt;String&gt;
	 * @param record - Record
	 * @param relInfo - RelationInfo
	 * @return boolean
	 */
	public boolean VerifToutesConditions(ArrayList<String>conditions,Record record, RelationInfo relInfo) {
		for (int i = 0; i < conditions.size(); i++) {
			if(!VerifCondition(conditions.get(i),record,relInfo))
				return false;
			
		}
		return true;
	}	
}
