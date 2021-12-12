/**
 * Classe contenant le Main de l'application
 * @author Hu Tony, SILVA Andrio, CONSTANTINE Benjohnson
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