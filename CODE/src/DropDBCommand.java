import java.io.File;

/**
 * Classe qui gère la commande DROPDB
 * @author Hu Tony, SILVA Andrio, CONSTANTINE Benjohnson
 *
 */
public class DropDBCommand {
	File dir;
	
	/**
	 * Constructeur
	 */
	public DropDBCommand() {
		dir=new File(DBParams.DBPath);
	}

	/**
	 * Methode qui permet d'executer la commande DROPDB
	 */
	public void Execute() {
		BufferManager.INSTANCE.reset();
		Catalog.INSTANCE.reset();
		for(File file:dir.listFiles()) {
			if(!file.isDirectory()) {
				file.delete();
			}
		}
	}
}

