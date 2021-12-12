import java.util.Scanner;

public class Menu {
	//CREATE RELATION dab (A:int,B:string4,C:int)
	//BATCHINSERT INTO dab FROM FILE R1.csv
	//SELECTMONO * FROM dab WHERE A>680 AND B=Conv
	
	//DROPDB
	//CREATE RELATION R (C1:int,C2:string3,C3:int)
	//INSERT INTO R RECORD (1,aab,2)
	//INSERT INTO R RECORD (2,abc,2)
	//INSERT INTO R RECORD (1,agh,1)
	//SELECTMONO * FROM R
	//SELECTMONO * FROM R WHERE C1=1
	//SELECTMONO * FROM R WHERE C3=1
	//SELECTMONO * FROM R WHERE C1=1 AND C3=2
	//SELECTMONO * FROM R WHERE C1<2
	//DELETE FROM R WHERE C3=2
	//SELECTMONO * FROM R
	//CREATE RELATION S (C1:string2,C2:int,C3:string4,C4:float,C5:string5,C6:int,C7:int,C8:int)
	//BATCHINSERT INTO S FROM FILE S1.csv
	//SELECTMONO * FROM S
	//SELECTMONO * FROM S WHERE C2=19
	//SELECTMONO * FROM S WHERE C3=Nati
	
	/*
	DROPDB
	CREATE RELATION R (C1:int,C2:string3,C3:int)
	INSERT INTO R RECORD (1,aab,2)
	INSERT INTO R RECORD (2,abc,2)
	INSERT INTO R RECORD (1,agh,1)
	UPDATE R SET C2=rrr WHERE C3>=2
	(message attendu : deux tuples actualisés )
	SELECTMONO * FROM R WHERE BB=rrr
	(résultat attendu : deux tuples : (1,rrr,2) et (2,rrr,2) )*/
	public static void menuCommande() {
		boolean end = false;
		Scanner scan;
		do {
			scan= new Scanner(System.in);
			String reponse = scan.nextLine();
			switch(reponse){
				case "EXIT":
					ExitCommand exit = new ExitCommand();
					exit.Execute();
					end=true;
					break;
				default: 
					DBManager.DBMANAGER.ProcessCommand(reponse);
					break;
			}	
		}while (!end);
		scan.close();
	}
}
