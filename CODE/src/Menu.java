import java.util.Scanner;

public class Menu {

	public static void menuCommande() {
		boolean end = false;
		Scanner scan;
		do {
			scan= new Scanner(System.in);
			String reponse = scan.nextLine();
			switch(reponse){
				case "EXIT":
					DBManager.Finish();
					end = true;
					break;
				default: 
					DBManager.ProcessCommand(reponse);
					break;
			}	
		}while (!end);
		scan.close();
	}
}