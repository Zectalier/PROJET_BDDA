import java.nio.*;
import java.io.File;
import java.io.IOException;

public class DiskManager {

	public PageID AllocPage() {

		boolean found = false;
		int x = 0;
		PageID tmp = new PageID();
		String filename = String.format("../DB/f%d.df", x);
		File file = new File(filename);

		// Si le fichier f0.df n'existe pas
		if (!file.exists()) {
			// Creer le nouveau fichier f0.df ici
		}

		// Recherche d'un fichier non plein
		while (found == false) {
			filename = String.format("../DB/f%d.df", x);
			file = new File(filename);
			long fileSize = file.length();

			// Si le fichier a de l'espace disponible
			if (fileSize <= DBParams.PageSize * DBParams.maxPagesPerFile) {
				// Allouer la page ici

				found = true;
			}

			// Si on ne trouve pas de fichiers qui a de la place
			else if (!file.exists()) {
				// Creer le nouveau fichier fx.df ici
				return tmp; // retourne un PageID ( lequel? )
			}

			// Si fx.df ne convient pas, on cherche pour x+1;
			x++;
		}

		return tmp; // retourne un PageID ( lequel? )
	}

	public PageID AllocPagev2() {
		File folder = new File("../DB/");
		char x=0;
		//on parcourt l'ensemble des fichiers dans le dossier DB
		for (File file : folder.listFiles()) {
			//on récupere le nom du fichier
			String fichier = file.getName().toString();
			// on recupere le n°
			x = fichier.charAt(1);
			// on vérifie qu'il y a de la place dans le fichier pour une nouvelle page
			if (file.length() < (4096 * 3 + 4095)) {
				if(file.length()==0) {  				//on vérifie si le fichier est vide
					PageID page = new PageID(x,0);
					return page;
				}else if(file.length() / 4096.0 < 1) { 	//on vérifie s'il y a une page dans le fichier
					PageID page = new PageID(x,1);
					return page;
				}else if (file.length() / 4096.0 < 2) {	//on vérifie s'il y a deux pages dans le fichier
					PageID page = new PageID(x,2);
					return page;
				}else if(file.length() / 4096.0 < 3) { //on vérifie s'il y a trois pages dans le fichier
					PageID page = new PageID(x,3);
					return page;
				}
			}
		}
		//si pas de fichier disponible, on créer un nouveau fichier
		File fichier = new File("../DB/f"+x+".df");
		try{
			fichier.createNewFile();
		}catch(IOException e) {
			System.out.println("Erreur, le fichier n'a pas pu être crée");
			e.getMessage();
		}
		PageID page = new PageID(x,0);
		return page;
	}

	public void ReadPage(int pageId, ByteBuffer buff) {

	}

	public void WritePage(int pageId, ByteBuffer[] buff) {

	}
}
