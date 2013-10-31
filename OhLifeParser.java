import java.io.*;
import java.util.regex.*;

public class OhLifeParser
{
	private String inPath;
	
	private BufferedReader in;
	// Maximum number of characters per line supported. Increase to read longer lines by using more memory.
	private final int maxLineLength = 1024;
	
	private Pattern datePattern;
	// Parens in the regex indicate "groups" which can be parsed into primitives
	private final String dateRegex = "(\\d{4})-(\\d{2})-(\\d{2})";
	
	public OhLifeParser(String ohLifeEntriesPath) throws FileNotFoundException
	{
		inPath = ohLifeEntriesPath;
		// http://stackoverflow.com/questions/858980/file-to-byte-in-java
		// open file as read and write so others can't write to the file while we read it
		try
		{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(inPath), "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		datePattern = Pattern.compile(dateRegex);
	}
	
	public OhLifeEntry getNextEntry()
	{
		OhLifeEntry entry = new OhLifeEntry();
		String currentLine = "";
		// Number of OhLife date markers found (format YYYY-MM-DD)
		int dateMarkersFound = 0;
		
		// While we haven't reached the date marker of the subsequent entry
		while (dateMarkersFound < 2)
		{
			try
			{
				// Mark the beginning of this line so we can return here if this line is the date marker of the subsequent entry
				// Internally, BufferedReader starts keeping a buffer of length maxLineLength to store subsequent characters
				in.mark(maxLineLength);
				
				// BufferedReader will remove \n and \r for us
				currentLine = in.readLine();
				
				if (currentLine == null)
				{
					switch (dateMarkersFound)
					{
					case 1:
						// We reached the last entry
						return entry;
					case 0:
						// We already passed the last entry but the user requested another entry
						return null;
					}
				}
				
				// If this is a date marker
				Matcher m = datePattern.matcher(currentLine);
				if (m.matches())
				{
					dateMarkersFound++;
					// If this is the date marker before the entry we want
					if (dateMarkersFound == 1)
					{
						// parse date marker
						entry.year = Integer.parseInt(m.group(1));
						entry.month = Integer.parseInt(m.group(2));
						entry.day = Integer.parseInt(m.group(3));
						
						// Skip the next line because it's blank and we don't want to include it
						in.readLine();
					}
				}
				// If this is part of the entry we want
				else if (dateMarkersFound == 1)
				{
					// Note: BufferedReader removes trailing \n and \r when it gives us currentLine
					entry.contents += (currentLine + "\n");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		// Get rid of leading and trailing whitespace
		entry.contents = entry.contents.trim();
		
		try
		{
			// Return the BufferedReader's position to the date marker of the next entry
			in.reset();
		}
		catch (IOException e)
		{
			// Somebody REALLY hates paragraphs
			System.out.printf("Error: Encountered a line longer than %d characters. Try increasing maxLineLength.", maxLineLength);
			e.printStackTrace();
		}
		
		
		return entry;
	}
}
