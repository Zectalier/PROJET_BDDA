
public class InsertCommand {
	RelationInfo relInfo;
	PageID freePage;
	Record record;
	String[] values;
	
	public InsertCommand(String reponse) {
		reponse=reponse.replace("(","");
		reponse=reponse.replace(")","");
		String[] chaine = reponse.split(" ");
		relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
		freePage = FileManager.INSTANCE.getFreeDataPage(relInfo);
		record = new Record(relInfo);
		values= chaine[4].split(",");
	}
	
	public void Execute() {
		for (int i = 0; i < values.length; i++) {
			record.getValues().add(values[i]);
		}
		FileManager.INSTANCE.writeRecordToDataPage(relInfo, record, freePage);
	}
}

