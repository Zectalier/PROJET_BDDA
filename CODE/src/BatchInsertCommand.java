import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Classe qui gère la commande BATCHINSERT
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class BatchInsertCommand {
	RelationInfo relInfo;
	String filename;
	
	/**
	 * Constructeur
	 * @param reponse - String, la commande donnée par l'uilisateur
	 */
	public BatchInsertCommand(String reponse) {
		String[] chaine = reponse.split(" ");
		relInfo = Catalog.INSTANCE.findRelation(chaine[2]);
		filename = chaine[5];
	}
	
	/**
	 * Methode qui permet d'executer la commande BATCHINSERT
	 */
	public void Execute() {
		try {
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
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
