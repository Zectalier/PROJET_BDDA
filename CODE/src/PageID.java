/**
 * Classe qui permet d'identifier une page afin de retrouver le fichier et 
 * l'emplacement du fichier dans laquelle la page se trouve
 * @author Hu Tony
 * 
 */

import java.io.Serializable;

public class PageID implements Serializable{

	private static final long serialVersionUID = 1L;
	private int FileIdx;
	private int PageIdx;

	/**
	 * Constructeur
	 * @param FileIdx
	 * @param PageIdx
	 */
	public PageID(int FileIdx, int PageIdx) {
		this.FileIdx = FileIdx;
		this.PageIdx = PageIdx;
	}

	/**
	 * Constructeur vide
	 */
	public PageID() {
	}

	/**
	 * Retourne le fileId de la page
	 * @return
	 */
	public int getFileId() {
		return this.FileIdx;
	}

	/**
	 * Retourne le pageId de la page
	 * @return
	 */
	public int getPageId() {
		return this.PageIdx;
	}
	
	/**
	 * Check si la page est égal a une autre page, soit qu'elles ont le même pageId et fileId
	 */
	@Override
	public boolean equals(Object o) {
		
		if(o == this) {
            return true;
        }
		
		if(FileIdx==((PageID) o).getFileId()&&PageIdx==((PageID) o).getPageId()) {
			return true;
		}
		
		return false;
	}
}
