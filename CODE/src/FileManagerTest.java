
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Classe de tests pour le FileManager
 * @author Hu Tony
 *
 */

public class FileManagerTest {

	public static void main(String[] args) {
		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;
		
		DropDBCommand dropDB= new DropDBCommand();
		dropDB.Execute();
		PageID headerpage = FileManager.INSTANCE.createHeaderPage();
		ColInfo colinfo = new ColInfo("test","int");
		ArrayList<ColInfo> al = new ArrayList<ColInfo>();
		al.add(colinfo);
		RelationInfo reltest = new RelationInfo("nomRelation",1,al,headerpage);
		Catalog.INSTANCE.AddRelation(reltest);
		DBManager.DBMANAGER.Finish();
		DBManager.DBMANAGER.Init();
	
		Catalog.INSTANCE.Init();
		ArrayList<RelationInfo> tableauRelInfo = Catalog.INSTANCE.getTableauRelInfo();
		headerpage = tableauRelInfo.get(0).getHeaderPageId();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerpage);
		buff.rewind();
		System.out.println(buff.getInt()==-1);
		System.out.println(buff.getInt()==0);
		System.out.println(buff.getInt()==-1);
		System.out.println(buff.getInt()==0);
		BufferManager.INSTANCE.freePage(headerpage, true);
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
		
		Catalog.INSTANCE.Init();
		tableauRelInfo = Catalog.INSTANCE.getTableauRelInfo();
		PageID pageId = FileManager.INSTANCE.addDataPage(tableauRelInfo.get(0));
		headerpage = tableauRelInfo.get(0).getHeaderPageId();
		buff = BufferManager.INSTANCE.getPage(headerpage);
		buff.rewind();
		PageID nextpageidheader = new PageID(buff.getInt(),buff.getInt());
		BufferManager.INSTANCE.freePage(headerpage, true);
		BufferManager.INSTANCE.flushAll();
		PageID freepage = FileManager.INSTANCE.getFreeDataPage(tableauRelInfo.get(0));
		System.out.println(pageId.equals(nextpageidheader));
		System.out.println(freepage.getFileId()+" "+freepage.getPageId());
		System.out.println(nextpageidheader.getFileId()+" "+nextpageidheader.getPageId());
		System.out.println(freepage.equals(nextpageidheader));
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
		
	}
	
}
