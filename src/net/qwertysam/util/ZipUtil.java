package net.qwertysam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ZipUtil
{
	public static void unzipFile(String inFile, String outDir, JLabel labelToUpdate, JProgressBar progressBar)
	{
		unzipFile(false, inFile, outDir, labelToUpdate, progressBar);
	}

	public static void unzipFile(boolean newThread, String inFile, String outDir, JLabel labelToUpdate,
			JProgressBar progressBar)
	{
		if (newThread)
		{
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					unzipProcedure(inFile, outDir, labelToUpdate, progressBar);
				}
			};

			Thread t = new Thread(run);
			t.start();
		}
		else
		{
			unzipProcedure(inFile, outDir, labelToUpdate, progressBar);
		}
	}

	private static void unzipProcedure(String inFile, String outDir, JLabel labelToUpdate, JProgressBar progressBar)
	{
		try
		{
			byte[] buffer = new byte[1024];

			File dir = new File(outDir);
			if (!dir.exists()) dir.mkdir();

			ZipInputStream zis = new ZipInputStream(new FileInputStream(inFile));

			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null)
			{
				if (!entry.isDirectory())
				{
					File newFile = new File(outDir + File.separator + entry.getName());

					labelToUpdate.setText("Extracting: " + entry.getName());

					new File(newFile.getParent()).mkdirs();

					try
					{
						FileOutputStream fos = new FileOutputStream(newFile);
						int i = 0;
						while ((i = zis.read(buffer)) > 0)
						{
							fos.write(buffer, 0, i);
						}
						fos.close();
					}
					catch (FileNotFoundException e)
					{
						System.out.println("[WARNING] Couldn't extract file: " + newFile.getName());
						e.printStackTrace();
					}
				}
				else
				{
					new File(outDir + File.separator + entry.getName()).mkdir();
					System.out.println(
							"[IGNORE] Created dir: " + DirUtil.formatPath(outDir + File.separator + entry.getName()));
				}
			}

			zis.closeEntry();
			zis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		labelToUpdate.setText("Unzipping: Finished");
	}

	public static void zipFile(List<File> filesToArchive, String zipOutput, JLabel labelToUpdate,
			JProgressBar progressBar)
	{
		zipFile(false, filesToArchive, zipOutput, labelToUpdate, progressBar);
	}

	public static void zipFile(boolean newThread, List<File> filesToArchive, String zipOutput, JLabel labelToUpdate,
			JProgressBar progressBar)
	{
		if (newThread)
		{
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					zipProcedure(filesToArchive, zipOutput, labelToUpdate, progressBar);
				}
			};

			Thread t = new Thread(run);
			t.start();
		}
		else
		{
			zipProcedure(filesToArchive, zipOutput, labelToUpdate, progressBar);
		}
	}

	public static void zipProcedure(List<File> filesToArchive, String zipOutput, JLabel labelToUpdate,
			JProgressBar progressBar)
	{
		File zipfile = new File(zipOutput);
		// Create a buffer for reading the files
		byte[] buffer = new byte[1024];
		try
		{
			// create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			// compress the files
			for (File file : filesToArchive)
			{
				if (!file.isDirectory())
				{
					try
					{
						labelToUpdate.setText("Compressing: " + file.getName());
	
						FileInputStream in = new FileInputStream(file.getCanonicalFile());
						// add ZIP entry to output stream with the offset of the path to maintain the folder structure of the minecraft.jar
						out.putNextEntry(new ZipEntry(file.getPath().replace(DirUtil.getTempDirPath(), "").substring(1)));
						// transfer bytes from the file to the ZIP file
						int len;
						while ((len = in.read(buffer)) > 0)
						{
							out.write(buffer, 0, len);
						}
						// complete the entry
						out.closeEntry();
						in.close();
					}
					catch (ZipException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					
				}
			}
			// complete the ZIP file
			out.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static String getRelativePath(String sourceDir, File file)
	{
		// Trim off the start of source dir path...
		String path = file.getPath().substring(sourceDir.length());
		if (path.startsWith(File.pathSeparator))
		{
			path = path.substring(1);
		}
		return path;
	}
}
