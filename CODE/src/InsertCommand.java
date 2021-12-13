/**
 * Classe qui gére la commande INSERT 
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class InsertCommand {
	RelationInfo relInfo;
	PageID freePage;
	Record record;
	String[] values;
	
	/**
	 * Constructeur
	 * @param reponse - String, la commande donnée par l'utilisateur
	 */
	public InsertCommand(String reponse) {
		reponse=reponse.replace("(","");
		reponse=reponse.replace(")","");
		String[] chaine = reponse.split(" ");
		relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
		freePage = FileManager.INSTANCE.getFreeDataPage(relInfo);
		record = new Record(relInfo);
		values= chaine[4].split(",");
	}
	
	/**
	 * Methode qui permet d'executer la commande INSERT
	 */
	public void Execute() {
		for (int i = 0; i < values.length; i++) {
			record.getValues().add(values[i]);
			BufferManager.INSTANCE.flushAll();
		}
		FileManager.INSTANCE.writeRecordToDataPage(relInfo, record, freePage);
	}
}
