import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Classe qui permet de g?rer les fichiers de la SGBD.<p>
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
	 * Cette m?thode lit un PageId de cha?nage depuis une page ? soit-elle la HeaderPage ou une page de donn?es.<br>
	 * Le buffer correspond au contenu de la page, et le boolean first si on lit le premier ou le 2?me des PageIds.
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
	 * Cette m?thode ?crit un PageId de cha?nage dans une page ? soit-elle la HeaderPage ou une page de donn?es.<br>
	 * Le buffer correspond au contenu de la page, et le boolean first si on ?crit le premier ou le 2?me des PageIds.
	 * @param pageId - PageID, le pageId qu'on souhaite ?crire
	 * @param buff - ByteBuffer, le buffer depuis lequel on souhaite ?crire
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
	 * M?thode qui g?re l'allocation d?une nouvelle page via AllocPage du DiskManager, la nouvelle page allou? sera notre nouveau headerPage.
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
	 * M?thode qui rajoute une page de donn?es ? vide ? au Heap File correspondant ? la relation identifi?e par relInfo, et retourner le PageId de cette page.
	 * @param relInfo - RelationInfo
	 * @return PageId
	 */
	public PageID addDataPage(RelationInfo relInfo) {
		PageID pageId = DiskManager.AllocPage();
		PageID headerPage = relInfo.getHeaderPageId();

		ByteBuffer buff = BufferManager.INSTANCE.getPage(headerPage);
		buff.position(0);
		//On récupère les données de la page suivante du headerPage
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

		//nextPage correspond ?  la page qui était ?  l'origine la page libre suivante du headerPage
		//On va changer cette page en mettant le PageID de la nouvelle page inséré comme page précédente sauf si c'est la page de fileID -1 (page factice)
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
	 * Retourne, pour la relation d?sign?e par relInfo, le PageId d?une page de donn?es sur laquelle il reste des cases libres.<br>
	 * Si une telle page n?existe pas, cr??e une nouvelle page et la chaine ? l'headerPage
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
	 * M?thode qui ?crit l?enregistrement record dans la page de donn?es identifi?e par pageId, et retourne son Rid
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
		boolean islast = false; //boolean pour vérifier si le slot trouvé est le dernier libre
		
		int i = 0;
		while(i < slotCount){
			if(buff.get(i+16) == (byte)0 && !found) { //ici 16 car la bytemap débute après les deux int de chaque pageId 
				buff.position(i+16);
				buff.put((byte)1);
				found = true;
				islast = true;
				//16 pour les pageIds, slotCount le nombre de byte dans la bytemap.
				position = 16+slotCount+i*rec.getRelationInfo().getRecordSize();
				slotId = i;
			}
			else if(buff.get(i+16) == (byte)0 && found) { //si on trouve un autre slot ?  0, le slot qu'on a choisi n'est pas le dernier libre, on sort de la boucle
				islast = false;
				break;
			}
			i++;
		}
		
		if(position!=-1) {
			rec.writeToBuffer(buff, position);
			if(islast == true) { //si le slot était le dernier libre, on bouge la page dans la liste des pages remplies
				buff.position(8);
				//Récuperons la page (pas remplie) qui était après la nouvelle page désormais remplie
				int oldNextFileId = buff.getInt();
				int oldNextPageId = buff.getInt();
				PageID oldNextPage = new PageID(oldNextFileId, oldNextPageId);
				//Remplaçons les anciens pointeurs par les nouveaux (la page remplie après l'headerpage)
				buff.position(0);
				buff.putInt(headerPage.getFileId());
				buff.putInt(headerPage.getPageId());
				buff.putInt(nextFileId);
				buff.putInt(nextPageId);
				BufferManager.INSTANCE.freePage(pageId, true);
				//Remplaçons dans le headerPage la page libre suivante par oldNextPage et la page remplie suivante par notre nouvelle page
				buff = BufferManager.INSTANCE.getPage(headerPage);
				buff.position(0);
				buff.putInt(oldNextFileId);
				buff.putInt(oldNextPageId);
				buff.putInt(pageId.getFileId());
				buff.putInt(pageId.getPageId());
				BufferManager.INSTANCE.freePage(headerPage, true);
				//Remplaçons les pointeurs de la page rempli qui était ?  l'origine après la headerpage par ceux de notre nouvelle page remplie
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
			System.out.println("Erreur, pas de slot libre trouvé");
			BufferManager.INSTANCE.freePage(pageId, false);
		}
		return new Rid(pageId, slotId);
	}
	
	/**
	 * M?thode qui renvoyer la liste des records stock?s dans la page identifi?e par pageId.
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
	 * Insert un record donn? dans la relation
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
	 * M?thode qui supprime les records d'une page respectant la condition donn?e et retourne le nombre de records supprim?
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
	 * M?thode qui supprime tous les records d'une relation respectant la condition donn?e et retourne le nombre de records supprim?
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
	 * M?thode qui permet de mettre ? jour les valeurs de tous les records dans une page qui respectent les conditions donn?es et retourne le nombre de record mis ? jour 
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
	 * M?thode qui permet de mettre ? jour les valeurs de tous les records dans une relation qui respectent les conditions donn?es et retourne le nombre de record mis ? jour 
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