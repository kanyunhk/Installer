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
	public static void formatJson(File json)
	{
		try
		{
			List<String> lines = Files.readAllLines(json.toPath());
			
			List<String> newLines = new ArrayList<String>();
			
			for (String line : lines)
			{
				if (line.contains("  \"id\": \""))
				{
					newLines.add("  \"id\": \"" + VersionsUtil.getSelectedVersion() + "\",");
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
}
