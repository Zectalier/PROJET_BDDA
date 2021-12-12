/**
 * Classe qui correspond au Rid (Record Id, donc identifiant) d’un enregistrement. 
 * La classe contient un PageId qui indique la page à laquelle appartient le record et 
 * un slotIdx, un entier, qui est l’indice de la case où le record est stocké.
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class Rid {

	private PageID pageId;
	private int slotIdx;

	/**
	 * Constructeur d'un Rid
	 * @param pageId
	 * @param slotIdx
	 */
	public Rid(PageID pageId, int slotIdx) {
		this.pageId = pageId;
		this.slotIdx = slotIdx;
	}

	/**
	 * Retourne le PageID du Rid
	 * @return PageID
	 */
	public PageID getPageId() {
		return pageId;
	}

	/**
	 * Retourne le slotIdx du Rid
	 * @return
	 */
	public int getSlotIdx() {
		return slotIdx;
	}

	/**
	 * Modifie la PageID du Rid par celle donnée en argument
	 * @param pageId - PageID
	 */
	public void setPageId(PageID pageId) {
		this.pageId = pageId;
	}

	/**
	 * Modifie le slotIdx du Rid par ceui donné en argument
	 * @param slotIdx - int
	 */
	public void setSlotIdx(int slotIdx) {
		this.slotIdx = slotIdx;
	}
}
