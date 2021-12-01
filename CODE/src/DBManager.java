import java.io.File;

public enum DBManager {

	DBMANAGER;
	
	public void Init() {
		Catalog.INSTANCE.Init();
	}
	
	public void Finish() {
		Catalog.INSTANCE.Finish();
	}
	
	public void ProcessCommand() {
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
