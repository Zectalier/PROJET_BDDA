import java.io.File;

public enum DBManager {

	DBMANAGER;
	
	public void Init() {
		Catalog.INSTANCE.Init();
	}
	
	public void Finish() {
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
	}
	
	public static void ProcessCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		switch(chaine[0]){
			case "CREATE":
				CreateRelationCommand create = new CreateRelationCommand(reponse);
				create.Execute();
				break;
		}
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
}