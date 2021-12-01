public enum DBManager {
	DBManager;
	
	public static void Finish() {
		// TODO Auto-generated method stub
		Catalog.INSTANCE.Init();
	}

	public static void Init() {
		// TODO Auto-generated method stub
		Catalog.INSTANCE.Finish();
	}

	public static void ProcessCommand(String reponse) {
		String[] chaine = reponse.split(" ()");
		switch(chaine[0]){
			case "CREATE":
				CreateRelationCommand create = new CreateRelationCommand(reponse);
				create.Execute();
				break;
		}

	}

}
