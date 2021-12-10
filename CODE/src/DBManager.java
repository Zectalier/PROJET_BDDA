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
	
	private int chercheIndiceColonne(RelationInfo relInfo, String string) {
		for (int i = 0; i < relInfo.getListe().size(); i++) {
			if(relInfo.getListe().get(i).getNom_col().equals(string)) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean VerifCondition(String chaine,Record record, RelationInfo relInfo) {
		String[]condition=chaine.split("<>");
		int indice;
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[1]);
			if(!record.getValues().get(indice).equals(condition[1]))
				return true;
			else
				return false;
		}
		condition=chaine.split("<=");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[1]);
			String type = relInfo.getListe().get(indice).getType_col();
			switch(type) {
				case "int":
					if(Integer.parseInt(record.getValues().get(indice))<=(Integer.parseInt(chaine))) 
						return true;
					else
						return false;
				case "float":
					if(Float.parseFloat(record.getValues().get(indice))<=(Float.parseFloat(chaine)))
						return true;
					else
						return false;
				default:
					if(record.getValues().get(indice).compareTo(chaine)<=0)
						return true;
					else
						return false;
			}
		}
		condition=chaine.split(">=");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[1]);
			String type = relInfo.getListe().get(indice).getType_col();
			switch(type) {
				case "int":
					if(Integer.parseInt(record.getValues().get(indice))>=(Integer.parseInt(chaine))) 
						return true;
					else
						return false;
				case "float":
					if(Float.parseFloat(record.getValues().get(indice))>=(Float.parseFloat(chaine)))
						return true;
					else
						return false;
				default:
					if(record.getValues().get(indice).compareTo(chaine)>=0)
						return true;
					else
						return false;
			}
		}
		condition=chaine.split(">");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[1]);
			String type = relInfo.getListe().get(indice).getType_col();
			switch(type) {
				case "int":
					if(Integer.parseInt(record.getValues().get(indice))>(Integer.parseInt(chaine))) 
						return true;
					else
						return false;
				case "float":
					if(Float.parseFloat(record.getValues().get(indice))>(Float.parseFloat(chaine)))
						return true;
					else
						return false;
				default:
					if(record.getValues().get(indice).compareTo(chaine)>0)
						return true;
					else
						return false;
			}
		}
		condition=chaine.split("<");
		if(condition.length==2) {
			indice=chercheIndiceColonne(relInfo,condition[1]);
			String type = relInfo.getListe().get(indice).getType_col();
			switch(type) {
				case "int":
					if(Integer.parseInt(record.getValues().get(indice))<(Integer.parseInt(chaine))) 
						return true;
					else
						return false;
				case "float":
					if(Float.parseFloat(record.getValues().get(indice))<(Float.parseFloat(chaine)))
						return true;
					else
						return false;
				default:
					if(record.getValues().get(indice).compareTo(chaine)<0)
						return true;
					else
						return false;
			}
		}
		condition=chaine.split("<");
		indice=chercheIndiceColonne(relInfo,condition[1]);
		String type = relInfo.getListe().get(indice).getType_col();
		switch(type) {
			case "int":
				if(Integer.parseInt(record.getValues().get(indice))<(Integer.parseInt(chaine))) 
					return true;
				else
					return false;
			case "float":
				if(Float.parseFloat(record.getValues().get(indice))<(Float.parseFloat(chaine)))
					return true;
				else
					return false;
			default:
				if(record.getValues().get(indice).compareTo(chaine)<0)
					return true;
				else
					return false;
		}
	}
	
	private boolean VerifToutesConditions(ArrayList<String>conditions,Record record, RelationInfo relInfo) {
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
				ArrayList<String> conditions = new ArrayList<String>();
				int compteur=0;
				for (int i = 5; i < chaine.length; i+=2) {
					conditions.add(chaine[i]);
				}
				for (int i = 0; i < record.size(); i++) {
					if(VerifToutesConditions(conditions, record.get(i), relInfo)) {
						for (int j = 0; j < record.get(i).getValues().size(); j++) {
							System.out.print(record.get(i).getValues().get(j)+";");
							compteur++;
						}
						System.out.println("");
					}
					
				}
				System.out.println("Total Record = " +compteur);
			}
			
		}catch(NoSuchElementException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
