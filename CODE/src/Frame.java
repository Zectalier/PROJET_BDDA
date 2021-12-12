import java.nio.*;

/**
 * Classe permettant de représenter une Page dans le BufferManager.<p>
 * Chaque Frame contient un buffer avec le contenu de la page, le PageID de la page, 
 * un pinCount et un flag dirty pour savoir si la Page a été modifiée.
 * @author Hu Tony, SILVA Andrio, CONSTANTINE Benjohnson
 *
 */
public class Frame {

	private ByteBuffer buffer;
	private PageID pageId;
	private int pinCount;
	private boolean dirty;

	/**
	 * Constructeur de la Frame. Initialise le pinCount de la page à 1, le flag dirty à false et lit le contenu de la page dans un buffer.
	 * @param pageId, un PageID
	 */
	public Frame(PageID pageId) {
		this.pinCount = 1;
		this.dirty = false;
		this.pageId = pageId;
		buffer = ByteBuffer.allocate(DBParams.PageSize);
		DiskManager.ReadPage(pageId,buffer);
	}

	/**
	 * Constructeur vide permettant d'initialiser des Frames vides utilisé par le BufferManager lors de son initialisation.
	 */
	public Frame() {
		this.pinCount = 0;
		this.dirty = false;
		this.pageId = null;
		buffer = null;
	}

	/**
	 * Retourne le buffer de la Frame
	 * @return buffer
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Modifie le buffer de la Frame par celui donné en argument
	 * @param buffer
	 */
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * Retourne la PageID de la frame
	 * @return pageId
	 */
	public PageID getPageId() {
		return pageId;
	}

	/**
	 * Modifie le PageID de la Frame par celui donné en argument
	 * @param pageId
	 */
	public void setPageId(PageID pageId) {
		this.pageId = pageId;
	}

	/**
	 * Retourne le pinCount de la frame
	 * @return pinCount
	 */
	public int getPinCount() {
		return pinCount;
	}

	/**
	 * Modifie le pinCount de la Frame par celui donné en argument
	 * @param pageId
	 */
	public void setPinCount(int pinCount) {
		this.pinCount = pinCount;
	}

	/**
	 * Retourne le flag de la Frame
	 * @return true
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Modifie l'état du flag par celui donné en argument
	 * @param dirty sous forme de boolean
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
