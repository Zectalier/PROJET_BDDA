import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Classe qui permet de gérer les fichiers de la SGBD.<p>
 * Elle comporte une seule et unique instance.
 * @author Hu Tony
 *
 */
public enum FileManager {

	INSTANCE;

	/**
	 * Retourne l'instance du FileManager
	 * @return INSTANCE
	 */
	public FileManager getInstance(){
		return INSTANCE;
	}

	/**
	 * Cette méthode lit un PageId de chaînage depuis une page – soit-elle la HeaderPage ou une page de données.<br>
	 * Le buffer correspond au contenu de la page, et le boolean first si on lit le premier ou le 2ème des PageIds.
	 * @param buff - ByteBuffer, le buffer depuis lequel on souhaite lire
	 * @param first - boolean, true si on lit la premiere page, false sinon
	 * @return PageID
	 */
	public PageID readPageIdFromPageBuffer(ByteBuffer buff, boolean first){
		PageID pageId;
		int pageIdx;
		int fileIdx;
		if(first) {
			buff.position(0);
			fileIdx = buff.getInt();
			pageIdx = buff.getInt();
			buff.rewind();
		}
		else {
			buff.position(8);
			fileIdx = buff.getInt();
			pageIdx = buff.getInt();
			buff.rewind();
		}
		pageId = new PageID(fileIdx,pageIdx);
		return pageId;
	}

	/**
	 * Cette méthode écrit un PageId de chaînage dans une page – soit-elle la HeaderPage ou une page de données.<br>
	 * Le buffer correspond au contenu de la page, et le boolean first si on écrit le premier ou le 2ème des PageIds.
	 * @param pageId - PageID, le pageId qu'on souhaite écrire
	 * @param buff - ByteBuffer, le buffer depuis lequel on souhaite écrire
	 * @param first - boolean, true si on lit la premiere page, false sinon
	 */
	public void writePageIdToPageBuffer(PageID pageId,ByteBuffer buff, boolean first){
		if(first) {
			buff.position(0);
			buff.putInt(pageId.getFileId());
			buff.putInt(pageId.getPageId());
			buff.rewind();
		}
		else {
			buff.position(8);
			buff.putInt(pageId.getFileId());
			buff.putInt(pageId.getPageId());
			buff.rewind();
		}
	}

	/**
	 * Méthode qui gère l'allocation d’une nouvelle page via AllocPage du DiskManager, la nouvelle page alloué sera notre nouveau headerPage.
	 * @return
	 */
	public PageID createHeaderPage(){
		PageID pageId = DiskManager.AllocPage();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(pageId);
		writePageIdToPageBuffer(new PageID(-1,0), buff, true);
		writePageIdToPageBuffer(new PageID(-1,0), buff, false);
		BufferManager.INSTANCE.freePage(pageId, true);
		return pageId;
	}

	/**
	 * Méthode qui rajoute une page de données « vide » au Heap File correspondant à la relation identifiée par relInfo, et retourner le PageId de cette page.
	 * @param relInfo - RelationInfo
	 * @return PageId
	 */
	public PageID addDataPage(RelationInfo relInfo) {
		PageID pageId = DiskManager.AllocPage();
		PageID headerPage = relInfo.getHeaderPageId();

		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(0);
		//On rÃ©cupÃ¨re les donnÃ©es de la page suivante du headerPage
		int nextFileId = buff.getInt();
		int nextPageId = buff.getInt();
		buff.position(0);
		buff.putInt(pageId.getFileId());
		buff.putInt(pageId.getPageId());
		BufferManager.INSTANCE.freePage(headerPage, true);

		buff = BufferManager.INSTANCE.getPage(pageId);
		buff.position(0);
		buff.putInt(headerPage.getFileId());
		buff.putInt(headerPage.getPageId());
		buff.putInt(nextFileId);
		buff.putInt(nextPageId);
		BufferManager.INSTANCE.freePage(pageId, true);

		//nextPage correspond Ã  la page qui Ã©tait Ã  l'origine la page libre suivante du headerPage
		//On va changer cette page en mettant le PageID de la nouvelle page insÃ©rÃ© comme page prÃ©cÃ©dente sauf si c'est la page de fileID -1 (page factice)
		if(nextFileId != -1) {
			PageID nextPage = new PageID(nextFileId, nextPageId);
			buff = BufferManager.INSTANCE.getPage(nextPage);
			buff.position(0);
			buff.putInt(pageId.getFileId());
			buff.putInt(pageId.getPageId());
			BufferManager.INSTANCE.freePage(pageId, true);
		}
		return pageId;
	}
	
	/**
	 * Retourne, pour la relation désignée par relInfo, le PageId d’une page de données sur laquelle il reste des cases libres.<br>
	 * Si une telle page n’existe pas, créée une nouvelle page et la chaine à l'headerPage
	 * @param relInfo - RelationInfo
	 * @return PageID
	 */
	public PageID getFreeDataPage(RelationInfo relInfo) {
		PageID headerPage = relInfo.getHeaderPageId();
		PageID freePageId;
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.rewind();
		int nextFileId = buff.getInt();
		int nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		if(nextFileId == -1) {
			freePageId = addDataPage(relInfo);
		}
		else {
			freePageId = new PageID(nextFileId, nextPageId);
		}
		return freePageId;
	}

	/**
	 * Méthode qui écrit l’enregistrement record dans la page de données identifiée par pageId, et retourne son Rid
	 * @param relinfo - RelationInfo
	 * @param rec - Record
	 * @param pageId - PageID
	 * @return Rid
	 */
	public Rid writeRecordToDataPage (RelationInfo relinfo, Record rec, PageID pageId) {
		PageID headerPage = rec.getRelationInfo().getHeaderPageId();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(8);
		int nextFileId = buff.getInt(); //nextFileId, fileid de la prochaine page remplie
		int nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		PageID nextPage = new PageID(nextFileId, nextPageId);
		buff = BufferManager.INSTANCE.getPage(pageId);
		int slotCount = rec.getRelationInfo().getSlotCount();
		int position = -1;
		int slotId = -1;
		boolean found = false;
		boolean islast = false; //boolean pour vÃ©rifier si le slot trouvÃ© est le dernier libre
		
		int i = 0;
		while(i < slotCount){
			if(buff.get(i+16) == (byte)0 && !found) { //ici 16 car la bytemap dÃ©bute aprÃ¨s les deux int de chaque pageId 
				buff.position(i+16);
				buff.put((byte)1);
				found = true;
				islast = true;
				//16 pour les pageIds, slotCount le nombre de byte dans la bytemap.
				position = 16+slotCount+i*rec.getRelationInfo().getRecordSize();
				slotId = i;
			}
			else if(buff.get(i+16) == (byte)0 && found) { //si on trouve un autre slot Ã  0, le slot qu'on a choisi n'est pas le dernier libre, on sort de la boucle
				islast = false;
				break;
			}
			i++;
		}
		
		if(position!=-1) {
			rec.writeToBuffer(buff, position);
			if(islast == true) { //si le slot Ã©tait le dernier libre, on bouge la page dans la liste des pages remplies
				buff.position(8);
				//RÃ©cuperons la page (pas remplie) qui Ã©tait aprÃ¨s la nouvelle page dÃ©sormais remplie
				int oldNextFileId = buff.getInt();
				int oldNextPageId = buff.getInt();
				PageID oldNextPage = new PageID(oldNextFileId, oldNextPageId);
				//RemplaÃ§ons les anciens pointeurs par les nouveaux (la page remplie aprÃ¨s l'headerpage)
				buff.position(0);
				buff.putInt(headerPage.getFileId());
				buff.putInt(headerPage.getPageId());
				buff.putInt(nextFileId);
				buff.putInt(nextPageId);
				BufferManager.INSTANCE.freePage(pageId, true);
				//RemplaÃ§ons dans le headerPage la page libre suivante par oldNextPage et la page remplie suivante par notre nouvelle page
				buff = BufferManager.INSTANCE.getPage(headerPage);
				buff.position(0);
				buff.putInt(oldNextFileId);
				buff.putInt(oldNextPageId);
				buff.putInt(pageId.getFileId());
				buff.putInt(pageId.getPageId());
				BufferManager.INSTANCE.freePage(headerPage, true);
				//RemplaÃ§ons les pointeurs de la page rempli qui Ã©tait Ã  l'origine aprÃ¨s la headerpage par ceux de notre nouvelle page remplie
				if(nextPage.getFileId()!=-1) {
					buff = BufferManager.INSTANCE.getPage(nextPage);
					buff.position(0);
					buff.putInt(pageId.getFileId());
					buff.putInt(pageId.getPageId());
					BufferManager.INSTANCE.freePage(nextPage, true);
				}
				//Il faut raccorder l'ancienne page suivante libre au headerpage
				if(oldNextPage.getFileId()!=-1) {
					buff = BufferManager.INSTANCE.getPage(oldNextPage);
					buff.position(0);
					buff.putInt(headerPage.getFileId());
					buff.putInt(headerPage.getPageId());
					BufferManager.INSTANCE.freePage(oldNextPage, true);
				}
			}
			else {
				BufferManager.INSTANCE.freePage(pageId, true);
			}
		}
		else {
			System.out.println("Erreur, pas de slot libre trouvÃ©");
			BufferManager.INSTANCE.freePage(pageId, false);
		}
		return new Rid(pageId, slotId);
	}
	
	/**
	 * Méthode qui renvoyer la liste des records stockés dans la page identifiée par pageId.
	 * @param relinfo - RelationInfo
	 * @param pageId - PageID
	 * @return ArrayList&lt;Record&gt;
	 */
	public ArrayList<Record> getRecordsInDataPage(RelationInfo relinfo, PageID pageId) {
		ArrayList<Record> listRec = new ArrayList<Record>();
		Record rec;
		ByteBuffer buff = BufferManager.INSTANCE.getPage(pageId);
		for(int position = 16; position<16+relinfo.getSlotCount();position++) {
			rec = new Record(relinfo);
			if(buff.get(position)==(byte)1) {
				rec.readFromBuffer(buff, (position-16)*relinfo.getRecordSize()+relinfo.getSlotCount()+16);
			}
			listRec.add(rec);
		}
		BufferManager.INSTANCE.freePage(pageId, false);
		buff = BufferManager.INSTANCE.getPage(new PageID(0,0));
		BufferManager.INSTANCE.freePage(new PageID(0,0), false);
		return listRec;
	}
	
	/**
	 * Insert un record donné dans la relation
	 * @param relinfo - RelationInfo
	 * @param rec - Record
	 * @return Rid
	 */
	public Rid insertRecordIntoRelation(RelationInfo relinfo, Record rec) {
		PageID freePage = getFreeDataPage(relinfo);
		return writeRecordToDataPage(relinfo,rec,freePage);
	}
	
	/**
	 * Retourne sous une ArrayList&lt;Record&gt; tous les records pour une RelationInfo
	 * @param relinfo - RelationInfo
	 * @return ArrayList&lt;Record&gt;
	 */
	public ArrayList<Record> getAllRecords(RelationInfo relinfo) {
		ArrayList<Record> listRec = new ArrayList<Record>();
		PageID headerPage = relinfo.getHeaderPageId();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(0);
		int nextFileId = buff.getInt();
		int nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		PageID nextPageID = new PageID(nextFileId, nextPageId);
		if(nextFileId!=-1) {
			listRec.addAll(getRecordsInDataPage(relinfo,nextPageID));
		}
		while(nextFileId!=-1) {
			buff = BufferManager.INSTANCE.getPage(nextPageID);
			buff.position(8);
			nextFileId = buff.getInt();
			nextPageId = buff.getInt();
			BufferManager.INSTANCE.freePage(nextPageID, false);
			nextPageID = new PageID(nextFileId, nextPageId);
			if(nextFileId!=-1) {
				listRec.addAll(getRecordsInDataPage(relinfo,nextPageID));
			}
		}
		buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(8);
		nextFileId = buff.getInt();
		nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		nextPageID = new PageID(nextFileId, nextPageId);
		if(nextFileId!=-1) {
			listRec.addAll(getRecordsInDataPage(relinfo,nextPageID));
		}
		while(nextFileId!=-1) {
			buff = BufferManager.INSTANCE.getPage(nextPageID);
			buff.position(8);
			nextFileId = buff.getInt();
			nextPageId = buff.getInt();
			BufferManager.INSTANCE.freePage(nextPageID, false);
			nextPageID = new PageID(nextFileId, nextPageId);
			if(nextFileId!=-1) {
				listRec.addAll(getRecordsInDataPage(relinfo,nextPageID));
			}
		}
		return listRec;
	}
	
	/**
	 * Méthode qui supprime les records d'une page respectant la condition donnée et retourne le nombre de records supprimé
	 * @param relInfo - RelationInfo
	 * @param condition - ArrayList&lt;String&gt;
	 * @param pageId - PageID
	 * @return int
	 */
	public int deleteRecordInDataPage(RelationInfo relInfo, ArrayList<String> condition, PageID pageId) {
		int compteur = 0;
		Record rec;
		ArrayList<String> emptyValuesArrayList = new ArrayList<String>();
		String str;
		for(int i = 0;i < relInfo.getNb_col(); i++) {
			if(relInfo.getListe().get(i).getType_col().equals("int") || relInfo.getListe().get(i).getType_col().equals("float")) {
				emptyValuesArrayList.add("0");
			}
			else {
				str = "";
				for(int j = 0; j < Integer.parseInt(relInfo.getListe().get(i).getType_col().substring(6)); j++) {
					str += "0";
				}
				emptyValuesArrayList.add(str);
			}
		}
		ByteBuffer buff = BufferManager.INSTANCE.getPage(pageId);
		for(int position = 16; position<16+relInfo.getSlotCount();position++) {
			rec = new Record(relInfo);
			if(buff.get(position)==(byte)1) {
				rec.readFromBuffer(buff, (position-16)*relInfo.getRecordSize()+relInfo.getSlotCount()+16);
				if(DBManager.DBMANAGER.VerifToutesConditions(condition, rec, relInfo)) {
					compteur++;
					buff.position(position);
					buff.put((byte)0);
					rec.setValues(emptyValuesArrayList);
					rec.writeToBuffer(buff, (position-16)*relInfo.getRecordSize()+relInfo.getSlotCount()+16);
				}
			}
		}
		BufferManager.INSTANCE.freePage(pageId, true);
		return compteur;
	}
	
	/**
	 * Méthode qui supprime tous les records d'une relation respectant la condition donnée et retourne le nombre de records supprimé
	 * @param relInfo - RelationInfo
	 * @param condition - ArrayList&lt;String&gt;
	 * @return
	 */
	public int deleteAllRecords(RelationInfo relInfo, ArrayList<String> condition) {
		int compteur = 0;
		PageID headerPage = relInfo.getHeaderPageId();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(0);
		int nextFileId = buff.getInt();
		int nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		PageID nextPageID = new PageID(nextFileId, nextPageId);
		if(nextFileId!=-1) {
			compteur += deleteRecordInDataPage(relInfo, condition, nextPageID);
		}
		while(nextFileId!=-1) {
			buff = BufferManager.INSTANCE.getPage(nextPageID);
			buff.position(8);
			nextFileId = buff.getInt();
			nextPageId = buff.getInt();
			BufferManager.INSTANCE.freePage(nextPageID, false);
			nextPageID = new PageID(nextFileId, nextPageId);
			if(nextFileId!=-1) {
				compteur += deleteRecordInDataPage(relInfo, condition, nextPageID);
			}
		}
		buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(8);
		nextFileId = buff.getInt();
		nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		nextPageID = new PageID(nextFileId, nextPageId);
		if(nextFileId!=-1) {
			compteur += deleteRecordInDataPage(relInfo, condition, nextPageID);
		}
		while(nextFileId!=-1) {
			buff = BufferManager.INSTANCE.getPage(nextPageID);
			buff.position(8);
			nextFileId = buff.getInt();
			nextPageId = buff.getInt();
			BufferManager.INSTANCE.freePage(nextPageID, false);
			nextPageID = new PageID(nextFileId, nextPageId);
			if(nextFileId!=-1) {
				compteur += deleteRecordInDataPage(relInfo, condition, nextPageID);
			}
		}
		return compteur;
	}
	
	/**
	 * Méthode qui permet de mettre à jour les valeurs de tous les records dans une page qui respectent les conditions données et retourne le nombre de record mis à jour 
	 * @param relInfo - RelationInfo
	 * @param updateTo - ArrayList&lt;String&gt;
	 * @param condition - ArrayList&lt;String&gt;
	 * @param pageId - PageID
	 * @return int
	 */
	public int updateRecordInDataPage(RelationInfo relInfo,ArrayList<String> updateTo, ArrayList<String> condition, PageID pageId) {
		int compteur = 0;
		Record rec;
		ByteBuffer buff = BufferManager.INSTANCE.getPage(pageId);
		for(int position = 16; position<16+relInfo.getSlotCount();position++) {
			rec = new Record(relInfo);
			if(buff.get(position)==(byte)1) {
				rec.readFromBuffer(buff, (position-16)*relInfo.getRecordSize()+relInfo.getSlotCount()+16);
				if(DBManager.DBMANAGER.VerifToutesConditions(condition, rec, relInfo)) {
					compteur++;
					for(int i = 0; i < updateTo.size(); i++) {
						rec.setValueFor(updateTo.get(i).split("=")[0],updateTo.get(i).split("=")[1]);
					}
					rec.writeToBuffer(buff, (position-16)*relInfo.getRecordSize()+relInfo.getSlotCount()+16);
				}
			}
		}
		return compteur;
	}
	
	/**
	 * Méthode qui permet de mettre à jour les valeurs de tous les records dans une relation qui respectent les conditions données et retourne le nombre de record mis à jour 
	 * @param relInfo - RelationInfo
	 * @param updateTo - ArrayList&lt;String&gt;
	 * @param condition - ArrayList&lt;String&gt;
	 * @return int
	 */
	public int updateAllRecords(RelationInfo relInfo,ArrayList<String> updateTo, ArrayList<String> condition) {
		int compteur = 0;
		PageID headerPage = relInfo.getHeaderPageId();
		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(0);
		int nextFileId = buff.getInt();
		int nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		PageID nextPageID = new PageID(nextFileId, nextPageId);
		if(nextFileId!=-1) {
			compteur += updateRecordInDataPage(relInfo, updateTo, condition, nextPageID);
		}
		while(nextFileId!=-1) {
			buff = BufferManager.INSTANCE.getPage(nextPageID);
			buff.position(8);
			nextFileId = buff.getInt();
			nextPageId = buff.getInt();
			BufferManager.INSTANCE.freePage(nextPageID, false);
			nextPageID = new PageID(nextFileId, nextPageId);
			if(nextFileId!=-1) {
				compteur += updateRecordInDataPage(relInfo, updateTo, condition, nextPageID);
			}
		}
		buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(8);
		nextFileId = buff.getInt();
		nextPageId = buff.getInt();
		BufferManager.INSTANCE.freePage(headerPage, false);
		nextPageID = new PageID(nextFileId, nextPageId);
		if(nextFileId!=-1) {
			compteur += updateRecordInDataPage(relInfo, updateTo, condition, nextPageID);
		}
		while(nextFileId!=-1) {
			buff = BufferManager.INSTANCE.getPage(nextPageID);
			buff.position(8);
			nextFileId = buff.getInt();
			nextPageId = buff.getInt();
			BufferManager.INSTANCE.freePage(nextPageID, false);
			nextPageID = new PageID(nextFileId, nextPageId);
			if(nextFileId!=-1) {
				compteur += updateRecordInDataPage(relInfo, updateTo, condition, nextPageID);
			}
		}
		return compteur;
	}
}