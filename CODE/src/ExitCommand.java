/**
 * Classe qui gère la commande EXIT
 * @author Hu Tony, SILVA Andrio, CONSTANTINE Benjohnson
 *
 */
public class ExitCommand {
	
	
	/**
	 * Methode qui permet d'executer la commande EXIT
	 */
	public void Execute() {
		DBManager.DBMANAGER.Finish();
	}
}
