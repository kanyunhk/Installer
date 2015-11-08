package net.qwertysam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JLabel;

import net.qwertysam.resource.PercentageCalc;

public class ZipUtil
{
	public static void unzipFile(String inFile, String outDir, JLabel labelToUpdate, PercentageCalc percentageCalc)
	{
		unzipFile(false, inFile, outDir, labelToUpdate, percentageCalc);
	}

	public static void unzipFile(boolean newThread, String inFile, String outDir, JLabel labelToUpdate,
			PercentageCalc percentageCalc)
	{
		if (newThread)
		{
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					unzipProcedure(inFile, outDir, labelToUpdate, percentageCalc);
				}
			};

			Thread t = new Thread(run);
			t.start();
		}
		else
		{
			unzipProcedure(inFile, outDir, labelToUpdate, percentageCalc);
		}
	}

	private static void unzipProcedure(String inFile, String outDir, JLabel labelToUpdate,
			PercentageCalc percentageCalc)
	{
		try
		{
			byte[] buffer = new byte[1024];

			File dir = new File(outDir);
			if (!dir.exists()) dir.mkdir();

			ZipFile zip = new ZipFile(inFile);
			percentageCalc.setMaxPercent(zip.size());
			System.out.println(zip.size());
			zip.close();

			ZipInputStream zis = new ZipInputStream(new FileInputStream(inFile));

			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null)
			{
				if (!entry.isDirectory())
				{
					File newFile = new File(outDir + File.separator + entry.getName());

					labelToUpdate.setText("Extracting: " + entry.getName());
					percentageCalc.addPercent();

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
			PercentageCalc percentageCalc)
	{
		zipFile(false, filesToArchive, zipOutput, labelToUpdate, percentageCalc);
	}

	public static void zipFile(boolean newThread, List<File> filesToArchive, String zipOutput, JLabel labelToUpdate,
			PercentageCalc percentageCalc)
	{
		if (newThread)
		{
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					zipProcedure(filesToArchive, zipOutput, labelToUpdate, percentageCalc);
				}
			};

			Thread t = new Thread(run);
			t.start();
		}
		else
		{
			zipProcedure(filesToArchive, zipOutput, labelToUpdate, percentageCalc);
		}
	}

	public static void zipProcedure(List<File> filesToArchive, String zipOutput, JLabel labelToUpdate,
			PercentageCalc percentageCalc)
	{
		File zipfile = new File(zipOutput);
		// Create a buffer for reading the files
		byte[] buffer = new byte[1024];
		try
		{
			percentageCalc.setMaxPercent(filesToArchive.size());

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
						percentageCalc.addPercent();

						FileInputStream in = new FileInputStream(file.getCanonicalFile());
						// add ZIP entry to output stream with the offset of the path to maintain the folder structure of the minecraft.jar
						out.putNextEntry(
								new ZipEntry(file.getPath().replace(DirUtil.getTempDirPath(), "").substring(1)));
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
			}
			// complete the ZIP file
			out.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void addFilesToExistingZip(String zipFilePath, List<File> files)
	{
		try
		{
			File zipFile = new File(zipFilePath);

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

			ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

			ZipEntry entry = zin.getNextEntry();
			while (entry != null)
			{
				String name = entry.getName();
				boolean notInFiles = true;
				for (File f : files)
				{
					if (f.getName().equals(name))
					{
						notInFiles = false;
						break;
					}
				}
				if (notInFiles)
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
				entry = zin.getNextEntry();
			}
			// Close the streams
			zin.close();
			// Compress the files
			for (File file : files)
			{
				InputStream in = new FileInputStream(file);
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(file.getPath().replace(DirUtil.getTempDirPath(), "").substring(1)));
				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				// Complete the entry
				out.closeEntry();
				in.close();
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
