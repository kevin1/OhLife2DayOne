import java.io.IOException;
import java.lang.Runtime;
import java.lang.Process;

public class OhLife2DayOne
{
	private String inPath;
	private String fakeEntryTime;
	private boolean isStar;
	
	private OhLifeParser myParser;
	
	public OhLife2DayOne(String ohLifeEntriesPath, String entryTime, boolean markStarred)
	{
		inPath = ohLifeEntriesPath;
		fakeEntryTime = entryTime;
		isStar = markStarred;
		
		try
		{
			myParser = new OhLifeParser(inPath);
		}
		catch (IOException e)
		{
			System.out.println("Problem opening file!");
			e.getMessage();
		}
	}
	
	public int migrateAllEntries()
	{
		// Number of entries we've seen
		int numProcessed = 0;
		OhLifeEntry entry;
		
		// Store the command and arguments for dayone CLI
		String[] shellCommand = new String[4];
		shellCommand[0] = "/usr/local/bin/dayone";
		
		System.out.print("Migrating entries");
		
		while (true)
		{
			entry = myParser.getNextEntry();
			// this is the last entry
			if (entry == null)
			{
				break;
			}
			
			// Construct the arguments from the entry's data
			shellCommand[1] = String.format("-d=\"%d/%d/%d %s\"", entry.month, entry.day, entry.year, fakeEntryTime, isStar ? "true" : "false");
			shellCommand[2] = String.format("-s=%s", isStar ? "true" : "false");
			shellCommand[3] = "new";
			
			try
			{
				addDayOneEntry(shellCommand, entry.contents);
				System.out.print('.');
			}
			catch (Exception e)
			{
				System.out.println("Couldn't run dayone command: " + e.getMessage());
			}
			
			numProcessed++;
		}
		
		System.out.println("done.");
		System.out.printf("Migrated %d entries.\n", numProcessed);
		
		return numProcessed;
	}
	
	// blocking shell execution because dayone shouldn't be run multiple copies
	// https://twitter.com/bdolman/status/382981642538590208
	public static int addDayOneEntry(String[] cmd, String stdin) throws Exception
	{
		// Get the runtime for this Java process
		Runtime rt = Runtime.getRuntime();
		// Run dayone command line utility
		Process p = rt.exec(cmd);
		// Get the stdin of the dayone process
		java.io.OutputStream os = p.getOutputStream();
		// Write our entry
		// REQUIRED to pass "UTF-8" as this tells the String to interpret its contents as UTF-8. 
		// Otherwise, it may return negative values for unicode characters, causing blank entries
		byte[] b = stdin.getBytes("UTF-8");
		os.write(b);
		// Write end of file to tell it we're done
		os.write(0x04);
		os.flush();
		//System.out.println("wrote bytes");
		os.close();
		// Wait for dayone to exit so we don't run multiple copies of it
		p.waitFor();
		
		int exitCode = p.exitValue();
		//System.out.println("Process exited with code = " + exitCode);
		
		return exitCode;
	}
}
