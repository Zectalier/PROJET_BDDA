import java.nio.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DiskManager {

	public PageID AllocPage() {
		
		boolean found = false;
		int x = 0;
		int pageid = 0;
		String filename = String.format("../DB/f%d.df",x);
		File file = new File(filename);
		
		if (!file.exists()) {
			//Creer le fichier f0.df ici
		}
		
		while(found == false) {
			filename = String.format("../DB/f%d.df",x);
			file = new File(filename);
			long fileSize = file.length();
			if(fileSize <= DBParams.PageSize * DBParams.maxPagesPerFile) {
				
			}
		}
	}
	
	public void ReadPage(int pageId,ByteBuffer buff) {
		
	}
	
	public void WritePage(int pageId,ByteBuffer[] buff) {
		
	}
}
