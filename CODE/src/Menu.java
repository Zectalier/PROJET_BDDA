import java.util.Scanner;
/**
 * Classe qui contient le menu pour le main
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class Menu {
	/**
	 * Menu qu'on va utilisé pour le main, et qui va permettre de lire les commandes des utilisateurs
	 */
	public static void menuCommande() {
		boolean end = false;
		Scanner scan;
		do {
			scan= new Scanner(System.in);
			String reponse = scan.nextLine();
			switch(reponse){
				case "EXIT":
					ExitCommand exit = new ExitCommand();
					exit.Execute();
					end=true;
					break;
				default: 
					DBManager.DBMANAGER.ProcessCommand(reponse);
					break;
			}	
		}while (!end);
		scan.close();
	}
}
