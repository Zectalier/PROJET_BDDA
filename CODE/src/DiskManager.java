import java.nio.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Classe qui permet d'allouer de nouvelles pages dans des fichiers sur le disque 
 * ainsi que de lire et écrire des pages dans ces fichiers du disque.<p>
 * Elle comporte une seule et unique instance.
 * @author Hu Tony
 *
 */
public enum DiskManager {

	DISKMANAGER;
	
	/**
	 * Alloue une page dans le disque en écrivant une nouvelle page "vide" dans un fichier qui possède encore de la place ou sinon dans un nouveau fichier
	 * @return PageID de la page qui vient d'être alloué
	 */
	public static PageID AllocPage() {

		boolean found = false;
		int x = 0;
		String filename;
		File file;
		PageID page = new PageID();

		// CrÃ©ation du buffer Ã  mettre dans la page lors de l'allocation
		ByteBuffer buffer = ByteBuffer.allocate(DBParams.PageSize);
		for (int i = 0; i < DBParams.PageSize; i++) {
			buffer.put(i, (byte) 0);
		}

		// Recherche d'un fichier non plein
		while (!found) {
			filename = String.format(DBParams.DBPath + "f%d.df", x);
			file = new File(filename);

			// CrÃ©ation du fichier si on ne trouve pas de fichier ayant de la place
			if (!file.exists()) {

				// Creer le nouveau fichier fx.df ici
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.out.println("Erreur, le fichier n'a pas pu Ãªtre crÃ©e");
					e.getMessage();
				}

				// Allouer la page PageID(x,0) au nouveau fichier ( qui devrait Ãªtre fx.df )
				// avec WritePage()
				page = new PageID(x, 0);
				WritePage(page, buffer);
				found = true;
				return page;
			}

			// Si le fichier a de l'espace disponible
			else if (file.length() < DBParams.PageSize * DBParams.maxPagesPerFile) {
				int n = (int) file.length() / DBParams.PageSize;
				page = new PageID(x, n);
				WritePage(page, buffer);
				found = true;
			}
			x++;
		}
		return page; // Devrait retourner le pageID trouvÃ© si un fichier non vide existe dans la
		// boucle prÃ©cÃ©dente
	}

	/**
	 * Rempli le ByteBuffer donné en argument par le contenu de la page identifié par la PageID pageId
	 * @param pageId le PageID de la page
	 * @param buff le ByteBuffer qu'on souhaite remplir
	 */
	public static void ReadPage(PageID pageId, ByteBuffer buff) {
		try {
			RandomAccessFile file = new RandomAccessFile(DBParams.DBPath + "f" + pageId.getFileId() + ".df", "r");
			int f_byte = DBParams.PageSize * pageId.getPageId();
			file.seek(f_byte);
			file.read(buff.array());
			file.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Ecrit dans la page identifié par la PageID pageId le contenu du ByteBuffer buff
	 * @param pageId la PageID de la page
	 * @param buff le ByteBuffer qui contient le nouveau contenu de la page
	 */
	public static void WritePage(PageID pageId, ByteBuffer buff) {
		try {
			RandomAccessFile file = new RandomAccessFile(DBParams.DBPath + "f" + pageId.getFileId() + ".df", "rw");
			buff.rewind();
			byte[] array = new byte[buff.remaining()];
			buff.get(array);
			file.seek(pageId.getPageId() * DBParams.PageSize);
			file.write(array);
			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("Erreur, le fichier n'a pas pu Ãªtre trouvÃ©");
		} catch (IOException e) {
			System.out.println("Erreur, IN/OUT Exception");
		}
	}
}
