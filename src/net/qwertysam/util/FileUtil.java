package net.qwertysam.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	/**
	 * for path in jar, MUST USE / AS SEPARATOR
	 * 
	 * @param pathInJar eg. "/data/text.txt"
	 * @param outPath eg. The temp directory
	 */
	public static void getFileFromInsideJar(String pathInJar, String outPath)
	{
		try
		{
			InputStream inputStream = Class.class.getResourceAsStream(pathInJar);
			String outFileName = pathInJar.split("/")[pathInJar.split("/").length - 1]; // Gets the last thing
			FileOutputStream outputStream = new FileOutputStream(new File(outPath + DirUtil.SEP + outFileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1)
			{
				outputStream.write(bytes, 0, read);
			}

			System.out.println("Done!");

			inputStream.close();
			// outputStream.flush();
			outputStream.close();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
