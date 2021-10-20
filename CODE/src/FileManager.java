import java.nio.ByteBuffer;

public enum FileManager {
	
	INSTANCE;
	
	public PageID readPageIdFromPageBuffer (ByteBuffer buff, boolean first) {
		
	}
	
	public void writePageIdToPageBuffer (PageID pageId,ByteBuffer buff, boolean first) {
		
	}
	
	public PageID createHeaderPage (PageID pageId) {
		
	}
	
	public PageID addDataPage(RelationInfo relInfo) {
		
	}
	
	public PageID getFreeDataPageId() {
		
	}
	
	public Rid writeRecordToDataPage(RelationInfo relInfo, Record record, PageID pageId) {
		
	}
	
	public 
}
