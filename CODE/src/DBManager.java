import java.util.ArrayList;

/**
 * Classe qui g�re la Base de Donn�e<p>
 * Elle comporte une seule et unique instance.
 * @author Hu Tony
 *
 */
public enum DBManager {
	
	DBMANAGER;

	/**
	 * M�thode qui contient un appel � la m�thode Finish du Catalog et un appel � la m�thode FlushAll du BufferManager
	 */
	public void Finish() {
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
	}

	/**
	 * M�thode qui contient un appel � la m�thode Init du Catalog
	 */
	public void Init() {
		Catalog.INSTANCE.Init();
	}
	
	/**
	 * M�thode qui prend en argument une cha�ne de caract�res qui correspond � une commande et execute la commande adapt�e
	 * @param reponse - String
	 */
	public void ProcessCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		switch (chaine[0]) {
			case "CREATE" :
				CreateRelationCommand create = new CreateRelationCommand(reponse);
				create.Execute();
				System.out.println("Relation Cr��e");
				break;
			case "DROPDB":
				DropDBCommand dropDB= new DropDBCommand();
				dropDB.Execute();
				System.out.println("La Base de donn�es � �t� remise � z�ro");
				break;
			case "INSERT":
				InsertCommand insert = new InsertCommand(reponse);
				insert.Execute();
				System.out.println("Le Record a �t� ins�r�");
				break;
			case "BATCHINSERT":
				BatchInsertCommand batchInsert = new BatchInsertCommand(reponse);
				batchInsert.Execute();
				System.out.println("Tout les tuples ont �t� ins�r�s");
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
	
	//on cherche l'indice de la colonne correspondant � la condition dans RelationInfo en v�rifiant nomCom=condition[0]
	private int chercheIndiceColonne(RelationInfo relInfo, String string) {
		for (int i = 0; i < relInfo.getListe().size(); i++) {
			if(relInfo.getListe().get(i).getNom_col().equals(string)) {
				return i;
			}
		}
		return -1;
	}
	
	//on v�rifie une condition du SELECTMONO
	private boolean VerifCondition(String chaine,Record record, RelationInfo relInfo) {
		//on split avec le s�parateur <>, s'il n'y a pas de <> dans l'expression, alors il n'y aura qu'un seul �l�ment dans condition, sinon, il y en aura 2 et donc, la condition � v�rifier est le <>
		String[]condition=chaine.split("<>");
		int indice;
		if(condition.length==2) { //si 2 �l�ments, alors on a trouv� le bon op�rateur
			indice=chercheIndiceColonne(relInfo,condition[0]);
			if(indice!=-1) {
				if(!record.getValues().get(indice).equals(condition[1])) //on v�rifie si la chaine values.get(i)!=condition[1]
					return true;
			}
			//si indice = -1, alors on return false
			return false;
		}
		//on tente de split avec le s�parateur <=
		condition=chaine.split("<=");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[0]);
			if(indice!=-1) { //si indice =-1, alors on n'a pas trouv� la colonne correspondante a ce qui a �t� donn� par l'utilisateur
				String type = relInfo.getListe().get(indice).getType_col();
			//switch pour v�rifier la v�rifier la condition en fonction du type de la valeur
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
		//on split avec le s�parateur >=
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
		//on split avec le s�parateur >
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
		//on split avec le s�parateur <
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
		//il ne reste plus que le = comme op�rateur possible
		condition=chaine.split("=");
		indice=chercheIndiceColonne(relInfo,condition[0]);
		if(indice!=-1) {
			if(record.getValues().get(indice).equals(condition[1])) //on v�rifie si la chaine values.get(i)==condition[1]
				return true;
		}
		return false;
	}
	
	//on v�rif toutes les conditions en faisant une boucle sur le nombre de conditions en utilisant la m�thode pr�c�dente pour chaque condition
	/**
	 * M�thode qui v�rifie si les valeurs du record respectent les condions donn�es
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
