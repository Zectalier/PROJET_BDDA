import java.nio.*;
import java.util.ArrayList;

/**
 * Classe qui permet de g�rer les diff�rents buffer qui vont �tre utilis� par les autres classes.<p>
 * Un BufferManager contient une liste de Frame qui repr�sente chacune le buffer d'une page 
 * et son �tat dans le BufferManager.<p>
 * Elle comporte une seule et unique instance.
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public enum BufferManager {

	INSTANCE;

	/**
	 * Retourne l'instance du BufferManager
	 * @return INSTANCE
	 */
	public BufferManager getInstance() {
		return INSTANCE;
	}

	private ArrayList<Frame> listFrame;

	private BufferManager(){
		ArrayList<Frame> liste = new ArrayList<Frame>();
		for(int i = 0;i<DBParams.frameCount;i++) {
			liste.add(new Frame());
		}
		listFrame = liste;
	}

	/**
	 * Retourne le ByteBuffer compris dans la Frame de la page si celle-ci est d�j� dans le BufferManager et incr�mente son pinCount, 
	 * sinon tente de retirer la Frame utilis�e la plus r�cente du BufferManager 
	 * tout en actualisant ses donn�es sur le disque si celui-ci est dirty 
	 * , la remplace par une nouvelle Frame repr�sentant la page entr�e en argument et retourne son ByteBuffer.
	 * @param page - le PageID de la page
	 * @return ByteBuffer
	 */
	public ByteBuffer getPage(PageID page) {

		Frame temp = new Frame();
		int indexmru = DBParams.frameCount-1;

		for(int i = 0; i<listFrame.size();i++) {
			//Vérifie si le PageId de la Frame est la même que page et si oui incrémente son PinCount et le met en dernière position de la liste
			if(listFrame.get(i).getPageId()!=null) {
				if(listFrame.get(i).getPageId().equals(page)) {
					temp = listFrame.get(i);
					temp.setPinCount(temp.getPinCount() + 1);
					listFrame.remove(i);
					listFrame.add(temp);
					return temp.getBuffer();
				}
			}
			//Sinon, vérifie si la frame �  listFrame[i] est la dernière frame MRU qui �  un PinCount �  0
			else {
				if(listFrame.get(i).getPinCount() == 0) {
					indexmru = i;
				}
			}
		}
		//Si on a une frame "vide", la supprimer et mettre une nouvelle Frame avec la page souhaité en fin de liste
		if(listFrame.get(0).getPageId() == null) {
			listFrame.remove(0);
			listFrame.add(new Frame(page));
			return listFrame.get(DBParams.frameCount-1).getBuffer();
		}
		//Sinon retirer la frame MRU et rajouter une nouvelle Frame(page) en fin de liste
		else {
			if(listFrame.get(indexmru).getPinCount()!=0) {
				System.out.println("ERREUR! Pas de frame disponible");
				return temp.getBuffer();
			}
			else {
				//Si la frame MRU a retir� est dirty, actualise ses donn�es dans la page de donn�es
				if(listFrame.get(indexmru).isDirty() == true) {
					DiskManager.WritePage(listFrame.get(indexmru).getPageId(), listFrame.get(indexmru).getBuffer());
				}
				listFrame.remove(indexmru);
				listFrame.add(new Frame(page));
				return listFrame.get(DBParams.frameCount-1).getBuffer();
			}
		}
	}

	/**
	 * Lib�re la page entr�e en argument en desincr�mentant son pinCount et actualise le flag dirty de la page par celui donn� en argument
	 * @param page - le PageID de la page
	 * @param valdirty - le flag de la page
	 */
	public void freePage(PageID page, boolean valdirty) {
		for(int i = 0; i<listFrame.size();i++) {
			if(listFrame.get(i).getPageId()!=null) {
				if(listFrame.get(i).getPageId().equals(page)) {
					if(listFrame.get(i).getPinCount() == 0) {
						System.out.println("Attention, pin count � 0");
						return;
					}
					else {
						Frame temp = listFrame.get(i);
						temp.setPinCount(temp.getPinCount() - 1);
						temp.setDirty(valdirty);
						listFrame.remove(temp);
						listFrame.add(temp);
						return;
					}
				}
			}
		}
		System.out.println("Erreur, page non trouvé dans le BufferManager");
		return;
	}

	/**
	 * Ecrit toute les pages dans le disque ayant un flag dirty true et remet � z�ro toute les frames du BufferManager.
	 */
	public void flushAll() {
		for(int i = 0; i<listFrame.size();i++) {
			if(listFrame.get(i).isDirty() == true) {
				DiskManager.WritePage(listFrame.get(i).getPageId(), listFrame.get(i).getBuffer());
			}
		}
		listFrame.clear();
		for(int i = 0;i<DBParams.frameCount;i++) {
			listFrame.add(new Frame());
		}
	}

	/**
	 * Remet � z�ro toute les frames du BufferManager.
	 */
	public void reset() {
		ArrayList<Frame> liste = new ArrayList<Frame>();
		for(int i = 0;i<DBParams.frameCount;i++) {
			liste.add(new Frame());
		}
		listFrame = liste;
	}
	
	/**
	 * M�thode qui permet d'afficher le contenu du BufferManager.
	 */
	public void printAll() {
		System.out.println("//////////////////////////////////////////////////////");
		for(int i = 0; i<listFrame.size();i++) {
			System.out.println("Frame "+i);
			System.out.println("("+listFrame.get(i).getPageId().getFileId()+","+listFrame.get(i).getPageId().getPageId()+")");
			System.out.println("Pin Count: "+listFrame.get(i).getPinCount());
			System.out.println("Is dirty ? "+listFrame.get(i).isDirty());
			System.out.print("Contenu du buffer :");
			for (int j = 0; j < listFrame.get(i).getBuffer().capacity(); j++) {
				System.out.print(listFrame.get(i).getBuffer().array()[j]);
			}
			System.out.println("");
			System.out.println("");
		}
		System.out.println("//////////////////////////////////////////////////////");
	}
}