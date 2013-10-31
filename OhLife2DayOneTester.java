
public class OhLife2DayOneTester
{
	public static void main(String[] args)
	{
		OhLife2DayOne myMigrator = new OhLife2DayOne(args[0], args[1], false);
		myMigrator.migrateAllEntries();
	}
}
