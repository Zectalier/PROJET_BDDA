public class Rid {
	
	private int pageId;
	private int slotIdx;
	
	public Rid(int pageId, int slotIdx) {
		this.pageId = pageId;
		this.slotIdx = slotIdx;
	}
	
	public int getPageId() {
		return pageId;
	}
	
	public int getSlotIdx() {
		return slotIdx;
	}
}
