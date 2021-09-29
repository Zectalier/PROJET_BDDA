import java.nio.file.Path;
import java.util.Arrays;
import java.io.File;
import java.nio.*;
import java.nio.charset.StandardCharsets;

public class DiskManagerTests {

	public static void main(String[] args) {
		
		//Tests à faire si possible sur un dossier DB vide
		
		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;
		
		PageID page = new PageID();
		
		//Test allocation nouvelle page
		page = DiskManager.AllocPage();
		
		ByteBuffer buffer = ByteBuffer.allocate(DBParams.PageSize);
		DiskManager.ReadPage(page,buffer);
		byte[] array = new byte[buffer.remaining()];
		buffer.get(array);
		System.out.println(Arrays.toString(array));
	}
}
