import java.io.*;
import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("coucou");
		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;
		
		File directory = new File("./");
	    System.out.println(directory.getAbsolutePath());
	    int x = 0;
	    String filename = String.format("../DB/f%d.df",x);
	    System.out.println(filename);
	    
	    File file = new File(filename);
		long fileSize = file.length();
	    System.out.println(fileSize);
		
	    try {
			BufferedReader reader = new BufferedReader(new FileReader("../DB/test.txt"));
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
