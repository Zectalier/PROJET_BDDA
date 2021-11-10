import java.nio.*;

public class Frame {

	private ByteBuffer buffer;
	private PageID pageId;
	private int pinCount;
	private boolean dirty;

	public Frame(PageID pageId) {
		this.pinCount = 1;
		this.dirty = false;
		this.pageId = pageId;
		buffer = ByteBuffer.allocate(DBParams.PageSize);
		DiskManager.ReadPage(pageId,buffer);
	}

	public Frame() {
		this.pinCount = 0;
		this.dirty = false;
		this.pageId = null;
		buffer = null;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public PageID getPageId() {
		return pageId;
	}

	public void setPageId(PageID pageId) {
		this.pageId = pageId;
	}

	public int getPinCount() {
		return pinCount;
	}

	public void setPinCount(int pinCount) {
		this.pinCount = pinCount;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
