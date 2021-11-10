
public class Rid {

	private PageID pageId;
	private int slotIdx;

	public Rid(PageID pageId, int slotIdx) {
		this.pageId = pageId;
		this.slotIdx = slotIdx;
	}

	public PageID getPageId() {
		return pageId;
	}

	public int getSlotIdx() {
		return slotIdx;
	}

	public void setPageId(PageID pageId) {
		this.pageId = pageId;
	}

	public void setSlotIdx(int slotIdx) {
		this.slotIdx = slotIdx;
	}
}
