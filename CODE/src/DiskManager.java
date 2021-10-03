import java.nio.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskManager {

	public static PageID AllocPage() {
		
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
			
			//Allouer la page PageID(0,0) au nouveau fichier ( qui devrait être "f0.df" ) avec WritePage()
			page = new PageID(x,0);
			ByteBuffer buffer = ByteBuffer.allocate(DBParams.PageSize);
			for(int i = 0; i<DBParams.PageSize;i++) {
				buffer.put(i,(byte)0);
			}
			WritePage(page,buffer);
			found = true;
			return page; //retourne un PageID (ici normalement PageID(0,0)
		}
		
		//Recherche d'un fichier non plein
		while(found == false) {
			filename = String.format(DBParams.DBPath+"f%d.df",x);
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
				page = new PageID(x,0);
				ByteBuffer buffer = ByteBuffer.allocate(DBParams.PageSize);
				for(int i = 0; i<DBParams.PageSize;i++) {
					buffer.put(i,(byte)0);
				}
				WritePage(page,buffer);
				found = true;
				return page; //retourne un PageID (ici normalement PageID(x,0))
			}
			
			//Si le fichier a de l'espace disponible
			else if(file.length() < DBParams.PageSize * DBParams.maxPagesPerFile) {
				
				if(file.length()==0) {  				//on vérifie si le fichier est vide ( ne devrait pas arriver normalement )
					page = new PageID(x,0);
				}
				
				int n = (int)file.length() / DBParams.PageSize;
				page = new PageID(x,n);
				
				//Allouer la page PageID(x,i) ici avec WritePage()
				ByteBuffer buffer = ByteBuffer.allocate(DBParams.PageSize);
				for(int i = 0; i<DBParams.PageSize;i++) {
					buffer.put(i,(byte)0);
				}
				WritePage(page,buffer);
				found = true;
			}
			//Si fx.df ne convient pas, on cherche pour x+1;
			x++;
		}
		
		return page; //Devrait retourner le pageID trouvé si un fichier non vide existe dans la boucle précédente
	}

	public static void ReadPage(PageID pageId, ByteBuffer buff) {
		try {
            RandomAccessFile file = new RandomAccessFile(DBParams.DBPath+"f"+pageId.getFileId()+".df","r");
            int f_byte=DBParams.PageSize*pageId.getPageId();
            file.seek(f_byte);
            file.read(buff.array());
            for (int i = 0; i < buff.capacity(); i++) {
				System.out.print(buff.array()[i]);
			}
            System.out.println();
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
			System.out.println("Erreur, IN/OUT Exception");
		}
	}
}
