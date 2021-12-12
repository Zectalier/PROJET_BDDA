/**
 * Classe contenant le Main de l'application
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 */

public class Main {

	public static void main(String[] args) {

		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;

		DBManager.DBMANAGER.Init();
		Menu.menuCommande();
		
	}

}