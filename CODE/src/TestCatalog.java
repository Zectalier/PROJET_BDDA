import java.util.ArrayList;

public class TestCatalog {
	public static void main(String[]args) {
		DBParams.DBPath = "../DB/";
		DBParams.PageSize = 4096;
		DBParams.maxPagesPerFile = 4;
		DBParams.frameCount = 2;
		
		RelationInfo relInfo = new RelationInfo("test",0,new ArrayList<ColInfo>(),new PageID(0,0));
		
		Catalog.INSTANCE.Init();
		Catalog.INSTANCE.AddRelation(relInfo);
		Catalog.INSTANCE.Finish();
	}
}
