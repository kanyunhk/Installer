package net.qwertysam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import net.qwertysam.resource.TransferringFile;

public class JarUtil
{
	private static final List<String> FILES_TO_DELETE = new ArrayUtil<String>()
			.toList(new String[] { "META-INF", "META-INF/MOJANGCS.SF", "META-INF/MOJANGCS.RSA", "MANIFEST.MF" });

	public static void moveFromThisJarToThatJar(String pathInJar, String jarOutputPath)
	{
		List<String> list = new ArrayList<String>();
		list.add(pathInJar);
		moveFromThisJarToThatJar(list, jarOutputPath);
	}

	public static void moveFromThisJarToPath(String pathInJar, String outputPath)
	{
		TransferringFile in = new TransferringFile(Class.class.getResourceAsStream(pathInJar), pathInJar.substring(1),
				pathInJar.split("/")[pathInJar.split("/").length - 1]);

		try
		{
			FileOutputStream fos = new FileOutputStream(outputPath + DirUtil.SEP + in.fileName);

			byte[] buf = new byte[2048];
			int read = in.stream.read(buf);
			while (read != -1)
			{
				fos.write(buf, 0, read);
				read = in.stream.read(buf);
			}
			if (fos != null)
			{
				fos.close();
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
					if (name.equals(file.fileName))
					{
						System.out.println("[IGNORE] Duplicate File: " + name);
						duplicateFile = true;
					}
				}

				for (String file : ModFiles.MOD_FILES)
				{
					if (name.equals(file.substring(1)))
					{
						System.out.println("[IGNORE] Duplicate Mod File: " + name);
						duplicateFile = true;
					}
				}

				for (String fileToDelete : FILES_TO_DELETE)
				{
					if (name.equals(fileToDelete))
					{
						System.out.println("[IGNORE] Deleting File: " + name);
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
