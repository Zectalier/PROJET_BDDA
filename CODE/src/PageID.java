public class PageID {

	private int FileIdx;
	private int PageIdx;

	public PageID(int FileIdx, int PageIdx) {
		this.FileIdx = FileIdx;
		this.PageIdx = PageIdx;
	}

	public PageID() {
	}

	public int getFileId() {
		return this.FileIdx;
	}

	public int getPageId() {
		return this.PageIdx;
	}
}