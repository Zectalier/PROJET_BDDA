import java.io.File;

public class DropDBCommand {
	File dir;
	
	public DropDBCommand() {
		dir=new File(DBParams.DBPath);
	}
	
	public void Execute() {
		BufferManager.INSTANCE.reset();
		Catalog.INSTANCE.reset();
		for(File file:dir.listFiles()) {
			if(!file.isDirectory()) {
				file.delete();
			}
		}
	}
}

