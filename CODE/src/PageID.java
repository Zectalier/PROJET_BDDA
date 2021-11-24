import java.io.Serializable;

public class PageID implements Serializable{

	private static final long serialVersionUID = 1L;
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
