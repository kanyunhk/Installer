package net.qwertysam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import net.qwertysam.resource.TransferringFile;

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

	public static void moveFromThisJarToThatJar(String pathInJar, String jarOutputPath)
	{
		List<String> list = new ArrayList<String>();
		list.add(pathInJar);
		moveFromThisJarToThatJar(list, jarOutputPath);
	}

	public static void moveFromThisJarToThatJar(List<String> pathsInJar, String jarOutputPath)
	{
		try
		{
			File zipFile = new File(jarOutputPath);

			// get a temp file
			File tempFile = File.createTempFile(zipFile.getName(), null);
			// delete it, otherwise you cannot rename your existing zip to it.
			tempFile.delete();

			boolean renameOk = zipFile.renameTo(tempFile);
			if (!renameOk)
			{
				throw new RuntimeException(
						"could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
			}
			byte[] buf = new byte[1024];

			JarInputStream zin = new JarInputStream(new FileInputStream(tempFile));
			JarOutputStream out = new JarOutputStream(new FileOutputStream(zipFile));

			List<TransferringFile> inputFiles = new ArrayList<TransferringFile>();
			for (String path : pathsInJar)
			{
				inputFiles.add(new TransferringFile(Class.class.getResourceAsStream(path), path.substring(1),
						path.split("/")[path.split("/").length - 1]));
			}
			
			ZipEntry entry;
			while ((entry = zin.getNextEntry()) != null)
			{
				String name = entry.getName();
				boolean duplicateFile = false;

				for (TransferringFile file : inputFiles)
				{
					if (file.fileName.equals(name))
					{
						duplicateFile = true;
					}
				}
				if (!duplicateFile)
				{
					// Add ZIP entry to output stream.
					out.putNextEntry(new ZipEntry(name));
					// Transfer bytes from the ZIP file to the output file
					int len;
					while ((len = zin.read(buf)) > 0)
					{
						out.write(buf, 0, len);
					}
				}
			}
			// Close the streams
			zin.close();
			// Compress the files
			for (TransferringFile file : inputFiles)
			{
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(file.pathInJar));
				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = file.stream.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				// Complete the entry
				out.closeEntry();
				file.stream.close();
			}
			// Complete the ZIP file
			out.close();
			tempFile.delete();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
