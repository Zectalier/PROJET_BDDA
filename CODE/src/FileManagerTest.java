import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//IMPORTANT: USE ON EMPTY DATABASE, WILL RESET THE DATABASE
class FileManagerTest {

	@BeforeAll
	public static void setup() {
		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;
		DBManager.DBMANAGER.DropDB();
		PageID headerpage = FileManager.INSTANCE.createHeaderPage();
		ColInfo colinfo = new ColInfo("test","int");
		ArrayList<ColInfo> al = new ArrayList<ColInfo>();
		al.add(colinfo);
		RelationInfo reltest = new RelationInfo("nomRelation",1,al,headerpage);
		Catalog.INSTANCE.AddRelation(reltest);
		DBManager.DBMANAGER.Finish();
		DBManager.DBMANAGER.Init();
	}

	@BeforeEach
	public void beforeEach() {
		Catalog.INSTANCE.Init();
	}
	
	@AfterEach
	public void afterEach() {
		BufferManager.INSTANCE.flushAll();
		Catalog.INSTANCE.Finish();
	}
	/**
	 * Ce test est la pour être sur que notre headerpage est créé avec les bonnes valeurs par défaut pour les pages: (-1,0) (-1,0)
	 */
	@Test
	public void testHeaderPageIsCreated() {
		ArrayList<RelationInfo> tableauRelInfo = Catalog.INSTANCE.getTableauRelInfo();
		PageID headerpage = tableauRelInfo.get(0).getHeaderPageId();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerpage);
		buff.rewind();
		assertEquals(buff.getInt(),-1);
		assertEquals(buff.getInt(),0);
		assertEquals(buff.getInt(),-1);
		assertEquals(buff.getInt(),0);
		BufferManager.INSTANCE.freePage(headerpage, true);
	}
	
	/**
	 * Test si la prochaine page du headerPage après création d'une page libre est bien celle qui a été créé après
	 */
	@Test
	public void addDataPageTest() {
		ArrayList<RelationInfo> tableauRelInfo = Catalog.INSTANCE.getTableauRelInfo();
		PageID pageId = FileManager.INSTANCE.addDataPage(tableauRelInfo.get(0));
		PageID headerpage = tableauRelInfo.get(0).getHeaderPageId();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerpage);
		buff.rewind();
		PageID nextpageidheader = new PageID(buff.getInt(),buff.getInt());
		BufferManager.INSTANCE.freePage(headerpage, true);
		BufferManager.INSTANCE.flushAll();
		PageID freepage = FileManager.INSTANCE.getFreeDataPage(tableauRelInfo.get(0));
		assertEquals(pageId,nextpageidheader);
		System.out.println(freepage.getFileId()+" "+freepage.getPageId());
		System.out.println(nextpageidheader.getFileId()+" "+nextpageidheader.getPageId());
		assertEquals(freepage,nextpageidheader);
	}
	
	@Test
	public void fullDataPage() {
		
	}
}
