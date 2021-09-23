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
		
	}
}
