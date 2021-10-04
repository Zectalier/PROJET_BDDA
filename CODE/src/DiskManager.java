import java.nio.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskManager {

	public static PageID AllocPage() {
		
		boolean found = false;
		int x = 0;
		String filename;
		File file;
		PageID page = new PageID();
		
		//Création du buffer à mettre dans la page lors de l'allocation
		ByteBuffer buffer = ByteBuffer.allocate(DBParams.PageSize);
		for(int i = 0; i<DBParams.PageSize;i++) {
			buffer.put(i,(byte)0);
		}
		
		//Recherche d'un fichier non plein
		while(!found) {
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
				
				//Allouer la page PageID(x,0) au nouveau fichier ( qui devrait être fx.df ) avec WritePage()
				page = new PageID(x,0);
				WritePage(page,buffer);
				found = true;
				return page;
			}
			
			//Si le fichier a de l'espace disponible
			else if(file.length() < DBParams.PageSize * DBParams.maxPagesPerFile) {
				int n = (int)file.length() / DBParams.PageSize;
				page = new PageID(x,n);
				WritePage(page,buffer);
				found = true;
			}
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
