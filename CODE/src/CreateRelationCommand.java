import java.util.ArrayList;

/**
 * Classe qui gère la commande CREATE RELATION
 * @author Hu Tony
 * @author CONSTANTINE Benjohnson
 * @author SILVA Andrio
 *
 */
public class CreateRelationCommand {

	private String nomRelation;
	private int nbColonnes;
	private ArrayList<String> listeNoms;
	private ArrayList<String> listeTypes;
	
	/**
	 * Constructeur
	 * @param chaine - String, la commande donnée par l'utilisateur
	 */
	public CreateRelationCommand(String chaine) { //Chaine sous forme: CREATE RELATION NomRelation (NomCol_1:TypeCol_1,NomCol_2:TypeCol_2, ... NomCol_NbCol:TypeCol_NbCol)
		listeNoms = new ArrayList<String>();
		listeTypes = new ArrayList<String>();
		String[] chainelist = chaine.split("\\s+|\\t+"); //Use \\s+|\\t+ to split on spaces even if they are more
		nomRelation = chainelist[2];
		String nomtype = chainelist[3];
		nomtype=nomtype.replace("(","");
		nomtype=nomtype.replace(")","");
		String[] listecolonne = nomtype.split(",");
		nbColonnes = listecolonne.length;
		String[] splittedlistecolonne;
		for(int i = 0; i < listecolonne.length; i++) {
			splittedlistecolonne = listecolonne[i].split(":");
			listeNoms.add(splittedlistecolonne[0]);
			listeTypes.add(splittedlistecolonne[1]);
		}
	}
	
	/**
	 * Methode qui permet d'executer la commande CREATE RELATION
	 */
	public void Execute() {
		PageID headerPage = FileManager.INSTANCE.createHeaderPage();
		ArrayList<ColInfo> listeColInfo = new ArrayList<ColInfo>();
		for(int i = 0; i < listeNoms.size(); i++) {
			listeColInfo.add(new ColInfo(listeNoms.get(i),listeTypes.get(i)));
		}
		RelationInfo relinfo = new RelationInfo(nomRelation,nbColonnes,listeColInfo,headerPage);
		Catalog.INSTANCE.AddRelation(relinfo);
		BufferManager.INSTANCE.flushAll();
	}
	
	
}
