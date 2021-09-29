import java.nio.*;
import java.nio.channels.FileChannel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskManager {

	public PageID AllocPage() {
		
		boolean found = false;
		int x = 0;
		String filename = String.format(DBParams.DBPath+"f%d.df",x);
		File file = new File(filename);
		PageID page = new PageID(0,0);
		
		//Si le fichier f0.df n'existe pas
		if (!file.exists()) {
			//Creer le nouveau fichier f0.df ici
			try{
				file.createNewFile();
			}catch(IOException e) {
				System.out.println("Erreur, le fichier n'a pas pu être crée");
				e.getMessage();
			}
			
			//Allouer la page PageID(0,0) au nouveau fichier ( qui devrait être "f0.df" ) avec WtitePage()
			
			//
			page = new PageID(x,0);
			found = true;
			return page; //retourne un PageID (ici normalement PageID(0,0)
		}
		
		//Recherche d'un fichier non plein
		while(found == false) {
			filename = String.format("../DB/f%d.df",x);
			file = new File(filename);
			
			//Création du fichier si on ne trouve pas de fichier ayant de la place
			if (!file.exists()) {
				
				//Creer le nouveau fichier fx.df ici
				try{
					file.createNewFile();
				}catch(IOException e) {
					System.out.println("Erreur, le fichier n'a pas pu être crée");
					e.getMessage();
				}
				//Allouer la page PageID(x,0) au nouveau fichier ( qui devrait être fx.df ) avec WtitePage()
				
				//
				page = new PageID(x,0);
				found = true;
				return page; //retourne un PageID (ici normalement PageID(x,0))
			}
			
			//Si le fichier a de l'espace disponible
			else if(file.length() < DBParams.PageSize * DBParams.maxPagesPerFile) {
				
				if(file.length()==0) {  				//on vérifie si le fichier est vide ( ne devrait pas arriver normalement )
					page = new PageID(x,0);
				}
				
				for(int i = 1;i<DBParams.maxPagesPerFile;i++) {
					if((file.length() / DBParams.PageSize) < i) {  				//On essaye de trouver de la première page libre si elle existe
						page = new PageID(x,i);
						break;
					}
				}
				//Allouer la page PageID(x,i) ici avec WritePage()
				
				//
				found = true;
			}
			
			//Si fx.df ne convient pas, on cherche pour x+1;
			x++;
		}
		
		return page; //Devrait retourner le pageID trouvé dans la boucle précédente
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

	public void ReadPage(PageID pageId, ByteBuffer buff) {
		try {
            RandomAccessFile file = new RandomAccessFile(DBParams.DBPath+"f"+pageId.getFileId()+".df","r");
            int f_byte=DBParams.PageSize*pageId.getPageId();
            file.seek(f_byte);
            file.read(buff.array());
            file.close();
		}catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }catch(IOException e) {
        	System.out.println(e.getMessage());
        }

	}

	public static void WritePage(PageID pageId, ByteBuffer buff) {
		try {
			RandomAccessFile file = new RandomAccessFile("../DB/f"+pageId.getFileId()+".df","rw");
			byte[] array = new byte[buff.remaining()];
			buff.get(array);
			file.seek(pageId.getPageId()*DBParams.PageSize);
			file.write(array);
			file.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("Erreur, le fichier n'a pas pu être trouvé");
		}
		catch(IOException e) {
			System.out.println("Erreur, le fichier n'a pas pu être crée");
		}
	}
}
