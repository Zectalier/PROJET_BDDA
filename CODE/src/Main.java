public class Main {

	public static void main(String[] args) {

		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;

		DBManager.Init();
		Menu.menuCommande();
		
	}

}
