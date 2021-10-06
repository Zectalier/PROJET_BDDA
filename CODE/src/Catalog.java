import java.util.ArrayList;

public class Catalog {
	
	private ArrayList<RelationInfo> tableau_rel_info;
	private int compteur;
	
	public Catalog(ArrayList<RelationInfo>tab, int c) {
		ArrayList<RelationInfo> tableau_rel_info = tab;
		compteur = c;
	}
	
	public void Init() {
		
	}
	
	public void Finish() {
		
	}
	
	public void AddRelation(RelationInfo liste) {
		tableau_rel_info.add(liste);
	}
}
