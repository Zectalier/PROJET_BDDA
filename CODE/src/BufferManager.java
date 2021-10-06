import java.nio.*;
import java.util.ArrayList;

public enum BufferManager {

	INSTANCE;
	
	private ArrayList<Frame> listFrame;
	
	private BufferManager(){
		ArrayList<Frame> liste = new ArrayList<Frame>();
		for(int i = 0;i<DBParams.frameCount;i++) {
			liste.add(new Frame());
		}
	}
	
	public BufferManager getInstance() {
        return INSTANCE;
    }
	
	public ByteBuffer GetPage(PageID page) {
		
		Frame temp = new Frame();
		int indexmru = DBParams.frameCount;
		
		for(int i = listFrame.size(); i<listFrame.size();i++) {
			//Vérifie si le PageId de la Frame est la même que page et si oui incrémente son PinCount et le met en dernière position de la liste
			if(listFrame.get(i).getPageId() == page) {
				temp = listFrame.get(i);
				temp.setPinCount(temp.getPinCount() + 1);
				listFrame.remove(i);
				listFrame.add(temp);
				return temp.getBuffer();
			}
			
			//Sinon, vérifie si la frame à listFrame[i] est la dernière frame MRU qui à un PinCount à 0
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
			return listFrame.get(DBParams.frameCount).getBuffer();
		}
		//Sinon retirer la frame MRU et rajouter une nouvelle Frame(page) en fin de liste
		else {
			listFrame.remove(indexmru);
			listFrame.add(new Frame(page));
			return listFrame.get(DBParams.frameCount).getBuffer();
		}
	}
	
	public void FreePage(PageID page, boolean valdirty) {
		for(int i = listFrame.size(); i<listFrame.size();i++) {
			if(listFrame.get(i).getPageId() == page) {
				Frame temp = listFrame.get(i);
				temp.setPinCount(temp.getPinCount() - 1);
				temp.setDirty(valdirty);
				listFrame.remove(i);
				listFrame.add(temp);
				return;
			}
		}
		System.out.println("Erreur, page non trouvé dans le BufferManager");
		return;
	}
	
	public void FlushAll() {
		for(int i = listFrame.size(); i<listFrame.size();i++) {
			if(listFrame.get(i).isDirty() == true) {
				DiskManager.WritePage(listFrame.get(i).getPageId(), listFrame.get(i).getBuffer());
			}
		}
		listFrame.clear();
		for(int i = 0;i<DBParams.frameCount;i++) {
			listFrame.add(new Frame());
		}
	}
}
