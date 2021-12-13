import java.nio.*;

/**
 * Classe de tests pour le DiskManager
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class DiskManagerTests {

	public static void main(String[] args) {

		// Tests à faire si possible sur un dossier DB vide

		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;

		PageID page = new PageID();

		// Test allocation nouvelle page
		page = DiskManager.AllocPage();

		// Test lecture de la page
		ByteBuffer buffer = ByteBuffer.allocate(DBParams.PageSize);
		System.out.println("Lecture de la page 1:");
		DiskManager.ReadPage(page, buffer);
		for (int i = 0; i < buffer.capacity(); i++) {
			System.out.print(buffer.array()[i]);
		}
		byte[] array = new byte[buffer.remaining()];
		buffer.get(array);
		// System.out.println(Arrays.toString(array));

		// Test allocation d'une nouvelle page dans le meme fichier que page
		PageID page2 = new PageID();
		page2 = DiskManager.AllocPage();

		// Test allocation d'une troisième page dans le même fichier que page et page2
		PageID page3 = new PageID();
		page3 = DiskManager.AllocPage();

		// Test allocation d'une quatrième page dans le même fichier que page, page2
		// et page3
		PageID page4 = new PageID();
		page4 = DiskManager.AllocPage();

		// Test allocation d'une page qui devra créer un nouveau fichier pour stocker
		// la page
		PageID page5 = new PageID();
		page5 = DiskManager.AllocPage();

		ByteBuffer buffer1 = ByteBuffer.allocate(DBParams.PageSize);
		for (int i = 0; i < DBParams.PageSize; i++) {
			buffer1.put(i, (byte) 1);
		}

		ByteBuffer buffer2 = ByteBuffer.allocate(DBParams.PageSize);
		// Test écriture de la page2 avec un tableau de byte rempli de 1
		DiskManager.WritePage(page2, buffer1);
		// Test lecture de la page 2 modifiée
		System.out.println("");
		System.out.println("Lecture de la page 2: ");
		DiskManager.ReadPage(page2, buffer2);
		for (int i = 0; i < buffer2.capacity(); i++) {
			System.out.print(buffer2.array()[i]);
		}

	}
}