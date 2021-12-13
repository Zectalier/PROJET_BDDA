/**
 * Classe qui gère la commande EXIT
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
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