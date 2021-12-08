import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;

public enum DBManager {
	DBManager;

	public void Finish() {
		// TODO Auto-generated method stub
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
	}

	public void Init() {
		// TODO Auto-generated method stub
		Catalog.INSTANCE.Init();
	}

	public void ProcessCommand(String reponse) {
		String[] chaine = reponse.split(" ()");
		switch (chaine[0]) {
			case "CREATE" :
				CreateRelationCommand create = new CreateRelationCommand(
						reponse);
				create.Execute();
				break;
		}
	}
	
	public boolean Exit(String reponse) {
		Finish();
		return true;
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
		String[] chaine = reponse.split(" ()");
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
		String[] chaine = reponse.split(" ()");
		try {
			RelationInfo relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
			PageID freePage = FileManager.INSTANCE.getFreeDataPage(relInfo);
			String filename = chaine[5];
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line;
			Record record;
			while((line = br.readLine())!=null){
				String[] lotValues = reponse.split(",");
				record=new Record(relInfo);
				for (int i = 0; i < lotValues.length; i++) {
					record.getValues().add(lotValues[i]);
				}
				FileManager.INSTANCE.writeRecordToDataPage(relInfo, record, freePage);
			}
		}catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
