package net.qwertysam.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil
{
	public static void renameFile(String originalPath, String newPath)
	{
		try
		{
			Files.move(new File(originalPath).toPath(), new File(newPath).toPath(),
					java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException ex)
		{
			System.out.println("[CRITICAL] Failed to move original jar to safe location.");
			ex.printStackTrace();
		}
	}

	public static List<File> getFiles(String dir)
	{
		// List<File> list = new ArrayUtil<File>().toList(new File(dir).listFiles());

		File directory = new File(dir);

		List<File> resultList = new ArrayList<File>();

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList)
		{
			if (file.isFile())
			{
				resultList.add(file);
			}
			else if (file.isDirectory())
			{
				resultList.addAll(getFiles(file.getAbsolutePath()));
			}
		}
		return resultList;
	}
}
