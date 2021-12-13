import java.nio.*;
import java.util.ArrayList;

/**
 * Classe qui permet de gérer les différents buffer qui vont être utilisé par les autres classes.<p>
 * Un BufferManager contient une liste de Frame qui représente chacune le buffer d'une page 
 * et son état dans le BufferManager.<p>
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
	 * Retourne le ByteBuffer compris dans la Frame de la page si celle-ci est déjà dans le BufferManager et incrémente son pinCount, 
	 * sinon tente de retirer la Frame utilisée la plus récente du BufferManager 
	 * tout en actualisant ses données sur le disque si celui-ci est dirty 
	 * , la remplace par une nouvelle Frame représentant la page entrée en argument et retourne son ByteBuffer.
	 * @param page - le PageID de la page
	 * @return ByteBuffer
	 */
	public ByteBuffer getPage(PageID page) {

		Frame temp = new Frame();
		int indexmru = DBParams.frameCount-1;

		for(int i = 0; i<listFrame.size();i++) {
			//VÃ©rifie si le PageId de la Frame est la mÃªme que page et si oui incrÃ©mente son PinCount et le met en derniÃ¨re position de la liste
			if(listFrame.get(i).getPageId()!=null) {
				if(listFrame.get(i).getPageId().equals(page)) {
					temp = listFrame.get(i);
					temp.setPinCount(temp.getPinCount() + 1);
					listFrame.remove(i);
					listFrame.add(temp);
					return temp.getBuffer();
				}
			}
			//Sinon, vÃ©rifie si la frame Ã  listFrame[i] est la derniÃ¨re frame MRU qui Ã  un PinCount Ã  0
			else {
				if(listFrame.get(i).getPinCount() == 0) {
					indexmru = i;
				}
			}
		}
		//Si on a une frame "vide", la supprimer et mettre une nouvelle Frame avec la page souhaitÃ© en fin de liste
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
				//Si la frame MRU a retiré est dirty, actualise ses données dans la page de données
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
	 * Libère la page entrée en argument en desincrémentant son pinCount et actualise le flag dirty de la page par celui donné en argument
	 * @param page - le PageID de la page
	 * @param valdirty - le flag de la page
	 */
	public void freePage(PageID page, boolean valdirty) {
		for(int i = 0; i<listFrame.size();i++) {
			if(listFrame.get(i).getPageId()!=null) {
				if(listFrame.get(i).getPageId().equals(page)) {
					if(listFrame.get(i).getPinCount() == 0) {
						System.out.println("Attention, pin count à 0");
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
		System.out.println("Erreur, page non trouvÃ© dans le BufferManager");
		return;
	}

	/**
	 * Ecrit toute les pages dans le disque ayant un flag dirty true et remet à zéro toute les frames du BufferManager.
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
	 * Remet à zéro toute les frames du BufferManager.
	 */
	public void reset() {
		ArrayList<Frame> liste = new ArrayList<Frame>();
		for(int i = 0;i<DBParams.frameCount;i++) {
			liste.add(new Frame());
		}
		listFrame = liste;
	}
	
	/**
	 * Méthode qui permet d'afficher le contenu du BufferManager.
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