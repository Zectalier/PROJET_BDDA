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
				break;
			case "DROPDB":
				DropDB();
				break;
			case "INSERT":
				Insert(reponse);
				break;
			case "BATCHINSERT":
				BatchInsert(reponse);
				break;
			case "SELECTMONO":
				SelectMono(reponse);
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
	
	public boolean VerifCondition(ArrayList<Record> record) {
		
		return true;
	}
	
	public void SelectMono(String reponse) {
		String[] chaine = reponse.split(" ");
		try {
			RelationInfo relInfo = Catalog.INSTANCE.findRelation(chaine[3]);
			ArrayList<Record> record = FileManager.INSTANCE.getAllRecords(relInfo);
			if(chaine.length==3) {
				if(chaine[1].equals("*")){
					for (int i = 0; i < record.size(); i++) {
						for (int j = 0; j < record.get(i).getValues().size(); j++) {
							System.out.print(record.get(i).getValues().get(j)+";");
						}
						System.out.println("");
					}
					System.out.println("Total Record = " +record.size());	
				}else {
					System.out.println("");
				}
			}else{
				for (int i = 5; i < chaine.length; i+=2) {
					String[] condition = chaine[i].split("<>");
					if(condition.length==2) {
						int indice = 0;
						String type = null;
						for (int k=0; k<relInfo.getListe().size();k++) {
							if(relInfo.getListe().get(k).getNom_col().equals(condition[0])) {
								indice = k;
								type = relInfo.getListe().get(k).getType_col();
							}
						}
						for (int y = 0; y < record.size(); y++) {
							for (int j = 0; j < record.get(y).getValues().size(); j++) {
								if(type.equals("int") || type.equals("float") ) {
									if(record.get(y).getValues().get(indice)!=(Interger.parseInt(option1))) {
										
									}
								}
								System.out.print(record.get(y).getValues().get(j)+";");
							}
							System.out.println("");
						}
					}
				}
			}
			
		}catch(NoSuchElementException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
