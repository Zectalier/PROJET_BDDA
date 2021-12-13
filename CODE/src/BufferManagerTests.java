import java.nio.ByteBuffer;

/**
 * Classe de tests pour le BufferManager
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class BufferManagerTests {

	public static void main(String[] args) {

		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;

		PageID page = new PageID();
		page = DiskManager.AllocPage();
		PageID page2 = new PageID();
		page2 = DiskManager.AllocPage();
		PageID page3 = new PageID();
		page3 = DiskManager.AllocPage();

		ByteBuffer buffer1 = ByteBuffer.allocate(DBParams.PageSize);
		for (int i = 0; i < DBParams.PageSize; i++) {
			buffer1.put(i, (byte) 1);
		}

		ByteBuffer buffer2 = ByteBuffer.allocate(DBParams.PageSize);
		for (int i = 0; i < DBParams.PageSize; i += 2) {
			buffer2.put(i, (byte) 1);
			buffer2.put(i + 1, (byte) 0);
		}

		// Test Ã©criture des pages avec les differents buffer
		DiskManager.WritePage(page2, buffer1);
		DiskManager.WritePage(page3, buffer2);

		// Frame de la page page qui devrai avoir un Pin Count à 1
		BufferManager.INSTANCE.getPage(page);
		BufferManager.INSTANCE.getPage(page2);
		BufferManager.INSTANCE.getPage(page);

		// Print contenu buffermanager
		BufferManager.INSTANCE.printAll();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Libere les pages une fois
		BufferManager.INSTANCE.freePage(page2, true);
		BufferManager.INSTANCE.freePage(page, true);

		// Print tout ce qui a de le BufferManager
		BufferManager.INSTANCE.printAll();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Frame de la page2 qui devrai être remplacé car Pin Count à 0
		BufferManager.INSTANCE.getPage(page3);
		BufferManager.INSTANCE.printAll();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Libere les pages une fois afin de tester la politique MRU
		BufferManager.INSTANCE.freePage(page, true);
		BufferManager.INSTANCE.freePage(page3, true);

		// Frame de la page3 qui devrai être remplacé car MRU
		BufferManager.INSTANCE.getPage(page2);
		BufferManager.INSTANCE.printAll();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ByteBuffer trash = ByteBuffer.allocate(DBParams.PageSize);
		for (int i = 0; i < DBParams.PageSize; i++) {
			trash.put(i, (byte) 1);
		}
		DiskManager.WritePage(page, trash);
		DiskManager.WritePage(page2, trash);
		DiskManager.WritePage(page3, trash);
		DiskManager.ReadPage(page, buffer1);
		for (int i = 0; i < buffer1.capacity(); i++) {
			System.out.print(buffer1.array()[i]);
		}
		DiskManager.ReadPage(page2, buffer1);
		for (int i = 0; i < buffer1.capacity(); i++) {
			System.out.print(buffer1.array()[i]);
		}
		DiskManager.ReadPage(page3, buffer1);
		for (int i = 0; i < buffer1.capacity(); i++) {
			System.out.print(buffer1.array()[i]);
		}
		BufferManager.INSTANCE.flushAll();
		DiskManager.ReadPage(page, buffer1);
		for (int i = 0; i < buffer1.capacity(); i++) {
			System.out.print(buffer1.array()[i]);
		}
		DiskManager.ReadPage(page2, buffer1);
		for (int i = 0; i < buffer1.capacity(); i++) {
			System.out.print(buffer1.array()[i]);
		}
		DiskManager.ReadPage(page3, buffer1);
		for (int i = 0; i < buffer1.capacity(); i++) {
			System.out.print(buffer1.array()[i]);
		}
	}
}