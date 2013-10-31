
public class OhLifeEntry
{
	public int year, month, day;
	public String contents;
	
	public OhLifeEntry()
	{
		year = month = day = -1;
		contents = "";
	}
	
	public String getDateString()
	{
		return String.format("%d-%d-%d", month, day, year);
	}
}
