import java.nio.ByteBuffer;

public enum FileManager {

	INSTANCE;

	public FileManager getInstance(){
		return INSTANCE;
	}

	public PageID readPageIdFromPageBuffer(ByteBuffer buff, boolean first){
		PageID pageId;
		int pageIdx;
		int fileIdx;
		buff.rewind();
		if(first) {
			fileIdx = buff.getInt();
			pageIdx = buff.getInt();
		}
		else {
			buff.position(7);
			fileIdx = buff.getInt();
			pageIdx = buff.getInt();
		}
		pageId = new PageID(fileIdx,pageIdx);
		return pageId;
	}

	public void writePageIdToPageBuffer(PageID pageId,ByteBuffer buff, boolean first){
		buff.rewind();
		if(first) {
			buff.putInt(pageId.getFileId());
			buff.putInt(pageId.getPageId());
		}
		else {
			buff.position(7);
			buff.putInt(pageId.getFileId());
			buff.putInt(pageId.getPageId());
		}
	}

	public PageID createHeaderPage(){
		PageID pageId = DiskManager.AllocPage();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(pageId);
		writePageIdToPageBuffer(new PageID(-1,0), buff, true);
		writePageIdToPageBuffer(new PageID(-1,0), buff, false);
		BufferManager.INSTANCE.freePage(pageId, true);
		return pageId;
	}

	public PageID addDataPage(RelationInfo relInfo) {
		PageID pageId = DiskManager.AllocPage();
		PageID headerPage = relInfo.getHeaderPageId();

		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.rewind();
		buff.putInt(pageId.getFileId());
		buff.putInt(pageId.getPageId());
		//On récupère les données de la page suivante du headerPage
		int nextFileId = buff.getInt();
		int nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, true);

		buff = BufferManager.INSTANCE.getPage(pageId);
		buff.rewind();
		buff.putInt(headerPage.getFileId());
		buff.putInt(headerPage.getPageId());
		buff.putInt(nextFileId);
		buff.putInt(nextPageId);
		BufferManager.INSTANCE.freePage(pageId, true);

		//nextPage correspond à la page qui était à l'origine la page suivante du headerPage
		PageID nextPage = new PageID(nextFileId, nextPageId);
		buff = BufferManager.INSTANCE.getPage(nextPage);
		buff.rewind();
		buff.putInt(pageId.getFileId());
		buff.putInt(pageId.getPageId());
		BufferManager.INSTANCE.freePage(pageId, true);

		return pageId;
	}

}
