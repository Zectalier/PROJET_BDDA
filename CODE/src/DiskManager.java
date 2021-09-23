import java.nio.*;
import java.io.File;

public class DiskManager {

	public PageID AllocPage() {
		
		boolean found = false;
		int x = 0;
		PageID tmp = new PageID();
		String filename = String.format("../DB/f%d.df",x);
		File file = new File(filename);
		
		//Si le fichier f0.df n'existe pas
		if (!file.exists()) {
			//Creer le nouveau fichier f0.df ici
		}
		
		//Recherche d'un fichier non plein
		while(found == false) {
			filename = String.format("../DB/f%d.df",x);
			file = new File(filename);
			long fileSize = file.length();
			
			//Si le fichier a de l'espace disponible
			if(fileSize <= DBParams.PageSize * DBParams.maxPagesPerFile) {
				//Allouer la page ici
				
				found = true;
			}
			
			//Si on ne trouve pas de fichiers qui a de la place
			else if (!file.exists()) {
				//Creer le nouveau fichier fx.df ici
				return tmp; //retourne un PageID ( lequel? )
			}
			
			//Si fx.df ne convient pas, on cherche pour x+1;
			x++;
		}
		
		return tmp; //retourne un PageID ( lequel? )
	}
	
	public void ReadPage(int pageId,ByteBuffer buff) {
		
	}
	
	public void WritePage(int pageId,ByteBuffer[] buff) {
		
	}
}

