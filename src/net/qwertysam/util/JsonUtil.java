package net.qwertysam.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil
{
	public static final String INHERITANCE = "MCM-1.12.2-JSON";
	
	public static void formatJsonID(File json, String newID)
	{
		try
		{
			List<String> lines = Files.readAllLines(json.toPath());

			List<String> newLines = new ArrayList<String>();

			boolean done = false;

			for (String line : lines)
			{
				if (!done && line.startsWith("    \"id\": \"1.12.2\""))
				{
					newLines.add("    \"id\": \"" + newID + "\",");
					done = true;
				}
				else
				{
					newLines.add(line);
				}
			}
			FileWriter fw = new FileWriter(json);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String line : newLines)
			{
				bw.write(line);
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void formatJsonInheritance(File json)
	{
		try
		{
			List<String> lines = Files.readAllLines(json.toPath());

			List<String> newLines = new ArrayList<String>();

			for (String line : lines)
			{
				if (line.startsWith("    \"inheritsFrom\": \""))
				{
					newLines.add("    \"inheritsFrom\": \"" + INHERITANCE + "\"" + (line.contains(",") ? "," : ""));
				}
				else
				{
					newLines.add(line);
				}
			}
			FileWriter fw = new FileWriter(json);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String line : newLines)
			{
				bw.write(line);
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void formatJsonDownloads(File json)
	{
		formatJsonDownloads(json, json);
	}
	
	public static void formatJsonDownloads(File inJson, File outJson)
	{
		try
		{
			List<String> lines = Files.readAllLines(inJson.toPath());

			List<String> newLines = new ArrayList<String>();

			int downloadTagCount = 0;

			// Does initial count
			for (String line : lines)
			{
				if (line.startsWith("    \"downloads\": "))
				{
					downloadTagCount++;
				}
			}

			int currentTagCount = 0;
			boolean doRemoveSequence = false;
			int indentCount = 0;

			for (String line : lines)
			{
				if (!doRemoveSequence) // Find where to do the remove sequence
				{
					if (line.startsWith("    \"downloads\": "))
					{
						currentTagCount++;

						// If it's at the designated lines then set it to start removing them
						if (currentTagCount == downloadTagCount)
						{
							doRemoveSequence = true;
						}
						else
						{
							newLines.add(line);
						}
					}
					else
					{
						newLines.add(line);
					}
				}
				
				if (doRemoveSequence) // Do the remove sequence
				{
					if (line.contains("{"))
					{
						indentCount++;
					}
					else if (line.contains("}"))
					{
						indentCount--;
					}
					
					if (indentCount == 0)
					{
						doRemoveSequence = false;
					}
				}
			}

			FileWriter fw = new FileWriter(outJson);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String line : newLines)
			{
				bw.write(line);
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
