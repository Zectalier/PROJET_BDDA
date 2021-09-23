import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("coucou");
		DBParams.DBPath = "../../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("../../DB/test.txt"));
			String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
		}catch(FileNotFoundException e){
			System.out.println("Erreur, fichier introuvable");
			e.getMessage();
		}catch(IOException e) {
			e.getMessage();
		}
	}
}
